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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Merger<X, Y> {
    private static final Logger LOG = LoggerFactory.getLogger(Merger.class);
    private final CopierFactory copierFactory;
    private Boolean ignoreNullValue;

    public Merger(Map<Class, Function> customs, Boolean ignoreNullValue) {
        this.copierFactory = new CopierFactory(this, customs);
        this.ignoreNullValue = ignoreNullValue;

    }

    public boolean merge(X from, Y to) {
        return merge(from, to, "");
    }

    public boolean merge(X from, Y to, String path) {
        return getFields(to.getClass())
                .stream()
                .filter(field -> isWriteAble(from, to, field))
                .filter(field -> isIgnore(from, field))
                .map(field -> updateField(to, field, copierFactory.getCopier(field.getType()).copy(from, to, field)))
                .collect(Collectors.toList()).stream()
                .anyMatch(Boolean::booleanValue);
    }

    private boolean updateField(Y to, Field field, Object value) {
        try {
            if (value.equals(BeanUtilsBean.getInstance().getPropertyUtils().getNestedProperty(to, field.getName()))) {
                return false;
            } else {
                BeanUtils.setProperty(to, field.getName(), value);
                return true;
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return false;
        }
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

    private boolean isIgnore(X from, Field field) {
        try {
            return !(ignoreNullValue && BeanUtils.getProperty(from, field.getName()) == null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOG.error("get property error: {}", e);
            throw new MergeException();
        }
    }

}
