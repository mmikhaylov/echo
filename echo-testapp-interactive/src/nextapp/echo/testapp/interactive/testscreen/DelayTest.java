/* 
 * This file is part of the Echo Web Application Framework (hereinafter "Echo").
 * Copyright (C) 2002-2009 NextApp, Inc.
 *
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 */

package nextapp.echo.testapp.interactive.testscreen;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import nextapp.echo.app.Button;
import nextapp.echo.app.CheckBox;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Column;
import nextapp.echo.app.TextField;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.SplitPaneLayoutData;
import nextapp.echo.testapp.interactive.InteractiveApp;
import nextapp.echo.webcontainer.sync.component.TextComponentPeer;

/**
 * A test for handling of long-running server-interactions.
 */
public class DelayTest extends Column {
    
    private int clickCount = 0;
    private TextField textField;
    
    public DelayTest() {
        super();
        
        SplitPaneLayoutData splitPaneLayoutData = new SplitPaneLayoutData();
        splitPaneLayoutData.setInsets(new Insets(10));
        setLayoutData(splitPaneLayoutData);
        
        Button delayButton = new Button("Test 3 second delay");
        delayButton.setStyleName("Default");
        delayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                }
            }
        });
        add(delayButton);
        
        delayButton = new Button("Test 3 second delay, remove text field");
        delayButton.setStyleName("Default");
        delayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Thread.sleep(3000);
                    remove(textField);
                } catch (InterruptedException ex) {
                }
            }
        });
        add(delayButton);
        
        delayButton = new Button("Test 3 second delay, disable text field");
        delayButton.setStyleName("Default");
        delayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Thread.sleep(3000);
                    textField.setEnabled(false);
                } catch (InterruptedException ex) {
                }
            }
        });
        add(delayButton);
        
        delayButton = new Button("Test 3 second delay, set text field editable = false");
        delayButton.setStyleName("Default");
        delayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Thread.sleep(3000);
                    textField.setEditable(false);
                } catch (InterruptedException ex) {
                }
            }
        });
        add(delayButton);
        
        Button queryButton = new Button("Query Text");
        queryButton.setStyleName("Default");
        queryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                InteractiveApp.getApp().consoleWrite("\"" + textField.getText() + "\"");
            }
        });
        add(queryButton);
        
        final Button blockedButton = new Button("This button has been clicked 0 times");
        blockedButton.setStyleName("Default");
        blockedButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                blockedButton.setText("This button has been clicked " + ++clickCount + " times");
            }
        });
        add(blockedButton);
        
        Button immediateListenerButton = new Button("Receive input events immediately");
        immediateListenerButton.setStyleName("Default");
        immediateListenerButton.addActionListener(new ActionListener() {
        
            public void actionPerformed(ActionEvent e) {
                textField.set(TextComponentPeer.PROPERTY_SYNC_MODE, new Integer(TextComponentPeer.SYNC_ON_CHANGE));
                textField.addPropertyChangeListener(new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent e) {
                        InteractiveApp.getApp().consoleWrite("immediate: \"" + textField.getText() + "\"");
                    }
                });
            }
        });
        add(immediateListenerButton);
        
        textField = new TextField();
        textField.addActionListener(new ActionListener() {
        
            public void actionPerformed(ActionEvent e) {
                InteractiveApp.getApp().consoleWrite("TextField Action Event: " + e);
            }
        });
        add(textField);
        
        CheckBox checkBox = new CheckBox();
        add(checkBox);
    }
}
