package org.fastfed4j.core.util;

import org.fastfed4j.core.metadata.Metadata;

import java.lang.reflect.Method;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class ReflectionUtils {

    /**
     * Java reflection doesn't support inheritance when looking up methods based on parameters.
     * This utility can find a matching method with support for inheritance in the method parameter types.
     * @param obj the object which implements the method
     * @param methodName the name of the method
     * @param params the parameters to the method
     * @return
     */
    public static Method getMethod(Object obj, String methodName, Object... params) {
        Method[] methods = obj.getClass().getMethods();
        Method returnVal = null;
        methodLoop: for (Method method : methods) {
            if (!methodName.equals(method.getName())) {
                continue;
            }

            Class<?>[] paramTypes = method.getParameterTypes();
            if (params == null && paramTypes.length == 0) {
                returnVal = method;
                break;
            }

            if (params == null || paramTypes.length != params.length) {
                continue;
            }

            for (int i = 0; i < params.length; ++i) {
                if (! isAssignable(paramTypes[i], params[i] == null ? null : params[i].getClass())) {
                    continue methodLoop;
                }
            }
            returnVal = method;
        }

        if (returnVal == null) {
            throw new RuntimeException(obj.getClass().getName() + " has no method " + methodName + " with params (" + params + ")");
        }

        return returnVal;
    }

    /**
     * Creates a copy of an object by invoking the copy constructor.
     * @param originalObj the object to be copied
     * @return the copy
     */
    public static Object copy(Object originalObj) {
        try {
            //Invoke the copy constructor via reflection
            Object copy = originalObj.getClass().getConstructor(originalObj.getClass()).newInstance(originalObj);
            return copy;
        }
        catch (NoSuchMethodException ex) {
            throw new RuntimeException("No copy constructor is defined for class " + originalObj.getClass().getName(), ex);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static boolean isAssignable(Class targetType, Class valueType) {

        if (valueType == null) {
            return true;
        }

        if (targetType.isAssignableFrom(valueType)) {
            return true;
        }

        return false;
    }

}
