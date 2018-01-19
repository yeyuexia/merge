package io.github.yeyuexia.merge;

import com.exmertec.dummie.Dummie;
import com.exmertec.dummie.configuration.GenerationStrategy;
import io.github.yeyuexia.merge.base.data.BaseObject;
import io.github.yeyuexia.merge.base.data.SimpleObjectA;
import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class CustomFieldMergeTest extends BaseTest {
    @Test
    public void should_use_custom_consumer_do_copy_when_merge_data() throws Exception {
        Merger merger = Merge.withConfiguration(new MergeConfiguration().custom(BaseObject.class, from -> new BaseObject()));
        ObjectWithCustomField from = Dummie.withStrategy(GenerationStrategy.RANDOM)
                .create(ObjectWithCustomField.class);
        ObjectWithCustomField to = new ObjectWithCustomField();

        merger.merge(from, to);

        Arrays.stream(to.getField2().getClass().getDeclaredFields()).map(Field::getName)
                .forEach(field -> fieldEquals(from.getField2(), to.getField2(), field));
        List<String> fields = Arrays.stream(to.getField1().getClass().getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList());
        for (String field : fields) {
            assertEquals(null, PropertyUtils.getProperty(to.getField1(), field));
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
