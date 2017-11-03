package com.dist.threads;

import java.io.BufferedReader;
import java.io.EOFException;
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
		updateServerTextArea("Accepted client address - " + socket.getInetAddress().getHostAddress() +"\n");
		try {
			inputFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outputToClient = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			respondToClient("You are connected to the server\n Your address :" + socket.getInetAddress().getHostAddress());
			updateServerTextArea("Processing...\n");
			
			while (!socket.isInputShutdown()) {
				Integer studentId = Integer.parseInt(inputFromClient.readLine()); //get student id
				String moduleName = inputFromClient.readLine();					//get the moduleName
				
				//get connection to the database
				Connection con = ConnectionPool.getInstance().getConnection();
				
				if(isValidStudent(con, studentId)){
					updateServerTextArea("Student is valid.....\n");
					String name = getStudentName(con, studentId);
					if(name != null){
						respondToClient("Welcome " + name + "\n");
					}
					Student student = createStudent(con, studentId, moduleName);
					if(student != null){		//could be null if user enters the wrong module name
						respondToClient(student.toString() + "\n");
					}
				}else{
					//tell them it is invalid
					respondToClient("Sorry " + studentId + ". You are not a registered student bye\n Closing socket....\n");
					socket.close();
					break;
				}
			}
			updateServerTextArea("Connection to client was closed....\n");
		}catch(EOFException | NumberFormatException e){
			updateServerTextArea("Connection to client was closed....\n");
		}catch (IOException e) {
			e.printStackTrace();
		} finally{		//freeup resources so thread can be reclaimed by the OS
			try {
				inputFromClient.close();
				outputToClient.close();
				socket.close();
				updateServerTextArea("Resources freed from client\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
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
			if(resultSet.isBeforeFirst() && resultSet.first()){
				name = resultSet.getString("FNAME");
			}else{
				respondToClient("Student has no name.....\n");
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
			if(resultSet.isBeforeFirst() && resultSet.first()){
				Module module = new Module(resultSet.getString("ModuleName"), resultSet.getDouble("CA_Mark"), 
						resultSet.getDouble("Exam_Mark"), resultSet.getDouble("overall_grade"));
				student = new Student(resultSet.getInt("STUD_ID"), resultSet.getString("FNAME"), 
						resultSet.getString("SNAME"), module);
			}else{
				respondToClient("No module found with name:" + moduleName);
				sendAvailableModuleNames(con);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return student;
	}
	
	/**
	 * Get the list of available modules from the database
	 * @return A List of module names from the database
	 */
	private void sendAvailableModuleNames(Connection con){
		try{
			Statement statement = con.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT DISTINCT ModuleName FROM modulegrades");
			if(resultSet.isBeforeFirst()){
				respondToClient(getDistinctModuleNames(resultSet));
			}else{
				respondToClient("There are no modules available to query at this time....please try again later.\n");
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Takes in a result set of module names and formats them into a string for the client
	 * @param resultSet - the result set of module names from the database
	 * @return String - a message for the client containing the available module names
	 * @throws SQLException 
	 */
	private String getDistinctModuleNames(ResultSet resultSet) throws SQLException{
		StringBuilder builder = new StringBuilder("Please choose a module from the following:\n");
		int moduleNum = 1;
		while(resultSet.next()){
			builder.append( "\t" + moduleNum + "." + resultSet.getString("ModuleName") + "\n");
			moduleNum++;
		}
		return builder.toString();
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
	 * Helper method to send messages to the client
	 * @param message - the message to send to the client
	 */
	private void respondToClient(String message){
		outputToClient.println(message);
		outputToClient.flush();
	}
	
	/**
	 * Updates the servers text area, synchronized so it it locked until this thread
	 * is completed adding its message to the text area
	 * @param message - the message to display on the board
	 */
	private synchronized void updateServerTextArea(String message){
		textArea.append(message);
	}
}
