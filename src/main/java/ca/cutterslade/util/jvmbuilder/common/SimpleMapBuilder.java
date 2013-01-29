package ca.cutterslade.util.jvmbuilder.common;

import java.util.Map;

import ca.cutterslade.util.jvmbuilder.JvmFactoryBuilder;
import ca.cutterslade.util.jvmbuilder.MapBuilder;

import com.google.common.collect.Maps;

public class SimpleMapBuilder<T extends JvmFactoryBuilder<T>> implements MapBuilder<T> {
  public enum Type {
    PROPERTIES {
      @Override
      Map<String, String> inherit() {
        return Maps.newHashMap(Maps.fromProperties(System.getProperties()));
      }

      @Override
      <T extends JvmFactoryBuilder<T>> T setBuilder(final SimpleMapBuilder<T> builder) {
        return builder.builder.setProperties(builder.map);
      }
    },
    ENVIRONMENT {
      @Override
      Map<String, String> inherit() {
        return System.getenv();
      }

      @Override
      <T extends JvmFactoryBuilder<T>> T setBuilder(final SimpleMapBuilder<T> builder) {
        return builder.builder.setEnvironment(builder.map);
      }
    };

    abstract <T extends JvmFactoryBuilder<T>> T setBuilder(final SimpleMapBuilder<T> builder);

    abstract Map<String, String> inherit();
  }

  public static <T extends JvmFactoryBuilder<T>> SimpleMapBuilder<T> cleanProperties(final T builder) {
    return clean(builder, Type.PROPERTIES);
  }

  public static <T extends JvmFactoryBuilder<T>> SimpleMapBuilder<T> inheritProperties(final T builder) {
    return inherit(builder, Type.PROPERTIES);
  }

  public static <T extends JvmFactoryBuilder<T>> SimpleMapBuilder<T> cleanEnvironment(final T builder) {
    return clean(builder, Type.ENVIRONMENT);
  }

  public static <T extends JvmFactoryBuilder<T>> SimpleMapBuilder<T> inheritEnvironment(final T builder) {
    return inherit(builder, Type.ENVIRONMENT);
  }

  static <T extends JvmFactoryBuilder<T>> SimpleMapBuilder<T> clean(final T builder, final Type type) {
    return new SimpleMapBuilder<>(builder, Maps.<String, String>newHashMap(), type);
  }

  static <T extends JvmFactoryBuilder<T>> SimpleMapBuilder<T> inherit(final T builder, final Type type) {
    return new SimpleMapBuilder<>(builder, type.inherit(), type);
  }

  private final JvmFactoryBuilder<T> builder;
  private final Map<String, String> map;
  private final Type type;

  private SimpleMapBuilder(final JvmFactoryBuilder<T> builder, final Map<String, String> map, final Type type) {
    this.builder = builder;
    this.map = map;
    this.type = type;
  }

  @Override
  public MapBuilder<T> set(final String key, final String value) {
    map.put(key, value);
    return this;
  }

  @Override
  public MapBuilder<T> remove(final String key) {
    map.remove(key);
    return this;
  }

  @Override
  public JvmFactoryBuilder<T> build() {
    return type.setBuilder(this);
  }
}
