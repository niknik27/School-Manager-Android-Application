package com.example.WGUC196Project;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

//this is to control the Assessment Details screen

public class AssessmentDetailsActivity extends AppCompatActivity {

    Assessment selectedAssessment = null;
    DBHelper myHelper;

    Spinner courseChoice;
    Spinner typeChoice;

    EditText assessNameEdit;
    EditText goalDateEdit;

    EditText startDateText;
    EditText endDateText;

    Switch alertActiveSwitch;

    TextView assessDetailsTitle;

    BottomNavigationView bottomNavigationView;
    BottomNavigationView editBottomNavigationView;
    BottomNavigationView addBottomNavigationView;

    private TextWatcher mgoalDateEntryWatcher;
    private TextWatcher mStartDateEntryWatcher;
    private TextWatcher mEndDateEntryWatcher;

    private boolean addAssessButtonClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_details);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        myHelper = new DBHelper(AssessmentDetailsActivity.this);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationView);
        editBottomNavigationView = (BottomNavigationView) findViewById(R.id.editNavigationView);
        addBottomNavigationView = (BottomNavigationView) findViewById(R.id.addNavigationView);

        courseChoice = (Spinner) findViewById(R.id.courseChoice);
        typeChoice = (Spinner) findViewById(R.id.typeChoice);

        assessNameEdit = (EditText) findViewById(R.id.assessNameEdit);
        goalDateEdit = (EditText) findViewById(R.id.goalDateEdit);

        startDateText = (EditText) findViewById(R.id.startDateText);
        endDateText = (EditText) findViewById(R.id.endDateText);

        alertActiveSwitch = (Switch) findViewById(R.id.alertSwitch);

        assessDetailsTitle = (TextView) findViewById(R.id.assessDetailsTitle);

        addAssessButtonClicked = Assessment.getAddClickBool();

        loadCourseSpinnerData();
        loadTypeSpinnerData();

        showAssessDetails();

        createEndDateTextWatcher();
        createStartDateTextWatcher();
        createGoalDateTextWatcher();

        goalDateEdit.addTextChangedListener(mgoalDateEntryWatcher);
        startDateText.addTextChangedListener(mStartDateEntryWatcher);
        endDateText.addTextChangedListener(mEndDateEntryWatcher);

        bottomNavigation();
        addBottomNavigation();
        editBottomNavigation();

        //checks if the "add assessment" button is clicked from the "Assessment list" screen
        //if it was clicked, the assessment details screen enables the appropriate buttons for adding a new assessment
        if(addAssessButtonClicked == true){

            turnOnEditStyle();

            addBottomNavigationView.setVisibility(View.VISIBLE);
            editBottomNavigationView.setVisibility(View.GONE);
            bottomNavigationView.setVisibility(View.GONE);

            assessDetailsTitle.setText("Add Assessment");

        //if the add assessment button was NOT pressed, the screen shows the details of the selected assessment
        }else{

            turnOffEditStyle();

            assessNameEdit.setEnabled(false);
            goalDateEdit.setEnabled(false);
            courseChoice.setEnabled(false);
            typeChoice.setEnabled(false);
            startDateText.setEnabled(false);
            endDateText.setEnabled(false);

            addBottomNavigationView.setVisibility(View.GONE);
            editBottomNavigationView.setVisibility(View.GONE);
            bottomNavigationView.setVisibility(View.VISIBLE);

            assessDetailsTitle.setText("Assessment Details");

        }

        alertToggle();

        //this enables the app to highlight all text in the textbox when selected
        assessNameEdit.setSelectAllOnFocus(true);
        goalDateEdit.setSelectAllOnFocus(true);
        startDateText.setSelectAllOnFocus(true);
        endDateText.setSelectAllOnFocus(true);


    }

    @Override
    public void onBackPressed(){

    }

    public void showAssessDetails(){

        selectedAssessment = myHelper.returnAssessment();

        //sets the textboxes with data of the selected assessment object
        assessNameEdit.setText(selectedAssessment.getAssessName());
        goalDateEdit.setText(selectedAssessment.getGoalDate());
        startDateText.setText(selectedAssessment.getStartDate());
        endDateText.setText(selectedAssessment.getEndDate());

        //initializes choices for a drop down box (android calls them spinners)
        //drop down box selection is initialized to the choice made in the selected assessment object
        ArrayAdapter courseChoiceAdapter = (ArrayAdapter) courseChoice.getAdapter(); //cast to an ArrayAdapter
        int courseSpinnerPosition = courseChoiceAdapter.getPosition(selectedAssessment.getCourseName());

        ArrayAdapter typeChoiceAdapter = (ArrayAdapter) typeChoice.getAdapter(); //cast to an ArrayAdapter
        int typeSpinnerPosition = typeChoiceAdapter.getPosition(selectedAssessment.getType());

        if(courseSpinnerPosition < 0){
            courseChoice.setSelection(0);
        }else{
            courseChoice.setSelection(courseSpinnerPosition);
        }

        if(typeSpinnerPosition < 0){
            typeChoice.setSelection(0);
        }else{
            typeChoice.setSelection(typeSpinnerPosition);
        }

        //this checks if the selected assessment's notifications were previously activated or not
        if(selectedAssessment.getAlertActive() == 1){

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

                    Toast.makeText(getApplicationContext(), "Alerts turned ON for: " + selectedAssessment.getAssessName(), Toast.LENGTH_SHORT).show();
                    myHelper.updateAssessAlertActive(1, selectedAssessment.getAssessName());

                }else{

                    Toast.makeText(getApplicationContext(), "Alerts turned OFF for: " + selectedAssessment.getAssessName(), Toast.LENGTH_SHORT).show();
                    myHelper.updateAssessAlertActive(0, selectedAssessment.getAssessName());
                }
                //Log.d("Names", termName);

            }
        });
    }

    private void loadCourseSpinnerData() {

        ArrayList<String> courses = myHelper.readCourseRecordName("SELECT courseTitle FROM Course_tbl;");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, courses);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        courseChoice.setAdapter(dataAdapter);

    }

    private void loadTypeSpinnerData() {

        ArrayList<String> type = new ArrayList<String>();
        type.add("Objective");
        type.add("Performance");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, type);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        typeChoice.setAdapter(dataAdapter);

    }

    ////////////NAVIGATION BAR METHODS/////////////////
    //there are more than one navigation bars that are enabled and disabled based on the circumstances
    public void bottomNavigation(){

        bottomNavigationView.getMenu().getItem(0).setCheckable(false);//placed here so that no buttons are pre-selected

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    //these are the choices in the bottom navigation menu
                    case R.id.navigation_edit:
                        //Toast.makeText(AssessmentDetailsActivity.this, "Terms", Toast.LENGTH_SHORT).show();
                        item.setCheckable(true);
                        edit();
                        break;
                    case R.id.navigation_back:
                        //Toast.makeText(AssessmentListActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(AssessmentDetailsActivity.this, AssessmentListActivity.class);
                        startActivity(intent2);
                        break;
                }
                return true;
            }
        });



    }

    public void addBottomNavigation(){

        addBottomNavigationView.getMenu().getItem(0).setCheckable(false);//placed here so that no buttons are pre-selected

        addBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    //these are the choices in the bottom navigation menu

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

        editBottomNavigationView.getMenu().getItem(0).setCheckable(false);//placed here so that no buttons are pre-selected

        editBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    //these are the choices in the bottom navigation menu

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

    //////////////EDIT BUTTON METHOD/////////////////////

    public void edit(){

        turnOnEditStyle();

        //enables the text boxes to be editable when the edit button is pressed
        assessNameEdit.setEnabled(true);
        goalDateEdit.setEnabled(true);
        courseChoice.setEnabled(true);
        typeChoice.setEnabled(true);
        startDateText.setEnabled(true);
        endDateText.setEnabled(true);

        editBottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.setVisibility(View.GONE);

        assessDetailsTitle.setText("Edit Assessment");


    }

    //////FOR ADDING ASSESSMENTS///////////////

    public void cancelAdd(){

        String assessName = assessNameEdit.getText().toString();

        myHelper.updateRecord("DELETE FROM Assessment_tbl WHERE name = \"" + assessName + "\"");
        Intent intent = new Intent(AssessmentDetailsActivity.this, AssessmentListActivity.class);
        startActivity(intent);

    }

    public void saveAdd(){

        int assessID = selectedAssessment.getAssessID();
        String newAssessName = assessNameEdit.getText().toString();
        String newType = typeChoice.getSelectedItem().toString();
        String newGoalDate = goalDateEdit.getText().toString();
        String startDate = startDateText.getText().toString();
        String endDate = endDateText.getText().toString();

        String newCourseName = courseChoice.getSelectedItem().toString();
        int newCourseID = myHelper.getCourseID(newCourseName);

        String[] whereArgs = {Integer.toString(assessID)};

        myHelper.updateRecordAssessmentTbl(assessID, newCourseID, newAssessName, newType,
                newGoalDate, startDate, endDate, "assessmentId = ?", whereArgs);

        Intent intent = new Intent(AssessmentDetailsActivity.this, AssessmentListActivity.class);
        startActivity(intent);

    }

    //////FOR EDITING TERMS///////////////

    public void cancelEdit(){

        showAssessDetails();
        turnOffEditStyle();

        assessDetailsTitle.setText("Assessment Details");

        assessNameEdit.setEnabled(false);
        goalDateEdit.setEnabled(false);
        courseChoice.setEnabled(false);
        typeChoice.setEnabled(false);
        startDateText.setEnabled(false);
        endDateText.setEnabled(false);

        editBottomNavigationView.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.VISIBLE);

    }

    public void saveEdit(){

        int assessID = selectedAssessment.getAssessID();
        String newAssessName = assessNameEdit.getText().toString();
        String newType = typeChoice.getSelectedItem().toString();
        String newGoalDate = goalDateEdit.getText().toString();
        String startDate = startDateText.getText().toString();
        String endDate = endDateText.getText().toString();

        String[] whereArgs = {Integer.toString(assessID)};

        String newCourseName = courseChoice.getSelectedItem().toString();
        int newCourseID = myHelper.getCourseID(newCourseName);

        myHelper.updateRecordAssessmentTbl(assessID, newCourseID, newAssessName, newType,
                newGoalDate, startDate, endDate,"assessmentId = ?", whereArgs);

        Intent intent = new Intent(AssessmentDetailsActivity.this, AssessmentListActivity.class);
        startActivity(intent);

    }

    ////////////DELETE ASSESSMENTS////////////////

    public void delete(){

        AlertDialog.Builder builder = new AlertDialog.Builder(AssessmentDetailsActivity.this);
        builder.setCancelable(true);
        builder.setTitle("CONFIRM DELETE");
        builder.setMessage("Are you sure you would like to delete this assessment?");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        myHelper.updateRecord("DELETE FROM Assessment_tbl WHERE assessmentId = " + selectedAssessment.getAssessID());

                        Intent intent = new Intent(AssessmentDetailsActivity.this, AssessmentListActivity.class);
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


    //these methods watches and forces the date format when the user is typing

    public void createGoalDateTextWatcher(){

        mgoalDateEntryWatcher = new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String working = s.toString();
                boolean isValid = true;
                if (working.length()==2 && before ==0) {
                    if (Integer.parseInt(working) < 1 || Integer.parseInt(working)>12) {
                        isValid = false;
                    } else {
                        working+="/";
                        goalDateEdit.setText(working);
                        goalDateEdit.setSelection(working.length());
                    }
                }else if (working.length()== 5 && before == 0){
                    String enteredDate = working.substring(3);
                    if(Integer.parseInt(enteredDate) < 1 || Integer.parseInt(enteredDate) > 31) {
                        isValid = false;
                    }else{
                        working+="/";
                        goalDateEdit.setText(working);
                        goalDateEdit.setSelection(working.length());
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
                    goalDateEdit.setError("Enter a valid date: MM/DD/YYYY");
                    editBottomNavigationView.getMenu().getItem(2).setCheckable(false);
                    addBottomNavigationView.getMenu().getItem(1).setCheckable(false);
                } else {
                    goalDateEdit.setError(null);
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

    public void turnOnEditStyle(){

        assessNameEdit.setBackgroundResource(R.drawable.edit_on_text);
        startDateText.setBackgroundResource(R.drawable.edit_on_text);
        endDateText.setBackgroundResource(R.drawable.edit_on_text);
        goalDateEdit.setBackgroundResource(R.drawable.edit_on_text);



    }

    public void turnOffEditStyle(){

        assessNameEdit.setBackgroundResource(R.drawable.edit_off_text);
        startDateText.setBackgroundResource(R.drawable.edit_off_text);
        endDateText.setBackgroundResource(R.drawable.edit_off_text);
        goalDateEdit.setBackgroundResource(R.drawable.edit_off_text);

    }

}
