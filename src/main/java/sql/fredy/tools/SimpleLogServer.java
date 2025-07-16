package sql.fredy.tools;


/**
 *
 * Copyright (c) 2002 Fredy Fischer
 * fredy.fischer@hulmen.ch
 *
 * Fredy Fischer
 * Hulmenweg 36
 * 8405 Winterthur
 * Switzerland
 *
 *
 * SimpleLogServer is a tool I did to enhance my sql-Admin package.
 * http://www.hulmen.ch/admin
 *
 * SimpleLogServer is based onto java.util.logging and therefore
 * needs to have in minimum jdk 1.4 underneath.
 *
 * SimpleLogServer is listenning onto a port ( default: 5237 )
 * It receives java.util.logging.LogRecord and logs it into
 * the default log-environment. To change file-names and LogHandlers
 * see the  /usr/java/jre/lib/logging.properties file (or wherever
 * this file in your os is), so you can easily change this by giving
 * the VM the parameter -Djava.util.logging.config.file= [FILE]
 *
 * Additionally it allows to display the LogRecords within a GUI if
 * you launch it via -GUI parameter.
 *
 * Parameters: -p   port to listen onto (default: 5237 )
 * -GUI launch GUI
 *
 *
 * As I do not know, where to put the logging.dtd and to use it, you
 * have to set sql.fredy.tools.FxmlFormatter as the Formatter for the
 * java.util.loggin.SocketHandler.formatter .
 * If someone knows a solution, I'm really interested to hear about.
 *
 *
 *
 *
 * I took the ideas to the server from David Flanagans 'Java in a Nutshell'
 *
 *
    Copyright (c) 2017 Fredy Fischer, sql@hulmen.ch
    
   Permission is hereby granted, free of charge, to any person obtaining a copy 
   of this software and associated documentation files (the "Software"), to deal
   in the Software without restriction, including without limitation the rights
   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
   copies of the Software, and to permit persons to whom the Software is
   furnished to do so, subject to the following conditions:
  
   The above copyright notice and this permission notice shall be included in
   all copies or substantial portions of the Software.

   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
   SOFTWARE.
 *
 **/
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.logging.*;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;


import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;



public class SimpleLogServer {
    
    private StringBuffer msgShadow;
    private JTextField portField;
    private Logger logger;
    private Thread runner;
    private SLStableModel slsTableModel;
    private JTable table;
    public JFrame frame;
    private TextViewer tv;
    
    
    boolean listen = true;
    boolean gui=true;
    /**
     * Get the value of gui.
     * @return value of gui.
     */
    public boolean isGui() {
        return gui;
    }
    
    /**
     * Set the value of gui.
     * @param v  Value to assign to gui.
     */
    public void setGui(boolean  v) {
        this.gui = v;
    }
    
    boolean guionly=false;
    
    /**
     * Get the value of guionly.
     * @return value of guionly.
     */
    public boolean isGuionly() {
        return guionly;
    }
    
    /**
     * Set the value of guionly.
     * @param v  Value to assign to guionly.
     */
    public void setGuionly(boolean  v) {
        this.guionly = v;
        if (v) setGui(true);
    }
    
    
    
    int port=5237;
    
    /**
     * Get the value of port.
     * @return value of port.
     */
    public int getPort() {
        return port;
    }
    
    /**
     * Set the value of port.
     * @param v  Value to assign to port.
     */
    public void setPort(int  v) {
        this.port = v;
    }
    
    
    int maxCapacity = 4096;
    
    private boolean local = true;
    
    /**
     * Get the value of maxCapacity.
     * @return value of maxCapacity.
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }
    
    /**
     * Set the value of maxCapacity.
     * @param v  Value to assign to maxCapacity.
     */
    public void setMaxCapacity(int  v) {
        this.maxCapacity = v;
    }
    
    
    
    private JPanel buttonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        
        JButton cancel= new JButton("Exit");
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        
        JButton half = new JButton("reduce");
        half.setToolTipText("to save memory, eliminate upper half of the buffer");
        
