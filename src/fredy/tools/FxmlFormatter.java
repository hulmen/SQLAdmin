/*
 * @(#)XMLFormatter.java	1.16 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package sql.fredy.tools;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.*;

/**
 * Format a LogRecord into a standard XML format.
 * <p>
 * The DTD specification is provided as Appendix A to the Java Logging APIs
 * specification.
 * <p>
 * The XMLFormatter can be used with arbitrary character encodings, but it is
 * recommended that it normally be used with UTF-8. The character encoding can
 * be set on the output Handler.
 *
 * @version 1.16, 12/03/01
 * @since 1.4
 */

public class FxmlFormatter extends java.util.logging.Formatter {

    private LogManager manager = LogManager.getLogManager();

    // Append a two digit number.
    private void a2(StringBuffer sb, int x) {
        if (x < 10) {
            sb.append('0');
        }
        sb.append(x);
    }

    // Append the time and date in ISO 8601 format
    private void appendISO8601(StringBuffer sb, long millis) {
        Date date = new Date(millis);
        sb.append(date.getYear() + 1900);
        sb.append('-');
        a2(sb, date.getMonth() + 1);
        sb.append('-');
        a2(sb, date.getDate());
        sb.append('T');
        a2(sb, date.getHours());
        sb.append(':');
        a2(sb, date.getMinutes());
        sb.append(':');
        a2(sb, date.getSeconds());
    }

    // Append to the given StringBuffer an escaped version of the
    // given text string where XML special characters have been escaped.
    // For a null string we appebd "<null>"
    private void escape(StringBuffer sb, String text) {
        if (text == null) {
            text = "<null>";
        }
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '<') {
                sb.append("&lt;");
            } else if (ch == '>') {
                sb.append("&gt;");
            } else if (ch == '&') {
                sb.append("&amp;");
            } else {
                sb.append(ch);
            }
        }
    }

    /**
     * Format the given message to XML.
     *
     * @param record the log record to be formatted.
     * @return a formatted log record
     */
    public String format(LogRecord record) {
        StringBuffer sb = new StringBuffer(520);
        sb.append("<log>\n");
        sb.append("<record>\n");

        sb.append("  <date>");
        appendISO8601(sb, record.getMillis());
        sb.append("</date>\n");

        sb.append("  <millis>");
        sb.append(record.getMillis());
        sb.append("</millis>\n");

        sb.append("  <sequence>");
        sb.append(record.getSequenceNumber());
        sb.append("</sequence>\n");

        String name = record.getLoggerName();
        if (name != null) {
            sb.append("  <logger>");
            escape(sb, name);
            sb.append("</logger>\n");
        }

        sb.append("  <level>");
        escape(sb, record.getLevel().toString());
        sb.append("</level>\n");

        if (record.getSourceClassName() != null) {
            sb.append("  <class>");
            escape(sb, record.getSourceClassName());
            sb.append("</class>\n");
        }

        if (record.getSourceMethodName() != null) {
            sb.append("  <method>");
            escape(sb, record.getSourceMethodName());
            sb.append("</method>\n");
        }

        sb.append("  <thread>");
        sb.append(record.getThreadID());
        sb.append("</thread>\n");

        if (record.getMessage() != null) {
            // Format the message string and its accompanying parameters.
            String message = formatMessage(record);
            sb.append("  <message>");
            escape(sb, message);
            sb.append("</message>");
            sb.append("\n");
        }

	// If the message is being localized, output the key, resource
        // bundle name, and params.
        ResourceBundle bundle = record.getResourceBundle();
        try {
            if (bundle != null && bundle.getString(record.getMessage()) != null) {
                sb.append("  <key>");
                escape(sb, record.getMessage());
                sb.append("</key>\n");
                sb.append("  <catalog>");
                escape(sb, record.getResourceBundleName());
                sb.append("</catalog>\n");
                Object parameters[] = record.getParameters();
                for (int i = 0; i < parameters.length; i++) {
                    sb.append("  <param>");
                    try {
                        escape(sb, parameters[i].toString());
                    } catch (Exception ex) {
                        sb.append("???");
                    }
                    sb.append("</param>\n");
                }
            }
        } catch (Exception ex) {
            // The message is not in the catalog.  Drop through.
        }

        if (record.getThrown() != null) {
            // Report on the state of the throwable.
            Throwable th = record.getThrown();
            sb.append("  <exception>\n");
            sb.append("    <message>");
            escape(sb, th.toString());
            sb.append("</message>\n");
            StackTraceElement trace[] = th.getStackTrace();
            for (int i = 0; i < trace.length; i++) {
                StackTraceElement frame = trace[i];
                sb.append("    <frame>\n");
                sb.append("      <class>");
                escape(sb, frame.getClassName());
                sb.append("</class>\n");
                sb.append("      <method>");
                escape(sb, frame.getMethodName());
                sb.append("</method>\n");
                // Check for a line number.
                if (frame.getLineNumber() >= 0) {
                    sb.append("      <line>");
                    sb.append(frame.getLineNumber());
                    sb.append("</line>\n");
                }
                sb.append("    </frame>\n");
            }
            sb.append("  </exception>\n");
        }

        sb.append("</record>\n");
        sb.append("</log>\n");
        return sb.toString();
    }

    /**
     * Return the header string for a set of XML formatted records.
     *
     * @param h The target handler.
     * @return header string
     */
    public String getHead(Handler h) {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\"");
        String encoding = h.getEncoding();
        if (encoding == null) {
            // Figure out the default encoding.
            OutputStreamWriter writer = new OutputStreamWriter(new ByteArrayOutputStream());
            encoding  = writer.getEncoding();
        }
        // Try to map the encoding name to a canonical name.
        try {
            Charset cs = Charset.forName(encoding);
            encoding = cs.name();
        } catch (Exception ex) {
	    // We hit problems finding a canonical name.
            // Just use the raw encoding name.
        }

        sb.append(" encoding=\"");
        sb.append(encoding);
        sb.append("\"");
        sb.append(" standalone=\"no\"?>\n");
	//sb.append("<!DOCTYPE log SYSTEM \"logger.dtd\">\n");
        //sb.append("<log>\n");
        return sb.toString();
    }

    /**
     * Return the tail string for a set of XML formatted records.
     *
     * @param h The target handler.
     * @return tail string
     */
    public String getTail(Handler h) {
        return "\n";
    }
}
