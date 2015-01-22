/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intel.sto.bdt.driver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.crypto.CipherSuite;
import org.apache.hadoop.crypto.CryptoCodec;
import org.apache.hadoop.crypto.CryptoInputStream;

import com.intel.sto.bdt.driver.conf.EncryptParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptionMicroBenchMark {

  private static Logger LOG = LoggerFactory.getLogger(EncryptionDemoMain.class);
  private int currentTime = 0;
  EncryptParameters parameters;
  private long begin = 0;
  private long end = 0;

  private boolean isCompleted = false;
  private boolean isStarted = false;
  
  public EncryptionMicroBenchMark(EncryptParameters parameters) {
    this.parameters = parameters;
  }

  public byte[] prepareData() {
    return parameters.getData();
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
      return ((double) currentTime / (double) parameters.getExecutionTimes());
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
      return 1000.0 * currentTime * parameters.getDataSize() / ((end - begin) * 1024.0 * 1024.0);
    } else {
      return 0.00;
    }
  }

  public void encryptionThroughputTest() {
    try {

      byte[] data = prepareData();
      LOG.info(parameters.toString());
      ByteArrayInputStream inputStream = new ByteArrayInputStream(prepareData());
      CryptoInputStream cryptoInputStream = initialCryptoInputStream(inputStream);

      for (int i = 0; i < 1000; i++) {
        cryptoInputStream.read(data, 0, parameters.getDataSize());
        inputStream.reset();
      }

      begin = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
      isStarted = true;
      for (currentTime = 0; currentTime < parameters.getExecutionTimes(); currentTime++) {
        cryptoInputStream.read(data, 0, parameters.getDataSize());
        inputStream.reset();
      }
      LOG.info("Complete !!");
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    }
    isCompleted = true;
  }
}
