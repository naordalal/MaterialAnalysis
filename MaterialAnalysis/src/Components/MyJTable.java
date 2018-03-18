package Components;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class MyJTable extends JTable
{
	private static final long serialVersionUID = 1L;
	
	private List<Integer> invalidEditableColumns;
	int selectedRow = -1 , selectedColumn = -1 , popUpRow = -1 ,  popUpColumn= -1;
	boolean canEdit;

	public MyJTable(DefaultTableModel model , boolean canEdit) 
	{
		super(model);
		invalidEditableColumns = new ArrayList<Integer>();
		this.canEdit = canEdit;
	}

	@Override
	public boolean isCellEditable(int row, int column) 
	{
		if(canEdit)
			return !invalidEditableColumns.contains(column);
		return !invalidEditableColumns.contains(column) && this.selectedRow == row && this.selectedColumn == column;  
	}
	
	public void addInvalidEditableColumn(Integer column)
	{
		invalidEditableColumns.add(column);
	}
	
	public void removeInvalidEditableColumn(Integer column)
	{
		invalidEditableColumns.remove(column);
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
