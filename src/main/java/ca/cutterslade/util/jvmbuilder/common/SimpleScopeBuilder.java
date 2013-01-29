package ca.cutterslade.util.jvmbuilder.common;

import java.util.List;

import ca.cutterslade.util.jvmbuilder.JvmFactoryBuilder;
import ca.cutterslade.util.jvmbuilder.ScopeBuilder;
import ca.cutterslade.util.jvmbuilder.Status;

import com.google.common.collect.Lists;

public class SimpleScopeBuilder<T extends JvmFactoryBuilder<T>> implements ScopeBuilder<T> {
  public static <T extends JvmFactoryBuilder<T>> SimpleScopeBuilder<T> enable(final T builder) {
    return new SimpleScopeBuilder<>(builder, Status.ENABLED);
  }

  public static <T extends JvmFactoryBuilder<T>> SimpleScopeBuilder<T> disable(final T builder) {
    return new SimpleScopeBuilder<>(builder, Status.DISABLED);
  }

  private final T builder;
  private final Status status;
  private final List<String> parts = Lists.newArrayList();

  private SimpleScopeBuilder(final T builder, final Status status) {
    this.builder = builder;
    this.status = status;
  }

  @Override
  public ScopeBuilder<T> includePackage(final String packageName) {
    parts.add(packageName + "...");
    return this;
  }

  @Override
  public ScopeBuilder<T> includePackage(final Package pack) {
    return includePackage(pack.getName());
  }

  @Override
  public ScopeBuilder<T> includeClass(final Class<?> type) {
    return includeClass(type.getName());
  }

  @Override
  public ScopeBuilder<T> includeClass(final String className) {
    parts.add(className);
    return this;
  }

  @Override
  public T build() {
    return builder.setAssertions(status, parts);
  }
}
