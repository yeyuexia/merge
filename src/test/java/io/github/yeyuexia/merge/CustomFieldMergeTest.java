package io.github.yeyuexia.merge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.exmertec.dummie.Dummie;
import com.exmertec.dummie.configuration.GenerationStrategy;
import io.github.yeyuexia.merge.base.data.BaseObject;
import io.github.yeyuexia.merge.base.data.SimpleObjectA;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Test;

public class CustomFieldMergeTest extends BaseTest {

  @Test
  public void should_use_custom_consumer_do_copy_when_merge_data() throws Exception {
    Merger merger = Merge.withConfiguration(new MergeConfiguration<>()
        .custom(BaseObject.class, BaseObject.class, this::toBaseObject));
    ObjectWithCustomField from = Dummie.withStrategy(GenerationStrategy.RANDOM)
        .create(ObjectWithCustomField.class);
    ObjectWithCustomField to = new ObjectWithCustomField();

    merger.merge(from, to);

    Arrays.stream(to.getField2().getClass().getDeclaredFields())
        .filter(field -> !field.isSynthetic())
        .map(Field::getName)
        .forEach(field -> fieldEquals(from.getField2(), to.getField2(), field));
    List<String> fields = Arrays.stream(to.getField1().getClass().getDeclaredFields())
        .filter(field -> !field.isSynthetic())
        .map(Field::getName)
        .collect(Collectors.toList());
    for (String field : fields) {
      assertEquals(null, PropertyUtils.getProperty(to.getField1(), field));
    }
  }

  @Test
  public void should_success_merge_when_customer_value_as_null() {
    Merger merger = Merge.withConfiguration(new MergeConfiguration<>()
        .custom(BaseObject.class, BaseObject.class, source -> null));
    ObjectWithCustomField from = Dummie.withStrategy(GenerationStrategy.RANDOM)
        .create(ObjectWithCustomField.class);
    ObjectWithCustomField to = new ObjectWithCustomField();

    merger.merge(from, to);

    Arrays.stream(to.getField2().getClass().getDeclaredFields())
        .filter(field -> !field.isSynthetic())
        .map(Field::getName)
        .forEach(field -> fieldEquals(from.getField2(), to.getField2(), field));
    assertTrue(to.getField1() == null);
  }

  @Test
  public void should_use_custom_consumer_do_copy_when_merge_data_and_not_special_source_class_type() throws Exception {
    Merger merger = Merge.withConfiguration(new MergeConfiguration<>()
        .custom(BaseObject.class, this::toBaseObject));
    ObjectWithCustomField from = Dummie.withStrategy(GenerationStrategy.RANDOM)
        .create(ObjectWithCustomField.class);
    ObjectWithCustomField to = new ObjectWithCustomField();

    merger.merge(from, to);

    Arrays.stream(to.getField2().getClass().getDeclaredFields())
        .filter(field -> !field.isSynthetic())
        .map(Field::getName)
        .forEach(field -> fieldEquals(from.getField2(), to.getField2(), field));
    List<String> fields = Arrays.stream(to.getField1().getClass().getDeclaredFields())
        .filter(field -> !field.isSynthetic())
        .map(Field::getName)
        .collect(Collectors.toList());
    for (String field : fields) {
      assertEquals(null, PropertyUtils.getProperty(to.getField1(), field));
    }
  }

  private BaseObject toBaseObject(BaseObject from) {
    return new BaseObject();
  }

  public static class ObjectWithCustomFieldFrom {

    private String field1;
    private SimpleObjectA field2;

    public String getField1() {
      return field1;
    }

    public void setField1(String field1) {
      this.field1 = field1;
    }

    public SimpleObjectA getField2() {
      return field2;
    }

    public void setField2(SimpleObjectA field2) {
      this.field2 = field2;
    }
  }

  public static class ObjectWithCustomField {

    private BaseObject field1;
    private SimpleObjectA field2;

    public BaseObject getField1() {
      return field1;
    }

    public void setField1(BaseObject field1) {
      this.field1 = field1;
    }

    public SimpleObjectA getField2() {
      return field2;
    }

    public void setField2(SimpleObjectA field2) {
      this.field2 = field2;
    }
  }
}
