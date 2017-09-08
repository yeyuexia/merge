package com.yyx.merge.copier;

import com.yyx.merge.Merger;

import java.lang.reflect.Field;

public abstract class Copier<X, Y> {
    protected final Merger merger;

    public Copier(Merger merger) {this.merger = merger;}

    public abstract void copy(X from, Y to, Field field, String path);
}
