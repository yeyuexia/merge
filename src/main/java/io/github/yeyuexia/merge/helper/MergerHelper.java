package io.github.yeyuexia.merge.helper;

import io.github.yeyuexia.merge.MergeConfiguration;
import io.github.yeyuexia.merge.Merger;

public final class MergerHelper {

  private final MergeConfiguration configuration;

  public MergerHelper(MergeConfiguration configuration) {
    this.configuration = configuration;
  }

  public <Source, Target> boolean merge(Source source, Target target) {
    return new Merger(configuration.getCustoms(), configuration.getNotifiers(),
        configuration.getIgnoreNullValue(), configuration.getCustomImmutableTypes(), source, target).merge();
  }
}
