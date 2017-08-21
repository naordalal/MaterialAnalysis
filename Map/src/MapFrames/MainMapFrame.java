package MapFrames;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import MainPackage.CallBack;
import MainPackage.Globals;

public class MainMapFrame implements ActionListener 
{
	private CallBack<Integer> callBack;
	private Globals globals;
	private JFrame frame;
	private JPanel panel;
	private JButton mapButton;
	private JButton addForecastButton;
	private JButton addProductButton;
	private Analyzer analyzer;
	private String userName;
	private JLabel copyRight;

	public MainMapFrame(String userName, CallBack<Integer> callBack) 
	{
		this.callBack = callBack;
		analyzer = new Analyzer();
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
		
		mapButton = new JButton("<html><b>Map View</b></html>");
		mapButton.setLocation(30 , 30);
		mapButton.setSize(100, 60);
		mapButton.addActionListener(this);
		panel.add(mapButton);
		
		addForecastButton = new JButton("<html><b>Add Forecast</b></html>");
		addForecastButton.setLocation(150 , 30);
		addForecastButton.setSize(100, 60);
		addForecastButton.addActionListener(this);
		panel.add(addForecastButton);
		
		addProductButton = new JButton("<html><b>Add Product</b></html>");
		addProductButton.setLocation(270 , 30);
		addProductButton.setSize(100, 60);
		addProductButton.addActionListener(this);
		panel.add(addProductButton);
		
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
			ReportViewFrame mapFrame = new ReportViewFrame(columns, rows, canEdit , new ArrayList<Integer>());
			
			mapFrame.setCallBacks(getValueCellChangeAction(), getDoubleLeftClickAction(map, mapFrame), getRightClickAction());
			
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
		
	}
	
	public CallBack<Object> getValueCellChangeAction()
	{
		CallBack<Object> valueCellChangeAction = new CallBack<Object>()
		{
			@Override
			public Object execute(Object... objects) 
			{
		        return null;
			}
		};
		
		return valueCellChangeAction;
	}
	
	public CallBack<Object> getRightClickAction()
	{
		CallBack<Object> rightClickAction = new CallBack<Object>()
		{
			@Override
			public Object execute(Object... objects) 
			{
				return null;
			}
		};
		
		return rightClickAction;
	}
	
	public CallBack<Object> getDoubleLeftClickAction(Map<MonthDate, Map<String, ProductColumn>> map , ReportViewFrame mapFrame)
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
				ReportViewFrame reportViewFrame = new ReportViewFrame(columns, rows, canEdit , forms.get(0).getInvalidEditableColumns());
				
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
}
