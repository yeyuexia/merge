package io.github.yeyuexia.merge.notifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NotifierManager<Source, Target> {
  private final Set<String> paths;
  private final List<UpdatedNotifier> pendingNotifiers;
  private final List<UpdatedField> updatedFields;

  public NotifierManager(List<UpdatedNotifier> notifiers) {
    this.pendingNotifiers = notifiers;
    this.paths = notifiers.stream()
        .flatMap(notifier -> notifier.getPaths().stream())
        .collect(Collectors.toSet());
    this.updatedFields = new ArrayList<>();
  }

  public void addUpdatedPath(String updatedPath, Object from, Object to) {
    if (paths.contains(updatedPath)) {
      this.updatedFields.add(new UpdatedField(updatedPath, from, to));
    }
  }

  public <Source, Target> void notifyUpdate(Source source, Target target) {
    Set<String> updatedPaths = this.updatedFields.stream().map(UpdatedField::getPath).collect(Collectors.toSet());
    this.pendingNotifiers.stream()
        .filter(notify -> notify.shouldNotify(updatedPaths))
        .forEach(notify -> {
          List<UpdatedField> fields = this.updatedFields.stream().filter(field -> notify.getPaths().contains(field.getPath()))
              .collect(Collectors.toList());
          notify.getNotifier().updateNotify(source, target, fields);
        });
  }
}
