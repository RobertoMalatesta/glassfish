/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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

import java.io.*;
import java.net.*;
import com.sun.ejte.ccl.reporter.*;

/*
 * Unit test for IT 10100
 */
public class WebTest {

    private static final String TEST_NAME = "servlet-3.0-servlet-context-create-servlet-with-servlet-security";

    private static SimpleReporterAdapter stat
        = new SimpleReporterAdapter("appserv-tests");

    private String host;
    private int port;
    private String contextRoot;

    public WebTest(String[] args) {
        host = args[0];
        port = Integer.parseInt(args[1]);
        contextRoot = args[2];
    }
    
    public static void main(String[] args) {

        stat.addDescription("Unit test for setServletSecurity");
        WebTest webTest = new WebTest(args);

        try {
            boolean ok = webTest.run();
            stat.addStatus(TEST_NAME, ((ok)? stat.PASS : stat.FAIL));
        } catch( Exception ex) {
            ex.printStackTrace();
            stat.addStatus(TEST_NAME, stat.FAIL);
        }

	    stat.printSummary();
    }

    public boolean run() throws Exception {
        String contextPath = contextRoot + "/newServlet";
        boolean ok = doWebMethod("GET", host, port, contextPath, false, 200, "g:Hello");
        ok = ok && doWebMethod("POST", host, port, contextPath, true, 200, "p:Hello, javaee");
        ok = ok && doWebMethod("OPTIONS", host, port, contextPath, true, 403, null);

        String contextPath2 = contextRoot + "/newServlet2";
        ok = doWebMethod("GET", host, port, contextPath2, false, 200, "g2:Hello");
        ok = ok && doWebMethod("POST", host, port, contextPath2, true, 200, "p2:Hello, javaee");
        ok = ok && doWebMethod("OPTIONS", host, port, contextPath2, true, 403, null);

        String contextPath3 = contextRoot + "/newServlet2_1";
        ok = doWebMethod("GET", host, port, contextPath3, true, 403, null);
        ok = ok && doWebMethod("POST", host, port, contextPath3, true, 200, "p2:Hello, javaee");
        ok = ok && doWebMethod("OPTIONS", host, port, contextPath3, false, 200, "o2:Hello, null");

        return ok;
    }

    private boolean doWebMethod(String webMethod, String host, int port,
            String contextPath, boolean requireAuthenticate, 
            int responseCode, String expected) throws Exception {

        String urlStr = "http://" + host + ":" + port + contextPath;
        System.out.println(webMethod + " " + urlStr);
        URL url = new URL(urlStr);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setRequestMethod(webMethod);
        if (requireAuthenticate) {
            urlConnection.setRequestProperty("Authorization", "Basic amF2YWVlOmphdmFlZQ==");
        }
        urlConnection.connect();

        int code = urlConnection.getResponseCode();
        boolean ok = (code == responseCode);
        if (expected != null) {
            InputStream is = null;
            BufferedReader bis = null;
            String line = null;

            try{
                is = urlConnection.getInputStream();
                bis = new BufferedReader(new InputStreamReader(is));
                int lineNum = 1;
                while ((line = bis.readLine()) != null) {
                    System.out.println(lineNum + ":  " + line);
                    lineNum++;
                    ok = ok && expected.equals(line);
                }
            } catch( Exception ex){
                ex.printStackTrace();   
                throw new Exception("Test UNPREDICTED-FAILURE");
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch(IOException ioe) {
                    // ignore
                }
                try {
                    if (bis != null) {
                        bis.close();
                    }
                } catch(IOException ioe) {
                    // ignore
                }
            }
        }

        return ok;
    }
}
