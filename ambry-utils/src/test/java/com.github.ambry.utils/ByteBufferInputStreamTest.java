package com.github.ambry.utils;

import junit.framework.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Random;
import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class ByteBufferInputStreamTest {

  @Test
  public void byteBufferStreamTest()
      throws IOException {
    byte[] buf = new byte[1024];
    new Random().nextBytes(buf);
    ByteBufferInputStream stream = new ByteBufferInputStream(ByteBuffer.wrap(buf));
    for (int i = 0; i < 1024; i++) {
      Assert.assertEquals(stream.read(), (buf[i] & 0xFF));
    }
    ByteBufferInputStream stream1 = new ByteBufferInputStream(ByteBuffer.wrap(buf));
    byte[] outputBuf = new byte[500];
    stream1.read(outputBuf, 0, 500);
    for (int i = 0; i < 500; i++) {
      Assert.assertEquals(outputBuf[i], buf[i]);
    }

    stream1.read(outputBuf, 0, 500);
    for (int i = 500; i < 1000; i++) {
      Assert.assertEquals(outputBuf[i - 500], buf[i]);
    }
  }

  @Test
  public void markResetTest()
      throws IOException {
    byte[] buf = new byte[1024];
    new Random().nextBytes(buf);

    // Common case use
    ByteBufferInputStream stream = new ByteBufferInputStream(ByteBuffer.wrap(buf));
    assertTrue(stream.markSupported());
    stream.mark(1024);
    for (int i = 0; i < 1024; i++) {
      Assert.assertEquals(stream.read(), (buf[i] & 0xFF));
    }
    stream.reset();
    for (int i = 0; i < 1024; i++) {
      Assert.assertEquals(stream.read(), (buf[i] & 0xFF));
    }

    // Expect exception on reset afer reading beyond readLimit
    ByteBufferInputStream stream2 = new ByteBufferInputStream(ByteBuffer.wrap(buf));
    stream2.mark(1023);
    for (int i = 0; i < 1024; i++) {
      Assert.assertEquals(stream2.read(), (buf[i] & 0xFF));
    }
    try {
      stream2.reset();
      fail("stream reset should have thrown.");
    } catch (IOException e) {
      // Expected
    }

    // Expect exception on reset without mark being called.
    ByteBufferInputStream stream3 = new ByteBufferInputStream(ByteBuffer.wrap(buf));
    try {
      stream3.reset();
      fail("stream reset should have thrown.");
    } catch (IOException e) {
      // Expected
    }
  }
}
