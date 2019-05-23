package albert.error;
import albert.error.detecting.CPADetector;
import albert.error.detecting.CPCDetector;
import albert.error.detecting.CPMDetector;
import albert.error.detecting.ConversionsDetector;
import albert.error.detecting.CostDetector;
import albert.error.detecting.ErrorDetector;
import albert.error.detecting.ROASDetector;
import albert.excel_segmantation.Campaign;
import albert.excel_segmantation.Sheet;

public enum ErrorType
{
	COST(Pronoun.IS, Format.MONEY, Crisis.TOO_LOW, CostDetector.class),
	CONVERSIONS(Pronoun.ARE, Format.UNITS, Crisis.TOO_LOW, ConversionsDetector.class),
	CPA(Pronoun.IS, Format.MONEY, Crisis.TOO_HIGH, CPADetector.class),
	ROAS(Pronoun.IS, Format.MONEY, Crisis.TOO_LOW, ROASDetector.class),
	CPC(Pronoun.IS, Format.MONEY, Crisis.TOO_HIGH, CPCDetector.class),
	CPM(Pronoun.IS, Format.MONEY, Crisis.TOO_HIGH, CPMDetector.class);
	
	public static enum Crisis { TOO_LOW, TOO_HIGH, BOTH; }
	private static enum Pronoun { IS, ARE; }
	private static enum Format { MONEY, UNITS; }
	
	private String pronoun;
	private Crisis crisis;
	private Format format;
	private Class<? extends ErrorDetector> detectorClass;
	
	private ErrorType(Pronoun pronoun, Format format, Crisis crisis, Class<? extends ErrorDetector> detector) {
		this.pronoun = pronoun.name().toLowerCase();
		this.crisis = crisis;
		this.format = format;
		this.detectorClass = detector;
	}
	
	/**
	 * Format the value according to its unit type
	 * @param value - The value to format
	 * @return new value with the applied format
	 */
	public double format(double value) {
		switch(format) {
			case MONEY: return value / 1_000_000;
			default: return value;
		}
	}
	
	/**
	 * Create an error detector, modified for the specific type of error.
	 * @param sheet - The .xlsx sheet to detect errors in
	 * @param campaign - The campaign to detect errors of
	 * @return an optimized ErrorDetector object.
	 * @throws Exception when the object cannot be created for some reason.
	 */
	public ErrorDetector createDetector(Sheet sheet, Campaign campaign) throws Exception {
		return detectorClass.asSubclass(ErrorDetector.class).
			   getConstructor(Sheet.class, Campaign.class).
		   	   newInstance(sheet, campaign);
	}
	
	/**
	 * Get the pronoun of the error (is/are)
	 * @return correct pronoun word
	 */
	public String pronoun() { return pronoun; }
	
	/**
	 * Get whatever is considered a crisis for this type of error
	 * @return crisis type of this error
	 */
	public Crisis getCrisisModel() { return crisis; }
}