package ca.cutterslade.util.jvmbuilder;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

@Immutable
public interface JvmFactory<T extends JvmFactoryBuilder<T>> {
  T clearProgram();

  Process start(String... args) throws IOException;
}
