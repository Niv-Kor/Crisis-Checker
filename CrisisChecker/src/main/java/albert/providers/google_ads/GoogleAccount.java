package albert.providers.google_ads;
import albert.providers.Account;

public class GoogleAccount extends Account
{
	public GoogleAccount(long id, String name) {
		super(id, name);
		idDelimeter = '-';
		idLength = 12;
	}
}