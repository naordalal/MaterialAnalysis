package MainPackage;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class LoadingFrame 
{

	private JFileChooser fileChooser;
	private String directoryPath;

	public LoadingFrame() 
	{
		initialize();
	}
	
	private void initialize() 
	{		
		String [] filesPath = new String[3];
		String [] filesNames = {"Work Order" , "Customer Orders" , "Shipments"};
		
		for (int i = 0 ; i < filesNames.length ; i++) 
		{
			String fileName = filesNames[i];
			JOptionPane.showConfirmDialog(null, "Please Load " + fileName + " File","",JOptionPane.PLAIN_MESSAGE);
			fileChooser = new JFileChooser();
			if(directoryPath != null)
				fileChooser.setCurrentDirectory(new File(directoryPath));
			int returnVal = fileChooser.showOpenDialog(null);
		    if (returnVal == JFileChooser.APPROVE_OPTION) 
		    {
		        File file = fileChooser.getSelectedFile();
				directoryPath = file.getPath();
				filesPath[i] = file.getAbsolutePath();
				
		    }
		    else
		    	System.exit(0);
			
		}
		
		Analyzer analyzer = new Analyzer(filesPath[0] , filesPath[1] , filesPath[2]);
		try 
		{
			analyzer.analyze();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			JOptionPane.showConfirmDialog(null, "Error!","",JOptionPane.PLAIN_MESSAGE);
			System.exit(0);
		}
		
	}
}
