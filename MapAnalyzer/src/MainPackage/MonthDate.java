package MainPackage;

import java.util.Date;

public class MonthDate extends Date
{

	private static final long serialVersionUID = 1L;
	
	public MonthDate(Date date) 
	{	
		super(Globals.setFirstDayOfMonth(date).getTime());
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if(!(obj instanceof Date))
			return false;
		
		return Globals.getMonth((Date) obj) == Globals.getMonth(this) && Globals.getYear((Date) obj) == Globals.getYear(this);
	}
	
	@Override
	public String toString() 
	{
		return Globals.dateWithoutHourToString(this);
	}

}
