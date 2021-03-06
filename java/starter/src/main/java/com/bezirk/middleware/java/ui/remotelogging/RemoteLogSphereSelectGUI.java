/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bezirk.middleware.java.ui.remotelogging;

import com.bezirk.middleware.core.comms.Comms;
import com.bezirk.middleware.core.remotelogging.RemoteLog;
import com.bezirk.middleware.core.remotelogging.RemoteLoggingMessage;
import com.bezirk.middleware.core.remotelogging.RemoteLoggingMessageNotification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Class that displays the GUI to select the spheres and start the logging Zirk.
 * {@link RemoteLoggingMessageNotification}
 */
public final class RemoteLogSphereSelectGUI extends JFrame implements RemoteLoggingMessageNotification {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(RemoteLogSphereSelectGUI.class);
    /**
     * GUI components
     */
    private final JMenuBar menuBar = new JMenuBar();
    private final JMenu menuSettings = new JMenu();
    private final JMenu menuAboutUs = new JMenu();
    private final JTextField ipAddressTxt = new JTextField(13);
    private final JTextField portTxt = new JTextField(4);
    private final JPanel settingsPanel = new JPanel();
    private final JPanel framePanel = new JPanel();
    private final GridBagLayout frameLayout = new GridBagLayout();
    private final JScrollPane sphereListLeftScroll = new JScrollPane();
    private final JScrollPane sphereListRightScroll = new JScrollPane();
    private final JLabel selectSphereLbl = new JLabel();
    private final JLabel selectedSphereLbl = new JLabel();
    private final DefaultListModel<String> leftSphereListModel = new DefaultListModel<>();
    private final DefaultListModel<String> rightSphereListModel = new DefaultListModel<>();
    private final JList<String> leftSphereList = new JList<>(leftSphereListModel);
    private final JList<String> rightSphereList = new JList<>(rightSphereListModel);
    private final JButton listSphereBtn = new JButton();
    private final JButton startLoggingBtn = new JButton();
    private final JButton moveRightBtn = new JButton();
    private final JButton moveLeftBtn = new JButton();
    private final JCheckBox bezirkDeveloperCheckBox = new JCheckBox();
    private final JFrame thisFrame;
    private final transient ListSelectionListener leftSphereSelectionListListener = new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent event) {
            moveRightBtn.setEnabled(true);
        }
    };
    private final transient ListSelectionListener rightSphereSelectionListListener = new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent event) {
            moveLeftBtn.setEnabled(true);
        }
    };
    private final transient ActionListener listSphereBtnListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            leftSphereListModel.removeAllElements();
            rightSphereListModel.removeAllElements();
            leftSphereListModel.addElement(RemoteLog.ALL_SPHERES);
        }
    };
    private final transient Comms comms;
    private final RemoteLog msgLog = null;
    private String[] tempArray;
    private int size;
    /**
     * Moves the spheres from left to right box
     */
    private final transient ActionListener moveRightButtonActionListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            size = leftSphereListModel.size();
            final int index = leftSphereList.getSelectedIndex();
            tempArray = new String[leftSphereListModel.size()];

            final String temp = leftSphereListModel.elementAt(index);
            if (temp.equals(RemoteLog.ALL_SPHERES)) {
                rightSphereListModel.addElement(RemoteLog.ALL_SPHERES);
                for (int i = 0; i < size; i++) {
                    tempArray[i] = leftSphereListModel.elementAt(0);
                    leftSphereListModel.removeElementAt(0);
                }
                if (!rightSphereListModel.isEmpty()) {
                    startLoggingBtn.setEnabled(true);
                }
            } else {
                rightSphereListModel.addElement(temp);
                leftSphereListModel.remove(index);
                leftSphereListModel.removeElement(RemoteLog.ALL_SPHERES);
                if (!rightSphereListModel.isEmpty()) {
                    startLoggingBtn.setEnabled(true);
                }
            }
            moveRightBtn.setEnabled(false);
            moveLeftBtn.setEnabled(false);
        }
    };

    /**
     * Moves the spheres from right to left box
     */
    private final transient ActionListener moveLeftButtonActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            final String temp = rightSphereList.getSelectedValue();
            rightSphereListModel.remove(rightSphereList.getSelectedIndex());
            leftSphereListModel.addElement(temp);

            if (!rightSphereListModel.isEmpty()) {
                startLoggingBtn.setEnabled(false);
                if (!RemoteLog.ALL_SPHERES.equals(leftSphereListModel.elementAt(0))) {
                    leftSphereListModel.add(0, RemoteLog.ALL_SPHERES);
                } else if (RemoteLog.ALL_SPHERES.equals(temp)) {
                    leftSphereListModel.removeAllElements();
                    startLoggingBtn.setEnabled(false);
                    for (int i = 0; i < size; i++) {
                        leftSphereListModel.addElement(tempArray[i]);
                    }
                }
            }

            moveLeftBtn.setEnabled(false);
            moveRightBtn.setEnabled(false);
        }
    };

    /**
     * Thread that starts and stops the LoggingService.
     */
    private transient RemoteLogDetailsGUI remoteLogDetails;
    private transient RemoteLog remoteLog;
    private final transient WindowAdapter closeButtonListener = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent arg0) {
            shutGUI();
            super.windowClosing(arg0);
        }

        /**
         * Stops the GUI. Called when the zirk is shut down.
         */
        private void shutGUI() {
            try {
                remoteLog.enableLogging(false, false, false, null);
            } catch (Exception e) {
                logger.error("Error in stopping logging zirk. ", e);
            }
        }
    };

    private boolean isDeveloperModeEnabled;
    private final transient ItemListener developerModeListener = new ItemListener() {

        @Override
        public void itemStateChanged(ItemEvent itemEvent) {
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                isDeveloperModeEnabled = true;
            } else if (itemEvent.getStateChange() == ItemEvent.DESELECTED) {
                isDeveloperModeEnabled = false;
            }
        }
    };

    /**
     * Starts the logging Zirk by sending the {@link} on the wire to all the spheres and takes the
     * action to the logging screen.
     */
    private final transient ActionListener startLoggingButtonListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            String[] selectedSpheres = new String[rightSphereListModel.size()];

            for (int i = 0; i < rightSphereListModel.size(); i++) {
                selectedSpheres[i] = rightSphereListModel.getElementAt(i);
            }

            if (selectedSpheres[0].equals(RemoteLog.ALL_SPHERES)) {
                selectedSpheres = tempArray;
            }

            remoteLogDetails = new RemoteLogDetailsGUI(comms, selectedSpheres, thisFrame,
                    isDeveloperModeEnabled);
        }
    };

    /**
     * Starts the GUI and the Logging zirk.
     *
     * @param comms
     */
    public RemoteLogSphereSelectGUI(Comms comms) {
        thisFrame = this;
        this.comms = comms;
        jbInit();
    }

    /**
     * initialize the GUI with the components
     */
    // Ignore magic number warnings for GUI code
    @SuppressWarnings("squid:S109")
    private void jbInit() {
        this.setJMenuBar(menuBar);
        this.getContentPane().setLayout(frameLayout);
        this.setSize(new Dimension(628, 551));
        this.setTitle("Bezirk-Logging");
        this.setResizable(false);
        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.addWindowListener(closeButtonListener);

        final Dimension screenSize = Toolkit.getDefaultToolkit()
                .getScreenSize();
        final Dimension frameSize = this.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        this.setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
        menuSettings.setText("Settings");
        menuAboutUs.setText("About Us");
        framePanel.setLayout(null);
        sphereListLeftScroll.setBounds(new Rectangle(25, 110, 215, 285));

        selectSphereLbl.setText("Select sphere");
        selectSphereLbl.setBounds(new Rectangle(25, 85, 235, 20));

        listSphereBtn.setText("Get sphere List");
        listSphereBtn.setBounds(new Rectangle(25, 30, 135, 25));
        listSphereBtn.addActionListener(listSphereBtnListener);

        startLoggingBtn.setText("Start Logging");
        startLoggingBtn.setBounds(new Rectangle(180, 30, 135, 25));

        selectedSphereLbl.setText("Selected Spheres");
        selectedSphereLbl.setBounds(new Rectangle(360, 85, 235, 20));

        bezirkDeveloperCheckBox.setText("Enable Bezirk Developer Option");
        bezirkDeveloperCheckBox.setBounds(new Rectangle(25, 60, 325, 25));
        bezirkDeveloperCheckBox.addItemListener(developerModeListener);

        menuBar.add(menuSettings);
        menuBar.add(menuAboutUs);
        menuBar.setEnabled(false);

        settingsPanel.add(new JLabel("IP Address:"));
        settingsPanel.add(ipAddressTxt);
        settingsPanel.add(Box.createHorizontalStrut(15));
        settingsPanel.add(new JLabel("Port"));
        settingsPanel.add(portTxt);

        sphereListRightScroll.getViewport().add(rightSphereList, null);
        framePanel.add(sphereListRightScroll, null);
        framePanel.add(moveLeftBtn, null);
        framePanel.add(moveRightBtn, null);
        framePanel.add(startLoggingBtn, null);
        framePanel.add(listSphereBtn, null);
        framePanel.add(bezirkDeveloperCheckBox, null);
        framePanel.add(selectedSphereLbl, null);

        framePanel.add(selectSphereLbl, null);
        sphereListLeftScroll.getViewport().add(leftSphereList, null);
        framePanel.add(sphereListLeftScroll, null);
        leftSphereList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rightSphereList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        leftSphereList
                .addListSelectionListener(leftSphereSelectionListListener);
        rightSphereList
                .addListSelectionListener(rightSphereSelectionListListener);

        startLoggingBtn.setEnabled(false);

        startLoggingBtn.addActionListener(startLoggingButtonListener);

        moveRightBtn.setText(">>");
        moveRightBtn.setEnabled(false);
        moveRightBtn.setBounds(new Rectangle(270, 200, 50, 30));
        moveRightBtn.setFont(new Font("Tahoma", 0, 9));
        moveRightBtn.addActionListener(moveRightButtonActionListener);
        moveLeftBtn.setText("<<");
        moveLeftBtn.setEnabled(false);
        moveLeftBtn.setBounds(new Rectangle(270, 245, 50, 30));
        moveLeftBtn.setFont(new Font("Tahoma", 0, 9));
        moveLeftBtn.addActionListener(moveLeftButtonActionListener);
        sphereListRightScroll.setBounds(new Rectangle(360, 110, 215, 285));
        this.getContentPane().add(
                framePanel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 0), 604, 481));

    }

    @Override
    public void handleLogMessage(RemoteLoggingMessage logMessage) {
        logger.debug("inside handleLog message of RemoteLogSphereSelectGUI ");
        if (null != remoteLogDetails && msgLog != null) {
            logger.debug("remoteLogDetails and msgLog are not null");
            remoteLogDetails.updateTable(logMessage);
        }
    }
}
