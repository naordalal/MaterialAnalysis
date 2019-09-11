package MainPackage;


public class QuantityPerDate 
{
	private MonthDate date;
	private double quantity;
	
	public QuantityPerDate(MonthDate date , double quantity) 
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
	
	public double getQuantity() 
	{
		return quantity;
	}
	public void setQuantity(double quantity) 
	{
		this.quantity = quantity;
	}
	
	public void addQuantity(double quantity2) 
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
