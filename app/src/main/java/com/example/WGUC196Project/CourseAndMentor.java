package com.example.WGUC196Project;

import java.util.ArrayList;

//this is the class for course and mentor objects

public class CourseAndMentor {

    private static ArrayList<CourseAndMentor> allCourseAndMentors = new ArrayList<CourseAndMentor>();

    private int courseID;
    private int termID;
    private int mentorID;
    private String courseTitle;
    private String termTitle;
    private String mentorName;
    private String phone;
    private String email;
    private String startDate;
    private String endDate;
    private String status;
    private int alertActive;

    private static String selection = "";

    private static boolean addClicked = false;

    public CourseAndMentor(int courseID, int termID, int mentorID, String courseTitle,
                           String termTitle, String mentorName, String phone, String email,
                           String startDate, String endDate, String status, int alertActive){

        setCourseID(courseID);
        setTermID(termID);
        setMentorID(mentorID);
        setCourseTitle(courseTitle);
        setTermTitle(termTitle);
        setMentorName(mentorName);
        setPhone(phone);
        setEmail(email);
        setStartDate(startDate);
        setEndDate(endDate);
        setStatus(status);
        setAlertActive(alertActive);

    }

    //checks if add course button is clicked (must place in this class because var. will be null when activity changes)
    public static void setAddClickBool(boolean clicked){
        addClicked = clicked;
    }

    public static boolean getAddClickBool(){
        return addClicked;
    }

    //gets the term that user selects on term list
    public static void setSelection(String selectedTerm){
        selection = selectedTerm;
    }

    public static String getSelection(){
        return selection;
    }

    //GETTERS:

    public int getCourseID(){
        return courseID;
    }

    public int getTermID(){
        return termID;
    }

    public int getMentorID(){
        return mentorID;
    }

    public String getCourseTitle(){
        return courseTitle;
    }

    public String getTermTitle(){
        return termTitle;
    }

    public String getMentorName(){
        return mentorName;
    }

    public String getPhone(){
        return phone;
    }

    public String getEmail(){
        return email;
    }

    public String getStartDate(){
        return startDate;
    }

    public String getEndDate(){
        return endDate;
    }

    public String getStatus(){
        return status;
    }

    public int getAlertActive(){
        return alertActive;
    }


    //SETTERS:

    public void setCourseID(int courseID){
        this.courseID = courseID;
    }

    public void setTermID(int termID){
        this.termID = termID;
    }

    public void setMentorID(int mentorID){
        this.mentorID = mentorID;
    }

    public void setCourseTitle(String courseTitle){
        this.courseTitle = courseTitle;
    }

    public void setTermTitle(String termTitle){
        this.termTitle = termTitle;
    }

    public void setMentorName(String mentorName){
        this.mentorName = mentorName;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setStartDate(String startDate){
        this.startDate = startDate;
    }

    public void setEndDate(String endDate){
        this.endDate = endDate;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public void setAlertActive(int alertActive){
        this.alertActive = alertActive;
    }

}
