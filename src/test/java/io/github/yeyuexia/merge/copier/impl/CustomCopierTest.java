package io.github.yeyuexia.merge.copier.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

import com.exmertec.dummie.Dummie;
import com.exmertec.dummie.configuration.GenerationStrategy;
import io.github.yeyuexia.merge.base.data.BaseObject;
import io.github.yeyuexia.merge.base.data.SimpleObjectA;
import io.github.yeyuexia.merge.base.data.SimpleObjectB;
import io.github.yeyuexia.merge.exception.MergeException;
import java.util.function.Function;
import org.junit.Test;

public class CustomCopierTest {

  @Test
  public void should_get_copy_use_custom_generator() throws NoSuchFieldException {
    SimpleObjectA from = Dummie.withStrategy(GenerationStrategy.RANDOM).create(SimpleObjectA.class);
    SimpleObjectB to = new SimpleObjectB();
    CustomCopier copier = new CustomCopier(a -> 1000);

    Object result = copier.copy(from, to, to.getClass().getDeclaredField("scalarTypeInt"), "");

    assertEquals(result, 1000);
  }

  @Test(expected = MergeException.class)
  public void should_throw_exception_when_throw_NoSuchMethodException() throws NoSuchFieldException {
    SimpleObjectA from = Dummie.withStrategy(GenerationStrategy.RANDOM).create(SimpleObjectA.class);
    BaseObject to = new BaseObject();
    Function function = mock(Function.class);
    CustomCopier copier = new CustomCopier(function);


    copier.copy(from, to, to.getClass().getDeclaredField("baseObjectIntegerField"), "");
  }
}