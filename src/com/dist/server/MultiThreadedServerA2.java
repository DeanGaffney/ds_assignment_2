package com.dist.server;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.dist.threads.ServerThread;
import com.dist.threads.ThreadPool;


public class MultiThreadedServerA2 extends JFrame{

	private static final long serialVersionUID = 1L;

	private JTextArea textArea = new JTextArea();

	public static void main(String[] args) {
		new MultiThreadedServerA2();
	}

	public MultiThreadedServerA2() {

		// Place text area on the frame
		setLayout(new BorderLayout());
		add(new JScrollPane(textArea), BorderLayout.CENTER);

		setTitle("Server");
		setSize(500, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true); // It is necessary to show the frame here!

		while(true){
			try {
				// Create a server socket
				@SuppressWarnings("resource")
				ServerSocket serverSocket = new ServerSocket(8000);
				textArea.append("Server started at " + new Date() + '\n');
				while(true){
					ThreadPool.getInstance().execute(new ServerThread(serverSocket.accept(), textArea));
				}
			}
			catch(IOException ex) {
				System.err.println(ex);
			}
		}
	}

}
