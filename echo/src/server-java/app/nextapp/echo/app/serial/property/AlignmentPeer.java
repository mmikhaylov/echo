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

package nextapp.echo.app.serial.property;

import org.w3c.dom.Element;

import nextapp.echo.app.Alignment;
import nextapp.echo.app.serial.SerialContext;
import nextapp.echo.app.serial.SerialException;
import nextapp.echo.app.serial.SerialPropertyPeer;
import nextapp.echo.app.util.ConstantMap;
import nextapp.echo.app.util.Context;
import nextapp.echo.app.util.DomUtil;

/**
 * <code>SerialPropertyPeer</code> for <code>Alignment</code> properties.
 */
public class AlignmentPeer 
implements SerialPropertyPeer {
    
    private static final ConstantMap HORIZONTAL_CONSTANTS = new ConstantMap();
    static {
        HORIZONTAL_CONSTANTS.add(Alignment.LEADING, "leading");
        HORIZONTAL_CONSTANTS.add(Alignment.TRAILING, "trailing");
        HORIZONTAL_CONSTANTS.add(Alignment.LEFT, "left");
        HORIZONTAL_CONSTANTS.add(Alignment.CENTER, "center");
        HORIZONTAL_CONSTANTS.add(Alignment.RIGHT, "right");
    }
    
    private static final ConstantMap VERTICAL_CONSTANTS = new ConstantMap();
    static {
        VERTICAL_CONSTANTS.add(Alignment.TOP, "top");
        VERTICAL_CONSTANTS.add(Alignment.CENTER, "center");
        VERTICAL_CONSTANTS.add(Alignment.BOTTOM, "bottom");
    }

    /**
     * @see nextapp.echo.app.serial.SerialPropertyPeer#toProperty(Context, 
     *      Class, org.w3c.dom.Element)
     */
    public Object toProperty(Context context, Class objectClass, Element propertyElement) 
    throws SerialException {
        Element alignmentElement = DomUtil.getChildElementByTagName(propertyElement, "a");
        String horizontal = alignmentElement.getAttribute("h");
        String vertical = alignmentElement.getAttribute("v");
        return new Alignment(HORIZONTAL_CONSTANTS.get(horizontal, Alignment.DEFAULT),
                VERTICAL_CONSTANTS.get(vertical, Alignment.DEFAULT));
    }

    /**
     * @see nextapp.echo.app.serial.SerialPropertyPeer#toXml(nextapp.echo.app.util.Context,
     *      java.lang.Class, org.w3c.dom.Element, java.lang.Object)
     */
    public void toXml(Context context, Class objectClass, Element propertyElement, Object propertyValue) 
    throws SerialException {
        SerialContext serialContext = (SerialContext) context.get(SerialContext.class);
        propertyElement.setAttribute("t", 
                (serialContext.getFlags() & SerialContext.FLAG_RENDER_SHORT_NAMES) == 0 ? "Alignment" : "AL");
        Alignment alignment = (Alignment) propertyValue;
        Element aElement = serialContext.getDocument().createElement("a");
        String horizontal = HORIZONTAL_CONSTANTS.get(alignment.getHorizontal());
        if (horizontal != null) {
            aElement.setAttribute("h", horizontal);
        }
        String vertical = VERTICAL_CONSTANTS.get(alignment.getVertical());
        if (vertical != null) {
            aElement.setAttribute("v", vertical);
        }
        propertyElement.appendChild(aElement);
    }
}
