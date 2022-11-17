package com.imjustdoom.justdoomapi.util;

public class APIUtil {

    public static String createErrorResponse(String error) {
        return "{\"error\": \"" + error + "\"}";
    }

    public static String createSuccessResponse(String success) {
        return "{\"success\": \"" + success + "\"}";
    }

    public static String downloadLink(int id) {
        return "http://localhost:8080/file/" + id + "/download";
    }
}