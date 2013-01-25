package ca.cutterslade.util.jvmbuilder;

import java.nio.file.Path;
import java.util.Map;

public interface JvmBuilder<T extends JvmBuilder<T>> {
  T setJavaHome(Path javaHome);

  ClassPathBuilder<T> inheritClassPath();

  ClassPathBuilder<T> cleanClassPath();

  T putEnvironment(String key, String value);

  T setEnvironment(Map<String, String> environment);

  T putProperty(String key, String value);

  T setProperties(Map<String, String> properties);

  T setMaxHeapSpace(final int size, final SizeUnit unit);

  T setInitialHeapSpace(final int size, final SizeUnit unit);

  T setStackSize(final int size, final SizeUnit unit);

  Process start();
}
