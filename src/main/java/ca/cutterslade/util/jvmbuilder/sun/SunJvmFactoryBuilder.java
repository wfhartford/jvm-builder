package ca.cutterslade.util.jvmbuilder.sun;

import ca.cutterslade.util.jvmbuilder.common.AbstractJvmFactoryBuilder;

public final class SunJvmFactoryBuilder extends AbstractJvmFactoryBuilder<SunJvmFactoryBuilder> {
  @Override
  protected void checkBuildPreconditions() {
    super.checkBuildPreconditions();
  }

  @Override
  public SunJvmFactory build() {
    checkBuildPreconditions();
    return new SunJvmFactory(this);
  }

  @Override
  public SunJvmFactoryBuilder optionsFrom(final SunJvmFactory factory) {
    return super.optionsFrom(factory);
  }
}
