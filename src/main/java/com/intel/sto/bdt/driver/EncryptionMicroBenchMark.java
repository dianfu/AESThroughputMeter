package com.intel.sto.bdt.driver;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.crypto.*;

import java.io.*;
import java.util.*;

/**
 * Created by root on 1/19/15.
 */
public class EncryptionMicroBenchMark{
  private int currentTime = 0;
  private int dataSize = 0;
  private int executionTimes = 0;
  private long begin = 0;
  private long end = 0;

  private boolean isCompleted = false;

  private String keyProviderName = "org.apache.hadoop.crypto.OpensslAesCtrCryptoCodec";

  public EncryptionMicroBenchMark(int dataSize, int executionTimes, String keyProviderName) {
    this.dataSize = dataSize;
    this.executionTimes = executionTimes;
    this.keyProviderName = keyProviderName;
  }

  public byte[] prepareData() {
    byte[] data = new byte[dataSize];
    Random r = new Random();
    r.nextBytes(data);
    return data;
  }

  private CryptoCodec initialCryptoCodec() {
    Configuration conf = new Configuration();
    conf.set("hadoop.security.crypto.codec.classes.aes.ctr.nopadding", keyProviderName);
    CryptoCodec codec = CryptoCodec.getInstance(conf, CipherSuite.AES_CTR_NOPADDING);
    return codec;
  }

  public CryptoInputStream initialCryptoInputStream(InputStream inputStream) throws IOException {
    CryptoCodec codec = initialCryptoCodec();
    Random r = new Random();
    byte[] iv = new byte[16];
    byte[] key = new byte[16];
    CryptoInputStream cryptoInputStream =
      new CryptoInputStream(inputStream, codec, dataSize, key, iv);
    return cryptoInputStream;
  }

  public boolean isCompleted() {
    return isCompleted;
  }

  public double getPercentage() {
    return ((double) currentTime / (double) executionTimes);
  }

  public long getExecutedTime() {
    return end - begin;
  }

  public double getAverageThroughput() {
    return 1000.0 * currentTime * dataSize / ((end - begin) * 1024.0 * 1024.0);
  }

  public void encryptionThroughputTest() {
    try {
      byte[] data = prepareData();
      ByteArrayInputStream inputStream = new ByteArrayInputStream(prepareData());
      CryptoInputStream cryptoInputStream = initialCryptoInputStream(inputStream);
      begin = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();

      for (currentTime = 0; currentTime < executionTimes; currentTime++) {
        cryptoInputStream.read(data, 0, dataSize);
        inputStream.reset();
        end = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
      }
      System.out.println("Complete !!");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    isCompleted = true;
  }
}
