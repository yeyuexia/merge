package io.github.yeyuexia.merge.function;

public interface FieldUpdateNotifier<Target, Source> {

  void updateNotify(String fieldName, Target target, Source source, Object oldValue, Object newValue);
}
