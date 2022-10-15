package com.imjustdoom.justdoomapi.util;

public class APIUtil {

    public static String createErrorResponse(String error) {
        return "{\"error\": \"" + error + "\"}";
    }

    public static String createSuccessResponse(String success) {
        return "{\"success\": \"" + success + "\"}";
    }
}