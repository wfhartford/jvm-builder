package ca.cutterslade.util.jvmbuilder;

import javax.annotation.concurrent.Immutable;

@Immutable
public enum JvmArchitecture {
  ARCH_32_BIT("-d32"), ARCH_64_BIT("-d64");
  private final String argument;

  JvmArchitecture(final String argument) {
    this.argument = argument;
  }

  public String getArgument() {
    return argument;
  }
}
