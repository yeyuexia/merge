# merge [![Build Status](https://travis-ci.org/yeyuexia/merge.svg?branch=master)](https://travis-ci.org/yeyuexia/merge) 
A lightweight tool to merge one bean into another bean.

### Usage

* Add Dependency
```groovy
compile "io.github.yeyuexia:merge:1.3"
```
* Basic Use
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
We can set a custom copier only special target class.
```java
A from = new A();
from.setA("a");
B to = new B();

MergeConfiguration configuration = new MergeConfiguration<>().custom(TargetTypeInB.class, from -> "c");
withConfiguration(configuration).merge(from, to);

assertEquals(to.getA(), "c");
```
and also special the source class 
```java
A from = new A();
from.setA("a");
B to = new B();

MergeConfiguration configuration = new MergeConfiguration<>().custom(TargetTypeInB.class, SourTypeInA.class, from -> "c");
withConfiguration(configuration).merge(from, to);

assertEquals(to.getA(), "c");
```

* Notify update
  All notifiers would be invoked after merge operation finish, so that we can do anything in notifier and don't need to worry interfere the merge operation.
    * global
    ```java
    A from = new A();
    from.setA("a");
    B to = new B();
    withConfiguration(new MergeConfiguration<>().updateNotify(() -> mock.notifyUpdate())).merge(from, to);
  
    verify(mock, times(1)).notifyUpdate();
    ```
    * global notify can get from and to instance
    ```java
    A from = new A();
    from.setA("a");
    B to = new B();
    withConfiguration(new MergeConfiguration<A, B>().updateNotify((source, target) -> target.setUpdateDate(now()))).merge(from, to);
  
    verify(mock, times(1)).notifyUpdate();
    ```
    * field
    ```java
    A from = new A();
    from.setA("a");
    B to = new B();
    withConfiguration(new MergeConfiguration<>().updateNotify("a", (path, f, t) -> mock.notifyUpdate())).merge(from, to);
  
    verify(mock, times(1)).notifyUpdate();
    ```
    * field notify could also get from and to instance
    ```java
    A from = new A();
    from.setA("a");
    B to = new B();
    withConfiguration(new MergeConfiguration<A, B>().updateNotify("a", (path, source, target, f, t) -> target.setUpdateDate(now()))).merge(from, to);
  
    verify(mock, times(1)).notifyUpdate();
    ```
