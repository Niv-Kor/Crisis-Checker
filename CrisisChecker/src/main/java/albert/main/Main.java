package albert.main;
import albert.database.SQLModifier;
import albert.providers.google_ads.GoogleAds;
import albert.user_interface.MainWindow;
import albert.user_interface.Window;
import albert.user_interface.states.StateManager;
import albert.user_interface.states.StateManager.Substate;

public class Main
{
	private static boolean updateAccounts = false;
	
	public static void main(String[] args) {
		System.setProperty("com.google.inject.internal.cglib.core.$ReflectUtils$1", "true");
		
		if (updateAccounts) {
			//update all Google Ads accounts in DB
			GoogleAds.updateAccounts();
		}
		
		//connect to DB
		SQLModifier.init();
		
		//start at ReportBuilder state
		Window window = new MainWindow();
		StateManager.setState(window, Substate.REPORT_BUILDER);
	}
}