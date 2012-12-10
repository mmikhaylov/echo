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

package chatclient;

import nextapp.echo.app.Button;
import nextapp.echo.app.Column;
import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Grid;
import nextapp.echo.app.Label;
import nextapp.echo.app.Row;
import nextapp.echo.app.SplitPane;
import nextapp.echo.app.TextField;
import nextapp.echo.app.WindowPane;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.GridLayoutData;

/**
 * Login screen <code>ContentPane</code>.
 */
public class LoginScreen extends ContentPane {

    private static final Extent PX_300 = new Extent(300, Extent.PX);

    private TextField nameField;
    
    /**
     * Creates a new <code>LoginScreen</code>.
     */
    public LoginScreen() {
        super();
        setStyleName("LoginScreen.ContentPane");
        
        Label label;

        Column column = new Column();
        column.setStyleName("LoginScreen.Column");
        add(column);
        
        label = new Label(Styles.NEXTAPP_LOG_IMAGE);
        column.add(label);
        
        label = new Label(Styles.ECHO_IMAGE);
        column.add(label);
        
        label = new Label(Styles.CHAT_EXAMPLE_IMAGE);
        column.add(label);
        
        WindowPane loginWindow = new WindowPane();
        loginWindow.setTitle(Messages.getString("LoginScreen.LoginWindowTitle"));
        loginWindow.setStyleName("LoginScreen.LoginWindow");
        loginWindow.setClosable(false);
        add(loginWindow);
        
        SplitPane splitPane = new SplitPane(SplitPane.ORIENTATION_VERTICAL_BOTTOM_TOP, new Extent(32));
        loginWindow.add(splitPane);
        
        Row controlRow = new Row();
        controlRow.setStyleName("ControlPane");
        splitPane.add(controlRow);
        
        Button button = new Button(Messages.getString("LoginScreen.Continue"), Styles.ICON_24_YES);
        button.setStyleName("ControlPane.Button");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processLogin();
            }
        });
        controlRow.add(button);

        Grid layoutGrid = new Grid();
        layoutGrid.setStyleName("LoginScreen.LayoutGrid");
        splitPane.add(layoutGrid);

        Column warningColumn = new Column();
        GridLayoutData gridLayoutData = new GridLayoutData();
        gridLayoutData.setColumnSpan(2);
        warningColumn.setLayoutData(gridLayoutData);
        layoutGrid.add(warningColumn);
        
        label = new Label(Messages.getString("LoginScreen.WarningTitle"));
        label.setStyleName("LoginScreen.WarningTitle");
        warningColumn.add(label);
        
        label = new Label(Messages.getString("LoginScreen.WarningMessage"));
        label.setStyleName("LoginScreen.WarningMessage");
        warningColumn.add(label);

        label = new Label(Messages.getString("LoginScreen.PromptUserName"));
        label.setStyleName("LoginScreen.Prompt");
        layoutGrid.add(label);
        
        nameField = new TextField();
        nameField.setWidth(PX_300);
        nameField.setStyleName("Default");
        nameField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processLogin();
            }
        });
        layoutGrid.add(nameField);
        
        ChatApp.getActive().setFocusedComponent(nameField);
    }
    
    /**
     * Processes a user log-in request.
     */
    private void processLogin() {
        if (!ChatApp.getApp().connect(nameField.getText())) {
            MessageDialog messageDialog = new MessageDialog(Messages.getString("LoginScreen.InvalidLogin.Title"),
                    Messages.getString("LoginScreen.InvalidLogin.Message"), MessageDialog.TYPE_ERROR, MessageDialog.CONTROLS_OK);
            getApplicationInstance().getDefaultWindow().getContent().add(messageDialog);
        }
    }
}
