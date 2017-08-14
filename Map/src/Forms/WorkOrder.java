package Forms;

import java.util.Date;

public class WorkOrder extends Form
{

	private String woNumber;
	private String customer;
	private String description;

	public WorkOrder(String woNumber , String catalogNumber , String quantity , String customer , Date date , String description)
	{
		super(catalogNumber, quantity, date , date);
		this.woNumber = woNumber;
		this.customer = customer;
		this.description = description;
	}

	public String getWoNumber() 
	{
		return woNumber;
	}

	public void setWoNumber(String woNumber) 
	{
		this.woNumber = woNumber;
	}

	public String getCustomer() 
	{
		return customer;
	}

	public void setCustomer(String customer) 
	{
		this.customer = customer;
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
