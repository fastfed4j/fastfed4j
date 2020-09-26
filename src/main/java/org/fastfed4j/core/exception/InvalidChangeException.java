package org.fastfed4j.core.exception;

/**
 * Indicates that a FastFed Handshake would result in an invalid change to the contract between the
 * Identity Provider and Application Provider.
 */
public class InvalidChangeException extends IllegalArgumentException {

    public InvalidChangeException(String s) {
        super(s);
    }
}

