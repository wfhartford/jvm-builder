package ca.cutterslade.util.jvmbuilder.sun;

import java.lang.management.ManagementFactory;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import ca.cutterslade.util.jvmbuilder.JvmType;
import ca.cutterslade.util.jvmbuilder.common.AbstractJvmFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

public class SunJvmFactory extends AbstractJvmFactory<SunJvmFactoryBuilder> {

  private static final Pattern HANDLED_ARGUMENTS_PATTERN = Pattern.compile(
      "(?:-X(?:mx|ms|ss)\\d+[kmg]?)|-client|-server|" +
          "(?:-ea|-enableassertions|-da|-disablesystemassertions(?::.+)?)|" +
          "-esa|-enablesystemassertions|-dsa|-disablesystemassertions|-verbose(?::.+)?|-version:.+|-d32|-d64|-D.+");
  private static final Predicate<String> HANDLED_ARGUMENTS_PREDICATE = new Predicate<String>() {
    @Override
    public boolean apply(@Nullable final String input) {
      return HANDLED_ARGUMENTS_PATTERN.matcher(input).matches();
    }
  };

  private static final Predicate<String> PROPERTIES_ARGUMENTS_PREDICATE = new Predicate<String>() {
    @Override
    public boolean apply(@Nullable final String input) {
      return input.startsWith("-D");
    }
  };

  private static final Predicate<String> ARCHITECTURE_ARGUMENTS_PREDICATE = new Predicate<String>() {
    @Override
    public boolean apply(@Nullable final String input) {
      return "-d32".equals(input) || "-d64".equals(input);
    }
  };

  public SunJvmFactory(final SunJvmFactoryBuilder sunJvmFactoryBuilder) {
    super(sunJvmFactoryBuilder);
  }

  @Override
  protected String getCurrentJvmTypeArgument() {
    final String nameString = ManagementFactory.getRuntimeMXBean().getVmName().toLowerCase();
    final boolean server = nameString.contains("server");
    final boolean client = nameString.contains("client");
    return server == client ? null : server ? JvmType.SERVER.getArgument() : JvmType.CLIENT.getArgument();
  }

  @Nullable
  @Override
  protected String getCurrentJvmArchitectureArgument() {
    return Iterables
        .find(ManagementFactory.getRuntimeMXBean().getInputArguments(), ARCHITECTURE_ARGUMENTS_PREDICATE, null);
  }

  @Override
  protected Iterable<String> getCurrentJvmArguments() {
    return Collections2.filter(ManagementFactory.getRuntimeMXBean().getInputArguments(), HANDLED_ARGUMENTS_PREDICATE);
  }

  @Override
  protected Iterable<String> getCurrentPropertiesArguments() {
    return Collections2
        .filter(ManagementFactory.getRuntimeMXBean().getInputArguments(), PROPERTIES_ARGUMENTS_PREDICATE);
  }

  @Override
  public SunJvmFactoryBuilder clearProgram() {
    return new SunJvmFactoryBuilder().from(this).resetProgram();
  }

}
