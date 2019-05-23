package albert.utility;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.Callable;
import javax.swing.Icon;
import javax.swing.JLabel;

public abstract class InteractiveComponent extends JLabel implements MouseListener {
	private static final long serialVersionUID = 6779388611159602543L;
	
	protected Callable<?> func;
	protected boolean hovered;
	
	protected InteractiveComponent() {}
	
	protected InteractiveComponent(String text) {
		super(text);
	}
	
	protected InteractiveComponent(Icon icon) {
		super(icon);
	}
	
	public void mousePressed(MouseEvent e) {
		if (func != null) {
			try { func.call(); }
			catch (Exception ex) { ex.printStackTrace(); }
		}
	}
	
	public boolean isHovered() { 
		return false;
	}
	
	public Callable<?> getFunction() { return func; }
	public void setFunction(Callable<?> f) { func = f; }
	public void mouseClicked(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}
	public void mouseEntered(MouseEvent e) { hovered = true; }
	public void mouseExited(MouseEvent e) { hovered = false; }
}