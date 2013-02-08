package ca.cutterslade.util.jvmbuilder;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Preconditions;

@Immutable
public final class SizeArgument {
  private final int size;
  private final SizeUnit unit;
  private final SizeParameter parameter;

  public SizeArgument(final int size, final SizeUnit unit, final SizeParameter parameter) {
    Preconditions.checkArgument(0 < size);
    Preconditions.checkArgument(null != unit);
    Preconditions.checkArgument(null != parameter);
    this.size = size;
    this.unit = unit;
    this.parameter = parameter;
  }

  public String getArgument() {
    return parameter.getArg(size, unit);
  }
}
