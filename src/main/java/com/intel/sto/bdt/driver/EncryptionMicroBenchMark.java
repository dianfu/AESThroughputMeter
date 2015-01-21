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
  private int currentTime = 0;
  ThroughputParameters parameters;
  private long begin = 0;

  private long end = 0;

  private boolean isCompleted = false;

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
    BufferedImage bufferedImage = null;
    try {
      bufferedImage = ImageIO.read(imgPath);
    } catch (IOException e) {
      e.printStackTrace();
    }

    // get DataBufferBytes from Raster
    WritableRaster raster = bufferedImage.getRaster();
    DataBufferByte data = (DataBufferByte) raster.getDataBuffer();

    return data.getData();
  }

  private CryptoCodec initialCryptoCodec() {
    Configuration conf = new Configuration();
    conf.set("hadoop.security.crypto.codec.classes.aes.ctr.nopadding",
      parameters.getKeyProviderName());
    CryptoCodec codec = CryptoCodec.getInstance(conf, CipherSuite.AES_CTR_NOPADDING);
    return codec;
  }

  public CryptoInputStream initialCryptoInputStream(InputStream inputStream) throws IOException {
    CryptoCodec codec = initialCryptoCodec();
    Random r = new Random();
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
    return ((double) currentTime / (double) parameters.getExecutionTimes());
  }

  public long getExecutedTime() {
    return end - begin;
  }

  public double getAverageThroughput() {
    return 1000.0 * currentTime * parameters.getDataSize() / ((end - begin) * 1024.0 * 1024.0);
  }

  public void encryptionThroughputTest() {
    try {
      byte[] data = prepareData();
      ByteArrayInputStream inputStream = new ByteArrayInputStream(prepareData());
      CryptoInputStream cryptoInputStream = initialCryptoInputStream(inputStream);
      begin = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();

      for (currentTime = 0; currentTime < parameters.getExecutionTimes(); currentTime++) {
        cryptoInputStream.read(data, 0, parameters.getDataSize());
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
