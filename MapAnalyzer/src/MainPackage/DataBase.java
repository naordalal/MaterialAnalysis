package MainPackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import org.sqlite.SQLiteConfig;


public class DataBase 
{

	private Connection c = null;
	private PreparedStatement stmt = null;
	
	private void connect()
	{
		try {
			Class.forName("org.sqlite.JDBC");
		    SQLiteConfig config = new SQLiteConfig(); 
		    config.enforceForeignKeys(true);  
		    c = DriverManager.getConnection("jdbc:sqlite:"+Globals.con , config.toProperties());
		    c.setAutoCommit(false);
			}catch ( Exception e ) {
			      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			      JOptionPane.showConfirmDialog(null, "Can't find DB file \nThe file should be in : " + Globals.con,"",JOptionPane.PLAIN_MESSAGE);
			      System.exit(0);
			}
	}
	
	private void closeConnection()
	{
		try {
			if(stmt != null && !c.isClosed())
				stmt.close();
			if(c != null && !c.isClosed())
				c.close();
		} catch (SQLException e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	    
	}
	
	public boolean addWO(String woNumber , String catalogNumber , String quantity , String customer , String date , String description) 
	{
		try
		{
			
			connect();
			stmt = c.prepareStatement("INSERT INTO WorkOrder (WOId , customer , CN , description ,quantity , date ) VALUES (?,?,?,?,?,?)");
			stmt.setString(1, woNumber);
			stmt.setString(2, customer);
			stmt.setString(3, catalogNumber);
			stmt.setString(4, description);
			stmt.setString(5, quantity);
			stmt.setString(6, date);
			stmt.executeUpdate();
			
			c.commit();
			
			closeConnection();
			
			return true;
		
		}
		catch(Exception e)
		{
			try {
				c.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			e.printStackTrace();
			
			closeConnection();
			return false;
		}
	}
	
	public boolean addCustomerOrder(String customer , String orderNumber , String catalogNumber , String description , String quantity , String price , String orderDate , String guaranteedDate) 
	{
		try
		{
			
			connect();
			stmt = c.prepareStatement("INSERT INTO CustomerOrders (orderNumber , customer , orderDate , CN , description ,quantity , price , guaranteedDate) VALUES (?,?,?,?,?,?,?,?)");
			stmt.setString(1, orderNumber);
			stmt.setString(2, customer);
			stmt.setString(3, orderDate);
			stmt.setString(4, catalogNumber);
			stmt.setString(5, description);
			stmt.setString(6, quantity);
			stmt.setString(7, price);
			stmt.setString(8, guaranteedDate);
			stmt.executeUpdate();
			
			c.commit();
			
			closeConnection();
			
			return true;
		
		}
		catch(Exception e)
		{
			try {
				c.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			e.printStackTrace();
			
			closeConnection();
			return false;
		}
	}
	
	
	public boolean addShipment(String customer , String shipmentId , String catalogNumber , String quantity , String shipmentDate , String description) 
	{
		try
		{
			
			connect();
			stmt = c.prepareStatement("INSERT INTO Shipments (shipmentId , customer , CN , description , quantity , shipmentDate) VALUES (?,?,?,?,?,?)");
			stmt.setString(1, shipmentId);
			stmt.setString(2, customer);
			stmt.setString(3, catalogNumber);
			stmt.setString(4, description);
			stmt.setString(5, quantity);
			stmt.setString(6, shipmentDate);
			stmt.executeUpdate();
			
			c.commit();
			
			closeConnection();
			
			return true;
		
		}
		catch(Exception e)
		{
			try {
				c.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			e.printStackTrace();
			
			closeConnection();
			return false;
		}
	}
	
	public void updateProductShipments()
	{
		List<Shipment> shipments = getAllShipments();
		Map<MonthDate,List<Shipment>> newShipmentsPerDate = new HashMap<>();
		
		for (Shipment shipment : shipments) 
		{
			MonthDate monthDate = new MonthDate(shipment.getShipmentDate());
			if(newShipmentsPerDate.containsKey(monthDate))
				newShipmentsPerDate.get(monthDate).add(shipment);
			else
			{
				List<Shipment> shipmentOfMonth = new ArrayList<Shipment>();
				shipmentOfMonth.add(shipment);
				newShipmentsPerDate.put(monthDate , shipmentOfMonth);
			}
		}

		
		Map<String , List<QuantityPerDate>> newProductsQuantityPerDate = new HashMap<>();
		
		Iterator<Entry<MonthDate, List<Shipment>>> it = newShipmentsPerDate.entrySet().iterator();
	    while (it.hasNext()) 
	    {
	        Map.Entry<MonthDate,List<Shipment>> entry = (Map.Entry<MonthDate,List<Shipment>>)it.next();
	        for (Shipment shipment : entry.getValue()) 
	        {
	        	QuantityPerDate quantityPerDate = new QuantityPerDate(entry.getKey(), new Integer(shipment.getQuantity()));
	        	if(newProductsQuantityPerDate.containsKey(shipment.getCatalogNumber()))
	        	{
	        		List<QuantityPerDate> quantityPerDateList = newProductsQuantityPerDate.get(shipment.getCatalogNumber());
	        		int indexOfQuantity = quantityPerDateList.indexOf(quantityPerDate);
	        		if(indexOfQuantity != -1)
	        			quantityPerDateList.get(indexOfQuantity).addQuantity(quantityPerDate.getQuantity());
	        		else
	        			quantityPerDateList.add(quantityPerDate);
	        			
	        		
	        	}
	        	else
	        	{
	        		List<QuantityPerDate> quantityPerDateList = new ArrayList<>();
	        		quantityPerDateList.add(quantityPerDate);
	        		newProductsQuantityPerDate.put(shipment.getCatalogNumber(), quantityPerDateList);
	        	}
			}
	    }
	    
	    Map<String , List<QuantityPerDate>> productsQuantityPerDate = getAllProductsQuantityPerDate();
	    
	    
		
		
	}

	private Map<String, List<QuantityPerDate>> getAllProductsQuantityPerDate() 
	{
		// TODO Auto-generated method stub
		return null;
	}

	private Map<MonthDate, List<Shipment>> getAllShipmentsPerDate() 
	{
		return null;
	}

	private List<Shipment> getAllShipments() 
	{
		return null;
		
	}
}
