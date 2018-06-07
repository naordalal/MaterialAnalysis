package MapFrames;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.mail.Authenticator;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.ss.usermodel.Font;

import AnalyzerTools.Analyzer;
import AnalyzerTools.ForecastAnalyzer;
import Components.FilterCombo;
import MainPackage.DataBase;
import MainPackage.Globals;

public class AddForecastFrame extends KeyAdapter implements ActionListener
{
	private Globals globals;
	private JFrame frame;
	private JPanel panel;
	private Analyzer analyzer;
	private JComboBox<String> frameComboBox;
	private JLabel catalogNumberLabel;
	private JComboBox<String> catalogNumberComboBox;
	private JLabel quantityLabel;
	private JTextField quantityText;
	private JLabel requireDateLabel;
	private JTextField requireDateText;
	private JLabel notesLabel;
	private JTextArea notesText;
	private JButton addForecastButton;
	private DataBase db;
	private Map<String, String> productPerDescription;
	private JLabel copyRight;
	private String userName;
	private JLabel filePath;
	private AbstractButton attachFileButton;
	private Component attachFileLabel;
	private JFileChooser suppliersFileChooser;
	private String directoryPath;
	private File attachFile = null;
	private String email;
	private Authenticator auth;
	
	public AddForecastFrame(String userName , String email , Authenticator auth) 
	{
		this.userName = userName;
		this.email = email;
		this.auth = auth;
		analyzer = new Analyzer();
		db = new DataBase();
		initialize();
	}

	private void initialize() 
	{
		globals = new Globals();
		productPerDescription = db.getAllCatalogNumbersPerDescription(userName);
		
		frame = new JFrame("New Forecast");
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
		
		
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
		model.addElement("Single Forecast");
		model.addElement("Load balance forecast");
		model.addElement("Load new forecasts");
		
		frameComboBox = new JComboBox<>(model);
		frameComboBox.setLocation(30, 10);
		frameComboBox.setSize(150, 20);
		frameComboBox.addActionListener(this);
		panel.add(frameComboBox);
		
		catalogNumberLabel = new JLabel("<html><u>Catalog Number:</u></html>");
		catalogNumberLabel.setLocation(30,20);
		catalogNumberLabel.setSize(100,100);
		panel.add(catalogNumberLabel);
		
		
		model = new DefaultComboBoxModel<String>();
		List<String> catalogNumbers = productPerDescription.keySet().stream().collect(Collectors.toList());
		boolean clearWhenFocusLost = true;
		catalogNumberComboBox = new FilterCombo(catalogNumbers , model , clearWhenFocusLost);
		catalogNumberComboBox.setLocation(120, 60);
		catalogNumberComboBox.setSize(150, 20);
		catalogNumberComboBox.addActionListener(this);
		     
		panel.add(catalogNumberComboBox);
		
		quantityLabel = new JLabel("<html><u>Quantity:</u></html>");
		quantityLabel.setLocation(30,90);
		quantityLabel.setSize(100,100);
		panel.add(quantityLabel);
		
		quantityText = new JTextField();
		quantityText.setLocation(120, 130);
		quantityText.setSize(150, 20);
		panel.add(quantityText);
		
		requireDateLabel = new JLabel("<html><u>Require Date:</u></html>");
		requireDateLabel.setLocation(30,160);
		requireDateLabel.setSize(100,100);
		panel.add(requireDateLabel);
		
		requireDateText = new JTextField();
		requireDateText.setLocation(120, 200);
		requireDateText.setSize(150, 20);
		panel.add(requireDateText);
		
		notesLabel = new JLabel("<html><u>Notes:</u></html>");
		notesLabel.setLocation(30,230);
		notesLabel.setSize(100,100);
		panel.add(notesLabel);
		
		notesText = new JTextArea();
		notesText.setLocation(120, 270);
		notesText.setSize(200, 100);
		notesText.setFont(new java.awt.Font("Ariel", Font.ANSI_CHARSET , 12));
		notesText.setLineWrap(true);
		notesText.setWrapStyleWord(true);
		panel.add(notesText);
		
		addForecastButton = new JButton();
		addForecastButton.setLocation(200, 400);
		addForecastButton.setSize(80 , 40);
		addForecastButton.addActionListener(this);
		addForecastButton.setIcon(globals.okIcon);
		addForecastButton.setFocusable(false);
		addForecastButton.setContentAreaFilled(false);
		addForecastButton.setPressedIcon(globals.clickOkIcon);
		addForecastButton.setToolTipText("OK");
		panel.add(addForecastButton);
		
		
		attachFileLabel = new JLabel("<html><u>Attach Forecast File:</u></html>");
		attachFileLabel.setLocation(30,20);
		attachFileLabel.setSize(150,100);
		attachFileLabel.setVisible(false);
		panel.add(attachFileLabel);
		
		attachFileButton = new JButton();
		attachFileButton.setLocation(160 ,  45);
		attachFileButton.setSize(100, 40);
		attachFileButton.setIcon(globals.attachIcon);
		attachFileButton.setFocusable(false);
		attachFileButton.setContentAreaFilled(false);
		attachFileButton.setPressedIcon(globals.clickAttachIcon);
		attachFileButton.addActionListener(this);
		attachFileButton.setToolTipText("attachment");
		attachFileButton.setVisible(false);
		panel.add(attachFileButton);
		
		filePath = new JLabel("");
		filePath.setLocation(250 , 60);
		filePath.setSize(300, 20);
		filePath.setVisible(false);
		panel.add(filePath);
		
		
		copyRight = new JLabel("<html><b>\u00a9 Naor Dalal</b></html>");
		copyRight.setLocation(30 , 430);
		copyRight.setSize(100,30);
		panel.add(copyRight);
		
		frame.setVisible(true);
		
	}

