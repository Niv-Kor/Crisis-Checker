package albert.user_interface.states.report_builder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.threeten.bp.LocalDateTime;

import albert.error.ErrorType;
import albert.providers.Account;
import albert.providers.Provider;
import albert.providers.facebook.Facebook;
import albert.providers.google_ads.GoogleAccount;
import albert.providers.google_ads.GoogleAds;
import albert.reports.ReportRequest;
import albert.reports.TimeFrame;
import albert.reports.TimeFrame.DateRangeFormatter;
import albert.reports.TimeFrame.TimeFrameType;
import albert.user_interface.LogConsole;
import albert.user_interface.MainWindow;
import albert.user_interface.Window;
import albert.user_interface.states.State;
import albert.utility.InteractiveIcon;
import albert.utility.InteractiveLabel;
import albert.utility.files.FontHandler;
import albert.utility.files.FontHandler.FontStyle;
import albert.utility.files.ImageHandler;
import albert.utility.math.Percentage;
import albert.utility.math.Range;

public class ReportBuilderState extends State
{
	private final static Font LABEL_FONT = FontHandler.load("Raleway", FontStyle.PLAIN, 18);
	private final static Font VALUE_FONT = FontHandler.load("Ubuntu", FontStyle.PLAIN, 15);
	private final static int ID_COL_LENGTH = 100;
	private final static ImageIcon UNSELECTED_RADIO = ImageHandler.loadIcon("unselected_radio.png");
	private final static ImageIcon SELECTED_RADIO = ImageHandler.loadIcon("selected_radio.png");
	public final static Color CENTER_COLOR = new Color(29, 29, 29);
	
	private ProviderRadioButton googleButton, facebookButton;
	private CustomCellRenderer tableRender;
	private DefaultTableModel tableModel;
	private CustomTable table;
	private List<Account> selectedCustomers;
	private ReportRequest reportRequest;
	private CustomTextField fromDay, fromMonth, fromYear;
	private CustomTextField toDay, toMonth, toYear;
	
	public ReportBuilderState(Window window) {
		super(window, 1);
		
		this.selectedCustomers = new ArrayList<Account>();
		this.reportRequest = new ReportRequest();
		
		//panels
		createPanel(new CustomBevelPanel(new BorderLayout()), window.getDimension(), null);
		
		JPanel northPane = new JPanel(new GridBagLayout());
		northPane.setPreferredSize(Percentage.createDimension(MainWindow.DIM, 100, 40));
		northPane.setOpaque(false);
		panes[0].add(northPane, BorderLayout.NORTH);
		
		JPanel centerPane = new JPanel(new BorderLayout());
		centerPane.setPreferredSize(Percentage.createDimension(MainWindow.DIM, 100, 20));
		centerPane.setBackground(new Color(29, 29, 29));
		panes[0].add(centerPane, BorderLayout.CENTER);
		
		JPanel southPane = new JPanel(new BorderLayout());
		southPane.setPreferredSize(Percentage.createDimension(MainWindow.DIM, 100, 40));
		southPane.setOpaque(false);
		panes[0].add(southPane, BorderLayout.SOUTH);
		
		buildNorthPanel(northPane);
		buildCenterPanel(centerPane);
		buildSouthPanel(southPane);
		
		
		rearrangeTable(new GoogleAds(), "\n\n UNREACHABLE SEARCH RESULT \n\n");
	}
	
