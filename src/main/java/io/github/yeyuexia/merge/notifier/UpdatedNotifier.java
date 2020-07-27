package io.github.yeyuexia.merge.notifier;

import io.github.yeyuexia.merge.notifier.function.FieldUpdateNotifier;
import java.util.Set;

public class UpdatedNotifier {

  private final Set<String> paths;
  private final FieldUpdateNotifier notifier;

  public UpdatedNotifier(Set<String> paths, FieldUpdateNotifier notifier) {
    this.paths = paths;
    this.notifier = notifier;
  }

  public boolean shouldNotify(Set<String> updatedPaths) {
    return updatedPaths.containsAll(paths);
  }

  public Set<String> getPaths() {
    return paths;
  }

  public FieldUpdateNotifier getNotifier() {
    return notifier;
  }
}
