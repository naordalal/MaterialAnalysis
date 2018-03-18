package FollowUpAndExpediteFrames;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.mail.Authenticator;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Font;

import MainPackage.*;
import Senders.ExpediteOrdersSender;
import Senders.FollowUpSender;
import Senders.Sender;
import Senders.SimMrpSender;
import Senders.SimSender;

public class SenderFrame extends MouseAdapter implements ActionListener{

	private JFrame frame;
	private JPanel panel;
	private JLabel suppliersFileLabel;
	private JFileChooser suppliersFileChooser;
	private JButton suplierFileButton;
	private File supplierEmailsFile;
	private File supplierOrdersFile;
	private JLabel filePath;
	private JLabel subjectLabel;
	private JTextField subjectText;
	private JLabel bodyLabel;
	private JTextArea bodyText;
	private JButton send;
	private String from;
	private String password;
	private boolean acceptOrder;
	private boolean noDate;
	private boolean passDate;
	private boolean futureDate;
	private List<String> untilDate;
	private Globals globals;
	private int datesId;
	private JLabel copyRight;
	private boolean expediteDate;
	private List<String> ccList;
	private boolean purchasingPermission;
	private List<File> suppliersOrdersFiles;
	private String directoryPath;
	private boolean sendExpeditesOrders;
	private String nickName;
	private DataBase db;
	private String project;
	private JPopupMenu popMenu;
	private JMenuItem copyItem;
	private JMenuItem pasteItem;
	private JMenuItem cutItem;
	private boolean simMrp;
	private int clickSend;
	private int bomsQuantity;
	private boolean beyondRequestDate;
	private int daysBeyondRequestDate;
		
	public SenderFrame(String email, String password, File supplierEmailsFile, Activity activity, List<String> datesList,
			int datesId, boolean purchasingPermission, boolean mrpSim , int bomsQuantity) 
	{
		this.supplierEmailsFile = supplierEmailsFile;
		this.from = email;
		this.password = password;
		this.acceptOrder = activity.isAcceptOrder();
		this.noDate = activity.isWithoutDueDate();
		this.passDate = activity.isPastDueDate();
		this.futureDate = activity.isSupplyOnTime();
		this.beyondRequestDate = activity.isBeyondRequestDate();
		this.untilDate = datesList;
		this.datesId = datesId;
		this.expediteDate = activity.isImportExpediteReport();
		this.purchasingPermission = purchasingPermission;
		this.suppliersOrdersFiles = new ArrayList<File>();
		this.directoryPath = null;
		this.sendExpeditesOrders = activity.isExportExpediteReport();
		this.nickName = activity.getName();
		this.simMrp = mrpSim;
		this.bomsQuantity = bomsQuantity;
		this.clickSend = 0;
	}


