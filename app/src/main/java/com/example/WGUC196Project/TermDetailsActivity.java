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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

//this is to control the Term Details screen

public class TermDetailsActivity extends AppCompatActivity {

    Term selectedTerm = null;

    DBHelper myHelper;
    TermListActivity termListActivity;

    TextView errorMessage;

    ListView courseList;

    EditText termTitleEditText;
    EditText startDateEditText;
    EditText endDateEditText;

    BottomNavigationView bottomNavigationView;
    BottomNavigationView editBottomNavigationView;
    BottomNavigationView addBottomNavigationView;

    Switch alertActiveSwitch;

    TextView termDetailsTitle;

    private TextWatcher mStartDateEntryWatcher;
    private TextWatcher mEndDateEntryWatcher;

    private boolean addTermButtonClicked;

    private static ArrayList<String> coursesInTerm = new ArrayList<String>();
    ArrayAdapter<String> courseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_details);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        myHelper = new DBHelper(TermDetailsActivity.this);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationView);
        editBottomNavigationView = (BottomNavigationView) findViewById(R.id.editNavigationView);
        addBottomNavigationView = (BottomNavigationView) findViewById(R.id.addNavigationView);

        addTermButtonClicked = false;

        errorMessage = (TextView) findViewById(R.id.errorMessage);

        termTitleEditText = (EditText) findViewById(R.id.termTitleEditText);
        startDateEditText = (EditText) findViewById(R.id.startDateEditText);
        endDateEditText = (EditText) findViewById(R.id.endDateEditText);

        courseList = (ListView) findViewById(R.id.courseListView);

        alertActiveSwitch = (Switch) findViewById(R.id.alertSwitch);

        termDetailsTitle = (TextView) findViewById(R.id.termDetailsTitle);

        //editButton = (Button) findViewById(R.id.editButton);

        //deleteButton = (Button) findViewById(R.id.deleteButton);

        errorMessage.setVisibility(View.GONE);

        showTermDetails();
        populateCourseList();
        justifyListViewHeightBasedOnChildren(courseList);

        createStartDateTextWatcher();
        createEndDateTextWatcher();

        startDateEditText.addTextChangedListener(mStartDateEntryWatcher);
        endDateEditText.addTextChangedListener(mEndDateEntryWatcher);

        addTermButtonClicked = Term.getAddClickBool();

        bottomNavigation();
        addBottomNavigation();
        editBottomNavigation();

        //checks if the "add term" button is clicked from the "term list" screen
        //if it was clicked, the assessment details screen enables the appropriate buttons for adding a new term
        if(addTermButtonClicked == true){

            turnOnEditStyle();

            termDetailsTitle.setText("Add Term");

            addBottomNavigationView.setVisibility(View.VISIBLE);
            editBottomNavigationView.setVisibility(View.GONE);
            bottomNavigationView.setVisibility(View.GONE);

            //if the add term button was NOT pressed, the screen shows the details of the selected term
        }else{

            turnOffEditStyle();

            termDetailsTitle.setText("Term Details");

            termTitleEditText.setEnabled(false);
            startDateEditText.setEnabled(false);
            endDateEditText.setEnabled(false);

            addBottomNavigationView.setVisibility(View.GONE);
            editBottomNavigationView.setVisibility(View.GONE);
            bottomNavigationView.setVisibility(View.VISIBLE);

        }

        setCourseSelection();
        alertToggle();

        termTitleEditText.setSelectAllOnFocus(true);
        startDateEditText.setSelectAllOnFocus(true);
        endDateEditText.setSelectAllOnFocus(true);
    }

    @Override
    public void onBackPressed(){

    }

    public void showTermDetails(){

        selectedTerm = myHelper.returnTerm();

        //sets the textboxes with data of the selected term object
        termTitleEditText.setText(selectedTerm.getTermTitle());
        startDateEditText.setText(selectedTerm.getStartDate());
        endDateEditText.setText(selectedTerm.getEndDate());

        //this checks if the selected term's notifications were previously activated or not
        if(selectedTerm.getAlertActive() == 1){

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

                    Toast.makeText(getApplicationContext(), "Alerts turned ON for: " + selectedTerm.getTermTitle(), Toast.LENGTH_SHORT).show();
                    myHelper.updateTermAlertActive(1, selectedTerm.getTermTitle());

                }else{

                    Toast.makeText(getApplicationContext(), "Alerts turned OFF for: " + selectedTerm.getTermTitle(), Toast.LENGTH_SHORT).show();
                    myHelper.updateTermAlertActive(0, selectedTerm.getTermTitle());
                }
                //Log.d("Names", termName);

            }
        });
    }

    ///the assoociated courses in the term are listed in its details using this
    public void populateCourseList(){

        coursesInTerm = myHelper.readCourseRecordName("SELECT courseTitle FROM Course_tbl, Term_tbl, Mentor_tbl" +
                "\nWHERE Term_tbl.termId = " + selectedTerm.getID() +
                "\nAND Course_tbl.termId = Term_tbl.termId " +
                "\nAND Course_tbl.mentorId = Mentor_tbl.mentorId");

        // Create the ArrayAdapter using our "courses" string array.
        // The "android.R.layout.simple_list_item_1" defines the button layout.
        courseAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, coursesInTerm);

        // Pass our adapter to the ListView (we extended the ListActivity class)
        courseList.setAdapter(courseAdapter);

    }


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

    //detects the course the user selects and changes the screen to "CourseDetailsActivity"
    public void setCourseSelection(){

        courseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String courseName = ((TextView) view).getText().toString();

                CourseAndMentor.setSelection(courseName);
                CourseAndMentor.setAddClickBool(false);

                //Toast.makeText(getApplicationContext(), CourseAndMentor.getSelection(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TermDetailsActivity.this, CourseAndMentorDetailsActivity.class);
                startActivity(intent);
            }
        });
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
                        Intent intent2 = new Intent(TermDetailsActivity.this, TermListActivity.class);
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

    /////////EDIT BUTTON METHOD//////////////////

    public void edit(){

        turnOnEditStyle();

        termDetailsTitle.setText("Edit Term");

        termTitleEditText.setEnabled(true);
        startDateEditText.setEnabled(true);
        endDateEditText.setEnabled(true);

        editBottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.setVisibility(View.GONE);
    }

    //////FOR ADDING TERMS///////////////

    public void cancelAdd(){

        String termTitle = termTitleEditText.getText().toString();

        myHelper.updateRecord("DELETE FROM term_tbl WHERE termTitle = \"" + termTitle + "\"");
        Intent intent = new Intent(TermDetailsActivity.this, TermListActivity.class);
        startActivity(intent);
    }

    public void saveAdd(){

        int termID = selectedTerm.getID();
        String newTermTitle = termTitleEditText.getText().toString();
        String newStartDate = startDateEditText.getText().toString();
        String newEndDate = endDateEditText.getText().toString();

        String[] whereArgs = {Integer.toString(termID)};

        myHelper.updateRecordTermTbl(termID, newTermTitle, newStartDate, newEndDate, "termId = ?", whereArgs);

        Intent intent = new Intent(TermDetailsActivity.this, TermListActivity.class);
        startActivity(intent);
    }

    //////FOR EDITING TERMS///////////////

    public void cancelEdit(){

        showTermDetails();
        turnOffEditStyle();

        termDetailsTitle.setText("Term Details");

        termTitleEditText.setEnabled(false);
        startDateEditText.setEnabled(false);
        endDateEditText.setEnabled(false);

        editBottomNavigationView.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    public void saveEdit(){

        int termID = selectedTerm.getID();
        String newTermTitle = termTitleEditText.getText().toString();
        String newStartDate = startDateEditText.getText().toString();
        String newEndDate = endDateEditText.getText().toString();

        String[] whereArgs = {Integer.toString(termID)};

        myHelper.updateRecordTermTbl(termID, newTermTitle, newStartDate, newEndDate, "termId = ?", whereArgs);

        Intent intent = new Intent(TermDetailsActivity.this, TermListActivity.class);
        startActivity(intent);
    }

    ///////////////////DELETE TERMS///////////////////

    public void delete(){
        if(coursesInTerm.isEmpty()){

            AlertDialog.Builder builder = new AlertDialog.Builder(TermDetailsActivity.this);
            builder.setCancelable(true);
            builder.setTitle("CONFIRM DELETE");
            builder.setMessage("Are you sure you would like to delete this Term?");
            builder.setPositiveButton("Confirm",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            myHelper.updateRecord("DELETE FROM Term_tbl WHERE termId = " + selectedTerm.getID());
                            Intent intent = new Intent(TermDetailsActivity.this, TermListActivity.class);
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

        }else{

            ////terms are NOT allowed to be deleted when there are courses in it
            //deleteImageButton.setError("ERROR!");
            errorMessage.setVisibility(View.VISIBLE);
            errorMessage.setText("Cannot delete term because there are courses in it.");
        }
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
                        startDateEditText.setText(working);
                        startDateEditText.setSelection(working.length());
                    }
                }else if (working.length()== 5 && before == 0){
                    String enteredDate = working.substring(3);
                    if(Integer.parseInt(enteredDate) < 1 || Integer.parseInt(enteredDate) > 31) {
                        isValid = false;
                    }else{
                        working+="/";
                        startDateEditText.setText(working);
                        startDateEditText.setSelection(working.length());
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
                    startDateEditText.setError("Enter a valid date: MM/DD/YYYY");
                    editBottomNavigationView.getMenu().getItem(2).setCheckable(false);
                    addBottomNavigationView.getMenu().getItem(1).setCheckable(false);

                } else {
                    startDateEditText.setError(null);
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
                        endDateEditText.setText(working);
                        endDateEditText.setSelection(working.length());
                    }
                }else if (working.length()== 5 && before == 0){
                    String enteredDate = working.substring(3);
                    if(Integer.parseInt(enteredDate) < 1 || Integer.parseInt(enteredDate) > 31) {
                        isValid = false;
                    }else{
                        working+="/";
                        endDateEditText.setText(working);
                        endDateEditText.setSelection(working.length());
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
                    endDateEditText.setError("Enter a valid date: MM/DD/YYYY");
                    editBottomNavigationView.getMenu().getItem(2).setCheckable(false);
                    addBottomNavigationView.getMenu().getItem(1).setCheckable(false);

                } else {
                    endDateEditText.setError(null);
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

        termTitleEditText.setBackgroundResource(R.drawable.edit_on_text);
        startDateEditText.setBackgroundResource(R.drawable.edit_on_text);
        endDateEditText.setBackgroundResource(R.drawable.edit_on_text);

    }

    public void turnOffEditStyle(){

        termTitleEditText.setBackgroundResource(R.drawable.edit_off_text);
        startDateEditText.setBackgroundResource(R.drawable.edit_off_text);
        endDateEditText.setBackgroundResource(R.drawable.edit_off_text);

    }


}
