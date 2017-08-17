package Frames;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import mainPackage.CallBack;
import mainPackage.Globals;

public class ReportViewFrame 
{
	private JFrame frame;
	private Globals globals;
	private JPanel panel;
	private MyJTable table;
	private JScrollPane scrollPane;
	private String[] columns;
	private String[][] content;
	private CallBack<Object> valueCellChangeAction;
	private CallBack<Object> doubleLeftClickAction;
	private CallBack<Object> rightClickAction;
	private boolean canEdit;
	private List<Integer> invalidEditableColumns;

	public ReportViewFrame(String [] columns , String [][] content , boolean canEdit , List<Integer> invalidEditableColumns) 
	{
		this.columns = columns;
		this.content = content;
		this.globals = new Globals();
		this.canEdit = canEdit;
		this.invalidEditableColumns = invalidEditableColumns;
	}

	public void setCallBacks(CallBack<Object> valueCellChangeAction , CallBack<Object> doubleLeftClickAction ,CallBack<Object> rightClickAction)
	{
		this.valueCellChangeAction = valueCellChangeAction;
		this.doubleLeftClickAction = doubleLeftClickAction;
		this.rightClickAction = rightClickAction;
	}
	
	public void show()
	{
		initialize();
	}
	
	private void initialize() 
	{
		
		frame = new JFrame("MAP VIEW");
		frame.setVisible(true);
		frame.setLayout(null);
		frame.getRootPane().setFocusable(true);
		frame.setBounds(300, 100, 900, 780);
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
		panel.setSize(900, 780);
		panel.setLayout(null);
		frame.add(panel);
		
		DefaultTableModel model = new DefaultTableModel();
		
		
		table = new MyJTable(model , canEdit);
		if(canEdit)
			invalidEditableColumns.stream().forEach(column -> table.addInvalidEditableColumn(column));
		createTable(model);
		table.setRowHeight(30);
		table.setShowGrid(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		
		setRenderer(centerRenderer);

		//table.getTableHeader().setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		//table.getTableHeader().setReorderingAllowed(false);
		
		scrollPane = new JScrollPane(table);
		scrollPane.setLocation(30, 30);
		scrollPane.setSize(850,600);
		scrollPane.setVisible(true);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel.add(scrollPane);
		
		/*valueCellChangeAction = new CallBack<Object>()
		{
			@Override
			public Object execute(Object... objects) 
			{
		        return null;
			}
		};
		
		doubleLeftClickAction = new CallBack<Object>()
		{
			@Override
			public Object execute(Object... objects) 
			{
				TableCellListener tcl = (TableCellListener)objects[0];
				int row = tcl.getRow();
				int column = tcl.getColumn();
		        return null;
			}
		};
		
		rightClickAction = new CallBack<Object>()
		{
			@Override
			public Object execute(Object... objects) 
			{
				return null;
			}
		};*/
		
		new TableCellListener(table, valueCellChangeAction, doubleLeftClickAction, rightClickAction);		
	}

	private void setRenderer(DefaultTableCellRenderer renderer) 
	{
		for(int index = 0 ; index < columns.length ; index++)
		{
			table.getColumnModel().getColumn(index).setHeaderRenderer(renderer);
			table.getColumnModel().getColumn(index).setCellRenderer(renderer);
		}
		
	}

	private void createTable(DefaultTableModel model) 
	{
		for(int index = 0 ; index < columns.length ; index++)
			model.addColumn(columns[index]);	
		
		for(int index = 0 ; index < content.length ; index++)
			model.addRow(content[index]);	
		
	}

	public void refresh(String[][] rows) 
	{
		for(int row = 0 ; row < content.length ; row++)
			for(int column = 0 ; column < content[row].length ; column++)
				table.getModel().setValueAt(rows[row][column], row, column);
		
	}

	public void updateCellValue(int row, int column, String newValue) 
	{
		table.getModel().setValueAt(newValue, row, column);
	}

}

