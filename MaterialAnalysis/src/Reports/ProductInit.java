package Reports;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.Authenticator;
import javax.swing.JOptionPane;

import AnalyzerTools.Analyzer;
import AnalyzerTools.MonthDate;
import Components.TableCellListener;
import MainPackage.CallBack;
import MainPackage.DataBase;
import MainPackage.Globals;
import MainPackage.Globals.FormType;
import MainPackage.Message;
import MapFrames.MainMapFrame;
import MapFrames.ReportViewFrame;

public class ProductInit extends Report
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
		columns[0] = "Catalog Number";
		columns[1] = "Type";
		columns[2] = "Quantity";
		columns[3] = "InitDate";
		columns[4] = "RequireDate";
		
		return columns;
	}
	
	public String[] getRow() 
	{
		Globals globals = new Globals();
		String [] row = new String[5];
		row[0] = this.catalogNumber;
		row[1] = globals.FormTypeToString(this.type);
		row[2] = this.quantity;
		row[3] = this.initDate;
		row[4] = this.requireDate;
		
		return row;
	}
	
	public Message updateValue(String userName , int column, String newValue) throws Exception
	{
		DataBase db = new DataBase();
		String previousRequireDate = this.requireDate;
		switch(column)
		{
			case 0 :
				return null;
			case 1 : 
				return null;
			case 2:
				if(!org.apache.commons.lang3.math.NumberUtils.isCreatable(newValue.trim()))
					throw new Exception("Quantity have to be a numeric value");
				this.quantity = newValue;
				break;
			case 3:
				return null;
			case 4:
				Date date;
				if((date = Globals.isValidDate(newValue)) == null)
					throw new Exception("Please enter a valid date");
				this.requireDate = Globals.dateWithoutHourToString(date);
				break;
			default:
				return null;
		}
		
		String note = "";
		while(true)
		{
			note = JOptionPane.showInputDialog(null , "Enter note for this update", JOptionPane.OK_OPTION);
			if(note == null)
				continue;
			
			break;
		}
		
		String changeDate = Globals.dateWithoutHourToString(Globals.getTodayDate());
		db.addNewInitProductHistory(catalogNumber , quantity , initDate , requireDate , changeDate, note , userName ,  type);
		db.updateInitProduct(catalogNumber , quantity , initDate , previousRequireDate , requireDate , type);
		
		Analyzer analyzer = new Analyzer();
		MonthDate initMonth = new MonthDate(Globals.parseDate(initDate));
		MonthDate calculateMonth = new MonthDate(Globals.addMonths(Globals.getTodayDate(), -Globals.monthsToCalculate - 1));
		boolean ignorePast = initMonth.after(calculateMonth);
		analyzer.updateProductQuantities(userName , catalogNumber , type , ignorePast);
		return null;
	}
	
	public List<Integer> getInvalidEditableColumns()
	{
		List<Integer> invalidEditableColumns = new ArrayList<>();
		invalidEditableColumns.add(0);
		invalidEditableColumns.add(1);
		invalidEditableColumns.add(3);
		
		return invalidEditableColumns;
	}

	public List<Integer> getFilterColumns() 
	{
		List<Integer> filterColumns = new ArrayList<>();
		filterColumns.add(0);
		filterColumns.add(1);
		filterColumns.add(4);
		
		return filterColumns;
	}

	@Override
	public CallBack<Object> getValueCellChangeAction(String email , Authenticator auth , String userName , ReportViewFrame frame, Object... args) 
	{
		List<ProductInit> productsInit = (List<ProductInit>) args[0];
		DataBase db = new DataBase();
		
		CallBack<Object> valueCellChangeAction = new CallBack<Object>()
		{
			@Override
			public Object execute(Object... objects) 
			{
				TableCellListener tcl = (TableCellListener)objects[0];
				int row = tcl.getRow();
				int column = tcl.getColumn();
				String newValue = (String) tcl.getNewValue();
				String oldValue = (String) tcl.getOldValue();
				ProductInit productInit = productsInit.get(row);
				
				try
				{
					productInit.updateValue(userName , column, newValue);
				} 
				catch (Exception e) 
				{
					frame.updateCellValue(row,column,oldValue);
					JOptionPane.showConfirmDialog(null, e.getMessage() ,"Error",JOptionPane.PLAIN_MESSAGE);
					return e;
				}
							
				List<ProductInit> newProductsInit = db.getAllProductsInit(userName);
				productsInit.clear();
				productsInit.addAll(newProductsInit);
				frame.refresh(newProductsInit.stream().map(t -> t.getRow()).toArray(String[][]::new));
				frame.setColumnWidth();
				
				return null;
			}
		};
		
		return valueCellChangeAction;
	}

	@Override
	public CallBack<Object> getDoubleLeftClickAction(String email , Authenticator auth , String userName, ReportViewFrame frame, Object... args) 
	{
		List<ProductInit> productsInit = (List<ProductInit>) args[0];
		DataBase db = new DataBase();
		
		CallBack<Object> doubleLeftClickAction = new CallBack<Object>()
		{
			@Override
			public Object execute(Object... objects) 
			{
				TableCellListener tcl = (TableCellListener)objects[0];
				int row = tcl.getRow();
				int col = tcl.getColumn();
				ProductInit productInit = productsInit.get(row);
				if(col != 0)
					return null;
				
				List<ProductInitHistory> productsInitHistory = db.getProductInitHistory(productInit.getCatalogNumber());
				if(productsInitHistory == null || productsInitHistory.size() == 0)
					return null;
				ReportViewFrame initProductFrame = MainMapFrame.createReportViewFrame(email , auth , userName , productsInitHistory , "Init Product History View");
				
				initProductFrame.show();
				
				return null;
			}
		};
		
		return doubleLeftClickAction;
	}

	@Override
	public CallBack<Object> getRightClickAction(String email , Authenticator auth , String userName , ReportViewFrame frame, Object... args) 
	{
		return null;
	}
}
