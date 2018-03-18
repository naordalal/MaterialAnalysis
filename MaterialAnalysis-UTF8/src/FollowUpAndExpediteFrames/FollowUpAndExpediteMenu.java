package FollowUpAndExpediteFrames;
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
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import Components.MultiSelectionComboBox;
import MainPackage.Activity;
import MainPackage.CallBack;
import MainPackage.DataBase;
import MainPackage.Globals;
import MainPackage.SocialAuth;
import Senders.ActivitySender;
import Senders.Sender;

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


public class FollowUpAndExpediteMenu implements ActionListener{

	private JLabel suppliersFileLabel;
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
	private JComboBox<String> expediteDateComboBox;
	private JButton addDateButton;
	private JButton deleteDateButton;
	private JLabel dateLabel;
	private JTextField expediteDateText;
	private JButton confirmDateButton;
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
	private JRadioButton mrpRadioButton;
	private JRadioButton mrpSimRadioButton;
	private JLabel bomsQuantityLabel;
	private JTextField bomsQuantityText;
	private JLabel expediteDirectoryPath;
	private JLabel followUpDirectoryPath;
	private JLabel daysLabel;
	private JTextField daysText;
	private CallBack<Integer> callBack;
	private String userName;
	private String password;
	private JLabel followUpDirectoryLabel;
	
	
	public FollowUpAndExpediteMenu(String userName, String password, CallBack<Integer> callBack) 
	{
		this.callBack = callBack;
		this.userName = userName;
		this.password = password;
		initialize();
	}
	
	
	private void initialize() 
	{
		
		directoryPath = null;
		db = new DataBase();
		
		globals = new Globals();

		boolean adminPermission = db.checkAddOrDeletePermission(userName , password);
		boolean purchasingPermission = db.checkPurchasingPermission(userName , password);
		
		frame = new JFrame("Gathering material analysis system");
		frame.setLayout(null);
		frame.getRootPane().setFocusable(true);
		frame.setBounds(300, 50, 900, 780);
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
		
		JLabel cc = new JLabel("CC:");
		cc.setLocation(610, 70);
		cc.setSize(30,20);
		panel.add(cc);
		
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
		emailCC = new MultiSelectionComboBox<String>(model);
		emailCC.setLocation(650, 70);
		emailCC.setSize(100,20);
		panel.add(emailCC);
		
		if(adminPermission || purchasingPermission)
		{
			model.removeAllElements();
			for (String ccEmail : db.getAllCC()) 
			{
				model.addElement(ccEmail);
			}
			model.setSelectedItem(null);
			((MultiSelectionComboBox<String>) emailCC).removeAllSelectedItem();
		}
		else
		{
			model.removeAllElements();
		}

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
		
		
		suppliersFileLabel = new JLabel("<html><u>Attach suppliers Email file:</u></html>");
		suppliersFileLabel.setLocation(30,20);
		suppliersFileLabel.setSize(150,100);
		panel.add(suppliersFileLabel);
		
		suplierFileButton = new JButton();
		suplierFileButton.setLocation(160 ,  45);
		suplierFileButton.setSize(100, 40);
		suplierFileButton.setIcon(globals.attachIcon);
		suplierFileButton.setFocusable(false);
		suplierFileButton.setContentAreaFilled(false);
		suplierFileButton.setPressedIcon(globals.clickAttachIcon);
		suplierFileButton.addActionListener(this);
		suplierFileButton.setToolTipText("attachment");
		panel.add(suplierFileButton);
		
		filePath = new JLabel("");
		filePath.setLocation(250 , 60);
		filePath.setSize(300, 20);
		panel.add(filePath);
		
		followUpDirectoryLabel = new JLabel("<html><u>FollowUp Directory:</u></html>");
		followUpDirectoryLabel.setLocation(30 , 120);
		followUpDirectoryLabel.setSize(150, 20);
		panel.add(followUpDirectoryLabel);
		
		followUpDirectoryButton = new JButton();
		followUpDirectoryButton.setLocation(160 ,  110);
		followUpDirectoryButton.setSize(55, 40);
		followUpDirectoryButton.setIcon(globals.directoryIcon);
		followUpDirectoryButton.setFocusable(false);
		followUpDirectoryButton.setContentAreaFilled(false);
		followUpDirectoryButton.setPressedIcon(globals.clickDirectoryIcon);
		followUpDirectoryButton.addActionListener(this);
		followUpDirectoryButton.setToolTipText("Choose FollowUp Directory");
		followUpDirectoryButton.setVisible(adminPermission);
		panel.add(followUpDirectoryButton);
		
		followUpDirectoryPath = new JLabel("");
		followUpDirectoryPath.setLocation(255, 125);
		followUpDirectoryPath.setSize(250, 20);
		followUpDirectoryPath.setText(db.getFollowUpDirectory());
		followUpDirectoryPath.setVisible(adminPermission);
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
		acceptOrder.setLocation(30, 200);
		acceptOrder.setSize(120,20);
		acceptOrder.addActionListener(this);
		acceptOrder.setBackground(null);
		panel.add(acceptOrder);
		
		
		
		noDate = new JRadioButton("Without Due Date");
		noDate.setLocation(30, 230);
		noDate.setSize(120,20);
		noDate.addActionListener(this);
		noDate.setBackground(null);
		panel.add(noDate);
		
		pastDate = new JRadioButton("Past Due Date");
		pastDate.setLocation(30, 260);
		pastDate.setSize(100,20);
		pastDate.addActionListener(this);
		pastDate.setBackground(null);
		panel.add(pastDate);
		
		futureDate = new JRadioButton("Supply On Time");
		futureDate.setLocation(30, 290);
		futureDate.setSize(100,20);
		futureDate.addActionListener(this);
		futureDate.setBackground(null);
		panel.add(futureDate);
		
		untilDateLabel = new JLabel("Until:");
		untilDateLabel.setLocation(160 , 290);
		untilDateLabel.setSize(40,20);
		untilDateLabel.setVisible(false);
		panel.add(untilDateLabel);
		
		dateLimited = new JTextField();
		dateLimited.setLocation(210, 290);
		dateLimited.setSize(100 , 20);
		dateLimited.setVisible(false);
		panel.add(dateLimited);
		
		beyondRequestDate = new JRadioButton("Orders Beyond Request Date");
		beyondRequestDate.setLocation(30, 320);
		beyondRequestDate.setSize(170,20);
		beyondRequestDate.addActionListener(this);
		beyondRequestDate.setBackground(null);
		panel.add(beyondRequestDate);
		
		daysLabel = new JLabel("Days:");
		daysLabel.setLocation(230 , 320);
		daysLabel.setSize(40,20);
		daysLabel.setVisible(false);
		panel.add(daysLabel);
		
		daysText = new JTextField();
		daysText.setLocation(270, 320);
		daysText.setSize(50 , 20);
		daysText.setVisible(false);
		panel.add(daysText);
		
		addBodyButton = new JButton();
		addBodyButton.setLocation(700, 200);
		addBodyButton.setSize(50 , 50);
		addBodyButton.addActionListener(this);
		addBodyButton.setIcon(globals.addIcon);
		addBodyButton.setFocusable(false);
		addBodyButton.setContentAreaFilled(false);
		addBodyButton.setPressedIcon(globals.clickAddIcon);
		addBodyButton.setToolTipText("add subject and body for these dates");
		panel.add(addBodyButton);
		
		deleteBodyButton = new JButton();
		deleteBodyButton.setLocation(700, 290);
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
		separator1.setLocation(350, 200);
		separator1.setSize(10, 140);
		panel.add(separator1);
		
		JSeparator separator2 = new JSeparator(SwingConstants.VERTICAL);
		separator2.setBackground(Color.black);
		separator2.setLocation(650, 200);
		separator2.setSize(10, 140);
		panel.add(separator2);
		
		
		mailTemplateLabel = new JLabel("<html><u>Follow up order report</u></html>");
		mailTemplateLabel.setLocation(30, 170);
		mailTemplateLabel.setSize(180, 30);
		panel.add(mailTemplateLabel);
		
		addAndDeleteLabel = new JLabel("<html><u>Add or Delete mail template</u></html>");
		addAndDeleteLabel.setLocation(660, 170);
		addAndDeleteLabel.setSize(150, 30);
		panel.add(addAndDeleteLabel);
		
		templateLabel = new JLabel("<html><u>View subject and body of mail</u></html>");
		templateLabel.setLocation(360, 170);
		templateLabel.setSize(300, 30);
		panel.add(templateLabel);

		addLabel = new JLabel("Add:");
		addLabel.setLocation(660, 210);
		addLabel.setSize(100, 30);
		panel.add(addLabel);
		
		deleteLabel = new JLabel("Delete:");
		deleteLabel.setLocation(660, 300);
		deleteLabel.setSize(100, 30);
		panel.add(deleteLabel);
		
		subjectTemplate = new JTextPane();
		subjectTemplate.setLocation(360 , 200);
		subjectTemplate.setSize(280 , 20);
		subjectTemplate.setEditable(false);
		subjectTemplate.setVisible(false);
		panel.add(subjectTemplate);
		
		bodyTemplate = new JTextPane();
		bodyTemplate.setLocation(360 , 240);
		bodyTemplate.setSize(280 , 100);
		bodyTemplate.setEditable(false);
		bodyTemplate.setVisible(false);
		panel.add(bodyTemplate);
		
		copyRight = new JLabel("<html><b>\u00a9 Naor Dalal</b></html>");
		copyRight.setLocation(30 , 710);
		copyRight.setSize(100,30);
		panel.add(copyRight);
		
		expediteOrdersLabel = new JLabel("<html><u>Convergence analysis of the control material</u></html>");
		expediteOrdersLabel.setLocation(30, 350);
		expediteOrdersLabel.setSize(250, 50);
		panel.add(expediteOrdersLabel);
		
		mrpSimRadioButton = new JRadioButton("MRP-SIM");
		mrpSimRadioButton.setLocation(30 , 390);
		mrpSimRadioButton.setSize(80, 20);
		mrpSimRadioButton.addActionListener(this);
		panel.add(mrpSimRadioButton);
		
		mrpRadioButton = new JRadioButton("SIM");
		mrpRadioButton.setLocation(115 , 390);
		mrpRadioButton.setSize(50, 20);
		mrpRadioButton.addActionListener(this);
		panel.add(mrpRadioButton);
		
		bomsQuantityLabel = new JLabel("Boms quantity:");
		bomsQuantityLabel.setLocation(40 ,  415);
		bomsQuantityLabel.setSize(80,20);
		bomsQuantityLabel.setVisible(false);
		panel.add(bomsQuantityLabel);
		
		bomsQuantityText = new JTextField();
		bomsQuantityText.setLocation(120 ,  415);
		bomsQuantityText.setSize(60,20);
		bomsQuantityText.setVisible(false);
		panel.add(bomsQuantityText);
					
		expediteDate = new JRadioButton("Import expedite orders report");
		expediteDate.setLocation(30 , 445);
		expediteDate.setSize(170, 20);
		expediteDate.addActionListener(this);
		panel.add(expediteDate);
		
		sendExpediteDateOrders = new JRadioButton("Export expedite orders reports");
		sendExpediteDateOrders.setLocation(30 , 480);
		sendExpediteDateOrders.setSize(180, 20);
		sendExpediteDateOrders.addActionListener(this);
		panel.add(sendExpediteDateOrders);	
		
		/*expediteDate = new JRadioButton("Expedite orders");
		expediteDate.setLocation(30 , 450);
		expediteDate.setSize(120, 20);
		expediteDate.addActionListener(this);
		panel.add(expediteDate);*/
		
		DefaultComboBoxModel<String> model3 = new DefaultComboBoxModel<String>();
		expediteDateComboBox = new JComboBox<String>(model3);
		expediteDateComboBox.setLocation(220, 445);
		expediteDateComboBox.setSize(100,20);
		expediteDateComboBox.setVisible(false);
		panel.add(expediteDateComboBox);

		addDateButton = new JButton();
		addDateButton.setLocation(350, 429);
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
		deleteDateButton.setLocation(400, 430);
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
		dateLabel.setLocation(480, 440);
		dateLabel.setSize(30,30);
		dateLabel.setVisible(false);
		panel.add(dateLabel);
		
		expediteDateText = new JTextField();
		expediteDateText.setLocation(520, 445);
		expediteDateText.setSize(150, 20);
		expediteDateText.setVisible(false);
		panel.add(expediteDateText);
		
		confirmDateButton = new JButton();
		confirmDateButton.setLocation(670, 430);
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
		usesRadioButton.setLocation(30, 550);
		usesRadioButton.setSize(135,20);
		usesRadioButton.addActionListener(this);
		usesRadioButton.setBackground(null);
		usesRadioButton.setVisible(adminPermission);
		panel.add(usesRadioButton);
		
		fromLabel = new JLabel("From:");
		fromLabel.setLocation(170, 545);
		fromLabel.setSize(30,30);
		fromLabel.setVisible(false);
		panel.add(fromLabel);
		
		
		fromText = new JTextField();
		fromText.setLocation(210, 550);
		fromText.setSize(100 , 20);
		fromText.setVisible(false);
		panel.add(fromText);
		
		
		untilLabel = new JLabel("Until:");
		untilLabel.setLocation(320, 545);
		untilLabel.setSize(30,30);
		untilLabel.setVisible(false);
		panel.add(untilLabel);
		
		untilText = new JTextField();
		untilText.setLocation(360, 550);
		untilText.setSize(100 , 20);
		untilText.setVisible(false);
		panel.add(untilText);
		
		sendButton = new JButton();
		sendButton.setLocation(475 , 537);
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
		projectsLabel.setLocation(30, 590);
		projectsLabel.setSize(50, 20);
		projectsLabel.setVisible(adminPermission);
		panel.add(projectsLabel);
		
		DefaultComboBoxModel<String> model4 = new DefaultComboBoxModel<String>();
		List<String> projects = db.getAllProjects();
		for (String project : projects) 
		{
			model4.addElement(project);
		}
		projectsComboBox = new JComboBox<String>(model4);
		projectsComboBox.setLocation(90, 590);
		projectsComboBox.setSize(130,20);
		projectsComboBox.setVisible(adminPermission);
		projectsComboBox.addActionListener(this);
		panel.add(projectsComboBox);
		
		
		
		expediteDirectoryButton = new JButton();
		expediteDirectoryButton.setLocation(230 , 580);
		expediteDirectoryButton.setSize(55, 40);
		expediteDirectoryButton.setIcon(globals.directoryIcon);
		expediteDirectoryButton.setFocusable(false);
		expediteDirectoryButton.setContentAreaFilled(false);
		expediteDirectoryButton.setPressedIcon(globals.clickDirectoryIcon);
		expediteDirectoryButton.addActionListener(this);
		expediteDirectoryButton.setToolTipText("Choose Directory");
		expediteDirectoryButton.setVisible(adminPermission);
		panel.add(expediteDirectoryButton);
		
		expediteDirectoryPath = new JLabel("");
		expediteDirectoryPath.setLocation(200, 630);
		expediteDirectoryPath.setSize(250, 20);
		expediteDirectoryPath.setText(db.getDirectory((String) projectsComboBox.getModel().getSelectedItem()));
		expediteDirectoryPath.setVisible(adminPermission);
		panel.add(expediteDirectoryPath);
		
		addProjectButton = new JButton();
		addProjectButton.setLocation(300 , 580);
		addProjectButton.setSize(40 , 40);
		addProjectButton.addActionListener(this);
		addProjectButton.setIcon(globals.addIcon);
		addProjectButton.setFocusable(false);
		addProjectButton.setContentAreaFilled(false);
		addProjectButton.setPressedIcon(globals.clickAddIcon);
		addProjectButton.setToolTipText("add project");
		addProjectButton.setVisible(adminPermission);
		panel.add(addProjectButton);
		
		deleteProjectButton = new JButton();
		deleteProjectButton.setLocation(360 , 580);
		deleteProjectButton.setSize(40 , 40);
		deleteProjectButton.addActionListener(this);
		deleteProjectButton.setIcon(globals.deleteIcon);
		deleteProjectButton.setFocusable(false);
		deleteProjectButton.setContentAreaFilled(false);
		deleteProjectButton.setPressedIcon(globals.clickDeleteIcon);
		deleteProjectButton.setToolTipText("delete project");
		deleteProjectButton.setVisible(adminPermission);
		panel.add(deleteProjectButton);
		
		projectNameLabel = new JLabel("Project name:");
		projectNameLabel.setLocation(430, 590);
		projectNameLabel.setSize(70, 20);
		projectNameLabel.setVisible(false);
		panel.add(projectNameLabel);
		
		projectNameText = new JTextField();
		projectNameText.setLocation(510, 590);
		projectNameText.setSize(100 , 20);
		projectNameText.setVisible(false);
		panel.add(projectNameText);
		
		
		confirmProjectNameButton = new JButton();
		confirmProjectNameButton.setLocation(640 , 575);
		confirmProjectNameButton.setSize(40 , 40);
		confirmProjectNameButton.addActionListener(this);
		confirmProjectNameButton.setIcon(globals.okIcon);
		confirmProjectNameButton.setFocusable(false);
		confirmProjectNameButton.setContentAreaFilled(false);
		confirmProjectNameButton.setPressedIcon(globals.clickOkIcon);
		confirmProjectNameButton.setToolTipText("confirm");
		confirmProjectNameButton.setVisible(false);
		panel.add(confirmProjectNameButton); 
		
		frame.setVisible(true);
	}


