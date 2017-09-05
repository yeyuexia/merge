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
import java.util.function.Function;

public class CustomCopier extends Copier {
    private static final Logger LOG = LoggerFactory.getLogger(CustomCopier.class);

    private final Function function;

    public CustomCopier(Function function, Merger merger) {
        super(merger);
        this.function = function;
    }

    @Override
    public void copy(Object from, Object to, Field field) {
        try {
            Object value = BeanUtilsBean.getInstance().getPropertyUtils()
                    .getNestedProperty(from, field.getName());
            BeanUtils.setProperty(to, field.getName(), function.apply(value));
        } catch (IllegalAccessException e) {
            LOG.error("init field bean error: {}", e);
            throw new MergeException();
        } catch (NoSuchMethodException | InvocationTargetException e) {
            LOG.error("get property error: {}", e);
            throw new MergeException();
        }
    }
}
