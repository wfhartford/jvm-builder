package ca.cutterslade.util.jvmbuilder.sun;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import ca.cutterslade.util.jvmbuilder.ReaderThread;
import ca.cutterslade.util.jvmbuilder.SizeUnit;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;

public class SimpleCreateJvmTest {

  public static final String TEST_STRING = "Hello, World!";
  public static final String TEST_STRING_KEY = "TEST_STRING";

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

  public static final class PrintPropertyMain {
    public static void main(String[] args) {
      System.out.println(System.getProperty(TEST_STRING_KEY));
    }
  }

  public static final class PrintEnvironmentMain {
    public static void main(String[] args) {
      System.out.println(System.getenv(TEST_STRING_KEY));
    }
  }

  public static final class AssertFalseMain {
    public static void main(String[] args) {
      assert false;
    }
  }

  public static final class WorkingDirectoryMain {
    public static void main(String[] args) throws IOException {
      System.out.println(new File(".").getCanonicalPath());
    }
  }

  public static final class JvmTypeMain {
    public static void main(String[] args) {
      System.out.println(ManagementFactory.getRuntimeMXBean().getVmName());
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
    final String stdOut = getStdOut(getBuilder(PrintPropertyMain.class)
        .setProperties(ImmutableMap.of(TEST_STRING_KEY, TEST_STRING)));
    Assert.assertEquals(TEST_STRING + System.lineSeparator(), stdOut);
  }

  @Test(timeout = 10_000)
  public void testEnvironment() throws IOException, InterruptedException {
    final String stdOut = getStdOut(getBuilder(PrintEnvironmentMain.class)
        .setEnvironment(ImmutableMap.of(TEST_STRING_KEY, TEST_STRING)));
    Assert.assertEquals(TEST_STRING + System.lineSeparator(), stdOut);
  }

  @Test(timeout = 10_000)
  public void testAssertionsDisabled() throws IOException, InterruptedException {
    final String stdOut = getStdOut(getBuilder(AssertFalseMain.class).disableAssertions().build());
    Assert.assertEquals("", stdOut);
  }

  @Test(timeout = 10_000)
  public void testAssertionsEnabled() throws IOException, InterruptedException {
    final String stdOut = getStdOut(getBuilder(AssertFalseMain.class).enableAssertions().build(), 1,
        Pattern.compile("(?s)Exception in thread \"main\" java\\.lang\\.AssertionError.*"));
    Assert.assertEquals("", stdOut);
  }

  @Test(timeout = 10_000)
  public void testWorkingDirectory() throws IOException, InterruptedException {
    final File tempDir = Files.createTempDir();
    try {
      final String stdOut = getStdOut(getBuilder(WorkingDirectoryMain.class).setWorkingDirectory(tempDir.toPath()));
      Assert.assertEquals(tempDir.getCanonicalPath() + System.lineSeparator(), stdOut);
    }
    finally {
      tempDir.delete();
    }
  }

  @Test(timeout = 10_000)
  public void testServerVm() throws IOException, InterruptedException {
    final String stdOut = getStdOut(getBuilder(JvmTypeMain.class).server());
    Assert.assertTrue("Expected 'server' in stdOut: " + stdOut, stdOut.toLowerCase().contains("server"));
  }

  @Test(timeout = 10_000)
  @Ignore // Many VMs don't ship with a client implementation depending on target OS and architecture
  public void testClientVm() throws IOException, InterruptedException {
    final String stdOut = getStdOut(getBuilder(JvmTypeMain.class).client());
    Assert.assertTrue("Expected 'client' in stdOut: " + stdOut, stdOut.toLowerCase().contains("client"));
  }

  @Test(timeout = 10_000)
  public void testRequire32Bit() throws IOException, InterruptedException {
    final boolean current32Bit = "32".equals(System.getProperty("sun.arch.data.model"));
    final SunJvmFactoryBuilder builder = getBuilder(TestMain.class).require32Bit();
    final String stdOut = current32Bit ? getStdOut(builder) : getStdOut(builder, 1,
        Pattern.compile("(?s)Error: This Java instance does not support a 32-bit JVM\\..*"));
    Assert.assertEquals(current32Bit ? TEST_STRING + System.lineSeparator() : "", stdOut);
  }

  @Test(timeout = 10_000)
  public void testRequire64Bit() throws IOException, InterruptedException {
    final boolean current64Bit = "64".equals(System.getProperty("sun.arch.data.model"));
    final SunJvmFactoryBuilder builder = getBuilder(TestMain.class).require64Bit();
    final String stdOut = current64Bit ? getStdOut(builder) : getStdOut(builder, 1,
        Pattern.compile("(?s)Error: This Java instance does not support a 64-bit JVM\\..*"));
    Assert.assertEquals(current64Bit ? TEST_STRING + System.lineSeparator() : "", stdOut);
  }

  private static SunJvmFactoryBuilder getBuilder(final Class<?> mainClass) {
    return new SunJvmFactoryBuilder().setMainClass(mainClass);
  }

  private static String getStdOut(final SunJvmFactoryBuilder builder) throws IOException, InterruptedException {
    return getStdOut(builder, 0, null);
  }

  private static String getStdOut(final SunJvmFactoryBuilder builder, final int exitValue, final Pattern stdErrPattern)
      throws IOException, InterruptedException {
    final Process process = builder.start();
    final ReaderThread stdOut = new ReaderThread(process.getInputStream());
    stdOut.start();
    final ReaderThread stdErr = new ReaderThread(process.getErrorStream());
    stdErr.start();
    process.waitFor();
    stdOut.join();
    stdErr.join();
    Assert.assertEquals(exitValue, process.exitValue());
    if (null == stdErrPattern) {
      Assert.assertEquals("", stdErr.getResult());
    }
    else {
      Assert.assertTrue("stdErr does not match expected pattern: " + stdErr.getResult(),
          stdErrPattern.matcher(stdErr.getResult()).matches());
    }
    Assert.assertNull(stdErr.getThrowable());
    Assert.assertNull(stdOut.getThrowable());
    return stdOut.getResult();
  }
}
