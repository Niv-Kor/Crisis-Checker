package albert.user_interface.states.report_builder;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

class CustomCellRenderer extends DefaultTableCellRenderer implements TableCellRenderer
{
	private static final long serialVersionUID = 4196557267713466963L;
	private static final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();
	private static final Color EVEN_ROW_COLOR = new Color(74, 192, 255);
	private static final Color ODD_ROW_COLOR = new Color(62, 224, 255);
	private static final Color SELECTED_ROW_COLOR = new Color(206, 255, 0);
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component component = DEFAULT_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		// Apply zebra style on table rows
		if (table.getSelectedRow() == row) component.setBackground(SELECTED_ROW_COLOR);
		else if (row % 2 == 0) component.setBackground(EVEN_ROW_COLOR);
		else component.setBackground(ODD_ROW_COLOR);
		
        return component;
    }
}