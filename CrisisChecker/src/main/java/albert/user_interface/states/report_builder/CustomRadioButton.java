package albert.user_interface.states.report_builder;
import java.awt.Color;
import java.awt.Font;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import albert.utility.files.FontHandler;
import albert.utility.files.FontHandler.FontStyle;

class CustomRadioButton extends JRadioButton implements ChangeListener
{
	private static final long serialVersionUID = 8686643480017209081L;
	protected static final Color SELECTED_COLOR = new Color(206, 255, 0);
	protected final static Font FONT = FontHandler.load("Ubuntu", FontStyle.PLAIN, 14);
	
	protected Color originColor;
	
	/**
	 * @param name - The text to write next to the button
	 * @param group - The button group that will contain this button
	 */
	public CustomRadioButton(String name, ButtonGroup group) {
		super(name);
		group.add(this);
		setFocusable(false);
		addChangeListener(this);
		setFont(FONT);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (isSelected()) super.setForeground(SELECTED_COLOR);
		else setForeground(originColor);
	}
	
	@Override
	public void setForeground(Color c) {
		super.setForeground(c);
		originColor = c;
	}
}