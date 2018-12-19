package io.github.yeyuexia.merge.function;

public interface FieldUpdateNotifier<Source, Target> {

  void updateNotify(String fieldName, Source source, Target target, Object oldValue, Object newValue);
}
