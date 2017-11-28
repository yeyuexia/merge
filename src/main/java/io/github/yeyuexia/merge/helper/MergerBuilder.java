package io.github.yeyuexia.merge.helper;

import io.github.yeyuexia.merge.MergeConfiguration;
import io.github.yeyuexia.merge.Merger;

public final class MergerBuilder {
    private final MergeConfiguration configuration;

    public MergerBuilder(MergeConfiguration configuration) {
        this.configuration = configuration;
    }

    public Merger build() {
        return new Merger(configuration.getCustoms(), configuration.getNotifiers(), configuration.getIgnoreNullValue());
    }
}
