package albert.providers.google_ads;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.threeten.bp.LocalDateTime;

import com.google.api.ads.adwords.axis.factory.AdWordsServices;
import com.google.api.ads.adwords.axis.utils.v201809.SelectorBuilder;
import com.google.api.ads.adwords.axis.v201809.mcm.ManagedCustomer;
import com.google.api.ads.adwords.axis.v201809.mcm.ManagedCustomerPage;
import com.google.api.ads.adwords.axis.v201809.mcm.ManagedCustomerServiceInterface;
import com.google.api.ads.adwords.lib.client.AdWordsSession;
import com.google.api.ads.adwords.lib.client.reporting.ReportingConfiguration;
import com.google.api.ads.adwords.lib.jaxb.v201809.DateRange;
import com.google.api.ads.adwords.lib.jaxb.v201809.DownloadFormat;
import com.google.api.ads.adwords.lib.jaxb.v201809.ReportDefinition;
import com.google.api.ads.adwords.lib.jaxb.v201809.ReportDefinitionDateRangeType;
import com.google.api.ads.adwords.lib.jaxb.v201809.ReportDefinitionReportType;
import com.google.api.ads.adwords.lib.jaxb.v201809.Selector;
import com.google.api.ads.adwords.lib.selectorfields.v201809.cm.ManagedCustomerField;
import com.google.api.ads.adwords.lib.utils.ReportDownloadResponse;
import com.google.api.ads.adwords.lib.utils.v201809.ReportDownloaderInterface;
import com.google.api.ads.common.lib.auth.OfflineCredentials;
import com.google.api.ads.common.lib.auth.OfflineCredentials.Api;
import com.google.api.client.auth.oauth2.Credential;

import albert.database.SQLModifier;
import albert.error.ErrorType;
import albert.providers.Account;
import albert.providers.Provider;
import albert.reports.ReportRequest;
import albert.reports.TimeFrame.DateRangeFormatter;
import albert.user_interface.LogConsole;
import albert.user_interface.ResultLogWindow;
import albert.utility.files.Library;
import albert.utility.files.Document;
import albert.utility.files.FileConverter;

public class GoogleAds extends Provider
{
	private final static String PROVIDER_ID = "7237934112";
	private final static String TABLE_NAME = "google_customers";
	
	private AdWordsSession connection;
	private Library library;
	private Timer timer;
	private TimerTask task;
	
	public GoogleAds() {
		super(PROVIDER_ID);
		this.connection = (AdWordsSession) client;
		this.library = new Library(this);
		this.timer = new Timer();
		this.idLength = 12;
		this.headerRow = 1;
		this.dataRow = 2;
		
		//retrieve customers from DB
		List<String> custID = SQLModifier.readAllVARCHAR(getQuery(), "id");
		List<String> custName = SQLModifier.readAllVARCHAR(getQuery(), "name");
		
		for (int i = 0; i < custID.size(); i++)
			accounts.add(new GoogleAccount(Long.parseLong(custID.get(i)), custName.get(i)));
	}
	
