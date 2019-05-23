package albert.user_interface.result_log;
import java.util.ArrayList;
import java.util.List;

public class ButtonGroup
{
	private List<SegmantationButton> buttons;
	
	public ButtonGroup() {
		this.buttons = new ArrayList<SegmantationButton>();
	}
	
	/**
	 * Add a button to the group.
	 * @param button - The button to add
	 */
	public void add(SegmantationButton button) { buttons.add(button); }
	
	/**
	 * Remove a button from the group.
	 * @param button - The button to remove
	 */
	public void remove(SegmantationButton button) { buttons.remove(button); }
	
	/**
	 * Remove all buttons from the group.
	 */
	public void clear() { buttons.clear(); }
	
	/**
	 * Deselect all buttons added to the group.
	 */
	public void deselectAll() { for (SegmantationButton b : buttons) b.deselect(); }
}