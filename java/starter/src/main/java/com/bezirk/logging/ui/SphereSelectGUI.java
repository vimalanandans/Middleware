/**
 * @author Manjunatha Kuruvatti (ManjunathaKuravatti.Chandrashekar@in.bosch.com)
 * @modified 12/2/2015
 */
package com.bezirk.logging.ui;

import com.bezirk.commons.BezirkCompManager;
import com.bezirk.comms.BezirkComms;
import com.bezirk.comms.BezirkCommunications;
import com.bezirk.middleware.objects.BezirkSphereInfo;
import com.bezirk.remotelogging.loginterface.BezirkLogging;
import com.bezirk.remotelogging.manager.BezirkLoggingManager;
import com.bezirk.remotelogging.messages.BezirkLoggingMessage;
import com.bezirk.remotelogging.service.BezirkLoggingService;
import com.bezirk.remotelogging.util.Util;

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
import java.util.Iterator;

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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Class that displays the GUI to select the spheres and start the logging Zirk.
 * {@link com.bezirk.remotelogging.logininterface.IUhuLogging}
 */
public final class SphereSelectGUI extends JFrame implements BezirkLogging {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(SphereSelectGUI.class);

    /**
     * GUI components
     */
    private final JMenuBar menuBar = new JMenuBar();
    private final JMenu menuSettings = new JMenu(), menuAboutUs = new JMenu();
    private final JTextField ipAddressTxt = new JTextField(13),
            portTxt = new JTextField(4);
    private final JPanel settingsPanel = new JPanel(),
            framePanel = new JPanel();
    private final GridBagLayout frameLayout = new GridBagLayout();
    private final JScrollPane sphereListLeftScroll = new JScrollPane(),
            sphereListRightScroll = new JScrollPane();
    private final JLabel selectSphereLbl = new JLabel(),
            selectedSphereLbl = new JLabel();
    private final DefaultListModel<String> leftSphereListModel = new DefaultListModel<String>(),
            rightSphereListModel = new DefaultListModel<String>();
    private final JList<String> leftSphereList = new JList<String>(
            leftSphereListModel), rightSphereList = new JList<String>(
            rightSphereListModel);
    private final JButton listSphereBtn = new JButton(),
            startLoggingBtn = new JButton(), moveRightBtn = new JButton(),
            moveLeftBtn = new JButton();
    private final JCheckBox uhuDeveloperChck = new JCheckBox();
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
        public void actionPerformed(ActionEvent arg0) {
            leftSphereListModel.removeAllElements();
            rightSphereListModel.removeAllElements();
            leftSphereListModel.addElement(Util.ANY_SPHERE);
            try {
                final Iterator<BezirkSphereInfo> sphereInfoIterator = BezirkCompManager
                        .getSphereUI().getSpheres().iterator();
                while (sphereInfoIterator.hasNext()) {
                    leftSphereListModel.addElement(sphereInfoIterator.next()
                            .getSphereID());
                }
            } catch (Exception ex) {
                logger.error("Error in sphere list model.", ex);
            }
        }
    };
    transient BezirkComms comms;
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

            final String temp = leftSphereListModel.elementAt(index).toString();
            if (temp.equals(Util.ANY_SPHERE)) {
                rightSphereListModel.addElement(Util.ANY_SPHERE);
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
                leftSphereListModel.removeElement(Util.ANY_SPHERE);
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
            final int index = rightSphereList.getSelectedIndex();
            rightSphereListModel.remove(index);
            leftSphereListModel.addElement(temp);
            if (!rightSphereListModel.isEmpty()) {
                startLoggingBtn.setEnabled(false);
                if (!leftSphereListModel.elementAt(0).equals(Util.ANY_SPHERE)) {
                    leftSphereListModel.add(0, Util.ANY_SPHERE);
                } else if (temp.equals(Util.ANY_SPHERE)) {
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
     *
     * @see BezirkLoggingService
     */
    private transient BezirkLogDetailsGUI uhu;
    private transient BezirkLoggingManager bezirkLoggingManager;
    private final transient WindowAdapter closeButtonListener = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent arg0) {
            shutGUI();
            super.windowClosing(arg0);
        }
    };
    private boolean isDeveoperModeEnabled;
    private final transient ItemListener developerModeListener = new ItemListener() {

        @Override
        public void itemStateChanged(ItemEvent itemEvent) {
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                isDeveoperModeEnabled = true;
            } else if (itemEvent.getStateChange() == ItemEvent.DESELECTED) {
                isDeveoperModeEnabled = false;
            }
        }
    };
    /**
     * Starts the logging Zirk by sending the {@link} on the wire to all the spheres and takes the action to the logging screen.
     */
    private final transient ActionListener startLoggingButtonListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            String[] selectedSpheres = new String[rightSphereListModel.size()];

            for (int i = 0; i < rightSphereListModel.size(); i++) {
                selectedSpheres[i] = rightSphereListModel.getElementAt(i);
            }

            if (selectedSpheres[0].equals(Util.ANY_SPHERE)) {
                selectedSpheres = tempArray;
            }

            uhu = new BezirkLogDetailsGUI(comms, selectedSpheres, thisFrame,
                    isDeveoperModeEnabled);
        }
    };

    /**
     * Starts the GUI and the Logging zirk.
     *
     * @param comms
     */
    public SphereSelectGUI(BezirkComms comms) {
        thisFrame = this;
        this.comms = comms;
        try {
            jbInit();
        } catch (Exception e) {
            logger.error("Error in sphere select GUI init. ", e);
        }

        try {
            bezirkLoggingManager = new BezirkLoggingManager();
            bezirkLoggingManager.startLoggingService(
                    BezirkCommunications.getREMOTE_LOGGING_PORT(), this);
        } catch (Exception e) {
            logger.error("Error in sphere Select GUI init.", e);
        }
    }

    /**
     * initialize the GUI with the components
     *
     * @throws Exception
     */
    private void jbInit() throws Exception {

        this.setJMenuBar(menuBar);
        this.getContentPane().setLayout(frameLayout);
        this.setSize(new Dimension(628, 551));
        this.setTitle(LoggingGUILabels.LABEL_FRAME_HEADER);
        this.setResizable(false);
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
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
        menuSettings.setText(LoggingGUILabels.LABEL_SETTINGS);
        menuAboutUs.setText(LoggingGUILabels.LABEL_ABOUT_US);
        framePanel.setLayout(null);
        sphereListLeftScroll.setBounds(new Rectangle(25, 110, 215, 285));

        selectSphereLbl.setText(LoggingGUILabels.LABEL_SELECT_SPHERE);
        selectSphereLbl.setBounds(new Rectangle(25, 85, 235, 20));

        listSphereBtn.setText(LoggingGUILabels.LABEL_GET_SPHERE_LIST);
        listSphereBtn.setBounds(new Rectangle(25, 30, 135, 25));
        listSphereBtn.addActionListener(listSphereBtnListener);

        startLoggingBtn.setText(LoggingGUILabels.LABEL_START_LOGGING);
        startLoggingBtn.setBounds(new Rectangle(180, 30, 135, 25));

        selectedSphereLbl.setText(LoggingGUILabels.LABEL_SELECTED_SPHERE);
        selectedSphereLbl.setBounds(new Rectangle(360, 85, 235, 20));

        uhuDeveloperChck.setText(LoggingGUILabels.LABEL_ENABLE_DEVELOPER_MODE);
        uhuDeveloperChck.setBounds(new Rectangle(25, 60, 325, 25));
        uhuDeveloperChck.addItemListener(developerModeListener);

        menuBar.add(menuSettings);
        menuBar.add(menuAboutUs);
        menuBar.setEnabled(false);

        settingsPanel.add(new JLabel(LoggingGUILabels.LABEL_IP_ADDRESS));
        settingsPanel.add(ipAddressTxt);
        settingsPanel.add(Box.createHorizontalStrut(15)); // spacer between
        // textbox
        settingsPanel
                .add(new JLabel(LoggingGUILabels.LABEL_REMOTE_LOGGING_PORT));
        settingsPanel.add(portTxt);

        sphereListRightScroll.getViewport().add(rightSphereList, null);
        framePanel.add(sphereListRightScroll, null);
        framePanel.add(moveLeftBtn, null);
        framePanel.add(moveRightBtn, null);
        framePanel.add(startLoggingBtn, null);
        framePanel.add(listSphereBtn, null);
        framePanel.add(uhuDeveloperChck, null);
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

    /**
     * Stops the GUI. Called when the zirk is shut down.
     */
    public void shutGUI() {
        if (this != null) {
            try {
                bezirkLoggingManager.stopLoggingService();
            } catch (Exception e) {
                logger.error("Error in stopping logging zirk. ", e);
            }
        }

    }

    @Override
    public void handleLogMessage(BezirkLoggingMessage uhuLogMessage) {
        if (null != uhu) {

            uhu.updateTable(uhuLogMessage);
        }
    }

}
