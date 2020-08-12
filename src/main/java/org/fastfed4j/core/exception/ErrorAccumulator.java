package org.fastfed4j.core.exception;

import java.util.ArrayList;
import java.util.List;

/**
 Utility to collect a list of errors. This allows the parsing & validation
 functions to report all errors to the end-user, rather than
 stopping at the first error.
 */
public class ErrorAccumulator {

    private List<String> errors = new ArrayList<>();

    /**
     * Add an error.
     * @param error new error to add
     */
    public void add(String error) {
            errors.add(error);
    }

    /**
     * Get a list of all the accumulated errors.
     * @return all errors
     */
    public List<String> getErrors() {
            return errors;
    }

    /**
     * Test if errors have been added to the accumulator.
     * @return true if errors have been added
     */
    public boolean hasErrors() {
            return errors.size() > 0;
    }

    /**
     * Clear all errors from the accumulator, resetting to an empty list.
     */
    public void clear() {
        errors = new ArrayList<>();
    }

    /**
     * Concatenate the accumulated errors into a newline-delimited string.
     * @return formatted description of all errors
     */
    @Override
    public String toString() {
        return String.join("\n", errors);
    }

}
