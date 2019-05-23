package albert.user_interface.states.report_builder;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import albert.error.ErrorType;
import albert.reports.ReportRequest;

class CriteriaBox extends CustomCheckBox
{
	private static final long serialVersionUID = -6351730569192818868L;
	
	private ReportRequest reportRequest;
	private JCheckBox selectAll;
	private ErrorType error;
	
	/**
	 * @param all - All other JCheckBox objects this object affects
	 * @param error - The error type this box references
	 * @param reportRequest - The report request this box helps building
	 */
	public CriteriaBox(JCheckBox all, ErrorType error, ReportRequest reportRequest) {
		super(error.name());
		this.selectAll = all;
		this.reportRequest = reportRequest;
		this.error = error;
		addChangeListener(this);
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		super.stateChanged(e);
		
		if (isSelected()) reportRequest.addCriterion(error);
		else {
			selectAll.setSelected(false);
			reportRequest.removeCriterion(error);
		}
	}
}