package com.yyx.merge.function;

public interface FieldUpdateNotifier {
    void updateNotify(String fieldName, Object oldValue, Object newValue);
}
