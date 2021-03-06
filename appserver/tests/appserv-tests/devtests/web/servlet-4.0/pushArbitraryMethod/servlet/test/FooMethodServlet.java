/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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

package test;

import java.io.IOException;
import java.util.Arrays;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.HttpMethodConstraint;
import jakarta.servlet.annotation.ServletSecurity;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.PushBuilder;
import static jakarta.servlet.annotation.ServletSecurity.TransportGuarantee.CONFIDENTIAL;

@WebServlet(urlPatterns="/foo/*")
public class FooMethodServlet extends HttpServlet {
        
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {
            String pathInfo = req.getPathInfo();
            String method = req.getMethod();
            if ("FOO".equals(method)) {
                    if ("/my.css".equals(pathInfo)) {
                            res.getWriter().println("foo: bar");
                            res.setStatus(HttpServletResponse.SC_OK);
                            return;

                    }
            }
            if ("/my.css".equals(pathInfo)) {
                    res.getWriter().println("foo: bar");
                    res.setStatus(HttpServletResponse.SC_OK);
                    return;
            }
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
            
    }
}