	/**
	 * @param northPane - JPanel to use as container
	 */
	private void buildNorthPanel(JPanel northPane) {
		//provider
		//icon
		gbc.insets = new Insets(0, 208, 10, 10);
		insertComponent(northPane, new JLabel(ImageHandler.loadIcon("/label_icons/provider.png")), 0, 0);
		
		//label
		JLabel providerLab = new JLabel("Provider :");
		providerLab.setForeground(Color.WHITE);
		providerLab.setFont(LABEL_FONT);
		gbc.insets = new Insets(-10, -150, 0, 10);
		insertComponent(northPane, providerLab, 1, 0);
		
		ButtonGroup group = new ButtonGroup();
		Provider googleProv = new GoogleAds();
		Provider facebookProv = new Facebook();
		
		googleButton = new ProviderRadioButton(" Google Ads", group, googleProv, this);
		googleButton.setForeground(Color.WHITE);
		googleButton.setOpaque(false);
		googleButton.setBorder(null);
		googleButton.setIcon(UNSELECTED_RADIO);
		googleButton.setSelectedIcon(SELECTED_RADIO);
		gbc.insets = new Insets(10, 285, 0, 10);
		insertComponent(northPane, googleButton, 0, 1);
		
		this.facebookButton = new ProviderRadioButton(" Facebook", group, facebookProv, this);
		facebookButton.setForeground(Color.WHITE);
		facebookButton.setOpaque(false);
		facebookButton.setBorder(null);
		facebookButton.setIcon(UNSELECTED_RADIO);
		facebookButton.setSelectedIcon(SELECTED_RADIO);
		gbc.insets = new Insets(10, 274, 50, 10);
		insertComponent(northPane, facebookButton, 0, 2);
		
		//criteria fields
		//icon
		gbc.insets = new Insets(0, 210, 10, 10);
		insertComponent(northPane, new JLabel(ImageHandler.loadIcon("/label_icons/criteria.png")), 0, 3);
		
		//label
		JLabel criteriaLab = new JLabel("Criteria :");
		criteriaLab.setForeground(Color.WHITE);
		criteriaLab.setFont(LABEL_FONT);
		gbc.insets = new Insets(0, -160, 0, 10);
		insertComponent(northPane, criteriaLab, 1, 3);
		
		CustomCheckBox selectAll = new CustomCheckBox("SELECT ALL");
		selectAll.setOpaque(false);
		selectAll.setBorder(null);
		selectAll.setForeground(Color.WHITE);
		
		//init field check boxes
		CriteriaBox[] fields = new CriteriaBox[ErrorType.values().length];
		for (int i = 0; i < fields.length; i++) {
			fields[i] = new CriteriaBox(selectAll, ErrorType.values()[i], reportRequest);
			fields[i].setOpaque(false);
			fields[i].setBorder(null);
			fields[i].setForeground(Color.WHITE);
		}
		
		//add listener to selectAll
		selectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < fields.length; i++) {
					fields[i].setSelected(selectAll.isSelected());
				}
			}
		});
		
		gbc.insets = new Insets(10, 273, 0, 0);
		insertComponent(northPane, selectAll, 0, 4);
		gbc.insets = new Insets(30, 235, 0, 0);
		insertComponent(northPane, fields[0], 0, 5);
		gbc.insets = new Insets(10, 295, 0, 0);
		insertComponent(northPane, fields[1], 0, 6);
		gbc.insets = new Insets(30, 0, 0, 0);
		insertComponent(northPane, fields[2], 1, 5);
		gbc.insets = new Insets(10, 10, 0, 0);
		insertComponent(northPane, fields[3], 1, 6);
	}
	
	/**
	 * @param centerPane - JPanel to use as container
	 */
	private void buildCenterPanel(JPanel centerPane) {
		JPanel headerPane = new JPanel(new GridBagLayout());
		headerPane.setPreferredSize(Percentage.createDimension(centerPane.getPreferredSize(), 100, 20));
		headerPane.setOpaque(false);
		insertComponent(centerPane, headerPane, BorderLayout.NORTH);
		
		JPanel fixedTimePane = new JPanel(new GridBagLayout());
		fixedTimePane.setPreferredSize(Percentage.createDimension(centerPane.getPreferredSize(), 100, 40));
		fixedTimePane.setOpaque(false);
		insertComponent(centerPane, fixedTimePane, BorderLayout.CENTER);
		
		JPanel customTimePane = new JPanel(new GridBagLayout());
		customTimePane.setPreferredSize(Percentage.createDimension(centerPane.getPreferredSize(), 100, 40));
		customTimePane.setOpaque(false);
		insertComponent(centerPane, customTimePane, BorderLayout.SOUTH);
		
		//fixed time frame
		LocalDateTime now = LocalDateTime.now();
		ButtonGroup group = new ButtonGroup();
		JLabel timeFrameLab = new JLabel("Time Frame :");
		timeFrameLab.setFont(LABEL_FONT);
		timeFrameLab.setForeground(Color.WHITE);
		
		CustomRadioButton time7 = new CustomRadioButton("7 Days", group);
		time7.setIcon(UNSELECTED_RADIO);
		time7.setSelectedIcon(SELECTED_RADIO);
		time7.setOpaque(false);
		time7.setBorder(null);
		time7.setForeground(Color.WHITE);
		time7.addChangeListener(new CustomChangeListener(time7, TimeFrameType.DAYS_7, reportRequest));
		
		CustomRadioButton time14 = new CustomRadioButton("14 Days", group);
		time14.setIcon(UNSELECTED_RADIO);
		time14.setSelectedIcon(SELECTED_RADIO);
		time14.setOpaque(false);
		time14.setBorder(null);
		time14.setForeground(Color.WHITE);
		time14.addChangeListener(new CustomChangeListener(time14, TimeFrameType.DAYS_14, reportRequest));
		
		CustomRadioButton time30 = new CustomRadioButton("30 Days", group);
		time30.setIcon(UNSELECTED_RADIO);
		time30.setSelectedIcon(SELECTED_RADIO);
		time30.setOpaque(false);
		time30.setBorder(null);
		time30.setForeground(Color.WHITE);
		time30.addChangeListener(new CustomChangeListener(time30, TimeFrameType.DAYS_30, reportRequest));
		
		CustomRadioButton customTime = new CustomRadioButton("Customized", group);
		customTime.setIcon(UNSELECTED_RADIO);
		customTime.setSelectedIcon(SELECTED_RADIO);
		customTime.setOpaque(false);
		customTime.setBorder(null);
		customTime.setForeground(Color.WHITE);
		CustomChangeListener customTimeListener = new CustomChangeListener(customTime, TimeFrameType.CUSTOMIZED, reportRequest);
		customTime.addChangeListener(customTimeListener);
		
		//customized time frame
		Dimension dateDim = new Dimension(30, 25);
		Dimension yearDim = Percentage.createDimension(dateDim, 160, 100);
		
		JLabel from = new JLabel("From");
		from.setForeground(Color.WHITE);
		
		this.fromDay = new CustomTextField("" + now.getDayOfMonth());
		fromDay.setPreferredSize(dateDim);
		customTimeListener.addDependentField(fromDay);
		
		this.fromMonth = new CustomTextField("" + now.getMonth().getValue());
		fromMonth.setPreferredSize(dateDim);
		customTimeListener.addDependentField(fromMonth);
		
		this.fromYear = new CustomTextField("" + now.getYear());
		fromYear.setPreferredSize(yearDim);
		customTimeListener.addDependentField(fromYear);
		
		JLabel to = new JLabel("To");
		to.setForeground(Color.WHITE);
		
		this.toDay = new CustomTextField("1");
		toDay.setPreferredSize(dateDim);
		customTimeListener.addDependentField(toDay);
		
		this.toMonth = new CustomTextField("" + (now.getMonth().getValue() + 1));
		toMonth.setPreferredSize(dateDim);
		customTimeListener.addDependentField(toMonth);
		
		this.toYear = new CustomTextField("" + now.getYear());
		toYear.setPreferredSize(yearDim);
		customTimeListener.addDependentField(toYear);
		customTimeListener.activateFields(false);
		
		//placement
		//header label
		gbc.insets = new Insets(20, 0, 10, 10);
		insertComponent(headerPane, new JLabel(ImageHandler.loadIcon("/label_icons/clock.png")), 0, 0);
		gbc.insets = new Insets(20, 0, 5, 10);
		insertComponent(headerPane, timeFrameLab, 1, 0);
		
		//radio buttons
		gbc.insets = new Insets(10, 10, 10, 10);
		insertComponent(fixedTimePane, time7, 0, 0);
		insertComponent(fixedTimePane, time14, 1, 0);
		insertComponent(fixedTimePane, time30, 2, 0);
		insertComponent(fixedTimePane, customTime, 3, 0);
		
		//date range
		insertComponent(customTimePane, from, 0, 0);
		gbc.insets = new Insets(10, 5, 10, 5);
		insertComponent(customTimePane, fromDay, 1, 0);
		insertComponent(customTimePane, fromMonth, 2, 0);
		insertComponent(customTimePane, fromYear, 3, 0);
		gbc.insets = new Insets(10, 30, 10, 10);
		insertComponent(customTimePane, to, 4, 0);
		gbc.insets = new Insets(10, 5, 10, 5);
		insertComponent(customTimePane, toDay, 5, 0);
		insertComponent(customTimePane, toMonth, 6, 0);
		insertComponent(customTimePane, toYear, 7, 0);
	}
	
	/**
	 * @param southPane - JPanel to use as container
	 */
	private void buildSouthPanel(JPanel southPane) {
		//table
		this.table = new CustomTable(0, 0);
		table.setBackground(Color.WHITE);
		table.setRowHeight(15);
		table.setFont(VALUE_FONT);
		
		//table renderer for aligning cells
		this.tableRender = new CustomCellRenderer();
        tableRender.setHorizontalAlignment(JLabel.LEFT);
        tableRender.setVerticalAlignment(JLabel.CENTER);
        tableRender.setBorder(null);
        
        //table renderer for the headers
        DefaultTableCellRenderer headerRender = new DefaultTableCellRenderer();
        headerRender.setBackground(new Color(36, 49, 62));
        headerRender.setForeground(Color.WHITE);
        headerRender.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        
        //construct table model
        this.tableModel = new DefaultTableModel(0, 2);
        table.setModel(tableModel);
		table.getColumnModel().getColumn(0).setHeaderValue("Account Name");
        table.getColumnModel().getColumn(1).setHeaderValue("ID");
        table.getColumnModel().getColumn(0).setHeaderRenderer(headerRender);
        table.getColumnModel().getColumn(1).setHeaderRenderer(headerRender);
        table.getColumnModel().getColumn(1).setMinWidth(ID_COL_LENGTH);
		table.getColumnModel().getColumn(1).setMaxWidth(ID_COL_LENGTH);
		table.getColumnModel().getColumn(1).setWidth(ID_COL_LENGTH);
		table.setRowHeight(20);
		
        //force selection of only 1 row
        DefaultListSelectionModel selectionModel = new DefaultListSelectionModel();
        selectionModel.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        table.setSelectionModel(selectionModel);
        table.setDefaultEditor(Object.class, null);
		
		JScrollPane tableScrollPane = new JScrollPane(table);
		tableScrollPane.setPreferredSize(Percentage.createDimension(MainWindow.DIM, 80, 20));
		tableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		tableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		//modifying scroll bar colors
		Color barColor = new Color(36, 49, 62);
		tableScrollPane.setBackground(barColor);
		tableScrollPane.getVerticalScrollBar().setBackground(barColor);
		tableScrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = barColor.brighter().brighter();
            }
        });
		
		//north pane and search button
		JPanel searchPane = new JPanel(new GridBagLayout());
		searchPane.setOpaque(false);
		searchPane.setPreferredSize(Percentage.createDimension(MainWindow.DIM, 100, 10));
		searchPane.setBackground(Color.BLUE);
		
		String defMessage = "  Search account by name or ID ";
		JTextField searchField = new JTextField(defMessage);
		searchField.setHorizontalAlignment(JTextField.CENTER);
		searchField.setPreferredSize(new Dimension(230, 30));
		searchField.setBackground(Color.WHITE);
		searchField.setForeground(Color.GRAY);
		searchField.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals(defMessage)) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (searchField.getText().equals("")) {
                    searchField.setText(defMessage);
                    searchField.setForeground(Color.GRAY);
                }
            }
        });
		
		gbc.insets = new Insets(0, -183, -20, 10);
		insertComponent(searchPane, searchField, 0, 0);
		
		Color labelSelectedColor = new Color(0, 220, 255); 
		Font searchClearFont = FontHandler.load("Raleway", FontStyle.PLAIN, 14);
		
		InteractiveLabel clearLab = new InteractiveLabel("CLEAR");
		clearLab.setHoverColor(labelSelectedColor);
		clearLab.enableSelectionColor(false);
		clearLab.setForeground(Color.WHITE);
		clearLab.setFont(searchClearFont);
		clearLab.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				if (reportRequest.getProvider() != null) {
					rearrangeTable(reportRequest.getProvider(), "");
					reportRequest.setAccount(null);
					
					//remove current text and return focus to the window
					searchField.setText("");
					window.requestFocus();
					
					LogConsole.clear();
				}
				else LogConsole.write("No provider selected.");
				
				return null;
			}
		});
		
		gbc.insets = new Insets(0, 0, -20, 11);
		insertComponent(searchPane, clearLab, 1, 0);
		
		InteractiveLabel searchLab = new InteractiveLabel("SEARCH");
		searchLab.setHoverColor(labelSelectedColor);
		searchLab.enableSelectionColor(false);
		searchLab.setForeground(Color.WHITE);
		searchLab.setFont(searchClearFont);
		searchLab.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				if (reportRequest.getProvider() != null) {
					String keyword = (!searchField.getText().equals(defMessage)) ? searchField.getText() : "";
					rearrangeTable(reportRequest.getProvider(), keyword);
					LogConsole.clear();
				}
				else LogConsole.write("No provider selected.");
				
				return null;
			}
		});
		
		gbc.insets = new Insets(0, 0, -20, -310);
		insertComponent(searchPane, searchLab, 2, 0);
		
		//assist panes from east and west
		Dimension sideAssistDim = Percentage.createDimension(MainWindow.DIM, 10, 30);
		
		JPanel assistEastPane = new JPanel();
		assistEastPane.setOpaque(false);
		assistEastPane.setPreferredSize(sideAssistDim);
		
		JPanel assistWestPane = new JPanel();
		assistWestPane.setOpaque(false);
		assistWestPane.setPreferredSize(sideAssistDim);
		
		//result button panel
		JPanel resultPane = new JPanel(new GridBagLayout());
		resultPane.setOpaque(false);
		resultPane.setPreferredSize(Percentage.createDimension(MainWindow.DIM, 100, 10));
		
		//analyze function
		Callable<Void> analyzeFunc = new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				TimeFrame timeFrame = reportRequest.getTimeFrame();
				
				if (timeFrame == null) {
					LogConsole.write("Must select a valid time frame.");
					return null;
				}
				else if (timeFrame.getType() == TimeFrameType.CUSTOMIZED) {
					DateRangeFormatter legalDate = null;
					
					try {
						int fromD = Integer.parseInt(fromDay.getText());
						int fromM = Integer.parseInt(fromMonth.getText());
						int fromY = Integer.parseInt(fromYear.getText());
						int toD = Integer.parseInt(toDay.getText());
						int toM = Integer.parseInt(toMonth.getText());
						int toY = Integer.parseInt(toYear.getText());
						legalDate = calcCustomTimeFrame(fromD, fromM, fromY, toD, toM, toY);
					}
					catch(NumberFormatException e) { legalDate = null; }
					finally {
						if (legalDate == null) {
							LogConsole.write("Illegal customized date.");
							return null;
						}
						else reportRequest.getTimeFrame().setDateRange(legalDate);
					}
				}
				
				if (table.getSelectedRowCount() > 0) {
					String val = (String) table.getValueAt(table.getSelectedRow(), 1);
					long id = GoogleAccount.getPracticalID(val);
					Account account = reportRequest.getProvider().getAccount(id);
					reportRequest.setAccount(account);
				}
				else {
					LogConsole.write("Must select an account.");
					return null;
				}
				
				if (reportRequest.getProvider() == null) {
					LogConsole.write("Must select a provider.");
					return null;
				}
				else if (reportRequest.getAccount() == null) {
					LogConsole.write("Must select a valid account.");
					return null;
				}
				else if (reportRequest.getCriteria().isEmpty()) {
					LogConsole.write("Must select at least one criterion.");
					return null;
				}
				else if (reportRequest.isReady()) reportRequest.run();
				else LogConsole.write("Something went wrong. Please try again.");
				
				return null;
			}
		};
		
		//logo behind analyze button
		InteractiveIcon albertLogo = new InteractiveIcon(ImageHandler.loadIcon("/label_icons/unselected_albert_logo.png"));
		albertLogo.setSelectedIcon("/label_icons/selected_albert_logo.png");
		albertLogo.setFunction(analyzeFunc);
		
		gbc.insets = new Insets(5, -30, 0, 0);
		insertComponent(resultPane, albertLogo, 0, 0);
		
		//analyze button
		InteractiveLabel analyzeLab = new InteractiveLabel("Analyze");
		analyzeLab.setFont(LABEL_FONT);
		analyzeLab.setHoverColor(labelSelectedColor);
		analyzeLab.enableSelectionColor(false);
		analyzeLab.setForeground(Color.WHITE);
		analyzeLab.setFunction(analyzeFunc);
		
		gbc.insets = new Insets(5, 10, 0, 0);
		insertComponent(resultPane, analyzeLab, 1, 0);
		
		//add components to screen
		southPane.add(searchPane, BorderLayout.NORTH);
		southPane.add(assistEastPane, BorderLayout.EAST);
		southPane.add(assistWestPane, BorderLayout.WEST);
		southPane.add(resultPane, BorderLayout.SOUTH);
		southPane.add(tableScrollPane, BorderLayout.CENTER);
	}
	
	/**
	 * Convert integer values to a DateRangeFormatter object.
	 * Legal values are ranged from 10 years ago to 1 year in the future.
	 * 
	 * @param fromD - Starting day (of the month)
	 * @param fromM - Starting month
	 * @param fromY - Starting year
	 * @param toD - Ending day (of the month)
	 * @param toM - Ending month
	 * @param toY - Ending year
	 * @return a DateRangeFormatter if the values are legal, or null if they're not. 
	 */
	private DateRangeFormatter calcCustomTimeFrame(int fromD, int fromM, int fromY, int toD, int toM, int toY) {
		if (reportRequest.getTimeFrame().getType() != TimeFrameType.CUSTOMIZED) return null;
		
		Range<Integer> correctDayRange = null;
		Range<Integer> days30 = new Range<Integer>(1, 30);
		Range<Integer> days31 = new Range<Integer>(1, 31);
		Range<Integer> days28 = new Range<Integer>(1, 28);
		Range<Integer> days29 = new Range<Integer>(1, 29);
		Range<Integer> months = new Range<Integer>(1, 12);
		
		LocalDate now = LocalDate.now();
		Range<Integer> years = new Range<Integer>(now.getYear() - 10, now.getYear() + 1);
		
		//check start year
		if (!years.intersects(fromY)) return null;
		
		//check start month
		if (!months.intersects(fromM)) return null;
		
		//check start day
		switch(fromM) {
			case 1: correctDayRange = days31; break;
			case 2: {
				//check leap year
				if (fromY % 4 == 0) correctDayRange = days29;
				else correctDayRange = days28;
				break;
			}
			case 3: correctDayRange = days31; break;
			case 4: correctDayRange = days30; break;
			case 5: correctDayRange = days31; break;
			case 6: correctDayRange = days30; break;
			case 7: correctDayRange = days31; break;
			case 8: correctDayRange = days31; break;
			case 9: correctDayRange = days30; break;
			case 10: correctDayRange = days31; break;
			case 11: correctDayRange = days30; break;
			case 12: correctDayRange = days31; break;
		}
		
		if (!correctDayRange.intersects(fromD)) return null;
		
		//check end year
		if (!years.intersects(toY)) return null;
		
		//check end month
		if (!months.intersects(toM)) return null;
		
		//check end day
		switch(toM) {
			case 1: correctDayRange = days31; break;
			case 2: {
				//check leap year
				if (toY % 4 == 0) correctDayRange = days29;
				else correctDayRange = days28;
				break;
			}
			case 3: correctDayRange = days31; break;
			case 4: correctDayRange = days30; break;
			case 5: correctDayRange = days31; break;
			case 6: correctDayRange = days30; break;
			case 7: correctDayRange = days31; break;
			case 8: correctDayRange = days31; break;
			case 9: correctDayRange = days30; break;
			case 10: correctDayRange = days31; break;
			case 11: correctDayRange = days30; break;
			case 12: correctDayRange = days31; break;
		}
		
		if (!correctDayRange.intersects(toD)) return null;
		else {
			LocalDate start = LocalDate.of(fromY, fromM, fromD);
			LocalDate end = LocalDate.of(toY, toM, toD);
			return new DateRangeFormatter(start, end);
		}
	}
	
	/**
	 * Rebuild the table based on a search keyword.
	 * @param provider - The provider to retrieve the accounts from
	 * @param keyword - Search keyword
	 */
	void rearrangeTable(Provider provider, String keyword) {
		if (provider == null) return;
		table.clearSelection();
		
		List<Account> customers = provider.getAccounts();
		selectedCustomers.clear();
		keyword = keyword.toLowerCase();
		
		//choose the correct results based on the keyword
		if (!keyword.equals("")) {
			Account tempCust;
			String id, name;
			for (int i = 0; i < customers.size(); i++) {
				tempCust = customers.get(i);
				id = tempCust.getFormattedID().toLowerCase();
				name = tempCust.getName().toLowerCase();
				if (id.contains(keyword) || name.contains(keyword))
					selectedCustomers.add(tempCust);
			}
		}
		else selectedCustomers.addAll(customers);
		
		tableModel.setRowCount(selectedCustomers.size());
		table.setModel(tableModel);
		
		for (int i = 0; i < selectedCustomers.size(); i++) {
			table.setValueAt(selectedCustomers.get(i).getName(), i, 0);
			table.setValueAt(selectedCustomers.get(i).getFormattedID(), i, 1);
		}
		
		//align table cells
        table.getColumnModel().getColumn(0).setCellRenderer(tableRender);
        table.getColumnModel().getColumn(1).setCellRenderer(tableRender);
	}
	
	@Override
	public void insertPanels() {
		window.insertPanel(panes[0], BorderLayout.CENTER);
	}
	
	/**
	 * @return report request this builder constructs
	 */
	public ReportRequest getReportRequest() { return reportRequest; }
}