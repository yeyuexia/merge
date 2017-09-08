package com.yyx.merge;

public class Merge {
    public static <X, Y> boolean merge(X from, Y to) {
        return merge(from, to, true);
    }

    public static <X, Y> boolean merge(X from, Y to, boolean ignoreNullValue) {
        return new MergerBuilder().ignoreNullValue(ignoreNullValue).build().merge(from, to);
    }

    public static MergerBuilder prepare() {
        return new MergerBuilder();
    }
}
