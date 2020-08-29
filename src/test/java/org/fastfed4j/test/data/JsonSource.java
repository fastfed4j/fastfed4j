package org.fastfed4j.test.data;

import java.util.Date;
import java.util.List;

public abstract class JsonSource {


    /**
     * Get a timestamp that is 1 hour in the future
     */
    public static long oneHourFromNow() {
        long currentTime = (new Date()).getTime();
        return (new Date(currentTime + 3600000 )).getTime();
    }

    /**
     * Remove the outer brackets from a JSON string
     */
    public static String removeOuterBrackets(String s) {
        int indexOfOpenBracket = s.indexOf("{");
        int indexOfLastBracket = s.lastIndexOf("}");
        return s.substring(indexOfOpenBracket+1, indexOfLastBracket);
    }
}
