package io.github.yeyuexia.merge;

import static org.junit.Assert.assertEquals;

import io.github.dummiejava.dummie.Dummie;
import io.github.dummiejava.dummie.configuration.GenerationStrategy;
import java.util.List;
import org.junit.Test;

public class ListMergeTest {

  @Test
  public void should_success_merge_bean_with_field_has_list_type_with_parameter_type_is_basic_type() throws Exception {
    BasicParameterTypeListClass from = Dummie.withStrategy(GenerationStrategy.RANDOM)
        .create(BasicParameterTypeListClass.class);
    BasicParameterTypeListClass to = new BasicParameterTypeListClass();

    Merge.merge(from, to);

    assertEquals(from.getField(), to.getField());
  }

  @Test
  public void should_success_merge_bean_with_field_has_list_type_with_parameter_type_is_custom_object() throws Exception {
    CustomerTypeListClass from = Dummie.withStrategy(GenerationStrategy.RANDOM).create(CustomerTypeListClass.class);
    CustomerTypeListClass to = new CustomerTypeListClass();

    Merge.merge(from, to);

    assertEquals(from.getField().size(), to.getField().size());
    assertEquals(from.getField().get(0).getField(), to.getField().get(0).getField());
  }

  public static class BasicParameterTypeListClass {

    private List<String> field;

    public BasicParameterTypeListClass() {
    }

    public BasicParameterTypeListClass(List<String> field) {
      this.field = field;
    }

    public List<String> getField() {
      return field;
    }

    public void setField(List<String> field) {
      this.field = field;
    }
  }

  public static class CustomerTypeListClass {

    private List<SimpleObject> field;

    public CustomerTypeListClass() {
    }

    public CustomerTypeListClass(List<SimpleObject> field) {
      this.field = field;
    }

    public List<SimpleObject> getField() {
      return field;
    }

    public void setField(List<SimpleObject> field) {
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
  }
}

