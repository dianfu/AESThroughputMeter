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

import java.io.IOException;

import com.intel.sto.bdt.driver.conf.EncryptParameters;
import com.intel.sto.bdt.driver.conf.ThroughputConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptionDemoMain extends Thread {

  private static Logger LOG = LoggerFactory.getLogger(EncryptionDemoMain.class);
  
  private EncryptionMicroBenchMark microBenchMark;
  private EncryptParameters parameters;
  
  public EncryptionDemoMain() throws IOException {
    parameters = ThroughputConfig.getInstance().getConfig();
    LOG.info(parameters.toString());
    microBenchMark = new EncryptionMicroBenchMark(parameters);
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
  public void run() {
    microBenchMark.encryptionThroughputTest();
    LOG.info("average throughput is " + microBenchMark.getAverageThroughput());
  }
  
  public static void main(String[] args) throws InterruptedException, IOException {
    EncryptionDemoMain e = new EncryptionDemoMain();
    e.start();
  }
}
