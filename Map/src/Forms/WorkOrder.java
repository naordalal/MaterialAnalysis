package Forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import mainPackage.Globals;

public class WorkOrder extends Form
{

	private String woNumber;
	private String customer;
	private String description;

	public WorkOrder(int id , String woNumber , String catalogNumber , String quantity , String customer , Date date , String description)
	{
		super(id , catalogNumber, quantity, date , date);
		this.woNumber = woNumber;
		this.customer = customer;
		this.description = description;
	}

	public String getWoNumber() 
	{
		return woNumber;
	}

	public void setWoNumber(String woNumber) 
	{
		this.woNumber = woNumber;
	}

	public String getCustomer() 
	{
		return customer;
	}

	public void setCustomer(String customer) 
	{
		this.customer = customer;
	}

	public String getDescription() 
	{
		return description;
	}

	public void setDescription(String description) 
	{
		this.description = description;
	}

	@Override
	public String[] getColumns() 
	{
		String [] columns = new String[7];
		columns[0] = "Id";
		columns[1] = "Work Order Number";
		columns[2] = "Customer";
		columns[3] = "Catalog Number";
		columns[4] = "Description";
		columns[5] = "Quantity";
		columns[6] = "Date";
		
		return columns;
	}

	@Override
	public String[] getRow() 
	{
		String [] row = new String[7];
		row[0] = Integer.toString(super.getId());
		row[1] = this.woNumber;
		row[2] = this.customer;
		row[3] = super.getCatalogNumber();
		row[4] = this.description;
		row[5] = super.getQuantity();
		row[6] = Globals.dateWithoutHourToString(super.getRequestDate());
		
		return row;
	}

	@Override
	public boolean canEdit() 
	{
		return false;
	}

	@Override
	public void updateValue(int column, String newValue) throws Exception
	{
		if(!canEdit())
			return;
		
		switch(column)
		{
			case 0:
				break;
			case 1:
				this.woNumber = newValue;
				break;
			case 2:
				this.customer = newValue;
			case 3:
				super.setCatalogNumber(newValue);
				break;
			case 4:
				this.description = newValue;
				break;
			case 5:
				if(StringUtils.isNumeric(newValue))
					super.setQuantity(newValue);
				else
					throw new Exception("Quantity have to be a numeric value");
				break;
			case 6:
				Date requestDate;
				if((requestDate = Globals.isValidDate(newValue)) != null)
					super.setRequstDate(requestDate);
				else
					throw new Exception("Request date have to be a date format");
				break;
				
			default:
				break;
		}
		
	}
	
	@Override
	public List<Integer> getInvalidEditableColumns() 
	{
		List<Integer> columns = new ArrayList<>();
		columns.add(0);
		return columns;
	}

}
