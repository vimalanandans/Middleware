package com.bezirk.sphere.ui;

import com.bezirk.commons.BezirkVersion;
import com.bezirk.commstest.ui.CommsTestJFrame;
import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.sphere.api.BezirkSphereAPI;
import com.bezirk.sphere.impl.BezirkSphere;
import com.bezrik.network.BezirkNetworkUtilities;
import com.bezrik.network.IntfInetPair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

/**
 * @author Jan Zibuschka (jan.zibuschka@de.bosch.com) GUI for demoing/testing
 *         Spheres (mostly auto-generated)
 */
public class SphereManagementGUI extends JFrame {
    private static final long serialVersionUID = -5590619380386034560L;
    private static final Logger logger = LoggerFactory.getLogger(SphereManagementGUI.class);
    private static SphereManagementGUI staticThis;
    private JLabel warningLbl;
    private JMenuBar menuBar;
    private transient String misMatchVersion = BezirkVersion.UHU_VERSION;
    private transient BezirkSphere sphereManager;

    /**
     * Create the frame. (autogenerated code from WindowBuilder)
     */
    public SphereManagementGUI() {
        initSphereManagement();
    }
    public SphereManagementGUI(BezirkSphereAPI sphereForPC) {
        this.sphereManager = (BezirkSphere) sphereForPC;
        initSphereManagement();
    }

    public static SphereManagementGUI getStaticThis() {
        return staticThis;
    }

    public static void setStaticThis(SphereManagementGUI staticThis) {
        SphereManagementGUI.staticThis = staticThis;
    }

    /**
     * Launch the application. (autogenerated code from WindowBuilder)
     */
    public static void main(String... args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    final SphereManagementGUI frame = new SphereManagementGUI();
                    frame.setVisible(true);
                } catch (Exception e) {
                    logger.error("Error in initializing sphere management GUI. ", e);
                }
            }
        });
    }

    private void initSphereManagement() {
        staticThis = this;
        setTitle("sphere Management");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(menuBar);

        setBounds(10, 10, 902, 829);
        final JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        menuBar = new JMenuBar();

        // build the menu for catch code display and entry
        final JMenu settingsMenu = new JMenu("Setting");
        prepareSettingsMenu(settingsMenu);
        menuBar.add(settingsMenu);
        setJMenuBar(menuBar);

        JLabel lblQR;
        try {
            lblQR = new JLabel(new ImageIcon(com.bezirk.sphere.SphereManager.getUhuQRCode()
                    .getQRCode()));
            lblQR.setBounds(100, 100, 600, 600);
            contentPane.add(lblQR);
        } catch (Exception e1) {
            logger.error("Exception in setting QRCode.", e1);
        }

        final Timer timer = new Timer(500, new TimerListener());
        timer.start();

        // put the warning icon and set visibility to false
        warningLbl = prepareWarningLabel();
        contentPane.add(warningLbl);

    }

    private JLabel prepareWarningLabel() {
        warningLbl = new JLabel(new ImageIcon(getClass().getResource(
                "/ic_warning.png")));
        warningLbl.setBounds(new Rectangle(20, 20, 1600, 40));
        warningLbl.setVisible(false);

        final com.bezirk.util.TestUIMouseListener testUIMouseListener = new com.bezirk.util.TestUIMouseListener("sphereUI", null, 0, misMatchVersion);

        warningLbl.addMouseListener(testUIMouseListener);
        return warningLbl;
    }

    private void prepareSettingsMenu(final JMenu settingsMenu) {
        final JMenuItem interfaceMenuItem = new JMenuItem(
                "Select Network Interface");
        final JMenuItem catchMenuItem = new JMenuItem("Catch sphere Code Entry");
        final JMenuItem displaySphereCodeMenuItem = new JMenuItem(
                "Catch sphere Code Display");
        final JMenuItem commsTestMenuItem = new JMenuItem("Comms Test");

        interfaceMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                selectInterface();
            }
        });
        catchMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                spherePCCatch(SphereManagementGUI.this);
            }
        });
        displaySphereCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                displayCatchCode(SphereManagementGUI.this);
            }
        });

        commsTestMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                displayCommsTestGUI();
            }
        });

        settingsMenu.add(interfaceMenuItem);
        settingsMenu.add(catchMenuItem);
        settingsMenu.add(displaySphereCodeMenuItem);
        settingsMenu.add(commsTestMenuItem);
    }

    private void selectInterface() {
        final Iterator<IntfInetPair> itr = BezirkNetworkUtilities.getIntfInetPair()
                .iterator();
        final List<String> temp = new ArrayList<String>();
        IntfInetPair pair;
        while (itr.hasNext()) {
            pair = itr.next();
            temp.add(pair.getIntf().getName());
        }
        logger.debug(temp.toString());
        final String[] interfaceName = temp.toArray(new String[temp.size()]);

        final String result = (String) JOptionPane.showInputDialog(null,
                "Choose Interface Name", "Bezirk Ethernet Configuration",
                JOptionPane.QUESTION_MESSAGE, null, interfaceName, null);
        try {
            final String classpath = UPADeviceInterface.class.getClassLoader()
                    .getResource("comms.properties").getPath();
            final Properties properties = new Properties();
            final FileInputStream configStream = new FileInputStream(classpath);
            properties.load(configStream);
            configStream.close();

            properties.setProperty("InterfaceName", result);
            final FileOutputStream output = new FileOutputStream(classpath);
            properties.store(output, null);
            output.close();
        } catch (Exception e) {
            logger.error("Could not read comms properties file", e);
        }

    }

    private void spherePCCatch(JFrame frame) {

        final String catchCode = (String) JOptionPane.showInputDialog(frame,
                "Catch the PC\n", "Enter below the catch code for other PC",
                JOptionPane.PLAIN_MESSAGE, null, null, "");

        if (catchCode != null && !catchCode.isEmpty()) {
            // send the catch code to the sphere
            sphereManager.processCatchShortCode(catchCode);
        }
    }

    private void displayCatchCode(JFrame frame) {

        // get the catch code from the sphere

        final String defaultSphereCode = com.bezirk.sphere.SphereManager.getUhuQRCode()
                .getDefaultSphereCode();
        final String displayString = "Enter the Catch code : " + defaultSphereCode;

        JOptionPane.showMessageDialog(frame, displayString);

    }

    public void showWarningIcon(boolean showWarning, String misMatchVersion) {
        if (showWarning) {
            this.misMatchVersion = misMatchVersion;
            warningLbl.setVisible(true);
        }
    }

    private void displayCommsTestGUI() {
        final JFrame frame = new CommsTestJFrame();
        frame.setVisible(true);
    }

    /**
     * @author Jan Zibuschka
     *         <p/>
     *         TimerListener; regularly refreshes list of known Spheres
     */
    private static class TimerListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            // available in commit ID : 3f11ad216f9a0551155ef4fc4b27ebfb264757e1
        }

    }

}