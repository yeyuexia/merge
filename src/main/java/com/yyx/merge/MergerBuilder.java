package com.yyx.merge;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class MergerBuilder {
    private final Map<Class, Function> customs;
    private Boolean ignoreNullValue;

    public MergerBuilder() {
        this.customs = new HashMap<>();
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

    public Merger build() {
        return new Merger(customs, ignoreNullValue);
    }
}
