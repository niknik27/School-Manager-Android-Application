package com.example.WGUC196Project;

import java.util.ArrayList;

//this is the class for assessment objects

public class Assessment {

    private static ArrayList<Assessment> allAssessment = new ArrayList<Assessment>();

    private int assessID;
    private int courseID;
    private String courseName;
    private String assessName;
    private String type;
    private String goalDate;
    private String startDate;
    private String endDate;
    private int alertActive;

    private static String selection = "";

    private static boolean addClicked = false;

    public Assessment(int assessID, int courseID, String courseName, String assessName, String type,
                      String goalDate, String startDate, String endDate, int alertActive){

        setAssessID(assessID);
        setCourseID(courseID);
        setCourseName(courseName);
        setAssessName(assessName);
        setType(type);
        setGoalDate(goalDate);
        setStartDate(startDate);
        setEndDate(endDate);
        setAlertActive(alertActive);
    }

    //checks if add assessment button is clicked (must place in this class because var. will be null when activity changes)
    public static void setAddClickBool(boolean clicked){
        addClicked = clicked;
    }

    public static boolean getAddClickBool(){
        return addClicked;
    }

    //gets the assessment that user selects on assessment list
    public static void setSelection(String selectedAssessment){
        selection = selectedAssessment;
    }

    public static String getSelecton(){
        return selection;
    }

    //GETTERS:

    public int getAssessID(){
        return assessID;
    }

    public int getCourseID(){
        return courseID;
    }

    public String getCourseName(){
        return courseName;
    }

    public String getAssessName(){
        return assessName;
    }

    public String getType(){
        return type;
    }

    public String getGoalDate(){
        return goalDate;
    }

    public String getStartDate(){
        return startDate;
    }

    public String getEndDate(){
        return endDate;
    }

    public int getAlertActive(){
        return alertActive;
    }

    //SETTERS:


    public void setAssessID(int assessID){
        this.assessID = assessID;
    }

    public void setCourseID(int courseID){
        this.courseID = courseID;
    }

    public void setCourseName(String courseName){
        this.courseName = courseName;
    }

    public void setAssessName(String assessName){
        this.assessName = assessName;
    }

    public void setType(String type){
        this.type = type;
    }

    public void setGoalDate(String goalDate){
        this.goalDate = goalDate;
    }

    public void setStartDate(String startDate){
        this.startDate = startDate;
    }

    public void setEndDate(String endDate){
        this.endDate = endDate;
    }

    public void setAlertActive(int alertActive){
        this.alertActive = alertActive;
    }
}
