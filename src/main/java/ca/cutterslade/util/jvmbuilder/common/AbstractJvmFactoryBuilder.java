package ca.cutterslade.util.jvmbuilder.common;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

import ca.cutterslade.util.jvmbuilder.ArgumentsBuilder;
import ca.cutterslade.util.jvmbuilder.ClassPathBuilder;
import ca.cutterslade.util.jvmbuilder.Component;
import ca.cutterslade.util.jvmbuilder.JvmArchitecture;
import ca.cutterslade.util.jvmbuilder.JvmFactoryBuilder;
import ca.cutterslade.util.jvmbuilder.JvmType;
import ca.cutterslade.util.jvmbuilder.MapBuilder;
import ca.cutterslade.util.jvmbuilder.ScopeBuilder;
import ca.cutterslade.util.jvmbuilder.SizeArgument;
import ca.cutterslade.util.jvmbuilder.SizeParameter;
import ca.cutterslade.util.jvmbuilder.SizeUnit;
import ca.cutterslade.util.jvmbuilder.Status;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

@NotThreadSafe
public abstract class AbstractJvmFactoryBuilder<T extends AbstractJvmFactoryBuilder<T>>
    implements JvmFactoryBuilder<T> {
  protected enum StartType {
    JAR, CLASS
  }

  private Path javaHome;
  private JvmType jvmType;
  private JvmArchitecture jvmArchitecture;
  private String jvmVersion;
  private List<String> classPath;
  private Map<String, String> properties;
  private Map<String, String> environment;
  private Status assertions;
  private List<String> assertionParts;
  private Status systemAssertions;
  private Set<Component> verboseComponents;
  private List<String> jvmArguments;
  private List<String> programArguments;
  private StartType startType;
  private Path jarPath;
  private String mainClass;
  private int maxHeapSize;
  private SizeUnit maxHeapSizeUnit;
  private int initHeapSize;
  private SizeUnit initHeapSizeUnit;
  private int stackSize;
  private SizeUnit stackSizeUnit;

  @Override
  public T setJavaHome(final Path javaHome) {
    Preconditions.checkArgument(null != javaHome);
    Preconditions.checkState(null == this.javaHome);
    this.javaHome = javaHome;
    return getThis();
  }

  @Override
  public T client() {
    return setJvmType(JvmType.CLIENT);
  }

  @Override
  public T server() {
    return setJvmType(JvmType.SERVER);
  }

  @Override
  public T setJvmType(final JvmType jvmType) {
    Preconditions.checkArgument(null != jvmType);
    Preconditions.checkState(null == this.jvmType);
    this.jvmType = jvmType;
    return getThis();
  }

  @Override
  public T require32Bit() {
    return requireArchitecture(JvmArchitecture.ARCH_32_BIT);
  }

  @Override
  public T require64Bit() {
    return requireArchitecture(JvmArchitecture.ARCH_64_BIT);
  }

  @Override
  public T requireArchitecture(final JvmArchitecture jvmArchitecture) {
    Preconditions.checkArgument(null != jvmArchitecture);
    Preconditions.checkState(null == this.jvmArchitecture);
    this.jvmArchitecture = jvmArchitecture;
    return getThis();
  }

  @Override
  public T requireVersion(final String jvmVersion) {
    Preconditions.checkArgument(null != jvmVersion);
    Preconditions.checkState(null == this.jvmVersion);
    this.jvmVersion = jvmVersion;
    return getThis();
  }

  @Override
  public ScopeBuilder<T> enableAssertions() {
    Preconditions.checkState(null == this.assertions);
    this.assertions = Status.ENABLED;
    return SimpleScopeBuilder.enable(getThis());
  }

  @Override
  public ScopeBuilder<T> disableAssertions() {
    Preconditions.checkState(null == this.assertions);
    this.assertions = Status.DISABLED;
    return SimpleScopeBuilder.disable(getThis());
  }

  @Override
  public T setAssertions(final Status status, final List<String> parts) {
    Preconditions.checkArgument(null != status);
    Preconditions.checkArgument(null != parts);
    Preconditions.checkState(null == this.assertionParts);
    this.assertions = status;
    this.assertionParts = parts;
    return getThis();
  }

  @Override
  public T enableSystemAssertions() {
    return setSystemAssertions(Status.ENABLED);
  }

  @Override
  public T disableSystemAssertions() {
    return setSystemAssertions(Status.DISABLED);
  }

  @Override
  public T setSystemAssertions(final Status status) {
    Preconditions.checkArgument(null != status);
    Preconditions.checkState(null == this.systemAssertions);
    this.systemAssertions = status;
    return getThis();
  }

  @Override
  public T verbose(final Component... components) {
    Preconditions.checkArgument(null != components);
    final Component[] comp = 0 == components.length ? Component.values() : components;
    final ImmutableSet<Component> set = Sets.immutableEnumSet(Arrays.asList(comp));
    Preconditions.checkArgument(0 == components.length || set.size() == components.length);
    Preconditions.checkState(null == this.verboseComponents);
    this.verboseComponents = set;
    return getThis();
  }

  @Override
  public MapBuilder<T> inheritProperties() {
    Preconditions.checkState(null == properties);
    return SimpleMapBuilder.inheritProperties(getThis());
  }

  @Override
  public MapBuilder<T> cleanProperties() {
    Preconditions.checkState(null == properties);
    this.properties = ImmutableMap.of();
    return SimpleMapBuilder.cleanProperties(getThis());
  }

  @Override
  public T setProperties(final Map<String, String> properties) {
    Preconditions.checkArgument(null != properties);
    Preconditions.checkState(null == this.properties || this.properties.isEmpty());
    this.properties = properties;
    return getThis();
  }

  @Override
  public MapBuilder<T> inheritEnvironment() {
    Preconditions.checkState(null == this.environment);
    return SimpleMapBuilder.inheritEnvironment(getThis());
  }

  @Override
  public MapBuilder<T> cleanEnvironment() {
    Preconditions.checkState(null == this.environment);
    this.environment = ImmutableMap.of();
    return SimpleMapBuilder.cleanEnvironment(getThis());
  }

  @Override
  public T setEnvironment(final Map<String, String> environment) {
    Preconditions.checkArgument(null != environment);
    Preconditions.checkState(null == this.environment || this.environment.isEmpty());
    this.environment = environment;
    return getThis();
  }

  @Override
  public ArgumentsBuilder<T> inheritJvmArguments() {
    Preconditions.checkState(null == this.jvmArguments);
    return SimpleArgumentsBuilder.inheritJvmArguments(getThis());
  }

  @Override
  public ArgumentsBuilder<T> cleanJvmArguments() {
    Preconditions.checkState(null == this.jvmArguments);
    this.jvmArguments = ImmutableList.of();
    return SimpleArgumentsBuilder.cleanJvmArguments(getThis());
  }

  @Override
  public T setJvmArguments(final List<String> arguments) {
    Preconditions.checkArgument(null != arguments);
    Preconditions.checkState(null == this.jvmArguments || this.jvmArguments.isEmpty());
    this.jvmArguments = ImmutableList.copyOf(arguments);
    return getThis();
  }

  @Override
  public ArgumentsBuilder<T> programArguments() {
    Preconditions.checkState(null == this.programArguments);
    return SimpleArgumentsBuilder.cleanProgramArguments(getThis());
  }

  @Override
  public T setProgramArguments(final List<String> arguments) {
    Preconditions.checkState(null == this.programArguments);
    this.programArguments = arguments;
    return getThis();
  }

  @Override
  public T setMaxHeapSpace(final int size, final SizeUnit unit) {
    Preconditions.checkArgument(0 < size);
    Preconditions.checkArgument(null != unit);
    Preconditions.checkState(null == this.maxHeapSizeUnit);
    maxHeapSize = size;
    maxHeapSizeUnit = unit;
    return getThis();
  }

  @Override
  public T setInitialHeapSpace(final int size, final SizeUnit unit) {
    Preconditions.checkArgument(0 < size);
    Preconditions.checkArgument(null != unit);
    Preconditions.checkState(null == this.initHeapSizeUnit);
    initHeapSize = size;
    initHeapSizeUnit = unit;
    return getThis();
  }

  @Override
  public T setStackSize(final int size, final SizeUnit unit) {
    Preconditions.checkArgument(0 < size);
    Preconditions.checkArgument(null != unit);
    Preconditions.checkState(null == this.stackSizeUnit);
    stackSize = size;
    stackSizeUnit = unit;
    return getThis();
  }

  @Override
  public T jar(final Path jarPath) {
    Preconditions.checkArgument(null != jarPath);
    Preconditions.checkState(StartType.CLASS != startType, "manClass or classPath have already been set");
    Preconditions.checkState(null == this.jarPath, "jarPath has already been set");
    this.startType = StartType.JAR;
    this.jarPath = jarPath;
    return getThis();
  }

  @Override
  public ClassPathBuilder<T> inheritClassPath() {
    Preconditions.checkState(StartType.JAR != startType, "jarPath has already been set");
    Preconditions.checkState(null == this.classPath);
    this.startType = StartType.CLASS;
    return SimpleClassPathBuilder.inherit(getThis());
  }

  @Override
  public ClassPathBuilder<T> cleanClassPath() {
    Preconditions.checkState(StartType.JAR != startType, "jarPath has already been set");
    Preconditions.checkState(null == this.classPath);
    this.startType = StartType.CLASS;
    this.classPath = ImmutableList.of();
    return SimpleClassPathBuilder.clean(getThis());
  }

  @Override
  public T setClassPath(final List<String> classPath) {
    Preconditions.checkArgument(null != classPath);
    Preconditions.checkState(StartType.JAR != startType, "jarPath has already been set");
    Preconditions.checkState(null == this.classPath || this.classPath.isEmpty());
    this.startType = StartType.CLASS;
    this.classPath = classPath;
    return getThis();
  }

  @Override
  public T setMainClass(final Class<?> type) {
    Preconditions.checkArgument(null != type);
    return setMainClass(type.getName());
  }

  @Override
  public T setMainClass(final String type) {
    Preconditions.checkArgument(null != type);
    Preconditions.checkState(StartType.JAR != startType, "jarPath has already been set");
    this.startType = StartType.CLASS;
    this.mainClass = type;
    return getThis();
  }

  @Override
  public Process start() {
    return build().start();
  }

  public Path getJavaHome() {
    return javaHome;
  }

  public JvmType getJvmType() {
    return jvmType;
  }

  public JvmArchitecture getJvmArchitecture() {
    return jvmArchitecture;
  }

  public String getJvmVersion() {
    return jvmVersion;
  }

  public List<String> getClassPath() {
    return classPath;
  }

  public Map<String, String> getProperties() {
    return properties;
  }

  public Map<String, String> getEnvironment() {
    return environment;
  }

  public Status getAssertions() {
    return assertions;
  }

  public List<String> getAssertionParts() {
    return assertionParts;
  }

  public Status getSystemAssertions() {
    return systemAssertions;
  }

  public Set<Component> getVerboseComponents() {
    return verboseComponents;
  }

  public List<String> getJvmArguments() {
    return jvmArguments;
  }

  public List<String> getProgramArguments() {
    return programArguments;
  }

  public StartType getStartType() {
    return startType;
  }

  public Path getJarPath() {
    Preconditions.checkState(this.startType == StartType.JAR);
    return jarPath;
  }

  public String getMainClass() {
    Preconditions.checkState(this.startType == StartType.CLASS);
    return mainClass;
  }

  public SizeArgument getMaxHeapArgument() {
    return SizeParameter.MAX_HEAP.asArgument(maxHeapSize, maxHeapSizeUnit);
  }

  public SizeArgument getInitHeapArgument() {
    return SizeParameter.INITIAL_HEAP.asArgument(initHeapSize, initHeapSizeUnit);
  }

  public SizeArgument getStackArgument() {
    return SizeParameter.STACK_SIZE.asArgument(stackSize, stackSizeUnit);
  }

  public Iterable<SizeArgument> getSizeArguments() {
    return Iterables.filter(Arrays.asList(getMaxHeapArgument(), getInitHeapArgument(), getStackArgument()),
        Predicates.notNull());
  }

  @SuppressWarnings("unchecked")
  protected T getThis() {
    return (T) this;
  }
}
