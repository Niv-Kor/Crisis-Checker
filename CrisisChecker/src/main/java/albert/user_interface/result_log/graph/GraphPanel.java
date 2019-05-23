package albert.user_interface.result_log.graph;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import albert.excel_segmantation.Campaign;

public class GraphPanel extends JPanel
{
	public GraphPanel() {
		
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.GREEN);
		
		Point p1 = new Point(20, 32);
		Point p2 = new Point(30, 59);
		
		drawLine(g, p1, p2);
	}
	
	private void drawLine(Graphics g, Point p1, Point p2) {
		g.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());
	}
}