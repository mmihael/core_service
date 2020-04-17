package dev.mm.core.coreservice.util;

public class StringUtil {

    public boolean isNotBlank(String string) {
        return string != null && !string.trim().equals("");
    }
}
