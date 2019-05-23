package albert.user_interface.result_log;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import javax.swing.JLabel;
import javax.swing.JPanel;

import albert.user_interface.ResultLogWindow;
import albert.utility.InteractiveIcon;
import albert.utility.math.Percentage;

public class DirectivePanel extends JPanel implements MouseWheelListener
{
	/**
	 * A label that's formatted as "x/y" where x is the current index of the page,
	 * and y is the amount of pages.
	 * 
	 * @author Niv Kor
	 */
	private static class DivisorLabel extends JLabel
	{
		private static final long serialVersionUID = 1374014364495536965L;
		
		private int current, total;
		
		/**
		 * @param total - The total amount of pages
		 */
		public DivisorLabel(int total) {
			setFont(ResultLogWindow.FONT);
			this.total = total;
			this.current = (total > 0) ? 1 : 0;
			set(current, total);
		}
		
		/**
		 * @param totalAmount - Total amount of pages
		 */
		public void setTotal(int totalAmount) {
			total = totalAmount;
			reset();
		}
		
		/**
		 * Manually set a page index and the amount of all pages.
		 * @param num - Page index
		 * @param of - Total pages
		 */
		private void set(int num, int of) {
			setText(num + "/" + of);
		}
		
		/**
		 * Move back to page 1 (or 0 if total amount is 0).
		 */
		public void reset() {
			current = (total > 0) ? 1 : 0;
			set(current, total);
		}
		
		/**
		 * Increment page number (minimum is 1).
		 */
		public void next() {
			if (++current <= total) set(current, total);
			else current = total;
		}
		
		/**
		 * Decrement page number (maximum is total).
		 */
		public void previous() {
			if (--current >= 1) set(current, total);
			else current = (total > 0) ? 1 : 0;
		}
	}
	
	private static final long serialVersionUID = -3549354674581123405L;
	
	private ResultLogWindow resLogWindow;
	private DivisorLabel divisorLab;
	private InteractiveIcon nextArrow, prevArrow;
	private Timer arrowLedTimer;
	private TimerTask timerTask;
	private boolean taskScheduled;
	
	/**
	 * @param resLogWindow - The window that will contain this panel
	 * @param dim - Dimension of the panel
	 */
	public DirectivePanel(ResultLogWindow resLogWindow, Dimension dim) {
		super(new BorderLayout());
		setPreferredSize(dim);
		
		this.resLogWindow = resLogWindow;
		addMouseWheelListener(this);
		
		Dimension arrowPaneDim = Percentage.createDimension(getPreferredSize(), 100, 30);
		GridBagConstraints constraints = new GridBagConstraints();
		
		//up arrow
		JPanel northPane = new JPanel(new GridBagLayout());
		northPane.setPreferredSize(arrowPaneDim);
		northPane.setOpaque(false);
		
		this.prevArrow = new InteractiveIcon("unselected_up_campaign.png");
		prevArrow.setSelectedIcon("selected_up_campaign.png");
		prevArrow.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				resLogWindow.previous();
				return null;
			}
		});
		
		constraints.insets.bottom = 20;
		northPane.add(prevArrow, constraints);
		constraints.insets.bottom = 0;
		add(northPane, BorderLayout.NORTH);
		
		//numbering label (x/y)
		JPanel centerPane = new JPanel(new GridBagLayout());
		centerPane.setPreferredSize(Percentage.createDimension(getPreferredSize(), 100, 40));
		centerPane.setOpaque(false);
		
		this.divisorLab = new DivisorLabel(resLogWindow.getCampaignLogPanel().getCurrentList().size());
		divisorLab.setForeground(Color.WHITE);
		
		centerPane.add(divisorLab, constraints);
		add(centerPane, BorderLayout.CENTER);
		
		//down arrow
		JPanel southPane = new JPanel(new GridBagLayout());
		southPane.setPreferredSize(arrowPaneDim);
		southPane.setOpaque(false);
		
		this.nextArrow = new InteractiveIcon("unselected_down_campaign.png");
		nextArrow.setSelectedIcon("selected_down_campaign.png");
		nextArrow.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				resLogWindow.next();
				return null;
			}
		});
		
		constraints.insets.top = 20;
		southPane.add(nextArrow, constraints);
		constraints.insets.top = 0;
		add(southPane, BorderLayout.SOUTH);
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		boolean down = e.getWheelRotation() > 0;
		
		if (down) {
			resLogWindow.next();
			nextArrow.mouseEntered(null);
		}
		else {
			resLogWindow.previous();
			prevArrow.mouseEntered(null);
		}
		
		if (!taskScheduled)	executeTask();
	}
	
	/**
	 * Schedule the task that cancels the led light from the arrow,
	 * which turns on after a mouse wheel event.
	 */
	private void executeTask() {
		taskScheduled = true;
		
		this.timerTask = new TimerTask() {
			@Override
			public void run() {
				nextArrow.mouseExited(null);
				prevArrow.mouseExited(null);
				taskScheduled = false;
			}
		};
		
		arrowLedTimer = new Timer();
		arrowLedTimer.schedule(timerTask, 200);
	}
	
	/**
	 * @param totalAmount - Total amount of pages
	 */
	public void setTotal(int totalAmount) {
		divisorLab.setTotal(totalAmount);
	}
	
	/**
	 * Increment page number (minimum is 1).
	 */
	public void next() {
		divisorLab.next();
	}
	
	/**
	 * Decrement page number (maximum is total).
	 */
	public void previous() {
		divisorLab.previous();
	}
}