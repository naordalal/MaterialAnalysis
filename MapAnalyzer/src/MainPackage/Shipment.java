package MainPackage;

import java.util.Date;

public class Shipment extends Form
{
	private String customer;
	private String orderId;
	private String description;
	private String orderCustomerId;
	
	public Shipment(String customer , String orderId , String orderCustomerId , String catalogNumber , String quantity , Date shipmentDate , String description) 
	{
		super(catalogNumber,quantity,shipmentDate,shipmentDate);
		
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
}
