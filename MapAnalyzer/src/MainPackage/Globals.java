package MainPackage;

import java.time.LocalDateTime;

public class Globals 
{
	//public static final String con = "C:\\Users\\naordalal\\Desktop\\DB.db";
	public static final String con = "O:\\Purchasing\\PO_FollowUp\\Material Analysis\\DB.db";
	
	
	public static String dateWithoutHourToString(LocalDateTime date) 
	{
		int year = date.getYear();
		int month = date.getMonthValue();
		int day = date.getDayOfMonth();

		String s = String.format("%02d", day) + "/" + String.format("%02d", month) + "/" + year;
		
		return s;
	}
}
