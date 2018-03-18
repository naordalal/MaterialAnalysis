package Components;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.text.JTextComponent;

public class MultiFilterCombo extends JComboBox<String> implements KeyListener, FocusListener , ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private DefaultComboBoxModel<String> model;
	private JTextComponent tc;
	private List<String> baseValues;
	private String lastText;
	private boolean clearWhenFocusLost;
	private List<String> selectedItems = new ArrayList<String>();

	private boolean fromClick = true;
	private boolean close = false;


	public MultiFilterCombo(List<String> baseValues , DefaultComboBoxModel<String> model , boolean clearWhenFocusLost) 
	{
		super(model);
		this.model = model;
		this.baseValues = new ArrayList<>();
		this.baseValues.addAll(baseValues);
		this.clearWhenFocusLost = clearWhenFocusLost;
		
		initialize();

	}
	
	public void initialize()
	{
		this.setEditable(true);
		baseValues.stream().forEach(cn -> this.model.addElement(cn));     
		model.setSelectedItem(null);
		
		BasicComboBoxRenderer renderer = new MyComboBoxRenderer(this);
        this.setRenderer(renderer);          
        
		Component editor = this.getEditor().getEditorComponent();
		if (editor instanceof JTextComponent)
		{
			this.tc = (JTextComponent) editor;	
			this.tc.addKeyListener(this);
			this.tc.addFocusListener(this);
		}
		
		super.addActionListener(this);
	}
	
	@Override
	public void addActionListener(ActionListener l) 
	{
		this.removeActionListener(this);
		super.addActionListener(l);
		super.addActionListener(this);
	}
	
	 @Override 
    public void setPopupVisible(boolean v) 
    {
		if(close)
			super.setPopupVisible(v);
    }

	public void updateSelectedItem()
	{
		Object item = this.getSelectedItem();
        if(item == null)
        	return;
 
        if(!fromClick)
        	return;
        
        if (selectedItems.contains(item)) 
            selectedItems.remove(item);
        else
        	selectedItems.add((String) item);
        
        if(selectedItems.size() == 0)
        	this.setSelectedItem(null);
        else
        	this.setSelectedItem(getFirstSelectedElementInItemsList((DefaultComboBoxModel<String>)this.getModel()));
	}
	
	public Object getFirstSelectedElementInItemsList(DefaultComboBoxModel<String> model) 
    {
    	List<Object> ccList = new ArrayList<Object>();
		for(int i =0 ; i < model.getSize() ; i++)
			ccList.add(model.getElementAt(i));
		
		List<Integer> indexes = selectedItems.stream().map(obj->ccList.indexOf(obj)).collect(Collectors.toList());
		int minIndex = indexes.stream().min((n1 , n2) -> Integer.compare(n1, n2)).get();
		if(minIndex < 0)
			return null;
		return ccList.get(minIndex);
	}

	public MultiFilterCombo(DefaultComboBoxModel<String> model , boolean clearWhenFocusLost) 
	{
		super(model);
		this.model = model;
		this.clearWhenFocusLost = clearWhenFocusLost;
		this.baseValues = new ArrayList<>();
		
		initialize();
	}

	private void comboFilter(String enteredText) 
	{
		ActionListener[] actionListeners = this.getActionListeners();
		for (ActionListener actionListener : actionListeners) 
		{
			this.removeActionListener(actionListener);
		}
		
		if (this.isPopupVisible()) 
		{
			close = true;
			this.hidePopup();
			close = false;
		}
		
        this.model.removeAllElements();
		List<String> containsValue = getContainsValue(enteredText);

		if (containsValue.size() > 0) 
		{
	        for (String s: containsValue)
	            this.model.addElement(s);
	    }
		
		this.tc.setSelectionStart(enteredText.length());
		this.tc.setSelectionEnd(enteredText.length());
		
		close = true;
		this.showPopup();
		close = false;
	
				
		for (ActionListener actionListener : actionListeners) 
		{
			this.addActionListener(actionListener);
		}
		
		this.model.setSelectedItem(null);
		
		setText(enteredText);
		
		
	}

	public List<String> getContainsValue(String enteredText) 
	{
		return baseValues.stream().filter(cn -> cn.toLowerCase().contains(enteredText.toLowerCase())).collect(Collectors.toList());
	}

	public String getText()
	{
		return this.tc.getText();
	}
	
	public void setText(String text)
	{
		this.tc.setText(text);
		this.lastText = text;
	}
	
	@Override
	public void keyPressed(KeyEvent ke) 
	{
	}

	@Override
	public void keyReleased(KeyEvent ke) 
	{
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	if(ke.getKeyCode() == KeyEvent.VK_ESCAPE)
            	{
            		close = true;
            		hidePopup();
            		close = false;
            		if(model.getSelectedItem() == null)
            			setText("");
            		return;
            	}
            	if(ke.getKeyCode() == KeyEvent.VK_ENTER)
            	{
            		System.out.println(lastText + "  Y");
    				if(baseValues.contains(lastText))
    				{
    					fromClick = true;
    					model.setSelectedItem(lastText);
    					updateSelectedItem();
    					close = true;
    					hidePopup();
    					fromClick = false;
    					close = false;
    				}
            	}
            	else if(ke.getKeyCode() == KeyEvent.VK_UP || ke.getKeyCode() == KeyEvent.VK_DOWN)
            	{
            		fromClick  = false;
            		String text = (String) model.getSelectedItem();
            		model.setSelectedItem(null);
            		setText(text);
            		selectedItems.remove(text);
            		fromClick  = true;
            	}
            	else
            		comboFilter(tc.getText());
            }
        });
		
	}

	@Override
	public void keyTyped(KeyEvent ke) 
	{
		
	}

	@Override
	public void focusGained(FocusEvent event) 
	{

	}

	@Override
	public void focusLost(FocusEvent event) 
	{
		if(event.getSource() == this.tc)
		{
			SwingUtilities.invokeLater(new Runnable() {
	            public void run() 
	            {
	            	if(!clearWhenFocusLost)
	            	{
	            		setText(lastText);
	            	}
	            	else
	            	{
	            		if(model.getSelectedItem() == null)
	            			clear();
	            		String item = (String) getModel().getSelectedItem();
	            		if(baseValues.contains(item))
	            			return;
	    				if(!baseValues.contains(item) && (item == null || lastText.equals(item)))
	    				{
	    					lastText = "";
	    					getModel().setSelectedItem(null);
	    				}
	    				else
	    				{
	    					lastText = item;
	    				}
	    				
	            		if(model.getSelectedItem() == null)
	            			clear();
	            	}
	            }
			});
		}
	}
	
	public void clear() 
	{
		setText("");
		this.model.removeAllElements();
		baseValues.stream().forEach(cn -> this.model.addElement(cn));
		this.model.setSelectedItem(null);
	}	
	
	private void addElement(String element) 
	{
		ActionListener[] actionListeners = this.getActionListeners();
		for (ActionListener actionListener : actionListeners) 
		{
			this.removeActionListener(actionListener);
		}
		
		model.addElement(element);
		
		for (ActionListener actionListener : actionListeners) 
		{
			this.addActionListener(actionListener);
		}
	}

	public void setBaseValues(List<String> newBaseValues) 
	{
		this.removeAllItems();
		this.baseValues.clear();
		this.baseValues.addAll(newBaseValues);
		for (String val : newBaseValues) 
		{
			this.addItem(val);
		}
		model.setSelectedItem(null);
	}

	public boolean isSelected(Object value) 
	{
		return selectedItems.contains(value);
	}

	public List<String> getSelectedItems() 
	{
		return selectedItems;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(model.getSelectedItem() == null)
			lastText = "";
		else
			lastText = (String) model.getSelectedItem();
		
		updateSelectedItem();
	}



}
	   
