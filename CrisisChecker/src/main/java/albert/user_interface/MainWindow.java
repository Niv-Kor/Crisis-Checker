package albert.user_interface;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JPanel;
import albert.utility.files.Document;
import albert.utility.files.ImageHandler;

public class MainWindow extends Window
{
	/**
	 * A panel with the program's logo. 
	 * 
	 * @author Niv Kor
	 */
	private static class LogoPanel extends JPanel
	{
		private static final long serialVersionUID = 3196768978650339418L;
		
		/**
		 * @param layout - The panel's layout
		 */
		public LogoPanel(LayoutManager layout) {
			super(layout);
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			setBackground(Color.BLACK);
			g.drawImage(ImageHandler.loadImage("background_600x500.png"), 0, 0, null);
		}
	}
	
	private static final long serialVersionUID = 5188462882967200442L;
	public static final String TITLE = "Crisis Checker";
	private static final String ICON_PATH = "icon.png";
	private static final int CONSOLE_HEIGHT = 25;
	public static final Dimension DIM = new Dimension(600, 800);
	private static final Dimension WINDOW_DIM = new Dimension(DIM.width, DIM.height + CONSOLE_HEIGHT);
	public static final Color COLOR = Color.BLACK;
	
	private static LogConsole logConsole;
	
	public MainWindow() {
		super(TITLE, new LogoPanel(new BorderLayout()));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setIconImage(ImageHandler.loadImage(ICON_PATH));
		addWindowListener(new WindowListener() {
			@Override
			public void windowClosing(WindowEvent e) {
				Document.deleteFiles();
			}
			
			public void windowOpened(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
		});
		
		logConsole = new LogConsole();
		logConsole.setPreferredSize(new Dimension(DIM.width, CONSOLE_HEIGHT));
		mainPanel.add(logConsole, BorderLayout.SOUTH);
		
		LogConsole.write("Welcome to CrisisChecker!");
		
		add(mainPanel);
		setVisible(true);
		pack();
	}

	@Override
	public Dimension getDimension() { return WINDOW_DIM; }
}