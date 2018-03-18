package Forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import MainPackage.Globals;

public class Shipment extends Form
{
	private String customer;
	private String orderId;
	private String description;
	private String orderCustomerId;
	
	public Shipment() 
	{
		super();
	}
	
	public Shipment(int id , String customer , String orderId , String orderCustomerId , String catalogNumber , String quantity , Date shipmentDate , String description) 
	{
		super(id , catalogNumber,quantity,shipmentDate,shipmentDate);
		
		this.customer = customer;
		this.orderId = orderId;
		this.orderCustomerId = orderCustomerId;
		this.description = description;		
	}
	
	public String getCustomer() 
	{
		return customer;
	}
	public void setCustomer(String customer) 
	{
		this.customer = customer;
	}
	
	public String getorderId() 
	{
		return orderId;
	}
	public void setorderId(String orderId) 
	{
		this.orderId = orderId;
	}
	
	
	public String getDescription() 
	{
		return description;
	}
	public void setDescription(String description) 
	{
		this.description = description;
	}

	public String getOrderCustomerId() 
	{
		return orderCustomerId;
	}

	public void setOrderCustomerId(String orderCustomerId) 
	{
		this.orderCustomerId = orderCustomerId;
	}

	@Override
	public String[] getColumns() 
	{
		String [] columns = new String[7];
		columns[0] = "Customer";
		columns[1] = "Order Number";
		columns[2] = "Customer Order Number";
		columns[3] = "Catalog Number";
		columns[4] = "Description";
		columns[5] = "Quantity";
		columns[6] = "Shipment Date";
		
		return columns;
	}

	@Override
	public String[] getRow() 
	{
		String [] row = new String[7];
		row[0] = this.customer;
		row[1] = this.orderId;
		row[2] = this.orderCustomerId;
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
	public void updateValue(int column, String newValue , String userName) throws Exception 
	{
		if(!canEdit())
			return;
		
		switch(column)
		{
			case 0:
				this.customer = newValue;
			case 1:
				this.orderId = newValue;
				break;
			case 2:
				this.orderCustomerId = newValue;
				break;
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
		return false;
	}
	
	@Override
	public List<Integer> getFilterColumns() 
	{
		List<Integer> filterColumns = new ArrayList<>();
		filterColumns.add(0);
		filterColumns.add(1);
		filterColumns.add(2);
		filterColumns.add(3);
		filterColumns.add(4);
		
		return filterColumns;

	}
}
