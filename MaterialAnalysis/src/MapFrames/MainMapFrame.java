package MapFrames;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.mail.Authenticator;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import com.toedter.calendar.JDateChooser;

import AnalyzerTools.Analyzer;
import AnalyzerTools.MapPrice;
import AnalyzerTools.MonthDate;
import AnalyzerTools.ProductColumn;
import Components.FilterCombo;
import Components.MultiSelectionComboBox;
import Forms.Forecast;
import MainPackage.CallBack;
import MainPackage.DataBase;
import MainPackage.Globals;
import MainPackage.Globals.FormType;
import Reports.MrpHeader;
import Reports.ProductInit;
import Reports.ProductInitHistory;
import Reports.Report;
import Reports.Tree;

public class MainMapFrame implements ActionListener 
{
	private CallBack<Integer> callBack;
	private Globals globals;
	private JFrame frame;
	private JPanel panel;
	private JButton mapButton;
	private JButton addForecastButton;
	private JButton addProductButton;
	private JButton initProductButton;
	private Analyzer analyzer;
	private String userName;
	private String email;
	private JLabel copyRight;
	private JButton treeViewButton;
	private JButton initProductViewButton;
	private JButton initProductHistoryViewButton;
	private DataBase db;
	private Authenticator auth;
	private JLabel lastLoadLabel;
	private JButton mrpHeaderViewButton;
	private JButton loadingReportsButton;
	private JButton forecastHistoryButton;
	private JButton deleteProductButton;
	private boolean calculateMap;
	private JButton mapPriceButton;
	private JButton customerDeviationFromObligoButton;

	public MainMapFrame(String userName, String email , Authenticator auth , CallBack<Integer> callBack) 
	{
		this.callBack = callBack;
		analyzer = new Analyzer();
		db = new DataBase();
		this.userName = userName;
		this.email = email;
		this.auth = auth;
		calculateMap = false;
		initialize();
	}

