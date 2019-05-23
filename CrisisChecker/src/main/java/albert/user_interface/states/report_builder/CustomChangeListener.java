package albert.user_interface.states.report_builder;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import albert.reports.ReportRequest;
import albert.reports.TimeFrame;
import albert.reports.TimeFrame.TimeFrameType;

class CustomChangeListener implements ChangeListener
{
	/**
	 * A nested class to hold a JTextField object and its default text.
	 * 
	 * @author Niv Kor
	 */
	private static class Field
	{
		private JTextField field;
		private String str;
		
		/**
		 * @param f - The text field to hold
		 */
		private Field(JTextField f) {
			this.field = f;
			this.str = new String(f.getText());
		}
		
		/**
		 * @param c - Backgound color of the text field
		 */
		private void setBackground(Color c) { field.setBackground(c); }
		
		/**
		 * Enable the text field or disable it.
		 * A disabled field is painted gray and permits editing.
		 *  
		 * @param flag - True to enbale or false otherwise
		 */
		private void setEnabled(boolean flag) { field.setEnabled(flag); }
		
		/**
		 * Clear the text typed in the field.
		 * @param flag - True to clear (false does nothing)
		 */
		private void clearText(boolean flag) { if (flag) field.setText(""); }
		
		/**
		 * Type back the default text in the field.
		 * @param flag - True to restore (false does nothing)
		 */
		private void restoreOriginString(boolean flag) { if (flag) field.setText(str); }
	}
	
	private static final Color SELECTED_COLOR = new Color(206, 255, 0);
	
	private TimeFrameType timeFrame;
	private Color originColor;
	private JRadioButton button;
	private ReportRequest request;
	private List<Field> dependentFields;
	
	/**
	 * @param button - The button this listener is attached to
	 * @param timeFrame - Time frame this listener is about
	 * @param request - The report's request to modify
	 */
	public CustomChangeListener(JRadioButton button, TimeFrameType timeFrame, ReportRequest request) {
		this.button = button;
		this.originColor = button.getForeground();
		this.dependentFields = new ArrayList<Field>();
		this.timeFrame = timeFrame;
		this.request = request;
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		if (button.isSelected()) {
			button.setForeground(SELECTED_COLOR);
			activateFields(true);
			request.setTimeFrame(new TimeFrame(timeFrame));
		}
		else {
			button.setForeground(originColor);
			activateFields(false);
		}
	}
	
	/**
	 * Add a text field that this listener will affect.
	 * Affected fields will be painted gray when the listener decides to deactivate them.
	 * @param textField
	 */
	public void addDependentField(JTextField textField) {
		dependentFields.add(new Field(textField));
	}
	
	/**
	 * Activate the depandent fields added to this listener.
	 * @param flag - True to activate or false to deactivate
	 */
	public void activateFields(boolean flag) {
		Color bgColor = flag ? Color.WHITE : Color.GRAY;
		
		for (Field field : dependentFields) {
			field.setBackground(bgColor);
			field.clearText(!flag);
			field.restoreOriginString(flag);
			field.setEnabled(flag);
		}
	}
}