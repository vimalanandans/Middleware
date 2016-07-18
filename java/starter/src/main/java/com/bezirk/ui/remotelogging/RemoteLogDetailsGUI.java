package com.bezirk.ui.remotelogging;

import com.bezirk.BezirkCompManager;
import com.bezirk.comms.Comms;
import com.bezirk.remotelogging.RemoteLoggingMessage;
import com.bezirk.remotelogging.RemoteLog;

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

    RemoteLog msgLog = null;
    /*
     * GUI Components
     */
    private final GridBagLayout gridBagDialogLayout = new GridBagLayout();
    private final JPanel headerPanel = new JPanel(),
            tableHolderPanel = new JPanel();
    private final JLabel selectedSphereLbl = new JLabel();
    private final JButton clearLogBtn = new JButton();
    private final JFrame sphereSelectFrame, currentFrame;
    private final String[] selectedSpheres;
    private final boolean isDeveloperModeEnabled;
    /**
     * Linked HashMap that is used to store the logger messages and update them.
     */
    private final transient Map<String, Integer> logMsgMap = new LinkedHashMap<String, Integer>(
            SIZE_OF_LOG_MSG_MAP);
    /**
     * To print the timestamp of the received msg
     */
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS", Locale.GERMANY);
    transient Comms comms;
    private final transient WindowAdapter closeButtonListener = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent arg0) {
            showCancelDialog();
            super.windowClosing(arg0);
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
     *
     */
    public RemoteLogDetailsGUI(Comms comms, String[] spheres, JFrame frame,
                               boolean isDeveloperModeEnabled) {
        this.sphereSelectFrame = frame;
        selectedSpheres = spheres.clone();
        this.isDeveloperModeEnabled = isDeveloperModeEnabled;
        currentFrame = this;
        try {
            jbInit();
        } catch (Exception e1) {
            logger.debug("Error in Log Details GUI.", e1);
        }

        sendLoggingServiceMsg(true);

        this.comms = comms;
    }

    /**
     * init the GUI
     *
     * @throws Exception
     */
    private void jbInit() {

        this.setVisible(true);
        this.getContentPane().setLayout(gridBagDialogLayout);
        this.setSize(new Dimension(1266, 842));
        this.setTitle(LoggingGUILabels.LABEL_FRAME_HEADER);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        headerPanel.setLayout(null);
        tableHolderPanel.setLayout(null);
        selectedSphereLbl.setBounds(new Rectangle(15, 15, 745, 25));
        clearLogBtn.setText(LoggingGUILabels.LABEL_BUTTON_CLEAR_LOG);
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
        model.addColumn(LoggingGUILabels.LABEL_SPHERE);
        model.addColumn(LoggingGUILabels.LABEL_UNIQUE_ID);
        model.addColumn(LoggingGUILabels.LABEL_SENDER);
        model.addColumn(LoggingGUILabels.LABEL_RECIPIENT);
        model.addColumn(LoggingGUILabels.LABEL_TOPIC);
        model.addColumn(LoggingGUILabels.LABEL_NO_OF_RECEIVERS);

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

    private void showCancelDialog() {
        final int returnValue = JOptionPane.showConfirmDialog(null,
                LoggingGUILabels.LABEL_CONFIRM_DIALOG,
                LoggingGUILabels.LABEL_CONFIRM_DIALOG_TITLE,
                JOptionPane.OK_OPTION);
        if (returnValue == JOptionPane.OK_OPTION) {
            sphereSelectFrame.setVisible(true);
            currentFrame.dispose();
            shutLoggingGUI();
        }
    }

    /**
     * Shut the logging Zirk
     */
    public void shutLoggingGUI() {
        sendLoggingServiceMsg(false);
    }

    private void sendLoggingServiceMsg(boolean isActivateLogging) {

        msgLog.setLogger(isActivateLogging,selectedSpheres);

    }

    public void updateTable(RemoteLoggingMessage bezirkLogMessage) {

        if (!isDeveloperModeEnabled){

            return;
        }

        final StringBuilder tempMapKey = new StringBuilder();
        tempMapKey.append(bezirkLogMessage.uniqueMsgId).append(':').append(bezirkLogMessage.sphereName);

        if (checkEntry(tempMapKey.toString())) {
            try {
                model.addRow(new Object[]{
                        getSphereNameFromSphereId(bezirkLogMessage.sphereName),
                        sdf.format(Long.valueOf(bezirkLogMessage.timeStamp)),
                        bezirkLogMessage.sender,
                        getDeviceNameFromDeviceId(bezirkLogMessage.recipient),
                        bezirkLogMessage.topic, 0});
            } catch (Exception e) {
                logger.error("Error in updating the table", e);
            }
        } else {
            int tempValue = (int) model.getValueAt(
                    logMsgMap.get(tempMapKey.toString()), 5);
            model.setValueAt(++tempValue, logMsgMap.get(tempMapKey.toString()),
                    5);
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

    /**
     * Returns the DeviceName associated with the deviceId
     *
     * @param deviceId Device Id whose name is to be fetched
     * @return DeviceName if exists, null otherwise
     */
    private String getDeviceNameFromDeviceId(final String deviceId) {
        if (deviceId == null) {

            return RECIPIENT_MULTICAST_VALUE;
        }
        // Fixme : tempDeviceName get the device name from SphereServiceAccess
        //      object to access the info
        final String tempDeviceName = null;
        // final String tempDeviceName = BezirkCompManager.getSphereForPubSubBroker() \
       //         .getDeviceNameFromSphere(deviceId);
        return (null == tempDeviceName) ? deviceId : tempDeviceName;
    }

    /**
     * Gets the sphere name from the Sphere UI if available, "Un-defined" if not available.
     *
     * @param sphereId SphereId of the sphere
     * @return sphere Name associated with the sphere Id.
     */
    private String getSphereNameFromSphereId(final String sphereId) {
        final StringBuilder tempSphereName = new StringBuilder();
        try {
           /* commented for the MVP refactoring. inject the sphere API to get access
            tempSphereName.append(BezirkCompManager.getSphereUI()
                    .getSphere(sphereId).getSphereName());*/
        } catch (NullPointerException ne) {
            logger.error("Error in fetching sphereName from sphere UI", ne);
            tempSphereName.append("Un-defined");
        }
        return tempSphereName.toString();
    }
}
