package albert.error.detecting;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import albert.error.ErrorType;
import albert.excel_segmantation.Campaign;
import albert.excel_segmantation.Sheet;
import albert.reports.ReportRequest;

/**
 * Manages all error detectors.
 * Easy to use by only giving it the error types needed.
 * @author Niv Kor
 */
public class DetectingManager
{
	private Campaign campaign;
	private Sheet sheet;
	private Queue<String> errorStream;
	
	/**
	 * @param sheet - The whole report sheet
	 * @param campaign - Only the campaign to detect errors in
	 */
	public DetectingManager(Sheet sheet, Campaign campaign) {
		this.errorStream = new LinkedList<String>();
		this.campaign = campaign;
		this.sheet = sheet;
	}
	
	/**
	 * Detect all errors available in the enum ErrorType.
	 */
	public void detectAll() {
		detect(Arrays.asList(ErrorType.values()));
	}
	
	/**
	 * Detect all errors written in a single report request.
	 * @param request
	 */
	public void detect(ReportRequest request) {
		detect(request.getCriteria());
	}
	
	/**
	 * Detect errors of the given types in a list.
	 * @param list - List of error types to detect
	 */
	private void detect(List<ErrorType> list) {
		ErrorDetector detector;
		Queue<String> detectorStream;
		
		for (ErrorType error : list) {
			try{
				detector = error.createDetector(sheet, campaign);
				detector.detect();
				detectorStream = detector.getErrorStream();
				
				while(!detectorStream.isEmpty())
					errorStream.add(detectorStream.poll());
			}
			catch(Exception e) {
				System.err.println("Could not create a detector for " + error.name() + ".");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Print an error report to the console (mostly for debugging purposes).
	 */
	public void printStream() {
		System.out.println(campaign.getName());
		System.out.println("--------------------------------------");
		for (String s : errorStream) System.out.println(s);
		System.out.println();
	}
	
	/**
	 * @return a stream of all errors detected, in all types.
	 */
	public Queue<String> getStream() { return errorStream; }
}