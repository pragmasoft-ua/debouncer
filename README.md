# Debouncer solution

See Question 5 here:

https://www.javaspecialists.eu/archive/Issue265-Threading-Questions-in-Job-Interviews-Part-1.html

Sometimes we want to invoke a method call only once per time interval. Let's start with a simple interface Callback:

```java
public interface Callback<T> {
  void call(T t);
}
```

The Debouncer would extend that interface, but add the functionality to run the call only once per time interval. Thus if someone calls it 1000 times in a row, we still only call it once. The Debouncer follows the protection proxy design pattern, both in intent and in structure. Since it may start a background thread to manage the actual call, we give it a shutdown() method.

```java
public interface Debouncer<T> extends Callback<T> {
  void shutdown();
}
```

Implement the Debouncer interface so that the following test passes:

```java
// pass in Debouncer with interval of 1 second
public void test(Debouncer<Runnable> db) throws InterruptedException {
  AtomicInteger rx_count = new AtomicInteger();
  AtomicInteger ry_count = new AtomicInteger();

  Runnable rx = () -> {
    System.out.println("x");
    rx_count.incrementAndGet();
  };
  Runnable ry = () -> {
    System.out.println("y");
    ry_count.incrementAndGet();
  };

  for (int i = 0; i < 8; i++) {
    Thread.sleep(50);
    db.call(rx);
    Thread.sleep(50);
    db.call(ry);
  }
  Thread.sleep(200); // expecting x and y
  assertEquals(1, rx_count.get());
  assertEquals(1, ry_count.get());

  for (int i = 0; i < 10000; i++) {
    db.call(rx);
  }
  Thread.sleep(2_400); // expecting only x
  assertEquals(2, rx_count.get());
  assertEquals(1, ry_count.get());

  db.call(ry);
  Thread.sleep(1_100); // expecting only y
  assertEquals(2, rx_count.get());
  assertEquals(2, ry_count.get());
  db.shutdown();
}
```

### Build

`./mvnw package`

### Test

`./mvnw test`

### Run

`./mvnw exec:java`

## Formatting

This project uses automatic validation of source formatting rules. Format validation is performed automatically during validation phase, that is, first phase of maven build lifecycle, so any of the commands below also performs validation.

To skip format validation, use formatter.skip system property, for example

`./mvnw -Dformatter.skip validate`

To automatically reformat all sources according to rules, you can use following command

`./mvnw formatter:format`

You can also use formatter configuration `${project.basedir}/eclipse-java-google-style.xml` to set up formatting rules in your IDE.

These rules are in the format native to Eclipse formatter, so in other IDEs like IDEA, you will need
special [plugin](https://plugins.jetbrains.com/plugin/6546-eclipse-code-formatter) installed to be able to use this configuration.

You can also copy or symlink a pre-commit git hook from `src/main/git/hooks` to `.git/hooks`, which will automatically validate formatting rules
before git commits.

Alternatively, you may wish to edit hook to automatically reformat `./mvnw formatter:format` code, instead of validation
