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
import com.bezirk.middleware.core.sphere.api.SphereAPI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Class that shows the Log Details of the connected logging clients.
 */
public class RemoteLogDetailsGUI extends JFrame {
    private static final long serialVersionUID = 1210684068159783241L;

    private static final Logger logger = LoggerFactory.getLogger(RemoteLogDetailsGUI.class);
    /**
     * Value to be displayed for the Recipient during MULTICAST
     */
    private static final String RECIPIENT_MULTICAST_VALUE = "MULTI-CAST";
    private static final int SIZE_OF_LOG_MSG_MAP = 128;

    private final RemoteLog msgLog = null;
    /*
     * GUI Components
     */
    private final GridBagLayout gridBagDialogLayout = new GridBagLayout();
    private final JPanel headerPanel = new JPanel();
    private final JPanel tableHolderPanel = new JPanel();
    private final JLabel selectedSphereLbl = new JLabel();
    private final JButton clearLogBtn = new JButton();
    private final JFrame sphereSelectFrame;
    private final JFrame currentFrame;
    private final String[] selectedSpheres;
    private final boolean isDeveloperModeEnabled;
    /**
     * Linked HashMap that is used to store the logger messages and update them.
     */
    private final transient Map<String, Integer> logMsgMap = new LinkedHashMap<>(
            SIZE_OF_LOG_MSG_MAP);
    /**
     * To print the timestamp of the received msg
     */
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS", Locale.GERMANY);
    private final transient Comms comms;
    private final transient WindowAdapter closeButtonListener = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent windowEvent) {
            showCancelDialog();
            super.windowClosing(windowEvent);
        }

        private void showCancelDialog() {
            final int returnValue = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to exit Logging?\nAll the logged data will be lost!",
                    "Confirm Exit", JOptionPane.YES_NO_OPTION);
            if (returnValue == JOptionPane.YES_OPTION) {
                sphereSelectFrame.setVisible(true);
                currentFrame.dispose();
                shutLoggingGUI();
            }
        }

        /**
         * Shut the logging Zirk
         */
        private void shutLoggingGUI() {
            sendLoggingServiceMsg(false);
        }
    };
    private DefaultTableModel model = new DefaultTableModel(0, 0) {
        private static final long serialVersionUID = 3243397041333717170L;

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable logTbl = new JTable(model);
    private final transient ActionListener logClearButtonListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent event) {
            model = (DefaultTableModel) logTbl.getModel();
            model.getDataVector().removeAllElements();
            logTbl.updateUI();
        }
    };

    /**
     * start the loggingService Processors and init the GUI
     */
    public RemoteLogDetailsGUI(Comms comms, String[] spheres, JFrame frame,
                               boolean isDeveloperModeEnabled) {
        this.sphereSelectFrame = frame;
        selectedSpheres = spheres.clone();
        this.isDeveloperModeEnabled = isDeveloperModeEnabled;
        currentFrame = this;
        jbInit();
        sendLoggingServiceMsg(true);

        this.comms = comms;
    }

    /**
     * Returns the DeviceName associated with the deviceId
     *
     * @param deviceId Device Id whose name is to be fetched
     * @return DeviceName if exists, null otherwise
     */
    private static String getDeviceNameFromDeviceId(final String deviceId) {
        if (deviceId == null) {
            return RECIPIENT_MULTICAST_VALUE;
        }

        return deviceId;
    }

    /**
     * Gets the sphere name from the Sphere UI if available, "Un-defined" if not available.
     *
     * @param sphereId SphereId of the sphere
     * @return sphere Name associated with the sphere Id.
     */
    private static String getSphereNameFromSphereId(final String sphereId) {
        final StringBuilder tempSphereName = new StringBuilder();

        SphereAPI sphereAPI = null;
        if (sphereAPI != null && sphereAPI.getSphere(sphereId) != null &&
                sphereAPI.getSphere(sphereId).getSphereName() != null) {
            logger.debug("sphereAPI is not null in RemoteLogDetailsGUI");
            tempSphereName.append(sphereAPI.getSphere(sphereId).getSphereName());
        } else {
            logger.debug("sphereAPI is null in RemoteLogDetailsGUI");
        }

        return tempSphereName.toString();
    }

    // Ignore magic number warnings for GUI code
    @SuppressWarnings("squid:S109")
    private void jbInit() {
        this.setVisible(true);
        this.getContentPane().setLayout(gridBagDialogLayout);
        this.setSize(new Dimension(1266, 842));
        this.setTitle("Bezirk-Logging");
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        headerPanel.setLayout(null);
        tableHolderPanel.setLayout(null);
        selectedSphereLbl.setBounds(new Rectangle(15, 15, 745, 25));
        clearLogBtn.setText("Clear Log");
        clearLogBtn.setBounds(new Rectangle(1050, 10, 100, 25));

        selectedSphereLbl.setText("Selected Spheres: "
                + Arrays.asList(selectedSpheres).toString());

        clearLogBtn.addActionListener(logClearButtonListener);
        headerPanel.add(selectedSphereLbl, null);
        headerPanel.add(clearLogBtn, null);
        tableHolderPanel.setLayout(new BorderLayout());
        tableHolderPanel.add(new JScrollPane(logTbl), BorderLayout.CENTER);

        this.addWindowListener(closeButtonListener);

        selectedSphereLbl.setVisible(true);
        logTbl.setPreferredScrollableViewportSize(logTbl.getPreferredSize());
        logTbl.setBounds(new Rectangle(0, 0, 685, 680));
        model.addColumn("Sphere");
        model.addColumn("Timestamp");
        model.addColumn("Sender");
        model.addColumn("Recipient");
        model.addColumn("Topic");
        model.addColumn("# - Receivers");

        this.getContentPane().add(
                headerPanel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 0), 1175, 52));

        this.getContentPane().add(
                tableHolderPanel,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 0), 1153, 675));

        sphereSelectFrame.setVisible(false);

        this.pack();

    }

    private void sendLoggingServiceMsg(boolean isActivateLogging) {
        if (msgLog != null) {
            msgLog.enableLogging(isActivateLogging, false, true, selectedSpheres);
        } else {
            logger.debug("Attempting to send logging service msg with null message log");
        }
    }

    public void updateTable(RemoteLoggingMessage bezirkLogMessage) {
        if (!isDeveloperModeEnabled) {
            return;
        }

        final StringBuilder tempMapKey = new StringBuilder();
        tempMapKey.append(bezirkLogMessage.uniqueMsgId).append(':').append(bezirkLogMessage.sphereName);
        logger.debug("tempMapKey is {}", tempMapKey);
        if (checkEntry(tempMapKey.toString())) {
            try {
                model.addRow(new Object[]{
                        getSphereNameFromSphereId(bezirkLogMessage.sphereName),
                        sdf.format(Long.valueOf(bezirkLogMessage.timeStamp)),
                        bezirkLogMessage.sender,
                        getDeviceNameFromDeviceId(bezirkLogMessage.recipient), 0});
            } catch (Exception e) {
                logger.error("Error in updating the table", e);
            }
        } else {
            int tempValue = (int) model.getValueAt(logMsgMap.get(tempMapKey.toString()), 5) + 1;
            model.setValueAt(tempValue, logMsgMap.get(tempMapKey.toString()), 5);
        }
    }

    private boolean checkEntry(final String tempMapKey) {
        if (logMsgMap.containsKey(tempMapKey)) {
            return false;
        } else {
            logMsgMap.put(tempMapKey, logTbl.getRowCount());
            return true;
        }
    }
}
