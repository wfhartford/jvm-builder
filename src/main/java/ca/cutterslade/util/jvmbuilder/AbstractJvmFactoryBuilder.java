package ca.cutterslade.util.jvmbuilder;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

public abstract class AbstractJvmFactoryBuilder<T extends AbstractJvmFactoryBuilder<T>>
    implements JvmFactoryBuilder<T> {
  private enum StartType {
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
    Preconditions.checkState(null != this.jvmArchitecture);
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
    this.assertions = Status.ENABLED;
    return SimpleScopeBuilder.enable(getThis());
  }

  @Override
  public ScopeBuilder<T> disableAssertions() {
    this.assertions = Status.DISABLED;
    return SimpleScopeBuilder.disable(getThis());
  }

  @Override
  public T setAssertions(final Status status, final List<String> parts) {
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
    this.systemAssertions = status;
    return getThis();
  }

  @Override
  public T verbose(final Component... components) {
    this.verboseComponents =
        Sets.immutableEnumSet(Arrays.asList(0 == components.length ? Component.values() : components));
    return getThis();
  }

  @Override
  public T jar(final Path jarPath) {
    Preconditions.checkState(StartType.CLASS != startType, "parameters conflicting with jar have already been set");
    Preconditions.checkState(null == this.jarPath, "jarPath has already been set");
    this.startType = StartType.JAR;
    this.jarPath = jarPath;
    return getThis();
  }

  @Override
  public ClassPathBuilder<T> inheritClassPath() {
    Preconditions.checkState(StartType.JAR != startType, "jarPath has already been set");
    this.startType = StartType.CLASS;
    return SimpleClassPathBuilder.inherit(getThis());
  }

  @Override
  public ClassPathBuilder<T> cleanClassPath() {
    Preconditions.checkState(StartType.JAR != startType, "jarPath has already been set");
    this.startType = StartType.CLASS;
    this.classPath = ImmutableList.of();
    return SimpleClassPathBuilder.clean(getThis());
  }

  @Override
  public T setClassPath(final List<String> classPath) {
    Preconditions.checkState(StartType.JAR != startType, "jarPath has already been set");
    this.startType = StartType.CLASS;
    this.classPath = classPath;
    return getThis();
  }

  @Override
  public MapBuilder<T> inheritProperties() {
    return SimpleMapBuilder.inheritProperties(getThis());
  }

  @Override
  public MapBuilder<T> cleanProperties() {
    this.properties = ImmutableMap.of();
    return SimpleMapBuilder.cleanProperties(getThis());
  }

  @Override
  public T setProperties(final Map<String, String> properties) {
    this.properties = properties;
    return getThis();
  }

  @Override
  public MapBuilder<T> inheritEnvironment() {
    return SimpleMapBuilder.inheritEnvironment(getThis());
  }

  @Override
  public MapBuilder<T> cleanEnvironment() {
    this.environment = ImmutableMap.of();
    return SimpleMapBuilder.cleanEnvironment(getThis());
  }

  @Override
  public T setEnvironment(final Map<String, String> environment) {
    this.environment = environment;
    return getThis();
  }

  @Override
  public ArgumentsBuilder<T> inheritJvmArguments() {
    return SimpleArgumentsBuilder.inheritJvmArguments(getThis());
  }

  @Override
  public ArgumentsBuilder<T> cleanJvmArguments() {
    this.jvmArguments = ImmutableList.of();
    return SimpleArgumentsBuilder.cleanJvmArguments(getThis());
  }

  @Override
  public T setJvmArguments(final List<String> arguments) {
    this.jvmArguments = ImmutableList.copyOf(arguments);
    return getThis();
  }

  @Override
  public ArgumentsBuilder<T> programArguments() {
    return SimpleArgumentsBuilder.cleanProgramArguments(getThis());
  }

  @Override
  public T setProgramArguments(final List<String> arguments) {
    this.programArguments = arguments;
    return getThis();
  }

  @Override
  public T setMaxHeapSpace(final int size, final SizeUnit unit) {
    maxHeapSize = size;
    maxHeapSizeUnit = unit;
    return getThis();
  }

  @Override
  public T setInitialHeapSpace(final int size, final SizeUnit unit) {
    initHeapSize = size;
    initHeapSizeUnit = unit;
    return getThis();
  }

  @Override
  public T setStackSize(final int size, final SizeUnit unit) {
    stackSize = size;
    stackSizeUnit = unit;
    return getThis();
  }

  @Override
  public T setMainClass(final Class<?> type) {
    Preconditions.checkState(StartType.JAR != startType, "jarPath has already been set");
    this.startType = StartType.CLASS;
    throw new UnsupportedOperationException("not yet implemented");
  }

  @Override
  public T setMainClass(final String type) {
    Preconditions.checkState(StartType.JAR != startType, "jarPath has already been set");
    this.startType = StartType.CLASS;
    throw new UnsupportedOperationException("not yet implemented");
  }

  @Override
  public JvmFactory build() {
    throw new UnsupportedOperationException("not yet implemented");
  }

  @Override
  public Process start() {
    return build().start();
  }

  private T getThis() {
    return (T) this;
  }
}
