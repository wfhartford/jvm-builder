package ca.cutterslade.util.jvmbuilder.sun;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import ca.cutterslade.util.jvmbuilder.ReaderThread;

public class SimpleCreateJvmTest {

  public static final String TEST_STRING = "Hello, World!";

  public static final class TestMain {
    public static void main(final String[] args) {
      System.out.println(TEST_STRING);
    }
  }

  @Test(timeout = 10_000)
  public void testStart() throws IOException, InterruptedException {
    final SunJvmFactoryBuilder builder = new SunJvmFactoryBuilder();
    builder.setMainClass(TestMain.class);
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
    Assert.assertEquals(TEST_STRING + System.lineSeparator(), stdOut.getResult());
    Assert.assertNull(stdOut.getThrowable());
  }
}
