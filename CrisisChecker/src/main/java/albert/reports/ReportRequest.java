package albert.reports;
import java.util.ArrayList;
import java.util.List;

import albert.error.ErrorType;
import albert.providers.Account;
import albert.providers.Provider;

/**
 * A tool to create a report of an account's campaign.
 * Use this class to create a custom request for the report
 * and then attach it when creating the report itself.
 *  
 * @author Niv Kor
 */
public class ReportRequest
{
	private List<ErrorType> criteria;
	private Account account;
	private Provider provider;
	private TimeFrame timeFrame;
	
	public ReportRequest() {
		this.criteria = new ArrayList<ErrorType>();
	}
	
	/**
	 * Add a criterion to the report (Cost, Conversions, etc...).
	 * @param name - The criterion's name (as typed in the enum)
	 */
	public void addCriterion(String name) {
		addCriterion(ErrorType.valueOf(name));
	}
	
	/**
	 * Remove a criterion from the report (Cost, Conversions, etc...).
	 * @param name - The criterion's name (as typed in the enum)
	 */
	public void removeCriterion(String name) {
		removeCriterion(ErrorType.valueOf(name));
	}
	
	/**
	 * Add a criterion to the report (Cost, Conversions, etc...).
	 * @param error - The error type to add
	 */
	public void addCriterion(ErrorType error) {
		if (error != null && !criteria.contains(error)) criteria.add(error);
	}
	
	/**
	 * Remove a criterion from the report (Cost, Conversions, etc...).
	 * @param name - The error type to remove
	 */
	public void removeCriterion(ErrorType error) {
		criteria.remove(error);
	}
	
	/**
	 * Check if the request is ready to be attached to a report.
	 * @return true if the request covers the minimal demands.
	 */
	public boolean isReady() {
		return provider != null && account != null && !criteria.isEmpty();
	}
	
	/**
	 * Execute the request and convert it into a report, directly using the provider's class.
	 */
	public void run() { if (isReady()) provider.runReport(this); }
	
	/**
	 * @return the fields that were added to the request.
	 */
	public List<ErrorType> getCriteria() { return criteria; }
	
	/**
	 * @return the provider that this request is dealing with.
	 */
	public Provider getProvider() { return provider; }
	
	/**
	 * @return the account that the report is about.
	 */
	public Account getAccount() { return account; }
	
	/**
	 * @return the time frame where the report focuses.
	 */
	public TimeFrame getTimeFrame() { return timeFrame; }
	
	/**
	 * @param p - The provider of the report's account of interest
	 */
	public void setProvider(Provider p) { provider = p; }
	
	/**
	 * @param a - The account to create a report of
	 */
	public void setAccount(Account a) { account = a; }
	
	/**
	 * @param tf - The time frame to focus on, in the report
	 */
	public void setTimeFrame(TimeFrame tf) { timeFrame = tf; }
}