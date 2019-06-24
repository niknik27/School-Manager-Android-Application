package com.example.WGUC196Project;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

//controls the assessment list screen of the application

public class AssessmentListActivity extends ListActivity {

    DBHelper myHelper;

    FloatingActionButton addAssessButton;

    private static final String TAG = "AssessListCheck";
    private static ArrayList<String> allAssessNames = new ArrayList<String>();
    private static ArrayList<String> allAssessGoalNEndDates = new ArrayList<String>();

    ArrayAdapter<String> assessmentAdapter;

    private boolean addClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_list);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        myHelper = new DBHelper(AssessmentListActivity.this);

        addAssessButtonClick();

        populateList();
        setAssessSelection();

        bottomNavigation();
    }

    public void setAssessSelection(){

        //this detects assessment name that user selected and will store it in class for "termdetailsactivity.java" and changes activity
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                LinearLayout ll = (LinearLayout) view; // get the parent layout view
                TextView tv = (TextView) ll.findViewById(R.id.label); // get the child text view
                String assessName = tv.getText().toString();

                Assessment.setSelection(assessName);
                Assessment.setAddClickBool(false);

                Toast.makeText(getApplicationContext(), assessName, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AssessmentListActivity.this, AssessmentDetailsActivity.class);
                startActivity(intent);
            }
        });
    }

    public void populateList(){

        //populates the list on this screen by accessing the assessment table in the local database
        allAssessNames = myHelper.readAssessRecordName("SELECT name FROM Assessment_tbl, Course_tbl " +
                                                        "\nWHERE Assessment_tbl.courseId = Course_tbl.courseId");

        allAssessGoalNEndDates = myHelper.readAssessDates("SELECT Assessment_tbl.goalDate, Assessment_tbl.endDate " +
                                                                "FROM Assessment_tbl, Course_tbl " +
                                                                "\nWHERE Assessment_tbl.courseId = Course_tbl.courseId");

        // Create the ArrayAdapter using our "courses" string array.
        // The "android.R.layout.simple_list_item_1" defines the button layout.
        assessmentAdapter = new CustomListAdapter(this, allAssessNames, allAssessGoalNEndDates);

        // Pass our adapter to the ListView (we extended the ListActivity class)
        setListAdapter(assessmentAdapter);

    }

    ////when button is clicked, a variable accessed by "assessmentDetails" screen is changed to show that it was pressed
    ////then the screen changes to assessment details
    public void addAssessButtonClick(){

        addAssessButton = findViewById(R.id.addAssessButton);
        addAssessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //      .setAction("Action", null).show();
                boolean courseAvailable = checkAvailableCourses();

                ///checks if there are any courses for the assessment to attach to
                //if there are no avaiblable courses, a toast pops up and informs the user
                if(courseAvailable == true){

                    String assessName = addAssessItem();

                    Assessment.setSelection(assessName);
                    populateList();
                    boolean clicked = true;

                    Assessment.setAddClickBool(clicked);
                    Toast.makeText(getApplicationContext(), assessName, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AssessmentListActivity.this, AssessmentDetailsActivity.class);
                    startActivity(intent);

                }else{

                    Toast.makeText(getApplicationContext(), "NO course to add assessment to", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    //when the add button is pressed, it creates a new assessment gameobject with default values
    public String addAssessItem(){

        int clickCounter = myHelper.IDSelectionLoop("SELECT assessmentId FROM Assessment_tbl;", "assessmentId");

        String assessName = "(Default) Assessment " + clickCounter;

        allAssessNames.add(assessName);

        myHelper.addAssessRecordinSQL(clickCounter, 1, assessName, "Type", "Goal Date:", "Start Date: ", "Expected End Date: ", 0);

        return assessName;

    }

    public boolean checkAvailableCourses(){

        ArrayList<CourseAndMentor> CourseAndMentors = new ArrayList<CourseAndMentor>();

        CourseAndMentors = myHelper.readCourseAndMentorObjects("SELECT courseId, Course_tbl.termId, Course_tbl.mentorId, \n" +
                "\t\t\t\tcourseTitle, Course_tbl.startDate, Course_tbl.endDate, \n" +
                "\t\t\t\t\ttermTitle, status, mentorName, phone, email, Course_tbl.alertActive\n" +
                "FROM Course_tbl, Mentor_tbl, Term_tbl\n" +
                "WHERE Course_tbl.termId = Term_tbl.termId\n" +
                "AND Course_tbl.mentorId = Mentor_tbl.mentorId");

        if(CourseAndMentors.isEmpty() == true){

            return false;

        }else{

            return true;

        }

    }

    public void bottomNavigation(){

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.navigation_terms:
                        //Toast.makeText(AssessmentListActivity.this, "Terms", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AssessmentListActivity.this, TermListActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_courses:
                        //Toast.makeText(AssessmentListActivity.this, "Courses", Toast.LENGTH_SHORT).show();
                        Intent intent3 = new Intent(AssessmentListActivity.this, CourseListActivity.class);
                        startActivity(intent3);
                        break;
                    case R.id.navigation_home:
                        //Toast.makeText(AssessmentListActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(AssessmentListActivity.this, MainActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.navigation_assessments:
                        //Toast.makeText(AssessmentListActivity.this, "Assessments", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.navigation_assessments);

    }

    /** Called when the user taps the Terms button */
    /*public void termListButtonPress(View view) {
        // Do something in response to button
        Intent intent = new Intent(this,TermListActivity.class);
        startActivity(intent);
    }

    *//** Called when the user taps the Courses button *//*
    public void courseListButtonPress(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, CourseListActivity.class);
        startActivity(intent);
    }

    *//** Called when the user taps the Home button *//*
    public void homeButtonPress(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }*/

}
