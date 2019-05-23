package albert.user_interface.states.report_builder;
import java.awt.Color;
import javax.swing.ButtonGroup;
import javax.swing.event.ChangeEvent;
import albert.providers.Provider;

class ProviderRadioButton extends CustomRadioButton
{
	private static final long serialVersionUID = 7869171803636347106L;
	
	private ReportBuilderState state;
	private Provider provider;
	private boolean selected;
	
	/**
	 * @param name - Text to write next to the button
	 * @param group - The button group that will contain this button
	 * @param provider - Provider of the constructed report
	 * @param state - ReportBuilderState object that contains this button
	 */
	public ProviderRadioButton(String name, ButtonGroup group, Provider provider, ReportBuilderState state) {
		super(name, group);
		this.state = state;
		this.provider = provider;
		this.selected = isSelected();
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		super.stateChanged(e);
		
		if (!selected && isSelected()) {
			state.getReportRequest().setProvider(provider);
			state.rearrangeTable(provider, "");
		}
		
		selected = isSelected();
	}
	
	@Override
	public void setForeground(Color c) {
		super.setForeground(c);
		originColor = c;
	}
}
