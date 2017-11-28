package io.github.yeyuexia.merge.copier.impl;

import com.exmertec.dummie.Dummie;
import com.exmertec.dummie.configuration.GenerationStrategy;
import io.github.yeyuexia.merge.base.data.SimpleObjectA;
import io.github.yeyuexia.merge.base.data.SimpleObjectB;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CustomCopierTest {
    @Test
    public void should_get_copy_use_custom_generator() throws Exception {
        SimpleObjectA from = Dummie.withStrategy(GenerationStrategy.RANDOM).create(SimpleObjectA.class);
        SimpleObjectB to = new SimpleObjectB();
        CustomCopier copier = new CustomCopier(a -> 1000);

        Object result = copier.copy(from, to, to.getClass().getDeclaredField("scalarTypeInt"), "");

        assertEquals(result, 1000);
    }
}