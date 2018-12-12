package io.github.yeyuexia.merge.copier;

import java.lang.reflect.Field;

public abstract class Copier<X, Y> {

  public abstract Object copy(X from, Y to, Field field, String path);
}
