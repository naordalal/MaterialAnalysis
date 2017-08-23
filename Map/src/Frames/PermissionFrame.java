package Frames;
import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.apache.poi.ss.usermodel.Font;

import Components.FilterCombo;
import Components.MultiSelectionComboBox;
import Components.MyComboBoxRenderer;
import MainPackage.DataBase;
import MainPackage.Globals;
import MainPackage.User;


public class PermissionFrame implements ActionListener{
	
	private JFrame frame;
	private JButton addButton;
	private JButton deleteButton;
	private JLabel emailLabel;
	private JTextField emailText;
	private JLabel passwordLabel;
	private JPasswordField passwordField;
	private JButton okButton;
	private JPanel panel;
	private JRadioButton permission;
	private JRadioButton purchasing;
	private JLabel signatureLabel;
	private JTextArea signatureText;
	private DataBase db;
	private Globals globals;
	private JLabel copyRight;
	private String from;
	private MethodHandle callbackMethodOfMenu;
	private JLabel nickNameLabel;
	private FilterCombo nickNameComboBox;
	private JLabel projectsPermissionLabel;
	private MultiSelectionComboBox<String> projectsPermissionComboBox;
	
	public PermissionFrame(DataBase db , String from, MethodHandle callbackMethodOfMenu)
	{
		this.db = db;
		this.from = from;
		this.callbackMethodOfMenu = callbackMethodOfMenu;
		initialize();
	}


