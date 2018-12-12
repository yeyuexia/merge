package io.github.yeyuexia.merge;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.exmertec.dummie.Dummie;
import com.exmertec.dummie.configuration.GenerationStrategy;
import io.github.yeyuexia.merge.base.data.ObjectWithCustomFieldA;
import io.github.yeyuexia.merge.base.data.ObjectWithCustomFieldB;
import io.github.yeyuexia.merge.base.data.SimpleObjectA;
import io.github.yeyuexia.merge.base.data.SimpleObjectB;
import io.github.yeyuexia.merge.base.data.SubObject;
import org.junit.Test;

public class NotifyTest {

  @Test
  public void should_success_notify_change_when_field_value_updated() throws Exception {
    ObjectWithCustomFieldA from = Dummie.withStrategy(GenerationStrategy.RANDOM)
        .create(ObjectWithCustomFieldA.class);
    ObjectWithCustomFieldB to = new ObjectWithCustomFieldB();
    SubObject mock = mock(SubObject.class);

    Merge.withConfiguration(new MergeConfiguration()
        .notifyUpdate("customFieldA.scalarTypeFloat", (name, f, t) -> mock.setSubObjectIntegerField(1)))
        .merge(from, to);

    verify(mock, times(1)).setSubObjectIntegerField(1);
  }

  @Test
  public void should_not_notify_change_if_use_wrong_path() throws Exception {
    ObjectWithCustomFieldA from = Dummie.withStrategy(GenerationStrategy.RANDOM)
        .create(ObjectWithCustomFieldA.class);
    ObjectWithCustomFieldB to = new ObjectWithCustomFieldB();
    SubObject mock = mock(SubObject.class);

    Merge.withConfiguration(new MergeConfiguration()
        .notifyUpdate("customFieldA.scalarType", (name, f, t) -> mock.setSubObjectIntegerField(1)))
        .merge(from, to);

    verify(mock, times(0)).setSubObjectIntegerField(1);
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

    verify(mock, times(0)).setSubObjectIntegerField(1);
  }

  @Test
  public void should_success_notify_global_change_when_field_value_updated() throws Exception {
    ObjectWithCustomFieldA from = Dummie.withStrategy(GenerationStrategy.RANDOM)
        .create(ObjectWithCustomFieldA.class);
    ObjectWithCustomFieldB to = new ObjectWithCustomFieldB();
    SubObject mock = mock(SubObject.class);

    Merge.withConfiguration(new MergeConfiguration().notifyUpdate(() -> mock.setSubObjectStringField("1")))
        .merge(from, to);

    verify(mock, times(1)).setSubObjectStringField("1");
  }

  @Test
  public void should_success_notify_global_change_with_target_and_source_when_field_value_updated() {
    SimpleObjectA from = Dummie.withStrategy(GenerationStrategy.RANDOM)
        .create(SimpleObjectA.class);
    SimpleObjectB to = new SimpleObjectB();
    String objectTypeString = "123 321";

    Merge.withConfiguration(new MergeConfiguration().notifyUpdate((target, source) -> from.setObjectTypeString(objectTypeString)))
        .merge(from, to);

    assertEquals(objectTypeString, from.getObjectTypeString());
  }
}
