package Frames;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import mainPackage.DataBase;
import mainPackage.Globals;

public class AdminFrame implements ActionListener 
{
	private Globals globals;
	private JFrame frame;
	private JPanel panel;
	private JLabel viewUsersLabel;
	private JButton viewUsersButton;
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
		frame.setVisible(true);
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
		scrollPane.setLocation(170, 145);
		scrollPane.setSize(350,140);
		scrollPane.setVisible(false);
		panel.add(scrollPane);
		
		copyRight = new JLabel("<html><b>\u00a9 Naor Dalal</b></html>");
		copyRight.setLocation(30 , 430);
		copyRight.setSize(100,30);
		panel.add(copyRight);
		
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
				scrollPane.setVisible(true);
			else
				scrollPane.setVisible(false);
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
