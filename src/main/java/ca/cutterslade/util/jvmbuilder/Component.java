package ca.cutterslade.util.jvmbuilder;

import java.util.Locale;

import javax.annotation.concurrent.Immutable;

@Immutable
public enum Component {
  CLASS, GC, JNI;

  private final String argument = "-verbose:" + name().toLowerCase(Locale.US);

  public String getArgument() {
    return argument;
  }
}