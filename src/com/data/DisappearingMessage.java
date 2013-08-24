package com.data;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * Author: Roshan C Subudhi
 * USC, Columbia
 * Ph: 803-743-2899
 * 
 * for EHG, Boston, MA
 * 
 * This class shows incoming message notifications on the PC.
 */
public class DisappearingMessage implements ActionListener
{
	private Timer timer;
	private JFrame frame;
	private JLabel sender, timestamp;
	private JTextArea msg;
	private JPanel panel;

	public DisappearingMessage (String from, String body, String time, int seconds) 
	{
		frame = new JFrame ("New Message");
		panel = new JPanel();

		sender = new JLabel (from, SwingConstants.LEFT);
		msg = new JTextArea (body,4,15);
		msg.setLineWrap(true);
		timestamp = new JLabel (time, SwingConstants.RIGHT);

		timer = new Timer (1000 * seconds, this);
		timer.setRepeats(false);
	}

	public void showNotice()
	{
		frame.getContentPane().add(sender, BorderLayout.NORTH);
		panel.add(msg, BorderLayout.NORTH);
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.getContentPane().add(timestamp, BorderLayout.SOUTH);
		frame.pack();

		frame.setVisible(true);
		frame.setLocation(900,500);
		frame.setSize(300,150);

		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		timer.start();
	}

	public void actionPerformed (ActionEvent event)
	{
		timer.stop();
		frame.dispose();
	}
}