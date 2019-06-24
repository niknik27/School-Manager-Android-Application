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

public class CourseListActivity extends ListActivity {

    DBHelper myHelper;

    FloatingActionButton addCourseButton;

    private static final String TAG = "CourseListCheck";
    private static ArrayList<String> allCourses = new ArrayList<String>();
    private static ArrayList<String> allCourseDates = new ArrayList<String>();
    ArrayAdapter<String> courseAdapter;

    private boolean addClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        myHelper = new DBHelper(CourseListActivity.this);



        populateList();

        setCourseSelection();

        addCourseButtonClick();

        bottomNavigation();
    }

    public void setCourseSelection(){

        //this detects assessment name that user selected and will store it in class for "termdetailsactivity.java" and changes activity
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                LinearLayout ll = (LinearLayout) view; // get the parent layout view
                TextView tv = (TextView) ll.findViewById(R.id.label); // get the child text view
                String courseName = (tv.getText().toString());

                CourseAndMentor.setSelection(courseName);
                CourseAndMentor.setAddClickBool(false);

                Toast.makeText(getApplicationContext(), CourseAndMentor.getSelection(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CourseListActivity.this, CourseAndMentorDetailsActivity.class);
                startActivity(intent);
            }
        });
    }

    public void populateList(){

        //populates the list on this screen by accessing the course table in the local database
        allCourses = myHelper.readCourseRecordName("SELECT courseTitle FROM Course_tbl, Term_tbl, Mentor_tbl" +
                "\nWHERE Course_tbl.termId = Term_tbl.termId " +
                "\nAND Course_tbl.mentorId = Mentor_tbl.mentorId");

        allCourseDates = myHelper.readCourseDates("SELECT Course_tbl.startDate, Course_tbl.endDate FROM Course_tbl, Term_tbl, Mentor_tbl" +
                "\nWHERE Course_tbl.termId = Term_tbl.termId " +
                "\nAND Course_tbl.mentorId = Mentor_tbl.mentorId");



        // Create the ArrayAdapter using our "courses" string array.
        // The "android.R.layout.simple_list_item_1" defines the button layout.
        courseAdapter = new CustomListAdapter(this, allCourses, allCourseDates);

        // Pass our adapter to the ListView (we extended the ListActivity class)
        //setListAdapter(courseAdapter);

        setListAdapter(courseAdapter);
    }

    ////when button is clicked, a variable accessed by "assessmentDetails" screen is changed to show that it was pressed
    ////then the screen changes to assessment details
    public boolean addCourseButtonClick(){

        addCourseButton = findViewById(R.id.addCourseButton);
        addCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //      .setAction("Action", null).show();

                boolean termAvaialable = checkAvailableTerms();

                if(termAvaialable == true){

                    String courseName = addCourseItem();

                    CourseAndMentor.setSelection(courseName);
                    populateList();
                    boolean clicked = true;

                    CourseAndMentor.setAddClickBool(clicked);
                    Toast.makeText(getApplicationContext(), courseName, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CourseListActivity.this, CourseAndMentorDetailsActivity.class);
                    startActivity(intent);

                }else{

                    Toast.makeText(getApplicationContext(), "NO term to add course to", Toast.LENGTH_SHORT).show();
                }


            }
        });


        return addClicked;
    }

    //when the add button is pressed, it creates a new assessment gameobject with default values
    public String addCourseItem(){

        int clickCounter = myHelper.IDSelectionLoop("SELECT courseId FROM Course_tbl;", "courseId");

        String courseName = "(Default) Course " + clickCounter;
        String mentorName = "(Default) Mentor " + clickCounter;

        allCourses.add(courseName);

        myHelper.addCourseRecordinSQL(clickCounter, 1, clickCounter, courseName, "Start Date:",
                                "End Date:","Status", 0);

        myHelper.addMentorRecordinSQL(clickCounter, mentorName, "Phone:", "Email:");

        return courseName;

    }

    public boolean checkAvailableTerms(){

        ArrayList<Term> Terms = new ArrayList<Term>();

        Terms = myHelper.readTermObjects("SELECT * FROM Term_tbl");

        if(Terms.isEmpty() == true){

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
                        //Toast.makeText(CourseListActivity.this, "Terms", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CourseListActivity.this, TermListActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_courses:
                        //Toast.makeText(CourseListActivity.this, "Courses", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navigation_home:
                        //Toast.makeText(CourseListActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(CourseListActivity.this, MainActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.navigation_assessments:
                        //Toast.makeText(CourseListActivity.this, "Assessments", Toast.LENGTH_SHORT).show();
                        Intent intent3 = new Intent(CourseListActivity.this, AssessmentListActivity.class);
                        startActivity(intent3);
                        break;
                }
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.navigation_courses);

    }


    /** Called when the user taps the Terms button */
    /*public void termListButtonPress(View view) {
        // Do something in response to button
        Intent intent = new Intent(this,TermListActivity.class);
        startActivity(intent);
    }

    *//** Called when the user taps the Assessments button *//*
    public void assessListButtonPress(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, AssessmentListActivity.class);
        startActivity(intent);
    }

    *//** Called when the user taps the Home button *//*
    public void homeButtonPress(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }*/

}
