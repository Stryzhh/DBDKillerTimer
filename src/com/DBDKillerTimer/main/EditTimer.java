package com.DBDKillerTimer.main;

import com.google.gson.Gson;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.util.Objects;
import javax.swing.*;

public class EditTimer {

    private final TimerProperties timer;
    private JTabbedPane timerProperties;
    private JPanel mainPanel;
    private JPanel timerPanel;

    /** the path of the icons image. */
    private String iconPath = "";
    /** the TextField where the timers name is entered. */
    private JTextField timerName;
    /** the combo box including all timer types. */
    private JComboBox<TimerProperties.TimerType> timerTypeBox;
    /** the combo box including all timers modes. */
    private JComboBox<TimerProperties.TimerMode> timerModeBox;
    /** the combo box including all possible starting times. */
    private JComboBox<Integer> startTime;
    /** the combo box including all the possible binds. */
    private JComboBox<Character> startBind;
    /** the button used to point to image. */
    private JButton browseButton;
    /** the button used to open a colour chooser. */
    private JButton chooseColour;
    /** the button to save the timer. */
    private JButton saveTimer;
    /** the path of the icons image. */
    private JCheckBox timerEnabled;
    /** the label which shows the path of the selected image. */
    private JLabel txtPath;
    /** the label which shows the color selected by the user. */
    private JLabel txtColor;
    private Color timerStartColor;

    public EditTimer(TimerProperties timer, String title) {
        this.timer = timer;
        JFrame frame = new JFrame(title);
        frame.setPreferredSize(new Dimension(390, 450));
        frame.setMaximumSize(new Dimension(390, 450));
        frame.setMinimumSize(new Dimension(390, 450));
        frame.setContentPane(timerProperties);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        populateComboBoxes();
        if (timer != null) {
            setTimerProperties();
        }

        //allows you to browse for image
        browseButton.addActionListener(e -> {
            FileDialog fd = new FileDialog(new JFrame());
            fd.setVisible(true);
            File[] f = fd.getFiles();
            if (f.length > 0) {
                iconPath = fd.getFiles()[0].getAbsolutePath();
                txtPath.setText(fd.getFiles()[0].getName());
            }
        });

        //lets you choose colour
        chooseColour.addActionListener(e -> {
            timerStartColor = JColorChooser.showDialog(null, "Pick a Color",
                    Color.BLACK);
            if (timerStartColor != null) {
                txtColor.setText("RGB: " + timerStartColor.getRed() + ", "
                        + timerStartColor.getGreen() + ", "
                        + timerStartColor.getBlue());
            }
        });

        //appends/creates new timer
        saveTimer.addActionListener(e -> {
            if (!iconPath.equals("") && timerStartColor != null) {
                try {
                    FileWriter fw = new FileWriter("timers\\"
                            + timerName.getText() + ".json");
                    Gson g = new Gson();

                    TimerProperties newTimer = new TimerProperties();
                    newTimer.setName(timerName.getText());
                    newTimer.setStartColor(timerStartColor);
                    newTimer.setTimerType((TimerProperties.TimerType) timerTypeBox.getSelectedItem());
                    if (newTimer.getTimerType() == TimerProperties.TimerType.CountUp) {
                        newTimer.setStartTime(0);
                    } else {
                        newTimer.setStartTime((int) startTime.getSelectedItem());
                    }
                    newTimer.setIcon(iconPath);
                    newTimer.setTimerMode((TimerProperties.TimerMode) timerModeBox.getSelectedItem());
                    newTimer.setEnabled(timerEnabled.isSelected());
                    newTimer.setStartBind(Objects.requireNonNull(startBind.getSelectedItem()).toString());

                    fw.write(g.toJson(newTimer));
                    fw.close();
                    JOptionPane.showMessageDialog(null, "timer update.");
                    frame.dispose();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(null, "cant update timer.");
            }
        });
    }

    private void setTimerProperties() {
        timerName.setText(timer.getName());
        iconPath = timer.getIcon();
        timerTypeBox.setSelectedItem(timer.getTimerType());
        timerStartColor = timer.getStartColor();
        startTime.setSelectedItem(timer.getStartTime());
        startBind.setSelectedItem(timer.getStartBind().charAt(0));
        timerModeBox.setSelectedItem(timer.getTimerMode());
        timerEnabled.setSelected(timer.isEnabled());
        txtPath.setText(timer.getIcon());
        txtColor.setText(timer.getStartColor().getRed() + ", " + timer.getStartColor().getGreen()
                + ", " + timer.getStartColor().getBlue());
    }

    private void populateComboBoxes() {
        populateTimerTypeBox();
        populateTimerModeBox();
        populateStartBind();
        populateStartTime();
    }

    /** populates the combo box including timer types. */
    private void populateTimerTypeBox() {
        timerTypeBox.addItem(TimerProperties.TimerType.CountUp);
        timerTypeBox.addItem(TimerProperties.TimerType.CountDown);
        startTime.setEnabled(false);
        timerTypeBox.addActionListener(e -> {
            //disable start time if timer type is count up
            startTime.setEnabled(Objects.requireNonNull(timerTypeBox.
                    getSelectedItem()).toString().equals("CountDown"));
        });
    }

    /** populates the combo box including timer modes. */
    private void populateTimerModeBox() {
        timerModeBox.addItem(TimerProperties.TimerMode.Killer);
        timerModeBox.addItem(TimerProperties.TimerMode.Survivor);
    }

    /** populates the combo box including start timer binds. */
    private void populateStartBind() {
        for (char c = 'A'; c <= 'Z'; ++c) {
            startBind.addItem(c);
        }
        for (char num = 48; num <= 57; ++num) {
            startBind.addItem(num);
        }
    }

    /** populates the combo box including timer start times. */
    private void populateStartTime() {
        for (int num = 1; num <= 180; ++num) {
            startTime.addItem(num);
        }
    }

}