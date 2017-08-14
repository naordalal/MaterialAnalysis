package Senders;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Authenticator;
import javax.swing.JOptionPane;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import mainPackage.Globals;
import mainPackage.SendEmail;
import mainPackage.noValidEmailException;

public abstract class Sender<T>
{
     private String from;
     private Authenticator auth;
     
     public Sender(String from , Authenticator auth) {
		this.from = from;
		this.auth = auth;
	}
     
     
	 public abstract T send();
	 
	 public void sendSuppliersNames(List<String> suppliersNamesList) 
		{
	     	Globals globals = new Globals();
			File file = new File(globals.suppliersFolderPath);
			int index = 1;
			while(file.exists())
			{
				file = new File(globals.suppliersFolderPath.split("\\.")[0]+" (" + index + ")."+globals.suppliersFolderPath.split("\\.")[1]);
	    		index++;
			}
		    /*WorkbookSettings wbSettings = new WorkbookSettings();

		    wbSettings.setLocale(new Locale("en", "EN"));*/

			XSSFWorkbook w = null;
		    w = new XSSFWorkbook();
		    
		    w.createSheet("Report");
		    XSSFSheet excelSheet = w.getSheetAt(0);
		        
		    Row newRow = excelSheet.createRow(0);
		    
		    Cell cell = newRow.createCell(0);
		    
		    cell.setCellValue("שם ספק");
	    	
	    	XSSFCellStyle style = w.createCellStyle();
	    	style.setFillForegroundColor(IndexedColors.TURQUOISE.index);
	    	style.setFillPattern(CellStyle.SOLID_FOREGROUND);

	    	style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	    	style.setBorderTop(HSSFCellStyle.BORDER_THIN);
	    	style.setBorderRight(HSSFCellStyle.BORDER_THIN);
	    	style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	    	cell.setCellStyle(style);
		    
			style = w.createCellStyle();
			
	    	style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	    	style.setBorderTop(HSSFCellStyle.BORDER_THIN);
	    	style.setBorderRight(HSSFCellStyle.BORDER_THIN);
	    	style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	    	
		    int i = 1;
		    for (String supplierName : suppliersNamesList) 
		    {
				newRow = excelSheet.createRow(i);
				cell = newRow.createCell(0);
				
				cell.setCellValue(supplierName);
				cell.setCellStyle(style);
				
				i++;
			}
		    
		    excelSheet.autoSizeColumn(0); 
		    
		    try {
		    	FileOutputStream fos = new FileOutputStream(file);
				w.write(fos);
				fos.close();
				w.close();
				
				List <String> to = new ArrayList<String>();
				to.add(from);
				SendEmail senderMail = new SendEmail(from, to, auth);
			    boolean success;
				try {
					success = senderMail.send("suppliers that not recieve report - without email", "", file , globals.suppliersFolderPath);
				} catch (noValidEmailException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					success = false;
					
				}
			     
			    file.delete();
			    
			    if(!success)
			    {
			    	 JOptionPane.showConfirmDialog(null, "Wrong User/Password OR there is no internet connection","",JOptionPane.PLAIN_MESSAGE);
			    }
			     
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
}
