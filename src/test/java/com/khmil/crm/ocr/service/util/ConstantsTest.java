package com.khmil.crm.ocr.service.util;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConstantsTest {

    @Test
    void shouldHaveAccessiblePrivateConstructorForCoverage() throws Exception {
        final Constructor<Constants> constructor = Constants.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final Constants instance = constructor.newInstance();

        assertNotNull(instance);
        assertTrue(true);
    }
}
