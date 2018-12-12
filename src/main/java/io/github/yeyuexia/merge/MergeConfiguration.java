package io.github.yeyuexia.merge;

import io.github.yeyuexia.merge.function.FieldUpdateNotifier;
import io.github.yeyuexia.merge.function.GlobalUpdateNotifier;
import io.github.yeyuexia.merge.function.OrdinaryFieldUpdateNotifier;
import io.github.yeyuexia.merge.function.OrdinaryGlobalUpdateNotifier;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MergeConfiguration<Target, Source> {

  private final Map<Class, Function> customs;
  private final Map<String, FieldUpdateNotifier<Target, Source>> notifiers;
  private Boolean ignoreNullValue;

  public MergeConfiguration() {
    this.customs = new HashMap<>();
    this.notifiers = new HashMap<>();
    this.ignoreNullValue = true;
  }

  public MergeConfiguration ignoreNullValue(Boolean ignoreNullValue) {
    this.ignoreNullValue = ignoreNullValue;
    return this;
  }

  public <F, T> MergeConfiguration custom(Class<T> specialClass, Function<F, T> generator) {
    this.customs.put(specialClass, generator);
    return this;
  }

  public MergeConfiguration notifyUpdate(GlobalUpdateNotifier notifier) {
    this.notifiers.put("", (fieldName, target, source, oldValue, newValue) -> notifier.updateNotify(target, source));
    return this;
  }

  public MergeConfiguration notifyUpdate(OrdinaryGlobalUpdateNotifier notifier) {
    this.notifiers.put("", (fieldName, target, source, oldValue, newValue) -> notifier.updateNotify());
    return this;
  }

  public MergeConfiguration notifyUpdate(String path, FieldUpdateNotifier notifier) {
    this.notifiers.put(path, (fieldName, target, source, oldValue, newValue) -> notifier.updateNotify(fieldName, target, source, oldValue, newValue));
    return this;
  }

  public MergeConfiguration notifyUpdate(String path, OrdinaryFieldUpdateNotifier notifier) {
    this.notifiers.put(path, (fieldName, target, source, oldValue, newValue) -> notifier.updateNotify(fieldName, oldValue, newValue));
    return this;
  }

  public Map<Class, Function> getCustoms() {
    return customs;
  }

  public Map<String, FieldUpdateNotifier<Target, Source>> getNotifiers() {
    return notifiers;
  }

  public Boolean getIgnoreNullValue() {
    return ignoreNullValue;
  }
}
