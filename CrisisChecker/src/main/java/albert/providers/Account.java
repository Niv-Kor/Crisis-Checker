package albert.providers;
import java.util.ArrayList;
import java.util.List;

/**
 * Account of a provider.
 * @author Niv Kor
 */
public abstract class Account
{
	protected static Character idDelimeter;
	protected static int idLength;
	protected long id;
	protected String name;
	
	/**
	 * @param id - Account's ID
	 * @param name - Account's name
	 */
	public Account(long id, String name) {
		this.id = id;
		this.name = new String(name);
	}
	
	/**
	 * @return formatted ID with delimeters;
	 */
	public String getFormattedID() {
		String source = (Long.toString(id));
		List<Character> list = new ArrayList<Character>();
		int[] divisor = {3, 3};
		String str = "";
		
		//to list
		for (int i = 0; i < source.length(); i++)
			list.add(source.charAt(i));
		
		//add delimeters
		for (int i = 0, s = 0; i < divisor.length; i++) {
			//set s to be the offset for the new delimeter
			int sum = 0;
			for (int j = 0; j < i + 1; j++)	sum += divisor[i];
			s = sum;
			
			list.add(s, idDelimeter);
		}
		
		//back to string
		for (Character c : list) str = str.concat(c.toString());
		
		return str;
	}
	
	/**
	 * Convert formatted ID to a non-formatted one.
	 * @param formatted - The formatted ID
	 * @return non-formatted ID without delimeters.
	 */
	public static long getPracticalID(String formatted) {
		return Long.parseLong(formatted.replace(idDelimeter.toString(), ""));
	}
	
	/**
	 * @return non-formatted ID without delimeters.
	 */
	public long getPracticalID() { return id; }
	
	/**
	 * @return account's name.
	 */
	public String getName() { return name; }
	
	/**
	 * @return ID's length.
	 */
	public int getIDLength() { return idLength; }
}