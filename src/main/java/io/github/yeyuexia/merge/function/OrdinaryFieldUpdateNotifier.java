package io.github.yeyuexia.merge.function;

public interface OrdinaryFieldUpdateNotifier {

  void updateNotify(String fieldName, Object oldValue, Object newValue);
}