	@Override
	public void actionPerformed(ActionEvent event) 
	{
		if(event.getSource() == addForecastButton)
		{
			String selectedFrame = (String) frameComboBox.getSelectedItem();
			
			switch(selectedFrame)
			{
				case "Single Forecast":
					addSingleForecast();
					break;
				case "Load balance forecast":
					loadBalanceForecast();
					break;
				case "Load new forecasts":
					loadNewForecasts();
			}
			
		}
		else if(event.getSource() == frameComboBox)
		{
			String selectedFrame = (String) frameComboBox.getSelectedItem();
			
			switch(selectedFrame)
			{
				case "Single Forecast":
					switchFrame(false);
					break;
				default:
					switchFrame(true);
					break;
			}
		}
		else if(event.getSource() == attachFileButton)
		{
			suppliersFileChooser = new JFileChooser();
			if(directoryPath != null)
				suppliersFileChooser.setCurrentDirectory(new File(directoryPath));
			int returnVal = suppliersFileChooser.showOpenDialog(null);
		    if (returnVal == JFileChooser.APPROVE_OPTION) {
		    	attachFile = suppliersFileChooser.getSelectedFile();
		        // What to do with the file, e.g. display it in a TextArea
				filePath.setText(attachFile.getAbsolutePath());
				directoryPath = attachFile.getPath();
		    } else {
		        System.out.println("File access cancelled by user.");
		    }
		}
		
	}
	
	private void loadNewForecasts() 
	{
		
		if(attachFile == null)
		{
			JOptionPane.showConfirmDialog(null, "Please enter a file","",JOptionPane.PLAIN_MESSAGE);
			return;
		}
		
		List<String> customers = db.getCustomersOfUser(userName);
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(customers.toArray(new String[customers.size()]));
		JComboBox<String> customerChoosen = new JComboBox<>(model);
		customerChoosen.setSelectedItem(null);
		int confirm = JOptionPane.showConfirmDialog(
		  null, customerChoosen, "Select customer", JOptionPane.PLAIN_MESSAGE);
		
		if(confirm != JOptionPane.OK_OPTION)
			return;
		
		if(customerChoosen.getSelectedItem() == null)
		{
			JOptionPane.showConfirmDialog(null, "You have to select customer","",JOptionPane.PLAIN_MESSAGE);
			return;
		}
		
		ForecastAnalyzer forecastAnalyzer = new ForecastAnalyzer();
		
		String[][] forecasts;
		try 
		{
			forecasts = forecastAnalyzer.getForecastQuantity(filePath.getText(), customerChoosen.getSelectedItem().toString());
		}
		catch (Exception e) 
		{
			JOptionPane.showConfirmDialog(null, "Wrong file format","",JOptionPane.PLAIN_MESSAGE);
			return;
		}
		
		String [] columns = forecasts[0];
		String [][] rows = ArrayUtils.subarray(forecasts, 1, forecasts.length);
		List<Integer> invalidEditableCoulmns = IntStream.range(0, columns.length).boxed().collect(Collectors.toList());
		
		boolean canEdit = invalidEditableCoulmns.size() < columns.length;
		ReportViewFrame forecastFrame = new ReportViewFrame(email , auth , "Balance Forecast View" , columns, rows, canEdit ,invalidEditableCoulmns);
		
		List<Integer> filterColumns = IntStream.range(0, 1).boxed().collect(Collectors.toList());
		List<String> filterNames = new ArrayList<>();
		filterColumns.stream().forEach(col -> filterNames.add(columns[col] + ": "));
		forecastFrame.setFilters(filterColumns, filterNames);
		
		forecastFrame.show();
	}

