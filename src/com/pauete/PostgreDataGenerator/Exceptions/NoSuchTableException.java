package com.pauete.PostgreDataGenerator.Exceptions;

/**
 * Signals an unexisting table has been called.
 */
public class NoSuchTableException extends Exception {
    public NoSuchTableException() {
        super();
    }
}
