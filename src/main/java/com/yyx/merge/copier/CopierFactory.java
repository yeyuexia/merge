package com.yyx.merge.copier;

import com.yyx.merge.Merger;
import com.yyx.merge.copier.impl.CustomCopier;
import com.yyx.merge.copier.impl.DeepCopyCopier;

import java.util.Map;
import java.util.function.Function;

public class CopierFactory {
    private final Merger merger;

    public CopierFactory(Merger merger) {this.merger = merger;}

    public Copier getCopier(Class<?> type, Map<Class, Function> customs) {
        return customs.containsKey(type) ? new CustomCopier(customs.get(type), merger) : new DeepCopyCopier(merger);
    }
}
