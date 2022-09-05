package org.apache.coyote.http11.model;

public class Header {

    private static final String KEY_VALUE_SEPARATOR = ": ";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final String key;
    private final String value;

    public Header(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    public static Header of(final String line) {
        String[] keyValue = line.split(KEY_VALUE_SEPARATOR);
        return new Header(keyValue[KEY_INDEX], keyValue[VALUE_INDEX]);
    }

    public String getString() {
        return key + KEY_VALUE_SEPARATOR + value + " ";
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
