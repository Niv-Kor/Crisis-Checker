package albert.user_interface.result_log;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicScrollBarUI;

import albert.user_interface.ResultLogWindow;
import albert.user_interface.states.report_builder.ReportBuilderState;

public class CampaignLogPanel extends JScrollPane
{
	public static enum Segmant {
		ERROR, CLEAR, ALL;
	}
	
	private static final long serialVersionUID = -1204666321456210045L;
	private static final JPanel DUMMY_PANEL = new JPanel();
	private static final Color CLEAR_CAMPAIGN_COLOR = new Color(79, 149, 60);
	private static final Color ERROR_CAMPAIGN_COLOR = new Color(199, 67, 63);
	
	private List<CampaignLog> sourceList, currentList;
	private int currentIndex;
	
	/**
	 * @param campaignLogs - List of the campaign logs to show in the panel
	 */
	public CampaignLogPanel(List<CampaignLog> campaignLogs) {
		super(initView(campaignLogs));
		DUMMY_PANEL.setBackground(ReportBuilderState.CENTER_COLOR);
		
		this.currentIndex = 0;
		this.sourceList = campaignLogs;
		this.currentList = campaignLogs;
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		getVerticalScrollBar().setUnitIncrement(16);
		
		//modifying scroll bar colors
		Color barColor = ReportBuilderState.CENTER_COLOR;
		getVerticalScrollBar().setBackground(barColor.darker());
		getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = barColor.brighter();
            }
        });
	}
	
	/**
	 * Change the list of the campaignLogs.
	 * @param list - The new list
	 */
	public void setCampaignLogs(List<CampaignLog> list) {
		currentList = list;
		currentIndex = 0;
		
		CampaignLog log = (currentList.size() > 0) ? currentList.get(currentIndex) : null;
		setLog(log);
	}
	
	/**
	 * @return the list of the campaign logs that are currently to the panel.
	 */
	public List<CampaignLog> getCurrentList() { return currentList; }
	
	/**
	 * Display the first campaign log.
	 */
	public void reset() {
		currentIndex = 0;
		
		CampaignLog log = (currentList.size() > 0) ? currentList.get(currentIndex) : null;
		setLog(log);
	}
	
	/**
	 * Display to the next campaign log.
	 */
	public void next() {
		if (++currentIndex <= currentList.size() - 1)
			setLog(currentList.get(currentIndex));
		else
			currentIndex = currentList.size() - 1;
	}
	
	/**
	 * Display the previous campaign log.
	 */
	public void previous() {
		if (--currentIndex >= 0)
			setLog(currentList.get(currentIndex));
		else
			currentIndex = 0;
	}
	
	/**
	 * Permanently change the campaign logs displayed in the planel,
	 * using a specific segmantation.
	 * 
	 * @param seg - The segmantation to use
	 */
	public void trim(Segmant seg) {
		setCampaignLogs(segmant(seg));
	}
	
	/**
	 * Retrieve an alternative list of campaign logs, based on a specific segmantation.
	 * This method does not override the list this panel displays.
	 * 
	 * @param seg - The segmantation to use
	 * @return a sub-list of the panel's current list.
	 */
	public List<CampaignLog> segmant(Segmant seg) {
		List<CampaignLog> segmanted = new ArrayList<CampaignLog>();
		
		switch(seg) {
			case ALL: {
				for (CampaignLog log : sourceList)
					segmanted.add(log);
				
				break;
			}
			case ERROR: {
				for (CampaignLog log : sourceList)
					if (!log.isClear()) segmanted.add(log);
				
				break;
			}
			case CLEAR: {
				for (CampaignLog log : sourceList)
					if (log.isClear()) segmanted.add(log);
				
				break;
			}
		}
		
		return segmanted;
	}
	
	/**
	 * Display a specific campaign log in the panel.
	 * @param log - The campaign log to display
	 */
	public void setLog(CampaignLog log) {
		if (log != null) setViewportView(log);
		else setViewportView(DUMMY_PANEL);
	}
	
	/**
	 * @return the displayed campaign's name.
	 */
	public String getCurrentCampaignName() {
		CampaignLog log = (currentList.size() > 0) ? currentList.get(currentIndex) : null;
		return (log != null) ? log.getCampaign().getName() : "";
	}
	
	/**
	 * @return the displayed campaign's color (different when clear or has errors).
	 */
	public Color getCurrentCampaignColor() {
		CampaignLog log = (currentList.size() > 0) ? currentList.get(currentIndex) : null;
		if (log != null) return log.isClear() ? CLEAR_CAMPAIGN_COLOR : ERROR_CAMPAIGN_COLOR;
		else return ResultLogWindow.PANEL_COLOR;
		
	}
	
	@Override
	public void setBackground(Color color) {
		super.setBackground(color);
		
		if (currentList != null)
			for (CampaignLog log : currentList)
				log.setBackground(color);
	}
	
	@Override
	public void setPreferredSize(Dimension dim) {
		super.setPreferredSize(dim);
		
		if (currentList != null)
			for (CampaignLog log : currentList)
				log.setPreferredSize(dim);
	}
	
	/**
	 * @return the index of the current displayed campaign log.
	 */
	public int getCurrentIndex() { return currentIndex; }
	
	/**
	 * Get the first campaign in the list to display.
	 * This method is used inside the super() declaration in the constructor.
	 * 
	 * @param list - The list of campaign logs to display in the panel
	 * @return the first campaign log (as a Component object).
	 */
	private static Component initView(List<CampaignLog> list) {
		return (list.size() > 0) ? list.get(0) : null;
	}
}