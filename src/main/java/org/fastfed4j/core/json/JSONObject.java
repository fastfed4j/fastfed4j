package org.fastfed4j.core.json;

import java.util.*;

import org.fastfed4j.core.exception.ErrorAccumulator;

/**
 * Abstracts an underlying JSON parser implementation behind a consistent interface. Also adds convenience utilities for
 * casting to particular data types and reporting errors when JSON contents do not conform to the expected types.
 */
public class JSONObject {
    public static final String JSON_PATH_DELIMITER = ".";

    private final org.json.simple.JSONObject impl;
    private final ErrorAccumulator errorAccumulator;
    private final String jsonPath;

    /**
     * JSONObject is immutable. Builder pattern is used for construction.
     */
    public static class Builder {
        private Optional<String> wrapperName;
        private final JSONObject instance = new JSONObject( new ErrorAccumulator(), new org.json.simple.JSONObject());

        // There isn't a deep clone method in JSON Simple. As a result, when built, this implementation simply returns
        // the JSONObject used during construction. This opens the door to a risk that somebody builds a new JSONObject
        // and then continues to invoke put() methods on the Builder that generated the object, causing the contents of
        // the supposedly immutable JSONObject to mutate. To enforce a stronger promise of immutability, a flag is set
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
         *     JSONObject.Builder builder = new JSONObject.Builder("Foo");
         *     builder.put("Attribute1", "Hello");
         *     builder.put("Attribute2", "World");
         * </pre>
         * then an invocation of build() would produce a JSONObject with the following contents:
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

        public Builder put(String memberName, JSONObject value) {
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

        /*
        public Builder put(String memberName, boolean value) {
            assertUnfinished();
            instance.impl.put(memberName, value);
            return this;
        }
*/

        public Builder putAll(JSONObject obj) {
            instance.impl.putAll(obj.impl);
            return this;
        }

        void assertUnfinished() {
            if (finished) {
                throw new RuntimeException("Illegal invocation of JSONObject.Builder on an already built instance.");
            }
        }

        public JSONObject build() {
            finished = true;
            if (wrapperName.isPresent()) {
                return new Builder().put(wrapperName.get(), instance).build();
            }
            return instance;
        }
    }

    protected JSONObject(ErrorAccumulator errorAccumulator, org.json.simple.JSONObject jsonObject) {
        this(errorAccumulator, "", jsonObject);
    }

    protected JSONObject(ErrorAccumulator errorAccumulator, String jsonPath, org.json.simple.JSONObject jsonObject) {
        Objects.requireNonNull(errorAccumulator, "errorAccumulator must not be null");
        Objects.requireNonNull(jsonPath, "jsonPath must not be null");
        Objects.requireNonNull(jsonObject, "jsonObject must not be null");
        this.errorAccumulator = errorAccumulator;
        this.jsonPath = jsonPath;
        this.impl = jsonObject;
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

    public boolean containsValueForMember(String key) {
        return (impl.get(key) != null);
    }

    public Set<String> keySet() {
        return new HashSet<String>( impl.keySet());
    }

    public String getString(String key) {
        Object result = impl.get(key);
        if (null == result) {
            return null;
        }
        if (! (result instanceof String)) {
            errorAccumulator.add( createTypeMismatchErrorMsg(key, "String", getDisplayableObjectType(result)));
            return null;
        }
        return ((String)result).trim();
    }

    public Boolean getBoolean(String key) {
        Object result = impl.get(key);
        if (null == result) {
            return null;
        }
        if (! (result instanceof Boolean)) {
            errorAccumulator.add( createTypeMismatchErrorMsg(key, "Boolean", getDisplayableObjectType(result)));
            return null;
        }
        return (Boolean)result;
    }

    public Long getLong(String key) {
        Object result = impl.get(key);
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
        Object result = impl.get(key);

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

    public Set<String> getStringSet (String key) {
        Set<String> returnVal = null;
        List<String> stringList = getStringList(key);
        if (stringList != null) {
            returnVal = new HashSet<>(stringList);
        }
        return returnVal;
    }

    public JSONObject getObject(String key) {
        Object result = impl.get(key);
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
     * @param wrapperName the name of the JSON member to unwrap
     * @return unwrapped JSONObject
     */
    public JSONObject unwrapObjectIfNeeded(String wrapperName) {
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
