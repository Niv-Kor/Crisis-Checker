package albert.utility.files;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.opencsv.CSVReader;
 
public class FileConverter
{
    private static final char FILE_DELIMITER = '\t';
    
    private static Logger logger = Logger.getLogger(FileConverter.class);
    
    /**
     * Convert a file of type A to a file of type B.
     * @param fileName - The file's name
     * @param from - Type of the file
     * @param to - Type to convert to
     * @return a path to the converted file.
     */
    public static String convert(String fileName, Document from, Document to) {
    	switch(from) {
	    	case CSV:
	    		switch(to) {
	    			case XLSX: return CSVtoXLSX(fileName);
	    			default: break;
	    		}
			default: break;
    	}
    	
    	System.err.println("Could not convert " + fileName + " from " + from + " to " + to);
		return fileName;
    }
    
    /**
     * Convert a CSV file to an XLSX file.
     * @param fileName - The file's name
     * @return a path to the converted file.
     */
    private static String CSVtoXLSX(String fileName) {
    	String filePath = fileName + ".csv";
    	
        XSSFSheet sheet = null;
        CSVReader reader = null;
        Workbook workBook = null;
        String generatedXlsFilePath = "";
        FileOutputStream fileOutputStream = null;
 
        try {
            /**** Get the CSVReader Instance & Specify The Delimiter To Be Used ****/
            String[] nextLine;
            reader = new CSVReader(new FileReader(filePath), FILE_DELIMITER);
 
            workBook = new XSSFWorkbook();
            sheet = (XSSFSheet) workBook.createSheet("Sheet");
 
            int rowNum = 0;
            logger.info("Creating New .Xls File From The Already Generated .Csv File");
            while((nextLine = reader.readNext()) != null) {
                Row currentRow = sheet.createRow(rowNum++);
                for(int i = 0; i < nextLine.length; i++) {
                	Cell cell = currentRow.createCell(i);
                    cell.setCellValue(nextLine[i]);
                }
            }
            
            generatedXlsFilePath = fileName + ".xlsx";
            logger.info("The File Is Generated At The Following Location?= " + generatedXlsFilePath);
 
            fileOutputStream = new FileOutputStream(generatedXlsFilePath.trim());
            workBook.write(fileOutputStream);
        }
        catch(Exception exObj) {
        	logger.error("Exception In convertCsvToXls() Method?=  " + exObj);
        }
        finally {
            try {
                fileOutputStream.close();
                reader.close();
            }
            catch (IOException ioExObj) {
            	logger.error("Exception While Closing I/O Objects In convertCsvToXls() Method?=  " + ioExObj);          
            }
        }
        
        return generatedXlsFilePath;
    }
}