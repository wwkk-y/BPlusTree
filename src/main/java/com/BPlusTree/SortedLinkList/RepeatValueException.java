package com.BPlusTree.SortedLinkList;

/**
 * 重复值 异常
 */
public class RepeatValueException extends RuntimeException{
    public RepeatValueException() {
        super();
    }

    public RepeatValueException(String message) {
        super(message);
    }

    public RepeatValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepeatValueException(Throwable cause) {
        super(cause);
    }
}