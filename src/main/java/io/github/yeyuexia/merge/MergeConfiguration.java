package io.github.yeyuexia.merge;

import io.github.yeyuexia.merge.copier.CustomerCopierAdapter;
import io.github.yeyuexia.merge.notifier.function.FieldUpdateNotifier;
import io.github.yeyuexia.merge.notifier.function.SingleFieldUpdateNotifier;
import io.github.yeyuexia.merge.notifier.function.GlobalUpdateNotifier;
import io.github.yeyuexia.merge.notifier.function.OrdinaryFieldUpdateNotifier;
import io.github.yeyuexia.merge.notifier.function.OrdinaryGlobalUpdateNotifier;
import io.github.yeyuexia.merge.notifier.UpdatedNotifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MergeConfiguration<Source, Target> {

  private final Map<Class, Set<CustomerCopierAdapter>> customs;
  private final List<UpdatedNotifier> notifiers;
  private final Set<Class> customImmutableTypes;
  private Boolean ignoreNullValue;

  public MergeConfiguration() {
    this.customs = new HashMap<>();
    this.notifiers = new ArrayList<>();
    this.customImmutableTypes = new HashSet<>();
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
    this.notifiers.add(new UpdatedNotifier(Stream.of("").collect(Collectors.toSet()), notifier));
    return this;
  }

  public MergeConfiguration<Source, Target> notifyUpdate(OrdinaryGlobalUpdateNotifier<Source, Target> notifier) {
    this.notifiers.add(new UpdatedNotifier(Stream.of("").collect(Collectors.toSet()), notifier));
    return this;
  }

  public MergeConfiguration<Source, Target> notifyUpdate(String path, SingleFieldUpdateNotifier<Source, Target> notifier) {
    this.notifiers.add(new UpdatedNotifier(Stream.of(path).collect(Collectors.toSet()), notifier));
    return this;
  }

  public MergeConfiguration<Source, Target> notifyUpdate(String path, OrdinaryFieldUpdateNotifier<Source, Target> notifier) {
    this.notifiers.add(new UpdatedNotifier(Stream.of(path).collect(Collectors.toSet()), notifier));
    return this;
  }

  public MergeConfiguration<Source, Target> notifyUpdate(List<String> paths, FieldUpdateNotifier<Source, Target> notifier) {
    this.notifiers.add(new UpdatedNotifier(paths.stream().collect(Collectors.toSet()), notifier));
    return this;
  }

  public MergeConfiguration<Source, Target> immutableTypes(Class... immutableClasses) {
    this.customImmutableTypes.addAll(Arrays.asList(immutableClasses));
    return this;
  }

  public MergeConfiguration<Source, Target> immutableTypes(Collection<Class> immutableClasses) {
    this.customImmutableTypes.addAll(immutableClasses);
    return this;
  }

  public Map<Class, Set<CustomerCopierAdapter>> getCustoms() {
    return customs;
  }

  public List<UpdatedNotifier> getNotifiers() {
    return notifiers;
  }

  public Boolean getIgnoreNullValue() {
    return ignoreNullValue;
  }

  public Set<Class> getCustomImmutableTypes() {
    return customImmutableTypes;
  }
}
