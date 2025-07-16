package sql.fredy.test;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

/**
 *
 * @author tkfir
 */
public class CaretPosition extends JFrame {

    private JLabel label;

    public CaretPosition() {
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.getContentPane().setSize(800, 600);
        this.getContentPane().setLayout(new BorderLayout());

        label = new JLabel();

        RSyntaxTextArea text = new RSyntaxTextArea(25, 80);
        text.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                int startPos = text.getText().lastIndexOf(";", text.getCaretPosition());
                int endPos = text.getText().indexOf(";", text.getCaretPosition());
                String s = "";
                if (startPos > 0) {
                    if (endPos > 0) {
                        s = text.getText().substring(startPos + 1, endPos);
                    } else {
                        s = text.getText().substring(startPos + 1);
                    }
                } else {
                    s = text.getText();
                }
                //label.setText("Caretposition:" + String.format("%,d", text.getCaretPosition()) + " " + String.format("%,d", startPos) + "/" + String.format("%,d", endPos) + "|" + s);
                Caret caret = text.getCaret();
                try {
                    RSyntaxTextArea editArea = (RSyntaxTextArea) e.getSource();
                int linenum = 1;
                int columnnum = 1;
                    int caretpos = editArea.getCaretPosition();
                    linenum = editArea.getLineOfOffset(caretpos);
                    columnnum = caretpos - editArea.getLineStartOffset(linenum);
                    linenum += 1;
                    label.setText( linenum+ "/" + columnnum);

                } catch (Exception exc) {

                }
                label.updateUI();
            }
        });

        this.getContentPane().add(BorderLayout.CENTER, new JScrollPane(text));
        this.getContentPane().add(BorderLayout.SOUTH, label);
        //this.getContentPane().setSize(800,600);
        this.pack();
        this.setVisible(true);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CaretPosition();
            }
        });

    }
}
