package ca.cutterslade.util.jvmbuilder;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public interface ScopeBuilder<T extends JvmFactoryBuilder<T>> {

  ScopeBuilder<T> includePackage(String packageName);

  ScopeBuilder<T> includePackage(Package pack);

  ScopeBuilder<T> includeClass(Class<?> type);

  ScopeBuilder<T> includeClass(String className);

  T build();
}
