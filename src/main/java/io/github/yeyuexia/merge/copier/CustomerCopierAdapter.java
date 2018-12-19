package io.github.yeyuexia.merge.copier;

import java.util.Objects;
import java.util.function.Function;

public class CustomerCopierAdapter<From, To> {

  private Class<From> sourceClass;
  private Function<From, To> customerCopier;

  public CustomerCopierAdapter(Class<From> sourceClass, Function<From, To> customerCopier) {
    this.sourceClass = sourceClass;
    this.customerCopier = customerCopier;
  }

  public boolean match(Object value) {
    return sourceClass.isInstance(value);
  }

  public Class<From> getSourceClass() {
    return sourceClass;
  }

  public void setSourceClass(Class<From> sourceClass) {
    this.sourceClass = sourceClass;
  }

  public Function<From, To> getCustomerCopier() {
    return customerCopier;
  }

  public void setCustomerCopier(Function<From, To> customerCopier) {
    this.customerCopier = customerCopier;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CustomerCopierAdapter<?, ?> that = (CustomerCopierAdapter<?, ?>) o;
    return Objects.equals(sourceClass, that.sourceClass)
        && Objects.equals(customerCopier, that.customerCopier);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sourceClass, customerCopier);
  }
}
