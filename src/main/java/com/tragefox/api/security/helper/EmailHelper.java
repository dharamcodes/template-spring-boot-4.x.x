package com.tragefox.api.security.helper;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EmailHelper {

    public boolean sendPasswordResetEmail(String recipient, String token) {
        return true;
    }

    public void sendEmail(String recipient, String subject, String body) {}
}
