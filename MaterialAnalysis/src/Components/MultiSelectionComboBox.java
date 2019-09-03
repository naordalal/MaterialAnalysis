package Components;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        super.addActionListener(manager);
        this.setRenderer(renderer);      
    }
	
	@Override
	public void addActionListener(ActionListener l) 
	{
		this.removeActionListener(manager);
		super.addActionListener(l);
		super.addActionListener(manager);
	}
    
    @Override 
    public void setPopupVisible(boolean v) 
    {
    	
    }
    
    
    
    public List<T> getSelectedItems() {
    	return (List<T>) manager.getSelectedItems();
    }
    
    @Override
    public void addItem(T item) {
    	if(((DefaultComboBoxModel) getModel()).getIndexOf(item) == -1)
    	{
    		((DefaultComboBoxModel)getModel()).addElement(item);
    		
    	}
    	manager.addSelectedItem(item);
    }
    
    public void addSelectedItem(T item)
    {
    	if(((DefaultComboBoxModel) getModel()).getIndexOf(item) != -1)
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

	public void setModelSelectedItem() 
	{
		manager.setModelSelectedItem();
		
	}
	
	public void removeSelectedItem(Object item)
	{
		manager.removeSelectedItem(item);
	}

	
}
