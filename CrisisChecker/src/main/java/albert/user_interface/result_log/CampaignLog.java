package albert.user_interface.result_log;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import albert.excel_segmantation.Campaign;
import albert.user_interface.ResultLogWindow;
import albert.utility.files.FontHandler;
import albert.utility.files.FontHandler.FontStyle;
import albert.utility.math.Range;

public class CampaignLog extends JTextPane
{
	private static final long serialVersionUID = -197807169943524613L;
	private static final Color NUMBER_COLOR = Color.CYAN;
	private static final Color CRITERIA_COLOR = Color.CYAN;
	private static final Color ROW_COLOR = Color.WHITE;
	private static final Font FONT = FontHandler.load("Montserrat", FontStyle.PLAIN, 13);
	
	private Campaign campaign;
	private boolean clear;
	
	/**
	 * @param camapign - The campaign to analyze
	 */
	public CampaignLog(Campaign camapign) {
		setFocusable(false);
		setFont(FONT);
		
		this.campaign = camapign;
		
		int[] frameIndex;
		int columnNameIndex;
		boolean firstLine = true;
		
		if (camapign.getErrorDetector().getStream().size() > 0) {
			for (String line : camapign.getErrorDetector().getStream()) {
				if (!firstLine && !line.equals("\n")) append("\n", Color.BLACK);
				
				if (line.length() > 5) { //size of date
					//date and space
					append(line.substring(0, 5), ROW_COLOR);
					
					//column name
					line = line.substring(5, line.length());
					columnNameIndex = columnNameIndex(line);
					append(line.substring(0, columnNameIndex), CRITERIA_COLOR);
					
					//number #1
					line = line.substring(columnNameIndex, line.length());
					
					frameIndex = numberIndex(line);
					append(line.substring(0, frameIndex[0]), ROW_COLOR);
					append(line.substring(frameIndex[0], frameIndex[1]), NUMBER_COLOR);
					
					//number #2
					line = line.substring(frameIndex[1], line.length());
					frameIndex = numberIndex(line);
					append(line.substring(0, frameIndex[0]), ROW_COLOR);
					append(line.substring(frameIndex[0], frameIndex[1]), NUMBER_COLOR);
					
					//number #3
					line = line.substring(frameIndex[1], line.length());
					frameIndex = numberIndex(line);
					append(line.substring(0, frameIndex[0]), ROW_COLOR);
					append(line.substring(frameIndex[0], frameIndex[1]), NUMBER_COLOR);
					append(line.substring(frameIndex[1], line.length()), ROW_COLOR);
				}
				else append(line, ResultLogWindow.FORGROUND_COLOR);
				
				firstLine = false;
			}
		}
		else clear = true;
		
		setEditable(false);
	}
	
	/**
	 * Add a String to the current line on the log.
	 * @param msg - The String to add
	 * @param color - The color of the String
	 */
	private void append(String msg, Color color) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);

		aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

		int len = getDocument().getLength();
		setCaretPosition(len);
		setCharacterAttributes(aset, false);
		replaceSelection(msg);
    }
	
	/**
	 * Get the error name's index within a String.
	 * @param s - The String to look at
	 * @return index of the the first character of the error's name
	 */
	private int columnNameIndex(String s) {
		for (int i = 0; i < s.length(); i++)
			if (s.charAt(i) == ' ') return i;
		
		return -1;
	}
	
	/**
	 * Get the first character index and the last character index + 1
	 * of a the first number in a String.
	 * 
	 * The output of this method is an array of 2 integers,
	 * where [0] = the first character, and [1] = the last character + 1.
	 * 
	 * @param s - The String to look at
	 * @return index of the the first character of the error's name
	 */
	private int[] numberIndex(String s) {
		int[] indexes = new int[2];
		boolean[] found = new boolean[2];
		Range<Integer> numbers = new Range<Integer>(48, 57);
		char c;
		
		for (int i = 0; i < s.length(); i++) {
			c = s.charAt(i);
			
			if (!found[0] && (numbers.intersects((int) c) || c == '~')) {
				indexes[0] = i;
				found[0] = true;
				continue;
			}
			else if (found[0] && !found[1] && !numbers.intersects((int) c) && c != '.') {
				indexes[1] = i;
				break;
			}
		}
		
		return indexes;
	}
	
	/**
	 * @return the campaign this object analyzes into a log.
	 */
	public Campaign getCampaign() { return campaign; }
	
	/**
	 * @return true if the log is empty (no errors) or false otherwise.
	 */
	public boolean isClear() { return clear; }
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		//gray line all the way down
		g.setColor(Color.GRAY);
		g.drawLine(57, 0, 57, Toolkit.getDefaultToolkit().getScreenSize().height);
	}
}