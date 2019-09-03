package Forms;

import java.util.Date;
import java.util.List;

import javax.mail.Authenticator;

import MainPackage.CallBack;
import MainPackage.Globals.FormType;
import MapFrames.ReportViewFrame;

public abstract class Form 
{
	private int id;
	private String catalogNumber;
	private String quantity;
	private Date requestDate , createDate;
	
	public Form() 
	{
		
	}
	
	public Form(int id , String catalogNumber , String quantity , Date createDate , Date requestDate) 
	{
		this.id = id;
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

	public int getId()
	{
		return id;
	}

	public void setId(int id) 
	{
		this.id = id;
	}

	
	public abstract String [] getColumns();

	public abstract String[] getRow();
	
	public abstract boolean canEdit();

	public abstract void updateValue(int column, String newValue , String userName) throws Exception;
	
	public abstract List<Integer> getInvalidEditableColumns();

	public static boolean isNeedRequireDate(String className) 
	{
		try {
			Class<? extends Form> form = (Class<? extends Form>) Class.forName(className);
			return form.newInstance().isNeedRequireDate();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean isNeedInit(String className) 
	{
		try {
			Class<? extends Form> form = (Class<? extends Form>) Class.forName(className);
			return form.newInstance().isNeedInit();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public abstract boolean isNeedRequireDate();
	
	public abstract boolean isNeedInit();
	
	public abstract List<Integer> getFilterColumns(); 
	
	public abstract CallBack<Object> getValueCellChangeAction(String email , Authenticator auth , String userName , ReportViewFrame frame , Object ... args);
	public abstract CallBack<Object> getDoubleLeftClickAction(String email , Authenticator auth , String userName , ReportViewFrame frame , Object ... args);
	public abstract CallBack<Object> getRightClickAction(String email , Authenticator auth , String userName , ReportViewFrame frame , Object ... args);
}
