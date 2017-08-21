package Forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import MainPackage.Globals;

public class WorkOrder extends Form
{

	private String woNumber;
	private String customer;
	private String description;

	public WorkOrder()
	{
		super();
	}
	
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
		String [] columns = new String[6];
		columns[0] = "Work Order Number";
		columns[1] = "Customer";
		columns[2] = "Catalog Number";
		columns[3] = "Description";
		columns[4] = "Quantity";
		columns[5] = "Date";
		
		return columns;
	}

	@Override
	public String[] getRow() 
	{
		String [] row = new String[6];
		row[0] = this.woNumber;
		row[1] = this.customer;
		row[2] = super.getCatalogNumber();
		row[3] = this.description;
		row[4] = super.getQuantity();
		row[5] = Globals.dateWithoutHourToString(super.getRequestDate());
		
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
				this.woNumber = newValue;
				break;
			case 1:
				this.customer = newValue;
			case 2:
				super.setCatalogNumber(newValue);
				break;
			case 3:
				this.description = newValue;
				break;
			case 4:
				if(StringUtils.isNumeric(newValue))
					super.setQuantity(newValue);
				else
					throw new Exception("Quantity have to be a numeric value");
				break;
			case 5:
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
		return columns;
	}

	@Override
	public boolean isNeedRequireDate() 
	{
		return false;
	}

	@Override
	public boolean isNeedInit() 
	{
		return true;
	}

}
