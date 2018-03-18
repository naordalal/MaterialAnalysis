package Components;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

public class MyComboBoxRenderer extends BasicComboBoxRenderer 
{

	private static final long serialVersionUID = 1L;
	
	private SelectionManager selectionManager;

	private MultiFilterCombo combo;
	  
    public MyComboBoxRenderer(SelectionManager sm) {
        this.selectionManager = sm;
    }
    
    public MyComboBoxRenderer(MultiFilterCombo combo) {
        this.combo = combo;
    }
      

	public Component getListCellRendererComponent(JList list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
        if (selectionManager != null && (selectionManager.isSelected(value) || isSelected)) 
        {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
            
        }
        else if (combo != null && (combo.isSelected(value) || isSelected)) 
        {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
            
        }
        else
        {
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
