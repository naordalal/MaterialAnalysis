package MapFrames;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.Authenticator;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import Components.FilterCombo;
import Components.MultiSelectionComboBox;
import Components.MyJTable;
import Components.MyTableRenderer;
import Components.TableCellListener;
import MainPackage.CallBack;
import MainPackage.DataBase;
import MainPackage.Excel;
import MainPackage.Globals;
import MainPackage.noValidEmailException;
import Senders.SendEmail;

public class ReportViewFrame implements ActionListener 
{
	private static final int maximumFilters = 4;
	
	private JFrame frame;
	private String email;
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
	private JLabel copyRight;
	private List<Integer> filterColumns;
	private List<String> filterNames;
	private String frameName;
	private JLabel[] filterLabels;
	private MultiSelectionComboBox<String>[] filterComboBoxs;
	private JPanel filterPanel;
	private Map<Integer,Integer> currentRowPerOriginalRow;
	private JButton exportReportButton;
	private Authenticator auth;


	public ReportViewFrame(String email , Authenticator auth , String frameName , String [] columns , String [][] content ,  boolean canEdit , List<Integer> invalidEditableColumns) 
	{
		this.email = email;
		this.auth = auth;
		this.frameName = frameName;
		this.columns = columns;
		this.content = content;
		this.globals = new Globals();
		this.canEdit = canEdit;
		this.invalidEditableColumns = invalidEditableColumns;
		this.filterColumns = new ArrayList<>();
		this.filterNames = new ArrayList<>();
		this.currentRowPerOriginalRow = new HashMap<>();
		for (int row = 0 ; row < content.length ; row ++)
			currentRowPerOriginalRow.put(row, row);
	}

