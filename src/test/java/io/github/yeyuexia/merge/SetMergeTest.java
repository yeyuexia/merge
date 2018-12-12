package io.github.yeyuexia.merge;

import static org.junit.Assert.assertEquals;

import com.exmertec.dummie.Dummie;
import com.exmertec.dummie.configuration.GenerationStrategy;
import java.util.Objects;
import java.util.Set;
import org.junit.Test;

public class SetMergeTest {

  @Test
  public void should_success_merge_bean_with_field_has_set_type_with_parameter_type_is_basic_type() throws Exception {
    BasicParameterTypeSetClass from = Dummie.withStrategy(GenerationStrategy.RANDOM)
        .create(BasicParameterTypeSetClass.class);
    BasicParameterTypeSetClass to = new BasicParameterTypeSetClass();

    Merge.merge(from, to);

    assertEquals(from.getField(), to.getField());
  }

  @Test
  public void should_success_merge_bean_with_field_has_set_type_with_parameter_type_is_custom_object() throws Exception {
    CustomerTypeSetClass from = Dummie.withStrategy(GenerationStrategy.RANDOM).create(CustomerTypeSetClass.class);
    CustomerTypeSetClass to = new CustomerTypeSetClass();

    Merge.merge(from, to);

    assertEquals(from.getField().size(), to.getField().size());
    assertEquals(from.getField(), to.getField());
  }

  public static class BasicParameterTypeSetClass {

    private Set<String> field;

    public BasicParameterTypeSetClass() {
    }

    public BasicParameterTypeSetClass(Set<String> field) {
      this.field = field;
    }

    public Set<String> getField() {
      return field;
    }

    public void setField(Set<String> field) {
      this.field = field;
    }
  }

  public static class CustomerTypeSetClass {

    private Set<SimpleObject> field;

    public CustomerTypeSetClass() {
    }

    public CustomerTypeSetClass(Set<SimpleObject> field) {
      this.field = field;
    }

    public Set<SimpleObject> getField() {
      return field;
    }

    public void setField(Set<SimpleObject> field) {
      this.field = field;
    }
  }

  public static class SimpleObject {

    private String field;

    public String getField() {
      return field;
    }

    public void setField(String field) {
      this.field = field;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      SimpleObject that = (SimpleObject) o;
      return Objects.equals(field, that.field);
    }

    @Override
    public int hashCode() {
      return Objects.hash(field);
    }
  }
}

