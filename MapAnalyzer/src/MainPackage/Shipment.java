package MainPackage;

import java.util.Date;

public class Shipment 
{
	private String customer;
	private String shipmentId;
	private String catalogNumber;
	private String quantity;
	private Date shipmentDate;
	private String description;
	
	public Shipment(String customer , String shipmentId , String catalogNumber , String quantity , Date shipmentDate , String description) 
	{
		this.customer = customer;
		this.shipmentId = shipmentId;
		this.catalogNumber = catalogNumber;
		this.quantity = quantity;
		this.shipmentDate = shipmentDate;
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
	
	public Date getShipmentDate() 
	{
		return shipmentDate;
	}
	public void setShipmentDate(Date shipmentDate) 
	{
		this.shipmentDate = shipmentDate;
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
