package com.yyx.merge;

import com.yyx.merge.function.FieldUpdateNotifier;
import com.yyx.merge.function.GlobalUpdateNotifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class MergerBuilder {
    private final Map<Class, Function> customs;
    private final Map<String, FieldUpdateNotifier> notifiers;
    private Boolean ignoreNullValue;

    public MergerBuilder() {
        this.customs = new HashMap<>();
        this.notifiers = new HashMap<>();
        this.ignoreNullValue = true;
    }

    public MergerBuilder ignoreNullValue(Boolean ignoreNullValue) {
        this.ignoreNullValue = ignoreNullValue;
        return this;
    }

    public <F, T> MergerBuilder custom(Class<T> specialClass, Function<F, T> generator) {
        this.customs.put(specialClass, generator);
        return this;
    }

    public MergerBuilder notifyChange(GlobalUpdateNotifier notifier) {
        this.notifiers.put("", (fieldName, oldValue, newValue) -> notifier.updateNotify());
        return this;
    }

    public MergerBuilder notifyChange(String path, FieldUpdateNotifier notifier) {
        this.notifiers.put(path, notifier);
        return this;
    }

    public Merger build() {
        return new Merger(customs, notifiers, ignoreNullValue);
    }
}
