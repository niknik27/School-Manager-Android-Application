package com.example.WGUC196Project;


import android.app.Dialog;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TermListActivity extends ListActivity {

    static final String[] MOBILE_OS =
            new String[] { "Android", "iOS", "WindowsMobile", "Blackberry"};

    DBHelper myHelper;

    FloatingActionButton addTermButton;

    private static final String TAG = "TermListCheck";
    private static ArrayList<String> allTerms = new ArrayList<String>();
    private static ArrayList<String> allTermDates = new ArrayList<String>();
    ArrayAdapter<String> courseAdapter;
    //MyListView listAdapter;

    private boolean addClicked;

    ListView termList;

    //int clickCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_list);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        myHelper = new DBHelper(TermListActivity.this);


        //myHelper.updateRecord("DELETE FROM term_tbl WHERE termTitle = \"(Default) Term 5\"");


        populateList();
        setTermSelection();

        bottomNavigation();
        addTermButtonClick();
    }

    protected void onResume(){
        super.onResume();

    }

    public void setTermSelection(){

        //THIS DETECTS TERM NAME THAT USER SELECTED AND WILL STORE IT IN CLASS FOR "TermDetailsActivity.java" AND CHANGES ACTIVITY
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                LinearLayout ll = (LinearLayout) view; // get the parent layout view
                TextView tv = (TextView) ll.findViewById(R.id.label); // get the child text view
                String termName = (tv.getText().toString());

                Term.setSelection(termName);

                Term.setAddClickBool(false);

                Toast.makeText(getApplicationContext(), termName, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TermListActivity.this, TermDetailsActivity.class);
                startActivity(intent);
            }
        });
    }

    public void populateList(){

        allTerms = myHelper.readTermRecordName("SELECT termTitle FROM Term_tbl");
        allTermDates = myHelper.readTermDates("SELECT startDate, endDate FROM Term_tbl");


        // Create the ArrayAdapter using our "courses" string array.
        // The "android.R.layout.simple_list_item_1" defines the button layout.

        courseAdapter = new CustomListAdapter(this, allTerms, allTermDates);

        //listAdapter =  new MyListView(this, allTerms , "Subtitle Text",R.mipmap.ic_arrowdetails_foreground);

        // Pass our adapter to the ListView (we extended the ListActivity class)
        //setListAdapter(courseAdapter);

        setListAdapter(courseAdapter);

    }

    public void bottomNavigation(){

            BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationView);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {

                        case R.id.navigation_terms:
                            //Toast.makeText(TermListActivity.this, "Terms", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.navigation_courses:
                            //Toast.makeText(TermListActivity.this, "Courses", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(TermListActivity.this, CourseListActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.navigation_home:
                            //Toast.makeText(TermListActivity.this, "Home", Toast.LENGTH_SHORT).show();
                            Intent intent2 = new Intent(TermListActivity.this, MainActivity.class);
                            startActivity(intent2);
                            break;
                        case R.id.navigation_assessments:
                            //Toast.makeText(TermListActivity.this, "Assessments", Toast.LENGTH_SHORT).show();
                            Intent intent3 = new Intent(TermListActivity.this, AssessmentListActivity.class);
                            startActivity(intent3);
                            break;
                    }
                    return true;
                }
            });

            bottomNavigationView.setSelectedItemId(R.id.navigation_terms);

    }

    public boolean addTermButtonClick(){

        addTermButton = findViewById(R.id.addTermButton);
        addTermButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //      .setAction("Action", null).show();

                String termName = addTermItem();

                Term.setSelection(termName);
                //courseAdapter.notifyDataSetChanged();
                populateList();

                boolean clicked = true;

                Term.setAddClickBool(clicked);
                Toast.makeText(getApplicationContext(), termName, Toast.LENGTH_SHORT).show();
                Log.d("Names", termName);
                Intent intent = new Intent(TermListActivity.this, TermDetailsActivity.class);
                startActivity(intent);

            }
        });


        return addClicked;
    }

    public String addTermItem(){

        int clickCounter = myHelper.IDSelectionLoop("SELECT termId FROM Term_tbl;", "termId");

        String termName = "(Default) Term " + clickCounter;

        allTerms.add(termName);

        myHelper.addTermRecordinSQL(clickCounter, termName, "Start Date:", "End Date:", 0);

        return termName;

    }


    /** Called when the user taps the Courses button */
    /*public void courseListButtonPress(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, CourseListActivity.class);
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
