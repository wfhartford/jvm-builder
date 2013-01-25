package ca.cutterslade.util.jvmbuilder;

public enum SizeArgument {
  MAX_HEAP("-Xmx%d%s"),
  INITIAL_HEAP("-Xms%d%s"),
  STACK_SIZE("-Xss%d%s");

  private final String format;

  SizeArgument(final String format) {
    this.format = format;
  }

  String getArg(final int size, final SizeUnit unit) {
    return String.format(format, size, unit.getSuffix());
  }
}