	private void initialize() 
	{
		globals = new Globals();
		
		frame = new JFrame("MAP-Version " + Globals.mapVersion);
		frame.setLayout(null);
		frame.getRootPane().setFocusable(true);
		frame.setBounds(400, 100, 500, 500);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) 
            {
            	frame.dispose();
                callBack.execute();
            }
        });
		frame.setResizable(false);
		frame.setIconImage(globals.frameImage);

		frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Cancel"); 
		frame.getRootPane().getActionMap().put("Cancel", new AbstractAction(){
			private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e)
	            {
					frame.dispose();
	                callBack.execute();
	            }
	        });

		panel = new JPanel();
		panel.setLocation(0 , 0);
		panel.setSize(500, 500);
		panel.setLayout(null);
		frame.add(panel);
		
		String lastLoadDate = (db.getLastLoad() == null) ? "No Loaded Yet" : Globals.dateWithoutHourToString(db.getLastLoad());
		lastLoadLabel = new JLabel("<html><b> Updated to date :  " + lastLoadDate + "</b></html>");
		lastLoadLabel.setLocation(10, 0);
		lastLoadLabel.setSize(200, 30);
		panel.add(lastLoadLabel);
		
		mapButton = new JButton("<html><b>Map View</b></html>");
		mapButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		mapButton.setHorizontalTextPosition(SwingConstants.CENTER);
		mapButton.setLocation(80 , 40);
		mapButton.setSize(130, 80);
		mapButton.addActionListener(this);
		mapButton.setFocusable(false);
		mapButton.setContentAreaFilled(false);
		mapButton.setIcon(globals.reprotIcon);
		mapButton.setToolTipText("Map View");
		mapButton.setPressedIcon(globals.clickReprotIcon);
		panel.add(mapButton);
		
		mrpHeaderViewButton = new JButton("<html><b>Mrp Header</b></html>");
		mrpHeaderViewButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		mrpHeaderViewButton.setHorizontalTextPosition(SwingConstants.CENTER);
		mrpHeaderViewButton.setLocation(290 , 40);
		mrpHeaderViewButton.setSize(130, 80);
		mrpHeaderViewButton.addActionListener(this);
		mrpHeaderViewButton.setFocusable(false);
		mrpHeaderViewButton.setContentAreaFilled(false);
		mrpHeaderViewButton.setIcon(globals.reprotIcon);
		mrpHeaderViewButton.setToolTipText("Mrp Header");
		mrpHeaderViewButton.setPressedIcon(globals.clickReprotIcon);
		panel.add(mrpHeaderViewButton);
		
		JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
		separator.setBackground(Color.black);
		separator.setLocation(0, 150);
		separator.setSize(500, 10);
		panel.add(separator);
		
		addForecastButton = new JButton("<html><b>Add<br>Forecast</b></html>");
		addForecastButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		addForecastButton.setHorizontalTextPosition(SwingConstants.CENTER);
		addForecastButton.setLocation(20 , 160);
		addForecastButton.setSize(100, 70);
		addForecastButton.addActionListener(this);
		addForecastButton.setFocusable(false);
		addForecastButton.setContentAreaFilled(false);
		addForecastButton.setIcon(globals.newIcon);
		addForecastButton.setToolTipText("Add Forecast");
		addForecastButton.setPressedIcon(globals.clickNewIcon);
		panel.add(addForecastButton);
		
		addProductButton = new JButton("<html><b>Add<br>Product</b></html>");
		addProductButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		addProductButton.setHorizontalTextPosition(SwingConstants.CENTER);
		addProductButton.setLocation(140 , 160);
		addProductButton.setSize(100, 70);
		addProductButton.addActionListener(this);
		addProductButton.setFocusable(false);
		addProductButton.setContentAreaFilled(false);
		addProductButton.setIcon(globals.newIcon);
		addProductButton.setToolTipText("Add Product");
		addProductButton.setPressedIcon(globals.clickNewIcon);
		panel.add(addProductButton);
		
		initProductButton = new JButton("<html><b>Init<br>Product</b></html>");
		initProductButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		initProductButton.setHorizontalTextPosition(SwingConstants.CENTER);
		initProductButton.setLocation(260 , 160);
		initProductButton.setSize(100, 70);
		initProductButton.addActionListener(this);
		initProductButton.setFocusable(false);
		initProductButton.setContentAreaFilled(false);
		initProductButton.setIcon(globals.initIcon);
		initProductButton.setToolTipText("Init Product");
		initProductButton.setPressedIcon(globals.clickInitIcon);
		panel.add(initProductButton);
		
		treeViewButton = new JButton("<html><b>Tree View</b></html>");
		treeViewButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		treeViewButton.setHorizontalTextPosition(SwingConstants.CENTER);
		treeViewButton.setLocation(380 , 160);
		treeViewButton.setSize(100, 70);
		treeViewButton.addActionListener(this);
		treeViewButton.setFocusable(false);
		treeViewButton.setContentAreaFilled(false);
		treeViewButton.setIcon(globals.productTreeIcon);
		treeViewButton.setToolTipText("Tree View");
		treeViewButton.setPressedIcon(globals.clickProductTreeIcon);
		panel.add(treeViewButton);
		
		separator = new JSeparator(SwingConstants.HORIZONTAL);
		separator.setBackground(Color.black);
		separator.setLocation(0, 240);
		separator.setSize(500, 10);
		panel.add(separator);
		
		initProductViewButton = new JButton("<html><b>Init Product View</b></html>");
		initProductViewButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		initProductViewButton.setHorizontalTextPosition(SwingConstants.CENTER);
		initProductViewButton.setLocation(20 , 250);
		initProductViewButton.setSize(100, 80);
		initProductViewButton.addActionListener(this);
		initProductViewButton.setFocusable(false);
		initProductViewButton.setContentAreaFilled(false);
		initProductViewButton.setIcon(globals.viewTableIcon);
		initProductViewButton.setToolTipText("Init Product View");
		initProductViewButton.setPressedIcon(globals.clickViewTableIcon);
		panel.add(initProductViewButton);
		
		initProductHistoryViewButton = new JButton("<html><b>Init Product History</b></html>");
		initProductHistoryViewButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		initProductHistoryViewButton.setHorizontalTextPosition(SwingConstants.CENTER);
		initProductHistoryViewButton.setLocation(140 , 250);
		initProductHistoryViewButton.setSize(100, 80);
		initProductHistoryViewButton.addActionListener(this);
		initProductHistoryViewButton.setFocusable(false);
		initProductHistoryViewButton.setContentAreaFilled(false);
		initProductHistoryViewButton.setIcon(globals.viewTableIcon);
		initProductHistoryViewButton.setToolTipText("Init Product History View");
		initProductHistoryViewButton.setPressedIcon(globals.clickViewTableIcon);
		panel.add(initProductHistoryViewButton);
		
		forecastHistoryButton = new JButton("<html><b>Forecast History View</b></html>");
		forecastHistoryButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		forecastHistoryButton.setHorizontalTextPosition(SwingConstants.CENTER);
		forecastHistoryButton.setLocation(260 , 250);
		forecastHistoryButton.setSize(100, 80);
		forecastHistoryButton.addActionListener(this);
		forecastHistoryButton.setFocusable(false);
		forecastHistoryButton.setContentAreaFilled(false);
		forecastHistoryButton.setIcon(globals.viewTableIcon);
		forecastHistoryButton.setToolTipText("Forecast History View");
		forecastHistoryButton.setPressedIcon(globals.clickViewTableIcon);
		panel.add(forecastHistoryButton);
		
		deleteProductButton = new JButton("<html><b>Delete Product</b></html>");
		deleteProductButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		deleteProductButton.setHorizontalTextPosition(SwingConstants.CENTER);
		deleteProductButton.setLocation(380 , 250);
		deleteProductButton.setSize(100, 80);
		deleteProductButton.addActionListener(this);
		deleteProductButton.setFocusable(false);
		deleteProductButton.setContentAreaFilled(false);
		deleteProductButton.setIcon(globals.deleteProductIcon);
		deleteProductButton.setToolTipText("Delete Product");
		deleteProductButton.setPressedIcon(globals.clickDeleteProductIcon);
		panel.add(deleteProductButton);
		
		separator = new JSeparator(SwingConstants.HORIZONTAL);
		separator.setBackground(Color.black);
		separator.setLocation(0, 340);
		separator.setSize(500, 10);
		panel.add(separator);
		
		mapPriceButton = new JButton("<html><b>Map<br>Price</b></html>");
		mapPriceButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		mapPriceButton.setHorizontalTextPosition(SwingConstants.CENTER);
		mapPriceButton.setLocation(50 , 350);
		mapPriceButton.setSize(100, 80);
		mapPriceButton.addActionListener(this);
		mapPriceButton.setFocusable(false);
		mapPriceButton.setContentAreaFilled(false);
		mapPriceButton.setIcon(globals.priceIcon);
		mapPriceButton.setToolTipText("Map Price");
		mapPriceButton.setPressedIcon(globals.clickPriceIcon);
		panel.add(mapPriceButton);
		
		customerDeviationFromObligoButton = new JButton("<html><b>Deviation From Obligo</b></html>");
		customerDeviationFromObligoButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		customerDeviationFromObligoButton.setHorizontalTextPosition(SwingConstants.CENTER);
		customerDeviationFromObligoButton.setLocation(200 , 350);
		customerDeviationFromObligoButton.setSize(100, 80);
		customerDeviationFromObligoButton.addActionListener(this);
		customerDeviationFromObligoButton.setFocusable(false);
		customerDeviationFromObligoButton.setContentAreaFilled(false);
		customerDeviationFromObligoButton.setIcon(globals.priceIcon);
		customerDeviationFromObligoButton.setToolTipText("Deviation From Obligo");
		customerDeviationFromObligoButton.setPressedIcon(globals.clickPriceIcon);
		panel.add(customerDeviationFromObligoButton);
		
		loadingReportsButton = new JButton("<html><b>Load<br>Reports</b></html>");
		loadingReportsButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		loadingReportsButton.setHorizontalTextPosition(SwingConstants.CENTER);
		loadingReportsButton.setLocation(350 , 350);
		loadingReportsButton.setSize(100, 80);
		loadingReportsButton.addActionListener(this);
		loadingReportsButton.setFocusable(false);
		loadingReportsButton.setContentAreaFilled(false);
		loadingReportsButton.setIcon(globals.loadReportsIcon);
		loadingReportsButton.setToolTipText("Load Reports");
		loadingReportsButton.setPressedIcon(globals.clickLoadReportsIcon);
		panel.add(loadingReportsButton);
	
		copyRight = new JLabel("<html><b>\u00a9 Naor Dalal</b></html>");
		copyRight.setLocation(30 , 430);
		copyRight.setSize(100,30);
		panel.add(copyRight);
		
		frame.setVisible(true);
		
	}

	@Override
	public void actionPerformed(ActionEvent event) 
	{	
		new Thread(() ->
		{
			if(event.getSource() == mapButton)
			{
				if(calculateMap)
				{
					JOptionPane.showConfirmDialog(null, "You already run MAP/MRP header , please wait for done","",JOptionPane.PLAIN_MESSAGE);
					return;
				}
				
				List<String> customers = db.getCustomersOfUser(userName);
				DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(customers.toArray(new String[customers.size()]));
				MultiSelectionComboBox<String> customerChoosen = new MultiSelectionComboBox<>(model);
				customerChoosen.setSelectedItem(null);
				int confirm = JOptionPane.showConfirmDialog(
				  null, customerChoosen, "Select customers", JOptionPane.PLAIN_MESSAGE);
				
				if(confirm != JOptionPane.OK_OPTION)
					return;
				
				if(customerChoosen.getSelectedItems().isEmpty())
				{
					JOptionPane.showConfirmDialog(null, "You have to select customer","",JOptionPane.PLAIN_MESSAGE);
					return;
				}
				
				calculateMap = true;
				
				Map<MonthDate, Map<String, ProductColumn>> map = analyzer.calculateMap(userName , true , customerChoosen.getSelectedItems() , null);
				String [] columns = analyzer.getColumns(map);
				String [][] rows = analyzer.getRows(map);
				List<Integer> invalidEditableCoulmns = analyzer.getInvalidEditableCoulmns(columns);
				
				boolean canEdit = invalidEditableCoulmns.size() < columns.length;
				ReportViewFrame mapFrame = new ReportViewFrame(email , auth , "Map View" , columns, rows, canEdit ,invalidEditableCoulmns);
				
				List<Integer> filterColumns = analyzer.getFilterColumns();
				List<String> filterNames = new ArrayList<>();
				filterColumns.stream().forEach(col -> filterNames.add(columns[col] + ": "));
				mapFrame.setFilters(filterColumns, filterNames);
				
				CallBack<Object> valueCellChangeAction = analyzer.getValueCellChangeAction(email, auth, userName, mapFrame, map);
				CallBack<Object> doubleLeftClickAction = analyzer.getDoubleLeftClickAction(email, auth, userName, mapFrame, map , customers);
				CallBack<Object> rightClickAction = analyzer.getRightClickAction(email, auth, userName, mapFrame, map);
				mapFrame.setCallBacks(valueCellChangeAction , doubleLeftClickAction, rightClickAction);
				
				mapFrame.show();
				
				calculateMap = false;

			}
			else if(event.getSource() == mapPriceButton)
			{
				List<String> customers = db.getCustomersOfUser(userName);
				DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(customers.toArray(new String[customers.size()]));
				MultiSelectionComboBox<String> customerChoosen = new MultiSelectionComboBox<>(model);
				customerChoosen.setSelectedItem(null);
				int confirm = JOptionPane.showConfirmDialog(
				  null, customerChoosen, "Select customers", JOptionPane.PLAIN_MESSAGE);
				
				if(confirm != JOptionPane.OK_OPTION)
					return;
				
				if(customerChoosen.getSelectedItems().isEmpty())
				{
					JOptionPane.showConfirmDialog(null, "You have to select customer","",JOptionPane.PLAIN_MESSAGE);
					return;
				}
				
				Map<MonthDate, Map<String, MapPrice>> mapPrice = analyzer.calculateMapPrice(userName, customerChoosen.getSelectedItems());
				String [] columns = analyzer.getColumnsOfMapPrice(mapPrice);
				String [][] rows = analyzer.getRowsOfMapPrice(mapPrice);
				List<Integer> invalidEditableCoulmns = IntStream.range(0, columns.length).boxed().collect(Collectors.toList());
				
				boolean canEdit = invalidEditableCoulmns.size() < columns.length;
				ReportViewFrame mapPriceFrame = new ReportViewFrame(email , auth , "Map Price View" , columns, rows, canEdit ,invalidEditableCoulmns);
				
				List<Integer> filterColumns = analyzer.getFilterColumnsOfMapPrice();
				List<String> filterNames = new ArrayList<>();
				filterColumns.stream().forEach(col -> filterNames.add(columns[col] + ": "));
				mapPriceFrame.setFilters(filterColumns, filterNames);
				mapPriceFrame.setCallBacks(null, analyzer.getDoubleLeftClickActionOfMapPrice(), null);
				
				mapPriceFrame.show();
			}
			else if(event.getSource() == addForecastButton)
			{
				new AddForecastFrame(userName , email , auth);
			}
			else if(event.getSource() == addProductButton)
			{
				new AddProductFrame(userName);
			}
			else if(event.getSource() == initProductButton)
			{
				List<FormType> formsType = new ArrayList<>();
				formsType.add(FormType.FC);
				formsType.add(FormType.WO);
				formsType.add(FormType.PO);
				formsType.add(FormType.SHIPMENT);
				new InitProductFrame(userName, formsType);
			}
			else if(event.getSource() == treeViewButton)
			{
				List<Tree> trees = db.getAllTrees(userName , null);
				ReportViewFrame treeFrame = createReportViewFrame(email , auth , userName, trees , "Tree View");

				treeFrame.show();
			}
			else if(event.getSource() == initProductViewButton)
			{
				List<ProductInit> productsInit = db.getAllProductsInit(userName);
				ReportViewFrame initProductFrame = createReportViewFrame(email , auth , userName , productsInit , "Init Product View");
				
				initProductFrame.show();
			}
			else if(event.getSource() == initProductHistoryViewButton)
			{
				List<ProductInitHistory> productsInitHistory = db.getAllProductsInitHistory(userName);
				ReportViewFrame initProductFrame = createReportViewFrame(email , auth , userName , productsInitHistory , "Init Product History View");
				
				initProductFrame.show();
			}
			else if(event.getSource() == mrpHeaderViewButton)
			{
				if(calculateMap)
				{
					JOptionPane.showConfirmDialog(null, "You already run MAP/MRP header , please wait for done","",JOptionPane.PLAIN_MESSAGE);
					return;
				}
				
				List<String> customers = db.getCustomersOfUser(userName);
				DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(customers.toArray(new String[customers.size()]));
				MultiSelectionComboBox<String> customerChoosen = new MultiSelectionComboBox<>(model);
				customerChoosen.setSelectedItem(null);
				int confirm = JOptionPane.showConfirmDialog(
				  null, customerChoosen, "Select customers", JOptionPane.PLAIN_MESSAGE);
				
				if(confirm != JOptionPane.OK_OPTION)
					return;
			
				if(customerChoosen.getSelectedItems().isEmpty())
				{
					JOptionPane.showConfirmDialog(null, "You have to select customer","",JOptionPane.PLAIN_MESSAGE);
					return;
				}
				
				calculateMap = true;
				
				List<MrpHeader> mrpHeaders = analyzer.getMrpHeaders(userName , customerChoosen.getSelectedItems());
				ReportViewFrame mrpHeaderFrame = createReportViewFrame(email , auth , userName , mrpHeaders , "Mrp Header");
				
				mrpHeaderFrame.show();
				
				calculateMap = false;
			}
			else if(event.getSource() == loadingReportsButton)
			{			
				Runtime runTime = Runtime.getRuntime();
				try 
				{
					runTime.exec("java -jar \"" + Globals.MapAnalyzerPath + "\" true");
				} 
				catch (IOException e1) 
				{
					e1.printStackTrace();
				}
			}
			else if(event.getSource() == deleteProductButton)
			{
				List<String> catalogNumbers = db.getAllCatalogNumbers(userName);
				DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
				JComboBox<String> catalogNumberComboBox = new FilterCombo(catalogNumbers,model, true);
				String message ="Catalog Number:\n";
				Object[] params = {message,catalogNumberComboBox};
				int confirm = JOptionPane.showConfirmDialog(null,params,"Delete Product", JOptionPane.PLAIN_MESSAGE);
				
				if(confirm != JOptionPane.OK_OPTION || catalogNumberComboBox.getSelectedItem() == null)
					JOptionPane.showConfirmDialog(null, "You have to choose catalog number from the list","",JOptionPane.PLAIN_MESSAGE);
				else
				{
					if(db.deleteProduct(catalogNumberComboBox.getSelectedItem().toString()))
						JOptionPane.showConfirmDialog(null, "Delete successfuly","",JOptionPane.PLAIN_MESSAGE);
					else
						JOptionPane.showConfirmDialog(null, "Cannot delete this product","",JOptionPane.PLAIN_MESSAGE);
				}
			}
			else if(event.getSource() == forecastHistoryButton)
			{
				JDateChooser fromDateChooser = new JDateChooser();
				String message ="Choose from date:\n";
				Object[] params = {message,fromDateChooser};
				int confirm = JOptionPane.showConfirmDialog(null,params,"From date", JOptionPane.PLAIN_MESSAGE);
				
				if(confirm != JOptionPane.OK_OPTION || fromDateChooser.getDate() == null)
				{
					JOptionPane.showConfirmDialog(null, "Please enter a from date","",JOptionPane.PLAIN_MESSAGE);
					return;
				}
				
				Date fromDate = fromDateChooser.getDate();
				
				JDateChooser toDateChooser = new JDateChooser();
				message ="Choose to date:\n";
				Object[] params2 = {message,toDateChooser};
				confirm = JOptionPane.showConfirmDialog(null,params2,"To date", JOptionPane.PLAIN_MESSAGE);
				
				if(confirm != JOptionPane.OK_OPTION || toDateChooser.getDate() == null)
				{
					JOptionPane.showConfirmDialog(null, "Please enter a to date","",JOptionPane.PLAIN_MESSAGE);
					return;
				}
				
				Date toDate = toDateChooser.getDate();
				
				List<Forecast> forecasts = db.getForecastBetweenDates(userName , fromDate, toDate);
				if(forecasts.size() == 0)
				{
					JOptionPane.showConfirmDialog(null, "There are no forecasts between those dates","",JOptionPane.PLAIN_MESSAGE);
					return;
				}
				
				ReportViewFrame frame = Analyzer.getFormsReportView(forecasts, email, auth);
				frame.setEditable(false);
				frame.setFrameName("Forecast History View");
				
				frame.show();
			}
			else if(event.getSource() == customerDeviationFromObligoButton)
			{
				List<String> customers = db.getCustomersOfUser(userName);
				DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(customers.toArray(new String[customers.size()]));
				MultiSelectionComboBox<String> customerChoosen = new MultiSelectionComboBox<>(model);
				customerChoosen.setSelectedItem(null);
				int confirm = JOptionPane.showConfirmDialog(
				  null, customerChoosen, "Select customers", JOptionPane.PLAIN_MESSAGE);
				
				if(confirm != JOptionPane.OK_OPTION)
					return;
			
				if(customerChoosen.getSelectedItems().isEmpty())
				{
					JOptionPane.showConfirmDialog(null, "You have to select customer","",JOptionPane.PLAIN_MESSAGE);
					return;
				}
				
				Map<MonthDate, Map<String, Double>> deviationFromObligo = analyzer.calculateCustomersDeviation(userName, customerChoosen.getSelectedItems());
				
				String [] columns = analyzer.getColumnsOfDeviationFromObligo(deviationFromObligo);
				String [][] rows = analyzer.getRowsOfDeviationFromObligo(deviationFromObligo);
				List<Integer> invalidEditableCoulmns = IntStream.range(0, columns.length).boxed().collect(Collectors.toList());
				
				boolean canEdit = invalidEditableCoulmns.size() < columns.length;
				ReportViewFrame customersDeviationFrame = new ReportViewFrame(email , auth , "Deviation From Obligo View" , columns, rows, canEdit ,invalidEditableCoulmns);
				
				List<Integer> filterColumns = IntStream.range(0, 1).boxed().collect(Collectors.toList());
				List<String> filterNames = new ArrayList<>();
				filterColumns.stream().forEach(col -> filterNames.add(columns[col] + ": "));
				customersDeviationFrame.setFilters(filterColumns, filterNames);
				customersDeviationFrame.setCallBacks(null, analyzer.getDoubleLeftClickActionOfDeviationReport(), null);
				
				customersDeviationFrame.show();
			}
			
		}).start();
		
	}
	
	public static ReportViewFrame createReportViewFrame(String email ,Authenticator auth , String userName , List<? extends Report> data , String frameName)
	{		
		String [] columns;
		String [][] rows;
		
		List<Integer> invalidEditableColumns = new ArrayList<Integer>();
		if(data.size() > 0)
		{
			columns = data.get(0).getColumns();
			invalidEditableColumns = data.get(0).getInvalidEditableColumns();
			rows = data.stream().map(t -> t.getRow()).toArray(String[][]::new);
		}
		else
		{
			columns = new String [0];
			rows = new String[0][0];
		}
		
		boolean canEdit = invalidEditableColumns.size() < columns.length;
		ReportViewFrame reportFrame = new ReportViewFrame(email , auth , frameName , columns, rows, canEdit, invalidEditableColumns);
		
		CallBack<Object> valueCellChangeAction = null;
		CallBack<Object> doubleLeftClickAction = null;
		CallBack<Object> rightClickAction = null;
		if(data.size() > 0)
		{
			valueCellChangeAction = data.get(0).getValueCellChangeAction(email ,auth ,userName, reportFrame, data);
			doubleLeftClickAction = data.get(0).getDoubleLeftClickAction(email ,auth ,userName, reportFrame, data);
			rightClickAction = data.get(0).getRightClickAction(email ,auth ,userName, reportFrame, data);
		}
		reportFrame.setCallBacks(valueCellChangeAction, doubleLeftClickAction, rightClickAction);
		
		List<Integer> filterColumns =  new ArrayList<>();
		if(data.size() > 0)
		{
			filterColumns.addAll(data.get(0).getFilterColumns());
		}
		
		List<String> filterNames = new ArrayList<>();
		if(data.size() > 0)
			filterColumns.stream().forEach(col -> filterNames.add(columns[col] + ": "));
		reportFrame.setFilters(filterColumns, filterNames);
		
		return reportFrame;
	}
}
