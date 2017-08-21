package MainPackage;
import java.awt.ComponentOrientation;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.security.Key;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.impl.util.Base64;

import FollowUpAndExpediteFrames.FollowUpAndExpediteMenu;

public class Globals {

	public enum Sort
	{
		ASC,
		DESC
	};
	
	public enum FormType 
	{
	    SHIPMENT,WO,PO,FC
	};
	
	public static String con = "C:\\Users\\naordalal\\Desktop\\DB.db";
	//public static final String con = "O:\\Purchasing\\PO_FollowUp\\Material Analysis\\DB.db";
	public static final String ALGO = "AES";
	public static final byte[] keyValue = {'T' , 'h' , 'e' , 'B' , 'e' ,'s' ,'t' , 'S' , 'e' , 'c' ,'r' ,'e' ,'t'
		 ,'K' ,'e' ,'y'
	};
	
	public String suppliersFolderPath = "supplier list no email.xlsx";
	public String supplierOrdersPath = "Orders.xlsx";
	public String expediteOrdersPath = "expedite Orders.xlsx";
	public String ShortagesItemsPath = "Shortages Items.xlsx";
	public static String usesPath = "Activity.xlsx";
	
	private URL nextPath = FollowUpAndExpediteMenu.class.getResource("/next.png");
	private URL clickNextPath = FollowUpAndExpediteMenu.class.getResource("/clickNext.png");
	private URL updatePath = FollowUpAndExpediteMenu.class.getResource("/update.png");
	private URL clickUpdatePath = FollowUpAndExpediteMenu.class.getResource("/clickUpdate.png");
	private URL attachPath = FollowUpAndExpediteMenu.class.getResource("/attach.png");
	private URL clickAttachPath = FollowUpAndExpediteMenu.class.getResource("/clickAttach.png");
	private URL addPath = FollowUpAndExpediteMenu.class.getResource("/add.png");
	private URL clickAddPath = FollowUpAndExpediteMenu.class.getResource("/clickAdd.png");
	private URL deletePath = FollowUpAndExpediteMenu.class.getResource("/delete.png");
	private URL clickdeletePath = FollowUpAndExpediteMenu.class.getResource("/clickDelete.png");
	private URL okPath = FollowUpAndExpediteMenu.class.getResource("/ok.png");
	private URL clickOkPath = FollowUpAndExpediteMenu.class.getResource("/clickOk.png");
	private URL sendPath = FollowUpAndExpediteMenu.class.getResource("/send.png");
	private URL clickSendPath = FollowUpAndExpediteMenu.class.getResource("/clickSend.png");
	private URL frameIconPath = FollowUpAndExpediteMenu.class.getResource("/frameIcon.png");
	private URL viewIconPath = FollowUpAndExpediteMenu.class.getResource("/view.png");
	private URL clickViewIconPath = FollowUpAndExpediteMenu.class.getResource("/clickView.png");
	private URL updatePasswordIconPath = FollowUpAndExpediteMenu.class.getResource("/updatePass.png");
	private URL clickUpdatePasswordIconPath= FollowUpAndExpediteMenu.class.getResource("/clickUpdatePass.png");
	private URL directoryIconPath= FollowUpAndExpediteMenu.class.getResource("/directory.png");
	private URL clickDirectoryIconPath= FollowUpAndExpediteMenu.class.getResource("/clickDirectory.png");
	
	
	
	
	public ImageIcon nextIcon = new ImageIcon(nextPath);
	public ImageIcon clickNextIcon = new ImageIcon(clickNextPath);
	public ImageIcon updateIcon = new ImageIcon(updatePath);
	public ImageIcon clickUpdateIcon = new ImageIcon(clickUpdatePath);
	public ImageIcon attachIcon = new ImageIcon(attachPath);
	public ImageIcon clickAttachIcon = new ImageIcon(clickAttachPath);
	public ImageIcon addIcon = new ImageIcon(addPath);
	public ImageIcon clickAddIcon = new ImageIcon(clickAddPath);
	public ImageIcon deleteIcon = new ImageIcon(deletePath);
	public ImageIcon clickDeleteIcon = new ImageIcon(clickdeletePath);
	public ImageIcon okIcon = new ImageIcon(okPath);
	public ImageIcon clickOkIcon = new ImageIcon(clickOkPath);
	public ImageIcon sendIcon = new ImageIcon(sendPath);
	public ImageIcon clickSendIcon = new ImageIcon(clickSendPath);
	public ImageIcon viewIcon = new ImageIcon(viewIconPath);
	public ImageIcon clickViewIcon = new ImageIcon(clickViewIconPath);
	public ImageIcon updatePasswordIcon = new ImageIcon(updatePasswordIconPath);
	public ImageIcon clickUpdatePasswordIcon = new ImageIcon(clickUpdatePasswordIconPath);
	public ImageIcon directoryIcon = new ImageIcon(directoryIconPath);
	public ImageIcon clickDirectoryIcon = new ImageIcon(clickDirectoryIconPath);
	public Image frameImage = new ImageIcon(frameIconPath).getImage(); 
	
