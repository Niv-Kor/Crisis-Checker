package albert.excel_segmantation;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import albert.providers.Provider;

/**
 * An excel sheet with the downloaded report.
 * Based on an original XSSFSheet object, but built to add more functionality to it.
 * @author Niv Kor
 */
public class Sheet {
	private DataFormatter formatter;
	private XSSFSheet xssfsheet;
	private Provider provider;
	
	/**
	 * @param xssf - The original XSSFSheet object to work on
	 * @param provider - The account's provider
	 */
	public Sheet(XSSFSheet xssf, Provider provider) {
		this.xssfsheet = xssf;
		this.formatter = new DataFormatter();
		this.provider = provider;
	}
	
	/**
	 * Find column's index by giving the column's name.
	 * @param col - The column's name (not case-sensitive)
	 * @return column index if it was found, or -1 if it wasn't found.
	 */
	public int getColumnIndex(String col) {
		Row row = xssfsheet.getRow(provider.getHeaderRow());
		
		for (int i = 0; i < row.getPhysicalNumberOfCells(); i++)
			if (cellValueString(row, i).equalsIgnoreCase(col)) return i;
		
		System.out.println("Could not find the column " + col);
		return -1;
	}
	
	/**
	 * Get the String value of a cell.
	 * @param row - The row of the cell (Row object)
	 * @param column - Column index
	 * @return cell's string value.
	 */
	public String cellValueString(Row row, int column) {
		return formatter.formatCellValue(row.getCell(column));
	}
	
	/**
	 * Get the String value of a cell.
	 * @param row - The row of the cell (Row object)
	 * @param column - Column name
	 * @return cell's string value.
	 */
	public String cellValueString(Row row, String column) {
		return formatter.formatCellValue(row.getCell(getColumnIndex(column)));
	}
	
	/**
	 * Get the double value of a cell.
	 * @param row - The row of the cell (Row object)
	 * @param column - Column index
	 * @return cell's double value.
	 */
	public Double cellValueDouble(Row row, int column) {
		return Double.parseDouble(cellValueString(row, column));
	}
	
	/**
	 * Get the double value of a cell.
	 * @param row - The row of the cell (Row object)
	 * @param column - Column name
	 * @return cell's double value.
	 */
	public Double cellValueDouble(Row row, String column) {
		return Double.parseDouble(cellValueString(row, column));
	}
	
	/**
	 * Split the sheet into a list of campaigns,
	 * while each list holds a seperate campaign.
	 * @return list of seperated campaigns.
	 */
	public List<Campaign> segmantCampaigns() {
		List<Campaign> campList = new ArrayList<Campaign>();
		List<List<Row>> lists = new ArrayList<List<Row>>();
		
		//group by campaigns and fix each one into a list
		outerloop:
		for (int i = provider.getDataRow(); i < xssfsheet.getPhysicalNumberOfRows(); i++) {
			Row row = xssfsheet.getRow(i);
			String campaignName = cellValueString(row, "Campaign");
			//if (!campaignName.contains("Albert")) continue;
			
			//search for a compatible row and insert
			for (List<Row> sublist : lists) {
				if (cellValueString(sublist.get(0), "Campaign").equalsIgnoreCase(campaignName)) {
					sublist.add(row);
					continue outerloop;
				}
			}
			//didn't find a compatible row
			List<Row> newList = new ArrayList<Row>();
			newList.add(row);
			lists.add(newList);
		}
		
		//create campaigns
		for (List<Row> sublist : lists) {
			String name = cellValueString(sublist.get(0), "Campaign");
			campList.add(new Campaign(this, name));
		}
		
		return campList;
	}
	
	/**
	 * Get the source xssf sheet of this Sheet object.
	 * The source object is of XSSFSheet type and has more operative functionality.
	 * @return XSSFSheet object that this object is built upon.
	 */
	public XSSFSheet getSource() { return xssfsheet; }
	
	/**
	 * @return the provider of the sheet (Google Ads, Facebook Business, etc...).
	 */
	public Provider getProvider() { return provider; }
}