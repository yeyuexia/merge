package io.github.yeyuexia.merge.function;

public interface GlobalUpdateNotifier<Target, Source> {

  void updateNotify(Target target, Source source);
}
