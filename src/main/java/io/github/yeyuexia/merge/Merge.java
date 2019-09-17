package io.github.yeyuexia.merge;

import io.github.yeyuexia.merge.helper.MergerHelper;

public class Merge {

  public static <From, To> boolean merge(From from, To to) {
    return merge(from, to, true);
  }

  public static <From, To> boolean merge(From from, To to, boolean ignoreNullValue) {
    return new MergerHelper(new MergeConfiguration<From, To>().ignoreNullValue(ignoreNullValue)).merge(from, to);
  }

  public static <From, To> MergerHelper withConfiguration(MergeConfiguration<From, To> configuration) {
    return new MergerHelper(configuration);
  }
}
