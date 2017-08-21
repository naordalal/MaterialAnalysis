package Components;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

public class FilterCombo extends JComboBox<String> implements KeyListener
{
	private static final long serialVersionUID = 1L;
	
	private DefaultComboBoxModel<String> model;
	private JTextComponent tc;
	private List<String> baseValues;

	public FilterCombo(List<String> baseValues , DefaultComboBoxModel<String> model) 
	{
		super(model);
		this.model = model;
		this.baseValues = baseValues;
		this.setEditable(true);
		baseValues.stream().forEach(cn -> model.addElement(cn));
		model.setSelectedItem(null);
		
		Component editor = this.getEditor().getEditorComponent();
		if (editor instanceof JTextComponent)
		{
			this.tc = (JTextComponent) editor;	
			this.tc.addKeyListener(this);
		}

	}

	private void comboFilter(String enteredText) 
	{
		if (this.isPopupVisible()) 
	        this.hidePopup();
		
        this.model.removeAllElements();
		List<String> containsValue = baseValues.stream().filter(cn -> cn.toLowerCase().contains(enteredText.toLowerCase())).collect(Collectors.toList());
		
		if (containsValue.size() > 0) 
		{
	        for (String s: containsValue)
	            this.model.addElement(s);

	        this.model.setSelectedItem(null);
	    }
		
        tc.setText(enteredText);
		this.showPopup();			
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
            	if(ke.getKeyCode() == KeyEvent.VK_CANCEL)
            	{
            		hidePopup();
            	}
            	if(ke.getKeyCode() == KeyEvent.VK_ENTER)
            	{
            		if(model.getSelectedItem() != null)
            			tc.setText((String) model.getSelectedItem());
            	}
            	else if(ke.getKeyCode() == KeyEvent.VK_DOWN || ke.getKeyCode() == KeyEvent.VK_UP)
            		return;
            	else
            		comboFilter(tc.getText());
            }
        });
		
	}

	@Override
	public void keyTyped(KeyEvent ke) 
	{
		
	}	
}
	   
