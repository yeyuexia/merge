package io.github.yeyuexia.merge;

import io.github.yeyuexia.merge.helper.MergerBuilder;

public class Merge {

  public static <From, To> boolean merge(From from, To to) {
    return merge(from, to, true);
  }

  public static <From, To> boolean merge(From from, To to, boolean ignoreNullValue) {
    return new MergerBuilder(new MergeConfiguration<From, To>().ignoreNullValue(ignoreNullValue)).build().merge(from, to);
  }

  public static <From, To> Merger<From, To> withConfiguration(MergeConfiguration<From, To> configuration) {
    return new MergerBuilder(configuration).build();
  }
}
