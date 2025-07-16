package sql.fredy.ui;

/**  
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


**/
import sql.fredy.io.MyFileFilter;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;


public class DashBoard extends JPanel {

  JLayeredPane desktop;

  String file;
  public void setFile(String v) { this.file=v; }
  public String getFile() { return file; }
  public ImageButton close;
  private JLabel statusLine;

  public DashBoard(String file) {

    setFile(file);
    init();
  }
  public DashBoard() {
    setFile(null);
    init();
  }


  public static void main(String args[]) {
    String file=null;

    System.out.println("Fredy's DashBoard Version 1.0\n" +
		       "-----------------\n" +
		       "Is used to display system-states\n\n" +
                       "Parameters: -f xmlfile\n" );

    if (( args.length > 1 ) && (args[0].toLowerCase().startsWith("-f") ) ) file = args[1];
    if ( args.length == 1 ) file = args[0];

    DashBoard db = new DashBoard(file);
    CloseableFrame cf = new CloseableFrame("DashBoard");
    cf.getContentPane().setLayout(new BorderLayout());
    cf.getContentPane().add(BorderLayout.CENTER,db);
    cf.pack();
    cf.setVisible(true);

    db.close.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	System.exit(0);
      }});
  
  }
    
  private void init() {

    
    desktop = new JDesktopPane();
    desktop.setOpaque(false);

    this.setLayout(new BorderLayout());
    this.add(BorderLayout.NORTH,buttonPanel());
    this.add(BorderLayout.CENTER,desktop);
    this.add(BorderLayout.SOUTH,statusPanel());

  }
      
    private JToolBar buttonPanel() {
	JToolBar toolbar = new JToolBar(SwingConstants.HORIZONTAL);
	toolbar.setFloatable(false);

	ImageButton runIt     = new ImageButton(null,"gauge.gif","dash it");
	ImageButton loadFile  = new ImageButton(null,"opendoc.gif","load XML-File");
	ImageButton newWindow = new ImageButton(null,"computer.gif","new Window");
	            close     = new ImageButton(null,"exit.gif","close");

	newWindow.addActionListener(new ActionListener() {
	       public void actionPerformed(ActionEvent e) {
		  final JFrame aFrame = new JFrame("another DashBoard");
		  aFrame.getContentPane().setLayout(new BorderLayout());
                  aFrame.addWindowListener(new WindowAdapter() {
		    public void windowActivated(WindowEvent e) {}
		    public void windowClosed(WindowEvent e) {}
		    public void windowClosing(WindowEvent e) {
		      aFrame.setVisible(false);
		      aFrame.dispose();
		    }
		    public void windowDeactivated(WindowEvent e) {}
		    public void windowDeiconified(WindowEvent e) {}
		    public void windowIconified(WindowEvent e) {}
		    public void windowOpened(WindowEvent e) {}});
	      
		  final DashBoard aDashBoard = new DashBoard();
		  aDashBoard.close.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		      aFrame.setVisible(false);
		      aFrame.dispose();
		    }
		  });
		  aFrame.getContentPane().add(BorderLayout.CENTER,aDashBoard);
		  aFrame.pack();
		  aFrame.setVisible(true);
	       }
	});


	runIt.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    exec();
	  }});

	loadFile.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    addFile();
	  }});


	toolbar.add(runIt);
	toolbar.add(loadFile);
	toolbar.add(newWindow);
	toolbar.add(close);

	return toolbar;
    }


    private JPanel statusPanel() {
	JPanel panel = new JPanel();
	statusLine = new JLabel();
	statusLine.setText("Fredy's DashBoard Version 0.1");
	statusLine.setFont(new Font("Monospaced", Font.PLAIN, 10));
	statusLine.setBackground(Color.yellow);
	statusLine.setForeground(Color.blue);
	panel.setBackground(Color.yellow);
	panel.setForeground(Color.blue);
	panel.setBorder(new BevelBorder(BevelBorder.LOWERED));

	panel.add(statusLine);
	return panel;

    }



  private void addFile() {
	JFileChooser chooser = new JFileChooser(); 
	chooser.setDialogTitle("Select XML file");

	MyFileFilter filter = new MyFileFilter();
	filter.addExtension("xml");
	filter.setDescription("XML-files");
	chooser.setFileFilter(filter);

	int returnVal = chooser.showOpenDialog(this);
	if(returnVal == JFileChooser.APPROVE_OPTION) 
	    setFile(chooser.getCurrentDirectory() + java.io.File.separator + chooser.getSelectedFile().getName());

  }

  private void exec() {
    if ( getFile() == null ) addFile();
  }

}
	

