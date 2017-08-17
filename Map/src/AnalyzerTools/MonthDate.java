package AnalyzerTools;

import java.text.DateFormatSymbols;
import java.util.Date;
import java.util.Locale;

import mainPackage.Globals;

public class MonthDate extends Date
{

	private static final long serialVersionUID = 1L;
	
	public MonthDate(Date date) 
	{	
		super(Globals.setFirstDayOfMonth(date).getTime());
	}
	
	public MonthDate(String monthOnShortName) 
	{
		super(Globals.parseDate("01/"+ parseShortDate(monthOnShortName)).getTime());
	}

	private static String parseShortDate(String monthOnShortName) 
	{
		DateFormatSymbols dfs = new DateFormatSymbols(Locale.US);
		String[] months = dfs.getShortMonths();
		String monthName = monthOnShortName.split(" ")[0];
		int monthNumber = 0;
		String year = monthOnShortName.split(" ")[1].trim();
		
		for (int i = 0 ; i < months.length ; i++) 
		{
			if(months[i].trim().equals(monthName.trim()))
			{
				monthNumber = i + 1;
				break;
				
			}
		}
		
		return monthNumber + "/" + year;
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
	
	public String shortString()
	{
		DateFormatSymbols dfs = new DateFormatSymbols(Locale.US);
        String[] months = dfs.getShortMonths();
        String nameOfMonth = months[Globals.getMonth(this) - 1];
        return nameOfMonth + " " + Globals.getYear(this);
	}

}

