package ca.cutterslade.util.jvmbuilder.sun;

import java.lang.management.ManagementFactory;

import ca.cutterslade.util.jvmbuilder.JvmType;
import ca.cutterslade.util.jvmbuilder.common.AbstractJvmFactory;

public class SunJvmFactory extends AbstractJvmFactory<SunJvmFactoryBuilder> {
  public SunJvmFactory(final SunJvmFactoryBuilder sunJvmFactoryBuilder) {
    super(sunJvmFactoryBuilder);
  }

  @Override
  protected String getCurrentJvmTypeArgument() {
    final String nameString = ManagementFactory.getRuntimeMXBean().getVmName().toLowerCase();
    boolean server = nameString.contains("server");
    boolean client = nameString.contains("client");
    return server == client ? null : server ? JvmType.SERVER.getArgument() : JvmType.CLIENT.getArgument();
  }

  @Override
  public SunJvmFactoryBuilder clearProgram() {
    return new SunJvmFactoryBuilder().optionsFrom(this);
  }
}
