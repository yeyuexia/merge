package com.yyx.merge;

import com.yyx.merge.exception.MergeException;
import org.apache.commons.beanutils.BeanUtilsBean2;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class Merger {
    private static final Logger LOG = LoggerFactory.getLogger(Merger.class);

    private Boolean ignoreNullValue;

    private PropertyUtilsBean propertyUtils = BeanUtilsBean2.getInstance().getPropertyUtils();

    public <X, Y> boolean merge(X from, Y to) {
        return merge(from, to, true);
    }

    public <X, Y> boolean merge(X from, Y to, boolean ignoreNullValue) {
        Arrays.stream(to.getClass().getDeclaredFields())
                .map(Field::getName)
                .filter(field -> isWriteAble(from, to, field))
                .filter(field -> isNullValue(from, field))
                .forEach(field -> copy(from, to, field));
        return true;
    }

    private <X, Y> boolean isWriteAble(X from, Y to, String field) {
        return propertyUtils.isWriteable(to, field) && propertyUtils.isReadable(to, field)
                && propertyUtils.isWriteable(from, field) && propertyUtils.isReadable(from, field);
    }

    private <X, Y> void copy(X from, Y to, String field) {
        try {
            propertyUtils.setProperty(to, field, propertyUtils.getProperty(from, field));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOG.error("get property error: {}", e);
            throw new MergeException();
        }
    }

    private <X> boolean isNullValue(X from, String field) {
        try {
            return !(ignoreNullValue && propertyUtils.getProperty(from, field) == null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOG.error("get property error: {}", e);
            throw new MergeException();
        }
    }

    public Merger ignoreNullValue(boolean ignored) {
        this.ignoreNullValue = ignored;
        return this;
    }
}
