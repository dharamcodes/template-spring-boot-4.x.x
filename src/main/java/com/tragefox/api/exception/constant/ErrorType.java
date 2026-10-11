package com.tragefox.api.exception.constant;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorType {
    INVALID_PARAMETER(400, "E0001", "error.message.INVALID_PARAMETER"),
    NOT_ENOUGH_DATA(422, "E0002", "error.message.NOT_ENOUGH_DATA"),
    MAPPER_NOT_FOUND(500, "E0003", "error.message.MAPPER_NOT_FOUND"),
    DATA_NOT_FOUND(404, "E0004", "error.message.DATA_NOT_FOUND"),
    ACCESS_DENIED(403, "E0005", "error.message.ACCESS_DENIED"),
    DATA_EXISTS(409, "E0006", "error.message.DATA_EXISTS"),
    INTERNAL_ERROR(500, "E0007", "error.message.INTERNAL_ERROR"),
    SERVICE_UNAVAILABLE(503, "E0008", "error.message.SERVICE_UNAVAILABLE"),
    INVALID_CREDENTIALS(401, "E0009", "error.message.INVALID_CREDENTIALS"),
    SESSION_EXPIRED(401, "E0010", "error.message.SESSION_EXPIRED"),
    PASSWORD_MISMATCH(400, "E0011", "error.message.PASSWORD_MISMATCH"),
    USER_NOT_FOUND(404, "E0012", "error.message.USER_NOT_FOUND"),
    USER_NOT_ACTIVE(401, "E0012", "error.message.USER_NOT_ACTIVE"),
    INVALID_TOKEN(401, "E0013", "error.message.INVALID_TOKEN"),
    TOO_MANY_REQUESTS(429, "E0014", "error.message.TOO_MANY_REQUESTS"),
    MAX_RETRY_ATTEMPTS_EXCEEDED(429, "E0015", "error.message.MAX_RETRY_ATTEMPTS_EXCEEDED"),
    INVALID_JSON_FORMAT(400, "E0016", "error.message.INVALID_JSON_FORMAT"),
    INVALID_DATE_FORMAT(400, "E0017", "error.message.INVALID_DATE_FORMAT"),
    INVALID_EMAIL_FORMAT(400, "E0018", "error.message.INVALID_EMAIL_FORMAT"),
    INVALID_PHONE_NUMBER_FORMAT(400, "E0019", "error.message.INVALID_PHONE_NUMBER_FORMAT"),
    INVALID_URL_FORMAT(400, "E0020", "error.message.INVALID_URL_FORMAT"),
    INVALID_CREDENTIALS_FORMAT(400, "E0021", "error.message.INVALID_CREDENTIALS_FORMAT"),
    INVALID_FILE_FORMAT(400, "E0022", "error.message.INVALID_FILE_FORMAT"),
    FILE_TOO_LARGE(413, "E0023", "error.message.FILE_TOO_LARGE"),
    FILE_NOT_FOUND(404, "E0024", "error.message.FILE_NOT_FOUND"),
    INVALID_FILE_TYPE(400, "E0025", "error.message.INVALID_FILE_TYPE"),
    INVALID_FIELD_VALUE(400, "E0026", "error.message.INVALID_FIELD_VALUE"),
    INVALID_FIELD_NAME(400, "E0027", "error.message.INVALID_FIELD_NAME"),
    INVALID_FIELD_TYPE(400, "E0028", "error.message.INVALID_FIELD_TYPE"),
    INVALID_FIELD_LENGTH(400, "E0029", "error.message.INVALID_FIELD_LENGTH"),
    INVALID_FIELD_PRECISION(400, "E0030", "error.message.INVALID_FIELD_PRECISION"),
    INVALID_FIELD_SCALE(400, "E0031", "error.message.INVALID_FIELD_SCALE"),
    USER_CAN_NOT_CREATED(400, "E0032", "error.message.USER_CAN_NOT_CREATED");

    private final Integer code;

    @NonNull
    private final String errorCode;

    @NonNull
    private final String message;
}
