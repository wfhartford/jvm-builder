package ca.cutterslade.util.jvmbuilder;

import java.net.URI;
import java.net.URL;
import java.nio.file.Path;

public interface ClassPathBuilder<T> {
  ClassPathBuilder<T> addEntry(String classPath);

  ClassPathBuilder<T> addEntry(Path classPath);

  ClassPathBuilder<T> addEntry(URL classPath);

  ClassPathBuilder<T> addEntry(URI classPath);
}
