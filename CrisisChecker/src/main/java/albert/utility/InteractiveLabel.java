package albert.utility;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.Callable;

public class InteractiveLabel extends InteractiveComponent implements MouseListener
{
	private static final long serialVersionUID = 3440174835250357589L;
	private final static Color DEF_HOVER_COLOR = Color.ORANGE;
	private final static Color DEF_SELECT_COLOR = new Color(88, 178, 255);
	
	private Color originColor, hoverColor, selectColor;
	private boolean clicked, enableSelection;
	
	public InteractiveLabel() {
		init();
	}
	
	public InteractiveLabel(String name) {
		super(name);
		init();
	}
	
	public InteractiveLabel(String name, Callable<Void> func) {
		this(name);
		this.func = func;
	}
	
	private void init() {
		addMouseListener(this);
		hoverColor = DEF_HOVER_COLOR;
		selectColor = DEF_SELECT_COLOR;
		enableSelection = true;
	}
	
	public void mouseEntered(MouseEvent e) {
		super.mouseEntered(e);
		super.setForeground(hoverColor);
	}
	
	public void mouseExited(MouseEvent e) {
		super.mouseExited(e);
		if (!clicked || !enableSelection) super.setForeground(originColor);
		else super.setForeground(selectColor);
	}

	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		
		clicked = !clicked;
		if (enableSelection) super.setForeground(selectColor);
	}
	
	public void enableSelectionColor(boolean flag) {
		enableSelection = flag;
	}

	public void setForeground(Color color) {
		super.setForeground(color);
		originColor = color;
	}
	
	public void release() {
		clicked = false;
		setForeground(originColor);
	}
	
	public Color getHoverColor() { return hoverColor; }
	public Color getSelectColor() { return selectColor; }
	public void setHoverColor(Color c) { hoverColor = c; }
	public void setSelectColor(Color c) { selectColor = c; }
	public boolean isClicked() { return clicked; }
}