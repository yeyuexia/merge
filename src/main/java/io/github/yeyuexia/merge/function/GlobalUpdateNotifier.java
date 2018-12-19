package io.github.yeyuexia.merge.function;

public interface GlobalUpdateNotifier<Source, Target> {

  void updateNotify(Source source, Target target);
}