	private void initialize() 
	{	
		globals = new Globals();
		
		
		frame = new JFrame("Gathering material analysis system");
		frame.setVisible(true);
		frame.getContentPane().setLayout(null);
		frame.getContentPane().setFocusable(true);
		frame.setBounds(500, 200, 550, 500);
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
		panel.setSize(550 , 500);
		panel.setLayout(null);
		frame.add(panel);
		
		
		addButton = new JButton();
		addButton.setLocation(125 , 10);
		addButton.setSize(100, 40);
		addButton.addActionListener(this);
		addButton.setIcon(globals.addIcon);
		addButton.setFocusable(false);
		addButton.setContentAreaFilled(false);
		addButton.setPressedIcon(globals.clickAddIcon);
		addButton.setToolTipText("add employee's permission");
		panel.add(addButton);
		
		deleteButton = new JButton();
		deleteButton.setLocation(325 , 10);
		deleteButton.setSize(100, 40);
		deleteButton.addActionListener(this);
		deleteButton.setIcon(globals.deleteIcon);
		deleteButton.setFocusable(false);
		deleteButton.setContentAreaFilled(false);
		deleteButton.setPressedIcon(globals.clickDeleteIcon);
		deleteButton.setToolTipText("delete employee's permission");
		panel.add(deleteButton);
		
		nickNameLabel = new JLabel("User:");
		nickNameLabel.setSize(40,100);
		nickNameLabel.setVisible(false);
		panel.add(nickNameLabel);
		
		Object[][] users = db.getAllUsers();
		List<String> nickNames = new ArrayList<>();
		for (Object[] user : users) 
		{
			nickNames.add((String) user[0]);
		}
		DefaultComboBoxModel<String> nickNameComboBoxModel = new DefaultComboBoxModel<String>();
		boolean clearWhenFocusLost = false;
		nickNameComboBox = new FilterCombo(nickNames , nickNameComboBoxModel , clearWhenFocusLost);
		nickNameComboBox.setSize(150, 20);
		nickNameComboBox.setVisible(false);
		nickNameComboBox.addActionListener(this);
		panel.add(nickNameComboBox);
		
		emailLabel = new JLabel("Email:");
		emailLabel.setSize(40,100);
		emailLabel.setVisible(false);
		panel.add(emailLabel);
		
		emailText = new JTextField();
		emailText.setSize(150, 20);
		emailText.setVisible(false);
		panel.add(emailText);
		
		passwordLabel = new JLabel("Password:");
		passwordLabel.setSize(60,100);
		passwordLabel.setVisible(false);
		panel.add(passwordLabel);
		
		passwordField = new JPasswordField();
		passwordField.setSize(150, 20);
		passwordField.setVisible(false);
		panel.add(passwordField);
		
		okButton = new JButton();
		okButton.setSize(80 , 40);
		okButton.addActionListener(this);
		okButton.setVisible(false);
		okButton.setIcon(globals.okIcon);
		okButton.setFocusable(false);
		okButton.setContentAreaFilled(false);
		okButton.setPressedIcon(globals.clickOkIcon);
		okButton.setToolTipText("OK");
		panel.add(okButton);
		
		permission = new JRadioButton("Admin permission");
		permission.setSize(110,20);
		permission.setVisible(false);
		permission.addActionListener(this);
		panel.add(permission);
		
		purchasing = new JRadioButton("Purchasing permission");
		purchasing.setSize(150,20);
		purchasing.setVisible(false);
		purchasing.addActionListener(this);
		panel.add(purchasing);
		
		projectsPermissionLabel = new JLabel("Projects:");
		projectsPermissionLabel.setSize(100 , 20);
		projectsPermissionLabel.setVisible(false);
		panel.add(projectsPermissionLabel);
		
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
		List<String> projects = db.getAllProjects();
		for (String project : projects)
			model.addElement(project);
		
		model.setSelectedItem(null);
		projectsPermissionComboBox = new MultiSelectionComboBox<>(model);
		projectsPermissionComboBox.setSize(150, 20);
		projectsPermissionComboBox.setVisible(false);
		panel.add(projectsPermissionComboBox);
		
		signatureLabel = new JLabel("Signature:");
		signatureLabel.setSize(100 , 20);
		signatureLabel.setVisible(false);
		panel.add(signatureLabel);
		
		signatureText = new JTextArea();
		signatureText.setSize(350, 200);
		signatureText.setVisible(false);
		signatureText.setFont(new java.awt.Font("Ariel", Font.ANSI_CHARSET , 12));
		signatureText.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		panel.add(signatureText);
		
		copyRight = new JLabel("<html><b>\u00a9 Naor Dalal</b></html>");
		copyRight.setLocation(30 , 440);
		copyRight.setSize(100,30);
		panel.add(copyRight);
		
		
	}


	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource() == addButton)
		{
			emailLabel.setLocation(30, 45);
			emailText.setLocation(90 , 85);
			emailText.setText("");
			
			nickNameLabel.setLocation(30 , 20);
			nickNameComboBox.setLocation(90 , 60);
			nickNameComboBox.setText("");
			
			passwordLabel.setLocation(30 , 70);
			passwordField.setLocation(90 , 110);
			passwordField.setText("");
			
			permission.setLocation(25, 150);
			purchasing.setLocation(150,150);
			
			signatureLabel.setLocation(30, 220);
			signatureText.setLocation(100, 225);
			
			projectsPermissionLabel.setLocation(30, 180);
			projectsPermissionComboBox.setLocation(90, 180);
			
			emailLabel.setVisible(true);
			emailText.setVisible(true);
			
			nickNameLabel.setVisible(true);
			nickNameComboBox.setVisible(true);
			
			passwordLabel.setVisible(true);
			passwordField.setVisible(true);
			
			permission.setSelected(false);
			permission.setVisible(true);
			
			purchasing.setSelected(false);
			purchasing.setVisible(true);
			
			projectsPermissionLabel.setVisible(true);
			projectsPermissionComboBox.setVisible(true);
			
			signatureText.setText("");
			
			nickNameComboBox.requestFocusInWindow();
			
			if(!okButton.isVisible())
			{
				okButton.setLocation(225, 425);
				okButton.setVisible(true);				
			}
			
			
		}
		else if(e.getSource() == deleteButton)
		{
			nickNameLabel.setLocation(280, 20);
			nickNameComboBox.setLocation(320 , 60);
			nickNameComboBox.setText("");

			passwordLabel.setVisible(false);
			passwordField.setVisible(false);
			
			nickNameComboBox.setVisible(true);
			nickNameLabel.setVisible(true);
			
			emailLabel.setVisible(false);
			emailText.setVisible(false);
			
			permission.setVisible(false);
			purchasing.setVisible(false);
			
			signatureLabel.setVisible(false);
			signatureText.setVisible(false);
			
			projectsPermissionLabel.setVisible(false);
			projectsPermissionComboBox.setVisible(false);
			projectsPermissionComboBox.removeAllSelectedItem();
			
			nickNameComboBox.requestFocusInWindow();
			
			if(!okButton.isVisible())
			{
				okButton.setLocation(225, 425);
				okButton.setVisible(true);				
			}
		}
		else if(e.getSource() == okButton)
		{
			if(passwordField.isVisible())
			{
				if(nickNameComboBox.getText().equals("") || emailText.getText().equals(""))
				{
					JOptionPane.showConfirmDialog(null, "please fill nick Name & email","",JOptionPane.PLAIN_MESSAGE);
				}
				else if(passwordField.isEnabled() && passwordField.getPassword().length == 0)
				{
					JOptionPane.showConfirmDialog(null, "please fill password","",JOptionPane.PLAIN_MESSAGE);
				}
				else
				{
					boolean purchasingPermission = (permission.isSelected()) ? true : (purchasing.isSelected());
					
					if(passwordField.isEnabled())
					{
						if(db.addUser(nickNameComboBox.getText() , emailText.getText(), new String(passwordField.getPassword()), permission.isSelected() 
								,purchasingPermission,signatureText.getText()))
						{
							db.addCustomersToUser(nickNameComboBox.getText(), projectsPermissionComboBox.getSelectedItems());
							
							JOptionPane.showConfirmDialog(null, "success","",JOptionPane.PLAIN_MESSAGE);
							try {
								callbackMethodOfMenu.invokeExact();
							} catch (Throwable e1) {
								e1.printStackTrace();
							}
						}
						else
						{
							JOptionPane.showConfirmDialog(null, "Fail , There is another user with a same userName or Email","",JOptionPane.PLAIN_MESSAGE);
						}	
					}
					else
					{
						if(db.updateUser(nickNameComboBox.getText() , emailText.getText() , permission.isSelected() 
							,purchasingPermission,signatureText.getText() , projectsPermissionComboBox.getSelectedItems()))
						{
							
							JOptionPane.showConfirmDialog(null, "success","",JOptionPane.PLAIN_MESSAGE);
							try {
								callbackMethodOfMenu.invokeExact();
							} catch (Throwable e1) {
								e1.printStackTrace();
							}
						}
						else
						{
							JOptionPane.showConfirmDialog(null, "Fail , There is another user with a same userName or Email","",JOptionPane.PLAIN_MESSAGE);
						}	
					}
					
					emailText.setText("");
					passwordField.setText("");
					nickNameComboBox.setText("");
					Object[][] users = db.getAllUsers();
					List<String> nickNames = new ArrayList<>();
					for (Object[] user : users) 
					{
						nickNames.add((String) user[0]);
					}
					nickNameComboBox.setBaseValues(nickNames);
					
					permission.setSelected(false);
					purchasing.setSelected(false);
					signatureText.setText("");
					signatureLabel.setVisible(false);
					signatureText.setVisible(false);
					projectsPermissionComboBox.removeAllSelectedItem();
					nickNameComboBox.requestFocusInWindow();
				}
			}
			else
			{
				if(nickNameComboBox.getText().equals(""))
				{
					JOptionPane.showConfirmDialog(null, "please fill userName","",JOptionPane.PLAIN_MESSAGE);
				}
				else
				{
					if(nickNameComboBox.getText().equals(from))
					{
						JOptionPane.showConfirmDialog(null, "Can not remove yourself","",JOptionPane.PLAIN_MESSAGE);
						return;
					}
					
					if(db.deleteUser(nickNameComboBox.getText()))
					{
						JOptionPane.showConfirmDialog(null, "success","",JOptionPane.PLAIN_MESSAGE);
						try {
							callbackMethodOfMenu.invokeExact();
						} catch (Throwable e1) {
							e1.printStackTrace();
						}
					}
					else
					{
						JOptionPane.showConfirmDialog(null, "Fail , There is no user with this userName","",JOptionPane.PLAIN_MESSAGE);
					}
					
					emailText.setText("");
					passwordField.setText("");
					nickNameComboBox.setText("");
					Object[][] users = db.getAllUsers();
					List<String> nickNames = new ArrayList<>();
					for (Object[] user : users) 
					{
						nickNames.add((String) user[0]);
					}
					nickNameComboBox.setBaseValues(nickNames);
					permission.setSelected(false);
					purchasing.setSelected(false);
					signatureText.setText("");
					signatureLabel.setVisible(false);
					signatureText.setVisible(false);
					projectsPermissionLabel.setVisible(false);
					projectsPermissionComboBox.setVisible(false);
					projectsPermissionComboBox.removeAllSelectedItem();
					nickNameComboBox.requestFocusInWindow();
					
				}
			}
			
		}
		else if(e.getSource() == permission)
		{
			signatureLabel.setVisible(permission.isSelected());
			signatureText.setVisible(permission.isSelected());
			
			if(permission.isSelected())
			{
				if(purchasing.isSelected())
				{
					JOptionPane.showConfirmDialog(null, "Can not choose both options","",JOptionPane.PLAIN_MESSAGE);
					permission.setSelected(false);
					return;
				}
			}
		}
		else if(e.getSource() == purchasing)
		{
			signatureLabel.setVisible(purchasing.isSelected());
			signatureText.setVisible(purchasing.isSelected());
			
			if(purchasing.isSelected())
			{
				if(permission.isSelected())
				{
					JOptionPane.showConfirmDialog(null, "Can not choose both options","",JOptionPane.PLAIN_MESSAGE);
					purchasing.setSelected(false);
					return;
				}
			}
		}
		else if(e.getSource() == nickNameComboBox)
		{
			if(!passwordField.isVisible())
				return;
			DefaultComboBoxModel<String> projectsPermissionComboBoxModel = (DefaultComboBoxModel<String>) projectsPermissionComboBox.getModel();
			DefaultComboBoxModel<String> nickNameComboBoxModel = (DefaultComboBoxModel<String>) nickNameComboBox.getModel();
			String nickName =  (nickNameComboBoxModel.getSelectedItem() == null) ? "" : (String) nickNameComboBoxModel.getSelectedItem();
			User user = db.getUser(nickName);
			
			if(user != null)
			{
				emailText.setText(user.getEmail());
				passwordField.setText("");
				permission.setSelected(user.isAdminPermission());
				purchasing.setSelected(user.isPurchasingPermission());
				projectsPermissionComboBox.removeAllSelectedItem();

				if(user.isAdminPermission() || user.isPurchasingPermission())
				{
					signatureText.setText(user.getSignature());
					signatureText.setVisible(true);
				}
				
				projectsPermissionComboBox.removeAllItems();
				List<String> allCustomers = db.getAllProjects();
				for (String customer : allCustomers) 
				{
					if(user.getCustomers().contains(customer))
						projectsPermissionComboBox.addItem(customer);
					else
						projectsPermissionComboBoxModel.addElement(customer);
				}
				
				if(user.getCustomers().size() == 0)
				{
					projectsPermissionComboBox.removeSelectedItem(allCustomers.get(0));
					projectsPermissionComboBox.setSelectedItem(null);
				}
				
				passwordField.setEnabled(false);
				
			}
			else
			{
				emailText.setText("");
				passwordField.setText("");
				permission.setSelected(false);
				purchasing.setSelected(false);
				projectsPermissionComboBox.removeAllSelectedItem();
				signatureText.setText("");
				signatureText.setVisible(false);
				
				passwordField.setEnabled(true);
			}
		}
		
	}
	
	

}
