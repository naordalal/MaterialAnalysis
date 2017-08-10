package MainPackage;


public class QuantityPerDate 
{
	private MonthDate date;
	private int quantity;
	
	public QuantityPerDate(MonthDate date , int quantity) 
	{
		this.date = date;
		this.quantity = quantity;
	}
	
	public MonthDate getDate() 
	{
		return date;
	}
	public void setDate(MonthDate date) 
	{
		this.date = date;
	}
	
	public int getQuantity() 
	{
		return quantity;
	}
	public void setQuantity(int quantity) 
	{
		this.quantity = quantity;
	}
	
	public void addQuantity(int quantity2) 
	{
		this.quantity += quantity2;
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if(!(obj instanceof QuantityPerDate))
			return false;
		
		return ((QuantityPerDate)obj).date.equals(date) && ((QuantityPerDate)obj).quantity == quantity;
	}

}
