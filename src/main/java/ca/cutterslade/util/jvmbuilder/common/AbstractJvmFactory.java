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
    properties = null == builder.getProperties() ? null : ImmutableMap.copyOf(builder.getProperties());
    environment = null == builder.getEnvironment() ? null : ImmutableMap.copyOf(builder.getEnvironment());
    assertions = builder.getAssertions();
    assertionParts = null == builder.getAssertionParts() ? null : ImmutableList.copyOf(builder.getAssertionParts());
    systemAssertions = builder.getSystemAssertions();
    verboseComponents =
        null == builder.getVerboseComponents() ? null : ImmutableSet.copyOf(builder.getVerboseComponents());
    jvmArguments = null == builder.getJvmArguments() ? null : ImmutableList.copyOf(builder.getJvmArguments());
    programArguments =
        null == builder.getProgramArguments() ? null : ImmutableList.copyOf(builder.getProgramArguments());
    startType = builder.getStartType();
    jarPath = startType == StartType.JAR ? builder.getJarPath() : null;
    mainClass = startType == StartType.CLASS ? builder.getMainClass() : null;
    maxHeapSize = builder.getMaxHeapArgument();
    initHeapSize = builder.getInitHeapArgument();
    stackSize = builder.getStackArgument();
    workingDirectory = builder.getWorkingDirectory();
  }

  Path getBuilderJavaHome() {
    return javaHome;
  }

  JvmType getBuilderJvmType() {
    return jvmType;
  }

  JvmArchitecture getBuilderJvmArchitecture() {
    return jvmArchitecture;
  }

  String getBuilderJvmVersion() {
    return jvmVersion;
  }

  ImmutableMap<String, String> getBuilderProperties() {
    return properties;
  }

  ImmutableList<String> getBuilderClassPath() {
    return classPath;
  }

  ImmutableMap<String, String> getBuilderEnvironment() {
    return environment;
  }

  Status getBuilderAssertions() {
    return assertions;
  }

  ImmutableList<String> getBuilderAssertionParts() {
    return assertionParts;
  }

  Status getBuilderSystemAssertions() {
    return systemAssertions;
  }

  ImmutableSet<Component> getBuilderVerboseComponents() {
    return verboseComponents;
  }

  ImmutableList<String> getBuilderJvmArguments() {
    return jvmArguments;
  }

  ImmutableList<String> getBuilderProgramArguments() {
    return programArguments;
  }

  StartType getBuilderStartType() {
    return startType;
  }

  Path getBuilderJarPath() {
    return jarPath;
  }

  String getBuilderMainClass() {
    return mainClass;
  }

  SizeArgument getBuilderMaxHeapSize() {
    return maxHeapSize;
  }

  SizeArgument getBuilderInitHeapSize() {
    return initHeapSize;
  }

  SizeArgument getBuilderStackSize() {
    return stackSize;
  }

  Path getBuilderWorkingDirectory() {
    return workingDirectory;
  }

  protected ImmutableList<String> getCommand(final String[] args) {
    return ImmutableList.<String>builder()
        .add(getExecutable())
        .addAll(getJvmOptions())
        .addAll(getProgram())
        .addAll(getProgramArguments())
        .add(args)
        .build();
  }

  protected Path getJavaHome() {
    return null == javaHome ? Paths.get(System.getProperty("java.home")) : javaHome;
  }

  protected String getExecutable() {
    return getJavaHome().resolve("bin").resolve("java").toString();
  }

  protected Iterable<String> getJvmOptions() {
    final ImmutableList.Builder<String> builder = ImmutableList.builder();
    addJvmTypeArgument(builder);
    addJvmArchitectureArgument(builder);
    addJvmVersionArgument(builder);
    addAssertionArguments(builder);
    addSystemAssertionArgument(builder);
    addVerboseComponentsArgument(builder);
    addJvmArguments(builder);
    addSizeArguments(builder);
    addPropertiesArguments(builder);
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

  protected void addVerboseComponentsArgument(final ImmutableList.Builder<String> builder) {
    if (null != verboseComponents) {
      if (verboseComponents.isEmpty()) {
        builder.add("-verbose");
      }
      else {
        for (final Component component : verboseComponents) {
          builder.add(component.getArgument());
        }
      }
    }
  }

  protected void addJvmArguments(final ImmutableList.Builder<String> builder) {
    if (null == jvmArguments) {
      builder.addAll(getCurrentJvmArguments());
    }
    else {
      builder.addAll(jvmArguments);
    }
  }

  protected abstract Iterable<String> getCurrentJvmArguments();

  protected void addSizeArguments(final ImmutableList.Builder<String> builder) {
    if (null != maxHeapSize) {
      builder.add(maxHeapSize.getArgument());
    }
    if (null != initHeapSize) {
      builder.add(initHeapSize.getArgument());
    }
    if (null != stackSize) {
      builder.add(stackSize.getArgument());
    }
  }

  protected void addPropertiesArguments(final ImmutableList.Builder<String> builder) {
    if (null == properties) {
      builder.addAll(getCurrentPropertiesArguments());
    }
    else {
      for (final Map.Entry<String, String> entry : properties.entrySet()) {
        builder.add("-D" + entry.getKey() + '=' + entry.getValue());
      }
    }
  }

  protected abstract Iterable<String> getCurrentPropertiesArguments();

  protected Iterable<String> getProgram() {
    return StartType.JAR == startType ? ImmutableList.of("-jar", getJarPath()) :
        ImmutableList.of("-cp", getClassPath(), getMainClass());
  }

  private String getJarPath() {
    return jarPath.toString();
  }

  private String getClassPath() {
    return null == classPath ? System.getProperty("java.class.path") :
        Joiner.on(System.getProperty("path.separator")).join(classPath);
  }

  private String getMainClass() {
    return mainClass;
  }

  protected Iterable<String> getProgramArguments() {
    return null == programArguments ? ImmutableList.<String>of() : programArguments;
  }

  protected ImmutableMap<String, String> getEnvironment() {
    return environment;
  }

  protected Path getWorkingDirectory() {
    return workingDirectory;
  }

  @Override
  public Process start(final String... args) throws IOException {
    return Runtime.getRuntime().exec(getCommandArray(args), getEnvironmentArray(), getWorkingDirectoryFile());
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
      for (final Map.Entry<String, String> entry : environment.entrySet()) {
        environmentArray[position++] = entry.getKey() + '=' + entry.getValue();
      }
    }
    return environmentArray;
  }

  private String[] getCommandArray(final String[] args) {
    final ImmutableList<String> command = getCommand(args);
    return command.toArray(new String[command.size()]);
  }

  private File getWorkingDirectoryFile() {
    final Path workingDirectory = getWorkingDirectory();
    return null == workingDirectory ? null : workingDirectory.toFile();
  }

}
