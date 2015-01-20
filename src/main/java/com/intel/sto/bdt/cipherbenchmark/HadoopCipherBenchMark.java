package com.intel.sto.bdt.cipherbenchmark;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.crypto.*;

import java.io.*;
import java.util.*;

/**
 * Created by root on 12/5/14.
 */
public class HadoopCipherBenchMark extends CipherBenchMark {
  private String keyProviderName = "org.apache.hadoop.crypto.OpensslAesCtrCryptoCodec";

  public HadoopCipherBenchMark(String keyProviderName, int dataSize) {
    this.keyProviderName = keyProviderName;
    this.dataSize = dataSize;
    benchMarkName = "HadoopCipherBenchMark using " + keyProviderName;
  }

  @Override
  public void getBenchMarkData(int times) {
    Configuration conf = new Configuration();
    conf.set("hadoop.security.crypto.codec.classes.aes.ctr.nopadding", getProvider());
    CryptoCodec codec = CryptoCodec.getInstance(conf, CipherSuite.AES_CTR_NOPADDING);
    Random r = new Random();
    byte[] iv = new byte[16];
    byte[] key = new byte[16];
    r.nextBytes(iv);
    r.nextBytes(key);
    byte[] data = prepareData(dataSize);
    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream(dataSize);
      CryptoOutputStream cryptoOutputStream =
        new CryptoOutputStream(outputStream, codec, dataSize, key, iv);


      long begin = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
      for (int i = 0; i < times; i++) {
        cryptoOutputStream.write(data);
        outputStream.reset();
      }

      long end = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
      printResult("encryption", 1000.0 * data.length * times / ((end - begin) * 1024.0 * 1024.0));

      ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
      CryptoInputStream cryptoInputStream = new CryptoInputStream(inputStream, codec, dataSize, key, iv);

      data = prepareData(dataSize);
      begin = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();

      for (int i = 0; i < times; i++) {
        cryptoInputStream.read(data, 0, dataSize);
        inputStream.reset();
      }
      end = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();

      printResult("decryption", 1000.0 * data.length * times / ((end - begin) * 1024.0 * 1024.0));

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String getProvider() {
    return keyProviderName;
  }
}
