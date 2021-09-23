package ro.marius.bedwars.utils;

import org.apache.commons.lang.ObjectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ReflectionUtils {

    public static Object getPrivateField(String fieldName, Class<?> clazz, Object object) {
        Field field;
        Object o = null;

        try {
            field = clazz.getDeclaredField(fieldName);

            field.setAccessible(true);

            o = field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return o;
    }

    public static Object getMethodResult(String methodName, Object object) {

        try {
            Method method = object.getClass().getMethod(methodName);
            return method.invoke(object);
        } catch (NullPointerException | InvocationTargetException | IllegalAccessException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static Object getField(String fieldName, Object object) {
        Field field;
        Object o = null;

        try {
            field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            o = field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return o;
    }

    public static void setFieldValue(String fieldName, Class<?> clazz, Object objectToBeReplaced, Object value) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(objectToBeReplaced, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setValue(Object fieldInstance,String fieldName, Object value) {
        try {
            Field f = fieldInstance.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(fieldInstance, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
