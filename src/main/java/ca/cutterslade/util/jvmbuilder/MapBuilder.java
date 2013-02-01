package ca.cutterslade.util.jvmbuilder;

public interface MapBuilder<T extends JvmFactoryBuilder<T>> {
  MapBuilder<T> set(String key, String value);

  MapBuilder<T> remove(String key);

  T build();
}
