package ca.cutterslade.util.jvmbuilder;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public interface JvmFactoryBuilder<T extends JvmFactoryBuilder<T>> {
  T setJavaHome(Path javaHome);

  T client();

  T server();

  T setJvmType(final JvmType client);

  T require32Bit();

  T require64Bit();

  T requireArchitecture(JvmArchitecture architecture);

  T requireVersion(String release);

  ScopeBuilder<T> enableAssertions();

  ScopeBuilder<T> disableAssertions();

  T setAssertions(Status status, final List<String> parts);

  T enableSystemAssertions();

  T disableSystemAssertions();

  T setSystemAssertions(Status status);

  T verbose(Component... components);

  T jar(Path jarFile);

  ClassPathBuilder<T> inheritClassPath();

  ClassPathBuilder<T> cleanClassPath();

  T setClassPath(List<String> entries);

  MapBuilder<T> inheritProperties();

  MapBuilder<T> cleanProperties();

  T setProperties(Map<String, String> properties);

  MapBuilder<T> inheritEnvironment();

  MapBuilder<T> cleanEnvironment();

  T setEnvironment(Map<String, String> environment);

  ArgumentsBuilder<T> inheritJvmArguments();

  ArgumentsBuilder<T> cleanJvmArguments();

  T setJvmArguments(List<String> arguments);

  ArgumentsBuilder<T> programArguments();

  T setProgramArguments(List<String> arguments);

  T setMaxHeapSpace(int size, SizeUnit unit);

  T setInitialHeapSpace(int size, SizeUnit unit);

  T setStackSize(int size, SizeUnit unit);

  T setMainClass(Class<?> type);

  T setMainClass(String type);

  JvmFactory<T> build();

  Process start();

}
