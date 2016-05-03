package com.bezirk.devices;

import com.bezirk.commons.BezirkCompManager;
import com.bezirk.middleware.addressing.Location;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;

/**
 * This class is used to show the current Device Location which is represented as the fields within {@link Location}
 */
public class DeviceWindow extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(DeviceWindow.class);
    private static final long serialVersionUID = 1L;
    private static final String TAHOMA_FONT = "Tahoma";
    private final JTextField textFieldRegionName;
    private final JTextField textFieldIn;
    private final JTextField textFieldNear;

    /**
     * Create the frame.
     */
    public DeviceWindow() {
        final BezirkDeviceInterface upaDevice = BezirkCompManager.getUpaDevice();

        setTitle("Device Location Window");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 505, 300);
        final JPanel contentPane = new JPanel();
        contentPane.setBorder(new CompoundBorder());
        setContentPane(contentPane);
        contentPane.setLayout(null);

        final JLabel lblUpaDeviceLocation = new JLabel("UPA Device Location");
        lblUpaDeviceLocation.setFont(new Font(TAHOMA_FONT, Font.PLAIN, 23));
        lblUpaDeviceLocation.setBounds(37, 22, 220, 28);
        contentPane.add(lblUpaDeviceLocation);

        final JLabel lblRegionName = new JLabel("Region");
        lblRegionName.setFont(new Font(TAHOMA_FONT, Font.PLAIN, 15));
        lblRegionName.setForeground(Color.BLACK);
        lblRegionName.setBounds(80, 61, 95, 14);
        contentPane.add(lblRegionName);

        textFieldRegionName = new JTextField();
        textFieldRegionName.setBounds(234, 60, 155, 20);
        textFieldRegionName
                .setText(upaDevice.getDeviceLocation().getWideScope() == null ? ""
                        : upaDevice.getDeviceLocation().getWideScope());
        contentPane.add(textFieldRegionName);
        textFieldRegionName.setColumns(10);

        final JLabel lblIn = new JLabel("In");
        lblIn.setForeground(Color.BLACK);
        lblIn.setFont(new Font(TAHOMA_FONT, Font.PLAIN, 15));
        lblIn.setBounds(80, 97, 95, 14);
        contentPane.add(lblIn);

        textFieldIn = new JTextField();
        textFieldIn.setColumns(10);
        textFieldIn.setBounds(234, 96, 155, 20);
        textFieldIn
                .setText(upaDevice.getDeviceLocation().getIntermediateScope() == null ? ""
                        : upaDevice.getDeviceLocation().getIntermediateScope());
        contentPane.add(textFieldIn);

        final JLabel lblNear = new JLabel("Near");
        lblNear.setForeground(Color.BLACK);
        lblNear.setFont(new Font(TAHOMA_FONT, Font.PLAIN, 15));
        lblNear.setBounds(80, 133, 95, 14);
        contentPane.add(lblNear);

        textFieldNear = new JTextField();
        textFieldNear.setBounds(234, 132, 155, 19);
        textFieldNear.setText(upaDevice.getDeviceLocation() == null ? ""
                : upaDevice.getDeviceLocation().getNarrowScope());
        contentPane.add(textFieldNear);
        textFieldNear.setColumns(10);

        final JButton btnSave = new JButton("Save");
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                final Location inLocation = new Location(textFieldRegionName
                        .getText(), textFieldIn.getText(), textFieldNear
                        .getText());
                upaDevice.setDeviceLocation(inLocation);
                DeviceWindow.saveParamChanges();
            }
        });
        btnSave.setBounds(80, 214, 89, 23);
        contentPane.add(btnSave);

        final JButton btnReset = new JButton("Reset");
        btnReset.setBounds(337, 214, 89, 23);
        contentPane.add(btnReset);
    }

    public static void saveParamChanges() {
        final BezirkDeviceInterface upaDevice = BezirkCompManager.getUpaDevice();
        try {
            final Properties props = BezirkDeviceForPC.loadProperties();

            props.setProperty("DeviceLocation", upaDevice.getDeviceLocation()
                    .toString());
            BezirkDeviceForPC.storeProperties(props);

            logger.debug("Current device location: {}", upaDevice.getDeviceLocation().toString());
        } catch (Exception e) {
            logger.error("Problem reading or writing properties file: ", e);
        }
    }
}
