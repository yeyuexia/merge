package io.github.yeyuexia.merge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;

import com.exmertec.dummie.Dummie;
import com.exmertec.dummie.configuration.GenerationStrategy;
import io.github.yeyuexia.merge.base.data.BaseObject;
import io.github.yeyuexia.merge.base.data.ImmutableFieldObject;
import io.github.yeyuexia.merge.base.data.ObjectWithCustomFieldA;
import io.github.yeyuexia.merge.base.data.ObjectWithCustomFieldB;
import io.github.yeyuexia.merge.base.data.SimpleObjectA;
import io.github.yeyuexia.merge.base.data.SimpleObjectB;
import io.github.yeyuexia.merge.base.data.SubObject;
import io.github.yeyuexia.merge.copier.impl.DeepCopyCopier;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Test;


public class MergeTest extends BaseTest {

  @Test
  public void should_success_merge_same_name_fields_which_have_getter_and_setter() throws Exception {
    SimpleObjectA from = Dummie.withStrategy(GenerationStrategy.RANDOM).create(SimpleObjectA.class);
    SimpleObjectB to = new SimpleObjectB();
    Merge.merge(from, to);
    Arrays.stream(SimpleObjectB.class.getDeclaredFields())
        .filter(field -> !field.isSynthetic())
        .map(Field::getName)
        .filter(field -> PropertyUtils.isWriteable(to, field))
        .filter(field -> PropertyUtils.isReadable(from, field))
        .forEach(field -> fieldEquals(from, to, field));
  }

  @Test
  public void should_only_merge_fields_which_has_setter() throws Exception {
    SimpleObjectA from = Dummie.withStrategy(GenerationStrategy.RANDOM).create(SimpleObjectA.class);
    SimpleObjectB to = new SimpleObjectB();
    Merge.merge(from, to);
    List<String> names = Arrays.stream(SimpleObjectB.class.getDeclaredFields())
        .filter(field -> !field.isSynthetic())
        .map(Field::getName)
        .collect(Collectors.toList());
    names.stream().filter(field -> PropertyUtils.isWriteable(to, field))
        .filter(field -> PropertyUtils.isReadable(from, field))
        .forEach(field -> fieldEquals(from, to, field));
    names.stream().filter(field -> PropertyUtils.isReadable(to, field))
        .filter(field -> !PropertyUtils.isWriteable(to, field))
        .forEach(field -> fieldNonEquals(from, to, field));
  }

  @Test
  public void should_merge_super_class_fields() throws Exception {
    BaseObject from = Dummie.withStrategy(GenerationStrategy.RANDOM).create(BaseObject.class);
    SubObject to = new SubObject();

    Merge.merge(from, to);

    Arrays.stream(BaseObject.class.getDeclaredFields()).map(Field::getName)
        .forEach(field -> fieldEquals(from, to, field));
  }

  @Test
  public void should_from_object_super_class_fields_success_merge_to_fields() throws Exception {
    SubObject from = Dummie.withStrategy(GenerationStrategy.RANDOM).create(SubObject.class);
    BaseObject to = new BaseObject();

    Merge.merge(from, to);

    Arrays.stream(BaseObject.class.getDeclaredFields())
        .filter(field -> !field.isSynthetic())
        .map(Field::getName)
        .forEach(field -> fieldEquals(from, to, field));
  }

  @Test
  public void should_merge_custom_object_type_field() throws Exception {
    ObjectWithCustomFieldA from = Dummie.withStrategy(GenerationStrategy.RANDOM)
        .create(ObjectWithCustomFieldA.class);
    ObjectWithCustomFieldB to = new ObjectWithCustomFieldB();

    Merge.merge(from, to);

    Arrays.stream(to.getCustomFieldA().getClass().getDeclaredFields())
        .filter(field -> !field.isSynthetic())
        .map(Field::getName)
        .forEach(field -> fieldEquals(from.getCustomFieldA(), to.getCustomFieldA(), field));
    Arrays.stream(to.getCustomFieldB().getClass().getDeclaredFields())
        .filter(field -> !field.isSynthetic())
        .map(Field::getName)
        .forEach(field -> fieldEquals(from.getCustomFieldB(), to.getCustomFieldB(), field));
  }

  @Test
  public void should_return_from_value_when_the_type_is_immutableType() {
    ImmutableFieldObject from = new ImmutableFieldObject();
    from.setLocalDateTime(LocalDateTime.now());
    from.setOffsetDateTime(OffsetDateTime.now());
    from.setZonedDateTime(ZonedDateTime.now());
    ImmutableFieldObject to = new ImmutableFieldObject();

    Merge.merge(from, to);

    Arrays.stream(ImmutableFieldObject.class.getDeclaredFields())
        .filter(field -> !field.isSynthetic())
        .map(Field::getName)
        .forEach(field -> fieldEquals(from, to, field));
  }

  @Test
  public void should_return_from_value_when_given_custom_immutableType() {
    ObjectWithCustomFieldA from = Dummie.withStrategy(GenerationStrategy.RANDOM)
        .create(ObjectWithCustomFieldA.class);
    ObjectWithCustomFieldB to = new ObjectWithCustomFieldB();

    Merge.withConfiguration(new MergeConfiguration<>().immutableTypes(SimpleObjectA.class)).merge(from, to);

    assertEquals(from.getCustomFieldA(), to.getCustomFieldA());
    assertNotEquals(from.getCustomFieldB(), to.getCustomFieldB());
    Arrays.stream(to.getCustomFieldB().getClass().getDeclaredFields())
        .filter(field -> !field.isSynthetic())
        .map(Field::getName)
        .forEach(field -> fieldEquals(from.getCustomFieldB(), to.getCustomFieldB(), field));
  }
}