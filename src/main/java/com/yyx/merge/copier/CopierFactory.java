package com.yyx.merge.copier;

import com.yyx.merge.Merger;
import com.yyx.merge.copier.impl.CustomCopier;
import com.yyx.merge.copier.impl.DeepCopyCopier;

import java.util.Map;
import java.util.function.Function;

public class CopierFactory {
    private final Merger merger;
    private final Map<Class, Function> customs;

    public CopierFactory(Merger merger, Map<Class, Function> customs) {
        this.merger = merger;
        this.customs = customs;
    }

    public Copier getCopier(Class<?> type) {
        return customs.containsKey(type) ? new CustomCopier(customs.get(type), merger) : new DeepCopyCopier(merger);
    }
}
