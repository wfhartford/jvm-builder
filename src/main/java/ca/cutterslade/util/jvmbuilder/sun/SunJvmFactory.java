package ca.cutterslade.util.jvmbuilder.sun;

import ca.cutterslade.util.jvmbuilder.common.AbstractJvmFactory;

public class SunJvmFactory extends AbstractJvmFactory<SunJvmFactoryBuilder> {
  public SunJvmFactory(final SunJvmFactoryBuilder sunJvmFactoryBuilder) {
    super(sunJvmFactoryBuilder);
  }

  @Override
  public SunJvmFactoryBuilder clearProgram() {
    return copyOptions(new SunJvmFactoryBuilder());
  }

  @Override
  public Process start(final String... args) {
    return null;
  }
}
