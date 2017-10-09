package com.yyx.merge.helper;

import com.yyx.merge.MergeConfiguration;
import com.yyx.merge.Merger;

public final class MergerBuilder {
    private final MergeConfiguration configuration;

    public MergerBuilder(MergeConfiguration configuration) {
        this.configuration = configuration;
    }

    public Merger build() {
        return new Merger(configuration.getCustoms(), configuration.getNotifiers(), configuration.getIgnoreNullValue());
    }
}
