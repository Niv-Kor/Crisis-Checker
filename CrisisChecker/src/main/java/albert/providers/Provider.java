package albert.providers;
import java.util.ArrayList;
import java.util.List;

import albert.error.ErrorType;
import albert.reports.ReportRequest;

public abstract class Provider
{
	protected long id;
	protected Object client;
	protected List<Account> accounts;
	protected int idLength, headerRow, dataRow;
	
	public Provider(String id) {
		this.id = Long.parseLong(id);
		this.accounts = new ArrayList<Account>();
		connect(this.id);
	}
	
	/**
	 * @param id - The ID of the account, as recognized by the provider
	 * @return Account object of the account that the ID belongs to.
	 */
	public abstract Account getAccount(long id);
	
	/**
	 * @return the query to call in order to get all the accounts from the DB.
	 */
	public abstract String getQuery();
	
	/**
	 * Perform a connection to the provider API.
	 * @param id - Albert's manager ID in the API.
	 */
	protected abstract void connect(long id);
	
	/**
	 * Run a report based on the report request.
	 * @param request - The request to run the report by
	 */
	public abstract void runReport(ReportRequest request);
	
	/**
	 * @return list of accounts, connected to this provider.
	 */
	public List<Account> getAccounts() { return accounts; }
	
	/**
	 * Get the name of an error as recognized by the provider.
	 * @param error - The error type
	 * @return error name as recognized by the provider.
	 */
	public abstract String errorName(ErrorType error);
	
	/**
	 * Get the column name of an error in the report as recognized by the provider.
	 * @param error - The error type
	 * @return error column name as recognized by the provider.
	 */
	public abstract String columnName(ErrorType error);
	
	/**
	 * @return length of the ID.
	 */
	public int getIDLength() { return idLength; }
	
	/**
	 * @return index of the headers row in the report.
	 */
	public int getHeaderRow() { return headerRow; }
	
	/**
	 * @return index of the row where the data starts displaying, under the header row.
	 */
	public int getDataRow() { return dataRow; }
}