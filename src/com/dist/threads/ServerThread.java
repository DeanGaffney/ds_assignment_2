package com.dist.threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JTextArea;

import com.dist.db.ConnectionPool;

/**
 * Server thread deals with client requests
 * @author Dean _Gaffney
 */
public class ServerThread implements Runnable{

	private Socket socket;
	private JTextArea textArea;
	private BufferedReader inputFromClient;
	private PrintWriter outputToClient;

	public ServerThread(Socket socket, JTextArea textArea){
		this.socket = socket;
		this.textArea = textArea;
	}

	@Override
	public void run() {
		updateTextArea("Accepted client address - " + socket.getInetAddress().getHostAddress() +"\n");
		try {
			inputFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outputToClient = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			outputToClient.println("You are connected to the server.... Please enter your Student ID and Module\n");
			outputToClient.flush();
			updateTextArea("Processing...\n");
			
			while (true) {
				Integer studentId = Integer.parseInt(inputFromClient.readLine()); //get student id
				String moduelName = inputFromClient.readLine();					//get the moduleName
				
				//get connection to the database
				Connection con = ConnectionPool.getInstance().getConnection();
				
				if(isValidStudent(con, studentId)){
					updateTextArea("Student is valid.....\n");
					String name = getStudentName(con, studentId);
					outputToClient.println("Welcome " + name + "\n");
					outputToClient.flush();

					double grade = getOverallGrade(con, studentId, moduelName);
					updateTextArea("Student grade is:" + grade + "\n");
					outputToClient.println("Your grade is:" + grade + "\n");
					outputToClient.flush();

				}else{
					//tell them it is invalid
					outputToClient.println("Sorry " + studentId + ". You are not a registered student bye\n");
					outputToClient.flush();
					socket.close();
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	/**
	 * Checks to see if the database contains a valid student for the student id
	 * @param con - the connection object to the database
	 * @param studentId - the student id to query
	 * @return true if the student exists in the database, false otherwise
	 */
	private boolean isValidStudent(Connection con, int studentId){
		boolean isValid = true;
		try{
			Statement statement = con.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT STUD_ID FROM students WHERE STUD_ID = " + studentId);
			isValid = (resultSet.isBeforeFirst()) ? true : false;
		}catch(SQLException e){
			e.printStackTrace();
			isValid = false;
		}
		return isValid;
	}
	
	/**
	 * Retrieves the students name from the database
	 * @param con - connection
	 * @param studentId - the studentId to query
	 * @return The students name
	 */
	private String getStudentName(Connection con, int studentId){
		String name = "";
		try{
			Statement statement = con.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT FNAME FROM students WHERE STUD_ID = " + studentId);
			if(resultSet.first()){
				name = resultSet.getString("FNAME");
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return name;
	}
	
	/**
	 * Gets the overall grade for a specific student for a specific module
	 * using the specified formula of (CA = 30% & EXAM = 70%)
	 * @param con - the connection object to the database
	 * @param studentId - the student id to query
	 * @param module - the module to query
	 * @return the overall grade for the module
	 */
	private double getOverallGrade(Connection con, int studentId, String moduleName){
		double grade = 0;
		try{
			Statement statement = con.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT STUD_ID, CA_Mark, Exam_Mark, "
					+ "((CA_Mark / 100.0 * 30 / 1) + (Exam_Mark / 100.0 * 70 / 1)) as overall_grade "
					+ "FROM modulegrades "
					+ "WHERE STUD_ID = " +  studentId + " " 
					+ "AND ModuleName = '" + moduleName + "'");
			if(resultSet.first()){
				grade = resultSet.getDouble("overall_grade");
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return grade;
	}
	
	/**
	 * Updates the servers text area, synchronized so it it locked until this thread
	 * is completed adding its message to the text area
	 * @param message
	 */
	private synchronized void updateTextArea(String message){
		textArea.append(message);
	}
}
