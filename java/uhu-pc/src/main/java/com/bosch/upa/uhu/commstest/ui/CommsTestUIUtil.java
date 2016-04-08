package com.bosch.upa.uhu.commstest.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JOptionPane;

import com.bosch.upa.uhu.commons.UhuCompManager;

/**
 * @author AJC6KOR
 *
 */
public class CommsTestUIUtil {
    
    MouseListener getMouseListener(final CommsTest commsTest, final CommsTestActionPerformer actionPerformer) {
        return new MouseListener() {
            
            @Override
            public void mouseReleased(MouseEvent e) {
                //Nothing to be done
                
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
              //Nothing to be done
                
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
              //Nothing to be done
                
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
              //Nothing to be done
                
                
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                String receivedMsgs = commsTest.getSelectedServices(UhuCompManager.getUpaDevice().getDeviceName()+":"+(actionPerformer.pingCount));
                if(receivedMsgs != null && !receivedMsgs.isEmpty()){
                    JOptionPane.showMessageDialog(null, receivedMsgs, "DEVICES THAT RESPONDED TO PING MESSAGE", JOptionPane.PLAIN_MESSAGE);
                }
            }
        };
    }

}
