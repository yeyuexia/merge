package com.yyx.merge.copier.impl;


import com.yyx.merge.Merger;
import com.yyx.merge.copier.Copier;
import com.yyx.merge.exception.MergeException;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import static com.yyx.merge.Helper.getPath;

public class DeepCopyCopier<X, Y> extends Copier<X, Y> {
    private static final Logger LOG = LoggerFactory.getLogger(DeepCopyCopier.class);

    private static final Set<Class<?>> WRAPPER_TYPES = getBasicTypes();

    private final Merger merger;

    public DeepCopyCopier(Merger merger) {
        this.merger = merger;
    }

    @Override
    public Object copy(X from, Y to, Field field, String path) {
        try {
            return isFinalValue(field) ? getFromValue(from, field) : getObjectValue(from, to, field, path);
        } catch (InstantiationException | IllegalAccessException e) {
            LOG.error("init field bean error: {}", e);
            throw new MergeException();
        } catch (NoSuchMethodException | InvocationTargetException e) {
            LOG.error("get property error: {}", e);
            throw new MergeException();
        }
    }

    public static boolean isWrapperType(Class<?> clazz)
    {
        return WRAPPER_TYPES.contains(clazz);
    }

    private static Set<Class<?>> getBasicTypes()
    {
        Set<Class<?>> ret = new HashSet<>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        ret.add(String.class);
        return ret;
    }

    private boolean isFinalValue(Field field) {
        return field.getType().isEnum() || field.getType().isPrimitive() || isWrapperType(field.getType());
    }

    private Object getObjectValue(X from, Y to, Field field, String path) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        Object property = getFromValue(to, field);
        Object fieldBean = property == null ? field.getType().newInstance() : property;
        merger.merge(getFromValue(from, field), fieldBean, getPath(path, field.getName()));
        return fieldBean;
    }

    private Object getFromValue(Object object, Field field) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return BeanUtilsBean.getInstance().getPropertyUtils().getNestedProperty(object, field.getName());
    }
}
