package MapFrames;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.Authenticator;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.lang3.math.NumberUtils;

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

public class ReportViewFrame implements ActionListener {
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
	private Map<Integer,List<Integer>> invalidEditableColumns;
	private JLabel copyRight;
	private List<Integer> filterColumns;
	private List<String> filterNames;
	private String frameName;
	private JLabel[] filterLabels;
	private MultiSelectionComboBox<String>[] filterComboBoxs;
	private JPanel filterPanel;
	private JButton exportReportButton;
	private Authenticator auth;
	private JComponent customComponent;

	private JTextField filterText;

	private JButton searchButton;

	private JLabel filterLabel;


	public ReportViewFrame(String email , Authenticator auth , String frameName , String [] columns , String [][] content ,  boolean canEdit , Map<Integer,List<Integer>> invalidEditableColumns)
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
		frame.setBounds(300, 100, 950, 780);
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
			filterPanel.setSize(950, yPanelLocation);
			filterPanel.setLayout(flowLayout);
			frame.add(filterPanel);
		}
		
		panel = new JPanel();
		panel.setLocation(0 , yPanelLocation);
		panel.setSize(950, 780);
		panel.setLayout(null);
		frame.add(panel);
		
		DefaultTableModel model = new DefaultTableModel();
		table = new MyJTable(model , canEdit, invalidEditableColumns);
		createTable(model);
		table.setShowGrid(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		setCellRenderer(new MyTableRenderer());
		//table.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		table.getTableHeader().setReorderingAllowed(false);
		//table.getTableHeader().setResizingAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
		headerRenderer.setHorizontalAlignment(JLabel.CENTER);
		setHeaderRenderer(headerRenderer);
		
		setColumnWidth();
		
		table.setBorder(new AbstractBorder() 
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) 
			{
				 g.setColor(Color.RED);
				 String lastVal = "";
				 int lengthOfColumns = getColumnWidth(); 
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
		scrollPane.setLocation(30, 50);
		scrollPane.setSize(900,580);
		scrollPane.setVisible(true);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		panel.add(scrollPane);
		
		InputMap im = table.getInputMap(JComponent.WHEN_FOCUSED);
		ActionMap am = table.getActionMap();
		
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F , Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) , "Find");
		
		am.put("Find", new AbstractAction() 
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				findValue();		
			}
		});
		
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
			filterComboBoxs[index].setPreferredSize(new Dimension(650 / maximumFilters , 20));
			filterPanel.add(filterComboBoxs[index]);
		}

		filterLabel = new JLabel("Insert text: ");
		filterLabel.setLocation(5, 10);
		filterLabel.setSize(80, 20);
		panel.add(filterLabel);
		
		filterText = new JTextField();
		filterText.setLocation(95, 10);
		filterText.setSize(100, 20);
		panel.add(filterText);
		
		searchButton = new JButton("Search");
		searchButton.setLocation(220, 10);
		searchButton.setSize(100, 25);
		searchButton.addActionListener(this);
		panel.add(searchButton);
		
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
		
		if(customComponent != null)
		{
			customComponent.setLocation(50, 640);
			customComponent.setSize(100,40);
			panel.add(customComponent);
		}
		
		copyRight = new JLabel("<html><b>\u00a9 Naor Dalal</b></html>");
		copyRight.setLocation(30 , 680);
		copyRight.setSize(100,30);
		panel.add(copyRight);
		
		
		frame.setVisible(true);
	}

	private void findValue() 
	{		
		String value = JOptionPane.showInputDialog(null , "Enter value" , "Find" , JOptionPane.QUESTION_MESSAGE);
		if(value == null)
			return;
		
		for (int row = 0; row <= table.getRowCount() - 1; row++) 
		{

            for (int col = 0; col <= table.getColumnCount() - 1; col++) 
            {

                if (table.getValueAt(row, col).toString().toLowerCase().contains(value.toLowerCase())) 
                {

                    // this will automatically set the view of the scroll in the location of the value
                    table.scrollRectToVisible(table.getCellRect(row, 0, true));

                    // this will automatically set the focus of the searched/selected row/value
                    table.setRowSelectionInterval(row, row);

                    table.changeSelection(row, col, false, false);
                   
                   return;
                }
            }
        }
	}

	private void updateFilterComboBoxValues(int index) 
	{
		DefaultComboBoxModel<String> filterModel = (DefaultComboBoxModel<String>) filterComboBoxs[index].getModel();
		filterComboBoxs[index].hidePopup();
		filterComboBoxs[index].removeAllItems();
		List<String> baseValues = new ArrayList<>();
		
		for(int row = 0 ; row < table.getModel().getRowCount() ; row++)
		{
			String value = (String) table.getModel().getValueAt(row , filterColumns.get(index));
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
		{
			model.addRow(content[index]);
			for(int cell = 0; cell < content[index].length ; cell++)
			{
				String value = content[index][cell].trim();
				
				if(NumberUtils.isCreatable(value))
    				updateCellValue(index , cell , String.format("%,d", (int)Double.parseDouble(value)));
				
				if(content[index][cell].trim().equals("0") || content[index][cell].trim().equals("0.0"))
					updateCellValue(index, cell, "");
			}
		}
	}

	public void refresh(String[][] rows) 
	{

		this.content = rows;
				
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		
		for(int rowIndex = model.getRowCount() - 1 ; rowIndex >= 0 ; rowIndex --)
				model.removeRow(rowIndex);
		
		createContent(model);
		
		for(int index = 0 ; index < filterComboBoxs.length ;index++)
		{
			List<String> selectedItems = new ArrayList<>(filterComboBoxs[index].getSelectedItems());
			updateFilterComboBoxValues(index);
			for (String item : selectedItems) 
			{
				filterComboBoxs[index].addSelectedItem(item);
			}
			filterComboBoxs[index].setModelSelectedItem();
		}
		
		if(filterComboBoxs.length > 0)
		{
			actionPerformed(new ActionEvent(filterComboBoxs[0], 0, null));
		}
		
		if(!filterText.getText().equals(""))
		{
			actionPerformed(new ActionEvent(searchButton, 0, null));
		}
		
	}

	public void updateCellValue(int row, int column, String newValue) 
	{
		if(newValue.trim().equals("0") || newValue.trim().trim().equals("0.0"))
		{
			table.getModel().setValueAt("", row, column);
			content[row][column] = "";
		}
		else
		{
			if(NumberUtils.isCreatable(newValue))
			{
				newValue = String.format("%,d", (int)Double.parseDouble(newValue));
				table.getModel().setValueAt(newValue, row, column);
				content[row][column] = newValue;
			}
			else
			{
				table.getModel().setValueAt(newValue, row, column);
				content[row][column] = newValue;
			}
		}
		
	}
	
	public int setColumnWidth() 
    {
		int width = 0;
        adjustJTableRowSizes(table);
        for (int i = 0; i < table.getColumnCount(); i++) 
        	width += adjustColumnSizes(table, i, 4);
        return width;
    }
	
	public int getColumnWidth() 
    {
		int width = 0;
        for (int i = 0; i < table.getColumnCount(); i++) 
        	width += getColumnSize(table, i);
        return width;
    }

    private int getColumnSize(MyJTable table, int column) 
    {
		JTableHeader th = table.getTableHeader();
		TableColumnModel tcm = th.getColumnModel();
		TableColumn tc = tcm.getColumn(column);
		
		return tc.getWidth();
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

	public void updateRow(int row) 
	{
        DefaultTableModel model = (DefaultTableModel)table.getModel();
        for(int column = 0 ; column < model.getColumnCount() ; column++)
        	updateCellValue(row, column, content[row][column]);
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
				if(sender.send(frameName, "", attachFile, Globals.getReportFileName(frameName)))
					JOptionPane.showConfirmDialog(null, "Sent successfully","",JOptionPane.PLAIN_MESSAGE);
				else
					JOptionPane.showConfirmDialog(null, "Wrong User/Password OR there is no internet connection","",JOptionPane.PLAIN_MESSAGE);	
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
			List<RowFilter<TableModel, Integer>> rowsFilters = new ArrayList<>();
			for (int comboxIndex = 0 ; comboxIndex < filterComboBoxs.length ; comboxIndex++) 
			{
				MultiSelectionComboBox<String> comboBox = filterComboBoxs[comboxIndex];
				List<String> selectionItems = comboBox.getSelectedItems().stream().map(item -> item.trim().toLowerCase()).collect(Collectors.toList());
				
				int column = filterColumns.get(comboxIndex);
				List<RowFilter<TableModel, Integer>> rowFilters = new ArrayList<>();
				selectionItems.forEach(selectionItem -> rowFilters.add(RowFilter.regexFilter("(?i)^" + selectionItem + "$" , column)));
				if(rowFilters.size() > 0)
				{
					RowFilter<TableModel, Integer> rowFilter = RowFilter.orFilter(rowFilters);
					
					rowsFilters.add(rowFilter);
				}
			}
			
			TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(((DefaultTableModel) table.getModel())); 
		    sorter.setRowFilter(RowFilter.andFilter(rowsFilters));

		    table.setRowSorter(sorter);
		}
		else if(event.getSource() == searchButton)
		{
			String text = filterText.getText();
			RowFilter<TableModel, Integer> rowFilter = RowFilter.regexFilter("(?i)" + text , 0);
			TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(((DefaultTableModel) table.getModel())); 
		    sorter.setRowFilter(rowFilter);
		    
		    table.setRowSorter(sorter);
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

	public void setEditable(boolean editable)
	{
		this.canEdit = editable;
		table.setEditable(editable);
	}
	
	public void setFrameName(String frameName)
	{
		this.frameName = frameName;
	}

	public void updateRows(String[][] rows) 
	{
		
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		List<String[]> rowsList = new ArrayList<>(Arrays.asList(rows));
		
		rowLoop:
		for(int rowIndex = 0 ; rowIndex < model.getRowCount() ; rowIndex ++)
		{
			String [] row = content[rowIndex];
			List<String[]> temp = new ArrayList<>(rowsList);
			for(String[] newRow : temp)
			{
				if(row[0].equals(newRow[0]))
				{
					content[rowIndex] = newRow;
					updateRow(rowIndex);
					rowsList.remove(newRow);
					continue rowLoop;
				}
					
			}
		}
		
		if(filterComboBoxs.length > 0)
		{
			actionPerformed(new ActionEvent(filterComboBoxs[0], 0, null));
		}
		
		if(!filterText.getText().equals(""))
		{
			actionPerformed(new ActionEvent(searchButton, 0, null));
		}
	}
	
	public void setCustomComponent(JComponent component)
	{
		this.customComponent = component;
	}

	public int convertIndexToModelIndex(int rowViewIndex)
	{
		if(table.getRowSorter() == null)
		{
			return rowViewIndex;
		}

		return table.getRowSorter().convertRowIndexToModel(rowViewIndex);
	}
}

