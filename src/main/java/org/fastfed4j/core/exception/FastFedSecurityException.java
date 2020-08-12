package org.fastfed4j.core.exception;

/**
 * Indicates an activity violates a security requirements of the FastFed specification
 */
public class FastFedSecurityException extends IllegalArgumentException {

    public FastFedSecurityException(String s) {
        super(s);
    }
}
