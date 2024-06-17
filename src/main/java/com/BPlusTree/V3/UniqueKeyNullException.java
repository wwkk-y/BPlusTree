package com.BPlusTree.V3;

/**
 * 唯一索引为 null 的异常
 */
public class UniqueKeyNullException extends RuntimeException{

    public UniqueKeyNullException() {
        super();
    }

    public UniqueKeyNullException(String message) {
        super(message);
    }

    public UniqueKeyNullException(String message, Throwable cause) {
        super(message, cause);
    }

    public UniqueKeyNullException(Throwable cause) {
        super(cause);
    }
}
