package org.prototypes.memorystore;

/**
 * @param type
 * @param data
 * Complex type for storing value and its type
 */
public record ValueType(Class<?> type, Object data) {

    // In efficient compared to c/c++ as this allocates Object header memory + actual d type of value
    // The JVM aligns every object to 8-byte boundaries. Padding fills the gap when the object's actual content doesn't land on a multiple of 8.
    // 12 bytes header + 4 bytes -> 16 bytes vs in C it's only 4 bytes
    // 12 bytes header + 8 bytes long + 4 padding -> 20  -> 24 bytes
    // 12 bytes header + 1 bytes boolean + 3 bytes padding -> 16 bytes



    //    public <T> T getData() {
    //        return (T) data;
    //    }
}
