package io.github.yeyuexia.merge.copier.impl;

import io.github.yeyuexia.merge.copier.Copier;
import io.github.yeyuexia.merge.exception.MergeException;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

public class CustomCopier extends Copier {
    private static final Logger LOG = LoggerFactory.getLogger(CustomCopier.class);

    private final Function function;

    public CustomCopier(Function function) {
        this.function = function;
    }

    @Override
    public Object copy(Object from, Object to, Field field, String path) {
        try {
            Object value = BeanUtilsBean.getInstance().getPropertyUtils()
                    .getNestedProperty(from, field.getName());
            return function.apply(value);
        } catch (IllegalAccessException e) {
            LOG.error("init field bean error: {}", e);
            throw new MergeException();
        } catch (NoSuchMethodException | InvocationTargetException e) {
            LOG.error("get property error: {}", e);
            throw new MergeException();
        }
    }
}
