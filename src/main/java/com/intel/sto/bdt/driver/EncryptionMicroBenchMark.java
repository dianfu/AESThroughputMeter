package com.intel.sto.bdt.driver;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.crypto.*;

import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

/**
 * Created by root on 1/19/15.
 */
public class EncryptionMicroBenchMark {
  private int currentIteration = 0;
  ThroughputParameters parameters;
  private long begin = 0;
  private long end = 0;

  private boolean isCompleted = false;

  private boolean isStarted = false;

  public EncryptionMicroBenchMark(ThroughputParameters parameters) {
    this.parameters = parameters;
  }

  public byte[] prepareData() {
    if(parameters.isFileBased()){
      return extractBytesFromPic(parameters.getFileName());
    }else{
      return extractBytesByRandom();
    }
  }

  public byte[] extractBytesByRandom() {
    byte[] data = new byte[parameters.getDataSize()];
    Random r = new Random();
    r.nextBytes(data);
    return data;
  }

  public byte[] extractBytesFromPic(String ImageName){
    // open image
    File imgPath = new File(ImageName);

    
    try {
      FileInputStream is = new FileInputStream(imgPath);
      byte[] picData = new byte[(int)imgPath.length()];
      readFully(is, picData, 0, picData.length);
      parameters.setDataSize(picData.length);
      return picData;
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  public static void readFully(InputStream in, byte buf[],
      int off, int len) throws IOException {
    int toRead = len;
    while (toRead > 0) {
      int ret = in.read(buf, off, toRead);
      if (ret < 0) {
        throw new IOException( "Premature EOF from inputStream");
      }
      toRead -= ret;
      off += ret;
    }
  }

  private CryptoCodec initialCryptoCodec() {
    Configuration conf = new Configuration();
    conf.set("hadoop.security.crypto.codec.classes.aes.ctr.nopadding",
      parameters.getKeyProviderName());
    CryptoCodec codec = CryptoCodec.getInstance(conf, CipherSuite.AES_CTR_NOPADDING);
    return codec;
  }

  private CryptoOutputStream initialCryptoOutputStream(OutputStream outputStream) throws IOException {
    CryptoCodec codec = initialCryptoCodec();
    byte[] iv = new byte[16];
    byte[] key = new byte[16];
    CryptoOutputStream cryptoOutputStream =
      new CryptoOutputStream(outputStream, codec, parameters.getDataSize(), key, iv);
    return cryptoOutputStream;
  }

  private CryptoInputStream initialCryptoInputStream(InputStream inputStream) throws IOException {
    CryptoCodec codec = initialCryptoCodec();
    byte[] iv = new byte[16];
    byte[] key = new byte[16];
    CryptoInputStream cryptoInputStream =
      new CryptoInputStream(inputStream, codec, parameters.getDataSize(), key, iv);
    return cryptoInputStream;
  }

  public boolean isCompleted() {
    return isCompleted;
  }

  public double getPercentage() {
    if (isStarted) {
      return ((double) currentIteration / (double) parameters.getIterations());
    } else {
      return 0;
    }
  }

  public long getExecutedTime() {
    if (isStarted) {
      end = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
      return end - begin;
    } else {
      return 0;
    }
  }

  public double getAverageThroughput() {
    if (isStarted) {
      end = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
      /*System.out.println("iterations: " + currentTime);
      System.out.println("data size: " + parameters.getDataSize());
      System.out.println("time used: " + (end-begin) / 1000.0);*/
      return 1000.0 * currentIteration * parameters.getDataSize() / ((end - begin) * 1024.0 * 1024.0);
    } else {
      return 0.00;
    }
  }

  public void encryptionThroughputTest() {
    try {
      byte[] data = prepareData();

      int entryCount = 280;
      byte[][] datas = new byte[entryCount][data.length];
      CryptoOutputStream[] cryptoOutputStreams = new CryptoOutputStream[entryCount];
      ByteArrayOutputStream[] outputStreams = new ByteArrayOutputStream[entryCount];
      for (int i = 0; i < entryCount; i++) {
        datas[i] = data.clone();
        outputStreams[i] = new ByteArrayOutputStream(data.length);
        cryptoOutputStreams[i] = initialCryptoOutputStream(outputStreams[i]);
      }

      System.out.println("encryptionThroughputTest:");
      System.out.println("data size: " + parameters.getDataSize());
      System.out.println("file name: " + parameters.getFileName());
      System.out.println("execute times: " + parameters.getIterations());

      //warming up
      for (int i = 0; i < 10000; i++) {
        int entryPos = i % entryCount;
        cryptoOutputStreams[entryPos].write(datas[entryPos], 0, datas[entryPos].length);
        outputStreams[entryPos].reset();
      }

      begin = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
      isStarted = true;
      for (currentIteration = 0; currentIteration < parameters.getIterations(); currentIteration++) {
        int entryPos = currentIteration % entryCount;
        cryptoOutputStreams[entryPos].write(datas[entryPos], 0, datas[entryPos].length);
        outputStreams[entryPos].reset();
      }
      System.out.println("Complete !!");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    isCompleted = true;
  }

  public void decryptionThroughputTest() {
    try {
      
      byte[] data = prepareData();

      int entryCount = 280;
      byte[][] datas = new byte[entryCount][data.length];
      byte[][] destDatas = new byte[entryCount][data.length];
      CryptoInputStream[] cryptoInputStreams = new CryptoInputStream[entryCount];
      ByteArrayInputStream[] inputStreams = new ByteArrayInputStream[entryCount];
      for (int i = 0; i < entryCount; i++) {
        datas[i] = data.clone();
        inputStreams[i] = new ByteArrayInputStream(datas[i]);
        cryptoInputStreams[i] = initialCryptoInputStream(inputStreams[i]);
      }

      System.out.println("decryptionThroughputTest");
      System.out.println("data size: " + parameters.getDataSize());
      System.out.println("file name: " + parameters.getFileName());
      System.out.println("execute times: " + parameters.getIterations());
      
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      CryptoOutputStream cryptoOutputStream = initialCryptoOutputStream(outputStream);

      //warming up
      for (int i = 0; i < 10000; i++) {
        cryptoOutputStream.write(data, 0, data.length);
        outputStream.reset();
      }

      begin = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
      isStarted = true;
      for (currentIteration = 0; currentIteration < parameters.getIterations(); currentIteration++) {
        int entryPos = currentIteration % entryCount;
        cryptoInputStreams[entryPos].read(destDatas[entryPos], 0, destDatas[entryPos].length);
        inputStreams[entryPos].reset();
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