	private void loadBalanceForecast() 
	{
		if(attachFile == null)
		{
			JOptionPane.showConfirmDialog(null, "Please enter a file","",JOptionPane.PLAIN_MESSAGE);
			return;
		}
		
		List<String> customers = db.getCustomersOfUser(userName);
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(customers.toArray(new String[customers.size()]));
		JComboBox<String> customerChoosen = new JComboBox<>(model);
		customerChoosen.setSelectedItem(null);
		int confirm = JOptionPane.showConfirmDialog(
		  null, customerChoosen, "Select customer", JOptionPane.PLAIN_MESSAGE);
		
		if(confirm != JOptionPane.OK_OPTION)
			return;
		
		if(customerChoosen.getSelectedItem() == null)
		{
			JOptionPane.showConfirmDialog(null, "You have to select customer","",JOptionPane.PLAIN_MESSAGE);
			return;
		}
		
		ForecastAnalyzer forecastAnalyzer = new ForecastAnalyzer();
		try 
		{
			forecastAnalyzer.addForecast(filePath.getText(), customerChoosen.getSelectedItem().toString(), userName);
		} 
		catch (Exception e) 
		{
			JOptionPane.showConfirmDialog(null, "Wrong file format","",JOptionPane.PLAIN_MESSAGE);
			return;
		}
		
		JOptionPane.showConfirmDialog(null, "Added successfully","",JOptionPane.PLAIN_MESSAGE);
		frame.dispose();
		
	}

	private void addSingleForecast() 
	{
		if(catalogNumberComboBox.getSelectedItem() == null)
		{
			JOptionPane.showConfirmDialog(null, "Please select a Catalog Number","",JOptionPane.PLAIN_MESSAGE);
			return;
		}
		
		if(!org.apache.commons.lang3.math.NumberUtils.isCreatable(quantityText.getText().trim()))
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
		else if(requireDate.before(Globals.getTodayDate()))
		{
			JOptionPane.showConfirmDialog(null, "Please enter a require date later or equal to today's date","",JOptionPane.PLAIN_MESSAGE);
			return;
		}
		
		Date initDate = Globals.getTodayDate();
		String description = productPerDescription.get(catalogNumberComboBox.getSelectedItem());
		String customer = db.getCustomerOfCatalogNumber((String) catalogNumberComboBox.getSelectedItem());
		String quantity = quantityText.getText();
		String notes = notesText.getText();
		
		analyzer.addNewFC(customer, (String) catalogNumberComboBox.getSelectedItem(), quantity, Globals.dateWithoutHourToString(initDate), Globals.dateWithoutHourToString(requireDate), description, userName , notes);
		JOptionPane.showConfirmDialog(null, "Added successfully","",JOptionPane.PLAIN_MESSAGE);
		frame.dispose();
		
	}

	public void switchFrame(boolean attachFile)
	{
		catalogNumberLabel.setVisible(!attachFile);
		catalogNumberComboBox.setVisible(!attachFile);
		quantityLabel.setVisible(!attachFile);
		quantityText.setVisible(!attachFile);
		requireDateLabel.setVisible(!attachFile);
		requireDateText.setVisible(!attachFile);
		notesLabel.setVisible(!attachFile);
		notesText.setVisible(!attachFile);
		
		attachFileLabel.setVisible(attachFile);
		attachFileButton.setVisible(attachFile);
		filePath.setVisible(attachFile);
		filePath.setText("");
		this.attachFile = null;
	}
	


}
