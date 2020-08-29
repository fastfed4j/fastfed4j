package org.fastfed4j.core.exception;

import java.util.Objects;

/**
 * Indicates that metadata is syntactically malformed or non-compliant.
 */
public class InvalidMetadataException extends IllegalArgumentException {
    private ErrorAccumulator errorAccumulator;
    private String json;

    public InvalidMetadataException(ErrorAccumulator errorAccumulator) {
        this(errorAccumulator, null);
    }

    public InvalidMetadataException(ErrorAccumulator errorAccumulator, String json) {
        super("Metadata is malformed or non-compliant.\n" + errorAccumulator.toString() + (json == null ? "" : "\n" + json));
        this.errorAccumulator = Objects.requireNonNull(errorAccumulator, "ErrorAccumulator must not be null");
    }

    public ErrorAccumulator getErrorAccumulator() {
        return errorAccumulator;
    }

}
