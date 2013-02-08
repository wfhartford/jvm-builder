package ca.cutterslade.util.jvmbuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public interface ClassPathBuilder<T extends JvmFactoryBuilder<T>> {
  ClassPathBuilder<T> addEntry(String classPath);

  ClassPathBuilder<T> addEntry(Path classPath);

  ClassPathBuilder<T> addEntry(URL classPath) throws URISyntaxException;

  ClassPathBuilder<T> addEntry(URI classPath);

  JvmFactoryBuilder<T> build();
}