	@Override
	public void actionPerformed(ActionEvent evt) {
		if(evt.getSource() == suplierFileButton)
		{
			if(!db.checkPurchasingPermission(userName, password))
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
			if(!db.checkAddOrDeletePermission(userName, password))
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
			if(!db.checkAddOrDeletePermission(userName, password))
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
			if(!db.checkPurchasingPermission(userName, password))
			{
				if(!expediteDate.isSelected())
				{
					JOptionPane.showConfirmDialog(null, "please fill all the details","",JOptionPane.PLAIN_MESSAGE);
					return;
				}
			}
			else if((!acceptOrder.isSelected() && !noDate.isSelected() && !pastDate.isSelected() && !futureDate.isSelected() && !beyondRequestDate.isSelected()
							&& !expediteDate.isSelected() && !sendExpediteDateOrders.isSelected()))
				{
					JOptionPane.showConfirmDialog(null, "please fill all the details","",JOptionPane.PLAIN_MESSAGE);
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
			String email = db.getEmail(userName , password);
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
			
			Activity activity = new Activity(userName, followUp, acceptOrder.isSelected()
					, noDate.isSelected(), pastDate.isSelected(), futureDate.isSelected(), beyondRequestDate.isSelected() , !followUp, expediteDate.isSelected()
					, sendExpediteDateOrders.isSelected(), project, null);
			
			SenderFrame senderFrame = new SenderFrame(email , password , supplierEmailsFile , activity , datesList , id 
					, db.checkPurchasingPermission(userName, password), mrpSim , bomsQuantity);
						
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
				if(!db.checkPurchasingPermission(userName, password))
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
				if(!db.checkPurchasingPermission(userName, password))
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
				if(!db.checkPurchasingPermission(userName , password))
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
				if(!db.checkPurchasingPermission(userName, password))
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
				if(!db.checkPurchasingPermission(userName, password))
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
		else if(evt.getSource() == addBodyButton)
		{
			if((!acceptOrder.isSelected() && !noDate.isSelected() && !pastDate.isSelected() && !futureDate.isSelected() && !beyondRequestDate.isSelected()
							&& !sendExpediteDateOrders.isSelected()))
			{
				JOptionPane.showConfirmDialog(null, "please choose dates and fill email and password","",JOptionPane.PLAIN_MESSAGE);
			}
			else 
			{
				if(db.checkAddOrDeletePermission(userName , password))
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
			if((!acceptOrder.isSelected() && !noDate.isSelected() && !pastDate.isSelected() && !futureDate.isSelected() && !beyondRequestDate.isSelected()
							&& !sendExpediteDateOrders.isSelected()))
			{
				JOptionPane.showConfirmDialog(null, "please choose dates and fill email and password","",JOptionPane.PLAIN_MESSAGE);
			}
			else 
			{
				if(db.checkAddOrDeletePermission(userName , password))
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
			if(!db.checkPurchasingPermission(userName , password))
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
			if(!db.checkPurchasingPermission(userName , password))
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
		else if(evt.getSource() == usesRadioButton)
		{
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
			
			String email = db.getEmail(userName , password);
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
			Authenticator auth = new SocialAuth("AL-NT/"+email.split("@")[0],password);
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
			
		}
		else if(evt.getSource() == mrpRadioButton)
		{
			if(mrpSimRadioButton.isSelected())
			{
				mrpRadioButton.setSelected(false);
				JOptionPane.showConfirmDialog(null, "Can't select both types of MRP ","",JOptionPane.PLAIN_MESSAGE);
				return;
			}
			
			if(!mrpRadioButton.isSelected())
			{
				sendExpediteDateOrders.setSelected(false);
				expediteDate.setSelected(false);
				expediteDateComboBox.setVisible(false);
				expediteDateComboBox.removeAllItems();
				addDateButton.setVisible(false);
				deleteDateButton.setVisible(false);
				dateLabel.setVisible(false);
				expediteDateText.setVisible(false);
				confirmDateButton.setVisible(false);		
				
				updateTemplateText();
			}
			
		}
		else if(evt.getSource() == mrpSimRadioButton)
		{		
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
			
				sendExpediteDateOrders.setSelected(false);
				expediteDate.setSelected(false);
				expediteDateComboBox.setVisible(false);
				expediteDateComboBox.removeAllItems();
				addDateButton.setVisible(false);
				deleteDateButton.setVisible(false);
				dateLabel.setVisible(false);
				expediteDateText.setVisible(false);
				confirmDateButton.setVisible(false);
				
				updateTemplateText();
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
	
	
	
}
