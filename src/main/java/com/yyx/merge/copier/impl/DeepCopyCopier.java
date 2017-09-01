package com.yyx.merge.copier.impl;


import com.yyx.merge.Merger;
import com.yyx.merge.copier.Copier;
import com.yyx.merge.exception.MergeException;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

public class DeepCopyCopier<X, Y> extends Copier<X, Y> {
    private static final Logger LOG = LoggerFactory.getLogger(DeepCopyCopier.class);
    private static final Set<Class<?>> WRAPPER_TYPES = getBasicTypes();

    public DeepCopyCopier(Merger merger) {
        super(merger);
    }

    @Override
    public void copy(X from, Y to, Field field) {
        try {
            Object fieldBean = getFieldValue(from, field);
            BeanUtils.setProperty(to, field.getName(), fieldBean);
        } catch (IllegalAccessException | InvocationTargetException e) {
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

    private Object getFieldValue(X from, Field field) {
        try {
            if (field.getType().isEnum() || field.getType().isPrimitive() || isWrapperType(field.getType())) {
                return BeanUtils.getProperty(from, field.getName());
            }
            Object fieldBean = field.getType().newInstance();
            Object value = BeanUtilsBean.getInstance().getPropertyUtils().getNestedProperty(from, field.getName());
            merger.merge(value, fieldBean);
            return fieldBean;
        } catch (InstantiationException | IllegalAccessException e) {
            LOG.error("init field bean error: {}", e);
            throw new MergeException();
        } catch (NoSuchMethodException | InvocationTargetException e) {
            LOG.error("get property error: {}", e);
            throw new MergeException();
        }
    }
}
