package MainPackage;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
			int answer = JOptionPane.showConfirmDialog(null, "Please Load " + fileName + " File","",JOptionPane.PLAIN_MESSAGE);
			if(answer != JOptionPane.OK_OPTION)
				System.exit(0);
			
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
			JFrame frame = new JFrame("ND System");
			frame.setLayout(null);
			frame.getRootPane().setFocusable(true);
			frame.setBounds(500, 200, 150, 150);
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			frame.setResizable(false);
			
			JLabel label = new JLabel("<html><b>Working...</b></html>");
			label.setLocation(40, 20);
			label.setSize(90 , 100);
			frame.add(label);
			
			frame.setVisible(true);
			
			analyzer.analyze();
			frame.dispose();
			Main.backupDB();
			JOptionPane.showConfirmDialog(null, "Done!","",JOptionPane.PLAIN_MESSAGE);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			JOptionPane.showConfirmDialog(null, "Error!","",JOptionPane.PLAIN_MESSAGE);
			System.exit(0);
		}
		
	}
}