	public void setFilters(List<Integer> filterColumns , List<String> filterNames)
	{
		int size = Math.min(Math.min(filterColumns.size(), filterNames.size()) , maximumFilters);
		this.filterColumns.addAll(filterColumns.subList(0, size));
		this.filterNames.addAll(filterNames.subList(0, size));
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
		
		frame = new JFrame(frameName);
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
		
		int yPanelLocation = 0;
		if(filterColumns.size() > 0)
		{
			yPanelLocation = 30;
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(FlowLayout.LEADING);
			filterPanel = new JPanel();
			filterPanel.setLocation(0 , 0);
			filterPanel.setSize(900, yPanelLocation);
			filterPanel.setLayout(flowLayout);
			frame.add(filterPanel);
		}
		
		panel = new JPanel();
		panel.setLocation(0 , yPanelLocation);
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
		setCellRenderer(new MyTableRenderer());
		//table.setFont(new Font("Tahoma", Font.PLAIN, 16));
		int lengthOfColumns = setColumnWidth(); 
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
		headerRenderer.setHorizontalAlignment(JLabel.CENTER);
		setHeaderRenderer(headerRenderer);
		
		table.setBorder(new AbstractBorder() 
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) 
			{
				 g.setColor(Color.RED);
				 String lastVal = "";
				 for (int rowIndex = 0 ; rowIndex < table.getRowCount() ; rowIndex++) 
				 {
					String [] row = getRow(rowIndex);
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
		
		
		filterLabels = new JLabel[filterColumns.size()];
		filterComboBoxs= new MultiSelectionComboBox[filterColumns.size()];
		
		for(int index = 0 ; index < filterLabels.length ; index ++)
		{
			filterLabels[index] = new JLabel(filterNames.get(index));
			filterPanel.add(filterLabels[index]);
			
			DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
			filterComboBoxs[index] = new MultiSelectionComboBox<String>(comboBoxModel);
			
			updateFilterComboBoxValues(index);
			filterComboBoxs[index].addActionListener(this);
			filterPanel.add(filterComboBoxs[index]);
		}

		
		exportReportButton = new JButton();
		exportReportButton.setLocation(800 , 640);
		exportReportButton.setSize(70,40);
		exportReportButton.setVisible(true);
		exportReportButton.addActionListener(this);
		exportReportButton.setIcon(globals.sendIcon);
		exportReportButton.setFocusable(false);
		exportReportButton.setContentAreaFilled(false);
		exportReportButton.setPressedIcon(globals.clickSendIcon);
		exportReportButton.setToolTipText("send");
		panel.add(exportReportButton);
		
		copyRight = new JLabel("<html><b>\u00a9 Naor Dalal</b></html>");
		copyRight.setLocation(30 , 680);
		copyRight.setSize(100,30);
		panel.add(copyRight);
		
		
		frame.setVisible(true);
	}

	private void updateFilterComboBoxValues(int index) 
	{
		DefaultComboBoxModel<String> filterModel = (DefaultComboBoxModel<String>) filterComboBoxs[index].getModel();
		filterComboBoxs[index].hidePopup();
		filterComboBoxs[index].removeAllItems();
		List<String> baseValues = new ArrayList<>();
		
		for(int row = 0 ; row < table.getRowCount() ; row++)
		{
			String value = (String) table.getValueAt(row , filterColumns.get(index));
			if(filterModel.getIndexOf(value) < 0)
			{
				filterModel.addElement(value);
				baseValues.add(value);
			}
		}
		
		filterComboBoxs[index].removeAllSelectedItem();
	}

	private String[] getRow(int rowIndex) 
	{
		String [] row = new String[table.getColumnCount()];
		for(int columnIndex = 0 ; columnIndex < row.length ; columnIndex ++)
			row[columnIndex] = (String) table.getValueAt(rowIndex, columnIndex);
		
		return row;
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
	
		createContent(model);
	}
	
	private void createContent(DefaultTableModel model)
	{		
		for(int index = 0 ; index < content.length ; index++)
			model.addRow(content[index]);
	}

	public void refresh(String[][] rows) 
	{

		this.content = rows;
		
		if(filterComboBoxs.length > 0)
		{
			actionPerformed(new ActionEvent(filterComboBoxs[0], 0, null));
			return;
		}
		
		for(int rowIndex = 	table.getRowCount() - 1 ; rowIndex >= 0 ; rowIndex --)
				removeRow(rowIndex);
		
		currentRowPerOriginalRow.clear();
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		for(int row = 0 ; row < rows.length ; row++)
		{
			model.addRow(rows[row]);
			currentRowPerOriginalRow.put(row, row);
		}

		
	}

	public void updateCellValue(int row, int column, String newValue) 
	{
		table.getModel().setValueAt(newValue, row, column);
		content[row][column] = newValue;
	}
	
	public int setColumnWidth() 
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

	public void removeRow(int row) 
	{
		int modelIndex = table.convertRowIndexToModel(row); 
        DefaultTableModel model = (DefaultTableModel)table.getModel();
        model.removeRow(modelIndex);
	}

	@Override
	public void actionPerformed(ActionEvent event) 
	{	
		if(event.getSource() == exportReportButton)
		{
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			String [][] currentContent = new String[table.getRowCount()][];
			for(int row = 0 ; row < table.getRowCount() ; row++)
			{
				currentContent[row] = getRow(row);
			}
			
			Excel excel = new Excel();
			File attachFile = excel.createExcelFile(Globals.getReportFileName(frameName) , columns, currentContent);
			
			List<String> dest = new ArrayList<>();
			dest.add(email);
			SendEmail sender = new SendEmail(email, dest, auth);
			
			try 
			{
				sender.send(frameName, "", attachFile, Globals.getReportFileName(frameName));
				JOptionPane.showConfirmDialog(null, "Sent successfully","",JOptionPane.PLAIN_MESSAGE);
			}
			catch (noValidEmailException e) 
			{
				e.printStackTrace();
				JOptionPane.showConfirmDialog(null, "Wrong User/Password OR there is no internet connection","",JOptionPane.PLAIN_MESSAGE);
			}
			
			attachFile.delete();
			
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		else if(isFilterComboBoxsEvent(event))
		{
			for(int rowIndex = 	table.getRowCount() - 1 ; rowIndex >= 0 ; rowIndex --)
				removeRow(rowIndex);
				
			DefaultTableModel model = (DefaultTableModel)table.getModel();
			
			for(int row = 0 ; row < content.length  ; row ++)
			{
				boolean filterRow = true;
				for (int comboxIndex = 0 ; comboxIndex < filterComboBoxs.length ; comboxIndex++) 
				{
					MultiSelectionComboBox<String> comboBox = filterComboBoxs[comboxIndex];
					List<String> selectionItems = comboBox.getSelectedItems().stream().map(item -> item.trim().toLowerCase()).collect(Collectors.toList());
					
					int column = filterColumns.get(comboxIndex);
					if(selectionItems.size() > 0 && !selectionItems.contains(content[row][column].trim().toLowerCase()))
						filterRow = false;
				}
				
				if(filterRow)
				{
					model.addRow(content[row]);
					currentRowPerOriginalRow.put(model.getRowCount() - 1 , row);
				}
			}
		}


	}


	private boolean isFilterComboBoxsEvent(ActionEvent event) 
	{
		for (int comboxIndex = 0 ; comboxIndex < filterComboBoxs.length ; comboxIndex++) 
		{
			if(event.getSource() == filterComboBoxs[comboxIndex])
				return true;
		}
		
		return false;
	}

	public int getOriginalRowNumber(int row) 
	{
		return currentRowPerOriginalRow.get(row);
	}
    

}

