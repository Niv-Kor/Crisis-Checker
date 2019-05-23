package albert.user_interface.states.report_builder;
import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextField;

class CustomTextField extends JTextField
{
	private static final long serialVersionUID = -2287207980711293839L;
	
	/**
	 * @param defMessage - The default message to be shown in the text field
	 */
	public CustomTextField(String defMessage) {
		super(defMessage);
		
		setBackground(Color.WHITE);
		setForeground(Color.GRAY);
		setHorizontalAlignment(JTextField.CENTER);
		addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                if (getText().equals(defMessage)) {
                    setText("");
                    setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (getText().equals("")) {
                    setText(defMessage);
                    setForeground(Color.GRAY);
                }
            }
        });
	}
}