package com.bezirk.ethernetconfigui;

import com.bezirk.network.IntfInetPair;

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
        final List<String> temp = new ArrayList<String>();
        IntfInetPair pair;
        while (iterator.hasNext()) {
            pair = iterator.next();
            temp.add(pair.getIntf().getName());
        }
        final String[] interfaceNames = temp.toArray(new String[temp.size()]);
        return (String) JOptionPane.showInputDialog(null,
                "Please select your active network for Bezirk to send and receive messages", "Bezirk Network Selection",
                JOptionPane.QUESTION_MESSAGE, null, interfaceNames, null);

    }

}