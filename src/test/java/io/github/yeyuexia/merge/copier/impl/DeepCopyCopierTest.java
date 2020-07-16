package io.github.yeyuexia.merge.copier.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.google.common.collect.Lists;
import io.github.dummiejava.dummie.Dummie;
import io.github.dummiejava.dummie.configuration.GenerationStrategy;
import io.github.yeyuexia.merge.Merger;
import io.github.yeyuexia.merge.base.data.BaseObject;
import io.github.yeyuexia.merge.base.data.ListObject;
import io.github.yeyuexia.merge.base.data.ObjectWithCustomFieldA;
import io.github.yeyuexia.merge.base.data.ObjectWithCustomFieldB;
import io.github.yeyuexia.merge.base.data.SimpleListObject;
import io.github.yeyuexia.merge.base.data.SimpleObjectA;
import io.github.yeyuexia.merge.base.data.SimpleObjectB;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DeepCopyCopierTest {

  @Test
  public void should_return_the_from_value_if_the_field_is_scalar_type() throws Exception {
    SimpleObjectA from = Dummie.withStrategy(GenerationStrategy.RANDOM).create(SimpleObjectA.class);
    SimpleObjectB to = new SimpleObjectB();
    DeepCopyCopier copier = new DeepCopyCopier(null);

    Object result = copier.copy(to.getClass().getDeclaredField("scalarTypeInt"), from.getScalarTypeInt(), to.getScalarTypeInt(), "");

    assertEquals(result, from.getScalarTypeInt());
  }

  @Test
  public void should_call_merger_to_get_value_if_the_field_is_high_level_type() throws Exception {
    ObjectWithCustomFieldA from = Dummie.withStrategy(GenerationStrategy.RANDOM).create(ObjectWithCustomFieldA.class);
    ObjectWithCustomFieldB to = new ObjectWithCustomFieldB();
    Merger merger = mock(Merger.class);
    DeepCopyCopier copier = new DeepCopyCopier(merger);

    copier.copy(to.getClass().getDeclaredField("customFieldA"), from.getCustomFieldA(), to.getCustomFieldA(), "");

    verify(merger, times(1)).merge(any(SimpleObjectA.class), any(SimpleObjectB.class), anyString());
  }

  @Test
  public void should_use_same_object_instance_when_to_object_has_value_for_non_scalar_type() throws Exception {
    ObjectWithCustomFieldA from = Dummie.withStrategy(GenerationStrategy.RANDOM).create(ObjectWithCustomFieldA.class);
    ObjectWithCustomFieldB to = new ObjectWithCustomFieldB();
    to.setCustomFieldA(new SimpleObjectA());
    Merger merger = mock(Merger.class);
    DeepCopyCopier copier = new DeepCopyCopier(merger);

    copier.copy(to.getClass().getDeclaredField("customFieldA"), from.getCustomFieldA(), to.getCustomFieldA(), "");

    verify(merger, times(1)).merge(from.getCustomFieldA(), to.getCustomFieldA(), "customFieldA");
  }

  @Test
  public void should_success_merge_list_when_from_length_larger_than_to_length() throws Exception {
    ListObject from = new ListObject();
    from.setObjects(IntStream.range(0, 5)
        .mapToObj(i -> Dummie.withStrategy(GenerationStrategy.RANDOM).create(BaseObject.class))
        .collect(Collectors.toList()));
    ListObject to = new ListObject();
    to.setObjects(Lists.newArrayList());
    Merger merger = mock(Merger.class);
    DeepCopyCopier copier = new DeepCopyCopier(merger);

    copier.copy(to.getClass().getDeclaredField("objects"), from.getObjects(), to.getObjects(), "");

    assertEquals(from.getObjects().size(), to.getObjects().size());
  }

  @Test
  public void should_update_first_two_element_when_from_list_only_has_two_element() throws Exception {
    SimpleListObject from = new SimpleListObject();
    from.setNumbers(Arrays.asList(1, 2));
    SimpleListObject to = new SimpleListObject();
    to.setNumbers(Lists.newArrayList(3, 4, 5));

    Merger merger = mock(Merger.class);
    DeepCopyCopier copier = new DeepCopyCopier(merger);

    copier.copy(to.getClass().getDeclaredField("numbers"), from.getNumbers(), to.getNumbers(), "");

    assertEquals(3, to.getNumbers().size());
    assertEquals(from.getNumbers().get(0), to.getNumbers().get(0));
    assertEquals(from.getNumbers().get(1), to.getNumbers().get(1));
  }
}