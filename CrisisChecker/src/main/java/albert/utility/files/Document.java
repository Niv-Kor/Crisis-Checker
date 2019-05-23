package albert.utility.files;
import java.io.File;
import java.util.Stack;

public enum Document {
	CSV,
	XLSX;
	
	private static Stack<File> buffer = new Stack<File>();
	
	/**
	 * Create a file of the specified type.
	 * @param fileName - The new file's name (including logical path)
	 * @return the File object that was created.
	 */
	public File createFile(String fileName) {
		File newFile = new File(fileName + end());
		System.out.println(newFile);
		buffer.push(newFile);
		return newFile;
	}
	
	/**
	 * @return the ending of the file type (Ex. ".csv").
	 */
	public String end() {
		return "." + name().toLowerCase();
	}
	
	/**
	 * Delete all files that were created during the program's session.
	 */
	public static void deleteFiles() {
		for (File f : buffer) f.delete();
	}
}