package Components;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.text.JTextComponent;

public class FilterCombo extends JComboBox<String> implements KeyListener, FocusListener
{
	private static final long serialVersionUID = 1L;
	
	private DefaultComboBoxModel<String> model;
	private JTextComponent tc;
	private List<String> baseValues;
	private String lastText;
	private boolean clearWhenFocusLost;

	public FilterCombo(List<String> baseValues , DefaultComboBoxModel<String> model , boolean clearWhenFocusLost) 
	{
		super(model);
		this.model = model;
		this.baseValues = baseValues;
		this.clearWhenFocusLost = clearWhenFocusLost;
		
		this.setEditable(true);
		baseValues.stream().forEach(cn -> this.model.addElement(cn));
		model.setSelectedItem(null);
		
		super.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(model.getSelectedItem() != null)
					lastText = ((String) model.getSelectedItem());
				else 
					lastText = "";
			}
		});
		
		Component editor = this.getEditor().getEditorComponent();
		if (editor instanceof JTextComponent)
		{
			this.tc = (JTextComponent) editor;	
			this.tc.addKeyListener(this);
			this.tc.addFocusListener(this);
		}

	}

	private void comboFilter(String enteredText) 
	{
		ActionListener[] actionListeners = this.getActionListeners();
		for (ActionListener actionListener : actionListeners) 
		{
			this.removeActionListener(actionListener);
		}
		
		if (this.isPopupVisible()) 
	        this.hidePopup();
		
        this.model.removeAllElements();
		List<String> containsValue = getContainsValue(enteredText);
		
		if (containsValue.size() > 0) 
		{
	        for (String s: containsValue)
	            this.addElement(s);
	    }
		
		this.showPopup();		
				
		for (ActionListener actionListener : actionListeners) 
		{
			this.addActionListener(actionListener);
		}
		
		if(containsValue.size() == 1 && containsValue.get(0).equalsIgnoreCase(enteredText))
		{
			this.model.setSelectedItem(containsValue.get(0));
		}
		else
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
            		hidePopup();
            		return;
            	}
            	if(ke.getKeyCode() == KeyEvent.VK_ENTER)
            	{
    				List<String> containsValue = getContainsValue(tc.getText());
    				if(containsValue.size() == 1)
    				{
            			model.setSelectedItem(containsValue.get(0));
            			setText(containsValue.get(0));
    				}
            		return;
            	}
            	else if(ke.getKeyCode() == KeyEvent.VK_DOWN || ke.getKeyCode() == KeyEvent.VK_UP)
            	{
            		if(model.getSelectedItem() != null)
            			setText((String) model.getSelectedItem());
            		return;
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
	            		tc.setText(lastText);
	            	}
	            	else if(model.getSelectedItem() == null)
	            	{
	            		tc.setText("");
	            		baseValues.stream().forEach(cn -> addElement(cn));
	            		model.setSelectedItem(null);
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


}
	   
