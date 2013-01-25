package ca.cutterslade.util.jvmbuilder;

public enum SizeUnit {
  BYTES("", 1),
  KILOBYTES("k", 1024),
  MEGABYTES("m", 1024 * 1024),
  GIGABYTES("g", 1024 * 1024 * 1024);

  private final String suffix;
  private final int bytes;

  SizeUnit(final String suffix, final int bytes) {
    this.suffix = suffix;
    this.bytes = bytes;
  }

  String getSuffix() {
    return suffix;
  }

  int toBytes(final int size) {
    return size * bytes;
  }

  int fromBytes(final int size) {
    return size / bytes;
  }

  int convert(final int size, final SizeUnit unit) {
    return fromBytes(unit.toBytes(size));
  }
}
