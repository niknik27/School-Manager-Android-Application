package com.example.WGUC196Project;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

////this controls the main screen of the application

public class MainActivity extends AppCompatActivity {

    DBHelper myHelper;

    Button notificationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        myHelper = new DBHelper(MainActivity.this);

        myHelper.getWritableDatabase();

        notificationButton = (Button) findViewById(R.id.notificationButton);

        ///these send out notifications to the user on application start
        MyAlarmReceiver.setAlarm(true, getApplicationContext());
        Intent serviceIntent = new Intent(MainActivity.this, MainActivity.class);
        checkDateService.enqueueWork(MainActivity.this, serviceIntent);

        bottomNavigation();

    }


    /////these are links to each individual list screens
    public void bottomNavigation(){

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.navigation_terms:
                        //Toast.makeText(MainActivity.this, "Terms", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, TermListActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_courses:
                        //Toast.makeText(MainActivity.this, "Courses", Toast.LENGTH_SHORT).show();
                        Intent intent3 = new Intent(MainActivity.this, CourseListActivity.class);
                        startActivity(intent3);
                        break;
                    case R.id.navigation_home:
                        //Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navigation_assessments:
                        //Toast.makeText(MainActivity.this, "Assessments", Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(MainActivity.this, AssessmentListActivity.class);
                        startActivity(intent2);
                        break;
                }
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

    }


    public void createNotifications(){

        notificationButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                addNotification();
            }
        });

    }


    public void addNotification() {

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "Term_Manager_App_Channel";
            CharSequence channelName = "Term Manager Channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 100});
            notificationManager.createNotificationChannel(notificationChannel);
        }

        int notifyId = 1;
        String channelId = "Term_Manager_App_Channel";

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                .setContentTitle("This is a Title") // title for notification
                .setContentText("This is a Test Body")// message for notification
                .setAutoCancel(true); // clear notification after click


        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        notificationManager.notify(notifyId, mBuilder.build());
    }


    @Override
    protected void onResume(){
        super.onResume();


    }

    @Override
    protected void onPause(){
        super.onPause();

        myHelper.close();
        //Toast.makeText(MainActivity.this, myHelper.getDatabaseName() + " closed!", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /////THIS IS FOR INITIALIZING QUICK TEST DATA FOR THE APPLICATION
    public void initTestData(){

        long result1 = myHelper.addRecordTermTbl(1,"Term 1" ,
                "01/01/2019",
                "06/03/2019",
                0);

        long result2 = myHelper.addRecordTermTbl(2, "Term 2" ,
                "07/01/2019",
                "12/31/2019",
                0);

        long result3 = myHelper.addRecordTermTbl(3, "Term 3" ,
                "01/01/2020",
                "06/30/2020",
                0);

        long result4 = myHelper.addRecordTermTbl(4, "Term 4" ,
                "07/01/2020",
                "12/31/2020",
                0);

        long result5 = myHelper.addRecordAssessTbl(1,1,
                "English Composition Assessment",
                "Performance",
                "01/28/2019",
                "07/01/2020",
                "12/31/2020",
                0);

        long result6 = myHelper.addRecordAssessTbl(2, 2,
                "Intro to IT Assessment",
                "Objective",
                "07/28/2019",
                "07/01/2020",
                "12/31/2020",
                0);

        long result7 = myHelper.addRecordAssessTbl(3, 3,
                "Software I Assessment",
                "Performance",
                "01/28/2020",
                "07/01/2020",
                "12/31/2020",
                0);

        long result8 = myHelper.addRecordAssessTbl(4, 4,
                "Software II Assessment",
                "Performance",
                "07/28/2020",
                "07/01/2020",
                "12/31/2020",
                0);


        long result9 = myHelper.addRecordMentorTbl(1, "Becky", "913-345-4657", "becky@gmail.com");

        long result10 = myHelper.addRecordMentorTbl(2, "James", "915-215-8005", "james@gmail.com");

        long result11 = myHelper.addRecordMentorTbl(3, "Judy", "312-345-2555", "judy@gmail.com");

        long result12 = myHelper.addRecordMentorTbl(4, "Benny", "708-222-4455", "benny@gmail.com");


        long result13 = myHelper.addRecordCourseTbl(1,1,
                1,
                "English Composition",
                "01/01/2019",
                "01/28/2019",
                "In Progress",
                0);

        long result14 = myHelper.addRecordCourseTbl(2,2,
                2,
                "Introduction to IT",
                "01/07/2019",
                "07/28/2019",
                "Plan to Take",
                0);

        long result15 = myHelper.addRecordCourseTbl(3,3,
                3,
                "Software I",
                "01/01/2020",
                "01/28/2020",
                "Plan to Take",
                0);

        long result16 = myHelper.addRecordCourseTbl(4,4,
                4,
                "Software II",
                "07/01/2020",
                "07/28/2020",
                "Plan to Take",
                0);
    }


}
