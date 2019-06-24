package com.example.WGUC196Project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

///these are all the methods used by other scripts in the application to access and manipulate the local SQLite database

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "INFODB.db";
    private static final int DATABASE_VERSION = 1;

    Term termClass;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        createTermTable(sqLiteDatabase);
        createCourseTable(sqLiteDatabase);
        createAssessTable(sqLiteDatabase);
        createMentorTable(sqLiteDatabase);
        createNoteTable(sqLiteDatabase);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(sqLiteDatabase);
    }

    //////////////RETURN OBJECTS///////////////////////////////////
    ///these loop through certain tables and then store the objects for use

    public ArrayList<CourseAndMentor> readCourseAndMentorObjects(String sqlStatement){

        ArrayList<CourseAndMentor> allCourseAndMentors = new ArrayList<CourseAndMentor>();

        int courseID;
        int termID;
        int mentorID;
        String courseTitle;
        String termTitle;
        String mentorName;
        String phone;
        String email;
        String startDate;
        String endDate;
        String status;
        int alertActive;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlStatement, null);

        while(cursor.moveToNext()){

            courseID = cursor.getInt(cursor.getColumnIndex("courseId"));
            termID = cursor.getInt(cursor.getColumnIndex("termId"));
            mentorID = cursor.getInt(cursor.getColumnIndex("mentorId"));
            courseTitle = cursor.getString(cursor.getColumnIndex("courseTitle"));
            termTitle = cursor.getString(cursor.getColumnIndex("termTitle"));
            mentorName = cursor.getString(cursor.getColumnIndex("mentorName"));
            phone = cursor.getString(cursor.getColumnIndex("phone"));
            email = cursor.getString(cursor.getColumnIndex("email"));
            startDate = cursor.getString(cursor.getColumnIndex("startDate"));
            endDate = cursor.getString(cursor.getColumnIndex("endDate"));
            status = cursor.getString(cursor.getColumnIndex("status"));
            alertActive = cursor.getInt(cursor.getColumnIndex("alertActive"));


            allCourseAndMentors.add(new CourseAndMentor(courseID, termID, mentorID, courseTitle, termTitle,
                                        mentorName, phone, email, startDate, endDate, status, alertActive));
        }

        return allCourseAndMentors;

    }


    public ArrayList<Term> readTermObjects(String sqlStatement){

        ArrayList<Term> allTerms = new ArrayList<Term>();

        int ID;
        String termTitle;
        String startDate;
        String endDate;
        int alertActive;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlStatement, null);

        while(cursor.moveToNext()){

            ID = cursor.getInt(cursor.getColumnIndex("termId"));
            termTitle = cursor.getString(cursor.getColumnIndex("termTitle"));
            startDate = cursor.getString(cursor.getColumnIndex("startDate"));
            endDate = cursor.getString(cursor.getColumnIndex("endDate"));
            alertActive = cursor.getInt(cursor.getColumnIndex("alertActive"));


            allTerms.add(new Term(ID, termTitle, startDate, endDate, alertActive));
        }

        return allTerms;

    }

    public ArrayList<Assessment> readAssessObjects(String sqlStatement){

        ArrayList<Assessment> allAssessments = new ArrayList<Assessment>();

        int assessID;
        int courseID;
        String courseName;
        String assessName;
        String type;
        String goalDate;
        String startDate;
        String endDate;
        int alertActive;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlStatement, null);

        while(cursor.moveToNext()){

            assessID = cursor.getInt(cursor.getColumnIndex("assessmentId"));
            courseID = cursor.getInt(cursor.getColumnIndex("courseId"));
            courseName = cursor.getString(cursor.getColumnIndex("courseTitle"));
            assessName = cursor.getString(cursor.getColumnIndex("name"));
            type = cursor.getString(cursor.getColumnIndex("type"));
            goalDate = cursor.getString(cursor.getColumnIndex("goalDate"));
            startDate = cursor.getString(cursor.getColumnIndex("startDate"));
            endDate = cursor.getString(cursor.getColumnIndex("endDate"));
            alertActive = cursor.getInt(cursor.getColumnIndex("alertActive"));

            allAssessments.add(new Assessment(assessID, courseID, courseName, assessName, type, goalDate, startDate, endDate, alertActive));
        }

        return allAssessments;

    }

    public ArrayList<Note> readNoteObjects(String sqlStatement){

        ArrayList<Note> allNotes = new ArrayList<Note>();

        int noteID;
        int courseID;
        String courseName;
        String noteText;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlStatement, null);

        while(cursor.moveToNext()){

            noteID = cursor.getInt(cursor.getColumnIndex("noteId"));
            courseID = cursor.getInt(cursor.getColumnIndex("courseId"));
            courseName = cursor.getString(cursor.getColumnIndex("courseTitle"));
            noteText = cursor.getString(cursor.getColumnIndex("note"));

            allNotes.add(new Note(noteID, courseID, courseName, noteText));
        }

        return allNotes;

    }

    //////////////////GET SELECTED OBJECT DETAILS/////////////////////
    ////these act the same as the methods above BUT only return one object a=instead of a full list

    public CourseAndMentor returnCourseAndMentor(){

        ArrayList<CourseAndMentor> selectedCourseAndMentor = new ArrayList<CourseAndMentor>();
        String courseName = CourseAndMentor.getSelection();
        CourseAndMentor selectedCourseAndMentorObjectReturn = null;

        if(!courseName.equals("")){

            selectedCourseAndMentor = readCourseAndMentorObjects("SELECT courseId, Course_tbl.termId, Course_tbl.mentorId, \n" +
                                                                        "\t\t\t\tcourseTitle, Course_tbl.startDate, Course_tbl.endDate, \n" +
                                                                        "\t\t\t\t\ttermTitle, status, Course_tbl.alertActive, mentorName, phone, email\n" +
                                                                        "FROM Course_tbl, Mentor_tbl, Term_tbl\n" +
                                                                        "WHERE Course_tbl.termId = Term_tbl.termId\n" +
                                                                        "AND Course_tbl.mentorId = Mentor_tbl.mentorId\n" +
                                                                        "AND courseTitle = \"" + courseName + "\"");

            for (CourseAndMentor courseAndMentor : selectedCourseAndMentor){

                selectedCourseAndMentorObjectReturn = courseAndMentor;


            }
        }else{

            String TAG = "nullLog";
            Log.d(TAG, "Mentor Selection is EMPTY");
        }



        return selectedCourseAndMentorObjectReturn;
    }

    public Term returnTerm(){

        ArrayList<Term> selectedTerm = new ArrayList<Term>();
        String termName = Term.getSelecton();
        Term selectedTermObjectReturn = null;

        if(!termName.equals("")){

            selectedTerm = readTermObjects("SELECT * FROM Term_tbl WHERE termTitle = \"" + termName + "\"");

            for (Term term : selectedTerm){

                selectedTermObjectReturn = term;
            }
        }else{

            String TAG = "nullLog";
            Log.d(TAG, "Term Selection is EMPTY");
        }

        return selectedTermObjectReturn;
    }

    public Assessment returnAssessment(){

        ArrayList<Assessment> selectedAssessment = new ArrayList<Assessment>();
        String assessName = Assessment.getSelecton();
        Assessment selectedAssessObjectReturn = null;

        if(!assessName.equals("")){

            selectedAssessment = readAssessObjects("SELECT assessmentId, Assessment_tbl.courseId, courseTitle, name, type, goalDate" +
                                                    ", Assessment_tbl.startDate, Assessment_tbl.endDate, Assessment_tbl.alertActive" +
                                                    "\nFROM Assessment_tbl, Course_tbl\n" +
                                                    "\nWHERE Assessment_tbl.courseId = Course_tbl.courseId" +
                                                    "\nAND name = \"" + assessName + "\"");

            for (Assessment assessment : selectedAssessment){

                selectedAssessObjectReturn = assessment;
            }

        }else{

            String TAG = "nullLog";
            Log.d(TAG, "Assessment Selection is EMPTY");
        }

        return selectedAssessObjectReturn;
    }

    public Note returnNote(){

        ArrayList<Note> selectedNote = new ArrayList<Note>();
        String noteText = Note.getSelecton();
        Note selectedNoteObjectReturn = null;


        if(!noteText.equals("")){

            selectedNote = readNoteObjects("SELECT noteId, Note_tbl.courseId, Course_tbl.courseTitle, note \n" +
                                                "FROM Note_tbl, Course_tbl \n" +
                                                "WHERE Note_tbl.courseId = Course_tbl.courseId\n" +
                                                "AND note = \"" + noteText + "\"");

            for (Note note : selectedNote){

                selectedNoteObjectReturn = note;


            }

        }else{

            String TAG = "nullLog";
            Log.d(TAG, "Note Selection is EMPTY");
        }

        return selectedNoteObjectReturn;
    }

    ///////////////////GET NAMES AND ID'S OF OBJECTS FOR LIST///////////////////

    public ArrayList<String> getCourseDates(String columnName, String tableName){

        ArrayList<String> allCourseDates = new ArrayList<String>();

        String courseDate;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + columnName +" FROM "+ tableName + ";", null);


        while(cursor.moveToNext()){

            courseDate = cursor.getString(cursor.getColumnIndex(columnName));

            allCourseDates.add(courseDate);
        }

        return allCourseDates;

    }

    public ArrayList<String> getAssessDates(String columnName, String tableName){

        ArrayList<String> allAssessDates = new ArrayList<String>();

        String assessDate;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + columnName +" FROM "+ tableName + ";", null);


        while(cursor.moveToNext()){

            assessDate = cursor.getString(cursor.getColumnIndex(columnName));

            allAssessDates.add(assessDate);
        }

        return allAssessDates;

    }

    public String getMatchTermName(String columnName, String date){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT termTitle \n" +
                "FROM Term_tbl \n" +
                "WHERE " + columnName + " = \"" + date + "\"", null);

        String matchingTermName = "";

        while(cursor.moveToNext()){

            matchingTermName = cursor.getString(cursor.getColumnIndex("termTitle"));

        }

        return matchingTermName;

    }

    public String getMatchCourseName(String columnName, String date){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT courseTitle \n" +
                                    "FROM Course_tbl \n" +
                                    "WHERE " + columnName + " = \"" + date + "\"", null);

        String matchingCourseName = "";

        while(cursor.moveToNext()){

            matchingCourseName = cursor.getString(cursor.getColumnIndex("courseTitle"));

        }

        return matchingCourseName;

    }

    public String getMatchAssessName(String columnName, String date){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT name \n" +
                "FROM Assessment_tbl \n" +
                "WHERE " + columnName + " = \"" + date + "\"", null);

        String matchingAssessName = "";

        while(cursor.moveToNext()){

            matchingAssessName = cursor.getString(cursor.getColumnIndex("name"));

        }

        return matchingAssessName;

    }

    public int getCourseID(String name){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT courseId FROM Course_tbl\n" +
                                    "WHERE courseTitle = \"" + name + "\"", null);
        int selectedCourseID = 0;

        while(cursor.moveToNext()){

            selectedCourseID = cursor.getInt(cursor.getColumnIndex("courseId"));

        }

        return selectedCourseID;

    }

    public int getTermID(String name){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT termId FROM Term_tbl\n" +
                "WHERE termTitle = \"" + name + "\"", null);

        int selectedTermID = 0;

        while(cursor.moveToNext()){

            selectedTermID = cursor.getInt(cursor.getColumnIndex("termId"));

        }

        return selectedTermID;

    }

    public ArrayList<String> readCourseRecordName (String sqlStatement){

        ArrayList<String> allCourseNames = new ArrayList<String>();

        String courseName;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlStatement, null);


        while(cursor.moveToNext()){

            courseName = cursor.getString(cursor.getColumnIndex("courseTitle"));

            allCourseNames.add(courseName);
        }

        return allCourseNames;
    }

    public ArrayList<String> readAssessRecordName (String sqlStatement){

        ArrayList<String> allAssessNames = new ArrayList<String>();

        String assessName;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlStatement, null);

        while(cursor.moveToNext()){

            assessName = cursor.getString(cursor.getColumnIndex("name"));

            allAssessNames.add(assessName);
        }

        return allAssessNames;
    }

    public ArrayList<String> readNotesRecordName (String sqlStatement){

        ArrayList<String> allNotes = new ArrayList<String>();

        String note;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlStatement, null);

        while(cursor.moveToNext()){

            note = cursor.getString(cursor.getColumnIndex("note"));

            allNotes.add(note);
        }

        return allNotes;
    }

    public ArrayList<String> readTermRecordName (String sqlStatement){

        ArrayList<String> allTermNames = new ArrayList<String>();

        //int ID;
        String termTitle;
        //String startDate;
        //String endDate;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlStatement, null);

        while(cursor.moveToNext()){

            //ID = cursor.getInt(cursor.getColumnIndex("termId"));
            termTitle = cursor.getString(cursor.getColumnIndex("termTitle"));
            //startDate = cursor.getString(cursor.getColumnIndex("startDate"));
            //endDate = cursor.getString(cursor.getColumnIndex("endDate"));

            allTermNames.add(termTitle);
        }

        return allTermNames;
    }

    public ArrayList<String> readTermDates (String sqlStatement){

        ArrayList<String> allTermDates = new ArrayList<String>();

        //int ID;
        String termStartDate;
        String termEndDate;
        //String startDate;
        //String endDate;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlStatement, null);

        while(cursor.moveToNext()){

            //ID = cursor.getInt(cursor.getColumnIndex("termId"));
            termStartDate = cursor.getString(cursor.getColumnIndex("startDate"));
            termEndDate = cursor.getString(cursor.getColumnIndex("endDate"));
            //startDate = cursor.getString(cursor.getColumnIndex("startDate"));
            //endDate = cursor.getString(cursor.getColumnIndex("endDate"));

            allTermDates.add(termStartDate + " - " + termEndDate);
        }

        return allTermDates;
    }

    public ArrayList<String> readCourseDates (String sqlStatement){

        ArrayList<String> allCourseDates = new ArrayList<String>();

        //int ID;
        String courseStartDate;
        String courseEndDate;
        //String startDate;
        //String endDate;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlStatement, null);

        while(cursor.moveToNext()){

            //ID = cursor.getInt(cursor.getColumnIndex("termId"));
            courseStartDate = cursor.getString(cursor.getColumnIndex("startDate"));
            courseEndDate = cursor.getString(cursor.getColumnIndex("endDate"));
            //startDate = cursor.getString(cursor.getColumnIndex("startDate"));
            //endDate = cursor.getString(cursor.getColumnIndex("endDate"));

            allCourseDates.add(courseStartDate + " - " + courseEndDate);
        }

        return allCourseDates;
    }

    public ArrayList<String> readAssessDates (String sqlStatement){

        ArrayList<String> allAssessDates = new ArrayList<String>();

        //int ID;
        String assessGoalDate;
        String assessEndDate;
        //String startDate;
        //String endDate;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlStatement, null);

        while(cursor.moveToNext()){

            //ID = cursor.getInt(cursor.getColumnIndex("termId"));
            assessGoalDate = cursor.getString(cursor.getColumnIndex("goalDate"));
            assessEndDate = cursor.getString(cursor.getColumnIndex("endDate"));
            //startDate = cursor.getString(cursor.getColumnIndex("startDate"));
            //endDate = cursor.getString(cursor.getColumnIndex("endDate"));

            allAssessDates.add("Goal Date: " + assessGoalDate + "\nExpected End Date: " + assessEndDate);
        }

        return allAssessDates;
    }

    //////////////////ADDING RECORDS////////////////////////////
    public int IDSelectionLoop (String sqlStatement, String columnName){
        //IT WORKS BUT RETEST
        int newID = 1;
        int counter = 1;
        int i = 1;

        String TAG = "IDCounterCheck";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlStatement, null);

        while(cursor.moveToNext()){
                int IDNumber = cursor.getInt(cursor.getColumnIndex(columnName));
                if(i == IDNumber){
                    /////MEANS ID NUMBER IS TAKEN
                    Log.d(TAG, "number: " + IDNumber + " is in use.");
                    i++;
                    counter++;
                }else{
                    /////MEANS ID IS AVAILABLE
                    counter++;
                }
            }

            if(counter == i){
                newID = i++;
            }else{
                newID = i;
            }

        Log.d(TAG, "number: " + newID + " WILL be used.");

        return newID;
    }

    public void addTermRecordinSQL(int termID, String termTitle, String startDate, String endDate, int alertActive){

        //int availableID = IDSelectionLoop("SELECT termId FROM Term_tbl;");

        addRecordTermTbl(termID, termTitle, startDate, endDate, alertActive);
    }

    public void addAssessRecordinSQL(int assessID, int courseID, String name, String type, String goalDate
                                    , String startDate, String endDate, int alertActive){

        addRecordAssessTbl(assessID, courseID, name, type, goalDate, startDate, endDate, alertActive);
    }

    public void addCourseRecordinSQL(int courseID, int termID, int mentorID, String courseTitle,
                                     String startDate, String endDate, String status, int alertActive){

        addRecordCourseTbl(courseID, termID, mentorID, courseTitle, startDate, endDate, status, alertActive);
    }

    public void addMentorRecordinSQL(int mentorID, String mentorName, String phone, String email){

        addRecordMentorTbl(mentorID, mentorName, phone, email);
    }

    public void addNoteRecordinSQL(int noteID, int courseID, String note){

        addRecordNotesTbl(noteID, courseID, note);
    }

    /////////////UPDATING RECORDS AND TABLES:///////////////////


    public void updateRecord(String sqlStatement){
        this.getWritableDatabase().execSQL(sqlStatement);
    }

    public void deleteTable(String tableName){

        this.getWritableDatabase().execSQL("DROP TABLE " + tableName);
    }

    public long updateRecordTermTbl(int termID, String termTitle, String startDate, String endDate,
                                        String whereClause, String[] whereArgs){

        ///////ADDED TERMID PARAMETER, CHECK FOR ERRORS IF AUTOINCREMENT IN SQL DOES NOT ALLOW THIS
        //////IF ALLOWED, ADD TO OTHER ADD METHODS

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("termId", termID);
        values.put("termTitle", termTitle);
        values.put("startDate", startDate);
        values.put("endDate", endDate);

        return db.update("Term_tbl", values, whereClause, whereArgs);

    }

    public long updateRecordAssessmentTbl(int assessID, int courseID, String name, String type, String goalDate,
                                          String startDate, String endDate, String whereClause, String[] whereArgs){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("assessmentId", assessID);
        values.put("courseId", courseID);
        values.put("name", name);
        values.put("type", type);
        values.put("goalDate", goalDate);
        values.put("startDate", startDate);
        values.put("endDate", endDate);

        return db.update("Assessment_tbl", values, whereClause, whereArgs);

    }

    public long updateRecordCourseTbl(int courseID, int termID, int mentorID, String courseTitle,
                                   String startDate, String endDate, String status,
                                      String whereClause, String[] whereArgs){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("courseId", courseID);
        values.put("termId", termID);
        values.put("mentorId", mentorID);
        values.put("courseTitle", courseTitle);
        values.put("startDate", startDate);
        values.put("endDate", endDate);
        values.put("status", status);

        return db.update("Course_tbl", values, whereClause, whereArgs);

    }

    public long updateRecordMentorTbl(int mentorID, String mentorName, String phone, String email,
                                      String whereClause, String[] whereArgs){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("mentorId", mentorID);
        values.put("mentorName", mentorName);
        values.put("phone", phone);
        values.put("email", email);

        return db.update("Mentor_tbl", values, whereClause, whereArgs);

    }

    public long updateRecordNotesTbl(int noteID, int courseID, String note, String whereClause, String[] whereArgs){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("noteId", noteID);
        values.put("courseId", courseID);
        values.put("note", note);

        return db.update("Note_tbl", values, whereClause, whereArgs);

    }

    //////////////DATABASE CREATIONS:///////////////////////////

    public void createMentorTable(SQLiteDatabase db){

       db.execSQL("CREATE TABLE IF NOT EXISTS Mentor_tbl"
                + " (mentorId INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "mentorName VARCHAR(50),"
                + "phone VARCHAR(20),"
                + "email VARCHAR(50))");
    }

    public void createTermTable(SQLiteDatabase db){

        db.execSQL("CREATE TABLE IF NOT EXISTS Term_tbl"
                + " (termId INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "termTitle VARCHAR(50),"
                + "startDate DATE,"
                + "endDate DATE,"
                + "alertActive INTEGER DEFAULT 0)");
    }

    public void createCourseTable(SQLiteDatabase db){

        db.execSQL("CREATE TABLE IF NOT EXISTS Course_tbl"
                + " (courseId INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "termId INTEGER,"
                + "mentorId INTEGER,"
                + "courseTitle TEXT,"
                + "startDate DATE,"
                + "endDate DATE,"
                + "status VARCHAR(50),"
                + "alertActive INTEGER DEFAULT 0)");
    }

    public void createAssessTable(SQLiteDatabase db){

        db.execSQL("CREATE TABLE IF NOT EXISTS Assessment_tbl"
                + " (assessmentId INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "courseId INTEGER,"
                + "name TEXT,"
                + "type VARCHAR(50),"
                + "goalDate DATE,"
                + "startDate DATE,"
                + "endDate DATE,"
                + "alertActive INTEGER DEFAULT 0)");
    }

    public void createNoteTable(SQLiteDatabase db){

        db.execSQL("CREATE TABLE IF NOT EXISTS Note_tbl"
                + " (noteId INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "courseId INTEGER,"
                + "note TEXT)");

    }

    public long addRecordTermTbl(int termID, String termTitle, String startDate, String endDate, int alertActive){

        ///////ADDED TERMID PARAMETER, CHECK FOR ERRORS IF AUTOINCREMENT IN SQL DOES NOT ALLOW THIS
        //////IF ALLOWED, ADD TO OTHER ADD METHODS

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("termId", termID);
        values.put("termTitle", termTitle);
        values.put("startDate", startDate);
        values.put("endDate", endDate);
        values.put ("alertActive", alertActive);

        return db.insert("Term_tbl", null, values);

    }

    public long addRecordAssessTbl(int assessID, int courseID, String name, String type,
                                   String goalDate, String startDate, String endDate, int alertActive){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("assessmentId", assessID);
        values.put("courseId", courseID);
        values.put("name", name);
        values.put("type", type);
        values.put("goalDate", goalDate);
        values.put("startDate", startDate);
        values.put("endDate", endDate);
        values.put ("alertActive", alertActive);

        return db.insert("Assessment_tbl", null, values);

    }

    public long addRecordMentorTbl(int mentorID, String mentorName, String phone, String email){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("mentorId", mentorID);
        values.put("mentorName", mentorName);
        values.put("phone", phone);
        values.put("email", email);

        return db.insert("Mentor_tbl", null, values);

    }

    public long addRecordCourseTbl(int courseID, int termID, int mentorID, String courseTitle,
                                   String startDate, String endDate, String status, int alertActive){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("courseId", courseID);
        values.put("termId", termID);
        values.put("mentorId", mentorID);
        values.put("courseTitle", courseTitle);
        values.put("startDate", startDate);
        values.put("endDate", endDate);
        values.put("status", status);
        values.put ("alertActive", alertActive);

        return db.insert("Course_tbl", null, values);

    }

    public long addRecordNotesTbl(int noteID, int courseID, String note){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("noteId", noteID);
        values.put("courseId", courseID);
        values.put("note", note);

        return db.insert("Note_tbl", null, values);

    }

    ////////////////UPDATE ALERT ACTIVE COLUMN/////////////////////

    public void updateTermAlertActive(int alertActive, String termTitle){
        this.getWritableDatabase().execSQL("UPDATE Term_tbl \n" +
                "SET alertActive = " + alertActive + "\n" +
                "WHERE termTitle = \"" + termTitle + "\"");
    }

    public void updateCourseAlertActive(int alertActive, String courseTitle){
        this.getWritableDatabase().execSQL("UPDATE Course_tbl \n" +
                "SET alertActive = " + alertActive + "\n" +
                "WHERE courseTitle = \"" + courseTitle + "\"");
    }

    public void updateAssessAlertActive(int alertActive, String assessName){
        this.getWritableDatabase().execSQL("UPDATE Assessment_tbl \n" +
                "SET alertActive = " + alertActive + "\n" +
                "WHERE name = \"" + assessName + "\"");
    }

}
