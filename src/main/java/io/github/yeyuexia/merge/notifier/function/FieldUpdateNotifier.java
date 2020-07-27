package io.github.yeyuexia.merge.notifier.function;

import io.github.yeyuexia.merge.notifier.UpdatedField;
import java.util.List;

public interface FieldUpdateNotifier<Source, Target> {

  void updateNotify(Source source, Target target, List<UpdatedField> updatedFields);
}
