package ca.cutterslade.util.jvmbuilder.common;

import ca.cutterslade.util.jvmbuilder.JvmFactory;

public abstract class AbstractJvmFactory<T extends AbstractJvmFactoryBuilder<T>> implements JvmFactory<T> {
  public AbstractJvmFactory(final T builder) {
  }

  protected T copyOptions(final T builder) {
    return builder;
  }
}
