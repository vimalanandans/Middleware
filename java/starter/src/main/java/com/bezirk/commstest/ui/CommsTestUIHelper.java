package com.bezirk.commstest.ui;

import com.bezirk.commons.UhuCompManager;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

/**
 * @author AJC6KOR
 */
public class CommsTestUIHelper {

    private static final String UI_TYPE = "commsUI";
    private static final String TAHOMA_FONT = "Tahoma";
    private final ImageIcon icon = new ImageIcon(getClass().getResource(
            CommsTestConstants.IMAGE_PATH1));
    private final ImageIcon icon1 = new ImageIcon(getClass().getResource(
            CommsTestConstants.IMAGE_PATH2));
    private final JButton settingsBtn = new JButton(icon),
            infoBtn = new JButton(icon1), startBtn = new JButton(),
            clearBtn = new JButton(), pingBtn = new JButton();
    private final JPanel componentsPanel = new JPanel(),
            displayPanel = new JPanel(), btnPanel = new JPanel(),
            statusDisplayPanel = new JPanel();
    private final JSeparator displaySeparator = new JSeparator(),
            buttonSeparator = new JSeparator();
    private final JLabel deviceNameLbl = new JLabel(),
            statusLbl = new JLabel(), testStatusLbl = new JLabel();
    private final JTextArea statusDisplayTxt = new JTextArea();
    private final JTextField deviceNameTxt = new JTextField();
    private final JScrollPane scrollPane = new JScrollPane();
    private final CommsTestActionPerformer commsTestActionPerformer = new CommsTestActionPerformer();

    void setInfoAndSettingsButton(final CommsTest commsTest) {
        infoBtn.setToolTipText(CommsTestConstants.HINT_DIALOG_TITLE);
        infoBtn.setBounds(new Rectangle(690, 0, 25, 25));
        infoBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                commsTestActionPerformer.infoBtnActionPerformed();
            }
        });

        settingsBtn.setToolTipText(CommsTestConstants.SETTINGS_DIALOG_TITLE);
        settingsBtn.setBounds(new Rectangle(660, 0, 25, 25));
        settingsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                commsTestActionPerformer.settingsBtnActionPerformed(commsTest,
                        statusDisplayTxt, startBtn, pingBtn);
            }
        });
    }

    void setDeviceNameLabelAndDeviceNameTxt() {
        deviceNameLbl.setText(CommsTestConstants.DEVICE_NAME_LABEL);
        deviceNameLbl.setBounds(new Rectangle(5, 10, 165, 25));
        deviceNameLbl.setFont(new Font(TAHOMA_FONT, 1, 12));

        deviceNameTxt.setBounds(new Rectangle(120, 10, 575, 25));
        deviceNameTxt.setFont(new Font(TAHOMA_FONT, 1, 12));
        deviceNameTxt.setEditable(false);
        deviceNameTxt.setText(UhuCompManager.getUpaDevice().getDeviceName());
        deviceNameTxt.setForeground(Color.GRAY);
    }

    void setStartClearAndPingButton(final CommsTest commsTest) {

        startBtn.setText(CommsTestConstants.START_BUTTON_LABEL);
        startBtn.setBounds(new Rectangle(5, 3, 105, 25));
        startBtn.setFont(new Font(TAHOMA_FONT, 1, 9));
        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                commsTestActionPerformer.startBtnActionPerformed(startBtn,
                        commsTest);
            }
        });

        clearBtn.setText(CommsTestConstants.CLEAR_BUTTON_LABEL);
        clearBtn.setBounds(new Rectangle(575, 5, 105, 25));
        clearBtn.setFont(new Font(TAHOMA_FONT, 1, 9));
        clearBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                commsTestActionPerformer
                        .clearBtnActionPerformed(statusDisplayTxt);
            }
        });

        pingBtn.setText("PING - " + commsTestActionPerformer.pingCount);
        pingBtn.setBounds(new Rectangle(295, 5, 105, 25));
        pingBtn.setFont(new Font(TAHOMA_FONT, 1, 9));
        pingBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                commsTestActionPerformer.pingBtnActionPerformed(
                        statusDisplayTxt, commsTest, pingBtn);
            }
        });
    }

    void setStatusLabelAndStatusDisplayText(final CommsTest commsTest) {
        statusLbl.setText(CommsTestConstants.STATUS_LABEL);
        statusLbl.setBounds(new Rectangle(245, 0, 205, 25));
        statusLbl.setFont(new Font(TAHOMA_FONT, 1, 12));
        statusLbl.setHorizontalAlignment(SwingConstants.CENTER);

        statusDisplayTxt.setBounds(new Rectangle(0, 30, 695, 335));
        statusDisplayTxt.setEditable(false);
        statusDisplayTxt.setForeground(Color.GRAY);
        final com.bezirk.util.TestUIMouseListener testUIMouseListener = new com.bezirk.util.TestUIMouseListener(UI_TYPE, commsTest,
                commsTestActionPerformer.pingCount, null);

        statusDisplayTxt.addMouseListener(testUIMouseListener);
    }

    void setScrollPaneDisplayPanelAndButtonPanel() {
        scrollPane.setBounds(new Rectangle(5, 25, 690, 340));
        scrollPane.getViewport().add(statusDisplayTxt, null);
        scrollPane
                .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        displayPanel.add(deviceNameTxt, null);
        displayPanel.add(deviceNameLbl, null);

        btnPanel.add(pingBtn, null);
        btnPanel.add(clearBtn, null);
        btnPanel.add(startBtn, null);
    }

    void setStatusDisplayPanelAndPrepareTestStatusLabel() {
        statusDisplayPanel.add(statusLbl, null);
        statusDisplayPanel.add(scrollPane, null);

        testStatusLbl.setBounds(new Rectangle(10, 540, 695, 15));
        testStatusLbl.setFont(new Font(TAHOMA_FONT, 0, 10));
        testStatusLbl
                .setText("Press Test and look for the response. If you dont see the response from other device, check"
                        + "\nthe hints and redo the test!");
    }

    void addComponentsToComponentPanel() {
        componentsPanel.add(statusDisplayPanel, null);
        componentsPanel.add(buttonSeparator, null);
        componentsPanel.add(btnPanel, null);
        componentsPanel.add(displaySeparator, null);
        componentsPanel.add(displayPanel, null);
        componentsPanel.add(settingsBtn, null);
        componentsPanel.add(infoBtn, null);
        componentsPanel.add(testStatusLbl, null);
    }

}
