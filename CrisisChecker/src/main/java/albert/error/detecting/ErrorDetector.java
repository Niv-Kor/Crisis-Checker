package albert.error.detecting;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.poi.ss.usermodel.Row;

import albert.error.ErrorType;
import albert.error.ErrorType.Crisis;
import albert.excel_segmantation.Campaign;
import albert.excel_segmantation.Sheet;
import albert.utility.math.NumeralHandler;
import albert.utility.math.Percentage;
import albert.utility.math.Range;

/**
 * This class detects all errors in a single campaign sheet.
 * It has a derived class for every error type, that does it the best way possible.
 * @author Niv Kor
 */
public abstract class ErrorDetector
{
	/**
	 * Used to determine wether a value is lower or higher than the comparison parameter.
	 * @author Niv Kor
	 */
	public static enum Deviation {
		LOWER, HIGHER, NONE;
	}
	
	protected Campaign campaign;
	protected Sheet sheet;
	protected Queue<String> errorStream;
	protected Range<Double> valuesRange, valuesModel;
	protected ErrorType error;
	
	/**
	 * @param sheet - The whole report sheet
	 * @param campaign - Only the campaign to detect errors in
	 */
	public ErrorDetector(Sheet sheet, Campaign campaign) {
		this.sheet = sheet;
		this.campaign = campaign;
		this.errorStream = new LinkedList<String>();
	}
	
	/**
	 * Find errors in the campaign data and write them down at the error stream.
	 */
	public void detect() {
		double source;
		
		for (Row row : campaign.getRows()) {
			//original error
			source = NumeralHandler.round(source(row), 2);
			double value = value(row);
			Deviation devType = getDeviation(value, source);
			
			if (devType != Deviation.NONE) {
				//add an error
				String date = FormatDate(sheet.cellValueString(row, "Day"));
				String stream = date + "\t" + error.name() + " " + error.pronoun() + " "
								+ devType.name().toLowerCase() + " than expected ("
								+ value + " expecting ~" + source + ")";
				
				errorStream.add(stream);
			}
		}
		
		if (errorStream.size() > 0) errorStream.add("\n");
	}
	
	/**
	 * Calculate the deviation of a value, compared to the source.
	 * If the value is a bit off than the allowed deviation, it's considered an error.
	 * @param value - The value we want to compare
	 * @param source - The value to compare with
	 * @return a Deviation constant that describes the error.
	 */
	private Deviation getDeviation(double value, double source) {
		double siginificanceOfSource = Range.significanceOf(source, valuesRange, valuesModel);
		double deviation = Percentage.percentOfNum(siginificanceOfSource, source);
		double low = source - deviation;
		double high = source + deviation;
		Deviation devType;
		Crisis crisisModel = error.getCrisisModel();
		boolean lowCrisis = crisisModel == Crisis.TOO_LOW || crisisModel == Crisis.BOTH;
		boolean highCrisis = crisisModel == Crisis.TOO_HIGH || crisisModel == Crisis.BOTH;
		
		if (value < low && lowCrisis) devType = Deviation.LOWER;
		else if (value > high && highCrisis) devType = Deviation.HIGHER;
		else devType = Deviation.NONE;
		
		return devType;
	}
	
	/**
	 * Calculate the entire column's average value
	 * @return column's average value.
	 */
	protected double average() {
		double avg = 0;
		
		for (Row row : campaign.getRows())
			avg += value(row);
		
		return avg / campaign.getRows().size();
	}
	
	/**
	 * Format a date from: "yyyy-mm-dd" to: "dd/mm".
	 * @param oldFormat - A string of the date to format
	 * @return a formatted date.
	 */
	protected String FormatDate(String oldFormat) {
		try {
			String day = oldFormat.substring(8, 10);
			String month = oldFormat.substring(5, 7);
			return day + "/" + month;
		}
		catch(StringIndexOutOfBoundsException e) {
			System.err.println("Could not format " + oldFormat);
			return oldFormat;
		}
	}
	
	/**
	 * @param row - The row that containts the value
	 * @return the value in the specific column that's related to the error type.
	 */
	protected double value(Row row) {
		int cellIndex = sheet.getColumnIndex(sheet.getProvider().columnName(error));
		return NumeralHandler.round(error.format(sheet.cellValueDouble(row, cellIndex)), 2);
	}
	
	/**
	 * @return a queue of errors, fed with the detect() method.
	 */
	public Queue<String> getErrorStream() { return errorStream; }
	
	/**
	 * Get the value to compare data with, to see if the data is out of range.
	 * @param row - The row that containts the value (not always needed, depands on the detector)
	 * @return the source value to compare with.
	 */
	protected abstract double source(Row row);
	
	/**
	 * @return the error type that this detector detects.	
	 */
	public ErrorType getError() { return error; }
}