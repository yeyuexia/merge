package com.yyx.merge;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;

public class BaseTest {
    protected PropertyUtilsBean propertyUtils = BeanUtilsBean.getInstance().getPropertyUtils();

    protected <X, Y> void fieldNonEquals(X from, Y to, String field) {
        try {
            assertEquals(propertyUtils.getProperty(from, field), propertyUtils.getProperty(to, field));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    protected  <X, Y> void fieldEquals(X from, Y to, String field) {
        try {
            assertEquals(propertyUtils.getProperty(from, field), propertyUtils.getProperty(to, field));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
