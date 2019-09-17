package Components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class MyJTable extends JTable
{
	private static final long serialVersionUID = 1L;
	
	private Map<Integer,List<Integer>> invalidEditableColumns;
	int selectedRow = -1 , selectedColumn = -1 , popUpRow = -1 ,  popUpColumn= -1;
	boolean canEdit;

	public MyJTable(DefaultTableModel model , boolean canEdit, Map<Integer,List<Integer>> invalidEditableColumns)
	{
		super(model);
		this.invalidEditableColumns = invalidEditableColumns;
		this.canEdit = canEdit;
	}

	@Override
	public boolean isCellEditable(int row, int column) 
	{
		return canEdit && !invalidEditableColumns.get(row).contains(column);
	}

	public void setEditable(boolean canEdit)
	{
		this.canEdit = canEdit;
	}

	public void setCellEditable(int row, int column)
	{
		this.selectedRow = row;
		this.selectedColumn = column;
	}
	
	public void cancelCellEditable()
	{
		this.selectedRow = -1;
		this.selectedColumn = -1;
	}
	
	public void setPopUpCell(int row, int column)
	{
		this.popUpRow = row;
		this.popUpColumn = column;
	}
	
	public void clearPopUpCell()
	{
		this.popUpRow = -1;
		this.popUpColumn = -1;
	}
	
	public int getPopUpRow()
	{
		return this.popUpRow;
	}
	
	public int getPopUpColumn()
	{
		return this.popUpColumn;
	}
}
