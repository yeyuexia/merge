package io.github.yeyuexia.merge.copier;

import io.github.yeyuexia.merge.Merger;
import io.github.yeyuexia.merge.copier.impl.CustomCopier;
import io.github.yeyuexia.merge.copier.impl.DeepCopyCopier;

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
        return customs.containsKey(type) ? new CustomCopier(customs.get(type)) : new DeepCopyCopier(merger);
    }
}
