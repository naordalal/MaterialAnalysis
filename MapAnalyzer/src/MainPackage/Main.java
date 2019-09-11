package MainPackage;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.time.LocalDateTime;
import java.util.Date;

import javax.swing.UIManager;

public class Main {

	public static void main(String[] args) 
	{
		 if(args.length == 0 || !Boolean.parseBoolean(args[0]))
		 {
			 Analyzer analyzer = new Analyzer();
			 try {
				analyzer.analyze();
				backupDB();
			} catch (IOException e) {
				e.printStackTrace();
			}
		 }
		 else
		 {
			 EventQueue.invokeLater(new Runnable() 
				{
					public void run() 
					{
						try 
						{
							UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
							new LoadingFrame();
						} 
						catch (Exception e) 
						{
							e.printStackTrace();
						}
					}
				});
		 }
		
		
	}

	public static void backupDB() throws IOException 
	{
		Date date = Globals.getTodayDate();
		String todayDate = Globals.dateWithoutHourToString(date).replaceAll("/", ".");
		
		File db = new File(Globals.con);
		File backupDB = new File(Globals.dbBackupDirectory + "DB" + "_" + todayDate + ".db");
		FileChannel src = new FileInputStream(db).getChannel();
        FileChannel dst = new FileOutputStream(backupDB).getChannel();
        dst.transferFrom(src, 0, src.size());
        src.close();
        dst.close();
		
	}

}
