package com.yyx.merge;

import com.exmertec.dummie.Dummie;
import com.exmertec.dummie.configuration.GenerationStrategy;
import com.yyx.merge.base.BaseObjectA;
import com.yyx.merge.base.SimpleObjectA;
import com.yyx.merge.base.SimpleObjectB;
import com.yyx.merge.base.SubObjectB;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class MergeTest {

    private PropertyUtilsBean propertyUtils = BeanUtilsBean.getInstance().getPropertyUtils();

    @Test
    public void should_success_merge_same_name_fields_which_have_getter_and_setter() throws Exception {
        SimpleObjectA from = Dummie.withStrategy(GenerationStrategy.RANDOM).create(SimpleObjectA.class);
        SimpleObjectB to = new SimpleObjectB();
        Merge.merge(from, to);
        Arrays.stream(SimpleObjectB.class.getDeclaredFields())
                .map(Field::getName)
                .filter(field -> propertyUtils.isWriteable(to, field))
                .filter(field -> propertyUtils.isReadable(to, field))
                .filter(field -> propertyUtils.isWriteable(from, field))
                .filter(field -> propertyUtils.isReadable(from, field))
                .forEach(field -> fieldEquals(from, to, field));
    }

    @Test
    public void should_only_merge_fields_which_has_setter() throws Exception {
        SimpleObjectA from = Dummie.withStrategy(GenerationStrategy.RANDOM).create(SimpleObjectA.class);
        SimpleObjectB to = new SimpleObjectB();
        Merge.merge(from, to);
        List<String> names = Arrays.stream(SimpleObjectB.class.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList());
        names.stream().filter(field -> propertyUtils.isWriteable(to, field))
                .filter(field -> propertyUtils.isReadable(to, field))
                .filter(field -> propertyUtils.isWriteable(from, field))
                .filter(field -> propertyUtils.isReadable(from, field))
                .forEach(field -> fieldEquals(from, to, field));
        names.stream().filter(field -> propertyUtils.isReadable(to, field))
                .filter(field -> !propertyUtils.isWriteable(to, field))
                .forEach(field -> fieldNonEquals(from, to, field));
    }

    @Test
    public void should_merge_super_class_fields() throws Exception {
        BaseObjectA from = Dummie.withStrategy(GenerationStrategy.RANDOM).create(BaseObjectA.class);
        SubObjectB to = new SubObjectB();

        Merge.merge(from, to);

        Arrays.stream(BaseObjectA.class.getDeclaredFields()).map(Field::getName)
                .forEach(field -> fieldEquals(from, to, field));
    }

    @Test
    public void should_from_object_super_class_fields_success_merge_to_fields() throws Exception {
        SubObjectB from = Dummie.withStrategy(GenerationStrategy.RANDOM).create(SubObjectB.class);
        BaseObjectA to = new BaseObjectA();

        Merge.merge(from, to);

        Arrays.stream(BaseObjectA.class.getDeclaredFields()).map(Field::getName)
                .forEach(field -> fieldEquals(from, to, field));
    }

    private <X, Y> void fieldNonEquals(X from, Y to, String field) {
        try {
            assertEquals(propertyUtils.getProperty(from, field), propertyUtils.getProperty(to, field));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private <X, Y> void fieldEquals(X from, Y to, String field) {
        try {
            assertEquals(propertyUtils.getProperty(from, field), propertyUtils.getProperty(to, field));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}