package com.dist.models;

public class Student {
	
	private int id;
	private String firstName;
	private String surname;
	private Module module;
	
	public Student(int id, String firstName, String surname, Module module) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.surname = surname;
		this.module = module;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Module getModule() {
		return module;
	}

	public void setModule(Module module) {
		this.module = module;
	}
	
	@Override
	public String toString(){
		return "Student ID - " + getId() + "\n" +
			   "First Name - " + getFirstName() + "\n" +
			   "Surname - " + getSurname() + "\n" +
			   "Module Name - " + getModule().getName() + "\n" +
			   "CA Mark - " + getModule().getCaMark() + "\n" +
			   "Exam Mark - " + getModule().getExamMark() + "\n" +
			   "Overall Grade - " + getModule().getOverallGrade() + "\n";
	}
	
}
