package com.dist.models;

/**
 * Class for modeling a module inside the database
 * @author Dean Gaffney
 */
public class Module {
	
	private String name;
	private double caMark;
	private double examMark;
	private double overallGrade;
	
	public Module(String name, double caMark, double examMark, double overallGrade) {
		this.name = name;
		this.caMark = caMark;
		this.examMark = examMark;
		this.overallGrade = overallGrade;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getCaMark() {
		return caMark;
	}

	public void setCaMark(double caMark) {
		this.caMark = caMark;
	}

	public double getExamMark() {
		return examMark;
	}

	public void setExamMark(double examMark) {
		this.examMark = examMark;
	}
	
	public void setOverallGrade(double grade){
		this.overallGrade = grade;
	}
	
	public double getOverallGrade(){
		return this.overallGrade;
	}
}
