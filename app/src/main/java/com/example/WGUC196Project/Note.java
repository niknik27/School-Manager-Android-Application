package com.example.WGUC196Project;

///this is the note class for all note objects

public class Note {

    private int noteID;
    private int courseID;
    private String courseTitle;
    private String noteText;

    private static String selection = "";

    private static boolean addClicked = false;

    public Note(int noteID, int courseID, String courseTitle, String noteText){

        setNoteID(noteID);
        setCourseID(courseID);
        setCourseTitle(courseTitle);
        setNoteText(noteText);
    }

    //checks if add note button is clicked (must place in this class because var. will be null when activity changes)
    public static void setAddClickBool(boolean clicked){
        addClicked = clicked;
    }

    public static boolean getAddClickBool(){
        return addClicked;
    }

    //gets the note that user selects on note list
    public static void setSelection(String selectedNote){
        selection = selectedNote;
    }

    public static String getSelecton(){
        return selection;
    }

    //////////GETTERS:///////

    public int getNoteID(){
        return noteID;
    }

    public int getCourseID(){
        return courseID;
    }

    public String getCourseTitle(){
        return courseTitle;
    }

    public String getNoteText(){
        return noteText;
    }

    ////////SETTERS:////////

    public void setNoteID(int noteID){
        this.noteID = noteID;
    }

    public void setCourseID(int courseID){
        this.courseID = courseID;
    }

    public void setCourseTitle(String courseTitle){
        this.courseTitle = courseTitle;
    }

    public void setNoteText(String noteText){
        this.noteText = noteText;
    }
}
