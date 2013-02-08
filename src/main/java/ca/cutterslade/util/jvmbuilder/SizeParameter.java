package ca.cutterslade.util.jvmbuilder;

import javax.annotation.concurrent.Immutable;

@Immutable
public enum SizeParameter {
  MAX_HEAP("-Xmx%d%s"),
  INITIAL_HEAP("-Xms%d%s"),
  STACK_SIZE("-Xss%d%s");

  private final String format;

  SizeParameter(final String format) {
    this.format = format;
  }

  public SizeArgument asArgument(final int size, final SizeUnit unit) {
    return null == unit ? null : new SizeArgument(size, unit, this);
  }

  String getArg(final int size, final SizeUnit unit) {
    return String.format(format, size, unit.getSuffix());
  }
}
