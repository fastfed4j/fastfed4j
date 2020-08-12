package org.fastfed4j.core.exception;

import java.util.Objects;

/**
 * Indicates that metadata is syntactically malformed or non-compliant.
 */
public class InvalidMetadataException extends IllegalArgumentException {
    private ErrorAccumulator errorAccumulator;

    public InvalidMetadataException(ErrorAccumulator errorAccumulator) {
        super("Metadata is malformed or non-compliant:\n" + errorAccumulator.toString());
        this.errorAccumulator = Objects.requireNonNull(errorAccumulator, "ErrorAccumulator must not be null");
    }

    public ErrorAccumulator getErrorAccumulator() {
        return errorAccumulator;
    }

}
