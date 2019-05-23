package albert.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Used to connect to a data base.
 * @author Niv Kor
 */
public class SQLConnector {
	private final static String HOST = "jdbc:mysql://sql7.freemysqlhosting.net/sql7289129?useLegacyDatetimeCode=false&serverTimezone=UTC";
	private final static String USERNAME = "sql7289129";
	private final static String PASSWORD = "cdQTqPIYZQ";
	
	private Connection connection;
	
	/**
	 * Connect to the data base
	 * @return connection statement
	 */
	public Statement connect() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(HOST, USERNAME, PASSWORD);
			return connection.createStatement();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.err.println("Could not connect to MySQL database.");
		}
		
		return null;
	}
}