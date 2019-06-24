package com.example.WGUC196Project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;

//this controls the course and mentor details screen

public class CourseAndMentorDetailsActivity extends AppCompatActivity {

    CourseAndMentor selectedCourse = null;
    DBHelper myHelper;

    ListView assessmentListView;
    ListView noteListView;

    Spinner statusChoice;
    Spinner termChoice;

    EditText courseTitleText;
    EditText startDateText;
    EditText endDateText;
    EditText mentorNameText;
    EditText phoneText;
    EditText emailText;

    Button addAssessButton;

    Button addNoteButton;

    BottomNavigationView bottomNavigationView;
    BottomNavigationView editBottomNavigationView;
    BottomNavigationView addBottomNavigationView;

    Switch alertActiveSwitch;

    TextView courseDetailsTitle;

    private TextWatcher mStartDateEntryWatcher;
    private TextWatcher mEndDateEntryWatcher;

    private boolean addCourseButtonClicked;

    private static ArrayList<String> assessInCourse = new ArrayList<String>();
    ArrayAdapter<String> assessAdapter;

    private static ArrayList<String> notesInCourse = new ArrayList<String>();
    ArrayAdapter<String> notesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_and_mentor_details);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        myHelper = new DBHelper(CourseAndMentorDetailsActivity.this);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationView);
        editBottomNavigationView = (BottomNavigationView) findViewById(R.id.editNavigationView);
        addBottomNavigationView = (BottomNavigationView) findViewById(R.id.addNavigationView);

        assessmentListView = (ListView) findViewById(R.id.assessmentListView);
        noteListView = (ListView) findViewById(R.id.noteListView);

        termChoice = (Spinner) findViewById(R.id.termChoice);
        statusChoice = (Spinner) findViewById(R.id.statusChoice);

        courseTitleText = (EditText) findViewById(R.id.courseTitleText);
        startDateText = (EditText) findViewById(R.id.startDateText);
        endDateText = (EditText) findViewById(R.id.endDateText);
        mentorNameText = (EditText) findViewById(R.id.mentorNameText);
        phoneText = (EditText) findViewById(R.id.phoneText);
        emailText = (EditText) findViewById(R.id.emailText);

        alertActiveSwitch = (Switch) findViewById(R.id.alertSwitch);

        addAssessButton = (Button) findViewById(R.id.addAssessButton);

        addNoteButton = (Button) findViewById(R.id.addNoteButton);

        courseDetailsTitle = (TextView) findViewById(R.id.courseDetailsTitle);

        addCourseButtonClicked = CourseAndMentor.getAddClickBool();

        loadTermSpinnerData();
        loadStatusSpinnerData();

        showCourseAndMentorDetails();
        populateAssessmentList();
        populateNoteList();
        justifyListViewHeightBasedOnChildren(assessmentListView);
        justifyListViewHeightBasedOnChildren(noteListView);

        bottomNavigation();
        addBottomNavigation();
        editBottomNavigation();

        setAssessSelection();
        setNoteSelection();

        createStartDateTextWatcher();
        createEndDateTextWatcher();

        startDateText.addTextChangedListener(mStartDateEntryWatcher);
        endDateText.addTextChangedListener(mEndDateEntryWatcher);

        addAssessButtonClick();
        addNoteButtonClick();

        //checks if the "add course" button is clicked from the "course list" screen
        //if it was clicked, the course details screen enables the appropriate buttons for adding a new course
        if(addCourseButtonClicked == true){

            turnOnEditStyle();

            addBottomNavigationView.setVisibility(View.VISIBLE);
            editBottomNavigationView.setVisibility(View.GONE);
            bottomNavigationView.setVisibility(View.GONE);

            courseDetailsTitle.setText("Add Course");

            //if the add course button was NOT pressed, the screen shows the details of the selected course
        }else{

            turnOffEditStyle();

            courseDetailsTitle.setText("Course Details");

            termChoice.setEnabled(false);
            statusChoice.setEnabled(false);
            courseTitleText.setEnabled(false);
            startDateText.setEnabled(false);
            endDateText.setEnabled(false);
            mentorNameText.setEnabled(false);
            phoneText.setEnabled(false);
            emailText.setEnabled(false);

            addBottomNavigationView.setVisibility(View.GONE);
            editBottomNavigationView.setVisibility(View.GONE);
            bottomNavigationView.setVisibility(View.VISIBLE);
        }

        //editText.setSelectAllOnFocus(true);

        alertToggle();

        //this enables the app to highlight all text in the textbox when selected
        courseTitleText.setSelectAllOnFocus(true);
        startDateText.setSelectAllOnFocus(true);
        endDateText.setSelectAllOnFocus(true);
        mentorNameText.setSelectAllOnFocus(true);
        phoneText.setSelectAllOnFocus(true);
        emailText.setSelectAllOnFocus(true);
    }

    @Override
    public void onBackPressed(){

    }

    public void showCourseAndMentorDetails(){

        selectedCourse = myHelper.returnCourseAndMentor();

        //initializes choices for a drop down box (android calls them spinners)
        //drop down box selection is initialized to the choice made in the selected assessment object
        ArrayAdapter termChoiceAdapter = (ArrayAdapter) termChoice.getAdapter(); //cast to an ArrayAdapter
        int termSpinnerPosition = termChoiceAdapter.getPosition(selectedCourse.getTermTitle());

        ArrayAdapter statusChoiceAdapter = (ArrayAdapter) statusChoice.getAdapter(); //cast to an ArrayAdapter
        int statusSpinnerPosition = statusChoiceAdapter.getPosition(selectedCourse.getStatus());

        //sets the textboxes with data of the selected assessment object
        courseTitleText.setText(selectedCourse.getCourseTitle());
        startDateText.setText(selectedCourse.getStartDate());
        endDateText.setText(selectedCourse.getEndDate());
        mentorNameText.setText(selectedCourse.getMentorName());
        phoneText.setText(selectedCourse.getPhone());
        emailText.setText(selectedCourse.getEmail());


        if(termSpinnerPosition < 0){
            termChoice.setSelection(0);
        }else{
            termChoice.setSelection(termSpinnerPosition);
        }

        if(statusSpinnerPosition < 0){
            statusChoice.setSelection(0);
        }else{
            statusChoice.setSelection(statusSpinnerPosition);
        }

        //this checks if the selected course's notifications were previously activated or not
        if(selectedCourse.getAlertActive() == 1){

            alertActiveSwitch.setChecked(true);

        }else{

            alertActiveSwitch.setChecked(false);
        }
    }

    //toggles the notification alerts of the selected assessment
    public void alertToggle(){

        alertActiveSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //      .setAction("Action", null).show();

                boolean toggled = alertActiveSwitch.isChecked();
                if(toggled == true){

                    Toast.makeText(getApplicationContext(), "Alerts turned ON for: " + selectedCourse.getTermTitle(), Toast.LENGTH_SHORT).show();
                    myHelper.updateCourseAlertActive(1, selectedCourse.getCourseTitle());

                }else{

                    Toast.makeText(getApplicationContext(), "Alerts turned OFF for: " + selectedCourse.getTermTitle(), Toast.LENGTH_SHORT).show();
                    myHelper.updateCourseAlertActive(0, selectedCourse.getCourseTitle());
                }
                //Log.d("Names", termName);

            }
        });
    }

    ///////there are assessments and notes associated with each course
    /////these are populated using these methods
    public void populateAssessmentList(){

        assessInCourse = myHelper.readAssessRecordName("SELECT name FROM Assessment_tbl, Course_tbl \n" +
                                                        "WHERE Assessment_tbl.courseId = Course_tbl.courseId \n" +
                                                        "AND Assessment_tbl.courseId = " + selectedCourse.getCourseID());

        // Create the ArrayAdapter using our "courses" string array.
        // The "android.R.layout.simple_list_item_1" defines the button layout.
        assessAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, assessInCourse);

        // Pass our adapter to the ListView (we extended the ListActivity class)
        assessmentListView.setAdapter(assessAdapter);

    }

    public void populateNoteList(){

        notesInCourse = myHelper.readNotesRecordName("SELECT note FROM Note_tbl WHERE courseId = " + selectedCourse.getCourseID());

        // Create the ArrayAdapter using our "courses" string array.
        // The "android.R.layout.simple_list_item_1" defines the button layout.
        notesAdapter = new ArrayAdapter<String>(this, R.layout.note_list_item, R.id.noteList, notesInCourse);

        // Pass our adapter to the ListView (we extended the ListActivity class)
        noteListView.setAdapter(notesAdapter);

    }

    ///listviews that are inserted in a scroll view will NOT dynamically expand according to the number of items in the arraylist
    //this method is a wrokaround that loops through items first and then initializes the listview with a pre determined length
    public void justifyListViewHeightBasedOnChildren (ListView listView) {

        ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();

        if (adapter == null) {
            return;
        }

        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }


    private void loadTermSpinnerData() {

        ArrayList<String> terms = myHelper.readTermRecordName("SELECT termTitle FROM Term_tbl;");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, terms);

        //ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,R.layout.custom_spinner_style, terms);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        termChoice.setAdapter(dataAdapter);

    }

    private void loadStatusSpinnerData() {

        ArrayList<String> status = new ArrayList<String>();
        status.add("In Progress");
        status.add("Completed");
        status.add("Plan to Take");
        status.add("Dropped");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, status);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        statusChoice.setAdapter(dataAdapter);

    }

    ////////////NAVIGATION BAR METHODS/////////////////

    //there are more than one navigation bars that are enabled and disabled based on the circumstances
    public void bottomNavigation(){

        bottomNavigationView.getMenu().getItem(0).setCheckable(false);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.navigation_edit:
                        //Toast.makeText(AssessmentDetailsActivity.this, "Terms", Toast.LENGTH_SHORT).show();
                        item.setCheckable(true);
                        edit();
                        break;
                    case R.id.navigation_back:
                        //Toast.makeText(AssessmentListActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(CourseAndMentorDetailsActivity.this, CourseListActivity.class);
                        startActivity(intent2);
                        break;
                }
                return true;
            }
        });

    }

    public void addBottomNavigation(){

        addBottomNavigationView.getMenu().getItem(0).setCheckable(false);

        addBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.navigation_cancel:
                        //Toast.makeText(AssessmentListActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        item.setCheckable(true);
                        cancelAdd();
                        break;
                    case R.id.navigation_save:
                        //Toast.makeText(AssessmentListActivity.this, "Assessments", Toast.LENGTH_SHORT).show();
                        saveAdd();
                        break;
                }
                return true;
            }
        });

    }

    public void editBottomNavigation(){

        editBottomNavigationView.getMenu().getItem(0).setCheckable(false);

        editBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.navigation_delete:
                        //Toast.makeText(AssessmentDetailsActivity.this, "Terms", Toast.LENGTH_SHORT).show();
                        item.setCheckable(true);
                        delete();
                        break;
                    case R.id.navigation_cancel:
                        //Toast.makeText(AssessmentListActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        cancelEdit();
                        break;
                    case R.id.navigation_save:
                        //Toast.makeText(AssessmentListActivity.this, "Assessments", Toast.LENGTH_SHORT).show();
                        saveEdit();
                        break;
                }
                return true;
            }
        });

    }

    ///////////EDIT BUTTON METHOD///////////////////

    public void edit(){

        turnOnEditStyle();

        courseDetailsTitle.setText("Edit Course");

        //enables the text boxes to be editable when the edit button is pressed
        termChoice.setEnabled(true);
        statusChoice.setEnabled(true);
        courseTitleText.setEnabled(true);
        startDateText.setEnabled(true);
        endDateText.setEnabled(true);
        mentorNameText.setEnabled(true);
        phoneText.setEnabled(true);
        emailText.setEnabled(true);

        editBottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.setVisibility(View.GONE);
    }

    ////SELECTING AND EDITING ASSESSMENTS AND NOTES////////

    public void setAssessSelection(){

        //this detects assessment name that user selected and will store it in class for "termdetailsactivity.java" and changes activity
        assessmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String assessName = ((TextView) view).getText().toString();

                Assessment.setSelection(assessName);
                Assessment.setAddClickBool(false);

                //Toast.makeText(getApplicationContext(), assessName, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CourseAndMentorDetailsActivity.this, AssessmentDetailsActivity.class);
                startActivity(intent);
            }
        });
    }

    public void setNoteSelection(){

        //this detects note name that user selected and will store it in class for "termdetailsactivity.java" and changes activity
        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String noteText = ((TextView) view).getText().toString();

                TextView txtview= (TextView) view.findViewById(R.id.noteList);
                String noteText = txtview.getText().toString();

                Note.setSelection(noteText);
                Note.setAddClickBool(false);

                //String TAG = "selectedNote";
                //Log.d(TAG, "Note Selection is: " + Note.getSelecton());

                //Toast.makeText(getApplicationContext(), Note.getSelecton(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CourseAndMentorDetailsActivity.this, NoteDetailsActivity.class);
                startActivity(intent);
            }
        });
    }

    //////FOR ADDING COURSES///////////////

    public void cancelAdd(){

        String assessName = courseTitleText.getText().toString();
        String mentorName = mentorNameText.getText().toString();

        myHelper.updateRecord("DELETE FROM Course_tbl WHERE courseTitle = \"" + assessName + "\"");
        myHelper.updateRecord("DELETE FROM Mentor_tbl WHERE mentorName = \"" + mentorName + "\"");

        Intent intent = new Intent(CourseAndMentorDetailsActivity.this, CourseListActivity.class);
        startActivity(intent);
    }


    public void saveAdd(){

        int courseID = selectedCourse.getCourseID();
        int mentorID = selectedCourse.getMentorID();
        String courseTitle = courseTitleText.getText().toString();
        String startDate = startDateText.getText().toString();
        String endDate = endDateText.getText().toString();

        //String status = statusText.getText().toString();

        String status = statusChoice.getSelectedItem().toString();

        //String termTitle = termTitleText.getText().toString();
        String termTitle = termChoice.getSelectedItem().toString();

        String mentorName = mentorNameText.getText().toString();
        String phone = phoneText.getText().toString();
        String email = emailText.getText().toString();

        int newTermID = myHelper.getTermID(termTitle);


        String[] whereArgs = {Integer.toString(courseID)};
        String[] mentorWhereArgs = {Integer.toString(mentorID)};

        myHelper.updateRecordCourseTbl(courseID, newTermID, mentorID, courseTitle, startDate, endDate,
                status,"courseId = ?", whereArgs);

        myHelper.updateRecordMentorTbl(mentorID, mentorName, phone, email, "mentorId = ?", mentorWhereArgs);

        Intent intent = new Intent(CourseAndMentorDetailsActivity.this, CourseListActivity.class);
        startActivity(intent);
    }

    //////for editing courses///////////////

    public void cancelEdit(){

        turnOffEditStyle();

        showCourseAndMentorDetails();

        courseDetailsTitle.setText("Course Details");

        termChoice.setEnabled(false);
        statusChoice.setEnabled(false);
        courseTitleText.setEnabled(false);
        startDateText.setEnabled(false);
        endDateText.setEnabled(false);
        mentorNameText.setEnabled(false);
        phoneText.setEnabled(false);
        emailText.setEnabled(false);

        editBottomNavigationView.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    public void saveEdit(){

        int courseID = selectedCourse.getCourseID();
        int mentorID = selectedCourse.getMentorID();
        String courseTitle = courseTitleText.getText().toString();
        String startDate = startDateText.getText().toString();
        String endDate = endDateText.getText().toString();

        //String status = statusText.getText().toString();

        String status = statusChoice.getSelectedItem().toString();

        //String termTitle = termTitleText.getText().toString();
        String termTitle = termChoice.getSelectedItem().toString();

        String mentorName = mentorNameText.getText().toString();
        String phone = phoneText.getText().toString();
        String email = emailText.getText().toString();

        int newTermID = myHelper.getTermID(termTitle);


        String[] whereArgs = {Integer.toString(courseID)};
        String[] mentorWhereArgs = {Integer.toString(mentorID)};

        myHelper.updateRecordCourseTbl(courseID, newTermID, mentorID, courseTitle, startDate, endDate,
                status,"courseId = ?", whereArgs);

        myHelper.updateRecordMentorTbl(mentorID, mentorName, phone, email, "mentorId = ?", mentorWhereArgs);

        Intent intent = new Intent(CourseAndMentorDetailsActivity.this, CourseListActivity.class);
        startActivity(intent);
    }


    ////////////DELETING COURSES AND ASSESSMENTS////////////

    public void delete(){

        AlertDialog.Builder builder = new AlertDialog.Builder(CourseAndMentorDetailsActivity.this);
        builder.setCancelable(true);
        builder.setTitle("CONFIRM DELETE");
        builder.setMessage("Are you sure you would like to delete course, mentor info, and ALL of its assessments and Notes?");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        myHelper.updateRecord("DELETE FROM Course_tbl WHERE courseId = " + selectedCourse.getCourseID());
                        myHelper.updateRecord("DELETE FROM Mentor_tbl WHERE mentorId = " + selectedCourse.getMentorID());
                        myHelper.updateRecord("DELETE FROM Assessment_tbl WHERE courseId = " + selectedCourse.getCourseID());
                        myHelper.updateRecord("DELETE FROM Note_tbl WHERE courseId = " + selectedCourse.getCourseID());

                        Intent intent = new Intent(CourseAndMentorDetailsActivity.this, CourseListActivity.class);
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //////////ADDING ASSESSMENTS AND NOTES BUTTONS//////////////////

    public void addAssessButtonClick(){

        addAssessButton = findViewById(R.id.addAssessButton);
        addAssessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //      .setAction("Action", null).show();

                String assessName = addAssessItem();

                Assessment.setSelection(assessName);
                assessAdapter.notifyDataSetChanged();

                boolean clicked = true;

                Assessment.setAddClickBool(clicked);
                //Toast.makeText(getApplicationContext(), assessName, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CourseAndMentorDetailsActivity.this, AssessmentDetailsActivity.class);
                startActivity(intent);

            }
        });

    }

    public String addAssessItem(){

        int clickCounter = myHelper.IDSelectionLoop("SELECT assessmentId FROM Assessment_tbl;", "assessmentId");

        String assessName = "(Default) Assessment " + clickCounter;

        assessAdapter.add(assessName);

        myHelper.addAssessRecordinSQL(clickCounter, selectedCourse.getCourseID(), assessName, "Type",
                                "Goal Date:", "Start Date: ", "Expected End Date: ", 0);

        return assessName;

    }

    public void addNoteButtonClick(){

        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //      .setAction("Action", null).show();

                String noteText = addNoteItem();

                Note.setSelection(noteText);
                notesAdapter.notifyDataSetChanged();

                boolean clicked = true;

                Note.setAddClickBool(clicked);
                //Toast.makeText(getApplicationContext(), noteText, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CourseAndMentorDetailsActivity.this, NoteDetailsActivity.class);
                startActivity(intent);

            }
        });
    }

    public String addNoteItem(){

        int clickCounter = myHelper.IDSelectionLoop("SELECT noteId FROM Note_tbl;", "noteId");

        String noteText = "New Note # " + clickCounter;

        notesAdapter.add(noteText);

        myHelper.addNoteRecordinSQL(clickCounter, selectedCourse.getCourseID(), noteText);

        return noteText;

    }

    //these methods watches and forces the date format when the user is typing

    public void createStartDateTextWatcher(){


        mStartDateEntryWatcher = new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String working = s.toString();
                boolean isValid = true;
                if (working.length()==2 && before ==0) {
                    if (Integer.parseInt(working) < 1 || Integer.parseInt(working)>12) {
                        isValid = false;
                    } else {
                        working+="/";
                        startDateText.setText(working);
                        startDateText.setSelection(working.length());
                    }
                }else if (working.length()== 5 && before == 0){
                    String enteredDate = working.substring(3);
                    if(Integer.parseInt(enteredDate) < 1 || Integer.parseInt(enteredDate) > 31) {
                        isValid = false;
                    }else{
                        working+="/";
                        startDateText.setText(working);
                        startDateText.setSelection(working.length());
                    }
                }else if (working.length()==10 && before ==0) {
                    String enteredYear = working.substring(6);
                    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                    if (Integer.parseInt(enteredYear) < currentYear) {
                        isValid = false;
                    }
                } else if (working.length()!=10) {
                    isValid = false;
                }

                if (!isValid) {
                    startDateText.setError("Enter a valid date: MM/DD/YYYY");
                    editBottomNavigationView.getMenu().getItem(2).setCheckable(false);
                    addBottomNavigationView.getMenu().getItem(1).setCheckable(false);
                } else {
                    startDateText.setError(null);
                    editBottomNavigationView.getMenu().getItem(2).setCheckable(true);
                    addBottomNavigationView.getMenu().getItem(1).setCheckable(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        };

    }

    public void createEndDateTextWatcher(){

        mEndDateEntryWatcher = new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String working = s.toString();
                boolean isValid = true;

                if (working.length()==2 && before ==0) {
                    if (Integer.parseInt(working) < 1 || Integer.parseInt(working)>12) {
                        isValid = false;
                    } else {
                        working+="/";
                        endDateText.setText(working);
                        endDateText.setSelection(working.length());
                    }
                }else if (working.length()== 5 && before == 0){
                    String enteredDate = working.substring(3);
                    if(Integer.parseInt(enteredDate) < 1 || Integer.parseInt(enteredDate) > 31) {
                        isValid = false;
                    }else{
                        working+="/";
                        endDateText.setText(working);
                        endDateText.setSelection(working.length());
                    }
                }else if (working.length()==10 && before ==0) {
                    String enteredYear = working.substring(6);
                    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                    if (Integer.parseInt(enteredYear) < currentYear) {
                        isValid = false;
                    }
                } else if (working.length()!=10) {
                    isValid = false;
                }

                if (!isValid) {
                    endDateText.setError("Enter a valid date: MM/DD/YYYY");
                    editBottomNavigationView.getMenu().getItem(2).setCheckable(false);
                    addBottomNavigationView.getMenu().getItem(1).setCheckable(false);
                } else {
                    endDateText.setError(null);
                    editBottomNavigationView.getMenu().getItem(2).setCheckable(true);
                    addBottomNavigationView.getMenu().getItem(1).setCheckable(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        };

    }

    ////////////////CHANGE THE EDIT TEXT STYLES/////////////////

    public void turnOnEditStyle(){

        courseTitleText.setBackgroundResource(R.drawable.edit_on_text);
        startDateText.setBackgroundResource(R.drawable.edit_on_text);
        endDateText.setBackgroundResource(R.drawable.edit_on_text);
        mentorNameText.setBackgroundResource(R.drawable.edit_on_text);
        phoneText.setBackgroundResource(R.drawable.edit_on_text);
        emailText.setBackgroundResource(R.drawable.edit_on_text);

    }

    public void turnOffEditStyle(){

        courseTitleText.setBackgroundResource(R.drawable.edit_off_text);
        startDateText.setBackgroundResource(R.drawable.edit_off_text);
        endDateText.setBackgroundResource(R.drawable.edit_off_text);
        mentorNameText.setBackgroundResource(R.drawable.edit_off_text);
        phoneText.setBackgroundResource(R.drawable.edit_off_text);
        emailText.setBackgroundResource(R.drawable.edit_off_text);


    }

}
