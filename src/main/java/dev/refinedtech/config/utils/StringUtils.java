package dev.refinedtech.config.utils;

public final class StringUtils {

    private StringUtils() {

    }

    public static String startingLowercase(String s) {
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

}
