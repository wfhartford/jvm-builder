package ca.cutterslade.util.jvmbuilder;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public interface ArgumentsBuilder<T extends JvmFactoryBuilder<T>> {
  ArgumentsBuilder<T> addArguments(String... arguments);

  T build();
}
