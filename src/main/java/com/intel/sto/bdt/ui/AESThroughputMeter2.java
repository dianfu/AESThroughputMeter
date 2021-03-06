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
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;

import com.intel.sto.bdt.driver.EncryptionDemoMain;

public class AESThroughputMeter2 implements ActionListener {
  private JFrame mainFrame = null;
  private JLabel timeUsedValue = null;
  private JLabel throughputValue = null;
  private JLabel progressValue = null;
  private static final DecimalFormat formater = new DecimalFormat("0.00");

  public AESThroughputMeter2() {
    mainFrame = new JFrame("");
    mainFrame.setBounds(100, 100, 560, 450);
    mainFrame.setSize(560, 450);
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainFrame.setResizable(false);
    //mainFrame.setUndecorated(true);

    Container mainPane = mainFrame.getContentPane();
    mainPane.setLayout(null);

    // left top information
    JPanel panelDesc = new JPanel(null);
    panelDesc.setBounds(0, 0, 260, 225);
    drawLeftTop(panelDesc);
    panelDesc.setBackground(new Color(255, 255, 255));
    mainPane.add(panelDesc);

    // right top picture
    JPanel panelRight = new JPanel(null);
    panelRight.setBounds(260, 0, 300, 225);
    drawRightTop(panelRight);
    panelRight.setBackground(new Color(255, 255, 255));
    mainPane.add(panelRight);

    // time used and throughput
    JPanel panelBottom = new JPanel(null);
    panelBottom.setBounds(0, 225, 560, 450);
    drawButtom(panelBottom);
    mainPane.add(panelBottom);
    panelBottom.setBackground(new Color(255, 255, 255));

    // frame.pack();
    mainFrame.setVisible(true);

    new Timer(2000, this).start();
  }

  private void drawLeftTop(JPanel container) {
    JLabel titleLabel = new JLabel("AES-NI Disabled");
    titleLabel.setFont(titleLabel.getFont().deriveFont(20f));
    titleLabel.setBounds(20, 30, 240, 20);
    titleLabel.setForeground(new Color(91, 155, 213));
    container.add(titleLabel);

    JLabel imageSizeLabel = new JLabel("Image Size: ");
    imageSizeLabel.setFont(imageSizeLabel.getFont().deriveFont(20f));
    imageSizeLabel.setBounds(20, 70, 130, 20);
    imageSizeLabel.setForeground(new Color(91, 155, 213));
    container.add(imageSizeLabel);

    JLabel imageSizeValue = new JLabel("3.65 MB");
    imageSizeValue.setBounds(160, 70, 100, 20);
    imageSizeValue.setFont(imageSizeValue.getFont().deriveFont(20f));
    imageSizeValue.setForeground(Color.RED);
    container.add(imageSizeValue);

    JLabel iterationsLabel = new JLabel("Iterations: ");
    iterationsLabel.setFont(iterationsLabel.getFont().deriveFont(20f));
    iterationsLabel.setBounds(20, 110, 130, 20);
    iterationsLabel.setForeground(new Color(91, 155, 213));
    container.add(iterationsLabel);

    JLabel iterationsValue = new JLabel("10000");
    iterationsValue.setBounds(160, 110, 100, 20);
    iterationsValue.setFont(iterationsValue.getFont().deriveFont(20f));
    iterationsValue.setForeground(Color.RED);
    container.add(iterationsValue);

    JLabel totalSizeLabel = new JLabel("Total Size: ");
    totalSizeLabel.setFont(totalSizeLabel.getFont().deriveFont(20f));
    totalSizeLabel.setBounds(20, 150, 130, 20);
    totalSizeLabel.setForeground(new Color(91, 155, 213));
    container.add(totalSizeLabel);

    JLabel totalSizeValue = new JLabel("35.69 GB");
    totalSizeValue.setBounds(160, 150, 100, 20);
    totalSizeValue.setFont(totalSizeValue.getFont().deriveFont(20f));
    totalSizeValue.setForeground(Color.RED);
    container.add(totalSizeValue);
  }

  private void drawRightTop(JPanel container) {
    JLabel picRightLabel = new JLabel();
    picRightLabel.setFont(picRightLabel.getFont().deriveFont(Font.ITALIC));
    updatePicture(picRightLabel, "image-thumbnail.JPG", 260, 195);
    picRightLabel.setBounds(20, 30, 260, 195);
    container.add(picRightLabel);
  }

  private void drawButtom(JPanel container) {
    JLabel timeUsedLabel = new JLabel();
    timeUsedLabel.setFont(timeUsedLabel.getFont().deriveFont(30f));
    timeUsedLabel.setText("Time Used: ");
    timeUsedLabel.setBounds(20, 30, 230, 30);
    timeUsedLabel.setForeground(new Color(91, 155, 213));
    container.add(timeUsedLabel);

    timeUsedValue = new JLabel("0");
    timeUsedValue.setBounds(280, 30, 60, 30);
    timeUsedValue.setFont(timeUsedValue.getFont().deriveFont(30f));
    timeUsedValue.setForeground(Color.RED);
    container.add(timeUsedValue);

    JLabel timeUnit = new JLabel("s");
    timeUnit.setFont(timeUnit.getFont().deriveFont(30f));
    timeUnit.setBounds(360, 30, 100, 30);
    timeUnit.setForeground(Color.RED);
    container.add(timeUnit);

    JLabel throughputLabel = new JLabel();
    throughputLabel.setText("Throughput: ");
    throughputLabel.setFont(throughputLabel.getFont().deriveFont(30f));
    throughputLabel.setBounds(20, 80, 230, 30);
    throughputLabel.setForeground(new Color(91, 155, 213));
    container.add(throughputLabel);

    throughputValue = new JLabel("0.00");
    throughputValue.setFont(throughputValue.getFont().deriveFont(30f));
    throughputValue.setBounds(280, 80, 150, 30);
    throughputValue.setForeground(Color.RED);
    container.add(throughputValue);

    JLabel throughputUnit = new JLabel("MB/s");
    throughputUnit.setFont(throughputUnit.getFont().deriveFont(30f));
    throughputUnit.setBounds(430, 80, 100, 30);
    throughputUnit.setForeground(Color.RED);
    container.add(throughputUnit);

    JLabel progressLabel = new JLabel();
    progressLabel.setText("Progress: ");
    progressLabel.setFont(throughputLabel.getFont().deriveFont(30f));
    progressLabel.setBounds(20, 130, 230, 30);
    progressLabel.setForeground(new Color(91, 155, 213));
    container.add(progressLabel);

    progressValue = new JLabel("0%");
    progressValue.setFont(progressValue.getFont().deriveFont(30f));
    progressValue.setBounds(280, 130, 150, 30);
    progressValue.setForeground(Color.RED);
    container.add(progressValue);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    sw.execute();
  }

  protected void updatePicture(JLabel pictureLabel, String filename, int width, int height) {
    //Get the icon corresponding to the image.
    ImageIcon icon = createImageIcon(filename);

    if (icon == null) {
      pictureLabel.setText("Missing Image");
    } else {
      icon.setImage(icon.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));
      pictureLabel.setText(null);
    }
    pictureLabel.setIcon(icon);
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
      timeUsedValue.setText(String.valueOf(latest.getExecutedTime() / 1000));
      throughputValue.setText(formater.format(latest.getAverageThroughput()));
      progressValue.setText(String.valueOf((int)(latest.getPercentage() * 100)) + "%");
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