        half.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                slsTableModel.half();
                table.updateUI();
            }
        });
        
        
        final JButton start = new JButton("Stop");
        start.setToolTipText("Stop listening");
        
        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if ( listen ) {
                    listen=false;
                    start.setText("Start");
                    start.setToolTipText("Start listening");
                } else {
                    listen=true;
                    start.setText("Stop");
                    start.setToolTipText("Stop listening");
                }
            }
        });
        
        JButton save = new JButton("Save");
        save.setToolTipText("Save log to file");
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sql.fredy.io.FileWriter fw = new sql.fredy.io.FileWriter(slsTableModel.getDataAsText(),
                "?","-a");
            }
        });
        
        final JCheckBox guiFile = new JCheckBox("only GUI",isGuionly());
        guiFile.setToolTipText("only on GUI or also to standard Log. AVOID LOOPS!");
        guiFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setGuionly(guiFile.isSelected());
            }
        });
        guiFile.setBorder(new BevelBorder(BevelBorder.LOWERED));
        
        
        panel.setBorder(new EtchedBorder());
        panel.add(guiFile);
        
        panel.add(start);
        panel.add(half);
        panel.add(save);
        panel.add(cancel);
        
        
        return panel;
    }
    
    
    
    private JPanel logPanel() {
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BorderLayout());
        textPanel.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED),"Logserver on port " +
        Integer.toString(getPort())));
        
        initTable();
        textPanel.add(BorderLayout.CENTER,new JScrollPane(table));
        return textPanel;
    }
    
    private void initTable() {
        tv = new TextViewer(frame,"Zoom",true);
        tv.close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tv.setVisible(false);
                tv.dispose();
            }
        });
        
        slsTableModel = new SLStableModel();
        table         = new JTable(slsTableModel);
        
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setPreferredScrollableViewportSize(new Dimension(700,270));
        
        ListSelectionModel rowSM = table.getSelectionModel();
        rowSM.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
                
                ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                if (lsm.isSelectionEmpty()) {
                    //no rows are selected
                } else {
                    int selectedRow = lsm.getMinSelectionIndex();
                    StringBuffer sb = new StringBuffer();
                    sb.append("Date:         " +
                    (String)slsTableModel.getValueAt(selectedRow,slsTableModel.DATE) + "\n");
                    sb.append("Sourceclass:  " +
                    (String)slsTableModel.getValueAt(selectedRow,slsTableModel.SOURCECLASS) + "\n");
                    sb.append("Sourcemethod: " +
                    (String)slsTableModel.getValueAt(selectedRow,slsTableModel.SOURCEMETHOD) + "\n");
                    sb.append("Level:        " +
                    (String)slsTableModel.getValueAt(selectedRow,slsTableModel.LEVEL) + "\n");
                    sb.append("Message:      " +
                    (String)slsTableModel.getValueAt(selectedRow,slsTableModel.MESSAGE) + "\n");
                    
                    tv.setText(sb.toString());
                    tv.setVisible(true);
                }
            }
        });
    }
    
    private void createFrame() {
        frame = new JFrame("Fredy's SLS (simple log server)");
        
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(BorderLayout.CENTER,logPanel());
        frame.getContentPane().add(BorderLayout.SOUTH,buttonPanel());
        
        frame.addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
            }
            public void windowClosed(WindowEvent e){
                
            }
            public void windowClosing(WindowEvent e) {
                
                close();
            }
            public void windowDeactivated(WindowEvent e) {
                
            }
            public void windowDeiconified(WindowEvent e) {
                
            }
            public void windowIconified(WindowEvent e) {
                
            }
            public void windowOpened(WindowEvent e) {
                
            }
        });
        
        frame.pack();
        frame.setVisible(true);
        
    }
    
    
    public SimpleLogServer(boolean gui,
    int     port ,
    int     maxCapacity,
    boolean guionly
    ) {
        
        setGui(gui);
        setPort(port);
        setMaxCapacity(maxCapacity);
        setGuionly(guionly);
        init();
    }
    
    public SimpleLogServer(int port ) {
        setPort(port);
        init();
    }
    
    public SimpleLogServer() {
        init();
    }
    
    
    private void close() {
        listen=false;
        if ( isLocal()) {
            System.exit(0);
        } else {
            frame.setVisible(false);
        }        
    }
     
    private void startServer() {
        runner = new Thread() {
            public void run() {
                try {
                    
                    Charset charset = Charset.forName("ISO-8859-1");
                    CharsetEncoder encoder = charset.newEncoder();
                    CharsetDecoder decoder = charset.newDecoder();
                    
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    Selector selector = Selector.open();
                    
                    ServerSocketChannel server = ServerSocketChannel.open();
                    server.socket().bind(new java.net.InetSocketAddress(getPort()));
                    server.configureBlocking(false);
                    
                    SelectionKey serverkey = server.register(selector,
                    SelectionKey.OP_ACCEPT);
                    
                    for(;;) {
                        selector.select();
                        
                        Set keys = selector.selectedKeys();
                        
                        for(Iterator i = keys.iterator(); i.hasNext(); ) {
                            SelectionKey key = (SelectionKey) i.next();
                            i.remove();
                            
                            if (key == serverkey) {
                                if (key.isAcceptable()) {
                                    SocketChannel client = server.accept();
                                    client.configureBlocking(false);
                                    SelectionKey clientkey = client.register(selector,
                                    SelectionKey.OP_READ);
                                    clientkey.attach(0);
                                }
                            }
                            else {
                                SocketChannel client = (SocketChannel) key.channel();
                                if ( ! key.isReadable()) continue;
                                if ( ! listen ) {
                                    key.cancel();
                                    client.close();
                                    continue;
                                }
                                
                                int bytesread = client.read(buffer);
                                
                                if (bytesread == -1) {
                                    key.cancel();
                                    client.close();
                                    continue;
                                }
                                buffer.flip();
                                
                                byte[] byts = new byte[ buffer.remaining() ];
                                byts = buffer.array();
                                
                                
                                SimpleXMLConverter sxcv = new SimpleXMLConverter();
                                try {
                                    processLog(sxcv.convert(byts));
                                } catch ( org.xml.sax.SAXException e) {
                                    logger.log(Level.WARNING,"SAX-Exception: " + e.getMessage().toString());
                                }
                                
                                buffer.clear();
                                
                                // Get sequence number from SelectionKey
                                int num = (int) key.attachment();
                                
                                // Attach an incremented sequence nubmer to the key
                                key.attach(num+1);
                            }
                        }
                    }
                } catch (IOException iox ) {
                    //logger.log(Level.SEVERE,"Log server: " + iox.getMessage().toString());
                    System.out.println("LogServer: " + iox.getMessage().toString());
                    iox.printStackTrace();
                    System.exit(0);
                }
            }};
            runner.start();
            System.out.println("Started SimpleLogServer on port " + Integer.toString(getPort()));
            
    }
    
    
    private void processLog(LogRecord lr ) {
        if ( lr.getSourceClassName() != null )  {
            if ( ! isGuionly()) logger.log(lr);
            if ( isGui() ) displayOnGui(lr) ;
        }
        
    }
    
    private void displayOnGui(LogRecord lr ) {
        
        
        Date date = new Date(lr.getMillis() );
        
        SimpleDateFormat formatter
        = new SimpleDateFormat("yyyy.MM.dd G 'at' hh:mm:ss a zzz");
        String dateString = formatter.format(date);
        
        String level = "";
        if (lr.getLevel() == Level.FINEST ) level = "FINEST";
        if (lr.getLevel() == Level.FINE   ) level = "FINE";
        if (lr.getLevel() == Level.CONFIG ) level = "CONFIG";
        if (lr.getLevel() == Level.INFO   ) level = "INFO";
        if (lr.getLevel() == Level.WARNING) level = "WARNING";
        if (lr.getLevel() == Level.SEVERE ) level = "SEVERE";
        
        Vector logVector = new Vector();
        
        logVector.addElement((String)dateString);
        logVector.addElement((String)lr.getSourceClassName());
        logVector.addElement((String)lr.getSourceMethodName());
        logVector.addElement((String)level);
        logVector.addElement((String)lr.getMessage());
        
        /**
         * To avoid waste of memory, we delete half of the buffer, if capacity exceeds
         **/
        
        if ( slsTableModel.getRowCount() > getMaxCapacity() )  slsTableModel.half();
        slsTableModel.addRow(logVector);
        
        
    }
    
    
    private void init() {
        
        
        if ( isGui() ) {
            createFrame();
        }
        startServer();
        logger = Logger.getLogger("sql.fredy");
    }
    
    public static void main(String args[] ) {
        
        int port    = 5237;
        int capacity= 4096;
        boolean gui = false;
        boolean guionly = false;
        
        int i = 0;
        while ( i < args.length) {
            if (args[i].equals("-p")) {
                i++;
                port = Integer.parseInt(args[i]);
            }
            if (args[i].equals("-GUI")) {
                gui = true;
            }
            if (args[i].equals("-GUIONLY")) {
                guionly = true;
            }
            if (args[i].equals("-c")) {
                i++;
                capacity = Integer.parseInt(args[i]);
            }
            i++;
        };
        
        SimpleLogServer sls = new SimpleLogServer(gui,port,capacity,guionly);
    }
    
    public void setLocal(boolean v) {
        local = v;
    }
    
    public boolean isLocal() {
        return local;
    }
    
}
