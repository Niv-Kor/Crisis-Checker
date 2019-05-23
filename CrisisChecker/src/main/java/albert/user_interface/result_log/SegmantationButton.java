package albert.user_interface.result_log;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.Callable;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;

import albert.user_interface.ResultLogWindow;
import albert.utility.files.ImageHandler;
import albert.utility.math.Percentage;

public class SegmantationButton extends JPanel implements MouseListener
{
	private static final long serialVersionUID = 3410838008003300855L;
	private static final Color SELECTED_COLOR = ResultLogWindow.PANEL_COLOR.brighter();
	private static final Color UNSELECTED_COLOR = ResultLogWindow.PANEL_COLOR;
	private static final Color FOREGROUND = Color.WHITE;
	private static final Font FONT = ResultLogWindow.FONT;
	private static final Border UNSELECTED_BORDER = new MatteBorder(1, 1, 1, 1, UNSELECTED_COLOR.darker());
	private static final Border SELECTED_BORDER = new MatteBorder(2, 2, 2, 2, SELECTED_COLOR);
	
	private JLabel nameLab, amountLab;
	private Color originBG;
	private int amount;
	private boolean selected;
	private ButtonGroup group;
	private Callable<?> func;
	
	public SegmantationButton(String icon, String name, ButtonGroup group) {
		super(new BorderLayout());
		setBackground(SELECTED_COLOR);
		setFocusable(false);
		
		this.selected = false;
		this.group = group;
		group.add(this);
		
		//west icon pane
		JPanel westPane = new JPanel();
		westPane.setOpaque(false);
		
		JLabel iconLab = new JLabel();
		iconLab.setFont(FONT);
		if (ImageHandler.test(icon)) iconLab.setIcon(ImageHandler.loadIcon(icon));
		
		westPane.setPreferredSize(Percentage.createDimension(iconLab.getPreferredSize(), 170, 100));
		westPane.add(iconLab);
		add(westPane, BorderLayout.WEST);
		
		//center name pane
		JPanel centerPane = new JPanel();
		centerPane.setOpaque(false);
		centerPane.setPreferredSize(Percentage.createDimension(getPreferredSize(), 33, 100));
		
		this.nameLab = new JLabel(name);
		nameLab.setForeground(FOREGROUND);
		nameLab.setFont(FONT);
		
		centerPane.setPreferredSize(Percentage.createDimension(nameLab.getPreferredSize(), 170, 100));
		centerPane.add(nameLab);
		add(centerPane, BorderLayout.CENTER);
		
		//east amount pane
		JPanel eastPane = new JPanel();
		eastPane.setOpaque(false);
		eastPane.setPreferredSize(Percentage.createDimension(getPreferredSize(), 33, 100));
		
		this.amount = 0;
		this.amountLab = new JLabel("(!init)");
		amountLab.setForeground(FOREGROUND);
		amountLab.setFont(FONT);
		
		eastPane.setPreferredSize(amountLab.getPreferredSize());
		eastPane.add(amountLab);
		add(eastPane, BorderLayout.EAST);
		
		setBorder(UNSELECTED_BORDER);
		addMouseListener(this);
	}
	
	public void setAmount(int a) {
		amountLab.setText("(" + a + ")");
		amount = a;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		super.setBackground(originBG.brighter());
		select();
		
		if (func != null) {
			try { func.call(); }
			catch(Exception ex) { ex.printStackTrace(); }
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		super.setBackground(originBG);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		setBorder(SELECTED_BORDER);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		setBorder(UNSELECTED_BORDER);
		
	}
	
	@Override
	public void setBackground(Color color) {
		super.setBackground(color);
		originBG = color;
	}
	
	/**
	 * Add functionality to the button.
	 * @param f - The function to add
	 */
	public void setFunction(Callable<?> f) {
		this.func = f;
	}
	
	/**
	 * @return amount of pages the button has to show.
	 */
	public int getAmount() { return amount; }
	
	/**
	 * Select the button and deselect all others.
	 */
	public void select() {
		//deselect all other buttons in the group
		group.deselectAll();
		
		//select this one
		setBackground(SELECTED_COLOR);
		selected = true;
	}
	
	/**
	 * Deselect the button.
	 * Can cause a situation where none of the buttons are selected.
	 */
	public void deselect() {
		setBackground(UNSELECTED_COLOR);
		selected = false;
	}
	
	/**
	 * @return true if the button is selected or false otherwise.
	 */
	public boolean isSelected() { return selected; }
	
	@Override
	public void mouseClicked(MouseEvent e) {}
}