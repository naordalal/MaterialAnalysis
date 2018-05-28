package Frames;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang3.math.NumberUtils;

import MainPackage.DataBase;
import MainPackage.Globals;

public class AdminFrame implements ActionListener 
{
	private Globals globals;
	private JFrame frame;
	private JPanel panel;
	private JLabel viewUsersLabel;
	private JButton viewUsersButton;
	private JLabel connectingComputersLabel;
	private JButton connectingComputersButton;
	private JComboBox<String> connectingComputersComboBox;
	private int clickedTimes;
	private JTable usersTable;
	private JScrollPane scrollPane;
	private DataBase db;
	private JLabel permissionsLabel;
	private JButton permissionsButton;
	private JLabel projectsLabel;
	private JComboBox<String> projectsComboBox;
	private JButton addProjectButton;
	private JButton deleteProjectButton;
	private JLabel projectNameLabel;
	private JTextField projectNameText;
	private JButton confirmProjectNameButton;
	private JFileChooser expediteDirectoryChooser;
	private JButton expediteDirectoryButton;
	private String userName;
	private JLabel copyRight;
	private JLabel expediteDirectoryPath;
	private JTextField obligoText;
	private JLabel obligoLabel;
	private JLabel depositLabel;
	private JTextField depositText;

	public AdminFrame(String userName) 
	{
		db = new DataBase();
		this.userName = userName;
		this.clickedTimes = 0;
		initialize();
	}

