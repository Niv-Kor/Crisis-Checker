package albert.user_interface;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import org.threeten.bp.LocalDateTime;

import albert.excel_segmantation.Campaign;
import albert.excel_segmantation.Sheet;
import albert.reports.ReportRequest;
import albert.user_interface.result_log.ButtonGroup;
import albert.user_interface.result_log.CampaignLog;
import albert.user_interface.result_log.CampaignLogPanel;
import albert.user_interface.result_log.CampaignLogPanel.Segmant;
import albert.user_interface.result_log.DirectivePanel;
import albert.user_interface.result_log.SegmantationButton;
import albert.user_interface.result_log.graph.GraphPanel;
import albert.utility.files.Library;
import albert.utility.files.FontHandler;
import albert.utility.files.FontHandler.FontStyle;
import albert.utility.math.Percentage;

public class ResultLogWindow extends Window
{
	private static final long serialVersionUID = 4541736225189004723L;
	private static final String TITLE = "Results log";
	public static final Font FONT = FontHandler.load("Ubuntu", FontStyle.PLAIN, 14);
	private static final Dimension GENERAL_DIM = new Dimension(600, 700);
	private static final Dimension HALF_DIM = Percentage.createDimension(GENERAL_DIM, 100, 50);
	private static final Color BACKGROUP_COLOR = MainWindow.COLOR;
	public static final Color PANEL_COLOR = new Color(54, 54, 54);
	public static final Color FORGROUND_COLOR = Color.WHITE;
	
	private CampaignLogPanel campaignLogPanel;
	private DirectivePanel directivePanel;
	private JPanel errorPane, graphPane;
	private JPanel campaignNamePane;
	private JLabel campaignName;
	private Sheet sheet;
	private boolean graphOpen;
	private Border raisedBorder, loweredBorder;
	private List<CampaignLog> campaignLogs;
	
