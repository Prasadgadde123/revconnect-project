package com.revconnect.util;

import java.util.regex.Pattern;

public class ValidationUtil {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_]{3,50}$";

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return Pattern.compile(EMAIL_REGEX).matcher(email).matches();
    }

    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return Pattern.compile(USERNAME_REGEX).matcher(username).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static String extractHashtags(String content) {
        if (content == null) return "";

        StringBuilder hashtags = new StringBuilder();
        String[] words = content.split("\\s+");

        for (String word : words) {
            if (word.startsWith("#") && word.length() > 1) {
                String hashtag = word.substring(1);
                if (!hashtag.isEmpty()) {
                    if (hashtags.length() > 0) {
                        hashtags.append(",");
                    }
                    hashtags.append(hashtag);
                }
            }
        }

        return hashtags.toString();
    }
}