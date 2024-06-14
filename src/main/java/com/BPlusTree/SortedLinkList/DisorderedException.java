package com.BPlusTree.SortedLinkList;

/**
 * 乱序异常
 */
public class DisorderedException extends Exception{
    public DisorderedException() {
        super();
    }

    public DisorderedException(String message) {
        super(message);
    }

    public DisorderedException(String message, Throwable cause) {
        super(message, cause);
    }

    public DisorderedException(Throwable cause) {
        super(cause);
    }
}
