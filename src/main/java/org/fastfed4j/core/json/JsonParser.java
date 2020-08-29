package org.fastfed4j.core.json;

import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.exception.InvalidMetadataException;

/**
 * Utility to parse a JSON document
 */
public class JsonParser {

    /**
     * Parse a JSON string into a JSON object
     * @param jsonString JSON string
     * @param errorAccumulator Error accumulator
     * @return JsonObject
     * @throws InvalidMetadataException if JSON is malformed
     */
    public static JsonObject parse(String jsonString, ErrorAccumulator errorAccumulator)
        throws InvalidMetadataException
    {
        if (null == jsonString || jsonString.isEmpty()) {
            errorAccumulator.add("JSON is empty");
            throw new InvalidMetadataException(errorAccumulator);
        }

        Object obj;
        try {
            obj = (new org.json.simple.parser.JSONParser()).parse(jsonString);
        } catch (Exception e) {
            errorAccumulator.add("Malformed JSON: " + e + "\n" + jsonString);
            throw new InvalidMetadataException(errorAccumulator);
        }

        if (!(obj instanceof org.json.simple.JSONObject)) {
            errorAccumulator.add("Malformed JSON. Expected an Object, received a " + JsonObject.getDisplayableObjectType(obj));
            throw new InvalidMetadataException(errorAccumulator);
        }

        return new JsonObject(errorAccumulator, (org.json.simple.JSONObject)obj);
    }

}