	@Override
	protected void connect(long id) {
		try {
			File propertiesFile = new File("ads.properties.txt");
			Credential oAuth2Credential = new OfflineCredentials.Builder().forApi(Api.ADWORDS).fromFile(propertiesFile).build().generateCredential();
			client = new AdWordsSession.Builder().fromFile(propertiesFile).withOAuth2Credential(oAuth2Credential).build();
		}
		catch (Exception e) {
			System.err.println("Could not connect to Google Ads API service.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Update all the accounts from the Google Ads API.
	 * The new accounts are saved to the DB.
	 */
	public static void updateAccounts() {
		//form a temporary connection
		File propertiesFile = new File("ads.properties.txt");
		AdWordsSession connection = null;
		
		try {
			Credential oAuth2Credential = new OfflineCredentials.Builder().forApi(Api.ADWORDS).fromFile(propertiesFile).build().generateCredential();
			connection = (AdWordsSession) new AdWordsSession.Builder().fromFile(propertiesFile).withOAuth2Credential(oAuth2Credential).build();
		}
		catch (Exception e) {
			System.err.println("Could not temporarily connect to Google Ads API service.");
			e.printStackTrace();
		}
		
		//get all accounts under albert's manager account
		ManagedCustomerPage page;
		int pageSize = 100, offset = 0;
		List<String> knownAccounts = SQLModifier.readAllVARCHAR("SELECT id FROM " + TABLE_NAME, "id");
		List<Account> newAccounts = new ArrayList<Account>();
		ManagedCustomerServiceInterface service = AdWordsServices.getInstance().get(connection, ManagedCustomerServiceInterface.class);
	    SelectorBuilder builder = new SelectorBuilder().fields(ManagedCustomerField.CustomerId, ManagedCustomerField.Name);
	    builder.orderAscBy(ManagedCustomerField.Name).offset(offset).limit(pageSize);
	    
	    try {
	    	do {
	    		page = service.get(builder.build());
    			for (ManagedCustomer customer : page.getEntries()) {
    				if (!knownAccounts.contains(Long.toString(customer.getCustomerId()))) {
	    				newAccounts.add(new GoogleAccount(customer.getCustomerId(), customer.getName()));
    				}
    			}
    			
    			offset += pageSize;
    			builder.increaseOffsetBy(pageSize);
	    	}
	    	while (offset < page.getTotalNumEntries());
	    }
	    catch(Exception e) { e.printStackTrace(); }
	    
	    System.out.println("FOUND " + newAccounts.size() + " NEW ACCOUNTS.");
	    
	    //insert new accounts found to DB
		String query;
		int done = 0;
		for (Account customer : newAccounts) {
			query = "INSERT INTO " + TABLE_NAME + "(id, name) VALUES('" + customer.getPracticalID() + "', '" + customer.getName() + "')";
			SQLModifier.write(query);
			done++;
	    }
		
		System.out.println("SUCCESSFULY ADDED " + done + " ACCOUNTS.");
	}
	
	@Override
	public void runReport(ReportRequest request) {
		LogConsole.write("Downloading data...");
		
	    task = new TimerTask() {
			public void run() {
				//save my ID and set the customer's ID
				String myID = connection.getClientCustomerId();
				connection.setClientCustomerId(request.getAccount().getFormattedID());
				
				Selector selector = new Selector();
				
				boolean hasROAS = false; //determine if ROAS is needed
				List<String> list = new ArrayList<String>();
				list.add("Date");
				list.add("CampaignName");
				list.add("Amount");
				
				for (ErrorType error : request.getCriteria()) {
					if (error != ErrorType.ROAS) list.add(errorName(error));
					else hasROAS = true;
				}
				
				selector.getFields().addAll(list);
				
				ReportDefinitionDateRangeType timeFrame = null;
				switch(request.getTimeFrame().getType()) {
					case DAYS_7: timeFrame = ReportDefinitionDateRangeType.LAST_7_DAYS; break;
					case DAYS_14: timeFrame = ReportDefinitionDateRangeType.LAST_14_DAYS; break;
					case DAYS_30: timeFrame = ReportDefinitionDateRangeType.LAST_30_DAYS; break;
					case CUSTOMIZED: {
						timeFrame = ReportDefinitionDateRangeType.CUSTOM_DATE;
						DateRangeFormatter rangeFormatter = request.getTimeFrame().getDateRange();
						DateRange range = new DateRange();
						range.setMin(rangeFormatter.getStart());
						range.setMax(rangeFormatter.getEnd());
						selector.setDateRange(range);
						break;
					}
					default: break;
				}
				
			    ReportDefinition reportDefinition = new ReportDefinition();
			    reportDefinition.setReportName("Criteria performance report #" + System.currentTimeMillis());
			    reportDefinition.setDateRangeType(timeFrame);
			    reportDefinition.setReportType(ReportDefinitionReportType.CAMPAIGN_PERFORMANCE_REPORT);
			    reportDefinition.setDownloadFormat(DownloadFormat.CSVFOREXCEL);

			    ReportingConfiguration reportingConfiguration =
			        new ReportingConfiguration.Builder()
			            .skipReportHeader(false)
			            .skipColumnHeader(false)
			            .skipReportSummary(true)
			            .includeZeroImpressions(false)
			            .build();
			    
			    connection.setReportingConfiguration(reportingConfiguration);
			    reportDefinition.setSelector(selector);
			    ReportDownloaderInterface reportDownloader = AdWordsServices.getInstance().getUtility(connection, ReportDownloaderInterface.class);
				
				try {
					//create files
					ReportDownloadResponse response = reportDownloader.downloadReport(reportDefinition);
					String reportFile = "temp report " + request.getAccount().getName() + " " + LocalDateTime.now();
					File csvFile = Document.CSV.createFile(reportFile);																																																																														
					File xlsxFile = Document.XLSX.createFile(reportFile);
				    response.saveToFile(csvFile.getPath());
				    library.loadSheet(FileConverter.convert(reportFile, Document.CSV, Document.XLSX));
				    
				    //modify
				    if (hasROAS) calcROAS();																																																																																																															
				    deleteLastRow();

				    //analyze	
				    ResultLogWindow resultLog = new ResultLogWindow(library, request);																																																							
				    resultLog.run(true);
				    
				    //delete files
				    csvFile.delete();
				    xlsxFile.delete();
				}
				catch (Exception e) { e.printStackTrace(); }
				
				//restore my ID in the connection
				connection.setClientCustomerId(myID);
			}
	    };
	    
		timer.schedule(task, 1000);
	}
	
	/**
	 * Add the ROAS column manually (not as automatically as other columns).
	 * This is because Google Ads API does not offer a ROAS column and we
	 * have to calculate it with the Conversions column divided by the Cost column.
	 */
	private void calcROAS() {
		XSSFSheet source = library.getSheet().getSource();
		int rowCount = source.getPhysicalNumberOfRows();
		int freeCol = source.getRow(getHeaderRow()).getPhysicalNumberOfCells();
		
		//create ROAS header
		source.getRow(getHeaderRow()).createCell(freeCol).setCellValue("ROAS");
		
		Row row;
		Cell cell;
		double roasVal, costVal, convVal;;
		for (int i = getHeaderRow() + 1; i < rowCount; i++) {
			row = source.getRow(i);
			row.createCell(freeCol);
			cell = row.getCell(freeCol);
			
			//calculate roas value
			try { costVal = ErrorType.COST.format(library.getSheet().cellValueDouble(row, "Cost")); }
			catch(Exception e) { costVal = 0; }
			
			try { convVal = ErrorType.CONVERSIONS.format(library.getSheet().cellValueDouble(row, "Conversions")); }
			catch(Exception e) { convVal = 0; }
			
			if (costVal == 0 || convVal == 0) roasVal = 0;
			else roasVal = convVal / costVal;
			
			//set roas value
			cell.setCellValue(roasVal);
		}
		
		library.write();
	}
	
	/**
	 * Delete the last row in the report, That is, the row or today.
	 * Nn general, the last row is yet to be updated and should not be taken into consideration.
	 * Most of the time the last row would have been recognized as an error if we let it in the report. 
	 */
	private void deleteLastRow() {
		XSSFSheet source = library.getSheet().getSource();
		Row lastRow = source.getRow(source.getPhysicalNumberOfRows() - 1);
		source.removeRow(lastRow);
		library.write();
	}
	
	@Override
	public String errorName(ErrorType error) {
		switch(error) {
			case COST: return "Cost";
			case CONVERSIONS: return "Conversions";
			case CPA: return "CostPerConversion";
			case CPC: return "AverageCpc";
			case CPM: return "AverageCpm";
			case ROAS: return "ROAS";
			default: {
				System.err.println("Unrecognized error type");
				return "";
			}
		}
	}
	
	@Override
	public String columnName(ErrorType error) {
		switch(error) {
			case COST: return "Cost";
			case CONVERSIONS: return "Conversions";
			case CPA: return "Cost / conv.";
			case CPC: return "Avg. CPC";
			case CPM: return "Avg. CPM";
			case ROAS: return "ROAS";
			default: {
				System.err.println("Unrecognized error column");
				return "";
			}
		}
	}
	
	@Override
	public albert.providers.Account getAccount(long id) {
		for (albert.providers.Account c : accounts)
			if (c.getPracticalID() == id) return c;
		
		return null;
	}
	
	@Override
	public String getQuery() { return "SELECT * FROM " + TABLE_NAME + " ORDER BY name ASC"; }
}