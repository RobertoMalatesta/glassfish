/*
 * Copyright (c) 2009, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.sun.enterprise.util;

/**
 * A simple class that fills a hole in the JDK.  It parses out the version numbers
 *  of the JDK we are running.
 * Example:<p>
 * 1.6.0_u14 == major = 1 minor = 6, subminor = 0, update = 14
 *
 * @author bnevins
 */
public final class JDK {

    private JDK(String string) {
        String[] split = string.split("[\\._\\-]+");

        if (split.length > 0) {
            major = Integer.parseInt(split[0]);
        }
        if (split.length > 1) {
            minor = Integer.parseInt(split[1]);
        }
        if (split.length > 2) {
            subminor = Integer.parseInt(split[2]);
        }
        if (split.length > 3) {
            update = Integer.parseInt(split[3]);
        }
    }


    public static JDK getVersion(String string) {
        if (string.matches("([0-9]+[\\._\\-]+)*[0-9]+")) {
            return new JDK(string);
        } else {
            return null;
        }
    }
    /**
     * See if the current JDK is legal for running GlassFish
     * @return true if the JDK is >= 1.6.0
     */
    public static boolean ok() {
        return major == 1 && minor >= 6;
    }

    public static int getMajor() {
        return major;
    }
    public static int getMinor() {
        return minor;
    }

    public static int getSubMinor() {
        return subminor;
    }

    public static int getUpdate() {
        return update;
    }

    public boolean newerThan(JDK version) {
        if (major > version.getMajor()) {
            return true;
        } else if (major == version.getMajor()) {
            if (minor > version.getMinor()) {
                return true;
            } else if (minor == version.getMinor()) {
                if (subminor > version.getSubMinor()) {
                    return true;
                } else if (subminor == version.getSubMinor()) {
                    if (update > version.getUpdate()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean newerOrEquals(JDK version) {
        return newerThan(version) || equals(version);
    }

    public boolean olderThan(JDK version) {
        return !newerOrEquals(version);
    }

    public boolean olderOrEquals(JDK version) {
        return !newerThan(version);
    }

    /**
     * No instances are allowed so it is pointless to override toString
     * @return Parsed version numbers
     */
    public static String toStringStatic() {
        return "major: " + JDK.getMajor() +
        "\nminor: " + JDK.getMinor() +
        "\nsubminor: " + JDK.getSubMinor() +
        "\nupdate: " + JDK.getUpdate() +
        "\nOK ==>" + JDK.ok();
    }

    static {
        initialize();
    }

    // DO NOT initialize these variables.  You'll be sorry if you do!
    private static int major;
    private static int minor;
    private static int subminor;
    private static int update;

    // silently fall back to ridiculous defaults if something is crazily wrong...
    private static void initialize() {
        major = 1;
        minor = subminor = update = 0;
        try {
            String jv = System.getProperty("java.version");
            /*In JEP 223 java.specification.version will be a single number versioning , not a dotted versioning . So if we get a single
            integer as versioning we know that the JDK is post JEP 223
            For JDK 8:
                java.specification.version  1.8
                java.version    1.8.0_122
             For JDK 9:
                java.specification.version 9
                java.version 9.1.2
            */
            String javaSpecificationVersion = System.getProperty("java.specification.version");
            String[] jsvSplit = javaSpecificationVersion.split("\\.");
            if (jsvSplit.length == 1) {
                //This is handle Early Access build .Example 9-ea
                String[] jvSplit = jv.split("-");
                String jvReal = jvSplit[0];
                String[] split = jvReal.split("[\\.]+");

                if (split.length > 0) {
                    if (split.length > 0) {
                        major = Integer.parseInt(split[0]);
                    }
                    if (split.length > 1) {
                        minor = Integer.parseInt(split[1]);
                    }
                    if (split.length > 2) {
                        subminor = Integer.parseInt(split[2]);
                    }
                    if (split.length > 3) {
                        update = Integer.parseInt(split[3]);
                    }
                }
            } else {
                if (!StringUtils.ok(jv))
                    return; // not likely!!

                String[] ss = jv.split("\\.");

                if (ss.length < 3 || !ss[0].equals("1"))
                    return;

                major = Integer.parseInt(ss[0]);
                minor = Integer.parseInt(ss[1]);
                ss = ss[2].split("_");

                if (ss.length < 1)
                    return;

                subminor = Integer.parseInt(ss[0]);

                if (ss.length > 1)
                    update = Integer.parseInt(ss[1]);
            }
        }
        catch(Exception e) {
            // ignore -- use defaults
        }
    }
}
