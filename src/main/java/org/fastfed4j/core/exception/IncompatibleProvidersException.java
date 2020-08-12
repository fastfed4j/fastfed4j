package org.fastfed4j.core.exception;

/**
 * Indicates that two providers are not compatible based upon the capabilities that each proclaims to support
 * in their published metadata, and hence it is not possible to establish a federation
 * relationship between the providers.
 */
public class IncompatibleProvidersException extends RuntimeException {

    public IncompatibleProvidersException (String s) {
        super(s);
    }
}
