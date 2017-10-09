package com.yyx.merge;

import com.yyx.merge.function.FieldUpdateNotifier;
import com.yyx.merge.function.GlobalUpdateNotifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MergeConfiguration {
    private final Map<Class, Function> customs;
    private final Map<String, FieldUpdateNotifier> notifiers;
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
        this.notifiers.put("", (fieldName, oldValue, newValue) -> notifier.updateNotify());
        return this;
    }

    public MergeConfiguration notifyUpdate(String path, FieldUpdateNotifier notifier) {
        this.notifiers.put(path, notifier);
        return this;
    }

    public Map<Class, Function> getCustoms() {
        return customs;
    }

    public Map<String, FieldUpdateNotifier> getNotifiers() {
        return notifiers;
    }

    public Boolean getIgnoreNullValue() {
        return ignoreNullValue;
    }
}
