package MainPackage;

import java.awt.EventQueue;
import java.io.IOException;

import javax.swing.UIManager;

public class Main {

	public static void main(String[] args) 
	{
		/*
		 Analyzer analyzer = new Analyzer();
		 try {
			analyzer.analyze();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		
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
