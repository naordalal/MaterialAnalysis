package Frames;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints.Key;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import org.apache.poi.ss.usermodel.Font;

import AnalyzerTools.Analyzer;
import mainPackage.DataBase;
import mainPackage.Globals;

public class AddForecastFrame extends KeyAdapter implements ActionListener
{
	private Globals globals;
	private JFrame frame;
	private JPanel panel;
	private Analyzer analyzer;
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
	private String currentCatalogNumber;
	private JLabel copyRight;
	
	public AddForecastFrame() 
	{
		analyzer = new Analyzer();
		db = new DataBase();
		initialize();
	}

	private void initialize() 
	{
		globals = new Globals();
		productPerDescription = db.getAllCatalogNumbers();
		currentCatalogNumber = "";
		
		frame = new JFrame("New Forecast");
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
		
		
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
		List<String> catalogNumbers = productPerDescription.keySet().stream().collect(Collectors.toList());
		catalogNumberComboBox = new FilterCombo(catalogNumbers , model);
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
		
		copyRight = new JLabel("<html><b>\u00a9 Naor Dalal</b></html>");
		copyRight.setLocation(30 , 430);
		copyRight.setSize(100,30);
		panel.add(copyRight);
		
	}

	@Override
	public void actionPerformed(ActionEvent event) 
	{
		if(event.getSource() == addForecastButton)
		{
			if(currentCatalogNumber.equals(""))
			{
				JOptionPane.showConfirmDialog(null, "Please select a Catalog Number","",JOptionPane.PLAIN_MESSAGE);
				return;
			}
			
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
			else if(requireDate.before(Globals.getTodayDate()))
			{
				JOptionPane.showConfirmDialog(null, "Please enter a require date later or equal to today's date","",JOptionPane.PLAIN_MESSAGE);
				return;
			}
			
			Date initDate = Globals.getTodayDate();
			String description = productPerDescription.get(currentCatalogNumber);
			String customer = db.getCustomerOfCatalogNumber(currentCatalogNumber);
			String quantity = quantityText.getText();
			String notes = notesText.getText();
			
			analyzer.addNewFC(customer, currentCatalogNumber, quantity, Globals.dateWithoutHourToString(initDate), Globals.dateWithoutHourToString(requireDate), description, notes);
			JOptionPane.showConfirmDialog(null, "Added successfully","",JOptionPane.PLAIN_MESSAGE);
		}
		else if(event.getSource() == catalogNumberComboBox)
		{
			String catalogNumber = (String)catalogNumberComboBox.getModel().getSelectedItem();
			if(currentCatalogNumber.equals(catalogNumber) || catalogNumber == null)
			{
				currentCatalogNumber = "";
				catalogNumberComboBox.setSelectedIndex(-1);
			}
			else
				currentCatalogNumber = catalogNumber;
		}
		
	}


}
