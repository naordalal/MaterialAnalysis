package MainPackage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;

public class Globals 
{
	public static final String con = "C:\\Users\\naordalal\\Desktop\\DB3.db";
	//public static final String con = "O:\\Purchasing\\PO_FollowUp\\Material Analysis\\DB.db";
	
	public final String shipmentsFilePath = "C:\\Users\\naordalal\\Desktop\\SHIPMENTS.txt";
	public final String customerOrdersFilePath = "C:\\Users\\naordalal\\Desktop\\PO.txt";
	public final String WOFilePath = "C:\\Users\\naordalal\\Desktop\\WO.txt";
	
	/*public final String shipmentsFilePath = "O:\\Purchasing\\PO_FollowUp\\Material Analysis\\MAP system\\Reports\\SHIPMENTS.txt";
	public final String customerOrdersFilePath = "O:\\Purchasing\\PO_FollowUp\\Material Analysis\\MAP system\\Reports\\PO.txt";
	public final String WOFilePath = "O:\\Purchasing\\PO_FollowUp\\Material Analysis\\MAP system\\Reports\\WO.txt";*/

	public final String woNumberColumn = "מספר פקודה";
	public final String catalogNumberColumn = "מקט";
	public final String quantityColumn = "כמות";
	public final String customerColumn = "לקוח";
	public final String dateColumn = "תאריך פקודה";
	public final String descriptionColumn = "תאור";
	
	public final String customerIdColumn = "מס לקוח";
	public final String orderNumberColumn = "מס הזמנה";
	public final String customerOrderNumberColumn = "מס הזמנת לקוח";
	public final String orderDateColumn = "תאריך הזמנה";
	public final String priceColumn = "מחיר יחידה";
	public final String quantityOrderColumn = "כמות מוזמנת";
	public final String guaranteedDateColumn = "תאריך מובטח";
	
	public final String orderIdColumn = "מס הז";
	public final String orderCustomerIdColumn = "סימוכין";
	public final String shipmentDateColumn = "תאריך משלוח";
	
	public static final int monthsToIgnore = 6;
	public final String charsetName = "IBM862";
	
	public enum FormType {
	    SHIPMENT,WO,PO,FC
	}
	
	
	public static String dateWithoutHourToString(Date date) 
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		String s = String.format("%02d", day) + "/" + String.format("%02d", month + 1) + "/" + year;
		
		return s;
	}
	
	public static String dateToSqlFormatString(Date date) 
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		String s = year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", day);
		
		return s;
	}
	
	public static Date parseDate(String date)
	{
		DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yy");
		DateFormat outsourceFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date parseDate;
		Calendar c = Calendar.getInstance();
		try {
			parseDate = sourceFormat.parse(date);
			String toExp = outsourceFormat.format(parseDate);
			parseDate = outsourceFormat.parse(toExp);
			c.setTime(parseDate);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			parseDate = c.getTime();
		} catch (ParseException e) {

			e.printStackTrace();
			return null;
		}
		
		return parseDate;
	}

	public static int getMonth(Date date) 
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		return c.get(Calendar.MONTH) + 1;
	}

	public static int getYear(Date date) 
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		return c.get(Calendar.YEAR);
	}

	public static Date setFirstDayOfMonth(Date date) 
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);
		
		date = c.getTime();
		
		return date;
	}
	
	public static Date getTodayDate()
	{
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}
	
	public static Date addDays(Date date ,int days)
	{
		Calendar c2 = Calendar.getInstance();
		c2.setTime(date);
		c2.set(Calendar.HOUR_OF_DAY, 0);
		c2.set(Calendar.MINUTE, 0);
		c2.set(Calendar.SECOND, 0);
		c2.set(Calendar.MILLISECOND, 0);
		c2.add(Calendar.DAY_OF_MONTH, days);
		return c2.getTime();
	}
	
	public static Date addMonths(Date date ,int months)
	{
		Calendar c2 = Calendar.getInstance();
		c2.setTime(date);
		c2.set(Calendar.HOUR_OF_DAY, 0);
		c2.set(Calendar.MINUTE, 0);
		c2.set(Calendar.SECOND, 0);
		c2.set(Calendar.MILLISECOND, 0);
		c2.add(Calendar.MONTH, months);
		return c2.getTime();
	}

	public static String parseDateToSqlFormatString(String date) 
	{
		return dateToSqlFormatString(parseDate(date));
	}

	public static Date parseDateFromSqlFormat(String date) 
	{
		DateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat outsourceFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date parseDate;
		Calendar c = Calendar.getInstance();
		try {
			parseDate = sourceFormat.parse(date);
			String toExp = outsourceFormat.format(parseDate);
			parseDate = outsourceFormat.parse(toExp);
			c.setTime(parseDate);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			parseDate = c.getTime();
		} catch (ParseException e) {

			e.printStackTrace();
			return null;
		}
		
		return parseDate;
	}

	public static boolean isFirstDayOfMonth(Date date) 
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		return c.get(Calendar.DAY_OF_MONTH) == 1;
	}

	public static <T> List<T> topologicalSort(List<T> tree , CallBack<List<T>> callBack)
    {
		int V = tree.size();
        // Create a array to store indegrees of all
        // vertices. Initialize all indegrees as 0.
        int indegree[] = new int[V];
         
        // Traverse adjacency lists to fill indegrees of
        // vertices. This step takes O(V+E) time        
        for(T node : tree)
        {
            List<T> temp = callBack.execute(node);
            for(T elem : temp)
            {
                indegree[tree.indexOf(elem)]++;
            }
        }
         
        // Create a queue and enqueue all vertices with
        // indegree 0
        Queue<Integer> q = new LinkedList<Integer>();
        for(int i = 0; i < V; i++)
        {
            if(indegree[i] == 0)
                q.add(i);
        }
         
        // Initialize count of visited vertices
        int cnt = 0;
         
        // Create a vector to store result (A topological
        // ordering of the vertices)
        Vector <Integer> topOrder=new Vector<Integer>();
        while(!q.isEmpty())
        {
            // Extract front of queue (or perform dequeue)
            // and add it to topological order
            int u = q.poll();
            topOrder.add(u);
             
            // Iterate through all its neighboring nodes
            // of dequeued node u and decrease their in-degree
            // by 1
            List<T> temp = callBack.execute(tree.get(u));
            for(T elem : temp)
            {
            	int index = tree.indexOf(elem);
                // If in-degree becomes zero, add it to queue
                if(--indegree[index] == 0)
                    q.add(index);
            }
            cnt++;
        }
         
        // Check if there was a cycle       
        if(cnt != V)
        {
            System.out.println("There exists a cycle in the graph");
            return null;
        }
         
        // Print topological order   
        List<T> topologicList = new ArrayList<>();
        for(int i : topOrder)
        {
        	topologicList.add(tree.get(i));
        }
        
        return topologicList;
    }
	
	
}

