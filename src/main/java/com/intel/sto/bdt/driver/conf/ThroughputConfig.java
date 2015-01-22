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
package com.intel.sto.bdt.driver.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

import com.intel.sto.bdt.driver.EncryptionDemoMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ThroughputConfig {

    private static Logger LOG = LoggerFactory.getLogger(ThroughputConfig.class);

    public static final String CONFIG_FILE = "config.properties";

    public static final String TEST_DATA_SIZE = "test.data.size";
    public static final String EXECUTION_TIMES = "execution.times";
    public static final String JAVA_CIPHER_ENABLE = "java.cipher.enabled";
    public static final String HADOOP_JCE_CIPHER_ENABLED =
        "hadoop.jce.cipher.enabled";
    public static final String HADOOP_OPENSSL_CIPHER_ENABLED =
        "hadoop.openssl.cipher.enabled";
    public static final String INPUT_DATA_FILE_PATH = "input.data.file.path";
    public static final String FILE_BASED_TEST_ENABLED =
        "file.based.test.enabled";

    private EncryptParameters configParameters;
    
    private static ThroughputConfig INSTANCE;
    
    public static synchronized ThroughputConfig getInstance() throws IOException {
      if (INSTANCE == null) {
        INSTANCE = new ThroughputConfig();
      }
      return INSTANCE;
    }
    
    public EncryptParameters getConfig(){
      return  configParameters;
    }
    
    private ThroughputConfig() throws IOException {
      Properties prop = readConfiguration();
      ConvertProperties(prop);
    }

    private void ConvertProperties(Properties prop) throws IOException {
      int executionTimes = Integer.valueOf(prop.getProperty(EXECUTION_TIMES));
      if (Boolean.valueOf(prop.getProperty(FILE_BASED_TEST_ENABLED))) {
        String fileName = prop.getProperty(INPUT_DATA_FILE_PATH);
        this.configParameters = new FileEncryptParameters(executionTimes,fileName);
      } else {
        int dataSize = Integer.valueOf(prop.getProperty(TEST_DATA_SIZE));
        this.configParameters = new ArrayEncryptParameters(executionTimes,dataSize);
      }
    }

    private Properties readConfiguration() throws IOException {
      Properties prop = new Properties();
      File configFile = new File(CONFIG_FILE);
      InputStream inputStream = null;
      if (configFile.exists()) {
        LOG.info("Conf path: " + configFile.getAbsolutePath());
        try {
          inputStream = new FileInputStream(configFile);
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        }
      } else {
        inputStream = EncryptionDemoMain.class.getClassLoader()
            .getResourceAsStream(CONFIG_FILE);
        LOG.warn(
            "Conf path from ClassLoader: " + EncryptionDemoMain.class
                .getClassLoader().getResource(CONFIG_FILE));
      }
      try {
        prop.load(inputStream);
        return prop;
      } catch (IOException e) {
        LOG.error(e.getMessage(), e);
        throw e;
      }

    }


  private static class FileEncryptParameters extends EncryptParameters{
    private int dataSize = 0;
    private int executionTimes = 0;
    private String fileName;
    private byte[] fileContent;

    public FileEncryptParameters(int executionTimes,String fileName) throws
        IOException {
      this.executionTimes = executionTimes;
      this.fileName = fileName;
      init();
    }

    private void init() throws IOException {
      try {
        File imgPath = new File(fileName);
        FileInputStream is = new FileInputStream(imgPath);
        byte[] picData = new byte[(int)imgPath.length()];
        readFully(is, picData, 0, picData.length);
        dataSize = picData.length;
        fileContent = picData;
      } catch (IOException e) {
        throw e;
      }
    }

    public int getDataSize() {
      return dataSize;
    }

    public int getExecutionTimes() {
      return executionTimes;
    }

    @Override
    public byte[] getData() {
      return fileContent;
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

    @Override
    public String toString() {
      return "Current Config:" + "dataSize = " + dataSize +
          "executionTimes = " + executionTimes +
          "fileName = " + fileName;
    }
  }

  public class ArrayEncryptParameters extends EncryptParameters{
    private int dataSize = 0;
    private int executionTimes = 0;

    public ArrayEncryptParameters(int executionTimes, int dataSize) {
      this.dataSize = dataSize;
      this.executionTimes = executionTimes;
    }

    public int getDataSize() {
      return dataSize;
    }

    public int getExecutionTimes() {
      return executionTimes;
    }

    @Override
    public byte[] getData() {
      byte[] data = new byte[dataSize];
      Random r = new Random();
      r.nextBytes(data);
      return data;
    }


    @Override
    public String toString() {
      return "Current Config:" + "dataSize = " + dataSize +
          "executionTimes = " + executionTimes;
    }
  }
}