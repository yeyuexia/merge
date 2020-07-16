package io.github.yeyuexia.merge.copier.impl;

import io.github.yeyuexia.merge.Merger;
import io.github.yeyuexia.merge.copier.Copier;
import io.github.yeyuexia.merge.exception.MergeException;
import io.github.yeyuexia.merge.helper.Helper;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeepCopyCopier extends Copier {

  private static final Logger LOG = LoggerFactory.getLogger(DeepCopyCopier.class);

  private static final Set<Class<?>> WRAPPER_TYPES = getBasicTypes();

  private static final Set<Class<?>> IMMUTABLE_TYPES = getStandardImmutableTypes();

  private final Merger merger;

  public DeepCopyCopier(Merger merger) {
    this.merger = merger;
  }

  private static boolean isWrapperType(Class<?> clazz) {
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

  private static Set<Class<?>> getStandardImmutableTypes() {
    Set<Class<?>> ret = new HashSet<>();
    ret.add(ZonedDateTime.class);
    ret.add(LocalDateTime.class);
    ret.add(OffsetDateTime.class);
    ret.add(BigDecimal.class);
    return ret;
  }

  @Override
  public Object copy(Field field, Object fromValue, Object toValue, String path) {
    try {
      return isImmutableType(field.getType()) ? fromValue : getObjectValue(field, fromValue, toValue, path);
    } catch (InstantiationException | IllegalAccessException e) {
      LOG.error("Init field bean error: {}", e);
      throw new MergeException(e);
    }
  }

  private boolean isImmutableType(Class type) {
    return type.isEnum() || type.isPrimitive() || isWrapperType(type)
        || IMMUTABLE_TYPES.contains(type) || merger.getCustomImmutableTypes().contains(type);
  }

  private Object getObjectValue(Field field, Object fromValue, Object toValue, String path) throws IllegalAccessException, InstantiationException {
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
      String path) throws InstantiationException, IllegalAccessException {
    if (isImmutableType(type)) {
      Object[] objects = toValue.toArray();
      toValue.clear();
      toValue.addAll(fromValue);
      if (fromValue.size() < objects.length) {
        for (int i = fromValue.size(); i < objects.length; i++) {
          toValue.add(objects[i]);
        }
      }
    } else {
      int index = 0;
      Iterator toIterator = toValue.iterator();
      Iterator fromIterator = fromValue.iterator();
      while (toIterator.hasNext()) {
        index++;
        Object to = toIterator.next();
        if (fromIterator.hasNext()) {
          Object from = fromIterator.next();
          merger.merge(from, to, Helper.getCollectionPath(path,
              toValue.getClass().isInstance(List.class) ? String.valueOf(index) : "?"));
        }
      }
      while (fromIterator.hasNext()) {
        index++;
        Object from = fromIterator.next();
        Object to = generateInstance(from, type);
        toValue.add(to);
        merger.merge(from, to, Helper.getCollectionPath(path,
            toValue.getClass().isInstance(List.class) ? String.valueOf(index) : "?"));
      }
    }
  }

  private Object generateInstance(Object fromValue, Class type) throws InstantiationException, IllegalAccessException {
    if (fromValue != null && type.isInstance(fromValue)) {
      return fromValue.getClass().newInstance();
    } else {
      return type.newInstance();
    }
  }
}
