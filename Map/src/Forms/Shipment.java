package Forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import mainPackage.Globals;

public class Shipment extends Form
{
	private String customer;
	private String orderId;
	private String description;
	private String orderCustomerId;
	
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
		String [] columns = new String[8];
		columns[0] = "Id";
		columns[1] = "Customer";
		columns[2] = "Order Number";
		columns[3] = "Customer Order Number";
		columns[4] = "Catalog Number";
		columns[5] = "Description";
		columns[6] = "Quantity";
		columns[7] = "Shipment Date";
		
		return columns;
	}

	@Override
	public String[] getRow() 
	{
		String [] row = new String[8];
		row[0] = Integer.toString(super.getId());
		row[1] = this.customer;
		row[2] = this.orderId;
		row[3] = this.orderCustomerId;
		row[4] = super.getCatalogNumber();
		row[5] = this.description;
		row[6] = super.getQuantity();
		row[7] = Globals.dateWithoutHourToString(super.getRequestDate());
		
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
				this.customer = newValue;
			case 2:
				this.orderId = newValue;
				break;
			case 3:
				this.orderCustomerId = newValue;
				break;
			case 4:
				super.setCatalogNumber(newValue);
				break;
			case 5:
				this.description = newValue;
				break;
			case 6:
				if(StringUtils.isNumeric(newValue))
					super.setQuantity(newValue);
				else
					throw new Exception("Quantity have to be a numeric value");
				break;
			case 7:
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
