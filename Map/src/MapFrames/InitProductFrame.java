package MapFrames;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
import Components.FilterCombo;
import Forms.Form;
import MainPackage.DataBase;
import MainPackage.Globals;
import MainPackage.Globals.FormType;

public class InitProductFrame implements ActionListener 
{
	private JFrame frame;
	private JPanel panel;
	private String userName;
	private JComboBox<String> initTypeComboBox;
	private JLabel catalogNumberLabel;
	private FilterCombo catalogNumberComboBox;
	private JLabel quantityLabel;
	private JTextField quantityText;
	private JLabel requireDateLabel;
	private JTextField requireDateText;
	private Analyzer analyzer;
	private DataBase db;
	private Globals globals;
	private JButton addInitButton;
	private List<FormType> formsTypeThatNeedInit;
	private List<FormType> formsTypeThatNotNeedInit;
	private List<FormType> formsThatAlreadyInit;
	private String currentCatalogNumber;
	
	public InitProductFrame(String userName , List<FormType> formsType) 
	{
		this.userName = userName;
		globals = new Globals();
		this.formsTypeThatNeedInit = formsType.stream().filter(type -> Form.isNeedInit(globals.getClassName(type))).collect(Collectors.toList());
		List<FormType> formsTypeThatNeedInitThanNotNeedRequireDate = formsTypeThatNeedInit.stream().filter(type -> !Form.isNeedRequireDate(globals.getClassName(type))).collect(Collectors.toList());
		
		this.formsTypeThatNeedInit.removeAll(formsTypeThatNeedInitThanNotNeedRequireDate);
		this.formsTypeThatNeedInit.addAll(formsTypeThatNeedInitThanNotNeedRequireDate);
		formsType.removeAll(formsTypeThatNeedInit);
		this.formsTypeThatNotNeedInit = formsType;
		
		analyzer = new Analyzer();
		db = new DataBase();
		currentCatalogNumber = "";
		formsThatAlreadyInit = new ArrayList<FormType>();
		initialize();
	}

	private void initialize() 
	{
		
		frame = new JFrame("Init Product");
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
		
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
		//initTypeComboBox = new JLabel("<html><u><b>Init " + globals.FormTypeToString(formsTypeThatNeedInit.get(0)) + ":</b></u></html>");
		for (FormType formType : formsTypeThatNeedInit) 
		{
			model.addElement(globals.FormTypeToString(formType));
		}
		
		initTypeComboBox = new JComboBox<>(model);
		initTypeComboBox.setLocation(10,20);
		initTypeComboBox.setSize(120,20);
		initTypeComboBox.addActionListener(this);
		panel.add(initTypeComboBox);
		
		catalogNumberLabel = new JLabel("<html><u>Catalog Number:</u></html>");
		catalogNumberLabel.setLocation(30,40);
		catalogNumberLabel.setSize(100,100);
		panel.add(catalogNumberLabel);
		
		List<String> catalogNumbers = db.getAllCatalogNumbers(userName);
		model = new DefaultComboBoxModel<String>();
		
		boolean clearWhenFocusLost = true;
		catalogNumberComboBox = new FilterCombo(catalogNumbers , model, clearWhenFocusLost);
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
		requireDateLabel.setVisible(Form.isNeedRequireDate(globals.getClassName(formsTypeThatNeedInit.get(initTypeComboBox.getSelectedIndex()))));
		panel.add(requireDateLabel);
		
		requireDateText = new JTextField();
		requireDateText.setLocation(120, 220);
		requireDateText.setSize(150, 20);
		requireDateText.setVisible(Form.isNeedRequireDate(globals.getClassName(formsTypeThatNeedInit.get(initTypeComboBox.getSelectedIndex()))));
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
		
		frame.setVisible(true);
	}


	private void close() 
	{
		if(formsThatAlreadyInit.size() != 0)
		{
			JOptionPane.showConfirmDialog(null, "You are not done yet , you have to init all categories","",JOptionPane.PLAIN_MESSAGE);
			return;
		}
		else 
			frame.dispose();
	}

	@Override
	public void actionPerformed(ActionEvent event) 
	{
		if(event.getSource() == addInitButton)
		{
			if(!org.apache.commons.lang3.math.NumberUtils.isCreatable(quantityText.getText().trim()))
			{
				JOptionPane.showConfirmDialog(null, "Please enter a valid quantity","",JOptionPane.PLAIN_MESSAGE);
				return;
			}
			
			if(Form.isNeedRequireDate(globals.getClassName(formsTypeThatNeedInit.get(initTypeComboBox.getSelectedIndex()))))
			{
				Date requireDate = Globals.isValidDate(requireDateText.getText()); 
				if(requireDate == null)
				{
					JOptionPane.showConfirmDialog(null, "Please enter a valid require date","",JOptionPane.PLAIN_MESSAGE);
					return;
				}
			}	
			
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			String catalogNumber = (String) catalogNumberComboBox.getModel().getSelectedItem();
			analyzer.cleanProductQuantityPerDate(catalogNumber);
			
			String initDate = Globals.dateWithoutHourToString(Globals.getTodayDate());
			String quantity = quantityText.getText().trim();
			String requireDateString = (Form.isNeedRequireDate(globals.getClassName(formsTypeThatNeedInit.get(initTypeComboBox.getSelectedIndex()))))
					? requireDateText.getText() : Globals.dateWithoutHourToString(Globals.getTodayDate());
			analyzer.addNewInitProductCustomerOrders(catalogNumber, initDate, quantity, requireDateString , formsTypeThatNeedInit.get(initTypeComboBox.getSelectedIndex()));
			
			
			quantityText.setText("");
			requireDateText.setText("");
			quantityText.requestFocusInWindow();
			catalogNumberComboBox.setEnabled(false);
			
			if(!formsThatAlreadyInit.contains(formsTypeThatNeedInit.get(initTypeComboBox.getSelectedIndex())))
				formsThatAlreadyInit.add(formsTypeThatNeedInit.get(initTypeComboBox.getSelectedIndex()));
			
			if(formsThatAlreadyInit.size() == formsTypeThatNeedInit.size())
			{
				for (FormType formType : formsTypeThatNotNeedInit) 
				{
					analyzer.addNewInitProductCustomerOrders(catalogNumber, initDate, "0", initDate , formType);
				}
				
				JOptionPane.showConfirmDialog(null, "Init successfully","",JOptionPane.PLAIN_MESSAGE);
				formsThatAlreadyInit.clear();
				catalogNumberComboBox.setEnabled(true);
				initTypeComboBox.setSelectedIndex(0);
			}
			
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
		}
		else if(event.getSource() == initTypeComboBox)
		{
			requireDateText.setVisible(Form.isNeedRequireDate(globals.getClassName(formsTypeThatNeedInit.get(initTypeComboBox.getSelectedIndex()))));
			requireDateLabel.setVisible(Form.isNeedRequireDate(globals.getClassName(formsTypeThatNeedInit.get(initTypeComboBox.getSelectedIndex()))));
		}
		else if(event.getSource() == catalogNumberComboBox)
		{
			String catalogNumber = (String)catalogNumberComboBox.getModel().getSelectedItem();
			if(catalogNumber == null || currentCatalogNumber.equals(catalogNumber))
			{
				currentCatalogNumber = "";
				catalogNumberComboBox.setSelectedIndex(-1);
			}
			else
			{
				currentCatalogNumber = catalogNumber;
			}
		}
	}
	
}
