package com.yyx.merge.copier.impl;

import com.exmertec.dummie.Dummie;
import com.exmertec.dummie.configuration.GenerationStrategy;
import com.yyx.merge.Merger;
import com.yyx.merge.base.data.ObjectWithCustomFieldA;
import com.yyx.merge.base.data.ObjectWithCustomFieldB;
import com.yyx.merge.base.data.SimpleObjectA;
import com.yyx.merge.base.data.SimpleObjectB;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DeepCopyCopierTest {
    @Test
    public void should_return_the_from_value_if_the_field_is_scalar_type() throws Exception {
        SimpleObjectA from = Dummie.withStrategy(GenerationStrategy.RANDOM).create(SimpleObjectA.class);
        SimpleObjectB to = new SimpleObjectB();
        DeepCopyCopier copier = new DeepCopyCopier(null);

        Object result = copier.copy(from, to, to.getClass().getDeclaredField("scalarTypeInt"));

        assertEquals(result, from.getScalarTypeInt());
    }

    @Test
    public void should_call_merger_to_get_value_if_the_field_is_high_level_type() throws Exception {
        ObjectWithCustomFieldA from = Dummie.withStrategy(GenerationStrategy.RANDOM).create(ObjectWithCustomFieldA.class);
        ObjectWithCustomFieldB to = new ObjectWithCustomFieldB();
        Merger merger = mock(Merger.class);
        DeepCopyCopier copier = new DeepCopyCopier(merger);

        Object result = copier.copy(from, to, to.getClass().getDeclaredField("customFieldA"));

        verify(merger, times(1)).merge(any(SimpleObjectA.class), any(SimpleObjectB.class));
    }

    @Test
    public void should_use_same_object_instance_when_to_object_has_value_for_non_scalar_type() throws Exception {
        ObjectWithCustomFieldA from = Dummie.withStrategy(GenerationStrategy.RANDOM).create(ObjectWithCustomFieldA.class);
        ObjectWithCustomFieldB to = new ObjectWithCustomFieldB();
        to.setCustomFieldA(new SimpleObjectA());
        Merger merger = mock(Merger.class);
        DeepCopyCopier copier = new DeepCopyCopier(merger);

        Object result = copier.copy(from, to, to.getClass().getDeclaredField("customFieldA"));

        verify(merger, times(1)).merge(from.getCustomFieldA(), to.getCustomFieldA());
    }
}