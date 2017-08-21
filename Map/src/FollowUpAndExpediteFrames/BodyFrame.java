package FollowUpAndExpediteFrames;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.lang.invoke.MethodHandle;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.apache.poi.ss.usermodel.Font;

import MainPackage.DataBase;
import MainPackage.Globals;

public class BodyFrame implements ActionListener {

	
	private JFrame frame;
	private JPanel panel;
	private JLabel subjectLabel;
	private JTextField subjectText;
	private JLabel bodyLabel;
	private JTextArea bodyText;
	private JButton addButton;
	private Globals globals;
	private DataBase db;
	private int datesId;
	private JLabel copyRight;
	private MethodHandle callbackMethodOfBodyFrame;
	
	public BodyFrame(DataBase db , int datesId, MethodHandle callbackMethodOfBodyFrame)
	{
		this.db = db;
		this.datesId = datesId;
		this.callbackMethodOfBodyFrame = callbackMethodOfBodyFrame;
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
		
		
		subjectLabel = new JLabel("subject:");
		subjectLabel.setLocation(30 ,  5);
		subjectLabel.setSize(50 ,  100);
		panel.add(subjectLabel);
		
		subjectText = new JTextField();
		subjectText.setLocation(80 ,  40);
		subjectText.setSize(300 ,  30);
		globals.initTextComponent(subjectText);
		panel.add(subjectText);
		subjectText.requestFocusInWindow();
		
		bodyLabel = new JLabel("body:");
		bodyLabel.setLocation(30 ,  55);
		bodyLabel.setSize(50 ,  100);
		panel.add(bodyLabel);
		
		bodyText = new JTextArea();
		bodyText.setLocation(80 ,  100);
		bodyText.setSize(450 ,  250);
		bodyText.setFont(new java.awt.Font("Ariel", Font.ANSI_CHARSET , 14));
		globals.initTextComponent(bodyText);
		panel.add(bodyText);
		
		addButton = new JButton();
		addButton.setLocation(225 , 380);
		addButton.setSize(100, 40);
		addButton.addActionListener(this);
		addButton.setIcon(globals.okIcon);
		addButton.setFocusable(false);
		addButton.setContentAreaFilled(false);
		addButton.setPressedIcon(globals.clickOkIcon);
		addButton.setToolTipText("add subject and body");
		panel.add(addButton);
		
		copyRight = new JLabel("<html><b>\u00a9 Naor Dalal</b></html>");
		copyRight.setLocation(30 , 440);
		copyRight.setSize(100,30);
		panel.add(copyRight);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == addButton)
		{
			if(subjectText.getText().equals("") || bodyText.getText().equals(""))
			{
				JOptionPane.showConfirmDialog(null, "please fill subject and body","",JOptionPane.PLAIN_MESSAGE);
			}
			else
			{
				if(db.addSubjectAndBody(datesId, subjectText.getText(), bodyText.getText()))
				{
					JOptionPane.showConfirmDialog(null, "success","",JOptionPane.PLAIN_MESSAGE);
					try {
						callbackMethodOfBodyFrame.invokeExact();
					} catch (Throwable e1) {
						e1.printStackTrace();
					}
					frame.dispose();
				}
				else
				{
					JOptionPane.showConfirmDialog(null, "fail , body already exist for these dates","",JOptionPane.PLAIN_MESSAGE);
				}
			}
		}
		
	}
}
