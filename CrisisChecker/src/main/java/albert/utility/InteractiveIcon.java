package albert.utility;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ImageIcon;
import albert.utility.files.ImageHandler;

public class InteractiveIcon extends InteractiveComponent implements MouseListener
{
	private static final long serialVersionUID = -5905973344666262437L;
	
	private ImageIcon icon, selectedIcon;
	
	public InteractiveIcon(String iconPath) {
		this(ImageHandler.loadIcon(iconPath));
	}
	
	public InteractiveIcon(ImageIcon icon) {
		addMouseListener(this);
		this.icon = icon;
		setIcon(icon);
	}
	
	public void mouseEntered(MouseEvent arg0) {
		if (selectedIcon != null) super.setIcon(selectedIcon);
		revalidate();
		repaint();
	}

	public void mouseExited(MouseEvent arg0) {
		if (getIcon() != icon) super.setIcon(icon);
		revalidate();
		repaint();
	}

	public void setIcon(String iconPath) {
		setIcon(ImageHandler.loadIcon(iconPath));
	}
	
	public void setSelectedIcon(String iconPath) {
		setSelectedIcon(ImageHandler.loadIcon(iconPath));
	}
	
	public void setSelectedIcon(ImageIcon icon) {
		selectedIcon = icon;
	}
}