	public static double highPrice;
	
	public static int deafultPrice = 500;

	
	public String acceptNotes = "Please approve receipt of PO and delivery date";
	public String promisedDateInHebrewColumn = "תאריך מובטח";
	public String promisedDateInEnglishColumn = "delivery date";
	public String requestDateInHebrewColumn = "תאריך נדרש";
	public String requestDateInEnglishColumn = "required date";
	public String supplierInHebrewColumn = "ספק";
	public String supplierInEnglishColumn = "supplier";
	public String notesColumn = "הערות לספק";
	
	public String noDate = "11/11/19";
	public String frozenDate = "12/12/19";

	public void initTextComponent(JTextComponent text) 
	{
		UndoManager undoManager = new UndoManager();
		
		Document doc = text.getDocument();
		doc.addUndoableEditListener(new UndoableEditListener() {
			
			@Override
			public void undoableEditHappened(UndoableEditEvent e) {
				undoManager.addEdit(e.getEdit());
				
			}
		});
		
		InputMap im = text.getInputMap(JComponent.WHEN_FOCUSED);
		ActionMap am = text.getActionMap();
		
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z , Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) , "Undo");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y , Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) , "Redo");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_L , Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) , "Left");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_R , Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) , "Right");
		
		am.put("Undo", new AbstractAction() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(undoManager.canUndo())
					undoManager.undo();				
			}
		});
		
		am.put("Redo", new AbstractAction() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(undoManager.canRedo())
					undoManager.redo();				
			}
		});
		
		am.put("Left", new AbstractAction() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				text.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);				
			}
		});

		am.put("Right", new AbstractAction() {
		
		/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			text.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		}
	   });
		
	}


	public int getDatesId(boolean acceptOrder , boolean noDate, boolean pastDate, boolean futureDate, boolean expediteDate, boolean beyondRequestDate) 
	{
		int returnID = 0;
		int multipier;
		int base = 2;
		int pow;
		
		pow = 1;
		multipier = acceptOrder ? 1 : 0;
		returnID += multipier * Math.pow(base, pow);
		
		pow = 2;
		multipier = noDate ? 1 : 0;
		returnID += multipier * Math.pow(base, pow);
		
		pow = 3;
		multipier = pastDate ? 1 : 0;
		returnID += multipier * Math.pow(base, pow);
		
		pow = 4;
		multipier = futureDate ? 1 : 0;
		returnID += multipier * Math.pow(base, pow);
		
		pow = 5;
		multipier = expediteDate ? 1 : 0;
		returnID += multipier * Math.pow(base, pow);
		
		pow = 6;
		multipier = beyondRequestDate ? 1 : 0;
		returnID += multipier * Math.pow(base, pow);
		
		
		return returnID;
	}
	
	public static void setPrice(double price)
	{
		Globals.highPrice = price;
	}


	public static void setDeafultPrice() 
	{
		Globals.highPrice = deafultPrice;
		
	}
	
	public static Date isValidDate(String inDate)
	{
		DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yy");
		sourceFormat.setLenient(false);
		Date date;
		try
		{
			date = sourceFormat.parse(inDate.trim());
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			date = c.getTime();
			
		}catch(Exception e)
		{
			return null;
		}
		
		return date;
	}
	
	public static String encrypt(String data) throws Exception
	{
		Key key = generateKey();
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.ENCRYPT_MODE, key);
		byte [] encVal = c.doFinal(data.getBytes());
		String encryptedValue = new String(Base64.encode(encVal));
		return encryptedValue;
	}

	public static String decrypt(String encryptedData) throws Exception
	{
		Key key = generateKey();
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.DECRYPT_MODE, key);
		byte [] decordedValue = Base64.decode(encryptedData.getBytes());
		byte [] decVal = c.doFinal(decordedValue);
		String decryptedValue = new String(decVal);
		return decryptedValue;
	}

	private static Key generateKey() 
	{
		Key key = new SecretKeySpec(keyValue, ALGO);
		return key;
	}
	
	public static String dateToString(LocalDateTime date) 
	{
		int year = date.getYear();
		int month = date.getMonthValue();
		int day = date.getDayOfMonth();
		int hour = date.getHour();
		int minute = date.getMinute();
		
		String s = String.format("%02d", day) + "/" + String.format("%02d", month) + "/" + year + " " + String.format("%02d", hour) + ":" + String.format("%02d", minute);
		
		return s;
	}
	
	public static String dateWithoutHourToString(LocalDateTime date) 
	{
		int year = date.getYear();
		int month = date.getMonthValue();
		int day = date.getDayOfMonth();

		String s = String.format("%02d", day) + "/" + String.format("%02d", month) + "/" + year;
		
		return s;
	}


	public boolean isToday(Date date) 
	{
		DateFormat outsourceFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date today = new Date();
		String toExp = outsourceFormat.format(today);
		try {
			today = outsourceFormat.parse(toExp);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		Calendar c = Calendar.getInstance();
		c.setTime(today);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH);
		int year = c.get(Calendar.YEAR);
		
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		int expediteDay = c.get(Calendar.DAY_OF_MONTH);
		int expediteMonth = c.get(Calendar.MONTH);
		int expediteYear = c.get(Calendar.YEAR);
		
		return year == expediteYear && month == expediteMonth && day == expediteDay;
	}
	
	public static Date parseDate(String date)
	{
		DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yy");
		DateFormat outsourceFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date parseDate;
		Calendar c = Calendar.getInstance();
		try {
			parseDate = sourceFormat.parse(date);
			String toExp = outsourceFormat.format(parseDate);
			parseDate = outsourceFormat.parse(toExp);
			c.setTime(parseDate);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			parseDate = c.getTime();
		} catch (ParseException e) {

			e.printStackTrace();
			return null;
		}
		
		return parseDate;
	}
	
	public static String dateToSqlFormatString(Date date) 
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		String s = year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", day);
		
		return s;
	}
	
	public static String parseDateToSqlFormatString(String date) 
	{
		return dateToSqlFormatString(parseDate(date));
	}
	
	public Date addDays(Date date ,int days)
	{
		Calendar c2 = Calendar.getInstance();
		c2.setTime(date);
		c2.set(Calendar.HOUR_OF_DAY, 0);
		c2.set(Calendar.MINUTE, 0);
		c2.set(Calendar.SECOND, 0);
		c2.set(Calendar.MILLISECOND, 0);
		c2.add(Calendar.DAY_OF_MONTH, days);
		return c2.getTime();
	}
	
	public static Date addMonths(Date date ,int months)
	{
		Calendar c2 = Calendar.getInstance();
		c2.setTime(date);
		c2.set(Calendar.HOUR_OF_DAY, 0);
		c2.set(Calendar.MINUTE, 0);
		c2.set(Calendar.SECOND, 0);
		c2.set(Calendar.MILLISECOND, 0);
		c2.add(Calendar.MONTH, months);
		return c2.getTime();
	}
	
	public static Date getTodayDate()
	{
		Calendar c2 = Calendar.getInstance();
		c2.setTime(new Date());
		c2.set(Calendar.HOUR_OF_DAY, 0);
		c2.set(Calendar.MINUTE, 0);
		c2.set(Calendar.SECOND, 0);
		c2.set(Calendar.MILLISECOND, 0);
		return c2.getTime();
	}
	
	public Date convertDateToYYYY(Date date) 
	{
		DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yy");
		String parseDate;
		parseDate = sourceFormat.format(date);
		return parseDate(parseDate);
	}
	
	public static void setDateFormat(XSSFWorkbook workbook, XSSFCellStyle cellStyle)
	{
		CreationHelper createHelper = workbook.getCreationHelper();
		short dateFormat = createHelper.createDataFormat().getFormat("dd/MM/yyyy");
		cellStyle.setDataFormat(dateFormat);
	}
	
	public static int getMonth(Date date) 
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		return c.get(Calendar.MONTH) + 1;
	}

	public static int getYear(Date date) 
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		return c.get(Calendar.YEAR);
	}
	
	public static Date setFirstDayOfMonth(Date date) 
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);
		
		date = c.getTime();
		
		return date;
	}
	
	public static String dateWithoutHourToString(Date date) 
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		String s = String.format("%02d", day) + "/" + String.format("%02d", month + 1) + "/" + year;
		
		return s;
	}
	
	public static Date parseDateFromSqlFormat(String date) 
	{
		DateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat outsourceFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date parseDate;
		Calendar c = Calendar.getInstance();
		try {
			parseDate = sourceFormat.parse(date);
			String toExp = outsourceFormat.format(parseDate);
			parseDate = outsourceFormat.parse(toExp);
			c.setTime(parseDate);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			parseDate = c.getTime();
		} catch (ParseException e) {

			e.printStackTrace();
			return null;
		}
		
		return parseDate;
	}
	
	public String FormTypeToString(FormType type)
	{
		switch(type)
		{
			case PO:
				return "Customer Order";
			case WO:
				return "Work Order";
			case SHIPMENT:
				return "Shipments";
			case FC:
				return "Forecast";
			default:
				return "";
		}
	}
	
}
