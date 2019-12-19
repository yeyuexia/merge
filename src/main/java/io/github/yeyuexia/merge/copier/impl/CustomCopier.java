package io.github.yeyuexia.merge.copier.impl;

import io.github.yeyuexia.merge.copier.Copier;
import io.github.yeyuexia.merge.copier.CustomerCopierAdapter;
import io.github.yeyuexia.merge.exception.MergeException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.function.Function;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomCopier extends Copier {

  private static final Logger LOG = LoggerFactory.getLogger(CustomCopier.class);

  private final Set<CustomerCopierAdapter> adapters;

  public CustomCopier(Set<CustomerCopierAdapter> adapters) {
    this.adapters = adapters;
  }

  @Override
  public Object copy(Object from, Object to, Field field, String path) {
    try {
      Object value = PropertyUtils.getSimpleProperty(from, field.getName());
      Function function = adapters.stream()
          .filter(adapter -> adapter.getSourceClass() != Object.class)
          .filter(adapter -> adapter.match(value))
          .findFirst()
          .orElseGet(this::getDefaultAdapter)
          .getCustomerCopier();
      return function.apply(value);
    } catch (IllegalAccessException e) {
      LOG.error("Init field bean error: {}", e);
      throw new MergeException();
    } catch (NoSuchMethodException | InvocationTargetException e) {
      LOG.error("Get property error: {}", e);
      throw new MergeException();
    }
  }

  private CustomerCopierAdapter getDefaultAdapter() {
    return adapters.stream().filter(adapter -> adapter.getSourceClass() == Object.class).findFirst().get();
  }
}
