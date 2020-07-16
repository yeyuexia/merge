package io.github.yeyuexia.merge;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import io.github.dummiejava.dummie.Dummie;
import io.github.dummiejava.dummie.configuration.GenerationStrategy;
import io.github.yeyuexia.merge.base.data.ObjectWithCustomFieldA;
import io.github.yeyuexia.merge.base.data.ObjectWithCustomFieldB;
import io.github.yeyuexia.merge.base.data.SimpleObjectA;
import io.github.yeyuexia.merge.base.data.SimpleObjectB;
import io.github.yeyuexia.merge.base.data.SubObject;
import java.util.Arrays;
import org.junit.Test;

public class NotifyTest {

  @Test
  public void should_success_notify_change_when_field_value_updated() {
    ObjectWithCustomFieldA from = Dummie.withStrategy(GenerationStrategy.RANDOM)
        .create(ObjectWithCustomFieldA.class);
    ObjectWithCustomFieldB to = new ObjectWithCustomFieldB();
    SubObject mock = mock(SubObject.class);

    Merge.withConfiguration(new MergeConfiguration()
        .notifyUpdate("customFieldA.scalarTypeFloat", (name, f, t) -> mock.setSubObjectIntegerField(1)))
        .merge(from, to);

    verify(mock).setSubObjectIntegerField(1);
  }

  @Test
  public void should_success_notify_when_all_path_changed() {
    ObjectWithCustomFieldA from = Dummie.withStrategy(GenerationStrategy.RANDOM)
        .create(ObjectWithCustomFieldA.class);
    ObjectWithCustomFieldB to = new ObjectWithCustomFieldB();
    SubObject mock = mock(SubObject.class);

    Merge.withConfiguration(new MergeConfiguration()
        .notifyUpdate(Arrays.asList("customFieldA.scalarTypeFloat", "customFieldA.scalarTypeDouble"),
            (source, target, updatedFields) -> mock.setSubObjectIntegerField(1)))
        .merge(from, to);

    verify(mock).setSubObjectIntegerField(1);
  }

  @Test
  public void should_not_notify_when_not_all_path_changed() {
    ObjectWithCustomFieldA from = Dummie.withStrategy(GenerationStrategy.RANDOM)
        .create(ObjectWithCustomFieldA.class);
    ObjectWithCustomFieldB to = Dummie.create(ObjectWithCustomFieldB.class);
    from.getCustomFieldA().setScalarTypeDouble(to.getCustomFieldA().getScalarTypeDouble());
    SubObject mock = mock(SubObject.class);

    Merge.withConfiguration(new MergeConfiguration()
        .notifyUpdate(Arrays.asList("customFieldA.scalarTypeFloat", "customFieldA.scalarTypeDouble"),
            (name, f, t) -> mock.setSubObjectIntegerField(1)))
        .merge(from, to);

    verify(mock, never()).setSubObjectIntegerField(1);
  }

  @Test
  public void should_not_notify_change_if_use_wrong_path() {
    ObjectWithCustomFieldA from = Dummie.withStrategy(GenerationStrategy.RANDOM)
        .create(ObjectWithCustomFieldA.class);
    ObjectWithCustomFieldB to = new ObjectWithCustomFieldB();
    SubObject mock = mock(SubObject.class);

    Merge.withConfiguration(new MergeConfiguration()
        .notifyUpdate("customFieldA.scalarType", (name, f, t) -> mock.setSubObjectIntegerField(1)))
        .merge(from, to);

    verify(mock, never()).setSubObjectIntegerField(1);
  }

  @Test
  public void should_not_notify_change_if_field_value_not_change() throws Exception {
    ObjectWithCustomFieldA from = Dummie.withStrategy(GenerationStrategy.RANDOM)
        .create(ObjectWithCustomFieldA.class);
    ObjectWithCustomFieldB to = new ObjectWithCustomFieldB();
    to.setCustomFieldA(new SimpleObjectA());
    to.getCustomFieldA().setScalarTypeFloat(from.getCustomFieldA().getScalarTypeFloat());
    SubObject mock = mock(SubObject.class);

    Merge.withConfiguration(new MergeConfiguration()
        .notifyUpdate("customFieldA.scalarTypeFloat", (name, f, t) -> mock.setSubObjectIntegerField(1)))
        .merge(from, to);

    verify(mock, never()).setSubObjectIntegerField(1);
  }

  @Test
  public void should_success_notify_global_change_when_field_value_updated() throws Exception {
    ObjectWithCustomFieldA from = Dummie.withStrategy(GenerationStrategy.RANDOM)
        .create(ObjectWithCustomFieldA.class);
    ObjectWithCustomFieldB to = new ObjectWithCustomFieldB();
    SubObject mock = mock(SubObject.class);

    Merge.withConfiguration(new MergeConfiguration().notifyUpdate(() -> mock.setSubObjectStringField("1")))
        .merge(from, to);

    verify(mock).setSubObjectStringField("1");
  }

  @Test
  public void should_success_notify_global_change_with_target_and_source_when_field_value_updated() {
    SimpleObjectA from = Dummie.withStrategy(GenerationStrategy.RANDOM)
        .create(SimpleObjectA.class);
    SimpleObjectB to = new SimpleObjectB();
    String objectTypeString = "123 321";

    Merge.withConfiguration(new MergeConfiguration<SimpleObjectA, SimpleObjectB>().notifyUpdate(this::notifyChange))
        .merge(from, to);

    assertEquals(objectTypeString, to.getObjectTypeString());
  }

  private void notifyChange(SimpleObjectA from, SimpleObjectB to) {
    to.setObjectTypeString("123 321");
  }
}
