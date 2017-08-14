package mainPackage;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import mainPackage.Globals.Sort;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class Excel 
{

	
	public static Cell findCell (XSSFSheet sheet, String contents)
	{
		Iterator<Row> iterator = sheet.iterator();
		
		while(iterator.hasNext())
		{
			Row row = iterator.next();
			
			Iterator<Cell> cellIterator = row.cellIterator();
			
			while(cellIterator.hasNext())
			{
				Cell cell = cellIterator.next();
				if(cell.getCellType() == Cell.CELL_TYPE_STRING)
				{
					if(cell.getStringCellValue().trim().replaceAll(" ", "").equalsIgnoreCase(contents.trim().replaceAll(" ", "")))
						return cell;
				}
			}
		}
		
		return null;
	}

	public static Cell findCellInRow(Row row , String contents)
	{
		Iterator<Cell> cellIterator = row.cellIterator();
		
		while(cellIterator.hasNext())
		{
			Cell cell = cellIterator.next();
			if(cell.getCellType() == Cell.CELL_TYPE_STRING)
			{
				if(cell.getStringCellValue().trim().equalsIgnoreCase(contents.trim()))
					return cell;
			}
		}
		
		return null;
	}
	public static Cell getCell(XSSFSheet ordersSheet, int rowNumber, int column) 
	{
		
		Row row = ordersSheet.getRow(rowNumber);
		
		return row.getCell(column);
	}
	
	public static int rowsNumberFromRow(XSSFSheet sheet , int fromRow , int columnIndex)
	{
		int rows = fromRow;
		for(int i = fromRow ; i < sheet.getPhysicalNumberOfRows() ; i++)
		{	
			Row row = sheet.getRow(i);
			Cell cell = row.getCell(columnIndex);
			
			if(cell != null)
			{
				boolean nullString = cell.getCellType() == Cell.CELL_TYPE_STRING && cell.getStringCellValue().trim().equals("");
				boolean notString = cell.getCellType() != Cell.CELL_TYPE_STRING || cell.getCellType() == Cell.CELL_TYPE_BLANK;
				
				if(nullString || notString)
					break;
			}
			else
				break;
			
			rows ++;
		}
		
		return rows;
	}
	
	public static void sortSheet(XSSFSheet sheet , List<Integer> columns , List<Sort> sortMethods, int rowStart)
	{
		
	    int lastRow = sheet.getLastRowNum();
	    for (int i = 0 ; i < columns.size() ; i++) 
	    {
	    	boolean sorting = true;
	    	int column = columns.get(i);
	    	
		    while (sorting) 
		    {
		        sorting = false;
		        
		        for(int index = 0 ; index < sheet.getPhysicalNumberOfRows() ; index++)
		        {
		        	Row row = sheet.getRow(index);
		            // skip if this row is before first to sort
		            if (row.getRowNum() < rowStart) 
		            	continue;
		            
		            // end if this is last row
		            if (lastRow == row.getRowNum()) 
		            	break;
		            
		            Row row2 = sheet.getRow(row.getRowNum()+1);
		            if (row2 == null) 
		            	continue;     

	            	boolean sorted;
	            	
	            	if(i > 0)
	            		sorted = compareColumnsOfTwoRows(row , row2 , columns.subList(0, i));
	            	else
	            		sorted = true;
	            	
	            	if(row.getCell(column).getCellType() == Cell.CELL_TYPE_STRING)
		            {
		            	String firstValue = (row.getCell(column) != null) ? row.getCell(column).getStringCellValue() : "";
			            String secondValue = (row2.getCell(column) != null) ? row2.getCell(column).getStringCellValue() : "";
			            //compare cell from current row and next row - and switch if secondValue should be before first
			            if(sortMethods.get(i).equals(Globals.Sort.ASC))
			            {
			            	Date firstDate = Globals.isValidDate(firstValue);
			            	Date secondDate = Globals.isValidDate(secondValue);
			            	
			            	if(firstDate != null && secondDate != null  && sorted)
			            	{
			            		if(secondDate.before(firstDate))
			            		{
			            			swapRows(row , row2);
						            sorting = true;
			            		}
			            	}
			            	else if(firstDate == null && secondDate != null && sorted)
			            	{
			            		swapRows(row , row2);
					            sorting = true;
			            	}
			            	else if (secondValue.compareToIgnoreCase(firstValue) < 0 && firstDate == null && sorted) 
				            {                    
				                swapRows(row , row2);
				                sorting = true;
				            }
			            }
			            else
			            {
			            	Date firstDate = Globals.isValidDate(firstValue);
			            	Date secondDate = Globals.isValidDate(secondValue);
			            	
			            	if(firstDate != null && secondDate != null && sorted)
			            	{
			            		if(secondDate.after(firstDate))
			            		{
			            			swapRows(row , row2);
						            sorting = true;
			            		}
			            	}
			            	else if(secondDate == null && firstDate != null && sorted)
			            	{
			            		swapRows(row , row2);
					            sorting = true;
			            	}
			            	else if (secondValue.compareToIgnoreCase(firstValue) > 0 && secondDate == null && sorted) 
				            {                    
				                swapRows(row , row2);
				                sorting = true;
				            }
			            }
			            
		            }
		            else if(row.getCell(column).getCellType() == Cell.CELL_TYPE_NUMERIC)
		            {
		            	double firstValue = (row.getCell(column) != null) ? row.getCell(column).getNumericCellValue() : 0;
		            	double secondValue = (row2.getCell(column) != null) ? row2.getCell(column).getNumericCellValue() : 0;
			            //compare cell from current row and next row - and switch if secondValue should be before first
		            	if(sortMethods.get(i).equals(Globals.Sort.ASC))
			            {
		            		if (secondValue < firstValue && sorted) 
				            {                    
				                swapRows(row , row2);
				                sorting = true;
				            }

			            }
		            	else
		            	{
		            		if (secondValue > firstValue && sorted) 
				            {                    
				                swapRows(row , row2);
				                sorting = true;
				            }
		            	}
			        }
	            }
	            
	        }
		    
		    
		    }
	    }
		

	private static void swapRows(Row row, Row row2) 
	{
		for(int column = 0 ; column < Math.min(row.getPhysicalNumberOfCells(), row2.getPhysicalNumberOfCells()) ; column ++)
		{
			Cell cell1 = row.getCell(column);
			Cell cell2 = row2.getCell(column);
			XSSFCellStyle style = (XSSFCellStyle) cell1.getCellStyle();
			cell1.setCellStyle(cell2.getCellStyle());
			cell2.setCellStyle(style);
			
			
			if(cell1.getCellType() == Cell.CELL_TYPE_NUMERIC)
			{
				double value1 = cell1.getNumericCellValue();
				if(cell2.getCellType() == Cell.CELL_TYPE_NUMERIC)
				{
					double value2 = cell2.getNumericCellValue();
					cell1.setCellValue(value2);
					cell2.setCellValue(value1);
				}
				else
				{
					String value2 = cell2.getStringCellValue();
					cell1.setCellValue(value2);
					cell2.setCellValue(value1);
				}
			}
			else
			{			
				String value1 = cell1.getStringCellValue();
				if(cell2.getCellType() == Cell.CELL_TYPE_NUMERIC)
				{
					double value2 = cell2.getNumericCellValue();
					cell1.setCellValue(value2);
					cell2.setCellValue(value1);
				}
				else
				{
					String value2 = cell2.getStringCellValue();
					cell1.setCellValue(value2);
					cell2.setCellValue(value1);
				}
			}
		}
		
	}

	private static boolean compareColumnsOfTwoRows(Row row, Row row2,
			List<Integer> columns) 
	{
		for (Integer column : columns) 
		{
			Cell cell1 = row.getCell(column);
			Cell cell2 = row2.getCell(column);
			
			if(cell1.getCellType() == Cell.CELL_TYPE_NUMERIC && cell2.getCellType() == Cell.CELL_TYPE_NUMERIC)
			{
				double value1 = cell1.getNumericCellValue();
				double value2 = cell2.getNumericCellValue();
				if(value1 != value2)
					return false;
			}
			else if(cell1.getCellType() == Cell.CELL_TYPE_STRING && cell2.getCellType() == Cell.CELL_TYPE_STRING)
			{
				String value1 = cell1.getStringCellValue();
				String value2 = cell2.getStringCellValue();
				if(!value1.equalsIgnoreCase(value2))
					return false;
			}
			else 
				return false;
		}
		return true;
	}
}