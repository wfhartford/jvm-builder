package ca.cutterslade.util.jvmbuilder;

import javax.annotation.concurrent.Immutable;

@Immutable
public interface JvmFactory<T extends JvmFactoryBuilder<T>> {
  T clearProgram();

  Process start(String... args);
}
