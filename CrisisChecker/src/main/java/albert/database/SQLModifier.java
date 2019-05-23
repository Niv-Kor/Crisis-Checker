package albert.database;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import java.lang.Exception;

/**
 * Used to execute queries and perform vaious actions on a data base.
 * @author Niv Kor
 */
public class SQLModifier
{
	public static final int NaN = (int) Double.NaN;
	
	private static SQLConnector connector;
	private static Statement statement;
	private static ResultSet resultSet;
	
	/**
	 * Initialize the static fields of the class
	 */
	public static void init() {
		connector = new SQLConnector();
		statement = connector.connect();
	}
	
	/**
	 * Run an active query on the data base
	 * Common writing actions: INSERT, UPDATE, DELETE
	 * @param query - The query to run
	 * @return true if the query ran successfuly with no errors
	 */
	public static boolean write(String query) {
		try { 
			statement.executeUpdate(query);
			return true;
		}
		catch (Exception e) {
			printError(query, "", false);
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Read an INTEGER value from the data base
	 * @param query - The query to run
	 * @param column - The column from which to retrieve the value
	 * @return an INTEGER value that the query requested
	 */
	public static int readINT(String query, String column) {
		try {
			resultSet = statement.executeQuery(query);
			if (resultSet.next()) return resultSet.getInt(column);
		}
		catch (Exception e) { printError(query, "integer", true); }
		return NaN;
	}
	
	/**
	 * Read a list of INTEGER values from the data base
	 * @param query - The query to run
	 * @param column - The column from which to retrieve the list of values
	 * @return a list of INTEGER values that the query requested
	 */
	public static List<Integer> readAllINT(String query, String column) {
		List<Integer> list = new ArrayList<Integer>();
		
		try {
			resultSet = statement.executeQuery(query);
			while (resultSet.next()) list.add(resultSet.getInt(column));
		}
		catch (Exception e) { printError(query, "integer", true); }
		return list;
	}
	
	/**
	 * Read a DECIMAL value from the data base
	 * @param query - The query to run
	 * @param column - The column from which to retrieve the value
	 * @return a DECIMAL value that the query requested
	 */
	public static double readDECIMAL(String query, String column, Class<?> c) {
		try {
			resultSet = statement.executeQuery(query);
			if (resultSet.next()) return resultSet.getDouble(column);
		}
		catch (Exception e) { printError(query, "decimal", true); }
		return NaN;
	}
	
	/**
	 * Read a list of DECIMAL values from the data base
	 * @param query - The query to run
	 * @param column - The column from which to retrieve the list of values
	 * @return a list of DECIMAL values that the query requested
	 */
	public static List<Double> readAllDECIMAL(String query, String column) {
		List<Double> list = new ArrayList<Double>();
		
		try {
			resultSet = statement.executeQuery(query);
			while (resultSet.next()) list.add(resultSet.getDouble(column));
		}
		catch (Exception e) { printError(query, "decimal", true); }
		return list;
	}
	
	/**
	 * Read a VARCHAR value from the data base
	 * @param query - The query to run
	 * @param column - The column from which to retrieve the value
	 * @return a VARCHAR value that the query requested
	 */
	public static String readVARCHAR(String query, String column) {
		try {
			resultSet = statement.executeQuery(query);
			if (resultSet.next()) return resultSet.getString(column);
		}
		catch (Exception e) { printError(query, "varchar", true); }
		return null;
	}
	
	/**
	 * Read a list of VARCHAR values from the data base
	 * @param query - The query to run
	 * @param column - The column from which to retrieve the list of values
	 * @return a list of VARCHAR values that the query requested
	 */
	public static List<String> readAllVARCHAR(String query, String column) {
		List<String> list = new ArrayList<String>();
		
		try {
			resultSet = statement.executeQuery(query);
			while (resultSet.next()) list.add(resultSet.getString(column));
		}
		catch (Exception e) { printError(query, "varchar", true); }
		return list;
	}
	
	/**
	 * Read a BOOLEAN value from the data base
	 * @param query - The query to run
	 * @param column - The column from which to retrieve the value
	 * @return a BOOLEAN value that the query requested
	 */
	public static boolean readBOOLEAN(String query, String column) {
		try {
			resultSet = statement.executeQuery(query);
			if (resultSet.next()) return resultSet.getBoolean(column);
		}
		catch (Exception e) { printError(query, "boolean", true); }
		return false;
	}
	
	/**
	 * Read a list of BOOLEAN values from the data base
	 * @param query - The query to run
	 * @param column - The column from which to retrieve the list of values
	 * @return a list of BOOLEAN values that the query requested
	 */
	public static List<Boolean> readAllBOOLEAN(String query, String column) {
		List<Boolean> list = new ArrayList<Boolean>();
		
		try {
			resultSet = statement.executeQuery(query);
			while (resultSet.next()) list.add(resultSet.getBoolean(column));
		}
		catch (Exception e) { printError(query, "boolean", true); }
		return list;
	}
	
	/**
	 * Read a DATE value from the data base
	 * @param query - The query to run
	 * @param column - The column from which to retrieve the value
	 * @return a DATE value that the query requested
	 */
	public static Date readDATE(String query, String column) {
		try {
			resultSet = statement.executeQuery(query);
			if (resultSet.next()) return resultSet.getDate(column);
		}
		catch (Exception e) { printError(query, "date", true); }
		return null;
	}
	
	/**
	 * Read a list of DATE values from the data base
	 * @param query - The query to run
	 * @param column - The column from which to retrieve the list of values
	 * @return a list of DATE values that the query requested
	 */
	public static List<Date> readAllDATE(String query, String column) {
		List<Date> list = new ArrayList<Date>();
		
		try {
			resultSet = statement.executeQuery(query);
			while (resultSet.next()) list.add(resultSet.getDate(column));
		}
		catch (Exception e) { printError(query, "date", true); }
		return list;
	}
	
	/**
	 * Build a fixed query with this format: SELECT [select] FROM [from] WHERE [where] = [equals]
	 * @param select - Name of the column/s
	 * @param from - Name of the table
	 * @param where - INTEGER value
	 * @param equals - another INTEGER value
	 * @return String query
	 */
	public static String buildQuery(String select, String from, String where, int equals) {
		return "SELECT " + select + " "
			 + "FROM " + from + " "
			 + "WHERE " + where + " = " + equals + ";";
	}
	
	/**
	 * Build a fixed query with this format: SELECT [select] FROM [from] WHERE [where] = [equals]
	 * @param select - Name of the column/s
	 * @param from - Name of the table
	 * @param where - DECIMAL value
	 * @param equals - another DECIMAL value
	 * @return String query
	 */
	public static String buildQuery(String select, String from, String where, double equals) {
		return "SELECT " + select + " "
			 + "FROM " + from + " "
			 + "WHERE " + where + " = " + equals + ";";
	}
	
	/**
	 * Build a fixed query with this format: SELECT [select] FROM [from] WHERE [where] = [equals]
	 * @param select - Name of the column/s
	 * @param from - Name of the table
	 * @param where - VARCHAR value
	 * @param equals - another VARCHAR value
	 * @return String query
	 */
	public static String buildQuery(String select, String from, String where, String equals) {
		return "SELECT " + select + " "
			 + "FROM " + from + " "
			 + "WHERE " + where + " = '" + equals + "';";
	}
	
	/**
	 * Build a fixed query with this format: SELECT [select] FROM [from] WHERE [where] = [equals]
	 * @param select - Name of the column/s
	 * @param from - Name of the table
	 * @param where - BOOLEAN value
	 * @param equals - another BOOLEAN value
	 * @return String query
	 */
	public static String buildQuery(String select, String from, String where, boolean equals) {
		return "SELECT " + select + " "
			 + "FROM " + from + " "
			 + "WHERE " + where + " = " + equals + ";";
	}
	
	/**
	 * Build a fixed query with this format: SELECT [select] FROM [from] WHERE [where] = [equals]
	 * @param select - Name of the column/s
	 * @param from - Name of the table
	 * @param where - DATE value
	 * @param equals - another DATE value
	 * @return String query
	 */
	public static String buildQuery(String select, String from, String where, Date equals) {
		return "SELECT " + select + " "
			 + "FROM " + from + " "
			 + "WHERE " + where + " = " + equals + ";";
	}
	
	/**
	 * Print to the console the error that occured while trying to do a data base action.
	 * @param query - The query that was executed
	 * @param dataType - The data type that was suppose to return as a result
	 * @param read - True if the action was to read something from the data base
	 */
	private static void printError(String query, String dataType, boolean read) {
		String action = read ? "retrieve" : "write";
		if (dataType.equals("")) dataType = "the following";
		System.err.println("Could not " + action + " " + dataType + " data for the query: \n" + query + "\n\n");
	}
}