package Forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import mainPackage.Globals;

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
		String [] columns = new String[10];
		columns[0] = "Id";
		columns[1] = "Customer";
		columns[2] = "Order Number";
		columns[3] = "Customer Order Number";
		columns[4] = "Catalog Number";
		columns[5] = "Description";
		columns[6] = "Price";
		columns[7] = "Quantity";
		columns[8] = "Order Date";
		columns[9] = "Guaranteed Date";
		
		return columns;
	}

	@Override
	public String[] getRow() 
	{
		String [] row = new String[10];
		row[0] = Integer.toString(super.getId());
		row[1] = this.customer;
		row[2] = this.orderNumber;
		row[3] = this.customerOrderNumber;
		row[4] = super.getCatalogNumber();
		row[5] = this.description;
		row[6] = this.price;
		row[7] = super.getQuantity();
		row[8] = Globals.dateWithoutHourToString(super.getCreateDate());
		row[9] = Globals.dateWithoutHourToString(super.getRequestDate());
		
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
				this.orderNumber = newValue;
				break;
			case 3:
				this.customerOrderNumber = newValue;
				break;
			case 4:
				super.setCatalogNumber(newValue);
				break;
			case 5:
				this.description = newValue;
				break;
			case 6:
				this.price = newValue;
				break;
			case 7:
				if(StringUtils.isNumeric(newValue))
					super.setQuantity(newValue);
				else
					throw new Exception("Quantity have to be a numeric value");
				break;
			case 8:
				Date createDate;
				if((createDate = Globals.isValidDate(newValue)) != null)
					super.setCreateDate(createDate);
				else
					throw new Exception("Create date have to be a date format");
				break;
			case 9:
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
