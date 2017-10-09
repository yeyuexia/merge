package com.yyx.merge;

import com.yyx.merge.helper.MergerBuilder;

public class Merge {
    public static <X, Y> boolean merge(X from, Y to) {
        return merge(from, to, true);
    }

    public static <X, Y> boolean merge(X from, Y to, boolean ignoreNullValue) {
        return new MergerBuilder(new MergeConfiguration().ignoreNullValue(ignoreNullValue)).build().merge(from, to);
    }

    public static Merger withConfiguration(MergeConfiguration configuration) {
        return new MergerBuilder(configuration).build();
    }
}
