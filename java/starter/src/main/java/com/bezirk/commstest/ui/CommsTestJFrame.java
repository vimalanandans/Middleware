package com.bezirk.commstest.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author MCA7KOR
 * 
 */
public class CommsTestJFrame extends JFrame implements IUpdateResponse {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CommsTestJFrame.class);

    // UI Elements
    private static final long serialVersionUID = 1L;
    private final GridBagLayout mainFrameLayout = new GridBagLayout();
    private final JPanel componentsPanel = new JPanel();
    private final JPanel displayPanel = new JPanel();
    private final JSeparator displaySeparator = new JSeparator();
    private final JPanel btnPanel = new JPanel();
    private final JButton startBtn = new JButton();
    private final JSeparator buttonSeparator = new JSeparator();
    private final JPanel statusDisplayPanel = new JPanel();
    private final JTextArea statusDisplayTxt = new JTextArea();

    private static final String ID_STRING = "Id : ";

    private final transient CommsTest commsTest;
    private final transient CommsTestUIHelper commsUIHelper = new CommsTestUIHelper();

    private final transient WindowAdapter closeButtonListener = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent arg0) {
            super.windowClosing(arg0);
            commsTest.stopCommsReceiverThread();
            CommsTestJFrame.this.dispose();
        }
    };

    public CommsTestJFrame() {
        commsTest = new CommsTest(this);
        try {
            jbInit();
        } catch (Exception e) {
            LOGGER.error("Error initializing commsTest ", e);
        }

    }

    /**
     * Initializing UI
     */
    private void jbInit() throws Exception {
        this.getContentPane().setLayout(mainFrameLayout);
        this.setSize(new Dimension(730, 604));
        this.setTitle("Communication Test");
        this.setResizable(false);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        this.addWindowListener(closeButtonListener);

        final Dimension screenSize = Toolkit.getDefaultToolkit()
                .getScreenSize(), frameSize = this.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        this.setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
        componentsPanel.setLayout(null);

        commsUIHelper.setInfoAndSettingsButton(commsTest);

        displayPanel.setBounds(new Rectangle(10, 40, 695, 45));
        displayPanel.setLayout(null);

        commsUIHelper.setDeviceNameLabelAndDeviceNameTxt();

        displaySeparator.setBounds(new Rectangle(10, 90, 695, 2));
        btnPanel.setBounds(new Rectangle(10, 100, 695, 30));
        btnPanel.setLayout(null);

        commsUIHelper.setStartClearAndPingButton(commsTest);

        buttonSeparator.setBounds(new Rectangle(10, 140, 695, 2));
        statusDisplayPanel.setBounds(new Rectangle(10, 155, 695, 345));
        statusDisplayPanel.setLayout(null);

        commsUIHelper.setStatusLabelAndStatusDisplayText(commsTest);

        commsUIHelper.setScrollPaneDisplayPanelAndButtonPanel();

        commsUIHelper.setStatusDisplayPanelAndPrepareTestStatusLabel();

        commsUIHelper.addComponentsToComponentPanel();

        this.getContentPane().add(
                componentsPanel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 0), 719, 568));

        startBtn.doClick();
    }

    @Override
    public void updatePingResposne(String response) {
        // Nothing to be done. Status display available in commit ID :commit
        // 6d759437d8bf7bf1c9d94d152946b45a1bfb4e84
    }

    public static void main(String... args) {
        final JFrame frame = new CommsTestJFrame();
        final Dimension screenSize = Toolkit.getDefaultToolkit()
                .getScreenSize();
        final Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        frame.setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    @Override
    public void updateUIPingSent(PingMessage msg) {
        statusDisplayTxt.append("Message sent by this [" + msg.deviceName
                + "]." + ID_STRING + msg.pingId);
        statusDisplayTxt.append("\nMessage received by[" + msg.deviceName
                + "] by 0 devices");
    }

    @Override
    public void updateUIPingReceived(PingMessage msg) {
        if ("".equals(statusDisplayTxt.getText())) {
            statusDisplayTxt.append("Message request from [" + msg.deviceName
                    + "] " + ID_STRING + msg.pingId);
        } else {
            statusDisplayTxt.append("\nMessage request from [" + msg.deviceName
                    + "] " + ID_STRING + msg.pingId);
        }
    }

    @Override
    public void updateUIPongSent(PingMessage msg) {
        statusDisplayTxt.append("\nMessage response to [" + msg.deviceName
                + "] " + ID_STRING + msg.pingId);
    }

    @Override
    public void updateUIPongReceived(PongMessage msg, int size) {
        statusDisplayTxt.setText("");
        statusDisplayTxt.append("Message sent by this [" + msg.deviceName
                + "]. " + ID_STRING + msg.pingId);
        statusDisplayTxt.append("\nMessage received by[" + msg.deviceName
                + "] by " + size + " devices");
    }

}