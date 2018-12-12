package io.github.yeyuexia.merge;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.PropertyUtils;

public class BaseTest {

  protected <X, Y> void fieldNonEquals(X from, Y to, String field) {
    try {
      assertEquals(PropertyUtils.getProperty(from, field), PropertyUtils.getProperty(to, field));
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      e.printStackTrace();
    }
  }

  protected <X, Y> void fieldEquals(X from, Y to, String field) {
    try {
      assertEquals(PropertyUtils.getProperty(from, field), PropertyUtils.getProperty(to, field));
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      e.printStackTrace();
    }
  }
}
