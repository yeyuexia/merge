package io.github.yeyuexia.merge.copier;

import io.github.yeyuexia.merge.Merger;
import io.github.yeyuexia.merge.copier.impl.CustomCopier;
import io.github.yeyuexia.merge.copier.impl.DeepCopyCopier;
import java.util.Map;
import java.util.Set;

public class CopierFactory {

  private final Merger merger;
  private final Map<Class, Set<CustomerCopierAdapter>> customs;

  public CopierFactory(Merger merger, Map<Class, Set<CustomerCopierAdapter>> customs) {
    this.merger = merger;
    this.customs = customs;
  }

  public Copier getCopier(Class<?> target, Object source) {
    return hasCustomCopier(target, source) ? new CustomCopier(customs.get(target)) : new DeepCopyCopier(merger);
  }

  private boolean hasCustomCopier(Class<?> target, Object source) {
    return customs.containsKey(target) && customs.get(target).stream().filter(adapter -> adapter.match(source)).findFirst().isPresent();
  }
}
