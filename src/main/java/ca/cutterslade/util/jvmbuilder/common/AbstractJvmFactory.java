package ca.cutterslade.util.jvmbuilder.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import ca.cutterslade.util.jvmbuilder.Component;
import ca.cutterslade.util.jvmbuilder.JvmArchitecture;
import ca.cutterslade.util.jvmbuilder.JvmFactory;
import ca.cutterslade.util.jvmbuilder.JvmType;
import ca.cutterslade.util.jvmbuilder.SizeArgument;
import ca.cutterslade.util.jvmbuilder.Status;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

@Immutable
public abstract class AbstractJvmFactory<T extends AbstractJvmFactoryBuilder<T>> implements JvmFactory<T> {
  private final Path javaHome;
  private final JvmType jvmType;
  private final JvmArchitecture jvmArchitecture;
  private final String jvmVersion;
  private final ImmutableList<String> classPath;
  private final ImmutableMap<String, String> properties;
  private final ImmutableMap<String, String> environment;
  private final Status assertions;
  private final ImmutableList<String> assertionParts;
  private final Status systemAssertions;
  private final ImmutableSet<Component> verboseComponents;
  private final ImmutableList<String> jvmArguments;
  private final ImmutableList<String> programArguments;
  private final StartType startType;
  private final Path jarPath;
  private final String mainClass;
  private final SizeArgument maxHeapSize;
  private final SizeArgument initHeapSize;
  private final SizeArgument stackSize;
  private final Path workingDirectory;

  protected AbstractJvmFactory(final T builder) {
    javaHome = builder.getJavaHome();
    jvmType = builder.getJvmType();
    jvmArchitecture = builder.getJvmArchitecture();
    jvmVersion = builder.getJvmVersion();
    classPath = null == builder.getClassPath() ? null : ImmutableList.copyOf(builder.getClassPath());
    properties = null == builder.getProperties() ?
        ImmutableMap.<String, String>of() : ImmutableMap.copyOf(builder.getProperties());
    environment = null == builder.getEnvironment() ?
        ImmutableMap.<String, String>of() : ImmutableMap.copyOf(builder.getEnvironment());
    assertions = builder.getAssertions();
    assertionParts = null == builder.getAssertionParts() ? null : ImmutableList.copyOf(builder.getAssertionParts());
    systemAssertions = builder.getSystemAssertions();
    verboseComponents =
        null == builder.getVerboseComponents() ? null : ImmutableSet.copyOf(builder.getVerboseComponents());
    jvmArguments = null == builder.getJvmArguments() ? null : ImmutableList.copyOf(builder.getJvmArguments());
    programArguments = null == builder.getProgramArguments() ?
        ImmutableList.<String>of() : ImmutableList.copyOf(builder.getProgramArguments());
    startType = builder.getStartType();
    jarPath = builder.getJarPath();
    mainClass = builder.getMainClass();
    maxHeapSize = builder.getMaxHeapArgument();
    initHeapSize = builder.getInitHeapArgument();
    stackSize = builder.getStackArgument();
    workingDirectory = builder.getWorkingDirectory();
  }

  protected ImmutableList<String> getCommand() {
    return ImmutableList.<String>builder()
        .add(getExecutable())
        .addAll(getJvmOptions())
        .addAll(getProgram())
        .addAll(getProgramArguments())
        .build();
  }

  protected Path getJavaHome() {
    return null == javaHome ? Paths.get(System.getProperty("java.home")) : javaHome;
  }

  protected String getExecutable() {
    return getJavaHome().resolve("bin").resolve("java").toString();
  }

  protected Iterable<String> getJvmOptions() {
    ImmutableList.Builder<String> builder = ImmutableList.builder();
    addJvmTypeArgument(builder);
    addJvmArchitectureArgument(builder);
    addJvmVersionArgument(builder);
    addAssertionArguments(builder);
    addSystemAssertionArgument(builder);
    return builder.build();
  }

  protected void addJvmTypeArgument(@Nonnull final ImmutableList.Builder<String> builder) {
    if (null != jvmType) {
      builder.add(jvmType.getArgument());
    }
    else {
      final String currentVmTypeArgument = getCurrentJvmTypeArgument();
      if (null != currentVmTypeArgument) {
        builder.add(currentVmTypeArgument);
      }
    }
  }

  @Nullable
  protected abstract String getCurrentJvmTypeArgument();

  protected void addJvmArchitectureArgument(final ImmutableList.Builder<String> builder) {
    if (null != jvmArchitecture) {
      builder.add(jvmArchitecture.getArgument());
    }
    else {
      final String currentJvmArchitectureArgument = getCurrentJvmArchitectureArgument();
      if (null != currentJvmArchitectureArgument) {
        builder.add(currentJvmArchitectureArgument);
      }
    }
  }

  @Nullable
  protected abstract String getCurrentJvmArchitectureArgument();

  protected void addJvmVersionArgument(final ImmutableList.Builder<String> builder) {
    if (null != jvmVersion) {
      builder.add("-version:" + jvmVersion);
    }
  }

  protected void addAssertionArguments(final ImmutableList.Builder<String> builder) {
    if (null != assertions) {
      if (null == assertionParts) {
        builder.add(assertions.getProgramArgument());
      }
      else {
        builder.add(assertions.getProgramArgument() + ':' + Joiner.on(':').join(assertionParts));
      }
    }
  }

  protected void addSystemAssertionArgument(final ImmutableList.Builder<String> builder) {
    if (null != systemAssertions) {
      builder.add(systemAssertions.getSystemArgument());
    }
  }

  protected Iterable<String> getProgram() {
    return StartType.JAR == startType ? ImmutableList.of("-jar", getJarPath()) :
        ImmutableList.of("-cp", getClassPath(), getMainClass());
  }

  private String getJarPath() {
    return jarPath.toString();
  }

  private String getClassPath() {
    return Joiner.on(System.getProperty("path.separator")).join(classPath);
  }

  private String getMainClass() {
    return mainClass;
  }

  protected Iterable<String> getProgramArguments() {
    return programArguments;
  }

  protected ImmutableMap<String, String> getEnvironment() {
    return environment;
  }

  protected Path getWorkingDirectory() {
    return workingDirectory;
  }

  @Override
  public Process start(final String... args) throws IOException {
    return Runtime.getRuntime().exec(getCommandArray(), getEnvironmentArray(), getWorkingDirectoryFile());
  }

  private String[] getEnvironmentArray() {
    final ImmutableMap<String, String> environment = getEnvironment();
    final String[] environmentArray;
    if (null == environment) {
      environmentArray = null;
    }
    else {
      environmentArray = new String[environment.size()];
      int position = 0;
      for (Map.Entry<String, String> entry : environment.entrySet()) {
        environmentArray[position++] = entry.getKey() + '=' + entry.getValue();
      }
    }
    return environmentArray;
  }

  private String[] getCommandArray() {
    final ImmutableList<String> command = getCommand();
    return command.toArray(new String[command.size()]);
  }

  private File getWorkingDirectoryFile() {
    Path workingDirectory = getWorkingDirectory();
    return null == workingDirectory ? null : workingDirectory.toFile();
  }
}
