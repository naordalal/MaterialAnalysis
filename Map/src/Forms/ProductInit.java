package Forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import AnalyzerTools.Analyzer;
import MainPackage.DataBase;
import MainPackage.Globals;
import MainPackage.Globals.FormType;

public class ProductInit
{
	private String catalogNumber;
	private String quantity;
	private String initDate;
	private String requireDate;
	private FormType type;

	public ProductInit(String catalogNumber , String quantity , String initDate , String requireDate ,FormType type)
	{
		this.catalogNumber = catalogNumber;
		this.quantity = quantity;
		this.initDate = initDate;
		this.requireDate = requireDate;
		this.type = type;
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

	public String getInitDate() {
		return initDate;
	}

	public void setInitDate(String initDate)
	{
		this.initDate = initDate;
	}

	public String getRequireDate() 
	{
		return requireDate;
	}

	public void setRequireDate(String requireDate) 
	{
		this.requireDate = requireDate;
	}
	
	public FormType getType() 
	{
		return type;
	}

	public void setType(FormType type)
	{
		this.type = type;
	}
		
	public String [] getColumns()
	{
		String [] columns = new String[5];
		columns[0] = "Type";
		columns[1] = "Catalog Number";
		columns[2] = "Quantity";
		columns[3] = "InitDate";
		columns[4] = "RequireDate";
		
		return columns;
	}
	
	public String[] getRow() 
	{
		Globals globals = new Globals();
		String [] row = new String[5];
		row[0] = globals.FormTypeToString(this.type);
		row[1] = this.catalogNumber;
		row[2] = this.quantity;
		row[3] = this.initDate;
		row[4] = this.requireDate;
		
		return row;
	}
	
	public void updateValue(int column, String newValue) throws Exception
	{
		DataBase db = new DataBase();
		String previousRequireDate = this.requireDate;
		switch(column)
		{
			case 0 :
				return;
			case 1 : 
				return;
			case 2:
				if(!org.apache.commons.lang3.math.NumberUtils.isCreatable(newValue.trim()))
					throw new Exception("Quantity have to be a numeric value");
				this.quantity = newValue;
				break;
			case 3:
				return;
			case 4:
				Date date;
				if((date = Globals.isValidDate(newValue)) == null)
					throw new Exception("Please enter a valid date");
				this.requireDate = Globals.dateWithoutHourToString(date);
				break;
			default:
				break;
		}

		db.updateInitProduct(catalogNumber , quantity , initDate , previousRequireDate , requireDate , type);
		Analyzer analyzer = new Analyzer();
		analyzer.updateProductQuantities(catalogNumber , type);
	}
	
	public List<Integer> getInvalidEditableColumns()
	{
		List<Integer> invalidEditableColumns = new ArrayList<>();
		invalidEditableColumns.add(0);
		invalidEditableColumns.add(1);
		invalidEditableColumns.add(3);
		
		return invalidEditableColumns;
	}

	public static List<Integer> getFilterColumns() 
	{
		List<Integer> filterColumns = new ArrayList<>();
		filterColumns.add(0);
		filterColumns.add(1);
		filterColumns.add(4);
		
		return filterColumns;
	}
}
