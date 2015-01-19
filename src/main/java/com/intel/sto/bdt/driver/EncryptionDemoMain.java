package com.intel.sto.bdt.driver;

import java.io.*;
import java.util.*;

/**
 * Created by root on 1/19/15.
 */
public class EncryptionDemoMain extends Thread{
  static String defaultConfigPath = "./config.properties";
  final static String defaultDataSize = "52428800";
  final static String defaultExecutionTimes = "100000";
  final static String openSSLCodecName = "org.apache.hadoop.crypto.OpensslAesCtrCryptoCodec";

  public static void main(String[]args) throws InterruptedException, IOException{
    EncryptionDemoMain e = new EncryptionDemoMain();
    e.start();
  }

  @Override
  public void run(){
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

    EncryptionMicroBenchMark e =
      new EncryptionMicroBenchMark(dataSize, executionTimes, openSSLCodecName);
    e.encryptionThroughputTest();

    System.out.println("average throughput is " + e.getAverageThroughput());
  }
}
