/**
 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs for
 * DB-Administrations, like: - create/ drop tables - create indices - perform
 * sql-statements - simple form - a guided query - Data Export and a other
 * usefull things in DB-arena
 *
 * Admin (Version see below)
 *
 * sql@hulmen.ch
 *
 * Fredy Fischer Hulmenweg 36 8405 Winterthur Switzerland
 *
 *
 * The icons used in this application are from Dean S. Jones
 *
 * Icons Copyright(C) 1998 by Dean S. Jones dean@gallant.com
 * www.gallant.com/icons.htm
 *
 * CalendarBean is Copyright (c) by Kai Toedter
 *
 * MSeries is Copyright (c) by Martin Newstead
 *
 * POI is from the Apache Foundation
 *
 *
 * sql@hulmen.ch Postal: Fredy Fischer Hulmenweg 36 8405 Winterthur Switzerland
 *
 *
 * Copyright (c) 2017 Fredy Fischer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *
 * RSyntaxTextArea and AutoComplete is from www.fifesoft.com ----------------
 * Start fifesoft License ------------------------------------- Copyright (c)
 * 2012, Robert Futrell All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of the author nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ----------------End fifesoft License -------------------------------------
 *
 *
 *
 *
 *
 */
package sql.fredy.admin;

/**
 *
 * @author Fredy Fischer
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.*;
import sql.fredy.ui.StackTracer;

public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {

    static final Logger LOGGER = Logger.getLogger("sql.fredy.admin");
    StackTracer stackTrace = null;

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        LOGGER.log(Level.INFO, "Uncaught exception appeared: {0} ({1})", new Object[]{e.getLocalizedMessage(), e.getMessage()});
        LOGGER.log(Level.INFO, "Thread: {0} / {1}", new Object[]{t.getName(), t.toString()});
        LOGGER.log(Level.FINE, e.toString());

        if ((System.getProperty("sql.fredy.admin.printStackTrace").equalsIgnoreCase("yes"))
                || (System.getenv("sql.fredy.admin.printStackTrace").equalsIgnoreCase("yes"))) {
            LOGGER.log(Level.INFO, e.toString());
            e.printStackTrace();        

            /*
            if (stackTrace == null) {
                stackTrace = new StackTracer(null, "Exception", true);
                stackTrace.cancel.addActionListener((ActionEvent e1) -> {
                    stackTrace.setVisible(false);
                });
            }

            stackTrace.setExcpt((Exception) e);
            stackTrace.setVisible(true);
            */
        }

    }

}