	private void initialize() 
	{
		globals = new Globals();
		
		frame = new JFrame("Admin");
		frame.setLayout(null);
		frame.getRootPane().setFocusable(true);
		frame.setBounds(400, 100, 700, 500);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) 
            {
            	frame.dispose();
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
	            }
	        });
		
		panel = new JPanel();
		panel.setLocation(0 , 0);
		panel.setSize(700, 500);
		panel.setLayout(null);
		frame.add(panel);
		
		permissionsLabel = new JLabel("<html><u>Add User & Admin Permission:</u></html>");
		permissionsLabel.setLocation(30, 30);
		permissionsLabel.setSize(150, 100);
		panel.add(permissionsLabel);
		
		
		permissionsButton = new JButton();
		permissionsButton.setLocation(200 , 60);
		permissionsButton.setSize(40 , 40);
		permissionsButton.addActionListener(this);
		permissionsButton.setIcon(globals.updateIcon);
		permissionsButton.setFocusable(false);
		permissionsButton.setContentAreaFilled(false);
		permissionsButton.setPressedIcon(globals.clickUpdateIcon);
		permissionsButton.setToolTipText("update permissions for another emplyees");
		panel.add(permissionsButton);
				
		viewUsersLabel = new JLabel("<html><u>View users:</u></html>");
		viewUsersLabel.setLocation(30, 160);
		viewUsersLabel.setSize(70,20);
		panel.add(viewUsersLabel);
		
		viewUsersButton = new JButton();
		viewUsersButton.setLocation(105, 145);
		viewUsersButton.setSize(50 , 50);
		viewUsersButton.addActionListener(this);
		viewUsersButton.setIcon(globals.viewIcon);
		viewUsersButton.setFocusable(false);
		viewUsersButton.setContentAreaFilled(false);
		viewUsersButton.setPressedIcon(globals.clickViewIcon);
		viewUsersButton.setToolTipText("View users details");
		panel.add(viewUsersButton);
		
		DefaultTableModel model2 = new DefaultTableModel();
		
		usersTable = new JTable(model2);
		model2.addColumn("User");
		model2.addColumn("Admin");
		model2.addColumn("Purchasing");
		model2.addColumn("PP&C");
		addUsers();
		usersTable.getColumnModel().getColumn(0).setResizable(false);
		usersTable.getColumnModel().getColumn(0).setPreferredWidth(130);
		usersTable.getColumnModel().getColumn(1).setResizable(false);
		usersTable.getColumnModel().getColumn(1).setPreferredWidth(100);
		usersTable.getColumnModel().getColumn(2).setResizable(false);
		usersTable.getColumnModel().getColumn(2).setPreferredWidth(100);
		usersTable.getColumnModel().getColumn(3).setResizable(false);
		usersTable.getColumnModel().getColumn(3).setPreferredWidth(100);
		usersTable.setRowHeight(30);
		usersTable.setEnabled(false);
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		
		usersTable.getColumnModel().getColumn(0).setHeaderRenderer(centerRenderer);
		usersTable.getColumnModel().getColumn(1).setHeaderRenderer(centerRenderer);
		usersTable.getColumnModel().getColumn(2).setHeaderRenderer(centerRenderer);
		usersTable.getColumnModel().getColumn(3).setHeaderRenderer(centerRenderer);
				
		usersTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
		usersTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
		usersTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
		usersTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
		
		usersTable.setShowHorizontalLines(false);
		usersTable.setShowVerticalLines(false);
		usersTable.getTableHeader().setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		usersTable.getTableHeader().setReorderingAllowed(false);
		
		scrollPane = new JScrollPane(usersTable);
		scrollPane.setLocation(185, 145);
		scrollPane.setSize(350,140);
		scrollPane.setVisible(false);
		panel.add(scrollPane);
		
		
		connectingComputersLabel = new JLabel("<html><u>View connecting users:</u></html>");
		connectingComputersLabel.setLocation(30, 260);
		connectingComputersLabel.setSize(120,20);
		panel.add(connectingComputersLabel);
		
		connectingComputersButton = new JButton();
		connectingComputersButton.setLocation(140, 245);
		connectingComputersButton.setSize(50 , 50);
		connectingComputersButton.addActionListener(this);
		connectingComputersButton.setIcon(globals.viewIcon);
		connectingComputersButton.setFocusable(false);
		connectingComputersButton.setContentAreaFilled(false);
		connectingComputersButton.setPressedIcon(globals.clickViewIcon);
		connectingComputersButton.setToolTipText("View connecting users");
		panel.add(connectingComputersButton);
		
		List<String> connectingComputers = db.getConnectingComputers();
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(connectingComputers.toArray(new String[connectingComputers.size()]));
		connectingComputersComboBox = new JComboBox<>(model);
		connectingComputersComboBox.setLocation(200, 260);
		connectingComputersComboBox.setSize(70,20);
		connectingComputersComboBox.setVisible(false);
		panel.add(connectingComputersComboBox);
		
		projectsLabel = new JLabel("<html><u>Projects:</u></html>");
		projectsLabel.setLocation(30, 360);
		projectsLabel.setSize(50, 20);
		panel.add(projectsLabel);
		
		DefaultComboBoxModel<String> model4 = new DefaultComboBoxModel<String>();
		List<String> projects = db.getAllProjects();
		for (String project : projects) 
		{
			model4.addElement(project);
		}
		projectsComboBox = new JComboBox<String>(model4);
		projectsComboBox.setLocation(90, 360);
		projectsComboBox.setSize(130,20);
		projectsComboBox.addActionListener(this);
		panel.add(projectsComboBox);
		
		expediteDirectoryButton = new JButton();
		expediteDirectoryButton.setLocation(230 , 350);
		expediteDirectoryButton.setSize(55, 40);
		expediteDirectoryButton.setIcon(globals.directoryIcon);
		expediteDirectoryButton.setFocusable(false);
		expediteDirectoryButton.setContentAreaFilled(false);
		expediteDirectoryButton.setPressedIcon(globals.clickDirectoryIcon);
		expediteDirectoryButton.addActionListener(this);
		expediteDirectoryButton.setToolTipText("Choose Directory");
		panel.add(expediteDirectoryButton);
		
		expediteDirectoryPath = new JLabel("");
		expediteDirectoryPath.setLocation(50, 400);
		expediteDirectoryPath.setSize(400, 20);
		expediteDirectoryPath.setText(db.getDirectory((String) projectsComboBox.getModel().getSelectedItem()));
		panel.add(expediteDirectoryPath);
		
		addProjectButton = new JButton();
		addProjectButton.setLocation(300 , 350);
		addProjectButton.setSize(40 , 40);
		addProjectButton.addActionListener(this);
		addProjectButton.setIcon(globals.addIcon);
		addProjectButton.setFocusable(false);
		addProjectButton.setContentAreaFilled(false);
		addProjectButton.setPressedIcon(globals.clickAddIcon);
		addProjectButton.setToolTipText("add project");
		panel.add(addProjectButton);
		
		deleteProjectButton = new JButton();
		deleteProjectButton.setLocation(360 , 350);
		deleteProjectButton.setSize(40 , 40);
		deleteProjectButton.addActionListener(this);
		deleteProjectButton.setIcon(globals.deleteIcon);
		deleteProjectButton.setFocusable(false);
		deleteProjectButton.setContentAreaFilled(false);
		deleteProjectButton.setPressedIcon(globals.clickDeleteIcon);
		deleteProjectButton.setToolTipText("delete project");
		panel.add(deleteProjectButton);
		
		projectNameLabel = new JLabel("Project name:");
		projectNameLabel.setLocation(430, 320);
		projectNameLabel.setSize(70, 20);
		projectNameLabel.setVisible(false);
		panel.add(projectNameLabel);
		
		projectNameText = new JTextField();
		projectNameText.setLocation(510, 320);
		projectNameText.setSize(100 , 20);
		projectNameText.setVisible(false);
		panel.add(projectNameText);
		
		obligoLabel = new JLabel("Obligo:");
		obligoLabel.setLocation(430, 360);
		obligoLabel.setSize(35, 20);
		obligoLabel.setVisible(true);
		panel.add(obligoLabel);
		
		obligoText = new JTextField();
		obligoText.setLocation(465, 360);
		obligoText.setSize(60 , 20);
		obligoText.setVisible(true);
		obligoText.setText(db.getCustomerObligation((String) projectsComboBox.getModel().getSelectedItem()) + "");
		panel.add(obligoText);
		
		depositLabel = new JLabel("Deposit:");
		depositLabel.setLocation(530, 360);
		depositLabel.setSize(50, 20);
		depositLabel.setVisible(true);
		panel.add(depositLabel);
		
		depositText = new JTextField();
		depositText.setLocation(580, 360);
		depositText.setSize(60 , 20);
		depositText.setText(db.getCustomerDeposit((String) projectsComboBox.getModel().getSelectedItem()) + "");
		depositText.setVisible(true);
		panel.add(depositText);
		
		confirmProjectNameButton = new JButton();
		confirmProjectNameButton.setLocation(645 , 345);
		confirmProjectNameButton.setSize(40 , 40);
		confirmProjectNameButton.addActionListener(this);
		confirmProjectNameButton.setIcon(globals.okIcon);
		confirmProjectNameButton.setFocusable(false);
		confirmProjectNameButton.setContentAreaFilled(false);
		confirmProjectNameButton.setPressedIcon(globals.clickOkIcon);
		confirmProjectNameButton.setToolTipText("confirm");
		panel.add(confirmProjectNameButton); 
		
		copyRight = new JLabel("<html><b>\u00a9 Naor Dalal</b></html>");
		copyRight.setLocation(30 , 430);
		copyRight.setSize(100,30);
		panel.add(copyRight);
		
		frame.setVisible(true);
		
	}

	private void addUsers() 
	{
		DefaultTableModel model =  (DefaultTableModel) usersTable.getModel();
		
		for(int i = model.getRowCount() - 1; i >= 0  ; i--)
			model.removeRow(i);
		
		Object [][] users  = db.getAllUsers();
		
		if(users == null)
			return;
		
		for (Object[] user : users) 
		{
			model.addRow(user);
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent event) 
	{
		if(event.getSource() == viewUsersButton)
		{		
			clickedTimes++;
			if(clickedTimes % 2 != 0)
			{
				if(connectingComputersComboBox.isVisible())
				{
					JOptionPane.showConfirmDialog(null, "Please close connecting users","",JOptionPane.PLAIN_MESSAGE);
					clickedTimes--;
					return;
				}
				
				scrollPane.setVisible(true);
			}
			else
				scrollPane.setVisible(false);
		}
		else if(event.getSource() == connectingComputersButton)
		{
			if(connectingComputersComboBox.isVisible())
			{
				connectingComputersComboBox.setVisible(false);
			}
			else if(scrollPane.isVisible())
			{
				JOptionPane.showConfirmDialog(null, "Please close users table","",JOptionPane.PLAIN_MESSAGE);
				return;
			}
			else
			{
				DefaultComboBoxModel<String> model = ((DefaultComboBoxModel<String>)connectingComputersComboBox.getModel());
				model.removeAllElements();
				db.getConnectingComputers().stream().forEach(computer -> model.addElement(computer));
				connectingComputersComboBox.setVisible(true);
			}
		}
		else if(event.getSource() == permissionsButton)
		{
			MethodType methodTypeOfPermissionFrame = MethodType.methodType(void.class);
			MethodHandle callbackMethodOfPermissionFrame = null;
			
			try {
				callbackMethodOfPermissionFrame = MethodHandles.lookup().bind(this, "addUsers", methodTypeOfPermissionFrame);
			} catch (NoSuchMethodException | IllegalAccessException e) {
				e.printStackTrace();
			}
			new PermissionFrame(db , userName , callbackMethodOfPermissionFrame);
		}
		else if(event.getSource() == projectsComboBox)
		{
			DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) projectsComboBox.getModel();		
			expediteDirectoryPath.setText(db.getDirectory((String) model.getSelectedItem()));
			obligoText.setText(db.getCustomerObligation((String) projectsComboBox.getModel().getSelectedItem()) + "");
			depositText.setText(db.getCustomerDeposit((String) projectsComboBox.getModel().getSelectedItem()) + "");
		}
		else if(event.getSource() == expediteDirectoryButton)
		{			
			expediteDirectoryChooser = new JFileChooser();
			expediteDirectoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    //
		    // disable the "All files" option.
		    //
			expediteDirectoryChooser.setAcceptAllFileFilterUsed(false);
		    //    
		    if (expediteDirectoryChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) { 
		    	String directry = expediteDirectoryChooser.getSelectedFile().getAbsolutePath();
		    	expediteDirectoryPath.setText(directry);
		    	DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) projectsComboBox.getModel();
				db.setDirectory(((String)model.getSelectedItem()) , directry);
		      }
		    else {
		    	System.out.println("No Selection ");
		      }
		}
		else if(event.getSource() == addProjectButton)
		{
			projectNameLabel.setVisible(true);
			projectNameText.setVisible(true);
			obligoText.setText("");
			depositText.setText("");
			projectNameText.requestFocusInWindow();
		}
		else if(event.getSource() == deleteProjectButton)
		{
			int confirmed = JOptionPane.showConfirmDialog(null, "Are you sure?","",JOptionPane.YES_NO_OPTION);
			if(confirmed == JOptionPane.YES_OPTION)
			{
				DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) projectsComboBox.getModel();
				if(db.removeProject((String)model.getSelectedItem()))
				{
					model.removeElement(model.getSelectedItem());
					JOptionPane.showConfirmDialog(null, "Success","",JOptionPane.PLAIN_MESSAGE);
				}
				else
					JOptionPane.showConfirmDialog(null, "Fail","",JOptionPane.PLAIN_MESSAGE);
			}
			projectNameText.setText("");
			
			projectNameLabel.setVisible(false);
			projectNameText.setVisible(false);
		}
		else if(event.getSource() == confirmProjectNameButton)
		{
			DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) projectsComboBox.getModel();
			if(model.getIndexOf(projectNameText.getText()) != -1)
				JOptionPane.showConfirmDialog(null, "project already exists","",JOptionPane.PLAIN_MESSAGE);
			else if(projectNameText.isVisible())
			{
				if(projectNameText.getText().trim().equals(""))
				{
					projectNameLabel.setVisible(false);
					projectNameText.setVisible(false);
					obligoText.setText(db.getCustomerObligation((String) projectsComboBox.getModel().getSelectedItem()) + "");
					depositText.setText(db.getCustomerDeposit((String) projectsComboBox.getModel().getSelectedItem()) + "");
					return;
				}
				
				if(!obligoText.getText().equals("") && !NumberUtils.isCreatable(obligoText.getText()))
				{
					JOptionPane.showConfirmDialog(null, "obligo have to be numeric","",JOptionPane.PLAIN_MESSAGE);
					return;
				}
				
				if(!depositText.getText().equals("") && !NumberUtils.isCreatable(depositText.getText()))
				{
					JOptionPane.showConfirmDialog(null, "deposit have to be numeric","",JOptionPane.PLAIN_MESSAGE);
					return;
				}
				
				double obligo = !obligoText.getText().equals("") ? Double.parseDouble(obligoText.getText()) : 0;
				double deposit = !depositText.getText().equals("") ? Double.parseDouble(depositText.getText()) : 0;
				
				if(db.addProject(projectNameText.getText()))
				{
					String customer = projectNameText.getText();
					db.setCustomerObligation(customer, obligo);
					db.setCustomerDeposit(customer, deposit);
					
					model.addElement(projectNameText.getText());
					JOptionPane.showConfirmDialog(null, "Success","",JOptionPane.PLAIN_MESSAGE);
				}
				else
					JOptionPane.showConfirmDialog(null, "Fail","",JOptionPane.PLAIN_MESSAGE);
			}
			else
			{
				if(!obligoText.getText().equals("") && !NumberUtils.isCreatable(obligoText.getText()))
				{
					JOptionPane.showConfirmDialog(null, "obligo have to be numeric","",JOptionPane.PLAIN_MESSAGE);
					return;
				}
				
				if(!depositText.getText().equals("") && !NumberUtils.isCreatable(depositText.getText()))
				{
					JOptionPane.showConfirmDialog(null, "deposit have to be numeric","",JOptionPane.PLAIN_MESSAGE);
					return;
				}
				
				double obligo = !obligoText.getText().equals("") ? Double.parseDouble(obligoText.getText()) : 0;
				double deposit = !depositText.getText().equals("") ? Double.parseDouble(depositText.getText()) : 0;
				
				String customer = (String) projectsComboBox.getModel().getSelectedItem();
				if(db.setCustomerObligation(customer, obligo) && db.setCustomerDeposit(customer, deposit))
					JOptionPane.showConfirmDialog(null, "Success","",JOptionPane.PLAIN_MESSAGE);
				else
					JOptionPane.showConfirmDialog(null, "Fail","",JOptionPane.PLAIN_MESSAGE);
			}
				
			projectNameLabel.setVisible(false);
			projectNameText.setVisible(false);
			
			projectNameText.setText("");
			
			obligoText.setText(db.getCustomerObligation((String) projectsComboBox.getModel().getSelectedItem()) + "");
			depositText.setText(db.getCustomerDeposit((String) projectsComboBox.getModel().getSelectedItem()) + "");
		}
		
	}
}
