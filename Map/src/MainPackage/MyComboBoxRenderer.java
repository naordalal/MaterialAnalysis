package MainPackage;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

public class MyComboBoxRenderer extends BasicComboBoxRenderer 
{

	 SelectionManager selectionManager;
	  
    public MyComboBoxRenderer(SelectionManager sm) {
        selectionManager = sm;
    }
  
    public Component getListCellRendererComponent(JList list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
        if (selectionManager.isSelected(value) || isSelected) 
        {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
            
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
  
        setFont(list.getFont());
  
        if (value instanceof Icon) {
            setIcon((Icon)value);
        } else {
            setText((value == null) ? "" : value.toString());
        }
        return this;
    }


}
