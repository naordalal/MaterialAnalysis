package MainPackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

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
	
	public void closeConnection()
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
			stmt = c.prepareStatement("INSERT INTO WorkOrder (WOId , CN , quantity , customer , date , description) VALUES (?,?,?,?,?,?)");
			stmt.setString(1, woNumber);
			stmt.setString(2, catalogNumber);
			stmt.setString(3, quantity);
			stmt.setString(4, customer);
			stmt.setString(5, date);
			stmt.setString(5, description);
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
			stmt = c.prepareStatement("INSERT INTO CustomerOrders (customer , orderNumber , CN , quantity , price , description , orderDate , guaranteedDate) VALUES (?,?,?,?,?,?,?,?)");
			stmt.setString(1, customer);
			stmt.setString(2, orderNumber);
			stmt.setString(3, catalogNumber);
			stmt.setString(4, quantity);
			stmt.setString(5, price);
			stmt.setString(6, description);
			stmt.setString(7, orderDate);
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
	
	
	public boolean addShipment(String customer , String orderNumber , String catalogNumber , String quantity , String shipmentDate , String description) 
	{
		try
		{
			
			connect();
			stmt = c.prepareStatement("INSERT INTO Shipments (customer , orderNumber , CN , quantity , shipmentDate , description) VALUES (?,?,?,?,?,?)");
			stmt.setString(1, customer);
			stmt.setString(2, orderNumber);
			stmt.setString(3, catalogNumber);
			stmt.setString(4, quantity);
			stmt.setString(5, shipmentDate);
			stmt.setString(6, description);
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
}
