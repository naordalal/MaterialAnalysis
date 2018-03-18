package Reports;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.mail.Authenticator;

import MainPackage.CallBack;
import MainPackage.Globals;
import MainPackage.Globals.FormType;
import MainPackage.Message;
import MapFrames.ReportViewFrame;

public class ProductInitHistory extends Report
{
	private String catalogNumber;
	private String quantity;
	private String initDate;
	private String requireDate;
	private FormType type;
	private String changeDate;
	private String userUpdate;
	private String note;

	public ProductInitHistory(String catalogNumber , String quantity , String initDate , String requireDate ,String changeDate,
			String userUpdate, String note , FormType type)
	{
		this.catalogNumber = catalogNumber;
		this.quantity = quantity;
		this.initDate = initDate;
		this.requireDate = requireDate;
		this.changeDate = changeDate;
		this.userUpdate = userUpdate;
		this.note = note;
		this.type = type;
	}

	public String getCatalogNumber() 
	{
		return catalogNumber;
	}

	public void setCatalogNumber(String catalogNumber) 
	{
		this.catalogNumber = catalogNumber;
	}

	public String getQuantity()
	{
		return quantity;
	}

	public void setQuantity(String quantity) 
	{
		this.quantity = quantity;
	}

	public String getInitDate() {
		return initDate;
	}

	public void setInitDate(String initDate)
	{
		this.initDate = initDate;
	}

	public String getRequireDate() 
	{
		return requireDate;
	}

	public void setRequireDate(String requireDate) 
	{
		this.requireDate = requireDate;
	}
	
	public FormType getType() 
	{
		return type;
	}

	public void setType(FormType type)
	{
		this.type = type;
	}
	
	public String getChangeDate() 
	{
		return changeDate;
	}

	public void setChangeDate(String changeDate)
	{
		this.changeDate = changeDate;
	}

	public String getUserUpdate() 
	{
		return userUpdate;
	}

	public void setUserUpdate(String userUpdate)
	{
		this.userUpdate = userUpdate;
	}
	
	public String getNote() 
	{
		return note;
	}

	public void setNote(String note)
	{
		this.note = note;
	}
		
	public String [] getColumns()
	{
		String [] columns = new String[8];
		columns[0] = "Catalog Number";
		columns[1] = "Type";
		columns[2] = "Quantity";
		columns[3] = "InitDate";
		columns[4] = "RequireDate";
		columns[5] = "ChangeDate";
		columns[6] = "UserUpdate";
		columns[7] = "Note";
		
		return columns;
	}
	
	public String[] getRow() 
	{
		Globals globals = new Globals();
		String [] row = new String[8];
		row[0] = this.catalogNumber;
		row[1] = globals.FormTypeToString(this.type);
		row[2] = this.quantity;
		row[3] = this.initDate;
		row[4] = this.requireDate;
		row[5] = this.changeDate;
		row[6] = this.userUpdate;
		row[7] = this.note;
		
		return row;
	}

	@Override
	public Message updateValue(String userName, int column, String newValue) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> getInvalidEditableColumns() 
	{
		return IntStream.rangeClosed(0, 7).boxed().collect(Collectors.toList());
	}

	@Override
	public List<Integer> getFilterColumns() 
	{
		List<Integer> filterColumns = new ArrayList<>();
		filterColumns.add(0);
		filterColumns.add(1);
		filterColumns.add(4);
		
		return filterColumns;
	}

	@Override
	public CallBack<Object> getValueCellChangeAction(String email , Authenticator auth, String userName, ReportViewFrame frame, Object... args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CallBack<Object> getDoubleLeftClickAction(String email , Authenticator auth, String userName, ReportViewFrame frame, Object... args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CallBack<Object> getRightClickAction(String email , Authenticator auth, String userName, ReportViewFrame frame, Object... args) {
		// TODO Auto-generated method stub
		return null;
	}

}
