package albert.user_interface.states.report_builder;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;

class CustomTable extends JTable
{
	private static final long serialVersionUID = 2854268593915736646L;
	private static final MatteBorder BORDER = new MatteBorder(0, 0, 0, 0, Color.BLACK);
	
	/**
	 * @param rows - Number of rows in the table
	 * @param cols - Number of column in the table
	 */
	public CustomTable(int rows, int cols) {
		super(rows, cols);
	}
	
	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component component = super.prepareRenderer(renderer, row, column);
        JComponent jc = (JComponent) component;
        jc.setBorder(BORDER);
        return component;
    }
}