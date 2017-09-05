package com.yyx.merge;

import com.yyx.merge.copier.CopierFactory;
import com.yyx.merge.exception.MergeException;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Merger<X, Y> {
    private static final Logger LOG = LoggerFactory.getLogger(Merger.class);
    private final CopierFactory copierFactory;
    private final Map<Class, Function> customs;
    private Boolean ignoreNullValue;

    public Merger() {
        this.copierFactory = new CopierFactory(this);
        this.ignoreNullValue = true;
        this.customs = new HashMap<>();
    }

    public Merger ignoreNullValue(Boolean ignoreNullValue) {
        this.ignoreNullValue = ignoreNullValue;
        return this;
    }

    public <F, T> Merger custom(Class<T> specialClass, Function<F, T> generator) {
        this.customs.put(specialClass, generator);
        return this;
    }

    public boolean merge(X from, Y to) {
        getFields(to.getClass())
                .stream()
                .filter(field -> isWriteAble(from, to, field))
                .filter(field -> isNullValue(from, field))
                .forEach(field -> copierFactory.getCopier(field.getType(), customs).copy(from, to, field));
        return true;
    }

    private List<Field> getFields(Class from) {
        if (from == null || from == Object.class) {
            return new ArrayList<>();
        }
        List<Field> fields = new ArrayList<>(Arrays.asList(from.getDeclaredFields()));
        final Set<String> names = fields.stream().map(Field::getName).collect(Collectors.toSet());
        fields.addAll(getFields(from.getSuperclass()).stream()
                .filter(field -> !names.contains(field.getName()))
                .collect(Collectors.toList()));
        return fields;
    }

    private boolean isWriteAble(X from, Y to, Field field) {
        String name = field.getName();
        PropertyUtilsBean propertyUtils = BeanUtilsBean.getInstance().getPropertyUtils();
        return propertyUtils.isWriteable(to, name) && propertyUtils.isReadable(from, name);
    }

    private boolean isNullValue(X from, Field field) {
        try {
            return !(ignoreNullValue && BeanUtils.getProperty(from, field.getName()) == null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOG.error("get property error: {}", e);
            throw new MergeException();
        }
    }

}
