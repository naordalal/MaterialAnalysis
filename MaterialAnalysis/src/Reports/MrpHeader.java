package Reports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.mail.Authenticator;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import AnalyzerTools.Analyzer;
import AnalyzerTools.MonthDate;
import Components.TableCellListener;
import MainPackage.CallBack;
import MainPackage.DataBase;
import MainPackage.Message;
import MainPackage.Pair;
import MapFrames.ReportViewFrame;

public class MrpHeader extends Report
{

	private String catalogNumber;
	private String customer;
	private String description;
	private Map<MonthDate , Double> quantityPerMonth;
	
	public MrpHeader(String catalogNumber , String customer , String description , Map<MonthDate, Double> quantityPerMonth)
	{
		this.catalogNumber = catalogNumber;
		this.customer = customer;
		this.description = description;
		this.quantityPerMonth = quantityPerMonth;
	}
	
	public Map<MonthDate , Double> getQuantityPerMonth()
	{
		return quantityPerMonth;
	}
	
	@Override
	public String[] getRow() 
	{
		int monthCount = quantityPerMonth.size();
		String [] row = new String[3 + monthCount];
		row[0] = catalogNumber;
		row[1] = customer;
		row[2] = description;
		List<MonthDate> months = quantityPerMonth.keySet().stream().collect(Collectors.toList());
		Collections.sort(months);
		
		int index = 3;
		for (MonthDate month : months)
		{
			row[index] = quantityPerMonth.get(month).intValue() + "";
			index++;
		}
		
		return row;
	}

	@Override
	public String[] getColumns() 
	{
		int monthCount = quantityPerMonth.size();
		String [] column = new String[3 + monthCount];
		column[0] = "Catalog Number";
		column[1] = "Customer";
		column[2] = "Description";
		List<MonthDate> months = quantityPerMonth.keySet().stream().collect(Collectors.toList());
		Collections.sort(months);
		
		int index = 3;
		for (MonthDate month : months)
		{
			column[index] = month.shortString();
			index++;
		}
		
		return column;
	}

	@Override
	public Message updateValue(String userName, int column, String newValue) throws Exception 
	{
		return null;
	}

	@Override
	public List<Integer> getInvalidEditableColumns() 
	{
		int monthCount = quantityPerMonth.size();
		int columnsLength = 3 + monthCount;
		return IntStream.rangeClosed(0, columnsLength - 1).boxed().collect(Collectors.toList());
	}

	@Override
	public List<Integer> getFilterColumns() 
	{
		List<Integer> filterColumns = new ArrayList<>();
		filterColumns.add(0);
		filterColumns.add(1);
		filterColumns.add(2);
		
		return filterColumns;
	}

	@Override
	public CallBack<Object> getValueCellChangeAction(String email, Authenticator auth, String userName,
			ReportViewFrame frame, Object... args) {
		return null;
	}

	@Override
	public CallBack<Object> getDoubleLeftClickAction(String email, Authenticator auth, String userName,
			ReportViewFrame frame, Object... args) {
		CallBack<Object> doubleLeftClickAction = new CallBack<Object>()
		{
			private DataBase db = new DataBase();

			@Override
			public Object execute(Object... objects) 
			{
				TableCellListener tcl = (TableCellListener)objects[0];
				int row = tcl.getRow();
				int column = tcl.getColumn();
				
				if(column == 0)
				{
					String product = getProductOnRow(tcl.getTable() , row);
					StringBuilder description = new StringBuilder();
					description.append("Material availability difference ");
					List<Pair<String, Integer>> fathers = db.getFathers(product);
					for (Pair<String, Integer> father : fathers) 
					{
						description.append("- Material availability difference of ");
						description.append(father.getLeft() + "(father catalog number) * ");
						description.append(father.getRight());
						description.append("\n");
					}
					JOptionPane.showConfirmDialog(null, description.toString() , "Formula explanation",JOptionPane.PLAIN_MESSAGE); 
				}
				
				return null;
			}
		};
		
		return doubleLeftClickAction;
	}

	protected String getProductOnRow(JTable table, int row) 
	{
		return (String) table.getValueAt(row, 0);
	}
	

	@Override
	public CallBack<Object> getRightClickAction(String email, Authenticator auth, String userName,
			ReportViewFrame frame, Object... args) {
		return null;
	}


}
