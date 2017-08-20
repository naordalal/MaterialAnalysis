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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import mainPackage.DataBase;
import mainPackage.Globals;

public class LoginFrame implements ActionListener 
{
	private DataBase db;
	private Globals globals;
	private JFrame frame;
	private JPanel panel;
	private JLabel nickNameLabel;
	private JLabel passwordLabel;
	private JTextField nickNametext;
	private JPasswordField passwordField;
	private JButton loginButton;
	private JButton updatePasswordButton;
	private JLabel copyRight;
	
	
	public LoginFrame() 
	{
		db = new DataBase();
		initialize();
	}
	
	private void initialize() 
	{
		globals = new Globals();
		frame = new JFrame("ND System");
		frame.setVisible(true);
		frame.setLayout(null);
		frame.getRootPane().setFocusable(true);
		frame.setBounds(500, 200, 300, 300);
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
		panel.setSize(300, 300);
		panel.setLayout(null);
		frame.add(panel);
		
		nickNameLabel = new JLabel("<html><u>User Name:</u></html>");
		nickNameLabel.setLocation(30,10);
		nickNameLabel.setSize(100,100);
		panel.add(nickNameLabel);
		
		passwordLabel = new JLabel("<html><u>Password:</u></html>");
		passwordLabel.setLocation(30, 80);
		passwordLabel.setSize(90, 100);
		panel.add(passwordLabel);
		
		nickNametext = new JTextField();
		nickNametext.setLocation(120, 50);
		nickNametext.setSize(150, 20);
		panel.add(nickNametext);
		nickNametext.requestFocusInWindow();
		
		passwordField = new JPasswordField();
		passwordField.setLocation(120, 120);
		passwordField.setSize(150, 20);
		panel.add(passwordField);
		
		loginButton = new JButton();
		loginButton.setLocation(115 , 200);
		loginButton.setSize(60,40);
		loginButton.addActionListener(this);
		loginButton.setIcon(globals.nextIcon);
		loginButton.setFocusable(false);
		loginButton.setContentAreaFilled(false);
		loginButton.setPressedIcon(globals.clickNextIcon);
		loginButton.setToolTipText("Login");
		panel.add(loginButton);
		
		updatePasswordButton = new JButton();
		updatePasswordButton.setLocation(30 , 160);
		updatePasswordButton.setSize(60,40);
		updatePasswordButton.addActionListener(this);
		updatePasswordButton.setIcon(globals.updatePasswordIcon);
		updatePasswordButton.setFocusable(false);
		updatePasswordButton.setContentAreaFilled(false);
		updatePasswordButton.setPressedIcon(globals.clickUpdatePasswordIcon);
		updatePasswordButton.setToolTipText("Upadte Password");
		panel.add(updatePasswordButton);
		
		copyRight = new JLabel("<html><b>\u00a9 Naor Dalal</b></html>");
		copyRight.setLocation(30 , 230);
		copyRight.setSize(100,30);
		panel.add(copyRight);
			
	}

	@Override
	public void actionPerformed(ActionEvent event) 
	{
		if(event.getSource() == loginButton)
		{
			if(!db.checkConnectPermission(nickNametext.getText(), new String(passwordField.getPassword())))
			{
				JOptionPane.showConfirmDialog(null, "you don't have a permission","",JOptionPane.PLAIN_MESSAGE);
				return;
			}
			
			frame.dispose();
			new SelectionWindowFrame(nickNametext.getText() , new String(passwordField.getPassword()));
		}
		else if(event.getSource() == updatePasswordButton)
		{
			if(!db.checkConnectPermission(nickNametext.getText(), new String(passwordField.getPassword())))
			{
				JOptionPane.showConfirmDialog(null, "you don't have a permission","",JOptionPane.PLAIN_MESSAGE);
				return;
			}
			
			 new UpdatePasswordFrame(nickNametext.getText() , globals , (objects)->{
				passwordField.setText("");
				return 1;
			});
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
					new LoginFrame();
					//new AddForecastFrame();
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		});
		
	}
}
