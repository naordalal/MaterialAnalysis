package MainPackage;

import java.util.Date;

public class Form 
{
	private String catalogNumber;
	private String quantity;
	private Date date;
	
	public Form(String catalogNumber , String quantity , Date date) 
	{
		this.catalogNumber = catalogNumber;
		this.quantity = quantity;
		this.date = date;
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
	
	public Date getDate() 
	{
		return date;
	}
	public void setDate(Date date) 
	{
		this.date = date;
	}
}
