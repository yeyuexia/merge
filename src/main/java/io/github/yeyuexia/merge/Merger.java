package io.github.yeyuexia.merge;

import io.github.yeyuexia.merge.copier.CopierFactory;
import io.github.yeyuexia.merge.copier.CustomerCopierAdapter;
import io.github.yeyuexia.merge.exception.MergeException;
import io.github.yeyuexia.merge.helper.Helper;
import io.github.yeyuexia.merge.notifier.NotifierManager;
import io.github.yeyuexia.merge.notifier.UpdatedNotifier;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Merger<From, To> {

  private static final Logger LOG = LoggerFactory.getLogger(Merger.class);
  private final CopierFactory copierFactory;
  private final Boolean ignoreNullValue;
  private final NotifierManager notifierManager;
  private final Set<Class> customImmutableTypes;
  private final From source;
  private final To target;

  public Merger(Map<Class, Set<CustomerCopierAdapter>> customs, List<UpdatedNotifier> notifiers,
      Boolean ignoreNullValue, Set<Class> customImmutableTypes, From source, To target) {
    this.copierFactory = new CopierFactory(this, customs);
    this.ignoreNullValue = ignoreNullValue;
    this.customImmutableTypes = customImmutableTypes;
    this.notifierManager = new NotifierManager(notifiers);
    this.source = source;
    this.target = target;
  }

  public boolean merge() {
    boolean result = merge(source, target, "");
    sendNotify();
    return result;
  }

  public <Source, Target> boolean merge(Source from, Target to, String path) {
    boolean hasChange = getFields(to.getClass())
        .stream()
        .filter(field -> isWriteAble(from, to, field))
        .filter(field -> isIgnore(from, field))
        .map(field -> updateField(from, to, field, path))
        .collect(Collectors.toList()).stream().anyMatch(Boolean::booleanValue);
    if (hasChange) {
      notifierManager.addUpdatedPath(path, from, to);
    }
    return hasChange;
  }

  public Set<Class> getCustomImmutableTypes() {
    return customImmutableTypes;
  }

  private void sendNotify() {
    notifierManager.notifyUpdate(source, target);
  }

  private <Source, Target> boolean updateField(Source from, Target to, Field field, String path) {
    try {
      Object fromValue = PropertyUtils.getSimpleProperty(from, field.getName());
      Object toValue = copierFactory.getCopier(field.getType(), fromValue).copy(from, to, field, path);
      return updateField(to, field, path, toValue);
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new MergeException();
    }
  }

  private <Target> boolean updateField(Target to, Field field, String path, Object value) {
    try {
      Object originValue = getSimpleProperty(to, field);
      BeanUtils.setProperty(to, field.getName(), value);
      if (!Objects.equals(value, originValue)) {
        String fieldPath = Helper.getPath(path, field.getName());
        notifierManager.addUpdatedPath(fieldPath, originValue, value);
        return true;
      }
      return false;
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new MergeException();
    }
  }

  private List<Field> getFields(Class from) {
    if (from == null || from == Object.class) {
      return new ArrayList<>();
    }
    List<Field> fields = new ArrayList<>(Arrays.asList(from.getDeclaredFields()));
    final Set<String> names = fields.stream().map(Field::getName).collect(Collectors.toSet());
    fields.addAll(getFields(from.getSuperclass()).stream()
        .filter(field -> !field.isSynthetic())
        .filter(field -> !names.contains(field.getName()))
        .collect(Collectors.toList()));
    return fields;
  }

  private <Source, Target> boolean isWriteAble(Source from, Target to, Field field) {
    return PropertyUtils.isWriteable(to, field.getName()) && PropertyUtils.isReadable(from, field.getName());
  }

  private <Source> boolean isIgnore(Source from, Field field) {
    try {
      return !(ignoreNullValue && BeanUtils.getProperty(from, field.getName()) == null);
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      LOG.error("Get property error: {}", e);
      throw new MergeException();
    }
  }

  private Object getSimpleProperty(Object to, Field field) {
    try {
      return PropertyUtils.getSimpleProperty(to, field.getName());
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new MergeException();
    }
  }
}
