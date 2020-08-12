package org.fastfed4j.core.json;

import java.util.*;

import org.fastfed4j.core.constants.JSONMember;
import org.fastfed4j.core.exception.ErrorAccumulator;

/**
 * Abstracts an underlying JSON parser implementation behind a consistent interface. Also adds convenience utilities for
 * casting to particular data types and reporting errors when JSON contents do not conform to the expected types.
 */
public class JSONObject {
    public static final String PATH_DELIMITER = ".";

    private final org.json.simple.JSONObject wrappedObj;
    private final ErrorAccumulator errorAccumulator;
    private final String jsonPath;

    protected JSONObject(ErrorAccumulator errorAccumulator, org.json.simple.JSONObject jsonObject) {
        this(errorAccumulator, "", jsonObject);
    }

    protected JSONObject(ErrorAccumulator errorAccumulator, String jsonPath, org.json.simple.JSONObject jsonObject) {
        Objects.requireNonNull(errorAccumulator, "errorAccumulator must not be null");
        Objects.requireNonNull(jsonPath, "jsonPath must not be null");
        Objects.requireNonNull(jsonObject, "jsonObject must not be null");
        this.errorAccumulator = errorAccumulator;
        this.jsonPath = jsonPath;
        this.wrappedObj = jsonObject;
    }

    protected static String getDisplayableObjectType(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj instanceof String) {
            return "String";
        }
        if (obj instanceof Long) {
            return "Number";
        }
        if (obj instanceof Double) {
            return "Number";
        }
        if (obj instanceof Boolean) {
            return "Boolean";
        }
        if (obj instanceof org.json.simple.JSONArray) {
            return "Array";
        }
        if (obj instanceof org.json.simple.JSONObject) {
            return "Object";
        }
        return obj.getClass().getName();
    }

    private String createTypeMismatchErrorMsg(String keyName, String expectedType, String actualType) {
        StringBuilder builder = new StringBuilder();
        builder.append("Invalid type for \"");
        builder.append(getFullyQualifiedName(keyName));
        builder.append("\" ");
        builder.append("(expected: ");
        builder.append(expectedType);
        builder.append(", received: ");
        builder.append(actualType);
        builder.append(")");
        return builder.toString();
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public String getFullyQualifiedName(String jsonMemberName) {
        if (jsonPath.isEmpty()) {
            return jsonMemberName;
        }
        else {
            return String.join(PATH_DELIMITER, jsonPath, jsonMemberName);
        }
    }

    public ErrorAccumulator getErrorAccumulator() {
        return errorAccumulator;
    }

    public boolean containsKey(String key) {
        return wrappedObj.containsKey(key);
    }

    public boolean containsValueForMember(JSONMember key) {
        return containsValueForMember(key.toString());
    }

    public boolean containsValueForMember(String key) {
        return (wrappedObj.get(key) != null);
    }

    public Set<String> keySet() {
        return new HashSet<String>( wrappedObj.keySet());
    }

    public String getString(JSONMember member) {
        return getString(member.toString());
    }

    public String getString(String key) {
        Object result = wrappedObj.get(key);
        if (null == result) {
            return null;
        }
        if (! (result instanceof String)) {
            errorAccumulator.add( createTypeMismatchErrorMsg(key, "String", getDisplayableObjectType(result)));
            return null;
        }
        return ((String)result).trim();
    }

    public Boolean getBoolean(JSONMember member) {
        return getBoolean(member.toString());
    }

    public Boolean getBoolean(String key) {
        Object result = wrappedObj.get(key);
        if (null == result) {
            return null;
        }
        if (! (result instanceof Boolean)) {
            errorAccumulator.add( createTypeMismatchErrorMsg(key, "Boolean", getDisplayableObjectType(result)));
            return null;
        }
        return (Boolean)result;
    }

    public Long getLong(JSONMember member) {
        return getLong(member.toString());
    }

    public Long getLong(String key) {
        Object result = wrappedObj.get(key);
        if (null == result) {
            return null;
        }
        if (! (result instanceof Long)) {
            errorAccumulator.add( createTypeMismatchErrorMsg(key, "Long", getDisplayableObjectType(result)));
            return null;
        }
        return (Long)result;
    }

    public Date getDate(JSONMember member) {
        return getDate(member.toString());
    }

    public Date getDate(String key) {
        Long longValue = getLong(key);
        Date result = null;
        if (longValue != null) {
            result = new Date(longValue);
        }
        return result;
    }

    public List<String> getStringList(JSONMember member) {
        return getStringList(member.toString());
    }

    public List<String> getStringList(String key) {
        Object result = wrappedObj.get(key);

        if (null == result) {
            return null;
        }

        if (! (result instanceof org.json.simple.JSONArray)) {
            errorAccumulator.add( createTypeMismatchErrorMsg(key, "Array", getDisplayableObjectType(result)));
            return null;
        }

        ArrayList<String> response = new ArrayList<>();
        for (Object o : (org.json.simple.JSONArray)result) {
            if (! (o instanceof String)) {
                errorAccumulator.add(
                        createTypeMismatchErrorMsg(key, "Array containing Strings",
                                "Array containing " + getDisplayableObjectType(o) + "s"));
                return null;
            }
            response.add(((String)o).trim());
        }
        return response;
    }

    public Set<String> getStringSet (JSONMember member) {
        return getStringSet(member.toString());
    }

    public Set<String> getStringSet (String key) {
        Set<String> returnVal = null;
        List<String> stringList = getStringList(key);
        if (stringList != null) {
            returnVal = new HashSet<>(stringList);
        }
        return returnVal;
    }

    public JSONObject getObject(JSONMember member) {
        return getObject(member.toString());
    }

    public JSONObject getObject(String key) {
        Object result = wrappedObj.get(key);
        if (null == result) {
            return null;
        }
        if (! (result instanceof org.json.simple.JSONObject)) {
            errorAccumulator.add( createTypeMismatchErrorMsg(key, "Object", getDisplayableObjectType(result)));
            return null;
        }

        return new JSONObject( errorAccumulator, getFullyQualifiedName(key), (org.json.simple.JSONObject)result);
    }

    /**
     * Depending on context, a JSON metadata structure named "Foo" could arrive in two forms.
     * <p>In the first form, it can look like this:</p>
     * <pre>
     *       {
     *         "Foo" : {
     *              "Attribute1":123,
     *              "Attribute2":456
     *         }
     *       }
     *</pre>
     * <p>In the second form, the metadata can be "unwrapped" and only contain the attributes:</p>
     * <pre>
     *      {
     *         "Attribute1":123,
     *         "Attribute2":456
     *      }
     *</pre>
     * <p>This utility normalizes the contents into the latter, unwrapped form.</p>
     * @param objectName the name of the JSON member to unwrap
     * @return unwrapped JSONObject
     */
    public JSONObject unwrapObjectIfNeeded(JSONMember objectName) {
        if (containsKey(objectName.toString())) {
            return getObject(objectName);
        }
        else {
            // Already unwrapped
            return this;
        }
    }

}
