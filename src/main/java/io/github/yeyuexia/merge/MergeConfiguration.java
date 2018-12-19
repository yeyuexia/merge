package io.github.yeyuexia.merge;

import io.github.yeyuexia.merge.copier.CustomerCopierAdapter;
import io.github.yeyuexia.merge.function.FieldUpdateNotifier;
import io.github.yeyuexia.merge.function.GlobalUpdateNotifier;
import io.github.yeyuexia.merge.function.OrdinaryFieldUpdateNotifier;
import io.github.yeyuexia.merge.function.OrdinaryGlobalUpdateNotifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class MergeConfiguration<Source, Target> {

  private final Map<Class, Set<CustomerCopierAdapter>> customs;
  private final Map<String, FieldUpdateNotifier<Source, Target>> notifiers;
  private Boolean ignoreNullValue;

  public MergeConfiguration() {
    this.customs = new HashMap<>();
    this.notifiers = new HashMap<>();
    this.ignoreNullValue = true;
  }

  public MergeConfiguration<Source, Target> ignoreNullValue(Boolean ignoreNullValue) {
    this.ignoreNullValue = ignoreNullValue;
    return this;
  }

  public <From, To> MergeConfiguration<Source, Target> custom(Class<To> specialClass, Function<From, To> generator) {
    this.customs.putIfAbsent(specialClass, new HashSet<>());
    this.customs.get(specialClass).add(new CustomerCopierAdapter(Object.class, generator));
    return this;
  }

  public <From, To> MergeConfiguration<Source, Target> custom(Class<To> specialClass, Class<From> sourceClass, Function<From, To> generator) {
    this.customs.putIfAbsent(specialClass, new HashSet<>());
    this.customs.get(specialClass).add(new CustomerCopierAdapter<>(sourceClass, generator));
    return this;
  }

  public MergeConfiguration<Source, Target> notifyUpdate(GlobalUpdateNotifier<Source, Target> notifier) {
    this.notifiers.put("", (fieldName, target, source, oldValue, newValue) -> notifier.updateNotify(target, source));
    return this;
  }

  public MergeConfiguration<Source, Target> notifyUpdate(OrdinaryGlobalUpdateNotifier notifier) {
    this.notifiers.put("", (fieldName, target, source, oldValue, newValue) -> notifier.updateNotify());
    return this;
  }

  public MergeConfiguration<Source, Target> notifyUpdate(String path, FieldUpdateNotifier<Source, Target> notifier) {
    this.notifiers.put(path, (fieldName, source, target, oldValue, newValue) -> notifier.updateNotify(fieldName, source, target, oldValue, newValue));
    return this;
  }

  public MergeConfiguration<Source, Target> notifyUpdate(String path, OrdinaryFieldUpdateNotifier notifier) {
    this.notifiers.put(path, (fieldName, source, target, oldValue, newValue) -> notifier.updateNotify(fieldName, oldValue, newValue));
    return this;
  }

  public Map<Class, Set<CustomerCopierAdapter>> getCustoms() {
    return customs;
  }

  public Map<String, FieldUpdateNotifier<Source, Target>> getNotifiers() {
    return notifiers;
  }

  public Boolean getIgnoreNullValue() {
    return ignoreNullValue;
  }
}
