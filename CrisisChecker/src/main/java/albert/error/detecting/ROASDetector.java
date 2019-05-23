package albert.error.detecting;
import org.apache.poi.ss.usermodel.Row;
import albert.error.ErrorType;
import albert.excel_segmantation.Campaign;
import albert.excel_segmantation.Sheet;
import albert.utility.math.Range;

public class ROASDetector extends ErrorDetector
{
	private static final Range<Double> VALUES_RANGE = new Range<Double>(0.0, 100.0);
	private static final Range<Double> VALUES_MODEL = new Range<Double>(500.0, 50.0);
	
	public ROASDetector(Sheet sheet, Campaign campaign) {
		super(sheet, campaign);
		this.error = ErrorType.ROAS;
		this.valuesRange = VALUES_RANGE;
		this.valuesModel = VALUES_MODEL;
	}

	@Override
	protected double source(Row row) { return average(); }
}