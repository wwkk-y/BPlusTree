package com.BPlusTree.V1;

/**
 * 唯一索引异常
 */
public class UniqueKeyException extends RuntimeException{
    public UniqueKeyException() {
        super();
    }

    public UniqueKeyException(String message) {
        super(message);
    }

    public UniqueKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public UniqueKeyException(Throwable cause) {
        super(cause);
    }
}
