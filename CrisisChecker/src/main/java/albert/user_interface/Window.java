package albert.user_interface;
import javax.swing.JFrame;
import javax.swing.JPanel;
import albert.user_interface.states.State;

public abstract class Window extends JFrame implements Mutable
{
	private static final long serialVersionUID = 2755460981095593660L;
	
	protected JPanel mainPanel;
	
	/**
	 * @param title - The title in the frame
	 * @param mainPanel - The panel to use as the main surface.
	 * 					  (if left as 'null', panel is initiated as 'new JPanel()').
	 */
	public Window(String title, JPanel mainPanel) {
		super(title);
		setSize(getDimension());
		setPreferredSize(getDimension());
		setResizable(false);
		setLocationRelativeTo(null);
		
		this.mainPanel = (mainPanel != null) ? mainPanel : new JPanel();
		mainPanel.setPreferredSize(getDimension());
		
		add(mainPanel);
		setVisible(true);
		pack();
	}
	
	@Override
	public void insertPanel(JPanel panel, Object location) {
		try { mainPanel.add(panel, (String) location); }
		catch(IllegalArgumentException | ClassCastException e) {
			System.err.println("The location argument (\"" + location + "\") must be a BorderLayout constant.");
		}
	}
	
	@Override
	public void removePanel(JPanel panel) {
		mainPanel.remove(panel);
	}
	
	@Override
	public void applyState(State currentState, State newState) {
		if (currentState != null)
			for (JPanel p : currentState.getPanes()) mainPanel.remove(p);
		
		newState.insertPanels();
		validate();
		repaint();
	}
}