package albert.utility.files;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import albert.excel_segmantation.Sheet;
import albert.providers.Provider;

public class Library extends FileLoader
{
	private XSSFWorkbook workbook;
	private InputStream inputStream;
	private Provider provider;
	private Sheet sheet;
	private File file;
	
	/**
	 * @param provider - The provider that this library is dealing with
	 */
	public Library(Provider provider) {
		this.provider = provider;
	}
	
	/**
	 * Load a workbook from a file into the library.
	 * @param path - The logical path of the sheet
	 */
	public void loadSheet(String path) {
		file = new File(path);
		
		try {
			inputStream = new FileInputStream(file);
			workbook = new XSSFWorkbook(inputStream);
			sheet = new Sheet(workbook.getSheetAt(0), provider);
			
			Character[] gibberish = {'?'};
			clean(gibberish);
		}
		catch (Exception e) {
			System.err.println("Could not load sheet.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Write to the workbook.
	 * This method needs to be called after all changes to the workbook are done.
	 */
	public void write() {
		if (!isOpen()) {
			System.err.println("Cannot write to the file because it is not properly loaded.");
			return;
		}
		
		try {
			FileOutputStream outputStream = new FileOutputStream(file);
			workbook.write(outputStream);
			outputStream.close();
		} catch (IOException e) {
			System.err.println("Could not write to the file.");
			e.printStackTrace();
		}
	}
	
	/**
	 * @return true if the book contains a downloaded workbook or false otherwise. 
	 */
	public boolean isOpen() {
		return workbook != null;
	}
	
	/**
	 * Clean all gibberish characters from the sheet.
	 * @param gibberishArr - An array of gibberish characters to remove from the sheet. 
	 */
	private void clean(Character[] gibberishArr) {
		XSSFSheet source = sheet.getSource();
		int rowCount = source.getPhysicalNumberOfRows();
		String replaceStr;
		
		for (int i = 0; i < rowCount; i++) {
         	Row row = source.getRow(i);
         	for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
         		Cell cell = row.getCell(j);
         		
         		//replace selected gibberish characters with blank chararcter
         		replaceStr = cell.getStringCellValue();
         		for (Character c : gibberishArr)
         			replaceStr = replaceStr.replace(c.toString(), "");
         		
         		cell.setCellValue(replaceStr);
         	}
         }
		
		write();
	}
	
	/**
	 * @return the report sheet in the workbook that was loaded to this library.
	 */
	public Sheet getSheet() { return sheet; }
	
	/**
	 * @return the provider that this library downloaded its workbook from.
	 */
	public Provider getProvider() { return provider; }
	
	/**
	 * @return the workbook that was loaded to this library.
	 */
	public XSSFWorkbook getWorkbook() { return workbook; }
}