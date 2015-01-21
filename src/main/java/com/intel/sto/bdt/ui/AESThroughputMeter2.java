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
package com.intel.sto.bdt.ui;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.intel.sto.bdt.driver.EncryptionDemoMain;

public class AESThroughputMeter2 implements ActionListener, ChangeListener {
  JFrame frame = null;
  JButton startButton;
  JLabel timeUsedValue;
  JLabel throughputValue;

  public AESThroughputMeter2() {
    frame = new JFrame("");
    frame.setBounds(100, 100, 840, 480);
    frame.setSize(840, 480);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    //frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    frame.setUndecorated(true);

    Container pane = frame.getContentPane();
    pane.setLayout(null);
//    pane.setBackground(new Color(0, 0, 0));

    // left top infomration picture
    JPanel panelDesc = new JPanel(null);
    panelDesc.setBounds(0, 0, 420, 240);
    JLabel infoLabel = new JLabel();
    infoLabel.setFont(infoLabel.getFont().deriveFont(Font.ITALIC));
    updatePicture(infoLabel, "info.jpg");
    infoLabel.setBounds(0, 0, 210, 120);
    panelDesc.add(infoLabel);
    panelDesc.setBackground(new Color(0, 0, 0));
    pane.add(panelDesc);

    // right top picture
    JPanel panelRight = new JPanel(null);
    panelRight.setBounds(420, 0, 420, 240);
    JLabel picRightLabel = new JLabel();
    picRightLabel.setFont(picRightLabel.getFont().deriveFont(Font.ITALIC));
    updatePicture(picRightLabel, "info.jpg");
    picRightLabel.setBounds(420, 0, 210, 120);
    panelRight.add(picRightLabel);
    panelRight.setBackground(new Color(0, 0, 0));
    pane.add(panelRight);

    // time used and throughput
    JPanel panelRunInfo = new JPanel(null);
    panelRunInfo.setBounds(0, 240, 840, 240);
    JLabel timeUsedLabel = new JLabel();
    timeUsedLabel.setFont(timeUsedLabel.getFont().deriveFont(20f));
    timeUsedLabel.setText("time used: ");
    timeUsedLabel.setBounds(150, 70, 150, 20);
    panelRunInfo.add(timeUsedLabel);

    timeUsedValue = new JLabel("0");
    timeUsedValue.setBounds(370, 70, 100, 20);
    timeUsedValue.setFont(timeUsedValue.getFont().deriveFont(20f));
    panelRunInfo.add(timeUsedValue);
    
    JLabel timeUnit = new JLabel("s");
    timeUnit.setFont(timeUnit.getFont().deriveFont(20f));
    timeUnit.setBounds(500, 70, 100, 20);
    panelRunInfo.add(timeUnit);

    JLabel throughputLabel = new JLabel();
    throughputLabel.setText("throughput: ");
    throughputLabel.setFont(throughputLabel.getFont().deriveFont(20f));
    throughputLabel.setBounds(150, 120, 150, 20);
    panelRunInfo.add(throughputLabel);

    throughputValue = new JLabel("0");
    throughputValue.setFont(throughputValue.getFont().deriveFont(20f));
    throughputValue.setBounds(370, 120, 100, 20);
    panelRunInfo.add(throughputValue);
    
    JLabel throughputUnit = new JLabel("MB/s");
    throughputUnit.setFont(throughputUnit.getFont().deriveFont(20f));
    throughputUnit.setBounds(500, 120, 100, 20);
    panelRunInfo.add(throughputUnit);

    pane.add(panelRunInfo);
//    panelRunInfo.setBackground(new Color(0, 0, 0));
    // frame.pack();
    frame.setVisible(true);

    new Timer(2000, this).start();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    sw.execute();
  }

  protected void updatePicture(JLabel pictureLabel, String filename) {
    //Get the icon corresponding to the image.
    ImageIcon icon = createImageIcon(filename);
    pictureLabel.setIcon(icon);
    
    if (icon == null) {
        pictureLabel.setText("Missing Image");
    } else {
        pictureLabel.setText(null);
    }
  }

  /** Returns an ImageIcon, or null if the path was invalid. */
  protected static ImageIcon createImageIcon(String path) {
    java.net.URL imgURL = AESThroughputMeter2.class.getClassLoader().getResource(path);
    if (imgURL != null) {
        return new ImageIcon(imgURL);
    } else {
        System.err.println("Couldn't find file: " + path);
        return null;
    }
  }

  @Override
  public void stateChanged(ChangeEvent e1) {
    //int value = progressbar.getValue();/*
   // if (e1.getSource() == progressbar) {
      //runLabel.setText("目前已完成进度：" + Integer.toString(value) + "%");
      //runLabel.setForeground(Color.blue);
    //}
  }

  public static class Progress {
    double averageThroughput;        // MB/s
    long executedTime;     //seconds
    double percentage;   // %

    public Progress(double speed, long timeUsed, double percentage) {
      this.averageThroughput = speed;
      this.executedTime = timeUsed;
      this.percentage = percentage;
    }

    public double getAverageThroughput() {
      return averageThroughput;
    }

    public void setAverageThroughput(double averageThroughput) {
      this.averageThroughput = averageThroughput;
    }

    public long getExecutedTime() {
      return executedTime;
    }

    public void setExecutedTime(long executedTime) {
      this.executedTime = executedTime;
    }

    public double getPercentage() {
      return this.percentage;
    }

    public void setPercentage(double percentage) {
      this.percentage = percentage;
    }
  }

  private void updateProgess(Progress latest) {
    if (latest != null) {
//      progressbar.setValue((int)(latest.getPercentage() * 100));
      timeUsedValue.setText(String.valueOf(latest.getExecutedTime() / 1000));
      throughputValue.setText(String.valueOf(latest.getAverageThroughput()));
    }
  }

  final SwingWorker<Progress, Progress> sw = new SwingWorker<Progress, Progress>() {
    double averageThroughput = 0;
    long executedTime = 0;
    double percentage = 0;
    EncryptionDemoMain driver = new EncryptionDemoMain();

    private void startDriver() {
      driver.start();
    }

    @Override
    protected Progress doInBackground() throws Exception {
      startDriver();

      while (!driver.isCompleted()) {
        averageThroughput = driver.getAverageThroughput();
        executedTime = driver.getExecutedTime();
        percentage = driver.getPercentage();
        publish(new Progress(averageThroughput, executedTime, percentage));
      //  setProgress(i);//
        Thread.sleep(100);
      }
      return new Progress(averageThroughput, executedTime, 1);
    }

    @Override
    protected void process(List<Progress> chunks) {
      if (chunks != null && !chunks.isEmpty()) {
        Progress latest = chunks.get(chunks.size() - 1);
        updateProgess(latest);
      }
    }

    @Override
    protected void done() {
      Progress result = null;
      try {
        result = get();
      } catch (Exception e) {
        e.printStackTrace();
      }
      updateProgess(result);
    }
  };

  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } catch (Exception e) {
      Logger.getLogger(AESThroughputMeter2.class.getName()).log(Level.FINE,
        e.getMessage());
      e.printStackTrace();
    }

    new AESThroughputMeter2();
  }
}