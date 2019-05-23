package albert.user_interface.states;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.LayoutManager;
import javax.swing.JPanel;
import albert.user_interface.Window;

public abstract class State
{
	protected Window window;
	protected JPanel[] panes;
	protected GridBagConstraints gbc;
	private int paneIndex;
	
	/**
	 * @param window - The window that holds the state
	 * @param panesAmount - Amount of panels the state uses
	 */
	public State(Window window, int panesAmount) {
		this.panes = new JPanel[panesAmount];
		this.window = window;
		this.gbc = new GridBagConstraints();
		this.paneIndex = 0;
	}
	
	/**
	 * Insert a component to a panel with grid bag layout.
	 * @param panel - The container panel
	 * @param c - Component to add
	 * @param x - x grid
	 * @param y - y grid
	 */
	protected void insertComponent(JPanel panel, Component c, int x, int y) {
		gbc.gridx = x;
		gbc.gridy = y;
		panel.add(c, gbc);
	}
	
	/**
	 * Insert a component to a panel with border layout
	 * @param panel - The container panel
	 * @param c - Component to add
	 * @param location - BorderLayout constant that defines location
	 */
	protected void insertComponent(JPanel panel, Component c, String location) {
		try { panel.add(c, location); }
		catch(IllegalArgumentException e) { System.err.println("Illegal location " + location); }
	}
	
	/**
	 * Create a panel of the state.
	 * @param layout - Panel's layout
	 * @param dim - Panel's dimension
	 * @param color - Panel's background color.
	 * 				  if left as 'null', set as 'setOpaque(false)'.
	 */
	protected void createPanel(LayoutManager layout, Dimension dim, Color color) {
		if (paneIndex >= panes.length) return;
		
		panes[paneIndex] = new JPanel(layout);
		panes[paneIndex].setPreferredSize(dim);
		panes[paneIndex].setBackground(color);
		panes[paneIndex].setOpaque(color != null);
		++paneIndex;
	}
	
	/**
	 * Create a panel of the state.
	 * @param panel - The panel to use
	 * @param dim - Panel's dimension
	 * @param color - Panel's background color.
	 * 				  if left as 'null', set as 'setOpaque(false)'.
	 */
	protected void createPanel(JPanel panel, Dimension dim, Color color) {
		if (paneIndex >= panes.length) return;
		
		panes[paneIndex] = panel;
		panes[paneIndex].setPreferredSize(dim);
		panes[paneIndex].setBackground(color);
		panes[paneIndex].setOpaque(color != null);
		++paneIndex;
	}
	
	/**
	 * Insert all panels to the containing window
	 */
	public abstract void insertPanels();
	
	/**
	 * @return array of the state's panels
	 */
	public JPanel[] getPanes() { return panes; }
}