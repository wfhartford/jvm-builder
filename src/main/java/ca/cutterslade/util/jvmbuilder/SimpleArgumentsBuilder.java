package ca.cutterslade.util.jvmbuilder;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.collect.Lists;

@NotThreadSafe
public class SimpleArgumentsBuilder<T extends JvmFactoryBuilder<T>> implements ArgumentsBuilder<T> {
  public enum Type {
    JVM {
      @Override
      void set(final SimpleArgumentsBuilder<?> builder) {
        builder.builder.setJvmArguments(builder.arguments);
      }
    }, PROGRAM {
      @Override
      void set(final SimpleArgumentsBuilder<?> builder) {
        builder.builder.setProgramArguments(builder.arguments);
      }
    };

    abstract void set(SimpleArgumentsBuilder<?> builder);
  }

  public static <T extends JvmFactoryBuilder<T>> ArgumentsBuilder<T> inheritJvmArguments(final T builder) {
    return new SimpleArgumentsBuilder<>(builder, Type.JVM, ManagementFactory.getRuntimeMXBean().getInputArguments());
  }

  public static <T extends JvmFactoryBuilder<T>> ArgumentsBuilder<T> cleanJvmArguments(final T builder) {
    return new SimpleArgumentsBuilder<>(builder, Type.JVM, null);
  }

  public static <T extends JvmFactoryBuilder<T>> ArgumentsBuilder<T> cleanProgramArguments(final T builder) {
    return new SimpleArgumentsBuilder<>(builder, Type.PROGRAM, null);
  }

  private final T builder;
  private final Type type;
  private final List<String> arguments;

  public SimpleArgumentsBuilder(final T builder, final Type type, final List<String> arguments) {
    this.builder = builder;
    this.type = type;
    this.arguments = null == arguments ? Lists.<String>newArrayList() : Lists.newArrayList(arguments);
  }

  @Override
  public ArgumentsBuilder<T> addArguments(final String... arguments) {
    this.arguments.addAll(Arrays.asList(arguments));
    return this;
  }

  @Override
  public T build() {
    type.set(this);
    return builder;
  }

}
