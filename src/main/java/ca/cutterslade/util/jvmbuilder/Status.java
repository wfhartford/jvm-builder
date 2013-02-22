package ca.cutterslade.util.jvmbuilder;

import javax.annotation.concurrent.Immutable;

@Immutable
public enum Status {
  ENABLED("-ea", "-esa"), DISABLED("-da", "-dsa");
  private final String programArgument;
  private final String systemArgument;

  Status(final String programArgument, final String systemArgument) {
    this.programArgument = programArgument;
    this.systemArgument = systemArgument;
  }

  public String getProgramArgument() {
    return programArgument;
  }

  public String getSystemArgument() {
    return systemArgument;
  }
}
