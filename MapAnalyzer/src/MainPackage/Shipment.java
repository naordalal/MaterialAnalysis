package MainPackage;

import java.util.Date;

public class Shipment extends Form
{
	private String customer;
	private String shipmentId;
	private String description;
	
	public Shipment(String customer , String shipmentId , String catalogNumber , String quantity , Date shipmentDate , String description) 
	{
		super(catalogNumber,quantity,shipmentDate);
		
		this.customer = customer;
		this.shipmentId = shipmentId;
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
	
	public String getShipmentId() 
	{
		return shipmentId;
	}
	public void setShipmentId(String shipmentId) 
	{
		this.shipmentId = shipmentId;
	}
	
	
	public String getDescription() 
	{
		return description;
	}
	public void setDescription(String description) 
	{
		this.description = description;
	}
}
