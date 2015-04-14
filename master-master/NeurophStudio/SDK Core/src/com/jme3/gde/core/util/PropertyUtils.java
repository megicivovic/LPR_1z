/*
 *  Copyright (c) 2009-2010 jMonkeyEngine
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are
 *  met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *  TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *  PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.gde.core.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *
 * @author Nehon
 */
public class PropertyUtils {

    public static PropertyDescriptor getPropertyDescriptor(Class c, Field field) {
        try {
            PropertyDescriptor prop = new PropertyDescriptor(field.getName(), c);

            prop.setDisplayName(splitCamelCase(field.getName()));

            return prop;
        } catch (IntrospectionException ex) {
            //System.out.println(ex.getMessage());
            return null;
        }
    }

    public static PropertyDescriptor getPropertyDescriptor(Class c, Method meth) {
        try {

            String name = meth.getName();
            if (name.startsWith("get") || name.startsWith("set")) {
                name = name.substring(3);
                if (name.length() > 0) {
                    name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
                }
            } else {
                return null;
            }
            PropertyDescriptor prop = new PropertyDescriptor(name, c);

            prop.setDisplayName(splitCamelCase(meth.getName()));

            return prop;
        } catch (IntrospectionException ex) {
            //System.out.println(ex.getMessage());
            return null;
        }
    }

    static String splitCamelCase(String s) {
        s = capitalizeString(s);
        return s.replaceAll(
                String.format("%s|%s|%s",
                "(?<=[A-Z])(?=[A-Z][a-z])",
                "(?<=[^A-Z])(?=[A-Z])",
                "(?<=[A-Za-z])(?=[^A-Za-z])"),
                " ");
    }

    public static String capitalizeString(String string) {
        char[] chars = string.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return String.valueOf(chars);
    }
}
