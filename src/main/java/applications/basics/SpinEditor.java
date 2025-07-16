package applications.basics;



//import CalendarBean.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.BorderFactory; 
import javax.swing.border.Border;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;



class SpinEditor extends DefaultCellEditor {
        public String currentText = "";
        public int currentValue;
        public JDialog dialog;
        private JSpinner  spf;
        private SpinnerModel lengthModel;
        private ImageButton ok, cancel;

        public SpinEditor(JButton b,int min, int max, int start, String title) {
             super(new JCheckBox()); //Unfortunately, the constructor
                                     //expects a check box, combo box,
                                     //or text field.
            editorComponent = b;
            setClickCountToStart(1);    //This is usually 1 or 2.


	    dialog = new JDialog();
	    dialog.setTitle(title);
 	    dialog.setModal(false);
            lengthModel = new SpinnerNumberModel(start,min,max,1); 
            spf    = new JSpinner(lengthModel); 

	    
	    dialog.getContentPane().setLayout(new GridBagLayout());
	    GridBagConstraints gbc;
	    Insets insets = new Insets(5,5,5,5);
	    gbc = new GridBagConstraints();	    
	    gbc.anchor= GridBagConstraints.WEST;
	    gbc.fill  = GridBagConstraints.HORIZONTAL;
	    gbc.insets = insets;
	    gbc.gridx = 0;
	    gbc.gridy = 0;
	    gbc.gridwidth = 3; 
	    dialog.getContentPane().add(spf,gbc);

	    gbc.fill  = GridBagConstraints.NONE;
	    gbc.gridx = 0;
	    gbc.gridy = 1;
	    gbc.gridwidth = 3; 	 
	    dialog.getContentPane().add(buttonPanel(),gbc);


            //Must do this so that editing stops when appropriate.
            b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                 
		    spf.setValue(Integer.parseInt(currentText));
   
                    //Without the following line, the dialog comes up
                    //in the middle of the screen.
                    dialog.setLocationRelativeTo(editorComponent);                    
                    dialog.pack();
                    dialog.setVisible(true);

		    ok.addActionListener(new ActionListener() {
			    public void actionPerformed(ActionEvent e) {		   
				currentText = Integer.toString((Integer)lengthModel.getValue());;
				dialog.setVisible(false);
				fireEditingStopped();
		    }});
		    
		    cancel.addActionListener(new ActionListener() {
			    public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
				fireEditingCanceled();
		    }});
		}});
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
        protected void fireEditingCanceled() {
            super.fireEditingCanceled();
        }
        public Object getCellEditorValue() {
            return currentText;
        }

        public Component getTableCellEditorComponent(JTable table, 
                                                     Object value,
                                                     boolean isSelected,
                                                     int row,
                                                     int column) {

	    //System.out.println("getTableCellEditorComponent   " + value.toString());
            ((JButton)editorComponent).setText(value.toString());
            //currentText = (String)value;
	    currentText = value.toString();
            return editorComponent;
        }

        private JPanel buttonPanel() {
	  JPanel panel = new JPanel();
	  panel.setBorder(new EtchedBorder());
	  ok = new ImageButton(null,"ok.gif",null);
	  cancel = new ImageButton(null,"exit.gif",null);
	  panel.add(ok);
	  panel.add(cancel);
	  return panel;
	}
}
  
