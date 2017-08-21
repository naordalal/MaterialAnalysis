package Forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import MainPackage.Globals;

public class CustomerOrder extends Form{

	
	private String customer;
	private String orderNumber;
	private String description;
	private String price;
	private String customerOrderNumber;

	public CustomerOrder(int id , String customer , String orderNumber , String customerOrderNumber , String catalogNumber , String description , String quantity , String price , Date orderDate , Date guaranteedDate) 
	{
		super(id , catalogNumber, quantity,orderDate, guaranteedDate);
		
		this.customer = customer;
		this.orderNumber = orderNumber;
		this.customerOrderNumber = customerOrderNumber;
		this.description = description;
		this.price = price;
	}

	public String getCustomer() 
	{
		return customer;
	}

	public void setCustomer(String customer) 
	{
		this.customer = customer;
	}

	public String getOrderNumber() 
	{
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) 
	{
		this.orderNumber = orderNumber;
	}

	public String getDescription() 
	{
		return description;
	}

	public void setDescription(String description) 
	{
		this.description = description;
	}

	public String getPrice() 
	{
		return price;
	}

	public void setPrice(String price) 
	{
		this.price = price;
	}

	public String getCustomerOrderNumber() 
	{
		return customerOrderNumber;
	}

	public void setCustomerOrderNumber(String customerOrderNumber) 
	{
		this.customerOrderNumber = customerOrderNumber;
	}

	@Override
	public String[] getColumns() 
	{
		String [] columns = new String[9];
		columns[0] = "Customer";
		columns[1] = "Order Number";
		columns[2] = "Customer Order Number";
		columns[3] = "Catalog Number";
		columns[4] = "Description";
		columns[5] = "Price";
		columns[6] = "Quantity";
		columns[7] = "Order Date";
		columns[8] = "Guaranteed Date";
		
		return columns;
	}

	@Override
	public String[] getRow() 
	{
		String [] row = new String[9];
		row[0] = this.customer;
		row[1] = this.orderNumber;
		row[2] = this.customerOrderNumber;
		row[3] = super.getCatalogNumber();
		row[4] = this.description;
		row[5] = this.price;
		row[6] = super.getQuantity();
		row[7] = Globals.dateWithoutHourToString(super.getCreateDate());
		row[8] = Globals.dateWithoutHourToString(super.getRequestDate());
		
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
				this.customer = newValue;
			case 1:
				this.orderNumber = newValue;
				break;
			case 2:
				this.customerOrderNumber = newValue;
				break;
			case 3:
				super.setCatalogNumber(newValue);
				break;
			case 4:
				this.description = newValue;
				break;
			case 5:
				this.price = newValue;
				break;
			case 6:
				if(StringUtils.isNumeric(newValue))
					super.setQuantity(newValue);
				else
					throw new Exception("Quantity have to be a numeric value");
				break;
			case 7:
				Date createDate;
				if((createDate = Globals.isValidDate(newValue)) != null)
					super.setCreateDate(createDate);
				else
					throw new Exception("Create date have to be a date format");
				break;
			case 8:
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

}
