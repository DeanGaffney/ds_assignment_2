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
import com.dist.models.Module;
import com.dist.models.Student;

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
			outputToClient.println("You are connected to the server");
			outputToClient.println("Your address :" + socket.getInetAddress().getHostAddress());

			outputToClient.flush();
			updateTextArea("Processing...\n");
			
			while (true) {
				Integer studentId = Integer.parseInt(inputFromClient.readLine()); //get student id
				String moduleName = inputFromClient.readLine();					//get the moduleName
				
				//get connection to the database
				Connection con = ConnectionPool.getInstance().getConnection();
				
				if(isValidStudent(con, studentId)){
					updateTextArea("Student is valid.....\n");
					String name = getStudentName(con, studentId);
					outputToClient.println("Welcome " + name + "\n");
					outputToClient.flush();

					Student student = createStudent(con, studentId, moduleName);
					outputToClient.println(student.toString() + "\n");
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
	 * Creates a student object from a left join on the student and module table
	 * @return Student - the student object with all attributes including module with overall grade
	 */
	private Student createStudent(Connection con, int studentId, String moduleName){
		Student student = null;
		try{
			Statement statement = con.createStatement();
			ResultSet resultSet = statement.executeQuery(getStudentQuery(studentId, moduleName));
			if(resultSet.first()){
				Module module = new Module(resultSet.getString("ModuleName"), resultSet.getDouble("CA_Mark"), 
						resultSet.getDouble("Exam_Mark"), resultSet.getDouble("overall_grade"));
				student = new Student(resultSet.getInt("STUD_ID"), resultSet.getString("FNAME"), 
						resultSet.getString("SNAME"), module);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return student;
	}
	
	/**
	 * Returns the sql left join query on the 
	 * @return String - the string for the sql query 
	 */
	private String getStudentQuery(int studentId, String moduleName){
		return "SELECT students.STUD_ID, students.FNAME, students.SNAME, modulegrades.ModuleName, modulegrades.CA_Mark, modulegrades.Exam_Mark,"
				+ "((CA_Mark / 100.0 * 30 / 1) + (Exam_Mark / 100.0 * 70 / 1)) as overall_grade "
				+ "FROM students "
				+ "LEFT JOIN modulegrades ON students.STUD_ID = modulegrades.STUD_ID WHERE students.STUD_ID = " + studentId + " " 
				+ "AND modulegrades.ModuleName = '" + moduleName + "'";
	}
	
	/**
	 * Updates the servers text area, synchronized so it it locked until this thread
	 * is completed adding its message to the text area
	 * @param message - the message to display on the board
	 */
	private synchronized void updateTextArea(String message){
		textArea.append(message);
	}
}
