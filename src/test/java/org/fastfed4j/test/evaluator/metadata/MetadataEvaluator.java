package org.fastfed4j.test.evaluator.metadata;

import org.fastfed4j.core.metadata.Metadata;
import org.fastfed4j.core.util.ReflectionUtils;
import org.fastfed4j.test.evaluator.Operation;
import org.junit.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class MetadataEvaluator {

    public void evaluate(Operation operation, Metadata specimen) {
        Objects.requireNonNull(operation, "operation must not be null");
        Objects.requireNonNull(specimen, "specimen1 must not be null");
        evaluate(operation, specimen, null);
    }

    public void evaluate(Operation operation, Metadata specimen1, Metadata specimen2) {
        Objects.requireNonNull(operation, "operation must not be null");
        Objects.requireNonNull(specimen1, "specimen1 must not be null");
        switch (operation) {
            // The second specimen is only required for equality tests.
            case AssertEquals:
            case ToggleAndAssertNotEquals:
                Objects.requireNonNull(specimen2, "specimen2 must not be null");
        }
    }

    public void performOperation(Operation operation, Object specimen1, Object specimen2, String attributeName) {
        switch (operation) {
            case AssertNotEmpty:
                assertNotEmpty(specimen1, attributeName);
                break;
            case AssertEquals:
                assertEquals(specimen1, specimen2, attributeName);
                break;
            case ToggleAndAssertNotEquals:
                toggleAndAssertNotEquals(specimen1, specimen2, attributeName);
                break;
            default:
                throw new RuntimeException("Unrecognized operation: " + operation.toString());
        }
    }

    private void assertNotEmpty(Object specimen, String attributeName) {
        Object value = getAttributeValue(specimen, attributeName);
        Assert.assertFalse(specimen.getClass() + ":" + attributeName + " is missing" + value, isEmpty(value));
    }

    private void assertEquals(Object specimen1, Object specimen2, String attributeName) {
        Object value1 = getAttributeValue(specimen1, attributeName);
        Object value2 = getAttributeValue(specimen2, attributeName);
        Assert.assertEquals(specimen1.getClass() + ":" + attributeName + " is not equal", value1, value2);
    }

    private void toggleAndAssertNotEquals(Object specimen1, Object specimen2, String attributeName) {
        Object originalValue1 = getAttributeValue(specimen1, attributeName);
        Object originalValue2 = getAttributeValue(specimen2, attributeName);

        // If values are null or empty, exit out of this test.
        if (isEmpty(originalValue1) && isEmpty(originalValue2)) return;

        // Before starting, ensure all the equality operations behave as expected.
        // First, the individual attributes should be the same.
        // Second, the entire specimens should evaluate to be equal.
        Assert.assertEquals(specimen1.getClass() + ":" + attributeName + " is not equal", originalValue1, originalValue2);
        Assert.assertEquals("Specimens are not equal", specimen1, specimen2);

        // Change the value on specimen 2.
        // Not all values are mutable. The response indicates if the operation was successful.
        boolean wasModified = setAttributeValue(specimen2, attributeName, generateDifferentValue(originalValue2));

        if (wasModified) {
            // Reload the values and ensure they now differ.
            Object newValue1 = getAttributeValue(specimen1, attributeName);
            Object newValue2 = getAttributeValue(specimen2, attributeName);
            Assert.assertNotEquals(attributeName + " is equal", newValue1, newValue2);

            // Also confirm the entire specimens evaluate to non-equal
            Assert.assertNotEquals("Specimens are equal after changing the value of " + specimen1.getClass() + ":" + attributeName,
                                   specimen1, specimen2);
        }

        // As an additional test, nullify the value on specimen 2.
        // Not all attributes can be nullified. The response indicates if the operation was successful.
        boolean wasNullified = setAttributeValue(specimen2, attributeName, null);

        if (wasNullified) {
            // Once again, reload the values and ensure they differ.
            Object newValue1B = getAttributeValue(specimen1, attributeName);
            Object newValue2B = getAttributeValue(specimen2, attributeName);
            Assert.assertNotEquals(specimen1.getClass() + ":" + attributeName + " is equal", newValue1B, newValue2B);
            Assert.assertNotEquals("Specimens are equal", specimen1, specimen2);
        }

        // Restore the original value on specimen 2
        setAttributeValue(specimen2, attributeName, originalValue2);
    }

    private Object getAttributeValue(Object obj, String attributeName) {
        try {
            Method method = getAttributeGetter(obj, attributeName);
            return method.invoke(obj);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    private Class getAttributeClass(Object obj, String attributeName) {
        try {
            Method method = getAttributeGetter(obj, attributeName);
            return method.getReturnType();
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private Method getAttributeGetter(Object obj, String attributeName) {
        String methodName = "get" + uppercaseFirstCharacter(attributeName);
        return ReflectionUtils.getMethod(obj, methodName);
    }

    private boolean setAttributeValue(Object obj, String attributeName, Object newValue) {
        // If the new value is the same as the current value, stop executing and simply
        // return false to indicate nothing was modified.
        Object currentValue = getAttributeValue(obj, attributeName);
        if (Objects.equals(currentValue, newValue) || (currentValue == Optional.empty() && newValue == null)) {
            return false;
        }

        String methodName = "set" + uppercaseFirstCharacter(attributeName);
        try {
            Method method = ReflectionUtils.getMethod(obj, methodName, newValue);
            method.invoke(obj, newValue);
        }
        catch (InvocationTargetException ex) {
            //This can happen if the method asserts inputs cannot be null. In this case, OK to ignore.
            if (newValue == null) {
                return false;
            } else {
                throw new RuntimeException(ex);
            }
        }
        catch (Exception ex) {
            System.out.println("Error when calling " + methodName + "(" + newValue + "), " + ex.getMessage());
            throw new RuntimeException(ex);
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private Object generateDifferentValue(Object currentValue) {
        if (currentValue == null) {
            return null;
        }
        else if (currentValue instanceof Metadata) {
            // Do nothing. The visitor will operate on the sub-attributes of this object as it recurses further.
            return currentValue;
        }
        else if (currentValue instanceof String) {
            return generateRandomString();
        }
        else if (currentValue instanceof Integer) {
            return ((Integer)currentValue + 1);
        }
        else if (currentValue instanceof Long) {
            return ((Long)currentValue + 1);
        }
        else if (currentValue instanceof Date) {
            return new Date ( ((Date)currentValue).getTime() + 1);
        }
        else if (currentValue instanceof Boolean) {
            return !((Boolean) currentValue);
        }
        else if (currentValue instanceof Collection) {
            return generateDifferentCollection((Collection)currentValue);
        }
        else if (currentValue instanceof Enum) {
            return generateDifferentEnumValue((Enum)currentValue);
        }
        else if (currentValue instanceof Optional) {
            return generateDifferentOptional((Optional)currentValue);
        }
        else {
            throw new RuntimeException("No handling for type: " + currentValue.getClass().getName());
        }
    }

    @SuppressWarnings("unchecked")
    private Collection generateDifferentCollection(Collection currentValue) {
        try {
            // Create a copy of the collection
            Collection newValue = currentValue.getClass().getConstructor().newInstance();
            newValue.addAll(currentValue);
            // If the collection has any elements, remove one to change the contents of the collection.
            if (! newValue.isEmpty()) {
                newValue.remove( newValue.iterator().next());
            }
            return newValue;
        }
        catch (Exception ex ) {
            throw new RuntimeException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    private Enum generateDifferentEnumValue(Enum currentValue) {
        EnumSet values = EnumSet.allOf(currentValue.getClass());
        for (Object i : values) {
            if (! i.equals(currentValue))
                return (Enum)i;
        }
        // If a differing value was not found, it is probably because the currentValue is the only one
        // in the enumeration. Cannot change it. Simply echo it back.
        return currentValue;
    }

    @SuppressWarnings("unchecked")
    private Optional generateDifferentOptional(Optional currentValue) {
        if (currentValue.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(generateDifferentValue(currentValue.get()));
        }
    }

    private String generateRandomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 20;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }

    private String uppercaseFirstCharacter(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @SuppressWarnings("unchecked")
    private boolean isEmpty(Object value) {

        if (value == null) {
            return true;
        }

        if (value instanceof String) {
            return ((String)value).isEmpty();
        }

        if (value instanceof Collection) {
            return ((Collection)value).isEmpty();
        }

        return false;
    }
}
