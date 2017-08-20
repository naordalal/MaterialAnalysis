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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import mainPackage.CallBack;
import mainPackage.DataBase;
import mainPackage.Globals;
import mainPackage.Globals.FormType;

public class SelectionWindowFrame implements ActionListener 
{
	private int adminButtomPosition;
	private int adminButtomWidth;
	private JFrame frame;
	private JPanel panel;
	private JButton followUpAndMrpButton;
	private JButton mapButton;
	private JButton adminButton;
	private Globals globals;
	private String userName;
	private String password;
	private DataBase db;
	private JLabel copyRight;
	
	public SelectionWindowFrame(String userName, String password) 
	{
		globals = new Globals();
		db = new DataBase();
		this.userName = userName;
		this.password = password;
		initialize();
	}
	
	private void initialize() 
	{
		boolean adminPermission = db.checkAddOrDeletePermission(userName , password);
		this.adminButtomPosition = (adminPermission) ? 30 : 0;
		this.adminButtomWidth = (adminPermission) ? 100 : 0;
		
		frame = new JFrame("ND System");
		frame.setVisible(true);
		frame.getContentPane().setLayout(null);
		frame.getContentPane().setFocusable(true);
		frame.setBounds(500, 200, adminButtomPosition + adminButtomWidth + 300, 150);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) 
            {
            	frame.dispose();
                new LoginFrame();
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
	                new LoginFrame();
	            }
	        });
		
		panel = new JPanel();
		panel.setLocation(0 , 0);
		panel.setSize(adminButtomPosition + adminButtomWidth + 300 , 150);
		panel.setLayout(null);
		frame.add(panel);
		
		followUpAndMrpButton = new JButton("<html><b>Mrp<br>Expedite<br>Follow Up</b></html>");
		followUpAndMrpButton.setLocation(adminButtomPosition + adminButtomWidth + 30 , 30);
		followUpAndMrpButton.setSize(100, 60);
		followUpAndMrpButton.addActionListener(this);
		panel.add(followUpAndMrpButton);
		
		mapButton = new JButton("<html><b>MAP</b></html>");
		mapButton.setLocation(adminButtomWidth + adminButtomPosition + 160 , 30);
		mapButton.setSize(100, 60);
		mapButton.addActionListener(this);
		panel.add(mapButton);
		
		adminButton = new JButton("<html><b>Admin Window</b></html>");
		adminButton.setLocation(adminButtomPosition , 30);
		adminButton.setSize(adminButtomWidth, 60);
		adminButton.setVisible(adminPermission);
		adminButton.addActionListener(this);
		panel.add(adminButton);
		
		copyRight = new JLabel("<html><b>\u00a9 Naor Dalal</b></html>");
		copyRight.setLocation(30 , 90);
		copyRight.setSize(100,30);
		panel.add(copyRight);
	}
	@Override
	public void actionPerformed(ActionEvent event) 
	{
		if(event.getSource() == followUpAndMrpButton)
		{
			new FollowUpAndExpediteMenu(userName , password , new CallBack<Integer>() {
				
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
			new MainMapFrame(userName , password , new CallBack<Integer>() {
				
				@Override
				public Integer execute(Object... objects) {
					frame.setVisible(true);
					return null;
				}
			});
			frame.setVisible(false);
		}
		else if(event.getSource() == adminButton)
		{
			new AdminFrame(userName);
		}
		
	}
	
	
}
