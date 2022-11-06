package com.imjustdoom.justdoomapi.util;

import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^(.+)@(\\S+)$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("[^a-zA-Z0-9_]");

    public static boolean isEmailValid(String email) {
        return EMAIL_PATTERN.matcher(email).find();
    }

    public static boolean isUsernameValid(String username) {
        return !USERNAME_PATTERN.matcher(username).find();
    }
}
