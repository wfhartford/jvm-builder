package ca.cutterslade.util.jvmbuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class ReaderThread extends Thread {
  private final Object mutex = new Object();
  private final Reader reader;

  private String result;

  private Throwable throwable;

  public ReaderThread(final Reader reader) {
    this.reader = reader;
  }

  public ReaderThread(final InputStream inputStream) {
    this.reader = new InputStreamReader(inputStream);
  }

  @Override
  public void run() {
    try {
      final StringBuilder builder = new StringBuilder();
      int ch;
      try {
        while (-1 != (ch = reader.read())) {
          builder.append((char) ch);
        }
        synchronized (mutex) {
          result = builder.toString();
        }
      }
      finally {
        reader.close();
      }
    }
    catch (Throwable t) {
      synchronized (mutex) {
        throwable = t;
      }
    }
  }

  public String getResult() {
    synchronized (mutex) {
      return result;
    }
  }

  public Throwable getThrowable() {
    synchronized (mutex) {
      return throwable;
    }
  }
}
