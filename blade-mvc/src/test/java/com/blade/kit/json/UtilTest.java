package com.blade.kit.json;

import com.blade.kit.ReflectKit;
import com.blade.kit.StringKit;
import com.blade.kit.ason.Ason;
import com.blade.kit.ason.AsonArray;
import com.blade.kit.ason.AsonIgnore;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.blade.kit.ason.Util.*;
import static org.junit.Assert.*;

/**
 * @author Aidan Follestad (afollestad)
 */
@SuppressWarnings("unused")
public class UtilTest {

    @AsonIgnore
    Field ignoreYes1;
    Field $jacocoData;
    Field ignoreNo2;

    @SuppressWarnings({"FieldCanBeLocal", "unused", "MismatchedQueryAndUpdateOfCollection"})
    private List<Ason> listField;

    @Test
    public void test_is_list_cls() {
        assertFalse(isList(null));
        assertFalse(isList(int.class));
        assertFalse(isList(SimpleTestDataOne.class));
        assertTrue(isList(List.class));
        assertTrue(isList(ArrayList.class));
    }

    @Test
    public void generic_list_type_test() throws Exception {
        listField = new ArrayList<>(0);
        Field field = getClass().getDeclaredField("listField");
        assertEquals(Ason.class, listGenericType(field));
    }

    @Test
    public void test_is_number_true() {
        assertTrue(StringKit.isNumber("1234"));
        assertTrue(StringKit.isNumber("67891023231"));
    }

    @Test
    public void test_is_number_false() {
        assertFalse(StringKit.isNumber("hi"));
        assertFalse(StringKit.isNumber("@1234"));
        assertFalse(StringKit.isNumber("1234!%"));
    }

    @Test
    public void test_is_json_array_true() {
        assertTrue(isJsonArray("[]"));
        assertTrue(isJsonArray("   []    "));
    }

    @Test
    public void test_is_json_array_false() {
        assertFalse(isJsonArray(null));
        assertFalse(isJsonArray(""));
        assertFalse(isJsonArray("{}"));
        assertFalse(isJsonArray("  abc"));
    }

    @Test
    public void test_no_default_ctor() {
        try {
            getDefaultConstructor(NoDefaultCtorClass.class);
            assertFalse("No exception thrown for no default constructor!", false);
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void test_cant_access_field() throws Exception {
        DefaultCtorClass instance =
                (DefaultCtorClass) getDefaultConstructor(DefaultCtorClass.class).newInstance();
        Field field = DefaultCtorClass.class.getDeclaredField("hiddenField");
        try {
            setFieldValue(field, instance, "Test");
            assertFalse("No exception was thrown for accessing inaccessible field!", false);
        } catch (RuntimeException ignored) {
        }
    }

    @Test
    public void test_is_null() {
        assertTrue(isNull(null));
        assertTrue(isNull(JSONObject.NULL));
        assertFalse(isNull("Hello"));
        assertFalse(isNull(new Ason()));
        assertFalse(isNull(new AsonArray<>()));
    }

    @Test
    public void test_should_ignore() throws Exception {
        assertTrue(shouldIgnore(getClass().getDeclaredField("ignoreYes1")));
        assertTrue(shouldIgnore(getClass().getDeclaredField("$jacocoData")));
        assertFalse(shouldIgnore(getClass().getDeclaredField("ignoreNo2")));
    }

    @Test
    public void test_default_primitive() throws Exception {
        assertEquals(false, ReflectKit.defaultPrimitiveValue(boolean.class));
        assertEquals(0d, ReflectKit.defaultPrimitiveValue(double.class));
        assertEquals(0f, ReflectKit.defaultPrimitiveValue(float.class));
        assertEquals((short) 0, ReflectKit.defaultPrimitiveValue(short.class));
        assertEquals(0, ReflectKit.defaultPrimitiveValue(int.class));
        assertEquals(0L, ReflectKit.defaultPrimitiveValue(long.class));
        assertEquals((byte) 0, ReflectKit.defaultPrimitiveValue(byte.class));
        assertEquals('\0', ReflectKit.defaultPrimitiveValue(char.class));
        assertNull(ReflectKit.defaultPrimitiveValue(String.class));
        assertNull(ReflectKit.defaultPrimitiveValue(Character.class));
    }

    @SuppressWarnings("unused")
    static class DefaultCtorClass {

        private String hiddenField;

        @SuppressWarnings("unused")
        public DefaultCtorClass() {
        }
    }

    @SuppressWarnings("unused")
    static class NoDefaultCtorClass {

        @SuppressWarnings("unused")
        public NoDefaultCtorClass(String name) {
        }
    }

    @SuppressWarnings("unused")
    static class DefaultCtorErrorClass {

        @SuppressWarnings("unused")
        public DefaultCtorErrorClass() {
            throw new IllegalStateException("Here's an exception!");
        }
    }
}
