package albert.user_interface.states.report_builder;
import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import albert.utility.files.FontHandler;
import albert.utility.files.ImageHandler;
import albert.utility.files.FontHandler.FontStyle;

class CustomCheckBox extends JCheckBox implements ChangeListener
{
	private static final long serialVersionUID = -8931700857628460432L;
	protected static final Color SELECTED_COLOR = new Color(206, 255, 0);
	protected static final ImageIcon UNSELECTED_CHECKBOX = ImageHandler.loadIcon("unselected_checkbox.png");
	protected static final ImageIcon SELECTED_CHECKBOX = ImageHandler.loadIcon("selected_checkbox.png");
	protected final static Font FONT = FontHandler.load("Ubuntu", FontStyle.PLAIN, 14);
	
	protected Color originColor;
	
	/**
	 * @param name - Text to write next to the check box
	 */
	public CustomCheckBox(String name) {
		super(name);
		setIcon(UNSELECTED_CHECKBOX);
		setSelectedIcon(SELECTED_CHECKBOX);
		setFocusable(false);
		setFont(FONT);
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		if (isSelected()) super.setForeground(SELECTED_COLOR);
		else super.setForeground(originColor);
	}
	
	@Override
	public void setForeground(Color c) {
		super.setForeground(c);
		originColor = c;
	}
}