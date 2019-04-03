package io.github.yeyuexia.merge.base.data;

import java.util.Objects;

public class BaseObject {

  private String baseObjectStringField;
  private Integer baseObjectIntegerField;

  public String getBaseObjectStringField() {
    return baseObjectStringField;
  }

  public void setBaseObjectStringField(String baseObjectStringField) {
    this.baseObjectStringField = baseObjectStringField;
  }

  public Integer getBaseObjectIntegerField() {
    return baseObjectIntegerField;
  }

  public void setBaseObjectIntegerField(Integer baseObjectIntegerField) {
    this.baseObjectIntegerField = baseObjectIntegerField;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BaseObject that = (BaseObject) o;
    return Objects.equals(baseObjectStringField, that.baseObjectStringField) &&
        Objects.equals(baseObjectIntegerField, that.baseObjectIntegerField);
  }

  @Override
  public int hashCode() {
    return Objects.hash(baseObjectStringField, baseObjectIntegerField);
  }
}
