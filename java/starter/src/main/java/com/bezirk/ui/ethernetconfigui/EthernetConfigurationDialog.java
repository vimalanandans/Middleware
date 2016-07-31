package com.bezirk.ui.ethernetconfigui;

import com.bezirk.networking.IntfInetPair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

public class EthernetConfigurationDialog {

    private final Iterator<IntfInetPair> iterator;

    public EthernetConfigurationDialog(Iterator<IntfInetPair> iterator) {
        this.iterator = iterator;
    }

    public String showDialog() {
        final List<String> temp = new ArrayList<>();
        IntfInetPair pair;
        while (iterator.hasNext()) {
            pair = iterator.next();
            temp.add(pair.getIntf().getName());
        }
        final String[] interfaceNames = temp.toArray(new String[temp.size()]);
        return (String) JOptionPane.showInputDialog(null,
                "Choose Interface Name", "Bezirk Ethernet Configuration",
                JOptionPane.QUESTION_MESSAGE, null, interfaceNames, null);

    }

}