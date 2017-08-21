package MapFrames;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.apache.poi.ss.usermodel.Font;

import MainPackage.DataBase;
import MainPackage.Globals;

public class AddProductFrame implements ActionListener 
{
	private Globals globals;
	private DataBase db;
	private JFrame frame;
	private JPanel panel;
	private String userName;
	private JLabel catalogNumberLabel;
	private JTextField catalogNumberText;
	private JLabel customerLabel;
	private JComboBox<String> customerComboBox;
	private JLabel descriptionLabel;
	private JTextArea descriptionText;
	private JLabel fatherLabel;
	private JComboBox<String> fatherComboBox;
	private JLabel quantityLabel;
	private JTextField quantityText;
	private JButton addProductButton;
	private JLabel copyRight;
	private String currentFatherCatalogNumber;
	
	public AddProductFrame(String userName) 
	{
		this.userName = userName;
		db = new DataBase();
		this.currentFatherCatalogNumber = "";
		initialize();
	}

	private void initialize() 
	{
		globals = new Globals();
		
		frame = new JFrame("New Product");
		frame.setVisible(true);
		frame.setLayout(null);
		frame.getRootPane().setFocusable(true);
		frame.setBounds(300, 100, 500, 500);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		frame.setIconImage(globals.frameImage);
		
		frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Cancel"); 
		frame.getRootPane().getActionMap().put("Cancel", new AbstractAction(){
			private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e)
	            {
					frame.dispose();
	            }
	        });
		
		panel = new JPanel();
		panel.setLocation(0 , 0);
		panel.setSize(500, 500);
		panel.setLayout(null);
		frame.add(panel);
		
		catalogNumberLabel = new JLabel("<html><u>Catalog Number:</u></html>");
		catalogNumberLabel.setLocation(30,20);
		catalogNumberLabel.setSize(100,100);
		panel.add(catalogNumberLabel);
		
		catalogNumberText = new JTextField();
		catalogNumberText.setLocation(150, 60);
		catalogNumberText.setSize(150, 20);
		panel.add(catalogNumberText);
		
		customerLabel = new JLabel("<html><u>Customer:</u></html>");
		customerLabel.setLocation(30,90);
		customerLabel.setSize(100,100);
		panel.add(customerLabel);
		
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
		List<String> customers = db.getCustomersOfUser(userName);
		for (String customer : customers) 
		{
			model.addElement(customer);
		}
		
		customerComboBox = new JComboBox<String>(model);
		customerComboBox.setLocation(150, 130);
		customerComboBox.setSize(150, 20);
		customerComboBox.addActionListener(this);
		panel.add(customerComboBox);
		
		descriptionLabel = new JLabel("<html><u>Description:</u></html>");
		descriptionLabel.setLocation(30, 160);
		descriptionLabel.setSize(100,100);
		panel.add(descriptionLabel);
		
		descriptionText = new JTextArea();
		descriptionText.setLocation(150, 200);
		descriptionText.setSize(200, 70);
		descriptionText.setFont(new java.awt.Font("Ariel", Font.ANSI_CHARSET , 12));
		descriptionText.setLineWrap(true);
		descriptionText.setWrapStyleWord(true);
		panel.add(descriptionText);
		
		fatherLabel = new JLabel("<html><u>Father Catalog Number:</u></html>");
		fatherLabel.setLocation(30, 270);
		fatherLabel.setSize(120,100);
		panel.add(fatherLabel);
		
		model = new DefaultComboBoxModel<String>();
		List<String> catalogNumbers = db.getAllCatlogNumberOfCustomer((String) customerComboBox.getModel().getSelectedItem());
		for (String catalogNumber : catalogNumbers) 
			model.addElement(catalogNumber);
		
		model.setSelectedItem(null);
		
		fatherComboBox = new JComboBox<String>(model);
		fatherComboBox.setLocation(150, 310);
		fatherComboBox.setSize(150, 20);
		fatherComboBox.addActionListener(this);
		panel.add(fatherComboBox);
		
		quantityLabel = new JLabel("<html><u>Quantity:</u></html>");
		quantityLabel.setLocation(30, 340);
		quantityLabel.setSize(100,100);
		quantityLabel.setVisible(false);
		panel.add(quantityLabel);
		
