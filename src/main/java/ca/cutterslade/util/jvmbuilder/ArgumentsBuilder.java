package ca.cutterslade.util.jvmbuilder;

public interface ArgumentsBuilder<T extends JvmFactoryBuilder<T>> {
  ArgumentsBuilder<T> addArguments(String... arguments);

  T build();
}
