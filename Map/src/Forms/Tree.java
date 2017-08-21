package Forms;

import java.util.ArrayList;
import java.util.List;

import MainPackage.DataBase;

public class Tree 
{
	private String catalogNumber;
	private String customer;
	private String description;
	private String fatherCN;
	private String quantity;
	private String alias;

	public Tree(String catalogNumber , String customer , String description , String fatherCN , String quantity , String alias) 
	{
		this.catalogNumber = catalogNumber;
		this.customer = customer;
		this.description = description;
		this.fatherCN = fatherCN;
		this.quantity = quantity;
		this.alias = alias;
	}

	public String getCatalogNumber() 
	{
		return catalogNumber;
	}

	public void setCatalogNumber(String catalogNumber) 
	{
		this.catalogNumber = catalogNumber;
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

	public String getFatherCN() 
	{
		return fatherCN;
	}

	public void setFatherCN(String fatherCN) 
	{
		this.fatherCN = fatherCN;
	}

	public String getQuantity()
	{
		return quantity;
	}

	public void setQuantity(String quantity) 
	{
		this.quantity = quantity;
	}

	public String getAlias()
	{
		return alias;
	}

	public void setAlias(String alias) 
	{
		this.alias = alias;
	}

	public String [] getColumns()
	{
		String [] columns = new String[6];
		columns[0] = "Catalog Number";
		columns[1] = "Customer";
		columns[2] = "Description";
		columns[3] = "Father Catalog Number";
		columns[4] = "Quantity";
		columns[5] = "Rev";
		
		return columns;
	}
	
	public String[] getRow() 
	{
		String [] row = new String[6];
		row[0] = this.catalogNumber;
		row[1] = this.customer;
		row[2] = this.description;
		row[3] = this.fatherCN;
		row[4] = this.quantity;
		row[5] = (this.alias == null) ? "" : this.alias;
		
		return row;
	}
	
	public void updateValue(String userName , int column, String newValue) throws Exception
	{
		DataBase db = new DataBase();
		switch(column)
		{
			case 0 : 
				return;
			case 1:
				return;
			case 2:
				this.description = newValue;
				break;
			case 3:
				return;
			case 4:
				if(!org.apache.commons.lang3.StringUtils.isNumeric(newValue.trim()))
					throw new Exception("Quantity have to be a numeric value");
				this.quantity = newValue;
				break;
			case 5:
				if(!db.getAllCatlogNumberOfCustomer(customer).contains(newValue))
					throw new Exception("Invalid catalog number , catalog number does not exist");
				this.alias = newValue;
				break;
				
			default:
				return;
		}
		
		db.updateTree(catalogNumber, description , fatherCN ,quantity , alias);
		
	}
	
	public List<Integer> getInvalidEditableColumns()
	{
		List<Integer> invalidEditableColumns = new ArrayList<>();
		invalidEditableColumns.add(0);
		invalidEditableColumns.add(1);
		invalidEditableColumns.add(3);
		
		return invalidEditableColumns;
	}
}
