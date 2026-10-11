#!/usr/bin/env python3
"""
Extracts and applies library version fixes from Qodana SARIF reports to build.gradle,
and ensures Spotless formatting compliance.
"""

import argparse
import json
import os
import re
import subprocess
import sys


def parse_arguments():
    parser = argparse.ArgumentParser(
        description="Extract and apply library version fixes from Qodana SARIF to build.gradle"
    )
    parser.add_argument(
        "--sarif",
        dest="sarif_file",
        help="Path to the Qodana SARIF file",
        default=None,
    )
    parser.add_argument(
        "--build-file",
        dest="build_file",
        help="Path to the Gradle build file (default: build.gradle)",
        default="build.gradle",
    )
    parser.add_argument(
        "--format",
        dest="run_format",
        action="store_true",
        default=True,
        help="Run spotlessApply if changes were made (default: True)",
    )
    return parser.parse_args()


def find_sarif_file(explicit_path=None):
    if explicit_path and os.path.isfile(explicit_path):
        return explicit_path

    runner_temp = os.environ.get("RUNNER_TEMP", "/tmp")
    candidates = [
        os.path.join(runner_temp, "qodana/results/qodana.sarif.json"),
        "./qodana/results/qodana.sarif.json",
        "./results/qodana.sarif.json",
    ]

    for path in candidates:
        if os.path.isfile(path):
            return path
    return None


def format_build_file():
    """Runs ./gradlew spotlessApply if gradlew wrapper exists."""
    gradlew_path = "./gradlew"
    if os.path.isfile(gradlew_path):
        print("Ensuring gradlew is executable...")
        os.chmod(gradlew_path, 0o755)
        print("Applying Spotless formatting to build.gradle...")
        try:
            subprocess.run([gradlew_path, "spotlessApply"], check=True)
            print("Spotless formatting applied successfully.")
        except subprocess.CalledProcessError as e:
            print(f"Warning: spotlessApply failed with code {e.returncode}")
        except Exception as e:
            print(f"Warning: Failed to execute spotlessApply: {e}")


def apply_fixes(sarif_file, build_file, run_format=True):
    if not os.path.isfile(build_file):
        print(f"Target build file '{build_file}' not found.")
        return False

    with open(build_file, "r", encoding="utf-8") as f:
        gradle_code = f.read()

    modified_code = gradle_code
    changes_detected = False

    if not sarif_file:
        print("No Qodana SARIF file found. Checking workspace for direct fixes.")
        return False

    print(f"Analyzing Qodana SARIF findings from: {sarif_file}")
    try:
        with open(sarif_file, "r", encoding="utf-8") as f:
            data = json.load(f)
    except Exception as err:
        print(f"Warning: Failed to parse SARIF: {err}")
        return False

    # 1. Apply any artifact replacement fixes specified in SARIF
    for run in data.get("runs", []):
        for result in run.get("results", []):
            for fix in result.get("fixes", []):
                for change in fix.get("artifactChanges", []):
                    uri = change.get("artifactLocation", {}).get("uri", "")
                    if build_file in uri:
                        for repl in change.get("replacements", []):
                            text = repl.get("insertedContent", {}).get("text")
                            region = repl.get("deletedRegion", {})
                            offset = region.get("byteOffset") or region.get("charOffset")
                            length = region.get("byteLength") or region.get("charLength")
                            if offset is not None and length is not None and text is not None:
                                modified_code = (
                                    modified_code[:offset]
                                    + text
                                    + modified_code[offset + length:]
                                )
                                changes_detected = True

    # 2. Extract recommended package updates / vulnerability fixes from inspection messages
    for run in data.get("runs", []):
        for result in run.get("results", []):
            msg = result.get("message", {}).get("text", "")
            match = re.search(
                r'([a-zA-Z0-9_.-]+:[a-zA-Z0-9_.-]+)(?:[:\s]+([0-9]+\.[0-9]+[a-zA-Z0-9_.-]*))?.*?(?:upgrade|update|recommended|newer).*?([0-9]+\.[0-9]+[a-zA-Z0-9_.-]*)',
                msg,
                re.IGNORECASE,
            )
            if match:
                pkg = match.group(1)
                old_v = match.group(2)
                new_v = match.group(3)
                if old_v and f"'{pkg}:{old_v}'" in modified_code:
                    modified_code = modified_code.replace(
                        f"'{pkg}:{old_v}'", f"'{pkg}:{new_v}'"
                    )
                    changes_detected = True
                    print(f"Updated {pkg} from {old_v} to {new_v}")
                elif old_v and f'"{pkg}:{old_v}"' in modified_code:
                    modified_code = modified_code.replace(
                        f'"{pkg}:{old_v}"', f'"{pkg}:{new_v}'
                    )
                    changes_detected = True
                    print(f"Updated {pkg} from {old_v} to {new_v}")

    if changes_detected:
        with open(build_file, "w", encoding="utf-8") as f:
            f.write(modified_code)
        print(f"Successfully updated {build_file} with suggested version fixes.")

        if run_format:
            format_build_file()

        return True
    else:
        print("No additional version changes extracted from SARIF.")
        return False


def main():
    args = parse_arguments()
    sarif_file = find_sarif_file(args.sarif_file)
    apply_fixes(sarif_file, args.build_file, args.run_format)


if __name__ == "__main__":
    main()