	public void initialize() {
		
		globals = new Globals();
		
		frame = new JFrame("Gathering material analysis system");
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
		
		
		suppliersFileLabel = new JLabel();
		suppliersFileLabel = new JLabel("suppliers Orders File:");
		suppliersFileLabel.setLocation(30,5);
		suppliersFileLabel.setSize(150,100);
		panel.add(suppliersFileLabel);
		
		suplierFileButton = new JButton();
		suplierFileButton.setLocation(170 ,  30);
		suplierFileButton.setSize(100, 40);
		suplierFileButton.setIcon(globals.attachIcon);
		suplierFileButton.setFocusable(false);
		suplierFileButton.setContentAreaFilled(false);
		suplierFileButton.setPressedIcon(globals.clickAttachIcon);
		suplierFileButton.addActionListener(this);
		suplierFileButton.setToolTipText("attachment");
		suplierFileButton.addMouseListener(this);
		panel.add(suplierFileButton);
		
		
		filePath = new JLabel();
		filePath.setLocation(150 , 75);
		filePath.setSize(250, 20);
		panel.add(filePath);
		
		subjectLabel = new JLabel("subject:");
		subjectLabel.setLocation(30 ,  60);
		subjectLabel.setSize(60 ,  100);
		subjectLabel.setVisible(!expediteDate);
		panel.add(subjectLabel);
		
		String[] bodyAndSubject = FollowUpAndExpediteMenu.db.getSubjectAndBody(datesId);
		
		subjectText = new JTextField(bodyAndSubject[0]);
		subjectText.setLocation(100 ,  100);
		subjectText.setSize(300 ,  30);
		subjectText.setVisible(!expediteDate);
		globals.initTextComponent(subjectText);
		subjectText.addMouseListener(this);
		panel.add(subjectText);
			
		bodyLabel = new JLabel("body:");
		bodyLabel.setLocation(30 ,  130);
		bodyLabel.setSize(40 ,  100);
		bodyLabel.setVisible(!expediteDate);
		panel.add(bodyLabel);
		
		String signature = FollowUpAndExpediteMenu.db.getSignature(nickName);
		String body = bodyAndSubject[1];
		
		bodyText = new JTextArea(body+"\n\n"+signature);
		bodyText.setLocation(80 ,  160);
		bodyText.setSize(450 ,  250);
		bodyText.setFont(new java.awt.Font("Ariel", Font.ANSI_CHARSET , 14));
		globals.initTextComponent(bodyText);
		bodyText.setVisible(!expediteDate);
		bodyText.addMouseListener(this);
		panel.add(bodyText);
		
		

		send = new JButton();
		send.setLocation(470 , 420);
		send.setSize(70,40);
		send.setVisible(true);
		send.addActionListener(this);
		send.setIcon(globals.sendIcon);
		send.setFocusable(false);
		send.setContentAreaFilled(false);
		send.setPressedIcon(globals.clickSendIcon);
		send.setToolTipText("send");
		send.addMouseListener(this);
		panel.add(send);
		
		copyRight = new JLabel("<html><b>\u00a9 Naor Dalal</b></html>");
		copyRight.setLocation(30 , 440);
		copyRight.setSize(100,30);
		panel.add(copyRight);
		
		
		popMenu = new JPopupMenu();
		
		cutItem = new JMenuItem("Cut");
		cutItem.setHorizontalTextPosition(JMenuItem.CENTER);
		cutItem.addActionListener(this);
		popMenu.add(cutItem);
		
		popMenu.addSeparator();
		
		copyItem = new JMenuItem("Copy");
		copyItem.setHorizontalTextPosition(JMenuItem.CENTER);
		copyItem.addActionListener(this);
		popMenu.add(copyItem);
		
		popMenu.addSeparator();
		
		pasteItem = new JMenuItem("Paste");
		pasteItem.setHorizontalTextPosition(JMenuItem.CENTER);
		pasteItem.addActionListener(this);
		popMenu.add(pasteItem);
		
		popMenu.setLabel("Justification");
		popMenu.setBorder(new BevelBorder(BevelBorder.RAISED));
		panel.addMouseListener(this);
		
		frame.setVisible(true);
	}
	


	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == suplierFileButton)
		{
			suppliersFileChooser = new JFileChooser();
			if(directoryPath != null)
				suppliersFileChooser.setCurrentDirectory(new File(directoryPath));
			int returnVal = suppliersFileChooser.showOpenDialog(null);
		    if (returnVal == JFileChooser.APPROVE_OPTION) {
		        supplierOrdersFile = suppliersFileChooser.getSelectedFile();
		        // What to do with the file, e.g. display it in a TextArea
				filePath.setText(supplierOrdersFile.getAbsolutePath());
				directoryPath = supplierOrdersFile.getPath();
		    } else {
		        System.out.println("File access cancelled by user.");
		    }
		}
		else if(e.getSource() == send)
		{
			
			//send email to suppliers
			if(supplierOrdersFile == null)
			{
				JOptionPane.showConfirmDialog(null, "please fill attach file","",JOptionPane.PLAIN_MESSAGE);
				return;
			}
			
			clickSend++;
			
			suppliersOrdersFiles.add(supplierOrdersFile);
			
			if(expediteDate && !simMrp && clickSend < untilDate.size())
			{
				JOptionPane.showConfirmDialog(null, "Enter file Number " + (clickSend + 1),"",JOptionPane.PLAIN_MESSAGE);
				filePath.setText("");
				supplierOrdersFile = null;
				return;
			}
			

			Authenticator auth = new SocialAuth("AL-NT/"+from.split("@")[0],password);
			//Authenticator auth = new SocialAuth("naordalal1@gmail.com","nAOr1234");
			Sender sender;
			if(!sendExpeditesOrders && !expediteDate)
			{
				sender = new FollowUpSender(supplierEmailsFile, suppliersOrdersFiles, subjectText.getText(), bodyText.getText(), from, auth);
				((FollowUpSender) sender).setCC(ccList);
				((FollowUpSender) sender).setPermission(purchasingPermission);
				((FollowUpSender) sender).setDatesFilter(acceptOrder , noDate , passDate , futureDate , beyondRequestDate , untilDate);
				((FollowUpSender) sender).setDaysBeyondRequestDate(daysBeyondRequestDate);
			}				
			else if(expediteDate)
			{
				if(simMrp)
				{
					sender = new SimMrpSender(from, auth , suppliersOrdersFiles , bomsQuantity);
					((SimMrpSender) sender).setDatesFilter(untilDate);
				}
				else
				{
					sender = new SimSender(from, auth , suppliersOrdersFiles);
					((SimSender) sender).setDatesFilter(untilDate);
				}
					
					
			}
			else // sendExpeditesOrders == true , send expedite orders to suppliers
			{
				sender = new ExpediteOrdersSender(supplierEmailsFile, suppliersOrdersFiles, subjectText.getText(), bodyText.getText(), from, auth);
				((ExpediteOrdersSender) sender).setCC(ccList);
				((ExpediteOrdersSender) sender).setPermission(purchasingPermission);
			}
			
			
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			List<String> suppliersNamesList = (List<String>) sender.send();
			
			if(suppliersNamesList == null)
			{
				JOptionPane.showConfirmDialog(null, "error","",JOptionPane.PLAIN_MESSAGE);
				frame.setCursor(Cursor.getDefaultCursor());
				return;
			}

			if(suppliersNamesList.size() > 0)
			{
				sender.sendSuppliersNames(suppliersNamesList);
				
				for (String name : suppliersNamesList) 
				{
					int index = suppliersNamesList.indexOf(name);
					
					if(index != suppliersNamesList.size() - 1)
						name += " , ";
					
					if(index > 1 && index % 3 == 1)
						name += "\n";
					
					suppliersNamesList.set(index, name);
						
				}
				
				String suppliersNames = suppliersNamesList.stream().collect(Collectors.joining(""));
					
				frame.setCursor(Cursor.getDefaultCursor());
				
				JOptionPane.showConfirmDialog(null, "not found mail or there are no valid email for : " + suppliersNames,"",JOptionPane.PLAIN_MESSAGE);
				
			}
			else
				JOptionPane.showConfirmDialog(null, "success","",JOptionPane.PLAIN_MESSAGE);
			
			frame.setCursor(Cursor.getDefaultCursor());
			db.addUses(nickName, acceptOrder,noDate , passDate, futureDate, beyondRequestDate , expediteDate, sendExpeditesOrders , project);
			saveFiles();
			this.frame.dispose();
		}
		else if(e.getSource() == cutItem)
		{
			if(popMenu.getInvoker() == subjectText)
			{
				if(subjectText.getSelectedText() != null)
				{
					subjectText.cut();
				}
			}
			else if(popMenu.getInvoker() == bodyText)
			{
				if(bodyText.getSelectedText() != null)
				{
					bodyText.cut();
				}
			}
		}
		else if(e.getSource() == copyItem)
		{
			if(popMenu.getInvoker() == subjectText)
			{
				if(subjectText.getSelectedText() != null)
				{
					subjectText.copy();
				}
			}
			else if(popMenu.getInvoker() == bodyText)
			{
				if(bodyText.getSelectedText() != null)
				{
					bodyText.copy();
				}
			}
		}
		
		else if(e.getSource() == pasteItem)
		{
			if(popMenu.getInvoker() == subjectText)
			{
				subjectText.paste();
			}
			else if(popMenu.getInvoker() == bodyText)
			{
				bodyText.paste();
			}
		}
		
	}


	private void saveFiles() 
	{
		String directory = db.getDirectory(project);
		directory = (directory.equals("")) ? db.getFollowUpDirectory() : directory;
		if(directory.equals(""))
			return;
		
		directory = directory.replaceAll("/", "\\")+"\\";
		LocalDateTime date = LocalDateTime.now();
		String todayDate = Globals.dateToString(date).replaceAll("/", "");
		todayDate = todayDate.substring(0, todayDate.indexOf(" "));
		
		for (File file : suppliersOrdersFiles) 
		{
			String filename = file.getName().substring(0 , file.getName().indexOf("."));
			File destFile = new File(directory + filename + "_" + todayDate + ".xlsx");
			
			try {
				if(destFile.exists())
				{
					destFile.delete();
					destFile.createNewFile();
				}
				FileInputStream srcInputStream = new FileInputStream(file);
				FileChannel src = srcInputStream.getChannel();
				FileOutputStream dstOutputStream = new FileOutputStream(destFile);
				FileChannel dst = dstOutputStream.getChannel();
				
		        dst.transferFrom(src, 0, src.size());
		        src.close();
		        dst.close();
		        srcInputStream.close();
		        dstOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
	}


	public void setCC(List<String> ccList) 
	{
		this.ccList = ccList;
		
	}


	public void setPermission(boolean purchasingPermission) 
	{
		this.purchasingPermission = purchasingPermission;		
	}

	public void setDb(DataBase db) {
		this.db = db;
	}


	public void setProject(String project) 
	{
		this.project = project;
		
	}
	
	public void mousePressed(MouseEvent e)
	{
		checkPopup(e);
	}
	
	public void mouseClicked(MouseEvent e)
	{
		checkPopup(e);
	}
	
	public void mouseReleased(MouseEvent e)
	{
		checkPopup(e);
	}


	private void checkPopup(MouseEvent e) 
	{
		if(e.isPopupTrigger())
			popMenu.show(e.getComponent(), e.getX(), e.getY());
	}


	public void setDaysBeyondRequestDate(int days) 
	{
		this.daysBeyondRequestDate = days;
		
	}
	

}
