# merge [![Build Status](https://travis-ci.org/yeyuexia/merge.svg?branch=master)](https://travis-ci.org/yeyuexia/merge) 
merge one bean into another bean.

### Usage

* Basic use
```java
public class A {
    private AnyTypeA a;
    private AnyTypeB b;
}

public class B {
    private AnyTypeA a;
    private AnyTypeB b;
}

A from = new A();
from.setA("a");
B to = new B();

merge(from, to);

assertEqual(from.getA(), to.getA());
```

* IgnoreNullValue

```java
A from = new A();
from.setA("a");
B to = new B();
from.setA("b");

merge(from, to, true);

assertEqual(to.getB(), "b");
```

```java
A from = new A();
from.setA("a");
B to = new B();
from.setA("b");

merge(from, to, false);

assertEqual(to.getB(), null);
```

### Advance Usage

* Custom copier operation

```java
A from = new A();
from.setA("a");
B to = new B();

MergeConfiguration configuration = new MergeConfiguration().custom(AnyTypeA.class, from -> "c");
withConfiguration(configuration).merge(from, to);

assertEquals(to.getA(), "c");
```

* Notify update

    * global
    ```java
    A from = new A();
    from.setA("a");
    B to = new B();
    withConfiguration(new MergeConfiguration().updateNotify(() -> mock.notifyUpdate())).merge(from, to);
  
    verify(mock, times(1)).notifyUpdate();
    ```
    * field
    
    ```java
    A from = new A();
    from.setA("a");
    B to = new B();
    withConfiguration(new MergeConfiguration().updateNotify("a", (path, f, t) -> mock.notifyUpdate())).merge(from, to);
  
    verify(mock, times(1)).notifyUpdate();
    ```
