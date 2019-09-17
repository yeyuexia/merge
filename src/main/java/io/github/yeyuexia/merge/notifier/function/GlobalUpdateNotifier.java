package io.github.yeyuexia.merge.notifier.function;

import io.github.yeyuexia.merge.notifier.UpdatedField;
import java.util.List;

public interface GlobalUpdateNotifier<Source, Target> extends FieldUpdateNotifier<Source, Target> {

  void updateNotify(Source source, Target target);

  default void updateNotify(Source source, Target target, List<UpdatedField> updatedFields) {
    updateNotify(source, target);
  }
}
