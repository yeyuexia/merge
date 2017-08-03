package com.yyx.merge;

import com.exmertec.dummie.Dummie;
import com.exmertec.dummie.configuration.GenerationStrategy;
import com.yyx.merge.base.SimpleObjectA;
import com.yyx.merge.base.SimpleObjectB;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.BeanUtilsBean2;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.stream.Stream;

import static com.yyx.merge.Merge.merge;
import static org.junit.Assert.assertEquals;

public class MergerTest {

    private PropertyUtilsBean propertyUtils = BeanUtilsBean2.getInstance().getPropertyUtils();

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
        Stream<String> names = Arrays.stream(SimpleObjectB.class.getDeclaredFields()).map(Field::getName);
        names.filter(field -> propertyUtils.isWriteable(to, field))
                .filter(field -> propertyUtils.isReadable(to, field))
                .filter(field -> propertyUtils.isWriteable(from, field))
                .filter(field -> propertyUtils.isReadable(from, field))
                .forEach(field -> fieldEquals(from, to, field));
        names.filter(field -> propertyUtils.isReadable(to, field))
                .filter(field -> !propertyUtils.isWriteable(to, field))
                .forEach(field -> fieldNonEquals(from, to, field));
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