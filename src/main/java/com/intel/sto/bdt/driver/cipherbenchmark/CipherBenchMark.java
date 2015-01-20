package com.intel.sto.bdt.driver.cipherbenchmark;

import java.util.*;

/**
 * Created by root on 12/5/14.
 */
public abstract class CipherBenchMark {
  protected String benchMarkName;
  protected int dataSize = 512 * 1024;

  protected String getBenchMarkName() {
    return benchMarkName;
  }

  protected byte[] prepareData(int size) {
    byte[] data = new byte[size];
    Random r = new Random();
    r.nextBytes(data);
    return data;
  }

  protected void printResult(String operation, double timeCost) {
    System.out.println(
      "result of " + getBenchMarkName() + " for the " + operation + " operation is " + timeCost +
        " M/s");
    System.out.println();
  }

  public abstract void getBenchMarkData(int times);

  public abstract String getProvider();
}
