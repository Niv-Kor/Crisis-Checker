package albert.excel_segmantation;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;

import albert.error.detecting.DetectingManager;

public class Campaign
{
	private static class CampaignSorter implements Comparator<Row> 
	{
		private final static String FORMAT = "yyyy-MM-dd";
		
		private DateFormat formatter;
		private Sheet sheet;
		
		/**
		 * @param sheet - The entire sheet of the account
		 */
		public CampaignSorter(Sheet sheet) {
			this.sheet = sheet;
			this.formatter = new SimpleDateFormat(FORMAT);
		}
		
		@Override
		public int compare(Row r1, Row r2) {
			String day1 = sheet.cellValueString(r1, "Day");
			String day2 = sheet.cellValueString(r2, "Day");
			
			try { return formatter.parse(day1).compareTo(formatter.parse(day2)); }
			catch (ParseException e) { throw new IllegalArgumentException(e); }
		}
	}
	
	private List<Row> rows;
	private Sheet sheet;
	private String name;
	private DetectingManager errorDetector;
	
	/**
	 * @param sheet - The entire sheet of the account
	 * @param name - The campaign's exact name
	 */
	public Campaign(Sheet sheet, String name) {
		this.sheet = sheet;
		this.rows = new ArrayList<Row>();
		this.name = new String(name);
		this.errorDetector = new DetectingManager(sheet, this);
		
		for (int i = 0; i < sheet.getSource().getPhysicalNumberOfRows(); i++) {
			XSSFRow row = sheet.getSource().getRow(i);
			if (sheet.cellValueString(row, "Campaign").equals(name))
				rows.add(row);
		}
		
		Collections.sort(rows, new CampaignSorter(sheet));
		ignoreToday();
		groupSimilar();
	}
	
	/**
	 * Print the campaign's data to the console.
	 */
	public void print() {
		System.out.println();
		for (Row r : rows) {
			for (int c = 0; c < r.getPhysicalNumberOfCells(); c++)
				System.out.print(sheet.cellValueString(r, c) + "\t");
			
			System.out.println();
		}
		System.out.println();
	}
	
	/**
	 * Remove today's column (if exists).
	 * Today is usually irrelevant and messes with the average value.
	 * If today is not found in the sheet, do nothing. 
	 * @return true if it was deleted or false otherwise.
	 */
	private boolean ignoreToday() {
		LocalDateTime now = LocalDateTime.now();
		String day;
		
		for (Row r : rows) {
			day = sheet.cellValueString(r, "Day").substring(8, 10);
			if (Integer.parseInt(day) == now.getDayOfMonth()) {
				rows.remove(r);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Group all lines with similar dates.
	 */
	private void groupSimilar() {
		Set<Integer> check = new HashSet<Integer>();
		Queue<Row> pendingRemoval = new LinkedList<Row>();
		
		//iterate over all rows
		for (int i = 0; i < rows.size(); i++) {
			String day = sheet.cellValueString(rows.get(i), "Day");
			//sum all other similar rows and add to current row
			if (!check.contains(day.hashCode())) {
				//iterate over all other rows
				for (int j = i + 1; j < rows.size(); j++) {
					//check if rows are similar
					if (sheet.cellValueString(rows.get(j), "Day").equalsIgnoreCase(day)) {
						//iterate over every column
						for (int c = 2; c < rows.get(i).getPhysicalNumberOfCells(); c++) {
							double val1 = sheet.cellValueDouble(rows.get(i), c);
							double val2 = sheet.cellValueDouble(rows.get(j), c);
							rows.get(i).getCell(c).setCellValue(val1 + val2);
						}
					}
				}
				check.add(day.hashCode());
			}
			//row should be removed
			else pendingRemoval.add(rows.get(i));
		}
		
		//remove all unnecessary rows
		while (!pendingRemoval.isEmpty())
			rows.remove(pendingRemoval.poll());
	}
	
	/**
	 * @return a list of the campaign's rows.
	 */
	public List<Row> getRows() { return rows; }
	
	/**
	 * @return the name of the campaign.
	 */
	public String getName() { return name; }
	
	/**
	 * @return the campaign's error detector.
	 */
	public DetectingManager getErrorDetector() { return errorDetector; }
}