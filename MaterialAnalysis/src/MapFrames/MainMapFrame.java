package MapFrames;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.mail.Authenticator;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import AnalyzerTools.Analyzer;
import AnalyzerTools.MonthDate;
import AnalyzerTools.ProductColumn;
import MainPackage.CallBack;
import MainPackage.DataBase;
import MainPackage.Globals;
import MainPackage.Globals.FormType;
import Reports.MrpHeader;
import Reports.ProductInit;
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
	private DataBase db;
	private Authenticator auth;
	private JLabel lastLoadLabel;
	private JButton mrpHeaderViewButton;
	private JButton loadingReportsButton;

	public MainMapFrame(String userName, String email , Authenticator auth , CallBack<Integer> callBack) 
	{
		this.callBack = callBack;
		analyzer = new Analyzer();
		db = new DataBase();
		this.userName = userName;
		this.email = email;
		this.auth = auth;
		initialize();
	}

	private void initialize() 
	{
		globals = new Globals();
		
		frame = new JFrame("MAP");
		frame.setLayout(null);
		frame.getRootPane().setFocusable(true);
		frame.setBounds(400, 100, 505, 500);
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
		panel.setSize(505, 500);
		panel.setLayout(null);
		frame.add(panel);
		
		String lastLoadDate = (db.getLastLoad() == null) ? "No Loaded Yet" : Globals.dateWithoutHourToString(db.getLastLoad());
		lastLoadLabel = new JLabel("<html><b> Updated to date :  " + lastLoadDate + "</b></html>");
		lastLoadLabel.setLocation(10, 0);
		lastLoadLabel.setSize(200, 30);
		panel.add(lastLoadLabel);
		
		mapButton = new JButton("<html><b>Map View</b></html>");
		mapButton.setLocation(20 , 30);
		mapButton.setSize(100, 60);
		mapButton.addActionListener(this);
		panel.add(mapButton);
		
		addForecastButton = new JButton("<html><b>Add Forecast</b></html>");
		addForecastButton.setLocation(140 , 30);
		addForecastButton.setSize(100, 60);
		addForecastButton.addActionListener(this);
		panel.add(addForecastButton);
		
		addProductButton = new JButton("<html><b>Add Product</b></html>");
		addProductButton.setLocation(260 , 30);
		addProductButton.setSize(100, 60);
		addProductButton.addActionListener(this);
		panel.add(addProductButton);
		
		initProductButton = new JButton("<html><b>Init Product</b></html>");
		initProductButton.setLocation(380 , 30);
		initProductButton.setSize(100, 60);
		initProductButton.addActionListener(this);
		panel.add(initProductButton);
		
		treeViewButton = new JButton("<html><b>Tree View</b></html>");
		treeViewButton.setLocation(20 , 100);
		treeViewButton.setSize(100, 60);
		treeViewButton.addActionListener(this);
		panel.add(treeViewButton);
		
		initProductViewButton = new JButton("<html><b>Init Product View</b></html>");
		initProductViewButton.setLocation(140 , 100);
		initProductViewButton.setSize(100, 60);
		initProductViewButton.addActionListener(this);
		panel.add(initProductViewButton);
		
		mrpHeaderViewButton = new JButton("<html><b>Mrp Header</b></html>");
		mrpHeaderViewButton.setLocation(260 , 100);
		mrpHeaderViewButton.setSize(100, 60);
		mrpHeaderViewButton.addActionListener(this);
		panel.add(mrpHeaderViewButton);
		
		loadingReportsButton = new JButton("<html><b>Load Reports</b></html>");
		loadingReportsButton.setLocation(380 , 100);
		loadingReportsButton.setSize(100, 60);
		loadingReportsButton.addActionListener(this);
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
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		if(event.getSource() == mapButton)
		{
			Map<MonthDate, Map<String, ProductColumn>> map = analyzer.calculateMap(userName);
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
			CallBack<Object> doubleLeftClickAction = analyzer.getDoubleLeftClickAction(email, auth, userName, mapFrame, map);
			CallBack<Object> rightClickAction = analyzer.getRightClickAction(email, auth, userName, mapFrame, map);
			mapFrame.setCallBacks(valueCellChangeAction , doubleLeftClickAction, rightClickAction);
			
			mapFrame.show();

		}
		else if(event.getSource() == addForecastButton)
		{
			new AddForecastFrame(userName);
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
			ReportViewFrame treeFrame = createReportViewFrame(trees , "Tree View");

			treeFrame.show();
		}
		else if(event.getSource() == initProductViewButton)
		{
			List<ProductInit> productsInit = db.getAllProductsInit(userName);
			ReportViewFrame initProductFrame = createReportViewFrame(productsInit , "Init Product View");
			
			initProductFrame.show();
		}
		else if(event.getSource() == mrpHeaderViewButton)
		{
			List<MrpHeader> mrpHeaders = analyzer.getMrpHeaders(userName);
			ReportViewFrame mrpHeaderFrame = createReportViewFrame(mrpHeaders , "Mrp Header");
			
			mrpHeaderFrame.show();
		}
		else if(event.getSource() == loadingReportsButton)
		{			
			Runtime runTime = Runtime.getRuntime();
			try 
			{
				runTime.exec("java -jar \"" + Globals.MapAnalyzerPath + "\"");
			} 
			catch (IOException e1) 
			{
				e1.printStackTrace();
			}
		}
		
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		
	}
	
	public ReportViewFrame createReportViewFrame(List<? extends Report> data , String frameName)
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
			valueCellChangeAction = data.get(0).getValueCellChangeAction(userName, reportFrame, data);
			doubleLeftClickAction = data.get(0).getDoubleLeftClickAction(userName, reportFrame, data);
			rightClickAction = data.get(0).getRightClickAction(userName, reportFrame, data);
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
