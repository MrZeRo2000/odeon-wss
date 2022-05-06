package com.romanpulov.odeonwss.utils;

public class EnumUtils {
    /**
     * <a href="https://stackoverflow.com/questions/604424/how-to-get-an-enum-value-from-a-string-value-in-java">https://stackoverflow.com/questions/604424/how-to-get-an-enum-value-from-a-string-value-in-java</a>
     * A common method for all enums since they can't have another base class
     * @param <T> Enum type
     * @param c enum type. All enums must be all caps.
     * @param string case insensitive
     * @return corresponding enum, or null
     */
    public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String string) {
        if( c != null && string != null ) {
            try {
                return Enum.valueOf(c, string.trim().toUpperCase());
            } catch(IllegalArgumentException ignored) {
            }
        }
        return null;
    }
}
