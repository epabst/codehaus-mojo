package org.codehaus.mojo.graphing.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;

public class KeyAction extends AbstractAction
{
    private String command;
    private ActionListener listener;

    /**
     * Create KeyAction
     * 
     * @param listener
     * @param command
     */
    public KeyAction(ActionListener listener, String command) {
        this.listener = listener;
        this.command = command;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        ActionEvent nevt = new ActionEvent(e.getSource(), e.getID(),
                command, e.getWhen(), e.getModifiers());
        this.listener.actionPerformed(nevt);
    }
}
