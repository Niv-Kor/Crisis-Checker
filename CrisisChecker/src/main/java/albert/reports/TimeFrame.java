package albert.reports;
import java.time.LocalDate;

public class TimeFrame
{
	/**
	 * A tool to format LocalDate objects in "YYYYMMDD" format.
	 * It's needed when the provider won't accept a different format for time frames.
	 * 
	 * @author Niv Kor
	 */
	public static class DateRangeFormatter
	{
		private String start, end;
		
		/**
		 * @param start - The beginning date (only down to the resolution of days)
		 * @param end - The ending date (only down to the resolution of days)
		 */
		public DateRangeFormatter(LocalDate start, LocalDate end) {
			setStart(start);
			setEnd(end);
		}
		
		/**
		 * Convert a LocalDate object's date to the format of "YYYYMMDD".
		 * @param date - The LocalDate object to format
		 * @return a formatted String.
		 */
		public static String formatDate(LocalDate date) {
			String str = "";
			String excess;
			
			str = str.concat("" + date.getYear());
			
			excess = (date.getMonthValue() < 10) ? "0" : "";
			str = str.concat(excess + date.getMonthValue());
			
			excess = (date.getDayOfMonth() < 10) ? "0" : "";
			str = str.concat(excess + date.getDayOfMonth());

			return str;
		}
		
		/**
		 * @param s - The beginning date (only down to the resolution of days)
		 */
		public void setStart(LocalDate s) { start = formatDate(s); }
		
		/**
		 * @param s - The ending date (only down to the resolution of days)
		 */
		public void setEnd(LocalDate e) { end = formatDate(e); }
		
		/**
		 * @return a formatted start time.
		 */
		public String getStart() { return start; }
		
		/**
		 * @return a formatted end time.
		 */
		public String getEnd() { return end; }
	}
	
	/**
	 * A set of time frames to use when selecting a time frame for the report.
	 * A customized time frame will force the use of a start date and an end date.
	 * 
	 * @author Niv Kor
	 */
	public enum TimeFrameType {
		DAYS_7,
		DAYS_14,
		DAYS_30,
		CUSTOMIZED;
	}
	
	private TimeFrameType type;
	private DateRangeFormatter dateRange;
	
	/**
	 * @param type - The time frame type to use
	 */
	public TimeFrame(TimeFrameType type) {
		this.type = type;
	}
	
	/**
	 * @param start - The new start time
	 * @param end - The new end time
	 */
	public void setDateRange(LocalDate start, LocalDate end) { dateRange = new DateRangeFormatter(start, end); }
	
	/**
	 * @param range - The new range (an object with 'start' and 'end')
	 */
	public void setDateRange(DateRangeFormatter range) { dateRange = range; }
	
	/**
	 * @return the date range used.
	 */
	public DateRangeFormatter getDateRange() { return dateRange; }
	
	/**
	 * @return the type of the time frame.
	 */
	public TimeFrameType getType() { return type; }
}