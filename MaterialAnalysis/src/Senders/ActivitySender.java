package Senders;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
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

import MainPackage.Activity;
import MainPackage.Globals;
import MainPackage.noValidEmailException;

public class ActivitySender extends Sender<List<String>>
{
	
	private String email;
	private List<Activity> uses;
	private Authenticator auth;

	public ActivitySender(String email , List<Activity> uses , Authenticator auth) 
	{
		super(email , auth);
		this.email = email;
		this.uses = uses;
		this.auth = auth;
	}

	@Override
	public List<String> send() {
		File file = new File(Globals.usesPath);
		int index = 1;
		while(file.exists())
		{
			file = new File(Globals.usesPath.split("\\.")[0]+" (" + index + ")."+Globals.usesPath.split("\\.")[1]);
    		index++;
		}
	    /*WorkbookSettings wbSettings = new WorkbookSettings();

	    wbSettings.setLocale(new Locale("en", "EN"));*/
		XSSFWorkbook w = null;
	    w = new XSSFWorkbook();
	    
	    w.createSheet("Report");
	    XSSFSheet excelSheet = w.getSheetAt(0);
	        
	    Row newRow = excelSheet.createRow(0);
	    
    	XSSFCellStyle style = w.createCellStyle();
    	style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
    	style.setFillPattern(CellStyle.SOLID_FOREGROUND);

    	style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
    	style.setBorderTop(HSSFCellStyle.BORDER_THIN);
    	style.setBorderRight(HSSFCellStyle.BORDER_THIN);
    	style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
    	
	    Cell cell = newRow.createCell(0);
	    cell.setCellValue("Name");
    	cell.setCellStyle(style);
    	
    	style = w.createCellStyle();
    	style.setFillForegroundColor(IndexedColors.TURQUOISE.index);
    	style.setFillPattern(CellStyle.SOLID_FOREGROUND);

    	style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
    	style.setBorderTop(HSSFCellStyle.BORDER_THIN);
    	style.setBorderRight(HSSFCellStyle.BORDER_THIN);
    	style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
    	
    	cell = newRow.createCell(1);
	    cell.setCellValue("Follow Up");
	    cell.setCellStyle(style);	
	    
    	cell = newRow.createCell(2);
	    cell.setCellValue("Accept order");
	    cell.setCellStyle(style);	    
	    
	    cell = newRow.createCell(3);
	    cell.setCellValue("Without due date");
	    cell.setCellStyle(style);
	    
    	cell = newRow.createCell(4);
	    cell.setCellValue("Past due date");
	    cell.setCellStyle(style);
	    
	    cell = newRow.createCell(5);
	    cell.setCellValue("Supply on time");
	    cell.setCellStyle(style);
	    
	    cell = newRow.createCell(6);
	    cell.setCellValue("Orders Beyond Request Date");
	    cell.setCellStyle(style);
	    
	    style = w.createCellStyle();
    	style.setFillForegroundColor(IndexedColors.YELLOW.index);
    	style.setFillPattern(CellStyle.SOLID_FOREGROUND);

    	style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
    	style.setBorderTop(HSSFCellStyle.BORDER_THIN);
    	style.setBorderRight(HSSFCellStyle.BORDER_THIN);
    	style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
    	
	    cell = newRow.createCell(7);
	    cell.setCellValue("Expedite report");
	    cell.setCellStyle(style);
	    
	    cell = newRow.createCell(8);
	    cell.setCellValue("Import expedite report");
	    cell.setCellStyle(style);
	    
	    cell = newRow.createCell(9);
	    cell.setCellValue("Export expedite report");
	    cell.setCellStyle(style);
	    
	    style = w.createCellStyle();
    	style.setFillForegroundColor(IndexedColors.TURQUOISE.index);
    	style.setFillPattern(CellStyle.SOLID_FOREGROUND);

    	style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
    	style.setBorderTop(HSSFCellStyle.BORDER_THIN);
    	style.setBorderRight(HSSFCellStyle.BORDER_THIN);
    	style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
    	
    	cell = newRow.createCell(10);
	    cell.setCellValue("Project");
	    cell.setCellStyle(style);
	    
	    cell = newRow.createCell(11);
	    cell.setCellValue("Date");
	    cell.setCellStyle(style);
	    
	    style = w.createCellStyle();
	    
    	style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
    	style.setBorderTop(HSSFCellStyle.BORDER_THIN);
    	style.setBorderRight(HSSFCellStyle.BORDER_THIN);
    	style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	    
	    int rowIndex = 1;
	    
		for (Activity use : uses) 
		{
			Row row = excelSheet.createRow(rowIndex);
			
			String name = use.getName();
			cell = row.createCell(0);
			cell.setCellValue(name);
			cell.setCellStyle(style);
			
			boolean followUp = use.isFollowUp();
			cell = row.createCell(1);
			if(followUp)
				cell.setCellValue(1);
			else
				cell.setCellValue("");
			cell.setCellStyle(style);
			
			boolean acceptOrder = use.isAcceptOrder();
			cell = row.createCell(2);
			if(acceptOrder)
				cell.setCellValue(1);
			else
				cell.setCellValue("");
			cell.setCellStyle(style);
			
			boolean withoutDueDate = use.isWithoutDueDate();
			cell = row.createCell(3);
			if(withoutDueDate)
				cell.setCellValue(1);
			else
				cell.setCellValue("");
			cell.setCellStyle(style);
			
			boolean pastDueDate = use.isPastDueDate();
			cell = row.createCell(4);
			if(pastDueDate)
				cell.setCellValue(1);
			else
				cell.setCellValue("");
			cell.setCellStyle(style);
			
			boolean supplyOnTime = use.isSupplyOnTime();
			cell = row.createCell(5);
			if(supplyOnTime)
				cell.setCellValue(1);
			else
				cell.setCellValue("");
			cell.setCellStyle(style);
			
			boolean beyondRequestDate = use.isBeyondRequestDate();
			cell = row.createCell(6);
			if(beyondRequestDate)
				cell.setCellValue(1);
			else
				cell.setCellValue("");
			cell.setCellStyle(style);
			
			boolean expediteReport = use.isExpediteReport();
			cell = row.createCell(7);
			if(expediteReport)
				cell.setCellValue(1);
			else
				cell.setCellValue("");
			cell.setCellStyle(style);
			
			boolean importExpediteReport = use.isImportExpediteReport();
			cell = row.createCell(8);
			if(importExpediteReport)
				cell.setCellValue(1);
			else
				cell.setCellValue("");
			cell.setCellStyle(style);
			
			boolean exportExpediteReport = use.isExportExpediteReport();
			cell = row.createCell(9);
			if(exportExpediteReport)
				cell.setCellValue(1);
			else
				cell.setCellValue("");
			cell.setCellStyle(style);
			
			String project = use.getProject();
			cell = row.createCell(10);
			cell.setCellValue(project);
			cell.setCellStyle(style);
			
			LocalDateTime date = use.getDate();
			cell = row.createCell(11);
			cell.setCellValue(Globals.dateToString(date));
			cell.setCellStyle(style);
			
			rowIndex++;
		}
		
		for(int i = 0 ; i <= 11 ; i++)
	    	excelSheet.autoSizeColumn(i); 
		
	 	List<String> notValidEmails = new ArrayList<String>();
		 try {
			 	FileOutputStream fos = new FileOutputStream(file);
				w.write(fos);
				fos.close();
				w.close();
				
				List <String> to = new ArrayList<String>();
				to.add(email);
				SendEmail senderMail = new SendEmail(email, to, auth);
			    boolean success;
				try {
					success = senderMail.send("Activity report", "", file , Globals.usesPath);
				} catch (noValidEmailException e) {
					e.printStackTrace();
					success = false;
				}
			     
			    file.delete();
			    
			    if(!success)
			    {
			    	 JOptionPane.showConfirmDialog(null, "Wrong User/Password OR there is no internet connection","",JOptionPane.PLAIN_MESSAGE);
			    	 return null;
			    }
			     
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		 
		 return notValidEmails;
	}
}

