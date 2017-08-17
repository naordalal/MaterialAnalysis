package Frames;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import mainPackage.CallBack;
import mainPackage.Globals;
import mainPackage.Globals.FormType;

public class SelectionWindowFrame implements ActionListener 
{
	private JFrame frame;
	private JPanel panel;
	private JButton followUpAndMrpButton;
	private JButton mapButton;
	private Globals globals;
	
	public SelectionWindowFrame() 
	{
		globals = new Globals();
		initialize();
	}
	private void initialize() 
	{
		frame = new JFrame("ND System");
		frame.setVisible(true);
		frame.getContentPane().setLayout(null);
		frame.getContentPane().setFocusable(true);
		frame.setBounds(500, 200, 300, 150);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) 
            {
            	frame.dispose();
                System.exit(0);
            }
        });
		frame.setResizable(false);
		frame.setIconImage(globals.frameImage);
		
		frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Cancel");
		frame.getRootPane().getActionMap().put("Cancel", new AbstractAction(){
			private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e)
	            {
	                frame.dispose();
	                System.exit(0);
	            }
	        });
		
		panel = new JPanel();
		panel.setLocation(0 , 0);
		panel.setSize(300 , 150);
		panel.setLayout(null);
		frame.add(panel);
		
		followUpAndMrpButton = new JButton("<html><b>Mrp<br>Expedite<br>Follow Up</b></html>");
		followUpAndMrpButton.setLocation(30 , 30);
		followUpAndMrpButton.setSize(100, 60);
		followUpAndMrpButton.addActionListener(this);
		panel.add(followUpAndMrpButton);
		
		mapButton = new JButton("<html><b>MAP</b></html>");
		mapButton.setLocation(160 , 30);
		mapButton.setSize(100, 60);
		mapButton.addActionListener(this);
		panel.add(mapButton);
		
	}
	@Override
	public void actionPerformed(ActionEvent event) 
	{
		if(event.getSource() == followUpAndMrpButton)
		{
			new Menu(new CallBack<Integer>() {
				
				@Override
				public Integer execute(Object... objects) {
					frame.setVisible(true);
					return null;
				}
			});
			frame.setVisible(false);
		}
		else if(event.getSource() == mapButton)
		{
			new MainMapFrame(new CallBack<Integer>() {
				
				@Override
				public Integer execute(Object... objects) {
					frame.setVisible(true);
					return null;
				}
			});
			frame.setVisible(false);
		}
		
	}
	
	
	public static void main(String[] args) 
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");					
					new SelectionWindowFrame();
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		});
		
	}
	
}
