package org.fastfed4j.core.json;

import java.util.*;
import java.util.stream.Collectors;

import org.fastfed4j.core.exception.ErrorAccumulator;

/**
 * Abstracts an underlying JSON parser implementation behind a consistent interface. Also adds convenience utilities for
 * casting to particular data types and reporting errors when JSON contents do not conform to the expected types.
 */
@SuppressWarnings("unchecked")
public class JsonObject {
    public static final String JSON_PATH_DELIMITER = ".";

    private final org.json.simple.JSONObject impl;
    private final ErrorAccumulator errorAccumulator;
    private final String jsonPath;

    /**
     * JsonObject is immutable. Builder pattern is used for construction.
     */
    public static class Builder {
        private final Optional<String> wrapperName;
        private final JsonObject instance = new JsonObject( new ErrorAccumulator(), new org.json.simple.JSONObject());

        // There isn't a deep clone method in JSON Simple. As a result, when built, this implementation simply returns
        // the JsonObject used during construction. This opens the door to a risk that somebody builds a new JsonObject
        // and then continues to invoke put() methods on the Builder that generated the object, causing the contents of
        // the supposedly immutable JsonObject to mutate. To enforce a stronger promise of immutability, a flag is set
        // after building in order to block subsequent mutations.
        // It's a little hokey, but might be sufficient for now. To address this and other gaps, it may be appropriate
        // in the future to switch to a more feature-rich JSON parser.
        volatile private boolean finished = false;

        public Builder() {
            this.wrapperName = Optional.empty();
        }

        /**
         * Construct a Builder with a JSON wrapper name.
         *
         * <p>Upon invoking the build() method, the JSON contents will be nested inside another object keyed by
         * by the wrapper name. For example, if the following attributes are set:</p>
         * <pre>
         *     JsonObject.Builder builder = new JsonObject.Builder("Foo");
         *     builder.put("Attribute1", "Hello");
         *     builder.put("Attribute2", "World");
         * </pre>
         * then an invocation of build() would produce a JsonObject with the following contents:
         * <pre>
         *     {
         *         "Foo": {
         *             "Attribute1": "Hello",
         *             "Attribute2": "World"
         *         }
         *     }
         * </pre>
         */
        public Builder(String wrapperName) {
            Objects.requireNonNull(wrapperName, "wrapperName must not be null");
            this.wrapperName = Optional.of(wrapperName);
        }

        public Builder put(String memberName, JsonObject value) {
            assertUnfinished();
            if (value == null) return this;
            instance.impl.put(memberName, value.impl);
            return this;
        }

        public Builder put(String memberName, Collection<String> value) {
            assertUnfinished();
            if (value == null) return this;
            org.json.simple.JSONArray jsonArray = new org.json.simple.JSONArray();
            jsonArray.addAll(value);
            instance.impl.put(memberName, jsonArray);
            return this;
        }

        public Builder put(String memberName, Date value) {
            assertUnfinished();
            if (value == null) return this;
            instance.impl.put(memberName, value.getTime());
            return this;
        }

        public Builder put(String memberName, Object value) {
            assertUnfinished();
            if (value == null) return this;
            instance.impl.put(memberName, value);
            return this;
        }

        public Builder putAll(JsonObject obj) {
            instance.impl.putAll(obj.impl);
            return this;
        }

        void assertUnfinished() {
            if (finished) {
                throw new RuntimeException("Illegal invocation of JsonObject.Builder on an already built instance.");
            }
        }

        public JsonObject build() {
            finished = true;
            if (wrapperName.isPresent()) {
                return new Builder().put(wrapperName.get(), instance).build();
            }
            return instance;
        }
    }

    protected JsonObject(ErrorAccumulator errorAccumulator, org.json.simple.JSONObject jsonObject) {
        this(errorAccumulator, jsonObject, "");
    }

    protected JsonObject(ErrorAccumulator errorAccumulator, org.json.simple.JSONObject jsonObject, String jsonPath) {
        Objects.requireNonNull(errorAccumulator, "errorAccumulator must not be null");
        Objects.requireNonNull(jsonPath, "jsonPath must not be null");
        Objects.requireNonNull(jsonObject, "jsonObject must not be null");
        this.errorAccumulator = errorAccumulator;
        this.jsonPath = jsonPath;
        this.impl = jsonObject;
    }

