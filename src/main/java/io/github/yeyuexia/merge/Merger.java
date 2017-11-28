package io.github.yeyuexia.merge;

import io.github.yeyuexia.merge.copier.CopierFactory;
import io.github.yeyuexia.merge.function.FieldUpdateNotifier;
import io.github.yeyuexia.merge.exception.MergeException;
import io.github.yeyuexia.merge.helper.UpdateCollector;
import io.github.yeyuexia.merge.helper.Helper;
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
    private final Map<String, FieldUpdateNotifier> notifiers;
    private final Boolean ignoreNullValue;
    private final Map<String, UpdateCollector> collector;

    public Merger(Map<Class, Function> customs, Map<String, FieldUpdateNotifier> notifiers, Boolean ignoreNullValue) {
        this.copierFactory = new CopierFactory(this, customs);
        this.notifiers = notifiers;
        this.ignoreNullValue = ignoreNullValue;
        this.collector = new HashMap<>();
    }

    public boolean merge(X from, Y to) {
        boolean result = merge(from, to, "");
        sendNotify();
        return result;
    }

    private void sendNotify() {
        collector.entrySet()
                .forEach(entrySet -> entrySet.getValue().getNotifier().updateNotify(entrySet.getKey(),
                        entrySet.getValue().getFrom(), entrySet.getValue().getTo()));
    }

    public boolean merge(X from, Y to, String path) {
        boolean hasChange = getFields(to.getClass())
                .stream()
                .filter(field -> isWriteAble(from, to, field))
                .filter(field -> isIgnore(from, field))
                .map(field -> updateField(to, field, path,
                        copierFactory.getCopier(field.getType()).copy(from, to, field, path)))
                .collect(Collectors.toList()).stream().anyMatch(Boolean::booleanValue);
        if (hasChange && notifiers.containsKey(path)) {
            collector.put(path, new UpdateCollector(to, from, notifiers.get(path)));
        }
        return hasChange;
    }

    private boolean updateField(Y to, Field field, String path, Object value) {
        try {
            Object originValue = getNestedProperty(to, field);
            BeanUtils.setProperty(to, field.getName(), value);
            if (!value.equals(originValue)) {
                String fieldPath = Helper.getPath(path, field.getName());
                if (notifiers.containsKey(fieldPath)) {
                    collector.put(fieldPath, new UpdateCollector(originValue, value, notifiers.get(fieldPath)));
                }
                return true;
            }
            return false;
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new MergeException();
        }
    }

    private Object getNestedProperty(Object to, Field field) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return BeanUtilsBean.getInstance().getPropertyUtils().getNestedProperty(to, field.getName());
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
