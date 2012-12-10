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

import nextapp.echo.app.reflect.IntrospectorFactory;
import nextapp.echo.app.reflect.ObjectIntrospector;
import nextapp.echo.app.serial.SerialContext;
import nextapp.echo.app.serial.SerialException;
import nextapp.echo.app.serial.SerialPropertyPeer;
import nextapp.echo.app.util.Context;
import nextapp.echo.app.util.DomUtil;

import org.w3c.dom.Element;

/**
 * <code>SerialPropertyPeer</code> for <code>Integer</code> properties.
 */
public class IntegerPeer 
implements SerialPropertyPeer {
    
    /**
     * Uses an <code>ObjectIntrospector</code> to determine the integer value of 
     * a constant name
     * 
     * @param context the relevant <code>SerialContext</code>
     * @param objectClass the <code>Class</code> of object containing candidate 
     *        constant values
     * @param value the name of the constant value
     * @return an integer representing the constant value, or null if the 
     *         constant is not found
     */
    private Integer introspectConstantValue(Context context, Class objectClass, String value) 
    throws SerialException {
        SerialContext serialContext = (SerialContext) context.get(SerialContext.class);
        try {
            ObjectIntrospector introspector = IntrospectorFactory.get(objectClass.getName(), 
                    serialContext.getClassLoader());
            if (value.startsWith(objectClass.getName())) {
                // Remove class name if required.
                value = value.substring(objectClass.getName().length() + 1);
            }
            Object constantValue = introspector.getConstantValue(value);
            if (constantValue instanceof Integer) {
                return (Integer) constantValue;
            } else {
                return null;
            }
        } catch (ClassNotFoundException ex) {
            // Should not occur.
            throw new SerialException("Object class not found.", ex);  
        }
    }

    /**
     * @see nextapp.echo.app.serial.SerialPropertyPeer#toProperty(nextapp.echo.app.util.Context, 
     *      java.lang.Class, org.w3c.dom.Element)
     */
    public Object toProperty(Context context, Class objectClass, Element propertyElement) 
    throws SerialException {
        String valueText = propertyElement.hasAttribute("v") 
                ? propertyElement.getAttribute("v") : DomUtil.getElementText(propertyElement);
        try {
            return new Integer(valueText);
        } catch (NumberFormatException ex) {
            return introspectConstantValue(context, objectClass, valueText);
        }
    }

    /**
     * @see nextapp.echo.app.serial.SerialPropertyPeer#toXml(nextapp.echo.app.util.Context,
     *      java.lang.Class, org.w3c.dom.Element, java.lang.Object)
     */
    public void toXml(Context context, Class objectClass, Element propertyElement, Object propertyValue) 
    throws SerialException {
        SerialContext serialContext = (SerialContext) context.get(SerialContext.class);
        propertyElement.setAttribute("t", "i");
        propertyElement.appendChild(serialContext.getDocument().createTextNode(propertyValue.toString()));
    }
}
