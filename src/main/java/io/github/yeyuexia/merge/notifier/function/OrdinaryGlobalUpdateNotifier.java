package io.github.yeyuexia.merge.notifier.function;

import io.github.yeyuexia.merge.notifier.UpdatedField;
import java.util.List;

public interface OrdinaryGlobalUpdateNotifier<Source, Target> extends FieldUpdateNotifier<Source, Target> {

  void updateNotify();

  default void updateNotify(Source source, Target target, List<UpdatedField> updatedFields) {
    updateNotify();
  }
}
