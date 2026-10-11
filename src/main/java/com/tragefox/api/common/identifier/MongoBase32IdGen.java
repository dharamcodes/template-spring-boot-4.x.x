package com.tragefox.api.common.identifier;

import com.tragefox.api.common.identifier.service.Base32IdGenService;
import com.tragefox.api.exception.ServerException;
import com.tragefox.api.exception.constant.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MongoBase32IdGen {

    private static final String PREFIX = "A4U";
    private static final int BASE36_WIDTH = 6;

    private final Base32IdGenService base32IdGenService;

    public String generateId() {
        long seq = base32IdGenService.getNextSequence("idGenerator");

        String base36 = Long.toString(seq, 36).toUpperCase();

        if (base36.length() < BASE36_WIDTH) {
            base36 = "0".repeat(BASE36_WIDTH - base36.length()) + base36;
        } else if (base36.length() > BASE36_WIDTH) {
            throw new ServerException(ErrorType.INTERNAL_ERROR);
        }
        return PREFIX + base36;
    }
}
