package Components;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class MyTableRenderer extends DefaultTableCellRenderer 
{

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
	{
		Component currentComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (hasFocus) 
        {
        	currentComponent.setBackground(Color.yellow.brighter());
        	currentComponent.setForeground(Color.black);
        } else if(isSelected)
        {
        	currentComponent.setBackground(table.getSelectionBackground());
        	currentComponent.setForeground(Color.black);
        }
        else
        {
        	currentComponent.setBackground(table.getBackground());
        	currentComponent.setForeground(table.getForeground());
        	currentComponent.setForeground(Color.black);
        }
        return currentComponent;
	}

}
