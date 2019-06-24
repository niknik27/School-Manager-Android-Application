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
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

//this is to control the Assessment Details screen

public class NoteDetailsActivity extends AppCompatActivity {

    Note selectedNote;
    DBHelper myHelper;

    private boolean addNoteButtonClicked;

    EditText noteEditText;

    BottomNavigationView bottomNavigationView;
    BottomNavigationView editBottomNavigationView;
    BottomNavigationView addBottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        myHelper = new DBHelper(NoteDetailsActivity.this);

        noteEditText = (EditText) findViewById(R.id.noteEditText);

        /*editButton = (Button) findViewById(R.id.editButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        shareButton = (Button) findViewById(R.id.shareButton);*/

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationView);
        editBottomNavigationView = (BottomNavigationView) findViewById(R.id.editNavigationView);
        addBottomNavigationView = (BottomNavigationView) findViewById(R.id.addNavigationView);

        showNoteDetails();

        addNoteButtonClicked = Note.getAddClickBool();

        bottomNavigation();
        addBottomNavigation();
        editBottomNavigation();

        //checks if the "add note" button is clicked from the "note list" in the course details screen
        //if it was clicked, the note details screen enables the appropriate buttons for adding a new note
        if(addNoteButtonClicked == true){

            addBottomNavigationView.setVisibility(View.VISIBLE);
            editBottomNavigationView.setVisibility(View.GONE);
            bottomNavigationView.setVisibility(View.GONE);

            //if the add note button was NOT pressed, the screen shows the details of the selected note
        }else{

            noteEditText.setEnabled(false);

            addBottomNavigationView.setVisibility(View.GONE);
            editBottomNavigationView.setVisibility(View.GONE);
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed(){

    }

    public void showNoteDetails() {

        selectedNote = myHelper.returnNote();

        noteEditText.setText(selectedNote.getNoteText());

    }

    ////////////NAVIGATION BAR METHODS/////////////////
    public void bottomNavigation(){

        bottomNavigationView.getMenu().getItem(0).setCheckable(false);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.navigation_share:
                        //Toast.makeText(AssessmentDetailsActivity.this, "Terms", Toast.LENGTH_SHORT).show();
                        item.setCheckable(true);
                        share();
                        break;
                    case R.id.navigation_edit:
                        //Toast.makeText(AssessmentDetailsActivity.this, "Terms", Toast.LENGTH_SHORT).show();
                        edit();
                        break;
                    case R.id.navigation_back:
                        //Toast.makeText(AssessmentListActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(NoteDetailsActivity.this, CourseAndMentorDetailsActivity.class);
                        startActivity(intent2);
                        break;
                }
                return true;
            }
        });



    }

    //////NAVIGATION BARS/////////
    //there are more than one navigation bars that are enabled and disabled based on the circumstances
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

    //////////////EDIT BUTTON METHOD/////////////////////


    public void edit(){

        noteEditText.setEnabled(true);

        editBottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.setVisibility(View.GONE);
    }

    //////FOR ADDING NOTES///////////////

    public void cancelAdd(){
        String noteText = noteEditText.getText().toString();

        myHelper.updateRecord("DELETE FROM Note_tbl WHERE note = \"" + noteText + "\"");

        Intent intent = new Intent(NoteDetailsActivity.this, CourseAndMentorDetailsActivity.class);
        startActivity(intent);
    }

    public void saveAdd(){

        String noteText = noteEditText.getText().toString();

        int noteID = selectedNote.getNoteID();
        int courseID = selectedNote.getCourseID();

        String[] whereArgs = {Integer.toString(noteID)};

        myHelper.updateRecordNotesTbl(noteID, courseID, noteText ,"noteId = ?", whereArgs);

        Intent intent = new Intent(NoteDetailsActivity.this, CourseAndMentorDetailsActivity.class);
        startActivity(intent);
    }

    //////FOR EDITING NOTES///////////////

    public void cancelEdit(){

        showNoteDetails();

        noteEditText.setEnabled(false);

        editBottomNavigationView.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    public void saveEdit(){

        String noteText = noteEditText.getText().toString();

        int noteID = selectedNote.getNoteID();
        int courseID = selectedNote.getCourseID();

        String[] whereArgs = {Integer.toString(noteID)};

        myHelper.updateRecordNotesTbl(noteID, courseID, noteText ,"noteId = ?", whereArgs);

        Intent intent = new Intent(NoteDetailsActivity.this, CourseAndMentorDetailsActivity.class);
        startActivity(intent);
    }

    ////////////DELETING NOTES////////////

    public void delete(){

        AlertDialog.Builder builder = new AlertDialog.Builder(NoteDetailsActivity.this);
        builder.setCancelable(true);
        builder.setTitle("CONFIRM DELETE");
        builder.setMessage("Are you sure you would like to delete this Note?");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        myHelper.updateRecord("DELETE FROM Note_tbl WHERE noteId = " + selectedNote.getNoteID());

                        Intent intent = new Intent(NoteDetailsActivity.this, CourseAndMentorDetailsActivity.class);
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

    ///////////SHARING NOTES///////

    public void share(){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        String shareBody = "Here is a note for: " + selectedNote.getCourseTitle() +
                            "\n\n" + selectedNote.getNoteText();

        //sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Note for: " + selectedNote.getCourseTitle());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }


}
