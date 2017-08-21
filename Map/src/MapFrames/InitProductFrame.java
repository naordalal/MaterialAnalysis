package MapFrames;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.util.Date;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import AnalyzerTools.Analyzer;
import MainPackage.DataBase;
import MainPackage.Globals;
import MainPackage.Globals.FormType;

public class InitProductFrame implements ActionListener 
{
	private JFrame frame;
	private JPanel panel;
	private String userName;
	private JLabel initTypeLabel;
	private JLabel catalogNumberLabel;
	private JComboBox<String> catalogNumberComboBox;
	private JLabel quantityLabel;
	private JTextField quantityText;
	private JLabel requireDateLabel;
	private JTextField requireDateText;
	private Analyzer analyzer;
	private DataBase db;
	private Globals globals;
	private JButton addInitButton;
	private List<FormType> formsType;
	private int currentTypeIndex;
	
	public InitProductFrame(String userName , List<FormType> formsType) 
	{
		this.userName = userName;
		this.formsType = formsType;
		analyzer = new Analyzer();
		db = new DataBase();
		initialize();
	}

	private void initialize() 
	{
		globals = new Globals();
		this.currentTypeIndex = 0;
		
		frame = new JFrame("New Forecast");
		frame.setVisible(true);
		frame.setLayout(null);
		frame.getRootPane().setFocusable(true);
		frame.setBounds(300, 100, 500, 400);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) 
            {
            	close();
            }
        });
		frame.setResizable(false);
		frame.setIconImage(globals.frameImage);
		
		frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Cancel"); 
		frame.getRootPane().getActionMap().put("Cancel", new AbstractAction(){
			private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e)
	            {
					close();
	            }
	        });
		
		panel = new JPanel();
		panel.setLocation(0 , 0);
		panel.setSize(500, 500);
		panel.setLayout(null);
		frame.add(panel);
		
		initTypeLabel = new JLabel("<html><u><b>Init " + globals.FormTypeToString(formsType.get(0)) + ":</b></u></html>");
		initTypeLabel.setLocation(10,0);
		initTypeLabel.setSize(100,100);
		panel.add(initTypeLabel);
		
		catalogNumberLabel = new JLabel("<html><u>Catalog Number:</u></html>");
		catalogNumberLabel.setLocation(30,40);
		catalogNumberLabel.setSize(100,100);
		panel.add(catalogNumberLabel);
		
		List<String> catalogNumbers = db.getAllCatalogNumbers(userName);
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
		for (String catalogNumber : catalogNumbers) 	
			model.addElement(catalogNumber);
		
		catalogNumberComboBox = new JComboBox<>(model);
		catalogNumberComboBox.setLocation(120, 80);
		catalogNumberComboBox.setSize(150, 20);
		catalogNumberComboBox.addActionListener(this);
		     
		panel.add(catalogNumberComboBox);
		
		quantityLabel = new JLabel("<html><u>Quantity:</u></html>");
		quantityLabel.setLocation(30,110);
		quantityLabel.setSize(100,100);
		panel.add(quantityLabel);
		
		quantityText = new JTextField();
		quantityText.setLocation(120, 150);
		quantityText.setSize(150, 20);
		panel.add(quantityText);
		
		requireDateLabel = new JLabel("<html><u>Require Date:</u></html>");
		requireDateLabel.setLocation(30,180);
		requireDateLabel.setSize(100,100);
		panel.add(requireDateLabel);
		
		requireDateText = new JTextField();
		requireDateText.setLocation(120, 220);
		requireDateText.setSize(150, 20);
		panel.add(requireDateText);
		
		addInitButton = new JButton();
		addInitButton.setLocation(200, 300);
		addInitButton.setSize(80 , 40);
		addInitButton.addActionListener(this);
		addInitButton.setIcon(globals.okIcon);
		addInitButton.setFocusable(false);
		addInitButton.setContentAreaFilled(false);
		addInitButton.setPressedIcon(globals.clickOkIcon);
		addInitButton.setToolTipText("OK");
		panel.add(addInitButton);
	}

	private void close() 
	{
		if(currentTypeIndex == 0)
			frame.dispose();
		else
			JOptionPane.showConfirmDialog(null, "You are not done yet","",JOptionPane.PLAIN_MESSAGE);
	}

	@Override
	public void actionPerformed(ActionEvent event) 
	{
		if(event.getSource() == addInitButton)
		{
			if(!org.apache.commons.lang3.StringUtils.isNumeric(quantityText.getText().trim()))
			{
				JOptionPane.showConfirmDialog(null, "Please enter a valid quantity","",JOptionPane.PLAIN_MESSAGE);
				return;
			}
			
			Date requireDate = Globals.isValidDate(requireDateText.getText()); 
			if(requireDate == null)
			{
				JOptionPane.showConfirmDialog(null, "Please enter a valid require date","",JOptionPane.PLAIN_MESSAGE);
				return;
			}
			
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			String catalogNumber = (String) catalogNumberComboBox.getModel().getSelectedItem();
			if(currentTypeIndex == 0)
				analyzer.cleanProductQuantityPerDate(catalogNumber);
			
			String initDate = Globals.dateWithoutHourToString(Globals.getTodayDate());
			String quantity = quantityText.getText().trim();
			analyzer.addNewInitProductCustomerOrders(catalogNumber, initDate, quantity, requireDateText.getText(), formsType.get(currentTypeIndex));
			
			currentTypeIndex = (currentTypeIndex + 1) % formsType.size();
			initTypeLabel.setText("<html><u>Init " + globals.FormTypeToString(formsType.get(currentTypeIndex)) + ":</u></html>");
			
			if(currentTypeIndex >= 1)
				catalogNumberComboBox.setEnabled(false);
			else
			{
				catalogNumberComboBox.setEnabled(true);	
				catalogNumberComboBox.getModel().setSelectedItem(null);
			}
			
			quantityText.setText("");
			requireDateText.setText("");
			quantityText.requestFocusInWindow();
			
			frame.setCursor(Cursor.getDefaultCursor());
			
			if(currentTypeIndex == 0)
				JOptionPane.showConfirmDialog(null, "Init successfully","",JOptionPane.PLAIN_MESSAGE);
			
		}
	}
}
