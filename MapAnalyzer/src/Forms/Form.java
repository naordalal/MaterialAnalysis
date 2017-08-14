package Forms;

import java.util.Date;

public class Form 
{
	private String catalogNumber;
	private String quantity;
	private Date requestDate , createDate;
	
	public Form(String catalogNumber , String quantity , Date createDate , Date requestDate) 
	{
		this.catalogNumber = catalogNumber;
		this.quantity = quantity;
		this.createDate = createDate;
		this.requestDate = requestDate;
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
	
	public Date getRequestDate() 
	{
		return requestDate;
	}
	public void setRequstDate(Date requestDate) 
	{
		this.requestDate = requestDate;
	}

	public Date getCreateDate() 
	{
		return createDate;
	}

	public void setCreateDate(Date createDate) 
	{
		this.createDate = createDate;
	}
}
