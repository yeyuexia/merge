package io.github.yeyuexia.merge.copier.impl;

import io.github.yeyuexia.merge.copier.Copier;
import io.github.yeyuexia.merge.copier.CustomerCopierAdapter;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomCopier extends Copier {

  private static final Logger LOG = LoggerFactory.getLogger(CustomCopier.class);

  private final Set<CustomerCopierAdapter> adapters;

  public CustomCopier(Set<CustomerCopierAdapter> adapters) {
    this.adapters = adapters;
  }

  @Override
  public Object copy(Field field, Object fromValue, Object toValue, String path) {
    Function function = adapters.stream()
        .filter(adapter -> adapter.getSourceClass() != Object.class)
        .filter(adapter -> adapter.match(fromValue))
        .findFirst()
        .orElseGet(this::getDefaultAdapter)
        .getCustomerCopier();
    return function.apply(fromValue);
  }

  private CustomerCopierAdapter getDefaultAdapter() {
    return adapters.stream().filter(adapter -> adapter.getSourceClass() == Object.class).findFirst().get();
  }
}
