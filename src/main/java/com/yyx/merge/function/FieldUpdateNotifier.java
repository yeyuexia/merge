package com.yyx.merge.function;

public interface FieldUpdateNotifier {
    void notify(String oldValue, String newValue);
}
