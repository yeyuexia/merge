package io.github.yeyuexia.merge.copier.impl;


import io.github.yeyuexia.merge.Merger;
import io.github.yeyuexia.merge.copier.Copier;
import io.github.yeyuexia.merge.exception.MergeException;
import io.github.yeyuexia.merge.helper.Helper;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
            return isFinalValue(field.getType()) ? PropertyUtils.getSimpleProperty(from, field.getName()) :
                   getObjectValue(from, to, field, path);
        } catch (InstantiationException | IllegalAccessException e) {
            LOG.error("init field bean error: {}", e);
            throw new MergeException();
        } catch (NoSuchMethodException | NoSuchFieldException | InvocationTargetException e) {
            LOG.error("get property error: {}", e);
            throw new MergeException();
        }
    }

    public static boolean isWrapperType(Class<?> clazz) {
        return WRAPPER_TYPES.contains(clazz);
    }

    private static Set<Class<?>> getBasicTypes() {
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

    private boolean isFinalValue(Class type) {
        return type.isEnum() || type.isPrimitive() || isWrapperType(type);
    }

    private Object getObjectValue(X from, Y to, Field field, String path) throws IllegalAccessException,
            InvocationTargetException, NoSuchMethodException, InstantiationException, NoSuchFieldException {
        Object toValue = PropertyUtils.getSimpleProperty(to, field.getName());
        Object fromValue = PropertyUtils.getSimpleProperty(from, field.getName());
        Object fieldBean = toValue == null ? generateInstance(fromValue, field.getType()) : toValue;
        if (fieldBean instanceof Collection) {
            Type[] actualTypeArguments = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
            mergeCollectionValue((Collection) fromValue, (Collection) fieldBean, (Class) actualTypeArguments[0],
                    Helper.getPath(path, field.getName()));
        } else {
            merger.merge(fromValue, fieldBean, Helper.getPath(path, field.getName()));
        }
        return fieldBean;
    }

    private void mergeCollectionValue(Collection fromValue, Collection toValue, Class type,
                                      String path) throws NoSuchFieldException, InstantiationException, IllegalAccessException {
        Iterator toIterator = toValue.iterator();
        int index = 0;

        if (isFinalValue(type)) {
            toValue.clear();
            toValue.addAll(fromValue);
        } else {
            for (Object from : fromValue) {
                Object to;
                if (toIterator.hasNext()) {
                    to = toIterator.next();
                } else {
                    to = generateInstance(from, type);
                    toValue.add(to);
                }
                merger.merge(from, to, Helper.getCollectionPath(path,
                        toValue.getClass().isInstance(List.class) ? String.valueOf(index) : "?"));
                index++;
            }
        }
    }

    private Object generateInstance(Object fromValue, Class type) throws NoSuchFieldException,
            InstantiationException, IllegalAccessException {
        if (type.isInstance(fromValue)) {
            return fromValue.getClass().newInstance();
        } else {
            return type.newInstance();
        }
    }
}