	public ResultLogWindow(Library book, ReportRequest request) {
		super(TITLE + " for " + request.getAccount().getName(), new JPanel(new BorderLayout()));
		
		//locate the frame at the top half of the screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = screenSize.width / 2 - GENERAL_DIM.width / 2;
		int y = screenSize.height / 3 - HALF_DIM.height / 2;
		setLocation(x, y);
		
		addWindowListener(new WindowListener() {
			public void windowOpened(WindowEvent e) { LogConsole.write("Report downloaded at " + LocalDateTime.now()); }
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
			public void windowClosing(WindowEvent e) { LogConsole.clear(); }
			public void windowClosed(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
		});
		
		this.sheet = book.getSheet();
		
		List<Campaign> campaigns = sheet.segmantCampaigns();
		this.campaignLogs = new ArrayList<CampaignLog>();
		
		CampaignLog tempCampLog;
		for (Campaign campaign : campaigns) {
			campaign.getErrorDetector().detect(request);
			tempCampLog = new CampaignLog(campaign);
			tempCampLog.setBackground(BACKGROUP_COLOR);
			campaignLogs.add(tempCampLog);
		}
		
		this.raisedBorder = new BevelBorder(BevelBorder.RAISED);
		this.loweredBorder = new BevelBorder(BevelBorder.LOWERED);
		
		buildErrorPane();
		buildGraphPane();
		
		mainPanel.add(errorPane, BorderLayout.CENTER);
		
		invalidate();
		repaint();
	}
	
	private void buildErrorPane() {
		this.errorPane = new JPanel(new BorderLayout());
		errorPane.setPreferredSize(HALF_DIM);
		
		//processing pane
		JPanel processingPane = new JPanel(new BorderLayout());
		processingPane.setPreferredSize(Percentage.createDimension(errorPane.getPreferredSize(), 100, 10));
		processingPane.setBackground(PANEL_COLOR.darker());
		processingPane.setBorder(raisedBorder);
		errorPane.add(processingPane, BorderLayout.NORTH);
		
		JLabel processingLab = new JLabel();
		processingLab.setForeground(new Color(206, 255, 0));
		processingLab.setFont(FONT);
		
		if (campaignLogs.size() > 0)
			processingLab.setText("  Successfuly downloaded and analyzed " + campaignLogs.size() + " campaigns.");
		else
			processingLab.setText("  This account has no active Albert campaigns.");
		
		processingPane.add(processingLab);
		
		//console pane
		JPanel consolePane = new JPanel(new BorderLayout());
		consolePane.setPreferredSize(Percentage.createDimension(errorPane.getPreferredSize(), 100, 90));
		consolePane.setBackground(PANEL_COLOR);
		
		//north distribution pane
		JPanel distributionPane = new JPanel();
		distributionPane.setLayout(new BoxLayout(distributionPane, BoxLayout.X_AXIS));
		distributionPane.setPreferredSize(Percentage.createDimension(consolePane.getPreferredSize(), 100, 10));
		distributionPane.setBackground(PANEL_COLOR);
		distributionPane.setBorder(raisedBorder);
		consolePane.add(distributionPane, BorderLayout.NORTH);
		
		Dimension buttonDim = Percentage.createDimension(distributionPane.getPreferredSize(), 33, 100);
		ButtonGroup buttonGroup = new ButtonGroup();
		
		SegmantationButton errorsBtn = new SegmantationButton("error_campaign.png", "Errors", buttonGroup);
		errorsBtn.setPreferredSize(buttonDim);
		errorsBtn.setBackground(PANEL_COLOR);
		distributionPane.add(Box.createHorizontalGlue());
		distributionPane.add(errorsBtn);
		
		SegmantationButton clearBtn = new SegmantationButton("clear_campaign.png", "Clear", buttonGroup);
		clearBtn.setPreferredSize(buttonDim);
		clearBtn.setBackground(PANEL_COLOR);
		distributionPane.add(Box.createHorizontalGlue());
		distributionPane.add(clearBtn);
		
		SegmantationButton allBtn = new SegmantationButton("all_campaign.png", "All campaigns", buttonGroup);
		allBtn.setPreferredSize(buttonDim);
		allBtn.setBackground(PANEL_COLOR);
		distributionPane.add(Box.createHorizontalGlue());
		distributionPane.add(allBtn);
		
		//make errors tab the only one selected
		errorsBtn.select();
		
		//campaign log pane
		JPanel accountPane = new JPanel(new BorderLayout());
		accountPane.setPreferredSize(Percentage.createDimension(consolePane.getPreferredSize(), 90, 80));
		accountPane.setBackground(BACKGROUP_COLOR);
		accountPane.setBorder(raisedBorder);
		
		//log panels
		this.campaignLogPanel = new CampaignLogPanel(campaignLogs);
		campaignLogPanel.trim(Segmant.ERROR);
		campaignLogPanel.setPreferredSize(Percentage.createDimension(accountPane.getPreferredSize(), 100, 90));
		campaignLogPanel.setBackground(BACKGROUP_COLOR);
		campaignLogPanel.setBorder(loweredBorder);
		accountPane.add(campaignLogPanel, BorderLayout.CENTER);
		consolePane.add(accountPane, BorderLayout.CENTER);
		
		//update buttons' functionality
		allBtn.setAmount(campaignLogPanel.segmant(Segmant.ALL).size());
		allBtn.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				campaignLogPanel.trim(Segmant.ALL);
				directivePanel.setTotal(allBtn.getAmount());
				updateCampaignLog();
				return null;
			}
		});
		
		errorsBtn.setAmount(campaignLogPanel.segmant(Segmant.ERROR).size());
		errorsBtn.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				campaignLogPanel.trim(Segmant.ERROR);
				directivePanel.setTotal(errorsBtn.getAmount());
				updateCampaignLog();
				return null;
			}
		});
		
		clearBtn.setAmount(campaignLogPanel.segmant(Segmant.CLEAR).size());
		clearBtn.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				campaignLogPanel.trim(Segmant.CLEAR);
				directivePanel.setTotal(clearBtn.getAmount());
				updateCampaignLog();
				return null;
			}
		});
		
		//account name
		this.campaignName = new JLabel(campaignLogPanel.getCurrentCampaignName());
		campaignName.setForeground(Color.WHITE);
		campaignName.setFont(FONT);
		
		this.campaignNamePane = new JPanel();
		campaignNamePane.setPreferredSize(Percentage.createDimension(accountPane.getPreferredSize(), 100, 10));
		campaignNamePane.setBackground(campaignLogPanel.getCurrentCampaignColor());
		campaignNamePane.add(campaignName);
		accountPane.add(campaignNamePane, BorderLayout.NORTH);
		
		//west guide pane
		Dimension directiveDim = Percentage.createDimension(consolePane.getPreferredSize(), 10, 80);
		
		this.directivePanel = new DirectivePanel(this, directiveDim);
		directivePanel.setBackground(PANEL_COLOR);
		directivePanel.setBorder(raisedBorder);
		consolePane.add(directivePanel, BorderLayout.WEST);
		
		//south static log pane
		JPanel staticLogPane = new JPanel(new BorderLayout());
		staticLogPane.setPreferredSize(Percentage.createDimension(consolePane.getPreferredSize(), 100, 10));
		staticLogPane.setBackground(PANEL_COLOR);
		staticLogPane.setBorder(raisedBorder);
		
		staticLogPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (!graphOpen) {
					setSize(GENERAL_DIM);
					setPreferredSize(GENERAL_DIM);
					mainPanel.setPreferredSize(GENERAL_DIM);
					errorPane.setPreferredSize(HALF_DIM);
					mainPanel.add(graphPane, BorderLayout.SOUTH);
					graphOpen = true;
				}
				else {
					setSize(HALF_DIM);
					setPreferredSize(HALF_DIM);
					mainPanel.setPreferredSize(HALF_DIM);
					errorPane.setPreferredSize(HALF_DIM);
					mainPanel.remove(graphPane);
					graphOpen = false;
				}
				
				invalidate();
				repaint();
			}
		});
		
		consolePane.add(staticLogPane, BorderLayout.SOUTH);
		
		errorPane.add(consolePane, BorderLayout.CENTER);
	}
	
	private void buildGraphPane() {
		this.graphPane = new JPanel(new BorderLayout());
		graphPane.setPreferredSize(HALF_DIM);
		graphPane.setBackground(BACKGROUP_COLOR);
		
		GraphPanel drawingPane = new GraphPanel();
		graphPane.add(drawingPane, BorderLayout.CENTER);
	}
	
	public void run(boolean flag) {
		setVisible(flag);
	}
	
	private void updateCampaignLog() {
		campaignName.setText(campaignLogPanel.getCurrentCampaignName());
		campaignNamePane.setBackground(campaignLogPanel.getCurrentCampaignColor());
	}
	
	public void next() {
		campaignLogPanel.next();
		directivePanel.next();
		updateCampaignLog();
	}
	
	public void previous() {
		campaignLogPanel.previous();
		directivePanel.previous();
		updateCampaignLog();
	}
	
	public CampaignLogPanel getCampaignLogPanel() { return campaignLogPanel; }

	@Override
	public Dimension getDimension() { return HALF_DIM; }
}