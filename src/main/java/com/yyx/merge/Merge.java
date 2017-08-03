package com.yyx.merge;

public class Merge {
    public static <X, Y> boolean merge(X from, Y to) {
        return merge(from, to, true);
    }

    public static <X, Y> boolean merge(X from, Y to, boolean ignoreNullValue) {
        return new Merger(from.getClass(), to.getClass(), ignoreNullValue).merge(from, to);
    }
}
