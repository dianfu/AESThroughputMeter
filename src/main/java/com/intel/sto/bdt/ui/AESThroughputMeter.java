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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.intel.sto.bdt.driver.*;

public class AESThroughputMeter implements ActionListener, ChangeListener {
  JFrame frame = null;
  JProgressBar progressbar;
  Tick tick;
  JLabel label;
  JButton b;

  public AESThroughputMeter() {
    frame = new JFrame("进度条简单示例");
    frame.setBounds(100, 100, 400, 130);
    frame.setSize(840, 480);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(true);
    Container contentPanel = frame.getContentPane();

    label = new JLabel("点击运行按钮开始", JLabel.CENTER);

    progressbar = new JProgressBar();
    progressbar.setOrientation(JProgressBar.HORIZONTAL);
    progressbar.setMinimum(0);
    progressbar.setMaximum(100);
    progressbar.setValue(0);
    progressbar.setStringPainted(true);
    progressbar.addChangeListener(this);
    progressbar.setPreferredSize(new Dimension(300, 20));
    progressbar.setBorderPainted(true);
    progressbar.setBackground(Color.pink);

    tick = new Tick();
    tick.setForeground(Color.BLUE);
    tick.setType(Tick.RING_180);
    tick.addChangeListener(this);
    tick.setValue("0");
    tick.setUnit("MB/s");

    JPanel panel = new JPanel();
    b = new JButton("运行");
    b.setForeground(Color.blue);
    b.addActionListener(this);
    panel.add(b);

    contentPanel.setLayout(new GridBagLayout());
    /**
     * int gridx, int gridy,
     * int gridwidth, int gridheight,
     * double weightx, double weighty,
     * int anchor, int fill,
     * Insets insets, int ipadx, int ipady
     */
    //GridBagConstraints c = new GridBagConstraints(0,0,10,8,(double)0,(double)0,10,0,new Insets
    // (0,0,0,0),1,1);
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 0.5;
    c.gridwidth = 4;
    c.gridheight = 1;
    c.gridx = 0;
    c.gridy = 0;
    contentPanel.add(panel, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridwidth = 4;
    c.gridheight = 2;
    c.gridx = 0;
    c.gridy = 1;
    //c.gridheight = 1;
    //c.gridwidth = 10;
    contentPanel.add(new JPanel(), c);

    c.fill = GridBagConstraints.VERTICAL;
    //c.ipady = 40;
    //c.weightx = 0.0;
    c.gridwidth = 2;
    c.gridheight = 4;
    c.gridx = 0;
    c.gridy = 3;
    //c.gridheight = 2;
    //c.gridwidth = 5;
    contentPanel.add(tick, c);
    c.fill = GridBagConstraints.VERTICAL;
    //c.ipady = 40;
    c.gridwidth = 2;
    c.gridheight = 4;
    //c.weightx = 0.5;
    //c.gridheight = 2;
    //c.gridwidth = 5;
    c.gridx = 2;
    c.gridy = 3;
    contentPanel.add(label, c);

    c.fill = GridBagConstraints.NONE;
    c.gridwidth = 4;
    c.gridheight = 2;
    c.gridx = 0;
    c.gridy = 8;
    //c.gridheight = 1;
    //c.gridwidth = 10;
    contentPanel.add(new JPanel(), c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.PAGE_END;
    c.weightx = 1;
    c.gridwidth = 4;
    c.gridheight = 3;
    //c.gridwidth = 3;
    c.gridx = 0;
    c.gridy = 10;
    contentPanel.add(progressbar, c);

    // frame.pack();
    frame.setVisible(true);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == b) {
      sw.execute();
    }
  }

  @Override
  public void stateChanged(ChangeEvent e1) {
    int value = progressbar.getValue();
    if (e1.getSource() == progressbar) {
      label.setText("目前已完成进度：" + Integer.toString(value) + "%");
      label.setForeground(Color.blue);
    }
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
      progressbar.setValue((int)(latest.getPercentage() * 100));
      tick.setValue(String.valueOf(latest.getAverageThroughput()));
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
      Logger.getLogger(AESThroughputMeter.class.getName()).log(Level.FINE,
        e.getMessage());
      e.printStackTrace();
    }

    new AESThroughputMeter();
  }
}