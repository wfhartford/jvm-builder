package ca.cutterslade.util.jvmbuilder.sun;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import ca.cutterslade.util.jvmbuilder.ReaderThread;
import ca.cutterslade.util.jvmbuilder.SizeUnit;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;

public class SimpleCreateJvmTest {

  public static final String TEST_STRING = "Hello, World!";

  public static final class TestMain {
    public static void main(final String[] args) {
      System.out.println(TEST_STRING);
    }
  }

  public static final class MaxHeapMain {
    public static void main(final String[] args) {
      System.out.println(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax());
    }
  }

  public static final class PrintArgumentMain {
    public static void main(String[] args) {
      System.out.println(Joiner.on(' ').join(args));
    }
  }

  public static final class PringPropertyMain {
    public static void main(String[] args) {
      System.out.println(System.getProperty("test.string"));
    }
  }

  @Test(timeout = 10_000)
  public void testStart() throws IOException, InterruptedException {
    Assert.assertEquals(TEST_STRING + System.lineSeparator(), getStdOut(getBuilder(TestMain.class)));
  }

  @Test(timeout = 10_000)
  public void testAdjustMaxHeap() throws InterruptedException, IOException {
    final String stdOut = getStdOut(getBuilder(MaxHeapMain.class)
        .setMaxHeapSpace(256, SizeUnit.MEGABYTES));
    Assert.assertEquals("238616576" + System.lineSeparator(), stdOut);
  }

  @Test(timeout = 10_000)
  public void testArgument() throws IOException, InterruptedException {
    final String stdOut = getStdOut(getBuilder(PrintArgumentMain.class)
        .setProgramArguments(Arrays.asList("hello", "world")));
    Assert.assertEquals("hello world" + System.lineSeparator(), stdOut);
  }

  @Test(timeout = 10_000)
  public void testProperty() throws IOException, InterruptedException {
    final String stdOut = getStdOut(getBuilder(PringPropertyMain.class)
        .setProperties(ImmutableMap.of("test.string", TEST_STRING)));
    Assert.assertEquals(TEST_STRING + System.lineSeparator(), stdOut);
  }

  private static SunJvmFactoryBuilder getBuilder(final Class<?> mainClass) {
    return new SunJvmFactoryBuilder().setMainClass(mainClass);
  }

  private static String getStdOut(final SunJvmFactoryBuilder builder) throws IOException, InterruptedException {
    final Process process = builder.start();
    final ReaderThread stdOut = new ReaderThread(process.getInputStream());
    stdOut.start();
    final ReaderThread stdErr = new ReaderThread(process.getErrorStream());
    stdErr.start();
    process.waitFor();
    stdOut.join();
    stdErr.join();
    Assert.assertEquals(0, process.exitValue());
    Assert.assertEquals("", stdErr.getResult());
    Assert.assertNull(stdErr.getThrowable());
    Assert.assertNull(stdOut.getThrowable());
    return stdOut.getResult();
  }
}
