package ca.cutterslade.util.jvmbuilder.common;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

import ca.cutterslade.util.jvmbuilder.ClassPathBuilder;
import ca.cutterslade.util.jvmbuilder.JvmFactoryBuilder;

import com.google.common.collect.Lists;

public class SimpleClassPathBuilder<T extends JvmFactoryBuilder<T>> implements ClassPathBuilder<T> {

  private final T jvmBuilder;

  private final List<String> entries;

  public static <T extends JvmFactoryBuilder<T>> SimpleClassPathBuilder<T> clean(final T jvmBuilder) {
    return new SimpleClassPathBuilder<>(jvmBuilder, Lists.<String>newArrayList());
  }

  public static <T extends JvmFactoryBuilder<T>> SimpleClassPathBuilder<T> inherit(final T jvmBuilder) {
    return new SimpleClassPathBuilder<>(jvmBuilder, getInheritedPath());
  }

  private static List<String> getInheritedPath() {
    return Lists.newArrayList(System.getProperty("java.class.path").split(System.getProperty("path.separator")));
  }

  private SimpleClassPathBuilder(final T jvmBuilder, final List<String> entries) {
    this.jvmBuilder = jvmBuilder;
    this.entries = entries;
  }

  @Override
  public ClassPathBuilder<T> addEntry(final String classPath) {
    entries.add(classPath);
    return this;
  }

  @Override
  public ClassPathBuilder<T> addEntry(final Path classPath) {
    return addEntry(classPath.toUri());
  }

  @Override
  public ClassPathBuilder<T> addEntry(final URL classPath) throws URISyntaxException {
    return addEntry(classPath.toURI());
  }

  @Override
  public ClassPathBuilder<T> addEntry(final URI classPath) {
    return addEntry(classPath.toString());
  }

  @Override
  public JvmFactoryBuilder<T> build() {
    return jvmBuilder.setClassPath(entries);
  }
}
