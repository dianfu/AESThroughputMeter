package com.intel.sto.bdt.driver;

import java.io.*;
import java.util.*;

import com.intel.sto.bdt.driver.cipherbenchmark.CipherBenchMark;
import com.intel.sto.bdt.driver.cipherbenchmark.HadoopCipherBenchMark;
import com.intel.sto.bdt.driver.cipherbenchmark.JavaCipherBenchMark;

/**
 * Created by root on 12/4/14.
 */
public class STOBenchMark {
  static String defaultConfigPath = "./config.properties";
  static String jceAesCtrCryptoCodec = "org.apache.hadoop.crypto.JceAesCtrCryptoCodec";
  static String openSSLAesCtrCryptoCodec = "org.apache.hadoop.crypto.OpensslAesCtrCryptoCodec";
  static String defaultExecutionTimes = "123456";
  static String defaultDataSize = "524288";

  public static void main(String[] args) throws IOException {
    Properties prop = new Properties();
    String propFileName = "config.properties";

    File configFile = new File(defaultConfigPath);
    InputStream inputStream;
    if (configFile.exists()) {
      inputStream = new FileInputStream(configFile);
    } else {
      System.out.println("can not find the configuration file under the current path");
      inputStream = STOBenchMark.class.getClassLoader().getResourceAsStream(propFileName);
    }

    if (inputStream != null) {
      prop.load(inputStream);
    } else {
      throw new FileNotFoundException("No configuration file found");
    }

    boolean javaCipherTestEnabled =
      Boolean.valueOf(prop.getProperty("java.cipher.enabled", "false"));
    boolean hadoopJCECipherTestEnabled =
      Boolean.valueOf(prop.getProperty("hadoop.jce.cipher.enabled", "false"));
    boolean hadoopOpenSSLCipherTestEnabled =
      Boolean.valueOf(prop.getProperty("hadoop.openssl.cipher.enabled", "false"));

    int executionTimes =
      Integer.valueOf(prop.getProperty("execution.times", defaultExecutionTimes));
    int dataSize = Integer.valueOf(prop.getProperty("test.data.size", defaultDataSize));

    List<CipherBenchMark> benchMarks = new ArrayList<CipherBenchMark>();

    if (javaCipherTestEnabled) {
      benchMarks.add(new JavaCipherBenchMark(dataSize));
    } else {
      System.out.println("java cipher test disabled");
    }

    if (hadoopJCECipherTestEnabled) {
      benchMarks.add(new HadoopCipherBenchMark(jceAesCtrCryptoCodec, dataSize));
    } else {
      System.out.println("hadoop JCE cipher test disabled");
    }

    if (hadoopOpenSSLCipherTestEnabled) {
      benchMarks.add(new HadoopCipherBenchMark(openSSLAesCtrCryptoCodec, dataSize));
    } else {
      System.out.println("hadoop Openssl cipher test disabled");
    }

    System.out.println("begin test suite");
    for (CipherBenchMark benchMark : benchMarks) {
      benchMark.getBenchMarkData(executionTimes);
    }
    System.out.println("end test suite");
  }
}
