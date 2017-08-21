package MainPackage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

public class SelectionManager implements ActionListener
{
	private JComboBox<String> combo = null;
    private List<Object> selectedItems = new ArrayList<Object>();
    boolean fromClick = true;

    public void actionPerformed(ActionEvent e) {
        if (combo == null) {
            combo = (JComboBox<String>) e.getSource();
        }
        
        Object item = combo.getSelectedItem();
        if(item == null)
        	return;
        if(!fromClick)
        	return;
        // Toggle the selection state for item.  
        if (selectedItems.contains(item)) 
            selectedItems.remove(item);
        else
        	selectedItems.add(item);
        
        if(selectedItems.size() == 0)
        	combo.setSelectedItem(null);
        else
        	combo.setSelectedItem(getFirstSelectedElementInItemsList());
        
    }

    private Object getFirstSelectedElementInItemsList() 
    {
    	DefaultComboBoxModel<Object> model = ((DefaultComboBoxModel)combo.getModel());
    	List<Object> ccList = new ArrayList<Object>();
		for(int i =0 ; i < model.getSize() ; i++)
			ccList.add(model.getElementAt(i));
		
		List<Integer> indexes = selectedItems.stream().map(obj->ccList.indexOf(obj)).collect(Collectors.toList());
		int minIndex = indexes.stream().min((n1 , n2) -> Integer.compare(n1, n2)).get();
		return ccList.get(minIndex);
	}

	public List<Object> getSelectedItems() {
        return selectedItems;
    }
    
    public boolean isSelected(Object item) {
        return selectedItems.contains(item);
    }
    
    
    public void addSelectedItem(Object item)
    {
    	if(!selectedItems.contains(item))
    		selectedItems.add(item);
    }

	public void removeAllSelectedItem() 
	{
		selectedItems.clear();
	}

	public void removeItem(Object item) 
	{
		 if(selectedItems.size() == 1)
		 {
			 fromClick = false;	
			 combo.setSelectedItem(null);
			 selectedItems.remove(item);
		 }
	    else
	    {
	    	fromClick = false;
	        selectedItems.remove(item);
	        combo.setSelectedItem(getFirstSelectedElementInItemsList());
	    }
		 
		 ((DefaultComboBoxModel)combo.getModel()).removeElement(item);
		 
     	fromClick = true;

	}
	
}
