package com.yyx.merge;

import com.yyx.merge.exception.MergeException;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
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
                .filter(field -> propertyUtils.isWriteable(to, field))
                .filter(field -> propertyUtils.isReadable(to, field))
                .filter(field -> propertyUtils.isWriteable(from, field))
                .filter(field -> propertyUtils.isReadable(from, field))
                .filter(field -> isNullValue(from, field))
                .forEach(field -> copy(from, to, field));
        return true;
    }

    private <X, Y> void copy(X from, Y to, String field) {
        try {
            propertyUtils.setProperty(to, field, propertyUtils.getProperty(from, field));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOG.error("get property error: {}", e);
        }
    }

    private <X> boolean isNullValue(X from, String field) {
        try {
            return !(ignoreNullValue && propertyUtils.getProperty(from, field) == null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOG.error("get property error: {}", e);
        }
        return false;
    }

    public Merger ignoreNullValue(boolean ignored) {
        this.ignoreNullValue = ignored;
        return this;
    }
}
