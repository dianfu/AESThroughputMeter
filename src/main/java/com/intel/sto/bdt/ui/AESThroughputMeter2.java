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
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.intel.sto.bdt.driver.EncryptionDemoMain;

public class AESThroughputMeter2 implements ActionListener, ChangeListener {
  JFrame frame = null;
  JButton startButton;
  JLabel timeUsedValue;

  public AESThroughputMeter2() {
    frame = new JFrame("进度条简单示例");
    frame.setBounds(100, 100, 840, 480);
    frame.setSize(840, 480);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    Container pane = frame.getContentPane();
    pane.setLayout(null);
//    pane.setBackground(new Color(0, 0, 0));

    // add description
    JPanel panelDesc = new JPanel(null);
    panelDesc.setBounds(0, 0, 420, 240);
    JLabel pictureLabel = new JLabel();
    pictureLabel.setFont(pictureLabel.getFont().deriveFont(Font.ITALIC));
    updatePicture(pictureLabel, "test.jpg");
    pictureLabel.setBounds(0, 0, 210, 120);
    pane.add(pictureLabel);
    panelDesc.setBackground(new Color(0, 0, 0));

    // add time
    JPanel panelRun = new JPanel(null);
    panelRun.setBounds(0, 240, 420, 240);
    JLabel timeUsed = new JLabel();
    timeUsed.setText("time used: ");
    timeUsed.setBounds(50, 30, 100, 20);
    panelRun.add(timeUsed);

    timeUsedValue = new JLabel("0");
    timeUsedValue.setBounds(150, 30, 50, 20);
    panelRun.add(timeUsedValue);
    
    JLabel timeUnit = new JLabel("s");
    timeUnit.setBounds(200, 30, 100, 20);
    panelRun.add(timeUnit);

    JButton startButton = new JButton("Start");
    startButton.setBounds(50, 100, 100, 20);
    panelRun.add(startButton);
    startButton.addActionListener(this);
    pane.add(panelRun);
    panelRun.setBackground(new Color(0, 0, 0));
    // frame.pack();
    frame.setVisible(true);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    sw.execute();
  }

  protected void updatePicture(JLabel pictureLabel, String filename) {
    //Get the icon corresponding to the image.
    ImageIcon icon = createImageIcon("images/" + filename);
    pictureLabel.setIcon(icon);
    
    if (icon == null) {
        pictureLabel.setText("Missing Image");
    } else {
        pictureLabel.setText(null);
    }
  }

  /** Returns an ImageIcon, or null if the path was invalid. */
  protected static ImageIcon createImageIcon(String path) {
    java.net.URL imgURL = AESThroughputMeter2.class.getResource(path);
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
      System.out.println("2222");
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