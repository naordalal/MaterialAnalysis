package MainPackage;

import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

public class MultiSelectionComboBox<T> extends JComboBox<T>
{
    
    

	private SelectionManager manager;



	public MultiSelectionComboBox(DefaultComboBoxModel model)
    {
    	super(model);

        manager = new SelectionManager();

        BasicComboBoxRenderer renderer = new MyComboBoxRenderer(manager);
        this.addActionListener(manager);
        this.setRenderer(renderer);      
    }
    
    @Override 
    public void setPopupVisible(boolean v) {

      }
    
    
    
    public List<T> getSelectedItems() {
    	return (List<T>) manager.getSelectedItems();
    }
    
    @Override
    public void addItem(T item) {
    	if(((DefaultComboBoxModel) getModel()).getIndexOf(item) == -1)
    		((DefaultComboBoxModel)getModel()).addElement(item);
    	manager.addSelectedItem(item);
    }
    
    @Override
    public void removeAllItems() {
    	super.removeAllItems();
    	manager.removeAllSelectedItem();
    }

	public void removeAllSelectedItem() {
		manager.removeAllSelectedItem();
		
	}
	
	@Override
	public void removeItem(Object item) {	
		manager.removeItem(item);
	}
	
}
