package org.fastfed4j.core.util;

import java.util.Collection;
import java.util.Iterator;


public class FormattingUtils {

    /**
     * Join a collection of strings into a single value with a delimiter, and with each
     * element surrounded by quotes. Primarily used in generating error messages.
     */
    public String joinAndQuote(Collection<String> values) {
        StringBuilder builder = new StringBuilder();
        Iterator<String> iterator  = values.iterator();
        while (iterator.hasNext()) {
            builder.append("\"").append(iterator.next()).append("\"");
            if (iterator.hasNext())
                builder.append(", ");
        }
        return builder.toString();
    }
}
