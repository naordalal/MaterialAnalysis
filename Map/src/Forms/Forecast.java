package Forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import AnalyzerTools.Analyzer;
import mainPackage.Globals;

public class Forecast extends Form
{
	
	private String customer;
	private String description;
	private String notes;

	public Forecast(int id , String customer , String catalogNumber , String quantity , Date initDate , Date requireDate , String description , String notes) 
	{
		super(id , catalogNumber, quantity, initDate, requireDate);
		
		this.customer = customer;
		this.description = description;
		this.notes = notes;
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

	public String getNotes() 
	{
		return notes;
	}

	public void setNotes(String notes) 
	{
		this.notes = notes;
	}

	@Override
	public String[] getColumns() 
	{
		String [] columns = new String[7];
		columns[0] = "Customer";
		columns[1] = "Catalog Number";
		columns[2] = "Description";
		columns[3] = "Quantity";
		columns[4] = "Init Date";
		columns[5] = "Require Date";
		columns[6] = "Notes";
		
		return columns;
	}

	@Override
	public String[] getRow() 
	{
		String [] row = new String[7];
		row[0] = this.customer;
		row[1] = super.getCatalogNumber();
		row[2] = this.description;
		row[3] = super.getQuantity();
		row[4] = Globals.dateWithoutHourToString(super.getCreateDate());
		row[5] = Globals.dateWithoutHourToString(super.getRequestDate());
		row[6] = this.notes;
		
		return row;
	}

	@Override
	public boolean canEdit() 
	{
		return true;
	}

	@Override
	public void updateValue(int column, String newValue) throws Exception 
	{
		if(!canEdit())
			return;
		
		switch(column)
		{
			case 0:
				this.customer = newValue;
			case 1:
				super.setCatalogNumber(newValue);
				break;
			case 2:
				this.description = newValue;
				break;
			case 3:
				if(StringUtils.isNumeric(newValue))
					super.setQuantity(newValue);
				else
					throw new Exception("Quantity have to be a numeric value");
				break;
			case 5:
				Date requestDate;
				if((requestDate = Globals.isValidDate(newValue)) != null)
					super.setRequstDate(requestDate);
				else
					throw new Exception("Request date have to be a date format");
				break;
			case 6:
				this.notes = newValue;
				break;
				
			default:
				return;
		}
		
		Analyzer analyzer = new Analyzer();
		analyzer.updateFC(super.getId(), customer, super.getCatalogNumber(), super.getQuantity(), Globals.dateWithoutHourToString(super.getCreateDate())
				, Globals.dateWithoutHourToString(super.getRequestDate()), this.description, this.notes);
		
	}
	
	@Override
	public List<Integer> getInvalidEditableColumns() 
	{
		List<Integer> columns = new ArrayList<>();
		columns.add(4);
		return columns;
	}

}
