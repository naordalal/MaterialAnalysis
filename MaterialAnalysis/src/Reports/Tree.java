package Reports;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.Authenticator;
import javax.swing.JOptionPane;

import AnalyzerTools.Analyzer;
import AnalyzerTools.MonthDate;
import Components.TableCellListener;
import MainPackage.CallBack;
import MainPackage.DataBase;
import MainPackage.Globals;
import MainPackage.Message;
import MapFrames.ReportViewFrame;

public class Tree extends Report
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
	
	public Message updateValue(String userName , int column, String newValue) throws Exception
	{
		DataBase db = new DataBase();
		Message message = null;
		String previousFatherCN = fatherCN;
		boolean updateMap = false;
		switch(column)
		{
			case 0 : 
				return null;
			case 1:
				List<String> customers = db.getCustomersOfUser(userName);
				if(!customers.contains(newValue))
					throw new Exception("Invalid customer , customer does not exist");
				
				this.customer = newValue;
				break;
			case 2:
				this.description = newValue;
				break;
			case 3:
				if(!db.getAllCatalogNumberOfCustomer(customer).contains(newValue.trim()))
					throw new Exception("Invalid catalog number , catalog number does not exist");
				if(newValue.trim().equals(catalogNumber.trim()))
					throw new Exception("Invalid catalog number , cannot be defined as a child and a father");
				if(db.getFathers(catalogNumber).stream().filter(father -> father.getLeft().equals(newValue.trim())).count() > 0)
					throw new Exception("This catalog number is already defined as father of this product");
				
				if(quantity == null || quantity.equals("0") || quantity.equals(""))
					message = new Message("Please enter a quantity" , 4);
				else
				{
					message = null;
					updateMap = true;
				}	
				this.fatherCN = newValue;
				break;
			case 4:
				if(!org.apache.commons.lang3.StringUtils.isNumeric(newValue.trim()))
					throw new Exception("Quantity have to be a numeric value");
				else if(fatherCN == null || fatherCN.equals(""))
					throw new Exception("You cannot change quantity for empty father catalog number");
				
				this.quantity = newValue;
				if(quantity == null || quantity.equals("0") || quantity.equals(""))
				{
					this.quantity = "";
					this.fatherCN = "";	
				}
				message = null;
				updateMap = true;
				break;
			case 5:
				if(!newValue.trim().equals("") && !db.getAllCatalogNumberOfCustomer(customer).contains(newValue))
					throw new Exception("Invalid catalog number , catalog number does not exist");
				
				if(newValue.trim().equals(this.catalogNumber.trim()))
					throw new Exception("Invalid catalog number , rev cannot be equals to current catalog number");
				
				this.alias = (!newValue.trim().equals("")) ? newValue : null;
				message = null;
				break;
				
			default:
				message = null;
				break;
		}
		
		db.updateTree(catalogNumber, customer , description , previousFatherCN , fatherCN , quantity , alias);
		
		Analyzer analyzer = new Analyzer();
		
		if(updateMap)
			analyzer.updateLastMap(null , catalogNumber);
		
		return message;
		
	}
	
	public List<Integer> getInvalidEditableColumns()
	{
		List<Integer> invalidEditableColumns = new ArrayList<>();
		invalidEditableColumns.add(0);
		//invalidEditableColumns.add(1);
		
		return invalidEditableColumns;
	}

	public List<Integer> getFilterColumns() 
	{
		List<Integer> filterColumns = new ArrayList<>();
		filterColumns.add(0);
		filterColumns.add(1);
		filterColumns.add(2);
		
		return filterColumns;
	}

	@Override
	public CallBack<Object> getValueCellChangeAction(String email , Authenticator auth, String userName , ReportViewFrame frame, Object... args) 
	{
		List<Tree> trees = (List<Tree>) args[0];
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
				Tree tree = trees.get(row);
				Message message;
				try
				{
					message = tree.updateValue(userName , column , newValue);
				} 
				catch (Exception e) 
				{
					frame.updateCellValue(row,column,oldValue);
					JOptionPane.showConfirmDialog(null, e.getMessage() ,"Error",JOptionPane.PLAIN_MESSAGE);
					return e;
				}
				
				while(message != null)
				{
					boolean validInput = false;
					while(!validInput)
					{
						String answer = JOptionPane.showInputDialog(null ,message.getMessage(), "" , JOptionPane.OK_OPTION);
						if(answer != null)
						{
							try 
							{
								message = tree.updateValue(userName , message.getColumn() , answer.trim());
								validInput = true;
							} catch (Exception e) 
							{
								JOptionPane.showConfirmDialog(null, e.getMessage() ,"Error",JOptionPane.PLAIN_MESSAGE);
							}
						}

					}
						
				}
								
				List<Tree> newTrees = db.getAllTrees(userName , null);
				trees.clear();
				trees.addAll(newTrees);
				frame.refresh(newTrees.stream().map(t -> t.getRow()).toArray(String[][]::new));
				frame.setColumnWidth();
				
				return null;
			}
		};
		
		return valueCellChangeAction;
	}

	@Override
	public CallBack<Object> getDoubleLeftClickAction(String email , Authenticator auth, String userName , ReportViewFrame frame, Object... args) 
	{
		return null;
	}

	@Override
	public CallBack<Object> getRightClickAction(String email , Authenticator auth, String userName , ReportViewFrame frame, Object... args) 
	{
		return null;
	}
}
