package ca.cutterslade.util.jvmbuilder.sun;

import ca.cutterslade.util.jvmbuilder.common.AbstractJvmFactoryBuilder;

public final class SunJvmFactoryBuilder extends AbstractJvmFactoryBuilder<SunJvmFactoryBuilder> {
  @Override
  public SunJvmFactory build() {
    return new SunJvmFactory(this);
  }
}
