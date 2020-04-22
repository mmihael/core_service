package dev.mm.core.coreservice.util;

import java.util.HashMap;
import java.util.Map;

public class MapUtil {

    public static Map<String, Object> mapFromKeyVal(Object... keyValues) {
        if (keyValues.length % 2 == 1) {
            throw new IllegalArgumentException("Required even number of keyValues");
        }
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            if (!(keyValues[i] instanceof String)) {
                throw new IllegalArgumentException("Odd elements of keyValues must be a String");
            }
            map.put((String) keyValues[i], keyValues[i + 1]);
        }
        return map;
    }
}
