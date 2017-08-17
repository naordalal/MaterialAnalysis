package Frames;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import mainPackage.CallBack;
import mainPackage.Globals;

public class ReportViewFrame 
{
	protected static final int padding = 16;
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
		table.setShowGrid(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		setCellRenderer(centerRenderer);
		//table.setFont(new Font("Tahoma", Font.PLAIN, 16));
		int lengthOfColumns = setColumnWidth(); 
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
		headerRenderer.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		headerRenderer.setHorizontalAlignment(JLabel.CENTER);
		setHeaderRenderer(headerRenderer);
		
		table.setBorder(new AbstractBorder() 
		{
			@Override
			public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
				 g.setColor(Color.RED);
				 String lastVal = "";
				 for (int rowIndex = 0 ; rowIndex < content.length ; rowIndex++) 
				 {
					String [] row = content[rowIndex];
					int rowHeight = getRowHeight(rowIndex);
					if(!lastVal.equals(row[0]))
					{
						g.drawLine(x, y + rowIndex * rowHeight, x + lengthOfColumns, y + rowIndex * rowHeight);
						lastVal = row[0];
					}
				 }
			}
			
			@Override
		    public boolean isBorderOpaque()
		    {
		        return true;
		    }


			@Override
			public Insets getBorderInsets(Component arg0) {
				return new Insets(2,2,2,2);
			}
		});

		scrollPane = new JScrollPane(table);
		scrollPane.setLocation(30, 30);
		scrollPane.setSize(850,600);
		scrollPane.setVisible(true);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		panel.add(scrollPane);
		
		new TableCellListener(table, valueCellChangeAction, doubleLeftClickAction, rightClickAction);		
	}

	private int getRowHeight(int row) 
	{
        int maxHeight = 0;
        for (int column = 0; column < table.getColumnCount(); column++) 
        {
            TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
            Object valueAt = table.getValueAt(row, column);
            Component tableCellRendererComponent = cellRenderer.getTableCellRendererComponent(table, valueAt, false, false, row, column);
            int heightPreferable = tableCellRendererComponent.getPreferredSize().height;
            maxHeight = Math.max(heightPreferable, maxHeight);
        }
        
        return maxHeight;
		
	}

	private void setCellRenderer(DefaultTableCellRenderer renderer) 
	{
		for(int index = 0 ; index < columns.length ; index++)
			table.getColumnModel().getColumn(index).setCellRenderer(renderer);
	}
	
	private void setHeaderRenderer(DefaultTableCellRenderer renderer) 
	{
		for(int index = 0 ; index < columns.length ; index++)
			table.getColumnModel().getColumn(index).setHeaderRenderer(renderer);
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
	
	private int setColumnWidth() 
    {
		int width = 0;
        adjustJTableRowSizes(table);
        for (int i = 0; i < table.getColumnCount(); i++) 
        	width += adjustColumnSizes(table, i, 4);
        return width;
    }

    private void adjustJTableRowSizes(JTable jTable) 
    {
        for (int row = 0; row < jTable.getRowCount(); row++) 
        {
            int maxHeight = 0;
            for (int column = 0; column < jTable.getColumnCount(); column++) 
            {
                TableCellRenderer cellRenderer = jTable.getCellRenderer(row, column);
                Object valueAt = jTable.getValueAt(row, column);
                Component tableCellRendererComponent = cellRenderer.getTableCellRendererComponent(jTable, valueAt, false, false, row, column);
                int heightPreferable = tableCellRendererComponent.getPreferredSize().height;
                maxHeight = Math.max(heightPreferable, maxHeight);
            }
            jTable.setRowHeight(row, maxHeight);
        }

    }

    public int adjustColumnSizes(JTable table, int column, int margin) 
    {
        DefaultTableColumnModel colModel = (DefaultTableColumnModel) table.getColumnModel();
        TableColumn col = colModel.getColumn(column);
        int width;

        TableCellRenderer renderer = col.getHeaderRenderer();
        if (renderer == null) 
        {
            renderer = table.getTableHeader().getDefaultRenderer();
        }
        Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);
        width = comp.getPreferredSize().width;

        for (int r = 0; r < table.getRowCount(); r++) 
        {
            renderer = table.getCellRenderer(r, column);
            comp = renderer.getTableCellRendererComponent(table, table.getValueAt(r, column), false, false, r, column);
            int currentWidth = comp.getPreferredSize().width;
            width = Math.max(width, currentWidth);
        }

        width += 2 * margin;
        
        col.setPreferredWidth(width);
        col.setWidth(width);
        return width;
    }
    

}

