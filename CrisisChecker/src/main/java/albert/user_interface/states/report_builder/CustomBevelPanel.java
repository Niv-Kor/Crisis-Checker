package albert.user_interface.states.report_builder;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.LayoutManager;

import javax.swing.JPanel;

import albert.user_interface.MainWindow;

class CustomBevelPanel extends JPanel
{
	private static final long serialVersionUID = 7090903579212090728L;
	private static final Color LINE_COLOR = new Color(92, 125, 125);
	
	/**
	 * @param layout - The layout to use with this panel
	 */
	public CustomBevelPanel(LayoutManager layout) {
		super(layout);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.setColor(LINE_COLOR);
		drawLine(g, 316);
		drawLine(g, 451);
	}
	
	/**
	 * Draw a simple horizontal line inside the panel
	 * @param g - The Graphics object of the panel 
	 * @param y - The height of the line (y grid)
	 */
	private void drawLine(Graphics g, int y) {
		g.drawLine(0, y, MainWindow.DIM.width, y);
		g.drawLine(0, y + 2, MainWindow.DIM.width, y + 2);
	}
}