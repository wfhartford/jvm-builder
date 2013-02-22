package ca.cutterslade.util.jvmbuilder;

import javax.annotation.concurrent.Immutable;

@Immutable
public enum JvmType {
  CLIENT("-client"), SERVER("-server");
  private String argument;

  JvmType(final String argument) {
    this.argument = argument;
  }

  public String getArgument() {
    return argument;
  }
}
