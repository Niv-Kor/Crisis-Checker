package albert.user_interface;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

public class LogConsole extends JPanel
{
	private static final long serialVersionUID = -7306290403458822487L;
	private final static Color MESSAGE_COLOR = new Color(206, 255, 0);
	private final static int SPACE = 2;
	
	private static JLabel message = new JLabel();
	
	public LogConsole() {
		super(new BorderLayout());
		
		setBorder(new BevelBorder(BevelBorder.LOWERED));
		setBackground(new Color(100, 100, 100));
		message.setForeground(MESSAGE_COLOR);
		add(message, BorderLayout.LINE_START);
	}
	
	/**
	 * Write to the log (overriden).
	 * 
	 * @param msg - The message to write
	 */
	public static void write(String msg) {
		String space = "";
		for (int i = 0; i < SPACE; i++) space = space.concat(" ");
		
		message.setText(space + msg);
	}
	
	/**
	 * Clear all text in the log.
	 */
	public static void clear() { write(""); }
}