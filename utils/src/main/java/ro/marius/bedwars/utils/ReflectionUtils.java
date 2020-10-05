package ro.marius.bedwars.utils;

import java.lang.reflect.Field;

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

    public static void setFieldValue(String fieldName, Class<?> clazz, Object objectToBeReplaced, Object value) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(objectToBeReplaced, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
