package org.prototypes.memorystore;

public class ValueType {
    private final Class<?> type;
    private final Object data;

    public ValueType(Class<?> type, Object data) {
        this.type = type;
        this.data = data;
    }

    public Class<?> getType() {
        return type;
    }

    public <T> T getData() {
        return (T) data;
    }
}
