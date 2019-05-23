package albert.error.detecting;
import org.apache.poi.ss.usermodel.Row;

import albert.error.ErrorType;
import albert.excel_segmantation.Campaign;
import albert.excel_segmantation.Sheet;
import albert.utility.math.Range;

public class CPMDetector extends ErrorDetector {
	private static final Range<Double> VALUES_RANGE = new Range<Double>(0.0, 100.0);
	private static final Range<Double> VALUES_MODEL = new Range<Double>(500.0, 100.0);
	
	public CPMDetector(Sheet sheet, Campaign campaign) {
		super(sheet, campaign);
		this.error = ErrorType.CPM;
		this.valuesRange = VALUES_RANGE;
		this.valuesModel = VALUES_MODEL;
	}
	
	@Override
	protected double average() {
		double avg = 0;
		int size = 0;
		
		for (Row row : campaign.getRows()) {
			double value = value(row);
			
			//values that are 0 don't count in average
			if (value != 0) {
				avg += value;
				size++;
			}
		}
		
		//careful not to divide by zero
		return (size != 0) ? avg / size : 0;
	}
	
	@Override
	protected double source(Row row) {
		double average = average();
		return (average != 0) ? average : value(row);
	}
}