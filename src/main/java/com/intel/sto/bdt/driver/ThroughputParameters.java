package com.intel.sto.bdt.driver;

/**
 * Created by root on 1/20/15.
 */
public class ThroughputParameters {
  private int dataSize = 0;
  private int iterations = 0;
  private String keyProviderName = "org.apache.hadoop.crypto.OpensslAesCtrCryptoCodec";
  private boolean isFileBased = false;
  private String fileName;

  public int getDataSize() {
    return dataSize;
  }

  public void setDataSize(int dataSize) {
    this.dataSize = dataSize;
  }

  public int getIterations() {
    return iterations;
  }

  public void setIterations(int iterations) {
    this.iterations = iterations;
  }

  public String getKeyProviderName() {
    return keyProviderName;
  }

  public void setKeyProviderName(String keyProviderName) {
    this.keyProviderName = keyProviderName;
  }

  public boolean isFileBased() {
    return isFileBased;
  }

  public void setFileBased(boolean isFileBased) {
    this.isFileBased = isFileBased;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }
}
