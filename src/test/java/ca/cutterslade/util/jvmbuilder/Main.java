package ca.cutterslade.util.jvmbuilder;

import java.lang.management.ManagementFactory;

public class Main {
  public static void main(String[] args) {
    System.out.println(ManagementFactory.getRuntimeMXBean().getInputArguments());
  }
}
