package com.bosch.upa.uhu.commstest.ui;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author AJC6KOR
 *
 */
public class CommsTestActionPerformer {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(CommsTestActionPerformer.class);

    // data member to store ping button click count
    Integer pingCount = 0;

    void settingsBtnActionPerformed(CommsTest commsTest,
            JTextArea statusDisplayTxt, JButton startBtn, JButton pingBtn) {
        final JTextField multicastSendingPort = new JTextField(
                String.valueOf(CommsTestConstants.DEFAULT_MULTICAST_SENDING_PORT));
        final JTextField multicastReceivingPort = new JTextField(
                String.valueOf(CommsTestConstants.DEFAULT_MULTICAST_RECEIVING_PORT));
        final JTextField unicastReceivingPort = new JTextField(
                String.valueOf(CommsTestConstants.DEFAULT_UNICAST_RECEIVING_PORT));
        final JTextField unicastSendingPort = new JTextField(
                String.valueOf(CommsTestConstants.DEFAULT_UNICAST_SENDING_PORT));
        final JTextField timer = new JTextField(
                String.valueOf(CommsTestConstants.DEFAULT_TIMER_VALUE));
        final JComponent[] inputs = new JComponent[] {
                new JLabel("Unicast Sending Port"), unicastSendingPort,
                new JLabel("Unicast Receiving Port"), unicastReceivingPort,
                new JLabel("Multicast Sending Port"), multicastSendingPort,
                new JLabel("Multicast Receiving Port"), multicastReceivingPort,
                new JLabel("Waiting Time To Receive Responses(in MilliSec)"),
                timer };
        final int res = JOptionPane.showConfirmDialog(null, inputs,
                CommsTestConstants.SETTINGS_DIALOG_TITLE,
                JOptionPane.PLAIN_MESSAGE, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            if (arePortsNumeric(unicastSendingPort, unicastReceivingPort,
                    multicastSendingPort, multicastReceivingPort)
                    || timer.getText().toString().isEmpty()) {

                commsTest
                        .updateConfiguration(Integer
                                .parseInt(unicastSendingPort.getText().trim()),
                                Integer.parseInt(unicastReceivingPort.getText()
                                        .trim()), Integer
                                        .parseInt(multicastSendingPort
                                                .getText().trim()), Integer
                                        .parseInt(multicastReceivingPort
                                                .getText().trim()));

                pingCount = 0;
                updatePingButton(pingBtn);
                statusDisplayTxt.setText("");
                if ("STOP".equals(startBtn.getText())) {
                    startBtn.doClick();
                }

            } else {
                // show popup
                LOGGER.error("Error");
            }

        }
    }

    private boolean arePortsNumeric(JTextField unicastSendingPort,
            JTextField unicastReceivingPort, JTextField multicastSendingPort,
            JTextField multicastReceivingPort) {

        return isNumeric(unicastSendingPort.getText().toString())
                || isNumeric(unicastReceivingPort.getText().toString())
                || isNumeric(multicastSendingPort.getText().toString())
                || isNumeric(multicastReceivingPort.getText().toString());

    }

    void startBtnActionPerformed(JButton startBtn, CommsTest commsTest) {
        // start button is clicked

        if (startBtn.getText().equals(CommsTestConstants.START_BUTTON_LABEL)) {
            startBtn.setText(CommsTestConstants.STOP_BUTTON_LABEL);
            commsTest.startCommsReceiverThread();
        } else {
            startBtn.setText(CommsTestConstants.START_BUTTON_LABEL);
            commsTest.stopCommsReceiverThread();
        }
    }

    void pingBtnActionPerformed(JTextArea statusDisplayTxt,
            CommsTest commsTest, JButton pingBtn) {
        statusDisplayTxt.setText("");
        ++pingCount;
        commsTest.sendPing(pingCount);
        updatePingButton(pingBtn);
    }

    void clearBtnActionPerformed(JTextArea statusDisplayTxt) {
        statusDisplayTxt.setText("");
    }

    void infoBtnActionPerformed() {
        final JComponent[] inputs = new JComponent[] {
                new JLabel("1. Check the network connection."),
                new JLabel("2. Check if all the devices are in same network."),
                new JLabel("3. Check wifi is enabled on all devices."),
                new JLabel(
                        "4. Check the comms is started on all the devices( When the config changes, you need to manually start it."),
                new JLabel(
                        "5. Make sure your device is not conncted to any VPN networks!"),
                new JLabel(
                        "6. If still the communication does not happen, contact the platform team.") };
        JOptionPane
                .showMessageDialog(null, inputs,
                        CommsTestConstants.HINT_DIALOG_TITLE,
                        JOptionPane.PLAIN_MESSAGE);

    }

    void updatePingButton(JButton pingBtn) {
        pingBtn.setText("TEST - " + pingCount);
    }

    /**
     * Method to validate port numbers entered
     * 
     * @param str
     */
    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            int portLength = 4;
            if (str.length() == portLength) {
                return true;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return false;
    }

}