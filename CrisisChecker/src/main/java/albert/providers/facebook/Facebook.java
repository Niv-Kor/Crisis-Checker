package albert.providers.facebook;
import albert.error.ErrorType;
import albert.providers.Account;
import albert.providers.Provider;
import albert.reports.ReportRequest;

public class Facebook extends Provider
{
	public final static String PROVIDER_ID = "0"; //TODO
	
	public Facebook() {
		super(PROVIDER_ID);
	}

	public int getHeaderRow() { return 3; }
	public int getInfoRow() { return 5; }

	protected void connect(long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Account getAccount(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void runReport(ReportRequest request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String errorName(ErrorType error) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String columnName(ErrorType error) {
		// TODO Auto-generated method stub
		return null;
	}
}