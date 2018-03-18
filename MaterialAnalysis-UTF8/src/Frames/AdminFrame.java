package Frames;

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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

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
	private String userName;
	private JLabel copyRight;

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
		frame.setBounds(400, 100, 550, 500);
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
		panel.setSize(550, 500);
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
		
	}
}
