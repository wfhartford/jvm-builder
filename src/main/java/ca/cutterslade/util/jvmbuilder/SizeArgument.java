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

  public int getSize() {
    return size;
  }

  public SizeUnit getUnit() {
    return unit;
  }

  public String getArgument() {
    return parameter.getArg(size, unit);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final SizeArgument that = (SizeArgument) o;
    return size == that.size && parameter == that.parameter && unit == that.unit;
  }

  @Override
  public int hashCode() {
    int result = size;
    result = 31 * result + unit.hashCode();
    result = 31 * result + parameter.hashCode();
    return result;
  }
}
