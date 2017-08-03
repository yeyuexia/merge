package com.yyx.merge;

import com.yyx.merge.exception.MergeException;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Merger<X, Y> {
    private static final Logger LOG = LoggerFactory.getLogger(Merger.class);
    private final Class<X> fromClass;
    private final Class<Y> toClass;
    private final Boolean ignoreNullValue;

    public Merger(Class<X> fromClass, Class<Y> toClass, Boolean ignoreNullValue) {
        this.fromClass = fromClass;
        this.toClass = toClass;
        this.ignoreNullValue = ignoreNullValue;
    }


    public boolean merge(X from, Y to) {
        return merge(from, to, true);
    }

    public boolean merge(X from, Y to, boolean ignoreNullValue) {
        getFieldNames(from.getClass())
                .stream()
                .filter(field -> isWriteAble(from, to, field))
                .filter(field -> isNullValue(from, field))
                .forEach(field -> copy(from, to, field));
        return true;
    }

    private Set<String> getFieldNames(Class from) {
        if (from == null) {
            return new HashSet();
        }
        Set<String> names = Arrays.stream(from.getDeclaredFields())
                .map(Field::getName).collect(Collectors.toSet());
        names.addAll(getFieldNames(from.getSuperclass()));
        return names;
    }

    private boolean isWriteAble(X from, Y to, String field) {
        PropertyUtilsBean propertyUtils = BeanUtilsBean.getInstance().getPropertyUtils();
        return propertyUtils.isWriteable(to, field) && propertyUtils.isReadable(to, field)
                && propertyUtils.isWriteable(from, field) && propertyUtils.isReadable(from, field);
    }

    private void copy(X from, Y to, String field) {
        try {
            BeanUtils.setProperty(to, field, BeanUtils.getProperty(from, field));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOG.error("get property error: {}", e);
            throw new MergeException();
        }
    }

    private boolean isNullValue(X from, String field) {
        try {
            return !(ignoreNullValue && BeanUtils.getProperty(from, field) == null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOG.error("get property error: {}", e);
            throw new MergeException();
        }
    }

}
