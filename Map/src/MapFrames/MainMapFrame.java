package MapFrames;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import AnalyzerTools.Analyzer;
import AnalyzerTools.MonthDate;
import AnalyzerTools.ProductColumn;
import Components.TableCellListener;
import Forms.Form;
import Forms.Tree;
import MainPackage.CallBack;
import MainPackage.DataBase;
import MainPackage.Globals;
import MainPackage.Globals.FormType;
import MainPackage.Message;

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
	private JLabel copyRight;
	private JButton treeViewButton;
	private DataBase db;

	public MainMapFrame(String userName, CallBack<Integer> callBack) 
	{
		this.callBack = callBack;
		analyzer = new Analyzer();
		db = new DataBase();
		this.userName = userName;
		initialize();
	}

	private void initialize() 
	{
		globals = new Globals();
		
		frame = new JFrame("MAP");
		frame.setVisible(true);
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
		
		copyRight = new JLabel("<html><b>\u00a9 Naor Dalal</b></html>");
		copyRight.setLocation(30 , 430);
		copyRight.setSize(100,30);
		panel.add(copyRight);
	}

	@Override
	public void actionPerformed(ActionEvent event) 
	{
		if(event.getSource() == mapButton)
		{
			Map<MonthDate, Map<String, ProductColumn>> map = analyzer.calculateMap(userName);
			String [] columns = analyzer.getColumns(map);
			String [][] rows = analyzer.getRows(map);
			
			boolean canEdit = false;
			ReportViewFrame mapFrame = new ReportViewFrame("Map View" , columns, rows, canEdit , new ArrayList<Integer>());
			
			List<Integer> filterColumns = analyzer.getFilterColumns();
			List<String> filterNames = new ArrayList<>();
			filterColumns.stream().forEach(col -> filterNames.add(columns[col] + ": "));
			mapFrame.setFilters(filterColumns, filterNames);
			
			mapFrame.setCallBacks(null , getMapDoubleLeftClickAction(map, mapFrame), null);
			
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
			String [] columns;
			String [][] rows;
			
			List<Integer> invalidEditableColumns = new ArrayList<Integer>();
			if(trees.size() > 0)
			{
				columns = trees.get(0).getColumns();
				invalidEditableColumns = trees.get(0).getInvalidEditableColumns();
				rows = trees.stream().map(t -> t.getRow()).toArray(String[][]::new);
			}
			else
			{
				columns = new String [0];
				rows = new String[0][0];
			}
			
			boolean canEdit = true;
			ReportViewFrame treeFrame = new ReportViewFrame("Tree View" , columns, rows, canEdit, invalidEditableColumns);
			treeFrame.setCallBacks(getTreeValueCellChangeAction(treeFrame, trees), null, null);
			
			List<Integer> filterColumns = Tree.getFilterColumns();
			List<String> filterNames = new ArrayList<>();
			if(trees.size() > 0)
				filterColumns.stream().forEach(col -> filterNames.add(columns[col] + ": "));
			treeFrame.setFilters(filterColumns, filterNames);
			treeFrame.show();
		}
		
	}
		
	public CallBack<Object> getMapDoubleLeftClickAction(Map<MonthDate, Map<String, ProductColumn>> map , ReportViewFrame mapFrame)
	{
		CallBack<Object> doubleLeftClickAction = new CallBack<Object>()
		{
			@Override
			public Object execute(Object... objects) 
			{
				TableCellListener tcl = (TableCellListener)objects[0];
				int row = tcl.getRow();
				int column = tcl.getColumn();
				if(column < Analyzer.ConstantColumnsCount)
					return null;
				
				String monthOnShortName = tcl.getTable().getColumnName(column);
				String product = (String) analyzer.getProductOnRow(tcl.getTable() , row);
				MonthDate monthDate = new MonthDate(monthOnShortName);
				ProductColumn productColumn = map.get(monthDate).get(product);
				String category = productColumn.getColumn(row % productColumn.getCategoriesCount());
				List<? extends Form> forms = analyzer.getFormsFromCell(map , product , monthDate , category);
				
				if(forms == null || forms.size() == 0)
					return null;
				
				String [] columns = forms.get(0).getColumns();
				String [][] rows = new String[forms.size()][columns.length];
				int index = 0;
				for (Form form : forms) 
				{
					rows[index] = form.getRow();
					index++;
				}
				
				boolean canEdit = forms.get(0).canEdit();
				ReportViewFrame reportViewFrame = new ReportViewFrame("Reports View" , columns, rows, canEdit , forms.get(0).getInvalidEditableColumns());
				
				List<Integer> filterColumns = forms.get(0).getFilterColumns();
				List<String> filterNames = new ArrayList<>();
				filterColumns.stream().forEach(col -> filterNames.add(columns[col] + ": "));
				reportViewFrame.setFilters(filterColumns, filterNames);
				
				CallBack<Object> valueCellChangeAction = new CallBack<Object>()
				{
					@Override
					public Object execute(Object... objects) 
					{
						TableCellListener tcl = (TableCellListener)objects[0];
						int row = tcl.getRow();
						int column = tcl.getColumn();
						String newValue = (String) tcl.getNewValue();
						String oldValue = (String) tcl.getOldValue();
						Form updateForm = forms.get(row);
						
						try 
						{
							updateForm.updateValue(column , newValue);
							mapFrame.refresh(analyzer.getRows(analyzer.calculateMap(userName)));
							reportViewFrame.setColumnWidth();
							return null;
						} catch (Exception e) 
						{
							reportViewFrame.updateCellValue(row,column,oldValue);
							JOptionPane.showConfirmDialog(null, e.getMessage() ,"Error",JOptionPane.PLAIN_MESSAGE);
							return e;
						}
					}
				};
				reportViewFrame.setCallBacks(valueCellChangeAction, null, null);
				reportViewFrame.show();
		        return null;
			}
		};
		
		return doubleLeftClickAction;
	}
	
	public CallBack<Object> getTreeValueCellChangeAction(ReportViewFrame treeFrame , List<Tree> trees)
	{
		CallBack<Object> valueCellChangeAction = new CallBack<Object>()
		{
			@Override
			public Object execute(Object... objects) 
			{
				TableCellListener tcl = (TableCellListener)objects[0];
				int row = tcl.getRow();
				int column = tcl.getColumn();
				String newValue = (String) tcl.getNewValue();
				String oldValue = (String) tcl.getOldValue();
				Tree tree = trees.get(row);
				Message message;
				try
				{
					message = tree.updateValue(userName , column , newValue);
				} 
				catch (Exception e) 
				{
					treeFrame.updateCellValue(row,column,oldValue);
					JOptionPane.showConfirmDialog(null, e.getMessage() ,"Error",JOptionPane.PLAIN_MESSAGE);
					return e;
				}
				
				while(message != null)
				{
					boolean validInput = false;
					while(!validInput)
					{
						String answer = JOptionPane.showInputDialog(null ,message.getMessage(), "" , JOptionPane.OK_OPTION);
						if(answer != null)
						{
							try 
							{
								message = tree.updateValue(userName , message.getColumn() , answer.trim());
								validInput = true;
							} catch (Exception e) 
							{
								JOptionPane.showConfirmDialog(null, e.getMessage() ,"Error",JOptionPane.PLAIN_MESSAGE);
							}
						}

					}
						
				}
										
				treeFrame.refresh(db.getAllTrees(userName , null).stream().map(t -> t.getRow()).toArray(String[][]::new));
				treeFrame.setColumnWidth();
				
				return null;
			}
		};
		
		return valueCellChangeAction;
	}
}
