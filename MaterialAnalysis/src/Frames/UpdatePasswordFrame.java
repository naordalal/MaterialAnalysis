package Frames;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.KeyStroke;

import MainPackage.CallBack;
import MainPackage.DataBase;
import MainPackage.Globals;

public class UpdatePasswordFrame implements ActionListener
{

	private CallBack callBack;
	private JFrame frame;
	private JLabel newPasswordLabel;
	private JPasswordField newPasswordText;
	private JLabel confirmPasswordLabel;
	private JPasswordField confirmPasswordText;
	private JButton confirmButton;
	
	
	private JPanel panel;
	private String userName;
	private Globals globals;
	private DataBase db;
	
	public UpdatePasswordFrame(String userName , Globals globals , CallBack callback) 
	{
		this.callBack = callback;
		this.userName = userName;
		this.globals = globals;
		db = new DataBase();
		initialize();
	}
	
	private void initialize() 
	{
		globals = new Globals();

		frame = new JFrame("Update Password");
		frame.getContentPane().setLayout(null);
		frame.getContentPane().setFocusable(true);
		frame.setBounds(500, 200, 300, 200);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		frame.setIconImage(globals.frameImage);
		
		frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
	            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Cancel"); //$NON-NLS-1$
		frame.getRootPane().getActionMap().put("Cancel", new AbstractAction(){ //$NON-NLS-1$
	            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e)
	            {
	                frame.dispose();
	            }
	        });
		
		
		panel = new JPanel();
		panel.setLocation(0 , 0);
		panel.setSize(300 , 200);
		panel.setLayout(null);
		frame.add(panel);
		
		newPasswordLabel = new JLabel("New Password:");
		newPasswordLabel.setSize(80 , 20);
		newPasswordLabel.setLocation(20 , 20);
		panel.add(newPasswordLabel);
		
		newPasswordText = new JPasswordField();
		newPasswordText.setSize(150, 20);
		newPasswordText.setLocation(120, 20);
		panel.add(newPasswordText);
		
		
		confirmPasswordLabel = new JLabel("Confirm Password:");
		confirmPasswordLabel.setSize(100 , 20);
		confirmPasswordLabel.setLocation(20 , 60);
		panel.add(confirmPasswordLabel);
		
		confirmPasswordText = new JPasswordField();
		confirmPasswordText.setSize(150, 20);
		confirmPasswordText.setLocation(120, 60);
		panel.add(confirmPasswordText);
		
		confirmButton = new JButton();
		confirmButton.setLocation(125, 100);
		confirmButton.setSize(50 , 50);
		confirmButton.addActionListener(this);
		confirmButton.setIcon(globals.okIcon);  
		confirmButton.setFocusable(false);
		confirmButton.setContentAreaFilled(false);
		confirmButton.setPressedIcon(globals.clickOkIcon);
		confirmButton.setToolTipText("confirm password");
		panel.add(confirmButton);
		
		frame.setVisible(true);
		
		newPasswordText.requestFocusInWindow();
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == confirmButton)
		{
			if(new String(newPasswordText.getPassword()).trim().equals("") ||new String(confirmPasswordText.getPassword()).trim().equals(""))
			{
				JOptionPane.showConfirmDialog(null, "You have to fill all fields","",JOptionPane.PLAIN_MESSAGE);
				return;
			}
			
			if(!new String(newPasswordText.getPassword()).equals(new String(confirmPasswordText.getPassword())))
			{
				JOptionPane.showConfirmDialog(null, "Passwords don't match","",JOptionPane.PLAIN_MESSAGE);
				return;
			}
			
			if(db.updatePassword(userName , new String(newPasswordText.getPassword())))
				JOptionPane.showConfirmDialog(null, "Update success","",JOptionPane.PLAIN_MESSAGE);
			else
				JOptionPane.showConfirmDialog(null, "Update fail","",JOptionPane.PLAIN_MESSAGE);
			
			callBack.execute();
			frame.dispose();
			
		}
		
	}

}
