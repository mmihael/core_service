package dev.mm.core.coreservice.util;

public class StringUtil {

    public static boolean isBlank(String string) {
        return string == null || string.trim().equals("");
    }

    public static boolean isNotBlank(String string) {
        return string != null && !string.trim().equals("");
    }
}
