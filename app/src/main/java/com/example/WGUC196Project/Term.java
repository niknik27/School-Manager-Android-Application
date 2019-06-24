package com.example.WGUC196Project;

import java.util.ArrayList;

///this is the class for all term objects

public class Term {

    private static ArrayList<Term> allTerms = new ArrayList<Term>();

    private int ID;
    private String termTitle;
    private String startDate;
    private String endDate;
    private int alertActive;


    private static String selection = "";

    private static boolean addClicked = false;

    public Term(int ID, String termTitle, String startDate, String endDate, int alertActive){

        setID(ID);
        setTermTitle(termTitle);
        setStartDate(startDate);
        setEndDate(endDate);
        setAlertActive(alertActive);

    }

    //checks if add term button is clicked (must place in this class because var. will be null when activity changes)
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

    public static String getSelecton(){
        return selection;
    }

    //GETTERS:

    public int getID(){
        return ID;
    }

    public String getTermTitle(){
        return termTitle;
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

    public void setID(int ID){
        this.ID = ID;
    }

    public void setTermTitle(String termTitle){
        this.termTitle = termTitle;
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