    /**
     * Ensure that empty, null, and missing values are all treated equivalently by normalizing them into null
     * and/or removing them from collections.
     */
    private Object normalize(Object value) {
        if (value == null) {
            return null;
        }
        else if (value instanceof String) {
            String stringValue = ((String) value).trim();
            if (stringValue.isEmpty()) {
                return null;
            } else {
                return value;
            }
        }
        else if (value instanceof org.json.simple.JSONArray) {
            org.json.simple.JSONArray originalValue = (org.json.simple.JSONArray) value;
            org.json.simple.JSONArray filteredValue = new org.json.simple.JSONArray();
            for (Object o : originalValue) {
                //Filter nulls from the collection
                Object normalizedEntry = normalize(o);
                if (normalizedEntry != null) filteredValue.add(normalizedEntry);
            }
            return filteredValue;
        }
        return value;
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
            return String.join(JSON_PATH_DELIMITER, jsonPath, jsonMemberName);
        }
    }

    public ErrorAccumulator getErrorAccumulator() {
        return errorAccumulator;
    }

    public boolean onlyContainsKey(String key) {
        return containsKey(key) && keySet().size() == 1;
    }

    public boolean containsKey(String key) {
        return impl.containsKey(key);
    }

    public boolean containsValueForKey(String key) {
        return (null != normalize(impl.get(key)));
    }

    public Set<String> keySet() {
        return new HashSet<String>( impl.keySet());
    }

    public String getString(String key) {
        Object result = normalize(impl.get(key));
        if (null == result) {
            return null;
        }
        if (! (result instanceof String)) {
            errorAccumulator.add( createTypeMismatchErrorMsg(key, "String", getDisplayableObjectType(result)));
            return null;
        }
        return (String)result;
    }

    public Boolean getBoolean(String key) {
        Object result = normalize(impl.get(key));
        if (null == result) {
            return null;
        }
        if (! (result instanceof Boolean)) {
            errorAccumulator.add( createTypeMismatchErrorMsg(key, "Boolean", getDisplayableObjectType(result)));
            return null;
        }
        return (Boolean)result;
    }

    public Integer getInteger(String key) {
        Object result = normalize(impl.get(key));
        if (null == result) {
            return null;
        }
        if (! (result instanceof Long)) {
            errorAccumulator.add( createTypeMismatchErrorMsg(key, "Number", getDisplayableObjectType(result)));
            return null;
        }
        if ((Long)result > Integer.MAX_VALUE || (Long)result < Integer.MIN_VALUE) {
            errorAccumulator.add( "Invalid value for " + getFullyQualifiedName(key) + "(" + result + " out of bounds for an integer)");
            return null;
        }
        return ((Long) result).intValue();
    }

    public Long getLong(String key) {
        Object result = normalize(impl.get(key));
        if (null == result) {
            return null;
        }
        if (! (result instanceof Long)) {
            errorAccumulator.add( createTypeMismatchErrorMsg(key, "Long", getDisplayableObjectType(result)));
            return null;
        }
        return (Long)result;
    }

    public Date getDate(String key) {
        Long longValue = getLong(key);
        Date result = null;
        if (longValue != null) {
            result = new Date(longValue);
        }
        return result;
    }

    public List<String> getStringList(String key) {
        Object result = normalize(impl.get(key));

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
            response.add((String)o);
        }
        return response;
    }

    public Set<String> getStringSet (String key) {
        Set<String> returnVal = null;
        List<String> stringList = getStringList(key);
        if (stringList != null) {
            returnVal = new HashSet<>(stringList);
        }
        return returnVal;
    }

    /**
     * The FastFed spec generally requires that unspecified or null values for lists be
     * treated equivalently to an empty list. To comply with this requirement, this is a
     * convenience method that returns an empty list if the list is undefined.
     * @param key
     * @return Returns the value if defined in the JSON, else returns an empty set.
     */
    public Set<String> getNonNullableStringSet (String key) {
        List<String> stringList = getStringList(key);
        if (stringList != null) {
            return new HashSet<String>(stringList);
        } else {
            return new HashSet<String>();
        }
    }

    public JsonObject getObject(String key) {
        Object result = impl.get(key);
        if (null == result) {
            return null;
        }
        if (! (result instanceof org.json.simple.JSONObject)) {
            errorAccumulator.add( createTypeMismatchErrorMsg(key, "Object", getDisplayableObjectType(result)));
            return null;
        }

        return new JsonObject( errorAccumulator, (org.json.simple.JSONObject)result, getFullyQualifiedName(key));
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
     * @param wrapperName the name of the JSON member to unwrap
     * @return unwrapped JsonObject
     */
    public JsonObject unwrapObjectIfNeeded(String wrapperName) {
        if (onlyContainsKey(wrapperName)) {
            return getObject(wrapperName);
        }
        else {
            // Already unwrapped
            return this;
        }
    }

    @Override
    public String toString() {
        return impl.toJSONString();
    }
}