		quantityText = new JTextField();
		quantityText.setLocation(150, 380);
		quantityText.setSize(150, 20);
		quantityText.setVisible(false);
		panel.add(quantityText);
		
		addProductButton = new JButton();
		addProductButton.setLocation(200, 420);
		addProductButton.setSize(80 , 40);
		addProductButton.addActionListener(this);
		addProductButton.setIcon(globals.okIcon);
		addProductButton.setFocusable(false);
		addProductButton.setContentAreaFilled(false);
		addProductButton.setPressedIcon(globals.clickOkIcon);
		addProductButton.setToolTipText("OK");
		panel.add(addProductButton);
		
		copyRight = new JLabel("<html><b>\u00a9 Naor Dalal</b></html>");
		copyRight.setLocation(30 , 430);
		copyRight.setSize(100,30);
		panel.add(copyRight);
		

	}

	@Override
	public void actionPerformed(ActionEvent event) 
	{
		if(event.getSource() == customerComboBox)
		{
			DefaultComboBoxModel<String> customerComboBoxModel = (DefaultComboBoxModel<String>) customerComboBox.getModel();
			if(customerComboBoxModel.getSelectedItem() != null)
			{
				List<String> catalogNumbers = db.getAllCatlogNumberOfCustomer((String) customerComboBoxModel.getSelectedItem());
				DefaultComboBoxModel<String> fatherComboBoxModel = (DefaultComboBoxModel<String>) fatherComboBox.getModel();
				fatherComboBoxModel.removeAllElements();
				for (String catalogNumber : catalogNumbers) 
					fatherComboBoxModel.addElement(catalogNumber);
				
				fatherComboBoxModel.setSelectedItem(null);
			}
		}
		else if(event.getSource() == fatherComboBox)
		{
			DefaultComboBoxModel<String> fatherComboBoxModel = (DefaultComboBoxModel<String>) fatherComboBox.getModel();
			if(fatherComboBoxModel.getSelectedItem() != null)
			{
				if(currentFatherCatalogNumber.equals(fatherComboBoxModel.getSelectedItem()))
				{
					fatherComboBoxModel.setSelectedItem(null);	
					quantityLabel.setVisible(false);
					quantityText.setVisible(false);
					quantityText.setText("");
					currentFatherCatalogNumber = "";
				}
				else
				{
					quantityLabel.setVisible(true);
					quantityText.setVisible(true);
					currentFatherCatalogNumber = (String) fatherComboBoxModel.getSelectedItem();
				}
			}
			else
			{
				quantityLabel.setVisible(false);
				quantityText.setVisible(false);
				quantityText.setText("");
				currentFatherCatalogNumber = "";
			}
		}
		else if(event.getSource() == addProductButton)
		{			
			if(catalogNumberText.getText().trim().equals(""))
			{
				JOptionPane.showConfirmDialog(null, "Please enter a catalog number","",JOptionPane.PLAIN_MESSAGE);
				return;
			}
			
			if(descriptionText.getText().trim().equals(""))
			{
				JOptionPane.showConfirmDialog(null, "Please enter a description","",JOptionPane.PLAIN_MESSAGE);
				return;
			}
			
			DefaultComboBoxModel<String> fatherComboBoxModel = (DefaultComboBoxModel<String>) fatherComboBox.getModel();
			if(fatherComboBoxModel.getSelectedItem() != null)
			{
				if(!org.apache.commons.lang3.StringUtils.isNumeric(quantityText.getText().trim()))
				{
					JOptionPane.showConfirmDialog(null, "Please enter a valid quantity","",JOptionPane.PLAIN_MESSAGE);
					return;
				}
			}
			
			String catalogNumber = catalogNumberText.getText().trim();
			DefaultComboBoxModel<String> customerComboBoxModel = (DefaultComboBoxModel<String>) customerComboBox.getModel();
			String customer = (String) customerComboBoxModel.getSelectedItem();
			String description = descriptionText.getText().trim();
			String father = (fatherComboBoxModel.getSelectedItem() == null) ? null : (String) fatherComboBoxModel.getSelectedItem();
			String quantity = (fatherComboBoxModel.getSelectedItem() == null) ? "0" : quantityText.getText().trim();
			
			db.addNewProduct(catalogNumber, customer, description, father, quantity);
			JOptionPane.showConfirmDialog(null, "Added successfully","",JOptionPane.PLAIN_MESSAGE);
			
		}
		
	}
}
