package Frames;
import javax.mail.Authenticator;
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
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import Senders.ActivitySender;
import Senders.Sender;
import mainPackage.Activity;
import mainPackage.CallBack;
import mainPackage.DataBase;
import mainPackage.Globals;
import mainPackage.MultiSelectionComboBox;
import mainPackage.SocialAuth;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class Menu implements ActionListener , DocumentListener{

	private JLabel nickNameLabel;
	private JLabel suppliersFileLabel;
	private JTextField nickNametext;
	private JLabel filePath;
	private JFileChooser suppliersFileChooser;
	private JButton suplierFileButton;
	private JFileChooser followUpDirectoryChooser;
	private JButton followUpDirectoryButton;
	private JFileChooser expediteDirectoryChooser;
	private JButton expediteDirectoryButton;
	private JButton next;
	private JFrame frame;
	private JPanel panel;
	private JRadioButton acceptOrder;
	private JRadioButton noDate;
	private JRadioButton pastDate;
	private JRadioButton futureDate;
	private JRadioButton beyondRequestDate;
	private JTextField dateLimited;
	private JLabel untilDateLabel;
	private JLabel passwordLabel;
	private JPasswordField passwordField;
	private JButton permissionsButton;
	private JLabel permissionsLabel;
	private File supplierEmailsFile = null;
	private JButton addBodyButton;
	private JButton deleteBodyButton;
	private JLabel mailTemplateLabel;
	private JLabel addAndDeleteLabel;
	private JLabel templateLabel;
	private JLabel addLabel;
	private JLabel deleteLabel;
	private JTextPane bodyTemplate;
	private JTextPane subjectTemplate;
	private JLabel copyRight;
	public static DataBase db;
	private Globals globals;
	private JRadioButton sendExpediteDateOrders;
	private JRadioButton expediteDate;
	private JLabel expediteOrdersLabel;
	private JComboBox<String> emailCC;
	private JButton addCCButton;
	private JButton deleteCCButton;
	private JTextField ccEmailtext;
	private JButton okButton;
	private JLabel ccEmailLabel;
	private JTable usersTable;
	private JScrollPane scrollPane;
	private JLabel viewUsersLabel;
	private JButton viewUsersButton;
	private JComboBox<String> expediteDateComboBox;
	private JButton addDateButton;
	private JButton deleteDateButton;
	private JLabel dateLabel;
	private JTextField expediteDateText;
	private JButton confirmDateButton;
	private int clickedTimes;
	private String directoryPath;
	private JRadioButton usesRadioButton;
	private JLabel fromLabel;
	private JTextField fromText;
	private JLabel untilLabel;
	private JTextField untilText;
	private JButton sendButton;
	private JLabel projectsLabel;
	private JComboBox<String> projectsComboBox;
	private JButton addProjectButton;
	private JButton deleteProjectButton;
	private JLabel projectNameLabel;
	private JTextField projectNameText;
	private JButton confirmProjectNameButton;
	private JButton updatePassword;
	private JRadioButton mrpRadioButton;
	private JRadioButton mrpSimRadioButton;
	private JLabel bomsQuantityLabel;
	private JTextField bomsQuantityText;
	private JLabel expediteDirectoryPath;
	private JLabel followUpDirectoryPath;
	private JLabel daysLabel;
	private JTextField daysText;
	private CallBack<Integer> callBack;
	
	
	public Menu(CallBack<Integer> callBack) 
	{
		this.callBack = callBack;
		initialize();
	}
	
	
	private void initialize() {
		
		directoryPath = null;
		clickedTimes = 0;
		db = new DataBase();
		
		globals = new Globals();
		
		frame = new JFrame("Gathering material analysis system");
		frame.setVisible(true);
		frame.setLayout(null);
		frame.getRootPane().setFocusable(true);
		frame.setBounds(300, 100, 900, 780);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) 
            {
                close();
            }
        });
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
	                close();
	            }
	        });
		
		panel = new JPanel();
		panel.setLocation(0 , 0);
		panel.setSize(900, 780);
		panel.setLayout(null);
		frame.add(panel);
		
		nickNameLabel = new JLabel("<html><u>User Name:</u></html>");
		nickNameLabel.setLocation(30,30);
		nickNameLabel.setSize(100,100);
		panel.add(nickNameLabel);
		
		
		suppliersFileLabel = new JLabel("<html><u>Attach suppliers Email file:</u></html>");
		suppliersFileLabel.setLocation(30,120);
		suppliersFileLabel.setSize(150,100);
		panel.add(suppliersFileLabel);
		
		nickNametext = new JTextField();
		nickNametext.setLocation(130, 70);
		nickNametext.setSize(150, 20);
		nickNametext.getDocument().addDocumentListener(this);
		panel.add(nickNametext);
		nickNametext.requestFocusInWindow();
		
		passwordLabel = new JLabel("<html><u>Password:</u></html>");
		passwordLabel.setLocation(330, 30);
		passwordLabel.setSize(90, 100);
		panel.add(passwordLabel);
		
		passwordField = new JPasswordField();
		passwordField.setLocation(420, 70);
		passwordField.setSize(150, 20);
		passwordField.getDocument().addDocumentListener(this);
		panel.add(passwordField);
		
		updatePassword = new JButton();
		updatePassword.setLocation(330, 100);
		updatePassword.setSize(50 , 50);
		updatePassword.addActionListener(this);
		updatePassword.setIcon(globals.updatePasswordIcon);  
		updatePassword.setFocusable(false);
		updatePassword.setContentAreaFilled(false);
		updatePassword.setPressedIcon(globals.clickUpdatePasswordIcon);
		updatePassword.setToolTipText("update password");
		panel.add(updatePassword);
		
		JLabel cc = new JLabel("CC:");
		cc.setLocation(610, 70);
		cc.setSize(30,20);
		panel.add(cc);
		
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
		emailCC = new MultiSelectionComboBox<String>(model);
		emailCC.setLocation(650, 70);
		emailCC.setSize(100,20);
		panel.add(emailCC);

		addCCButton = new JButton();
		addCCButton.setLocation(770, 55);
		addCCButton.setSize(50 , 50);
		addCCButton.addActionListener(this);
		addCCButton.setIcon(globals.addIcon);
		addCCButton.setFocusable(false);
		addCCButton.setContentAreaFilled(false);
		addCCButton.setPressedIcon(globals.clickAddIcon);
		addCCButton.setToolTipText("add CC");
		panel.add(addCCButton);
		
		deleteCCButton = new JButton();
		deleteCCButton.setLocation(820, 55);
		deleteCCButton.setSize(50 , 50);
		deleteCCButton.addActionListener(this);
		deleteCCButton.setIcon(globals.deleteIcon);
		deleteCCButton.setFocusable(false);
		deleteCCButton.setContentAreaFilled(false);
		deleteCCButton.setPressedIcon(globals.clickDeleteIcon);
		deleteCCButton.setToolTipText("delete CC");
		panel.add(deleteCCButton);
		
		ccEmailLabel = new JLabel("Email:");
		ccEmailLabel.setLocation(580, 120);
		ccEmailLabel.setSize(30,30);
		ccEmailLabel.setVisible(false);
		panel.add(ccEmailLabel);
		
		ccEmailtext = new JTextField();
		ccEmailtext.setLocation(620, 125);
		ccEmailtext.setSize(150, 20);
		ccEmailtext.setVisible(false);
		panel.add(ccEmailtext);
		
		okButton = new JButton();
		okButton.setLocation(780, 110);
		okButton.setSize(80 , 40);
		okButton.addActionListener(this);
		okButton.setVisible(false);
		okButton.setIcon(globals.okIcon);
		okButton.setFocusable(false);
		okButton.setContentAreaFilled(false);
		okButton.setPressedIcon(globals.clickOkIcon);
		okButton.setToolTipText("OK");
		panel.add(okButton);
		
		suplierFileButton = new JButton();
		suplierFileButton.setLocation(160 ,  140);
		suplierFileButton.setSize(100, 40);
		suplierFileButton.setIcon(globals.attachIcon);
		suplierFileButton.setFocusable(false);
		suplierFileButton.setContentAreaFilled(false);
		suplierFileButton.setPressedIcon(globals.clickAttachIcon);
		suplierFileButton.addActionListener(this);
		suplierFileButton.setToolTipText("attachment");
		panel.add(suplierFileButton);
		
		filePath = new JLabel("");
		filePath.setLocation(300 , 155);
		filePath.setSize(250, 20);
		panel.add(filePath);
		
		followUpDirectoryButton = new JButton();
		followUpDirectoryButton.setLocation(50 ,  180);
		followUpDirectoryButton.setSize(55, 40);
		followUpDirectoryButton.setIcon(globals.directoryIcon);
		followUpDirectoryButton.setFocusable(false);
		followUpDirectoryButton.setContentAreaFilled(false);
		followUpDirectoryButton.setPressedIcon(globals.clickDirectoryIcon);
		followUpDirectoryButton.addActionListener(this);
		followUpDirectoryButton.setToolTipText("Choose Directory");
		followUpDirectoryButton.setVisible(false);
		panel.add(followUpDirectoryButton);
		
		followUpDirectoryPath = new JLabel("");
		followUpDirectoryPath.setLocation(135, 195);
		followUpDirectoryPath.setSize(250, 20);
		followUpDirectoryPath.setVisible(false);
		panel.add(followUpDirectoryPath);
		
		next = new JButton();
		next.setLocation(800 , 680);
		next.setSize(60,40);
		next.addActionListener(this);
		next.setIcon(globals.nextIcon);
		next.setFocusable(false);
		//next.setOpaque(false);
		next.setContentAreaFilled(false);
		next.setPressedIcon(globals.clickNextIcon);
		next.setToolTipText("next");
		//next.setBorderPainted(false);

		panel.add(next);
		
		acceptOrder = new JRadioButton("Accept Order");
		acceptOrder.setLocation(30, 250);
		acceptOrder.setSize(120,20);
		acceptOrder.addActionListener(this);
		acceptOrder.setBackground(null);
		panel.add(acceptOrder);
		
		
		
		noDate = new JRadioButton("Without Due Date");
		noDate.setLocation(30, 280);
		noDate.setSize(120,20);
		noDate.addActionListener(this);
		noDate.setBackground(null);
		panel.add(noDate);
		
		pastDate = new JRadioButton("Past Due Date");
		pastDate.setLocation(30, 310);
		pastDate.setSize(100,20);
		pastDate.addActionListener(this);
		pastDate.setBackground(null);
		panel.add(pastDate);
		
		futureDate = new JRadioButton("Supply On Time");
		futureDate.setLocation(30, 340);
		futureDate.setSize(100,20);
		futureDate.addActionListener(this);
		futureDate.setBackground(null);
		panel.add(futureDate);
		
		untilDateLabel = new JLabel("Until:");
		untilDateLabel.setLocation(160 , 340);
		untilDateLabel.setSize(40,20);
		untilDateLabel.setVisible(false);
		panel.add(untilDateLabel);
		
		dateLimited = new JTextField();
		dateLimited.setLocation(210, 340);
		dateLimited.setSize(100 , 20);
		dateLimited.setVisible(false);
		panel.add(dateLimited);
		
		beyondRequestDate = new JRadioButton("Orders Beyond Request Date");
		beyondRequestDate.setLocation(30, 370);
		beyondRequestDate.setSize(170,20);
		beyondRequestDate.addActionListener(this);
		beyondRequestDate.setBackground(null);
		panel.add(beyondRequestDate);
		
		daysLabel = new JLabel("Days:");
		daysLabel.setLocation(230 , 370);
		daysLabel.setSize(40,20);
		daysLabel.setVisible(false);
		panel.add(daysLabel);
		
		daysText = new JTextField();
		daysText.setLocation(270, 370);
		daysText.setSize(50 , 20);
		daysText.setVisible(false);
		panel.add(daysText);
		
		permissionsLabel = new JLabel("<html><u>Add User & Admin Permission:</u></html>");
		permissionsLabel.setLocation(30, 500);
		permissionsLabel.setSize(150, 100);
		panel.add(permissionsLabel);
		
		
		permissionsButton = new JButton();
		permissionsButton.setLocation(200 , 530);
		permissionsButton.setSize(40 , 40);
		permissionsButton.addActionListener(this);
		permissionsButton.setIcon(globals.updateIcon);
		permissionsButton.setFocusable(false);
		//next.setOpaque(false);
		permissionsButton.setContentAreaFilled(false);
		permissionsButton.setPressedIcon(globals.clickUpdateIcon);
		permissionsButton.setToolTipText("update permissions for another emplyees");
		panel.add(permissionsButton);
		
		addBodyButton = new JButton();
		addBodyButton.setLocation(700, 250);
		addBodyButton.setSize(50 , 50);
		addBodyButton.addActionListener(this);
		addBodyButton.setIcon(globals.addIcon);
		addBodyButton.setFocusable(false);
		addBodyButton.setContentAreaFilled(false);
		addBodyButton.setPressedIcon(globals.clickAddIcon);
		addBodyButton.setToolTipText("add subject and body for these dates");
		panel.add(addBodyButton);
		
		deleteBodyButton = new JButton();
		deleteBodyButton.setLocation(700, 340);
		deleteBodyButton.setSize(50 , 50);
		deleteBodyButton.addActionListener(this);
		deleteBodyButton.setIcon(globals.deleteIcon);
		deleteBodyButton.setFocusable(false);
		deleteBodyButton.setContentAreaFilled(false);
		deleteBodyButton.setPressedIcon(globals.clickDeleteIcon);
		deleteBodyButton.setToolTipText("delete subject and body for these dates");
		panel.add(deleteBodyButton);
		
		JSeparator separator1 = new JSeparator(SwingConstants.VERTICAL);
		separator1.setBackground(Color.black);
		separator1.setLocation(350, 250);
		separator1.setSize(10, 140);
		panel.add(separator1);
		
		JSeparator separator2 = new JSeparator(SwingConstants.VERTICAL);
		separator2.setBackground(Color.black);
		separator2.setLocation(650, 250);
		separator2.setSize(10, 140);
		panel.add(separator2);
		
		
		mailTemplateLabel = new JLabel("<html><u>Follow up order report</u></html>");
		mailTemplateLabel.setLocation(30, 220);
		mailTemplateLabel.setSize(180, 30);
		panel.add(mailTemplateLabel);
		
		addAndDeleteLabel = new JLabel("<html><u>Add or Delete mail template</u></html>");
		addAndDeleteLabel.setLocation(660, 220);
		addAndDeleteLabel.setSize(150, 30);
		panel.add(addAndDeleteLabel);
		
		templateLabel = new JLabel("<html><u>View subject and body of mail</u></html>");
		templateLabel.setLocation(360, 220);
		templateLabel.setSize(300, 30);
		panel.add(templateLabel);

		addLabel = new JLabel("Add:");
		addLabel.setLocation(660, 260);
		addLabel.setSize(100, 30);
		panel.add(addLabel);
		
		deleteLabel = new JLabel("Delete:");
		deleteLabel.setLocation(660, 350);
		deleteLabel.setSize(100, 30);
		panel.add(deleteLabel);
		
		subjectTemplate = new JTextPane();
		subjectTemplate.setLocation(360 , 250);
		subjectTemplate.setSize(280 , 20);
		subjectTemplate.setEditable(false);
		subjectTemplate.setVisible(false);
		panel.add(subjectTemplate);
		
		bodyTemplate = new JTextPane();
		bodyTemplate.setLocation(360 , 290);
		bodyTemplate.setSize(280 , 100);
		bodyTemplate.setEditable(false);
		bodyTemplate.setVisible(false);
		panel.add(bodyTemplate);
		
		copyRight = new JLabel("<html><b>\u00a9 Naor Dalal</b></html>");
		copyRight.setLocation(30 , 710);
		copyRight.setSize(100,30);
		panel.add(copyRight);
		
		expediteOrdersLabel = new JLabel("<html><u>Convergence analysis of the control material</u></html>");
		expediteOrdersLabel.setLocation(30, 380);
		expediteOrdersLabel.setSize(250, 50);
		panel.add(expediteOrdersLabel);
		
		mrpSimRadioButton = new JRadioButton("MRP-SIM");
		mrpSimRadioButton.setLocation(30 , 420);
		mrpSimRadioButton.setSize(80, 20);
		mrpSimRadioButton.addActionListener(this);
		panel.add(mrpSimRadioButton);
		
		mrpRadioButton = new JRadioButton("SIM");
		mrpRadioButton.setLocation(115 , 420);
		mrpRadioButton.setSize(50, 20);
		mrpRadioButton.addActionListener(this);
		panel.add(mrpRadioButton);
		
		bomsQuantityLabel = new JLabel("Boms quantity:");
		bomsQuantityLabel.setLocation(40 ,  445);
		bomsQuantityLabel.setSize(80,20);
		bomsQuantityLabel.setVisible(false);
		panel.add(bomsQuantityLabel);
		
		bomsQuantityText = new JTextField();
		bomsQuantityText.setLocation(120 ,  445);
		bomsQuantityText.setSize(60,20);
		bomsQuantityText.setVisible(false);
		panel.add(bomsQuantityText);
					
		expediteDate = new JRadioButton("Import expedite orders report");
		expediteDate.setLocation(30 , 475);
		expediteDate.setSize(170, 20);
		expediteDate.addActionListener(this);
		panel.add(expediteDate);
		
		sendExpediteDateOrders = new JRadioButton("Export expedite orders reports");
		sendExpediteDateOrders.setLocation(30 , 510);
		sendExpediteDateOrders.setSize(180, 20);
		sendExpediteDateOrders.addActionListener(this);
		panel.add(sendExpediteDateOrders);
		
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
		scrollPane.setLocation(270, 520);
		scrollPane.setSize(350,140);
		scrollPane.setVisible(false);
		panel.add(scrollPane);
		
		
		viewUsersLabel = new JLabel("<html><u>View users:</u></html>");
		viewUsersLabel.setLocation(30, 590);
		viewUsersLabel.setSize(70,20);
		panel.add(viewUsersLabel);
		
		viewUsersButton = new JButton();
		viewUsersButton.setLocation(105, 575);
		viewUsersButton.setSize(50 , 50);
		viewUsersButton.addActionListener(this);
		viewUsersButton.setIcon(globals.viewIcon);
		viewUsersButton.setFocusable(false);
		viewUsersButton.setContentAreaFilled(false);
		viewUsersButton.setPressedIcon(globals.clickViewIcon);
		viewUsersButton.setToolTipText("View users details");
		panel.add(viewUsersButton);
		
		/*expediteDate = new JRadioButton("Expedite orders");
		expediteDate.setLocation(30 , 450);
		expediteDate.setSize(120, 20);
		expediteDate.addActionListener(this);
		panel.add(expediteDate);*/
		
		DefaultComboBoxModel<String> model3 = new DefaultComboBoxModel<String>();
		expediteDateComboBox = new JComboBox<String>(model3);
		expediteDateComboBox.setLocation(220, 475);
		expediteDateComboBox.setSize(100,20);
		expediteDateComboBox.setVisible(false);
		panel.add(expediteDateComboBox);

		addDateButton = new JButton();
		addDateButton.setLocation(350, 459);
		addDateButton.setSize(50 , 50);
		addDateButton.addActionListener(this);
		addDateButton.setIcon(globals.addIcon);
		addDateButton.setFocusable(false);
		addDateButton.setContentAreaFilled(false);
		addDateButton.setVisible(false);
		addDateButton.setPressedIcon(globals.clickAddIcon);
		addDateButton.setToolTipText("add date");
		panel.add(addDateButton);
		
		deleteDateButton = new JButton();
		deleteDateButton.setLocation(400, 460);
		deleteDateButton.setSize(50 , 50);
		deleteDateButton.addActionListener(this);
		deleteDateButton.setIcon(globals.deleteIcon);
		deleteDateButton.setFocusable(false);
		deleteDateButton.setContentAreaFilled(false);
		deleteDateButton.setVisible(false);
		deleteDateButton.setPressedIcon(globals.clickDeleteIcon);
		deleteDateButton.setToolTipText("delete date");
		panel.add(deleteDateButton);
		
		dateLabel = new JLabel("Date:");
		dateLabel.setLocation(480, 470);
		dateLabel.setSize(30,30);
		dateLabel.setVisible(false);
		panel.add(dateLabel);
		
		expediteDateText = new JTextField();
		expediteDateText.setLocation(520, 475);
		expediteDateText.setSize(150, 20);
		expediteDateText.setVisible(false);
		panel.add(expediteDateText);
		
		confirmDateButton = new JButton();
		confirmDateButton.setLocation(670, 460);
		confirmDateButton.setSize(80 , 40);
		confirmDateButton.addActionListener(this);
		confirmDateButton.setVisible(false);
		confirmDateButton.setIcon(globals.okIcon);
		confirmDateButton.setFocusable(false);
		confirmDateButton.setContentAreaFilled(false);
		confirmDateButton.setPressedIcon(globals.clickOkIcon);
		confirmDateButton.setToolTipText("OK");
		panel.add(confirmDateButton);
		
		usesRadioButton = new JRadioButton("Send activity Report");
		usesRadioButton.setLocation(30, 640);
		usesRadioButton.setSize(135,20);
		usesRadioButton.addActionListener(this);
		usesRadioButton.setBackground(null);
		usesRadioButton.setVisible(false);
		panel.add(usesRadioButton);
		
		fromLabel = new JLabel("From:");
		fromLabel.setLocation(170, 635);
		fromLabel.setSize(30,30);
		fromLabel.setVisible(false);
		panel.add(fromLabel);
		
		
		fromText = new JTextField();
		fromText.setLocation(210, 640);
		fromText.setSize(100 , 20);
		fromText.setVisible(false);
		panel.add(fromText);
		
		
		untilLabel = new JLabel("Until:");
		untilLabel.setLocation(320, 635);
		untilLabel.setSize(30,30);
		untilLabel.setVisible(false);
		panel.add(untilLabel);
		
		untilText = new JTextField();
		untilText.setLocation(360, 640);
		untilText.setSize(100 , 20);
		untilText.setVisible(false);
		panel.add(untilText);
		
		sendButton = new JButton();
		sendButton.setLocation(475 , 627);
		sendButton.setSize(40 , 40);
		sendButton.addActionListener(this);
		sendButton.setIcon(globals.sendIcon);
		sendButton.setFocusable(false);
		//next.setOpaque(false);
		sendButton.setContentAreaFilled(false);
		sendButton.setPressedIcon(globals.clickSendIcon);
		sendButton.setToolTipText("send");
		sendButton.setVisible(false);
		panel.add(sendButton);
		
		
		projectsLabel = new JLabel("<html><u>Projects:</u></html>");
		projectsLabel.setLocation(30, 680);
		projectsLabel.setSize(50, 20);
		projectsLabel.setVisible(false);
		panel.add(projectsLabel);
		
		DefaultComboBoxModel<String> model4 = new DefaultComboBoxModel<String>();
		List<String> projects = db.getAllProjects();
		for (String project : projects) 
		{
			model4.addElement(project);
		}
		projectsComboBox = new JComboBox<String>(model4);
		projectsComboBox.setLocation(90, 680);
		projectsComboBox.setSize(130,20);
		projectsComboBox.setVisible(false);
		projectsComboBox.addActionListener(this);
		panel.add(projectsComboBox);
		
		
		
		expediteDirectoryButton = new JButton();
		expediteDirectoryButton.setLocation(230 , 670);
		expediteDirectoryButton.setSize(55, 40);
		expediteDirectoryButton.setIcon(globals.directoryIcon);
		expediteDirectoryButton.setFocusable(false);
		expediteDirectoryButton.setContentAreaFilled(false);
		expediteDirectoryButton.setPressedIcon(globals.clickDirectoryIcon);
		expediteDirectoryButton.addActionListener(this);
		expediteDirectoryButton.setToolTipText("Choose Directory");
		expediteDirectoryButton.setVisible(false);
		panel.add(expediteDirectoryButton);
		
		expediteDirectoryPath = new JLabel("");
		expediteDirectoryPath.setLocation(200, 720);
		expediteDirectoryPath.setSize(250, 20);
		expediteDirectoryPath.setVisible(false);
		panel.add(expediteDirectoryPath);
		
		addProjectButton = new JButton();
		addProjectButton.setLocation(300 , 670);
		addProjectButton.setSize(40 , 40);
		addProjectButton.addActionListener(this);
		addProjectButton.setIcon(globals.addIcon);
		addProjectButton.setFocusable(false);
		addProjectButton.setContentAreaFilled(false);
		addProjectButton.setPressedIcon(globals.clickAddIcon);
		addProjectButton.setToolTipText("add project");
		addProjectButton.setVisible(false);
		panel.add(addProjectButton);
		
		deleteProjectButton = new JButton();
		deleteProjectButton.setLocation(360 , 670);
		deleteProjectButton.setSize(40 , 40);
		deleteProjectButton.addActionListener(this);
		deleteProjectButton.setIcon(globals.deleteIcon);
		deleteProjectButton.setFocusable(false);
		deleteProjectButton.setContentAreaFilled(false);
		deleteProjectButton.setPressedIcon(globals.clickDeleteIcon);
		deleteProjectButton.setToolTipText("delete project");
		deleteProjectButton.setVisible(false);
		panel.add(deleteProjectButton);
		
		projectNameLabel = new JLabel("Project name:");
		projectNameLabel.setLocation(430, 680);
		projectNameLabel.setSize(70, 20);
		projectNameLabel.setVisible(false);
		panel.add(projectNameLabel);
		
		projectNameText = new JTextField();
		projectNameText.setLocation(510, 680);
		projectNameText.setSize(100 , 20);
		projectNameText.setVisible(false);
		panel.add(projectNameText);
		
		
		confirmProjectNameButton = new JButton();
		confirmProjectNameButton.setLocation(640 , 665);
		confirmProjectNameButton.setSize(40 , 40);
		confirmProjectNameButton.addActionListener(this);
		confirmProjectNameButton.setIcon(globals.okIcon);
		confirmProjectNameButton.setFocusable(false);
		confirmProjectNameButton.setContentAreaFilled(false);
		confirmProjectNameButton.setPressedIcon(globals.clickOkIcon);
		confirmProjectNameButton.setToolTipText("confirm");
		confirmProjectNameButton.setVisible(false);
		panel.add(confirmProjectNameButton); 
		
		
	}


	@Override
	public void actionPerformed(ActionEvent evt) {
		if(evt.getSource() == suplierFileButton)
		{
			if(!db.checkPurchasingPermission(nickNametext.getText(), new String(passwordField.getPassword())))
			{
				JOptionPane.showConfirmDialog(null, "you don't have a permission","",JOptionPane.PLAIN_MESSAGE);
				return;
			}
			
			suppliersFileChooser = new JFileChooser();
			if(directoryPath != null)
				suppliersFileChooser.setCurrentDirectory(new File(directoryPath));
			int returnVal = suppliersFileChooser.showOpenDialog(null);
		    if (returnVal == JFileChooser.APPROVE_OPTION) {
		        supplierEmailsFile = suppliersFileChooser.getSelectedFile();
		        // What to do with the file, e.g. display it in a TextArea
				filePath.setText(supplierEmailsFile.getAbsolutePath());
				directoryPath = supplierEmailsFile.getPath();
		    } else {
		        System.out.println("File access cancelled by user.");
		    }
		}
		
		else if(evt.getSource() == followUpDirectoryButton)
		{
			if(!db.checkAddOrDeletePermission(nickNametext.getText(), new String(passwordField.getPassword())))
			{
				JOptionPane.showConfirmDialog(null, "you don't have a permission","",JOptionPane.PLAIN_MESSAGE);
				return;
			}
			
			followUpDirectoryChooser = new JFileChooser();
			followUpDirectoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    //
		    // disable the "All files" option.
		    //
			followUpDirectoryChooser.setAcceptAllFileFilterUsed(false);
		    //    
		    if (followUpDirectoryChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) { 
		    	String directory = followUpDirectoryChooser.getSelectedFile().getAbsolutePath();
		    	db.setFollowUpDirectory(directory);
		    	followUpDirectoryPath.setText(directory);
		      }
		    else {
		      System.out.println("No Selection ");
		      }
		}
		else if(evt.getSource() == expediteDirectoryButton)
		{
			if(!db.checkAddOrDeletePermission(nickNametext.getText(), new String(passwordField.getPassword())))
			{
				JOptionPane.showConfirmDialog(null, "you don't have a permission","",JOptionPane.PLAIN_MESSAGE);
				return;
			}
			
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
		else if(evt.getSource() == next)
		{
			//open next frame
			if(!db.checkPurchasingPermission(nickNametext.getText(), new String(passwordField.getPassword())))
			{
				if(nickNametext.getText().equals("") || passwordField.getPassword().length == 0 || !expediteDate.isSelected())
				{
					JOptionPane.showConfirmDialog(null, "please fill all the details","",JOptionPane.PLAIN_MESSAGE);
					return;
				}
			}
			else if(nickNametext.getText().equals("") || passwordField.getPassword().length == 0 ||
					(!acceptOrder.isSelected() && !noDate.isSelected() && !pastDate.isSelected() && !futureDate.isSelected() && !beyondRequestDate.isSelected()
							&& !expediteDate.isSelected() && !sendExpediteDateOrders.isSelected()))
				{
					JOptionPane.showConfirmDialog(null, "please fill all the details","",JOptionPane.PLAIN_MESSAGE);
					return;
				}
				if(!db.checkConnectPermission(nickNametext.getText(), new String(passwordField.getPassword())))
				{
					JOptionPane.showConfirmDialog(null, "you don't have a permission","",JOptionPane.PLAIN_MESSAGE);
					return;
				}
				
			if(futureDate.isSelected())
			{
				if(dateLimited.getText().equals("") || !isValidDate(dateLimited.getText()))
				{
					JOptionPane.showConfirmDialog(null, "please fill a correct date","",JOptionPane.PLAIN_MESSAGE);
					return;
				}
			}
			if(beyondRequestDate.isSelected())
			{
				if(!org.apache.commons.lang3.StringUtils.isNumeric(daysText.getText().trim()))
				{
					JOptionPane.showConfirmDialog(null, "please enter a valid number of days","",JOptionPane.PLAIN_MESSAGE);
					return;
				}
			}
			if(expediteDate.isSelected())
			{
				DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) expediteDateComboBox.getModel();
				if(model.getSize() == 0)
				{
					JOptionPane.showConfirmDialog(null, "please enter at least one expedite date","",JOptionPane.PLAIN_MESSAGE);
					return;
				}
				
				if(mrpSimRadioButton.isSelected())
				{
					String bomsQuantity = bomsQuantityText.getText().trim();
					if(!bomsQuantity.equals("") && !org.apache.commons.lang3.StringUtils.isNumeric(bomsQuantity))
					{
						JOptionPane.showConfirmDialog(null, "please enter a valid number of boms","",JOptionPane.PLAIN_MESSAGE);
						return;
					}
				}
			}
			else
			{
				if(supplierEmailsFile == null)
				{
					JOptionPane.showConfirmDialog(null, "please enter a suppliers email file","",JOptionPane.PLAIN_MESSAGE);
					return;
				}
					
			}
			//this.frame.dispose();
			
			DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yy");
			DateFormat outsourceFormat = new SimpleDateFormat("dd/MM/yyyy");
			DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) expediteDateComboBox.getModel();
			List<String> datesList = new ArrayList<String>();
			List<Date> tempDateList = new ArrayList<Date>();
			
			Date today = new Date();
			String toExp = outsourceFormat.format(today);
			try {
				today = outsourceFormat.parse(toExp);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			
			Calendar c = Calendar.getInstance();
			c.setTime(today);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			today = c.getTime();
			
			if(mrpSimRadioButton.isSelected())
				tempDateList.add(today);
			
			for(int i = 0 ; i < model.getSize() ; i++)
			{
				datesList.add(model.getElementAt(i));
				try {
					Date toExpediteDate = sourceFormat.parse(model.getElementAt(i));
					toExp = outsourceFormat.format(toExpediteDate);
					toExpediteDate = outsourceFormat.parse(toExp);
					c = Calendar.getInstance();
					c.setTime(toExpediteDate);
					c.set(Calendar.HOUR_OF_DAY, 0);
					c.set(Calendar.MINUTE, 0);
					c.set(Calendar.SECOND, 0);
					c.set(Calendar.MILLISECOND, 0);
					toExpediteDate = c.getTime();
					tempDateList.add(toExpediteDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			
			Collections.sort(tempDateList);
			datesList = tempDateList.stream().map(date->outsourceFormat.format(date)).collect(Collectors.toList());
			
			if(futureDate.isSelected())
			{
				datesList = new ArrayList<String>();
				datesList.add(dateLimited.getText());
			}
			String project = "";
			if(expediteDate.isSelected())
			{
				List<String> projects = db.getAllProjects();
				project = (String) JOptionPane.showInputDialog(null , "Project: " , "Select project" , JOptionPane.PLAIN_MESSAGE, 
						null , projects.toArray() , projects.get(0));
				
				if(project == null)
					return;
				
				int confirmed = JOptionPane.showConfirmDialog(null , "Deafult excess value price is: " + Globals.deafultPrice + "$"+
						"\nDo you want change?" , "" , JOptionPane.YES_NO_OPTION);
						
						
				if(confirmed == JOptionPane.YES_OPTION)
				{
					String answer = JOptionPane.showInputDialog(null , "New price: " , "Update Price" , JOptionPane.OK_CANCEL_OPTION);
					if(answer != null)
						Globals.setPrice(Double.parseDouble(answer));
					else
						Globals.setDeafultPrice();
				}
				else
					Globals.setDeafultPrice();
			}
			
			if(sendExpediteDateOrders.isSelected())
			{
				List<String> projects = db.getAllProjects();
				project = (String) JOptionPane.showInputDialog(null , "Project: " , "Select project" , JOptionPane.PLAIN_MESSAGE, 
						null , projects.toArray() , projects.get(0));
				
				if(project == null)
					return;
			}
			
						
			int id = globals.getDatesId(acceptOrder.isSelected() , noDate.isSelected() , pastDate.isSelected() , futureDate.isSelected() 
					,sendExpediteDateOrders.isSelected() , beyondRequestDate.isSelected());
			

			List<String> selectedCC = ((MultiSelectionComboBox<String>)emailCC).getSelectedItems();
			String email = db.getEmail(nickNametext.getText() , new String(passwordField.getPassword()));
			boolean followUp = noDate.isSelected() || pastDate.isSelected() || futureDate.isSelected() || acceptOrder.isSelected() || beyondRequestDate.isSelected();
			boolean expedite = sendExpediteDateOrders.isSelected();
			if((followUp || expedite) && !selectedCC.contains(email))
			{
				int confirmed = JOptionPane.showConfirmDialog(null, "Do you want add your email in CC?","",JOptionPane.YES_NO_OPTION);
				if(confirmed == JOptionPane.YES_OPTION)
				{
					emailCC.addItem(email);
				}
			}
			
			boolean mrpSim = mrpSimRadioButton.isSelected();
			int bomsQuantity = bomsQuantityText.getText().trim().equals("") ? 0 : Integer.parseInt(bomsQuantityText.getText().trim());
			
			Activity activity = new Activity(nickNametext.getText(), followUp, acceptOrder.isSelected()
					, noDate.isSelected(), pastDate.isSelected(), futureDate.isSelected(), beyondRequestDate.isSelected() , !followUp, expediteDate.isSelected()
					, sendExpediteDateOrders.isSelected(), project, null);
			
			SenderFrame senderFrame = new SenderFrame(email , new String(passwordField.getPassword()) , supplierEmailsFile , activity , datesList , id 
					, db.checkPurchasingPermission(nickNametext.getText(), new String(passwordField.getPassword())), mrpSim , bomsQuantity);
						
			senderFrame.setDb(db);
			
			senderFrame.setCC(((MultiSelectionComboBox<String>)emailCC).getSelectedItems());
			senderFrame.setProject(project);
			
			
			int days = (daysText.getText().equals("")) ? 0 : Integer.parseInt(daysText.getText());
			senderFrame.setDaysBeyondRequestDate(days);
			
			senderFrame.initialize();
		}
		else if(evt.getSource() == acceptOrder)
		{
			if(acceptOrder.isSelected())
			{
				if(!db.checkPurchasingPermission(nickNametext.getText(), new String(passwordField.getPassword())))
				{
					JOptionPane.showConfirmDialog(null, "you don't have a permission","",JOptionPane.PLAIN_MESSAGE);
					acceptOrder.setSelected(false);
					return;
				}
				if(expediteDate.isSelected() || sendExpediteDateOrders.isSelected())
				{
					JOptionPane.showConfirmDialog(null, "cannot select both expedite and follow up option","",JOptionPane.PLAIN_MESSAGE);
					acceptOrder.setSelected(false);
					return;
				}
			}
			updateTemplateText();
		}
		else if(evt.getSource() == noDate)
		{
			if(noDate.isSelected())
			{
				if(!db.checkPurchasingPermission(nickNametext.getText(), new String(passwordField.getPassword())))
				{
					JOptionPane.showConfirmDialog(null, "you don't have a permission","",JOptionPane.PLAIN_MESSAGE);
					noDate.setSelected(false);
					return;
				}
				
				if(expediteDate.isSelected() || sendExpediteDateOrders.isSelected())
				{
					JOptionPane.showConfirmDialog(null, "cannot select both expedite and follow up option","",JOptionPane.PLAIN_MESSAGE);
					noDate.setSelected(false);
					return;
				}
			}
			updateTemplateText();
		}
		else if(evt.getSource() == pastDate)
		{
			if(pastDate.isSelected())
			{
				if(!db.checkPurchasingPermission(nickNametext.getText(), new String(passwordField.getPassword())))
				{
					JOptionPane.showConfirmDialog(null, "you don't have a permission","",JOptionPane.PLAIN_MESSAGE);
					pastDate.setSelected(false);
					return;
				}
				
				if(expediteDate.isSelected() || sendExpediteDateOrders.isSelected())
				{
					JOptionPane.showConfirmDialog(null, "cannot select both expedite and follow up option","",JOptionPane.PLAIN_MESSAGE);
					pastDate.setSelected(false);
					return;
				}
			}
			updateTemplateText();
		}
		else if(evt.getSource() == futureDate)
		{
			if(futureDate.isSelected())
			{
				if(!db.checkPurchasingPermission(nickNametext.getText(), new String(passwordField.getPassword())))
				{
					JOptionPane.showConfirmDialog(null, "you don't have a permission","",JOptionPane.PLAIN_MESSAGE);
					futureDate.setSelected(false);
					return;
				}
				
				if(expediteDate.isSelected() || sendExpediteDateOrders.isSelected())
				{
					JOptionPane.showConfirmDialog(null, "cannot select both expedite and follow up option","",JOptionPane.PLAIN_MESSAGE);
					futureDate.setSelected(false);
					return;
				}
				
				untilDateLabel.setText("Until:");
				untilDateLabel.setLocation(160 , 340);
				
				dateLimited.setText("");
				dateLimited.setLocation(210, 340);
				
				untilDateLabel.setVisible(true);
				dateLimited.setVisible(true);
				
				dateLimited.requestFocusInWindow();
			}
			else
			{
				untilDateLabel.setVisible(false);
				dateLimited.setVisible(false);
			}
			
			updateTemplateText();
		}
		else if(evt.getSource() == beyondRequestDate)
		{
			if(beyondRequestDate.isSelected())
			{
				if(!db.checkPurchasingPermission(nickNametext.getText(), new String(passwordField.getPassword())))
				{
					JOptionPane.showConfirmDialog(null, "you don't have a permission","",JOptionPane.PLAIN_MESSAGE);
					beyondRequestDate.setSelected(false);
					return;
				}
				
				if(expediteDate.isSelected() || sendExpediteDateOrders.isSelected())
				{
					JOptionPane.showConfirmDialog(null, "cannot select both expedite and follow up option","",JOptionPane.PLAIN_MESSAGE);
					beyondRequestDate.setSelected(false);
					return;
				}
				
				daysLabel.setVisible(true);
				daysText.setText("");
				daysText.setVisible(true);
			}
			else
			{
				daysLabel.setVisible(false);
				daysText.setVisible(false);
			}
			
			updateTemplateText();
		}
		else if(evt.getSource() == permissionsButton)
		{
			if(nickNametext.getText().equals("") || passwordField.getPassword().length == 0)
			{
				JOptionPane.showConfirmDialog(null, "please fill email and password","",JOptionPane.PLAIN_MESSAGE);
			}
			else
			{
				if(db.checkAddOrDeletePermission(nickNametext.getText(), new String(passwordField.getPassword())))
				{
					MethodType methodTypeOfPermissionFrame = MethodType.methodType(void.class);
					MethodHandle callbackMethodOfPermissionFrame = null;
					
					try {
						callbackMethodOfPermissionFrame = MethodHandles.lookup().bind(this, "addUsers", methodTypeOfPermissionFrame);
					} catch (NoSuchMethodException | IllegalAccessException e) {
						e.printStackTrace();
					}
					new PermissionFrame(db , nickNametext.getText() , callbackMethodOfPermissionFrame);
				}					
				else
					JOptionPane.showConfirmDialog(null, "you don't have a permission","",JOptionPane.PLAIN_MESSAGE);
			}
		}
		else if(evt.getSource() == addBodyButton)
		{
			if(nickNametext.getText().equals("") || passwordField.getPassword().length == 0 ||
					(!acceptOrder.isSelected() && !noDate.isSelected() && !pastDate.isSelected() && !futureDate.isSelected() && !beyondRequestDate.isSelected()
							&& !sendExpediteDateOrders.isSelected()))
			{
				JOptionPane.showConfirmDialog(null, "please choose dates and fill email and password","",JOptionPane.PLAIN_MESSAGE);
			}
			else 
			{
				if(db.checkAddOrDeletePermission(nickNametext.getText(), new String(passwordField.getPassword())))
				{
					if(!bodyTemplate.getText().equals("") || !subjectTemplate.getText().equals(""))
					{
						JOptionPane.showConfirmDialog(null, "subject and body already exist for these dates","",JOptionPane.PLAIN_MESSAGE);
						return;
					}
					int id = globals.getDatesId(acceptOrder.isSelected() , noDate.isSelected() , pastDate.isSelected() , futureDate.isSelected() 
							,sendExpediteDateOrders.isSelected() , beyondRequestDate.isSelected());
					
					MethodType methodTypeOfBodyFrame = MethodType.methodType(void.class);
					MethodHandle callbackMethodOfBodyFrame = null;
					
					try {
						callbackMethodOfBodyFrame = MethodHandles.lookup().bind(this, "updateTemplateText", methodTypeOfBodyFrame);
					} catch (NoSuchMethodException | IllegalAccessException e) {
						e.printStackTrace();
					}
					
					new BodyFrame(db , id , callbackMethodOfBodyFrame);
				}
					
				else
					JOptionPane.showConfirmDialog(null, "you don't have a permission","",JOptionPane.PLAIN_MESSAGE);
			}
		}
		else if(evt.getSource() == deleteBodyButton)
		{
			if(nickNametext.getText().equals("") || passwordField.getPassword().length == 0 ||
					(!acceptOrder.isSelected() && !noDate.isSelected() && !pastDate.isSelected() && !futureDate.isSelected() && !beyondRequestDate.isSelected()
							&& !sendExpediteDateOrders.isSelected()))
			{
				JOptionPane.showConfirmDialog(null, "please choose dates and fill email and password","",JOptionPane.PLAIN_MESSAGE);
			}
			else 
			{
				if(db.checkAddOrDeletePermission(nickNametext.getText(), new String(passwordField.getPassword())))
				{
					int id = globals.getDatesId(acceptOrder.isSelected() , noDate.isSelected() , pastDate.isSelected() , futureDate.isSelected()
							,sendExpediteDateOrders.isSelected() , beyondRequestDate.isSelected());
					if(db.deleteSubjectAndBody(id))
					{
						JOptionPane.showConfirmDialog(null, "success","",JOptionPane.PLAIN_MESSAGE);
						updateTemplateText();
					}
					else
					{
						JOptionPane.showConfirmDialog(null, "fail , there is not subject and body for these dates","",JOptionPane.PLAIN_MESSAGE);
					}
					
				}
					
				else
					JOptionPane.showConfirmDialog(null, "you don't have a permission","",JOptionPane.PLAIN_MESSAGE);
			}	
		}
		else if(evt.getSource() == expediteDate)
		{
			if(!db.checkConnectPermission(nickNametext.getText(), new String(passwordField.getPassword())))
			{
				JOptionPane.showConfirmDialog(null, "you don't have a permission","",JOptionPane.PLAIN_MESSAGE);
				expediteDate.setSelected(false);
				return;
			}
			
			if(acceptOrder.isSelected() || noDate.isSelected() || pastDate.isSelected() || futureDate.isSelected() || beyondRequestDate.isSelected() || sendExpediteDateOrders.isSelected())
			{
				JOptionPane.showConfirmDialog(null, "cannot select expedite with follow up option","",JOptionPane.PLAIN_MESSAGE);
				expediteDate.setSelected(false);
				return;
			}
			
			if(!mrpRadioButton.isSelected() && !mrpSimRadioButton.isSelected())
			{
				JOptionPane.showConfirmDialog(null, "You have to choose type of MRP","",JOptionPane.PLAIN_MESSAGE);
				expediteDate.setSelected(false);
				return;
			}
			else
			{
				if(expediteDate.isSelected())
				{
					expediteDateComboBox.setVisible(true);
					addDateButton.setVisible(true);
					deleteDateButton.setVisible(true);
				}
				else
				{
					expediteDateComboBox.setVisible(false);
					addDateButton.setVisible(false);
					deleteDateButton.setVisible(false);	
					dateLabel.setVisible(false);
					expediteDateText.setVisible(false);
					confirmDateButton.setVisible(false);
				}
			}
			
			//updateTemplateText();
		}
		else if(evt.getSource() == sendExpediteDateOrders)
		{
			if(!db.checkPurchasingPermission(nickNametext.getText(), new String(passwordField.getPassword())))
			{
				JOptionPane.showConfirmDialog(null, "you don't have a permission","",JOptionPane.PLAIN_MESSAGE);
				sendExpediteDateOrders.setSelected(false);
				return;
			}
			
			if(acceptOrder.isSelected() || noDate.isSelected() || pastDate.isSelected() || futureDate.isSelected() || beyondRequestDate.isSelected() || expediteDate.isSelected())
			{
				JOptionPane.showConfirmDialog(null, "cannot select expedite with follow up option","",JOptionPane.PLAIN_MESSAGE);
				sendExpediteDateOrders.setSelected(false);
				return;
			}
			
			updateTemplateText();
		}
		else if(evt.getSource() == addDateButton)
		{
			expediteDateText.setText("");
			
			dateLabel.setVisible(true);
			expediteDateText.setVisible(true);
			confirmDateButton.setVisible(true);
			
			expediteDateText.requestFocusInWindow();
		}
		else if(evt.getSource() == deleteDateButton)
		{
			DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) expediteDateComboBox.getModel();
			model.removeElement(model.getSelectedItem());
			
			expediteDateText.setText("");
			
			dateLabel.setVisible(false);
			expediteDateText.setVisible(false);
			confirmDateButton.setVisible(false);
		}
		else if(evt.getSource() == confirmDateButton)
		{
			Date today = new Date();
			Calendar c = Calendar.getInstance();
			c.setTime(today);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			today = c.getTime();
			
			if(expediteDateText.getText().equals(""))
			{
				expediteDateText.setText("");
				
				dateLabel.setVisible(false);
				expediteDateText.setVisible(false);
				confirmDateButton.setVisible(false);
				return;
			}
			
			if(!isValidDate(expediteDateText.getText()))
			{
				JOptionPane.showConfirmDialog(null, "please fill a correct date","",JOptionPane.PLAIN_MESSAGE);
				return;
			}
			
			if(mrpSimRadioButton.isSelected())
			{
				Date newDate = getDate(expediteDateText.getText());
				if(!newDate.after(today))
				{
					JOptionPane.showConfirmDialog(null, "Expedite date have to be after today","",JOptionPane.PLAIN_MESSAGE);
					return;
				}
			}
			
			DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) expediteDateComboBox.getModel();
			if(model.getIndexOf(expediteDateText.getText()) != -1)
				JOptionPane.showConfirmDialog(null, "date already exists","",JOptionPane.PLAIN_MESSAGE);
			else
				model.addElement(expediteDateText.getText());
			
			expediteDateText.setText("");
			
			dateLabel.setVisible(false);
			expediteDateText.setVisible(false);
			confirmDateButton.setVisible(false);
			return;
		}
		else if(evt.getSource() == addCCButton)
		{
			if(!db.checkPurchasingPermission(nickNametext.getText(), new String(passwordField.getPassword())))
			{
				JOptionPane.showConfirmDialog(null, "you don't have a permission","",JOptionPane.PLAIN_MESSAGE);
				return;
			}
			ccEmailtext.setText("");
			ccEmailLabel.setVisible(true);
			ccEmailtext.setVisible(true);
			okButton.setVisible(true);
			
			ccEmailtext.requestFocusInWindow();
		}
		else if(evt.getSource() == deleteCCButton)
		{	
			if(!db.checkPurchasingPermission(nickNametext.getText(), new String(passwordField.getPassword())))
			{
				JOptionPane.showConfirmDialog(null, "you don't have a permission","",JOptionPane.PLAIN_MESSAGE);
				return;
			}
			
			DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) emailCC.getModel();
			if(model.getSelectedItem() != null)
			{
				db.removeCC(model.getSelectedItem().toString());
				emailCC.removeItem(model.getSelectedItem());
			}

			
			ccEmailtext.setText("");
			
			ccEmailLabel.setVisible(false);
			ccEmailtext.setVisible(false);
			okButton.setVisible(false);
		}
		else if(evt.getSource() == okButton)
		{
			DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) emailCC.getModel();
			if(ccEmailtext.getText().trim().equals(""))
			{
				ccEmailLabel.setVisible(false);
				ccEmailtext.setVisible(false);
				okButton.setVisible(false);
				return;
			}
			if(model.getIndexOf(ccEmailtext.getText()) != -1)
				JOptionPane.showConfirmDialog(null, "cc already exists","",JOptionPane.PLAIN_MESSAGE);
			else
			{
				emailCC.addItem(ccEmailtext.getText());
				db.addCC(ccEmailtext.getText());
			}
				
			
			ccEmailLabel.setVisible(false);
			ccEmailtext.setVisible(false);
			okButton.setVisible(false);
			return;
		}
		else if(evt.getSource() == viewUsersButton)
		{
			if(sendButton.isVisible())
			{
				JOptionPane.showConfirmDialog(null, "Please unselected send activity report","",JOptionPane.PLAIN_MESSAGE);
				return;
			}
			if(!db.checkAddOrDeletePermission(nickNametext.getText(), new String(passwordField.getPassword())))
			{
				JOptionPane.showConfirmDialog(null, "you don't have a permission","",JOptionPane.PLAIN_MESSAGE);
				scrollPane.setVisible(false);
				return;
			}
			
			clickedTimes++;
			if(clickedTimes % 2 != 0)
				scrollPane.setVisible(true);
			else
				scrollPane.setVisible(false);
		}
		else if(evt.getSource() == usesRadioButton)
		{
			if(scrollPane.isVisible())
			{
				JOptionPane.showConfirmDialog(null, "Please close users table","",JOptionPane.PLAIN_MESSAGE);
				usesRadioButton.setSelected(false);
				return;
			}
			
			if(usesRadioButton.isSelected())
			{
				fromLabel.setVisible(true);
				fromText.setVisible(true);
				untilLabel.setVisible(true);
				untilText.setVisible(true);
				sendButton.setVisible(true);
				fromText.requestFocusInWindow();
			}
			else
			{
				fromLabel.setVisible(false);
				fromText.setVisible(false);
				untilLabel.setVisible(false);
				untilText.setVisible(false);
				sendButton.setVisible(false);
				
				fromText.setText("");
				untilText.setText("");
			}
		}
		else if(evt.getSource() == sendButton)
		{	
			if(!isValidDate(fromText.getText()) || !isValidDate(untilText.getText()))
			{
				JOptionPane.showConfirmDialog(null, "please fill a correct dates","",JOptionPane.PLAIN_MESSAGE);
				return;
			}
			
			String email = db.getEmail(nickNametext.getText() , new String(passwordField.getPassword()));
			DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yy");	
			Date fromDate;
			Date untilDate;
			try {
				fromDate = sourceFormat.parse(fromText.getText());
				untilDate = sourceFormat.parse(untilText.getText());
			} catch (ParseException e) {
				e.printStackTrace();
				return;
			}
			
			if(fromDate.after(untilDate))
			{
				JOptionPane.showConfirmDialog(null, "Until Date have to be equal to or larger than From Date","",JOptionPane.PLAIN_MESSAGE);
				fromText.setText("");
				untilText.setText("");
				fromText.requestFocusInWindow();
				return;
			}
			
			Calendar c = Calendar.getInstance();
			
			c.setTime(fromDate);
			java.sql.Date fromDateInSqlFormat = new java.sql.Date(c.getTimeInMillis());
			
			c.setTime(untilDate);
			java.sql.Date untilDateInSqlFormat = new java.sql.Date(c.getTimeInMillis());
			
			List<Activity> uses = db.getUses(fromDateInSqlFormat, untilDateInSqlFormat);
			
			if(uses.size() == 0)
			{
				JOptionPane.showConfirmDialog(null, "There is no activity between these dates","",JOptionPane.PLAIN_MESSAGE);
				fromText.setText("");
				untilText.setText("");
				fromText.requestFocusInWindow();
				return;
			}
			
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			Authenticator auth = new SocialAuth("AL-NT/"+email.split("@")[0],new String(passwordField.getPassword()));
			Sender<List<String>> sender = new ActivitySender(email, uses, auth);
			sender.send();
			frame.setCursor(Cursor.getDefaultCursor());
			fromText.setText("");
			untilText.setText("");
			fromText.requestFocusInWindow();
			JOptionPane.showConfirmDialog(null, "Done","",JOptionPane.PLAIN_MESSAGE);
		}
		else if(evt.getSource() == addProjectButton)
		{
			projectNameLabel.setVisible(true);
			projectNameText.setVisible(true);
			confirmProjectNameButton.setVisible(true);
			projectNameText.requestFocusInWindow();
		}
		else if(evt.getSource() == deleteProjectButton)
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
			confirmProjectNameButton.setVisible(false);
		}
		else if(evt.getSource() == confirmProjectNameButton)
		{
			DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) projectsComboBox.getModel();
			if(model.getIndexOf(projectNameText.getText()) != -1)
				JOptionPane.showConfirmDialog(null, "project already exists","",JOptionPane.PLAIN_MESSAGE);
			else
			{
				if(projectNameText.getText().trim().equals(""))
				{
					projectNameLabel.setVisible(false);
					projectNameText.setVisible(false);
					confirmProjectNameButton.setVisible(false);
					return;
				}
				
				if(db.addProject(projectNameText.getText()))
				{
					model.addElement(projectNameText.getText());
					JOptionPane.showConfirmDialog(null, "Success","",JOptionPane.PLAIN_MESSAGE);
				}
				else
					JOptionPane.showConfirmDialog(null, "Fail","",JOptionPane.PLAIN_MESSAGE);
			}
				
			projectNameLabel.setVisible(false);
			projectNameText.setVisible(false);
			confirmProjectNameButton.setVisible(false);
			
			projectNameText.setText("");
			
		}else if(evt.getSource() == updatePassword)
		{
			if(!db.checkConnectPermission(nickNametext.getText(), new String(passwordField.getPassword())))
			{
				JOptionPane.showConfirmDialog(null, "you don't have a permission","",JOptionPane.PLAIN_MESSAGE);
				return;
			}
			
			 new UpdatePasswordFrame(nickNametext.getText() , globals , (objects)->{
				String nickName = nickNametext.getText();
				passwordField.setText("");
				updateViews();
				nickNametext.setText(nickName);
				return 1;
				
			});
		}
		else if(evt.getSource() == mrpRadioButton)
		{
			if(!db.checkConnectPermission(nickNametext.getText(), new String(passwordField.getPassword())))
			{
				JOptionPane.showConfirmDialog(null, "you don't have a permission","",JOptionPane.PLAIN_MESSAGE);
				mrpRadioButton.setSelected(false);
				return;
			}
			
			if(mrpSimRadioButton.isSelected())
			{
				mrpRadioButton.setSelected(false);
				JOptionPane.showConfirmDialog(null, "Can't select both types of MRP ","",JOptionPane.PLAIN_MESSAGE);
				return;
			}
				
		}
		else if(evt.getSource() == mrpSimRadioButton)
		{
			if(!db.checkConnectPermission(nickNametext.getText(), new String(passwordField.getPassword())))
			{
				JOptionPane.showConfirmDialog(null, "you don't have a permission","",JOptionPane.PLAIN_MESSAGE);
				mrpSimRadioButton.setSelected(false);
				return;
			}
			
			if(mrpRadioButton.isSelected())
			{
				mrpSimRadioButton.setSelected(false);
				JOptionPane.showConfirmDialog(null, "Can't select both types of MRP ","",JOptionPane.PLAIN_MESSAGE);
				return;
			}
			
			if(mrpSimRadioButton.isSelected())
			{
				bomsQuantityLabel.setVisible(true);
				bomsQuantityText.setVisible(true);
			}
			else
			{
				bomsQuantityLabel.setVisible(false);
				bomsQuantityText.setVisible(false);
				bomsQuantityText.setText("");
			}
				
		}
		else if(evt.getSource() == projectsComboBox)
		{
			DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) projectsComboBox.getModel();		
			expediteDirectoryPath.setText(db.getDirectory((String) model.getSelectedItem()));
		}
		
		
	}
	
	
	private boolean isValidDate(String date) 
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setLenient(false);
		
		try {
			dateFormat.parse(date.trim());
		} catch (ParseException e) {
			return false;
		}
		
		return true;
	}
	
	private Date getDate(String dateString)
	{
		SimpleDateFormat sourceFormat = new SimpleDateFormat("dd/MM/yy");
		SimpleDateFormat outsourceFormat = new SimpleDateFormat("dd/MM/yyyy");
		outsourceFormat.setLenient(false);
		Date date;
		
		try {
			date = sourceFormat.parse(dateString);
			String toExp = outsourceFormat.format(date);
			date = outsourceFormat.parse(toExp);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			date = c.getTime();
		} catch (ParseException e) {
			return null;
		}
		
		return date;
	}
	
	private void close() 
	{
		db.closeConnection();
		this.frame.dispose();
		//System.exit(0);
		this.callBack.execute();
	}


	/*public static void main(String[] args) 
	{
		/*EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");					
					new Menu();
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		});
		Analyzer analyzer = new Analyzer();
		Map<MonthDate, Map<String, ProductColumn>> map = analyzer.calculateMap();
		System.out.println(map);
	}*/
	
	private void updateTemplateText()
	{
		int id = globals.getDatesId(acceptOrder.isSelected() , noDate.isSelected(), pastDate.isSelected(), futureDate.isSelected() 
				, sendExpediteDateOrders.isSelected() , beyondRequestDate.isSelected());
		String [] subjectAndBody = db.getSubjectAndBody(id);
		
		subjectTemplate.setText(subjectAndBody[0]);
		bodyTemplate.setText(subjectAndBody[1]);
		
		if(id == 0)
		{
			subjectTemplate.setVisible(false);
			bodyTemplate.setVisible(false);
		}
		else
		{
			subjectTemplate.setVisible(true);
			bodyTemplate.setVisible(true);
		}
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
	public void changedUpdate(DocumentEvent e) {
		if(e.getDocument() == nickNametext.getDocument() || e.getDocument() == passwordField.getDocument())
		{
			updateViews();
		}
		
	}


	@Override
	public void insertUpdate(DocumentEvent e) {
		if(e.getDocument() == nickNametext.getDocument() || e.getDocument() == passwordField.getDocument())
		{
			updateViews();
		}
		
	}


	@Override
	public void removeUpdate(DocumentEvent e) {
		if(e.getDocument() == nickNametext.getDocument() || e.getDocument() == passwordField.getDocument())
		{
			updateViews();
		}
		
	}
	
	private void updateViews()
	{
		boolean adminPermission = db.checkAddOrDeletePermission(nickNametext.getText(), new String(passwordField.getPassword()));
		boolean purchasingPermission = db.checkPurchasingPermission(nickNametext.getText(), new String(passwordField.getPassword()));
		
		if(adminPermission)
		{
			usesRadioButton.setVisible(true);
			
			projectsLabel.setVisible(true);
			projectsComboBox.setVisible(true);
			addProjectButton.setVisible(true);
			deleteProjectButton.setVisible(true);
			expediteDirectoryButton.setVisible(true);
			expediteDirectoryPath.setVisible(true);
			
			List<String> projects = db.getAllProjects();
			DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) projectsComboBox.getModel();
			model.removeAllElements();
			for (String project : projects) 
			{
				model.addElement(project);
			}
			
			expediteDirectoryPath.setText(db.getDirectory((String) model.getSelectedItem()));
			
			followUpDirectoryButton.setVisible(true);
			followUpDirectoryPath.setVisible(true);
			followUpDirectoryPath.setText(db.getFollowUpDirectory());
			
		}
		else
		{
			usesRadioButton.setSelected(false);
			usesRadioButton.setVisible(false);
			fromLabel.setVisible(false);
			fromText.setVisible(false);
			untilLabel.setVisible(false);
			untilText.setVisible(false);
			sendButton.setVisible(false);
			
			fromText.setText("");
			untilText.setText("");
			
			projectsLabel.setVisible(false);
			projectsComboBox.setVisible(false);
			addProjectButton.setVisible(false);
			deleteProjectButton.setVisible(false);
			projectNameLabel.setVisible(false);
			projectNameText.setVisible(false);
			confirmProjectNameButton.setVisible(false);
			expediteDirectoryButton.setVisible(false);
			expediteDirectoryPath.setVisible(false);
			followUpDirectoryPath.setVisible(false);
			followUpDirectoryButton.setVisible(false);
			projectNameText.setText("");
		}
		
		if(adminPermission || purchasingPermission)
		{
			DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) emailCC.getModel();
			model.removeAllElements();
			for (String cc : db.getAllCC()) {
				model.addElement(cc);
			}
			model.setSelectedItem(null);
			((MultiSelectionComboBox<String>) emailCC).removeAllSelectedItem();
		}
		else
		{
			DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) emailCC.getModel();
			model.removeAllElements();
		}
		
		scrollPane.setVisible(false);
		supplierEmailsFile = null;
		filePath.setText("");
		acceptOrder.setSelected(false);
		pastDate.setSelected(false);
		noDate.setSelected(false);
		futureDate.setSelected(false);
		beyondRequestDate.setSelected(false);
		expediteDate.setSelected(false);
		sendExpediteDateOrders.setSelected(false);
		updateTemplateText();
		untilDateLabel.setVisible(false);
		dateLimited.setText("");
		dateLimited.setVisible(false);
		
		daysLabel.setVisible(false);
		daysText.setText("");
		daysText.setVisible(false);
		
		okButton.setVisible(false);
		ccEmailLabel.setVisible(false);
		ccEmailtext.setVisible(false);
		
		expediteDateComboBox.setVisible(false);
		addDateButton.setVisible(false);
		deleteDateButton.setVisible(false);	
		dateLabel.setVisible(false);
		expediteDateText.setVisible(false);
		confirmDateButton.setVisible(false);
		DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) expediteDateComboBox.getModel();
		model.removeAllElements();
		
		mrpRadioButton.setSelected(false);
		mrpSimRadioButton.setSelected(false);
		
		bomsQuantityLabel.setVisible(false);
		bomsQuantityText.setVisible(false);
		bomsQuantityText.setText("");
		
		clickedTimes = 0;
		directoryPath = null;
		
	}

	
	
	
	
	
}
