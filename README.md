# Merge [![Build Status](https://travis-ci.org/yeyuexia/merge.svg?branch=master)](https://travis-ci.org/yeyuexia/merge) [![Maven Central](https://img.shields.io/maven-central/v/io.github.yeyuexia/merge.svg)](https://search.maven.org/search?q=g:io.github.yeyuexia%20AND%20a:merge)
A lightweight tool to merge one bean into another bean.

### Usage

* Add Dependency
```groovy
compile "io.github.yeyuexia:merge:1.4.4"
```
* Merge Object
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

* Notify update

  All notifiers would be invoked after merge operation finish, so that we can do anything in notifier and don't need to worry interfere the merge operation.
    * global
    ```java
    A from = new A();
    from.setA("a");
    B to = new B();
    withConfiguration(new MergeConfiguration<>().updateNotify(() -> mock.notifyUpdate())).merge(from, to);
  
    verify(mock).notifyUpdate();
    ```
    * global notify can get from and to instance
    ```java
    A from = new A();
    from.setA("a");
    B to = new B();
    withConfiguration(new MergeConfiguration<A, B>()
      .updateNotify((source, target) -> target.setUpdateDate(now()))).merge(from, to);
  
    verify(mock).notifyUpdate();
    ```
    * field
    ```java
    A from = new A();
    from.setA("a");
    B to = new B();
    withConfiguration(new MergeConfiguration<>()
      .updateNotify("a", (path, f, t) -> mock.notifyUpdate())).merge(from, to);
  
    verify(mock).notifyUpdate();
    ```
    * path support `.`
    ```java
    A from = new A();
    from.getA().setC("c");
    B to = new B();
    withConfiguration(new MergeConfiguration<>()
      .updateNotify("a.c", (path, f, t) -> mock.notifyUpdate())).merge(from, to);
  
    verify(mock).notifyUpdate();
    ```
    * multi fields, means the notifier should be trigger when all altered paths changed
    ```java
    A from = new A();
    from.setA("a");
    from.setB("b");
    B to = new B();
    withConfiguration(new MergeConfiguration<>()
      .updateNotify(Arrays.asList("a", "b"), (source, target, updatedFields) -> mock.notifyUpdate())).merge(from, to);
  
    verify(mock).notifyUpdate();
    ```
    * field notify could also get from and to instance
    ```java
    A from = new A();
    from.setA("a");
    B to = new B();
    withConfiguration(new MergeConfiguration<A, B>()
      .updateNotify("a", (path, source, target, f, t) -> target.setUpdateDate(now()))).merge(from, to);
  
    verify(mock).notifyUpdate();
    ```

* IgnoreNullValue

```java
A from = new A();
from.setA(null);
B to = new B();
from.setA("b");

merge(from, to, true);

assertEqual(to.getB(), "b");
```
*Notes: IgnoreNullValue default value is true*
```java
A from = new A();
from.setA("a");
B to = new B();
from.setA("b");

merge(from, to, false);

assertEqual(to.getB(), null);
```

### Advance Usage
* Immutable type support. Now `merge` auto identify the immutable types: `ZonedDateTime`, `LocalDateTime`, `OffsetDateTime` and `BigDecimal`, you can also pre-define the immutable types in `MergeConfiguration` so that `merge` can discern them and do right way.
```java
new MergeConfiguration<>().immutableTypes(SimpleObjectA.class);
```

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

MergeConfiguration configuration = new MergeConfiguration<>()
  .custom(TargetTypeInB.class, SourTypeInA.class, from -> "c");
withConfiguration(configuration).merge(from, to);

assertEquals(to.getA(), "c");
```
### Knowledge
##### Collection merge
When merge two collection, there are below cases:
  * `from.size() <= to.size()`, merge the elem with same index.
  * `from.size() > to.size()`, first use the same logical when `from.size() <= to.size()`, and create new instance and add to `to`.
  * Could not support merge `Map`.