package com.ll.gramgram.standard.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class UtTest {
    public static boolean setFieldValue(Object o, String fieldName, Object value) {
        return Ut.reflection.setFieldValue(o, fieldName, value);
    }
}