/*
 * The MIT License
 *
 * Copyright 2017 fredy.
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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package sql.fredy.share;

import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author fredy
 */
public class JdbcTableIntegerRenderer extends DefaultTableCellRenderer {

    public JdbcTableIntegerRenderer() {
        super();
    }

    public void setValue(Object value) {

        try {
            //setText((value == null) ? "{NULL}" : String.format("%,d", value));
            if ( value == null ) {
                setNull();
            } else {
                 setText(String.format("%,d", value));
            }
        } catch (Exception e) {
            setText("NULL");
            Font newLabelFont = new Font(this.getFont().getName(), Font.ITALIC, this.getFont().getSize());
            this.setFont(newLabelFont);
        }
        if (value == null) {
            setToolTipText("value is NULL");
        }
        setHorizontalAlignment(JLabel.RIGHT);

    }

    private void setNull() {
        setText("NULL");
            Font newLabelFont = new Font(this.getFont().getName(), Font.ITALIC, this.getFont().getSize());
            this.setFont(newLabelFont); 
    }
}
