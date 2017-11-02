package com.dist.client;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClientA2 extends JFrame{

	private static final long serialVersionUID = 1L;
	// Text field for receiving radius
	private JTextField studentIdTextField = new JTextField(10);
	private JTextField moduleNameTextField = new JTextField(10);
	private JButton submitButton = new JButton("Submit");

	// Text area to display contents
	private JTextArea jta = new JTextArea();

	// IO streams
	private BufferedReader fromServer;
	private PrintWriter toServer;
	private Socket socket;

	public static void main(String[] args) {
		new ClientA2();
	}

	public ClientA2() {
		// Panel p to hold the label and text field
		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());

		GridBagConstraints left = new GridBagConstraints();
		left.anchor = GridBagConstraints.EAST;

		GridBagConstraints right = new GridBagConstraints();
		right.weightx = 2.0;
		right.fill = GridBagConstraints.HORIZONTAL;
		right.gridwidth = GridBagConstraints.REMAINDER;

		GridBagConstraints south = new GridBagConstraints();
		left.anchor = GridBagConstraints.SOUTH;

		p.add(new JLabel("StudentId"), left);
		p.add(studentIdTextField, right);

		p.add(new JLabel("Module Name"), left);
		p.add(moduleNameTextField, right);

		p.add(submitButton, south);
		getContentPane().add(p);

		setLayout(new BorderLayout());
		add(p, BorderLayout.NORTH);
		add(new JScrollPane(jta), BorderLayout.CENTER);

		submitButton.addActionListener(new Listener());

		setTitle("Client");
		setSize(500, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true); // It is necessary to show the frame here!

		try {
			// Create a socket to connect to the server
			socket = new Socket("localhost", 8000);

			fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			toServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		}
		catch (IOException ex) {
			jta.append(ex.toString() + '\n');
		}finally{
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class Listener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				// Get the radius from the text field
				int studentId = Integer.parseInt(studentIdTextField.getText().trim());
				String moduleName = moduleNameTextField.getText().trim();
				toServer.println(studentId);
				toServer.println(moduleName);
				toServer.flush();

				// Get area from the server
				String serverResponse = fromServer.readLine();
				jta.append(serverResponse);
			}
			catch (IOException ex) {
				System.err.println(ex);
			}
		}
	}

}
