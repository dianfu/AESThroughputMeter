package com.intel.sto.bdt.driver;

import java.io.*;
import java.util.*;

/**
 * Created by root on 1/19/15.
 */
public class EncryptionDemoMain extends Thread{
  static String defaultConfigPath = "./src/main/resources/config.properties";
  final static String defaultDataSize = "52428800";
  final static String defaultExecutionTimes = "100000";
  final static String openSSLCodecName = "org.apache.hadoop.crypto.OpensslAesCtrCryptoCodec";

  private EncryptionMicroBenchMark microBenchMark;

  public static void main(String[]args) throws InterruptedException, IOException{
    EncryptionDemoMain e = new EncryptionDemoMain();
    e.start();
  }

  public EncryptionDemoMain() {
    Properties prop = new Properties();
    String propFileName = "config.properties";

    File configFile = new File(defaultConfigPath);
    InputStream inputStream = null;
    if (configFile.exists()) {
      try {
        inputStream = new FileInputStream(configFile);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    } else {
      System.out.println("can not find the configuration file under the current path");
      inputStream = EncryptionDemoMain.class.getClassLoader().getResourceAsStream(propFileName);
    }

    try {
      prop.load(inputStream);
    } catch (IOException e) {
      e.printStackTrace();
    }

    int dataSize = Integer.valueOf(prop.getProperty("test.data.size", defaultDataSize));
    int executionTimes =
      Integer.valueOf(prop.getProperty("execution.times", defaultExecutionTimes));

    microBenchMark =
      new EncryptionMicroBenchMark(dataSize, executionTimes, openSSLCodecName);
  }

  public boolean isCompleted() {
    return microBenchMark.isCompleted();
  }

  public double getPercentage() {
    return microBenchMark.getPercentage();
  }

  public long getExecutedTime() {
    return microBenchMark.getExecutedTime();
  }

  public double getAverageThroughput() {
    return microBenchMark.getAverageThroughput();
  }

  @Override
  public void run(){
    microBenchMark.encryptionThroughputTest();

    System.out.println("average throughput is " + microBenchMark.getAverageThroughput());
  }
}
