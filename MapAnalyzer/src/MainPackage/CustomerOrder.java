package MainPackage;

import java.util.Date;

public class CustomerOrder extends Form{

	
	private String customer;
	private String orderNumber;
	private String description;
	private String price;
	private String orderDate;

	public CustomerOrder(String customer , String orderNumber , String catalogNumber , String description , String quantity , String price , String orderDate , Date guaranteedDate) 
	{
		super(catalogNumber, quantity, guaranteedDate);
		
		this.customer = customer;
		this.orderNumber = orderNumber;
		this.description = description;
		this.price = price;
		this.orderDate = orderDate;
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

	public String getOrderDate() 
	{
		return orderDate;
	}

	public void setOrderDate(String orderDate) 
	{
		this.orderDate = orderDate;
	}

}
