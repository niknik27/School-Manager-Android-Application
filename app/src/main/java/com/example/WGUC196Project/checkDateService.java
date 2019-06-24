package com.example.WGUC196Project;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

//this is a background service that checks if any terms, courses, or assessments are within an X number of days and sends the user a notification
public class checkDateService extends JobIntentService {

    private static final int JOB_ID = 1000;
    DBHelper myHelper;

    String GROUP_KEY_DATE_NOTIFY = "com.android.example.DATE_NOTIFY";

    String channelId = "Term_Manager_App_Channel";


    //public checkDateService() {
      //  super("checkDateService");
    //}

    public static void enqueueWork(Context ctx, Intent intent) {
        enqueueWork(ctx, checkDateService.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(Intent intent) {

        myHelper = new DBHelper(checkDateService.this);

        // Do the task here

        //addNotification();

        Log.i("checkDateService", "Service running");

        checkTermStartDates();
        checkTermEndDates();
        checkCourseStartDates();
        checkCourseEndDates();
        checkAssessGoalDates();
        checkAssessStartDates();
        checkAssessEndDates();

        /* reset the alarm */
        MyAlarmReceiver.setAlarm(false, getApplicationContext());
        stopSelf();
    }

    ///////all the following methods check if the individual terms, courses, and assessments' notifications are enabled
    ///////and then check if any dates are close to the current date
    public void checkTermStartDates(){

        ArrayList<Term> allTerms = new ArrayList<Term>();

        allTerms = myHelper.readTermObjects("SELECT * FROM Term_tbl");

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        Calendar now = Calendar.getInstance();
        long oneDayInMillis = TimeUnit.DAYS.toMillis(1);
        long currentMillis = now.getTimeInMillis() - oneDayInMillis;
        Date currentDate = new Date(currentMillis);
        long daysInMillis = TimeUnit.DAYS.toMillis(15);

        String allCloseDatesAndNames = "";
        String closeTermName = "";

        for(Term term : allTerms){

            String date = term.getStartDate();
            int alertToggle = term.getAlertActive();

            if(isThisDateValid(date, "MM/dd/yyyy") == true){

                try {

                    Date convertedDate = sdf.parse(date);

                    Calendar calendar=Calendar.getInstance();

                    calendar.setTime(convertedDate);

                    Long mill=calendar.getTimeInMillis();
                    //Log.i("checkDateService", "date: " + date + " in millis: " + mill);

                    long milliDifference = mill - currentMillis;

                    Date convertedBackDate = new Date(mill);
                    String convertedBackString = sdf.format(convertedBackDate);

                    if(milliDifference <= daysInMillis && milliDifference > 0.0 && alertToggle == 1){

                        closeTermName = myHelper.getMatchTermName("startDate" , date);
                        allCloseDatesAndNames += "\n" + closeTermName + " on: " + date;


                        Log.i("checkDateService", "date: " + date + " is CLOSE" + " in millis: " + mill);


                        //allCloseCourseNames += "\n" + myHelper.getCourseName("startDate" , date);

                    }else if(milliDifference < 0.0){

                        Log.i("checkDateService", "date: " + date + " has PASSED " + " in millis: " + mill);

                    }else{

                        Log.i("checkDateService", "date: " + date + " is NOT CLOSE " + " in millis: " + mill);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }else{

                Log.i("checkDateService", "this date is not valid: " + date);

            }

        }

        if(allCloseDatesAndNames.equals("")){

            Log.i("checkDateService", "NO CLOSE TERM START DATES");

        }else{

            addNotification(1,
                    "Start of Term Approaching",
                    "The following term will start soon: " ,
                    allCloseDatesAndNames,
                    TermListActivity.class);

        }

    }

    public void checkTermEndDates(){

        ArrayList<Term> allTerms = new ArrayList<Term>();

        allTerms = myHelper.readTermObjects("SELECT * FROM Term_tbl");

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        Calendar now = Calendar.getInstance();
        long oneDayInMillis = TimeUnit.DAYS.toMillis(1);
        long currentMillis = now.getTimeInMillis() - oneDayInMillis;
        Date currentDate = new Date(currentMillis);
        long daysInMillis = TimeUnit.DAYS.toMillis(15);

        String allCloseDatesAndNames = "";
        String closeTermName = "";

        for(Term term : allTerms){

            String date = term.getEndDate();
            int alertToggle = term.getAlertActive();

            if(isThisDateValid(date, "MM/dd/yyyy") == true){

                try {

                    Date convertedDate = sdf.parse(date);

                    Calendar calendar=Calendar.getInstance();

                    calendar.setTime(convertedDate);

                    Long mill=calendar.getTimeInMillis();
                    //Log.i("checkDateService", "date: " + date + " in millis: " + mill);

                    long milliDifference = mill - currentMillis;

                    Date convertedBackDate = new Date(mill);
                    String convertedBackString = sdf.format(convertedBackDate);

                    if(milliDifference <= daysInMillis && milliDifference > 0.0 && alertToggle == 1){

                        closeTermName = myHelper.getMatchTermName("endDate" , date);
                        allCloseDatesAndNames += "\n" + closeTermName + " on: " + date;

                    }else if(milliDifference < 0.0){


                    }else{

                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }else{

                Log.i("checkDateService", "this date is not valid: " + date);

            }

        }

        if(allCloseDatesAndNames.equals("")){

            Log.i("checkDateService", "NO CLOSE TERM START DATES");

        }else{

            addNotification(2,
                    "End of Term Approaching",
                    "The following term will End soon: " ,
                    allCloseDatesAndNames,
                    TermListActivity.class);

        }

    }

    public void checkCourseStartDates(){

        ArrayList<CourseAndMentor> allCourses = new ArrayList<CourseAndMentor>();

        allCourses = myHelper.readCourseAndMentorObjects("SELECT courseId, Course_tbl.termId, Course_tbl.mentorId, \n" +
                                                            "\t\t\t\tcourseTitle, Course_tbl.startDate, Course_tbl.endDate, \n" +
                                                            "\t\t\t\t\ttermTitle, status, Course_tbl.alertActive, mentorName, phone, email\n" +
                                                            "FROM Course_tbl, Mentor_tbl, Term_tbl\n" +
                                                            "WHERE Course_tbl.termId = Term_tbl.termId\n" +
                                                            "AND Course_tbl.mentorId = Mentor_tbl.mentorId");

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        Calendar now = Calendar.getInstance();
        long oneDayInMillis = TimeUnit.DAYS.toMillis(1);
        long currentMillis = now.getTimeInMillis() - oneDayInMillis;
        Date currentDate = new Date(currentMillis);
        long daysInMillis = TimeUnit.DAYS.toMillis(15);

        String allCloseDatesAndNames = "";
        String closeCourseName = "";

        for(CourseAndMentor courseAndMentor : allCourses){

            String date = courseAndMentor.getStartDate();
            int alertToggle = courseAndMentor.getAlertActive();


            if(isThisDateValid(date, "MM/dd/yyyy") == true){

                try {

                    Date convertedDate = sdf.parse(date);

                    Calendar calendar=Calendar.getInstance();

                    calendar.setTime(convertedDate);

                    Long mill=calendar.getTimeInMillis();
                    //Log.i("checkDateService", "date: " + date + " in millis: " + mill);

                    long milliDifference = mill - currentMillis;

                    Date convertedBackDate = new Date(mill);
                    String convertedBackString = sdf.format(convertedBackDate);

                    if(milliDifference <= daysInMillis && milliDifference > 0.0 && alertToggle == 1){

                        closeCourseName = myHelper.getMatchCourseName("startDate" , date);
                        allCloseDatesAndNames += "\n" + closeCourseName + " on: " + date;


                        /*Log.i("checkDateService", "date: " + date + " is CLOSE" + " in millis: " + mill
                                    + "\nBACK TO DATE FROM MILLIS: " + convertedBackString
                                    + "\ncurrent DATE: " + currentDate + " in millis: " + currentMillis
                                    + "\ndifference from millis: " + (milliDifference/(1000*60*60*24))
                                    + "\n15 days from millis: " + (daysInMillis /(1000*60*60*24)));*/


                        //allCloseCourseNames += "\n" + myHelper.getCourseName("startDate" , date);

                    }else if(milliDifference < 0.0){

                        /*Log.i("checkDateService", "date: " + date + " has PASSED " + " in millis: " + mill
                                + "\nBACK TO DATE FROM MILLIS: " + convertedBackString
                                + "\ncurrent DATE: " + currentDate + " in millis: " + currentMillis
                                + "\ndifference from millis: " + (milliDifference/(1000*60*60*24))
                                + "\n15 days from Millis: " + (daysInMillis /(1000*60*60*24)));*/

                    }else{

                        /*Log.i("checkDateService", "date: " + date + " is NOT CLOSE " + " in millis: " + mill
                                + "\nBACK TO DATE FROM MILLIS: " + convertedBackString
                                + "\ncurrent DATE: " + currentDate + " in millis: " + currentMillis
                                + "\ndifference from millis: " + (milliDifference/(1000*60*60*24))
                                + "\n15 days from Millis: " + (daysInMillis /(1000*60*60*24)));*/
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }else{

                Log.i("checkDateService", "this date is not valid: " + date);

            }

        }

        if(allCloseDatesAndNames.equals("")){

            Log.i("checkDateService", "NO CLOSE COURSE START DATES");

        }else{

            addNotification(3,
                    "Start of Course Approaching",
                    "The following courses will start soon: " ,
                    allCloseDatesAndNames,
                    CourseListActivity.class);

        }

    }

    public void checkCourseEndDates(){

        ArrayList<CourseAndMentor> allCourses = new ArrayList<CourseAndMentor>();

        allCourses = myHelper.readCourseAndMentorObjects("SELECT courseId, Course_tbl.termId, Course_tbl.mentorId, \n" +
                "\t\t\t\tcourseTitle, Course_tbl.startDate, Course_tbl.endDate, \n" +
                "\t\t\t\t\ttermTitle, status, Course_tbl.alertActive, mentorName, phone, email\n" +
                "FROM Course_tbl, Mentor_tbl, Term_tbl\n" +
                "WHERE Course_tbl.termId = Term_tbl.termId\n" +
                "AND Course_tbl.mentorId = Mentor_tbl.mentorId");

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        Calendar now = Calendar.getInstance();
        long oneDayInMillis = TimeUnit.DAYS.toMillis(1);
        long currentMillis = now.getTimeInMillis() - oneDayInMillis;
        Date currentDate = new Date(currentMillis);
        long daysInMillis = TimeUnit.DAYS.toMillis(15);

        String allCloseDatesAndNames = "";
        String closeCourseName = "";

        for(CourseAndMentor courseAndMentor : allCourses){

            String date = courseAndMentor.getEndDate();
            int alertToggle = courseAndMentor.getAlertActive();

            if(isThisDateValid(date, "MM/dd/yyyy") == true){

                try {

                    Date convertedDate = sdf.parse(date);

                    Calendar calendar=Calendar.getInstance();

                    calendar.setTime(convertedDate);

                    Long mill=calendar.getTimeInMillis();
                    //Log.i("checkDateService", "date: " + date + " in millis: " + mill);

                    long milliDifference = mill - currentMillis;

                    Date convertedBackDate = new Date(mill);
                    String convertedBackString = sdf.format(convertedBackDate);

                    if(milliDifference <= daysInMillis && milliDifference > 0.0 && alertToggle == 1){

                        closeCourseName = myHelper.getMatchCourseName("endDate" , date);
                        allCloseDatesAndNames += "\n" + closeCourseName + " on: " + date;

                        Log.i("checkDateService", "date: " + date + " is CLOSE" + " in millis: " + mill);


                        //allCloseCourseNames += "\n" + myHelper.getCourseName("startDate" , date);

                    }else if(milliDifference < 0.0){

                        Log.i("checkDateService", "date: " + date + " has PASSED " + " in millis: " + mill);

                    }else{

                        Log.i("checkDateService", "date: " + date + " is NOT CLOSE " + " in millis: " + mill);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }else{

                Log.i("checkDateService", "this date is not valid: " + date);

            }

        }

        if(allCloseDatesAndNames.equals("")){

            Log.i("checkDateService", "NO CLOSE COURSE END DATES");

        }else {

            addNotification(4,
                    "End of Course Approaching",
                    "The following courses will end soon: " ,
                    allCloseDatesAndNames,
                    CourseListActivity.class);

        }

    }

    public void checkAssessGoalDates(){

        ArrayList<Assessment> allAssessments = new ArrayList<Assessment>();

        allAssessments = myHelper.readAssessObjects("SELECT assessmentId, Assessment_tbl.courseId, courseTitle, name, type, goalDate" +
                                                ", Assessment_tbl.startDate, Assessment_tbl.endDate, Assessment_tbl.alertActive" +
                                                "\nFROM Assessment_tbl, Course_tbl\n" +
                                                "\nWHERE Assessment_tbl.courseId = Course_tbl.courseId");

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        Calendar now = Calendar.getInstance();
        long oneDayInMillis = TimeUnit.DAYS.toMillis(1);
        long currentMillis = now.getTimeInMillis() - oneDayInMillis;
        Date currentDate = new Date(currentMillis);
        long daysInMillis = TimeUnit.DAYS.toMillis(15);

        String allCloseDatesAndNames = "";
        String closeAssessName = "";

        for(Assessment assessment : allAssessments){

            String date = assessment.getGoalDate();
            int alertToggle = assessment.getAlertActive();

            if(isThisDateValid(date, "MM/dd/yyyy") == true){

                try {

                    Date convertedDate = sdf.parse(date);

                    Calendar calendar=Calendar.getInstance();

                    calendar.setTime(convertedDate);

                    Long mill=calendar.getTimeInMillis();
                    //Log.i("checkDateService", "date: " + date + " in millis: " + mill);

                    long milliDifference = mill - currentMillis;

                    Date convertedBackDate = new Date(mill);
                    String convertedBackString = sdf.format(convertedBackDate);

                    if(milliDifference <= daysInMillis && milliDifference > 0.0 && alertToggle == 1){

                        closeAssessName = myHelper.getMatchAssessName("goalDate" , date);
                        allCloseDatesAndNames += "\n" + closeAssessName + " on: " + date;

                        Log.i("checkDateService", "date: " + date + " is CLOSE" + " in millis: " + mill);


                    }else if(milliDifference < 0.0){

                        Log.i("checkDateService", "date: " + date + " has PASSED " + " in millis: " + mill);

                    }else{

                        Log.i("checkDateService", "date: " + date + " is NOT CLOSE " + " in millis: " + mill);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }else{

                Log.i("checkDateService", "this date is not valid: " + date);

            }


        }

        if(allCloseDatesAndNames.equals("")){

            Log.i("checkDateService", "NO CLOSE ASSESSMENT GOAL DATES");

        }else {

            addNotification(5,
                    "Assessment Goal Date Approaching",
                    "The following assessment goals are approaching: " ,
                    allCloseDatesAndNames,
                    AssessmentListActivity.class);

        }

    }

    public void checkAssessStartDates(){

        ArrayList<Assessment> allAssessments = new ArrayList<Assessment>();

        allAssessments = myHelper.readAssessObjects("SELECT assessmentId, Assessment_tbl.courseId, courseTitle, name, type, goalDate" +
                ", Assessment_tbl.startDate, Assessment_tbl.endDate, Assessment_tbl.alertActive" +
                "\nFROM Assessment_tbl, Course_tbl\n" +
                "\nWHERE Assessment_tbl.courseId = Course_tbl.courseId");

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        Calendar now = Calendar.getInstance();
        long oneDayInMillis = TimeUnit.DAYS.toMillis(1);
        long currentMillis = now.getTimeInMillis() - oneDayInMillis;
        Date currentDate = new Date(currentMillis);
        long daysInMillis = TimeUnit.DAYS.toMillis(15);

        String allCloseDatesAndNames = "";
        String closeAssessName = "";

        for(Assessment assessment : allAssessments){

            String date = assessment.getStartDate();
            int alertToggle = assessment.getAlertActive();

            if(isThisDateValid(date, "MM/dd/yyyy") == true){

                try {

                    Date convertedDate = sdf.parse(date);

                    Calendar calendar=Calendar.getInstance();

                    calendar.setTime(convertedDate);

                    Long mill=calendar.getTimeInMillis();
                    //Log.i("checkDateService", "date: " + date + " in millis: " + mill);

                    long milliDifference = mill - currentMillis;

                    Date convertedBackDate = new Date(mill);
                    String convertedBackString = sdf.format(convertedBackDate);

                    if(milliDifference <= daysInMillis && milliDifference > 0.0 && alertToggle == 1){

                        closeAssessName = myHelper.getMatchAssessName("startDate" , date);
                        allCloseDatesAndNames += "\n" + closeAssessName + " on: " + date;

                        Log.i("checkDateService", "date: " + date + " is CLOSE" + " in millis: " + mill);


                    }else if(milliDifference < 0.0){

                        Log.i("checkDateService", "date: " + date + " has PASSED " + " in millis: " + mill);

                    }else{

                        Log.i("checkDateService", "date: " + date + " is NOT CLOSE " + " in millis: " + mill);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }else{

                Log.i("checkDateService", "this date is not valid: " + date);

            }


        }

        if(allCloseDatesAndNames.equals("")){

            Log.i("checkDateService", "NO CLOSE ASSESSMENT START DATES");

        }else {

            addNotification(6,
                    "Assessment Start Date Approaching",
                    "The following assessments will start soon: " ,
                    allCloseDatesAndNames,
                    AssessmentListActivity.class);

        }

    }

    public void checkAssessEndDates(){

        ArrayList<Assessment> allAssessments = new ArrayList<Assessment>();

        allAssessments = myHelper.readAssessObjects("SELECT assessmentId, Assessment_tbl.courseId, courseTitle, name, type, goalDate" +
                ", Assessment_tbl.startDate, Assessment_tbl.endDate, Assessment_tbl.alertActive" +
                "\nFROM Assessment_tbl, Course_tbl\n" +
                "\nWHERE Assessment_tbl.courseId = Course_tbl.courseId");

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        Calendar now = Calendar.getInstance();
        long oneDayInMillis = TimeUnit.DAYS.toMillis(1);
        long currentMillis = now.getTimeInMillis() - oneDayInMillis;
        Date currentDate = new Date(currentMillis);
        long daysInMillis = TimeUnit.DAYS.toMillis(15);

        String allCloseDatesAndNames = "";
        String closeAssessName = "";

        for(Assessment assessment : allAssessments){

            String date = assessment.getEndDate();
            int alertToggle = assessment.getAlertActive();

            if(isThisDateValid(date, "MM/dd/yyyy") == true){

                try {

                    Date convertedDate = sdf.parse(date);

                    Calendar calendar=Calendar.getInstance();

                    calendar.setTime(convertedDate);

                    Long mill=calendar.getTimeInMillis();
                    //Log.i("checkDateService", "date: " + date + " in millis: " + mill);

                    long milliDifference = mill - currentMillis;

                    Date convertedBackDate = new Date(mill);
                    String convertedBackString = sdf.format(convertedBackDate);

                    if(milliDifference <= daysInMillis && milliDifference > 0.0 && alertToggle == 1){

                        closeAssessName = myHelper.getMatchAssessName("endDate" , date);
                        allCloseDatesAndNames += "\n" + closeAssessName + " on: " + date;

                        Log.i("checkDateService", "date: " + date + " is CLOSE" + " in millis: " + mill);


                    }else if(milliDifference < 0.0){

                        Log.i("checkDateService", "date: " + date + " has PASSED " + " in millis: " + mill);

                    }else{

                        Log.i("checkDateService", "date: " + date + " is NOT CLOSE " + " in millis: " + mill);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }else{

                Log.i("checkDateService", "this date is not valid: " + date);

            }


        }

        if(allCloseDatesAndNames.equals("")){

            Log.i("checkDateService", "NO CLOSE ASSESSMENT START DATES");

        }else {

            addNotification(7,
                    "Assessment Due Date Approaching",
                    "The following assessments will end soon: " ,
                    allCloseDatesAndNames,
                    AssessmentListActivity.class);

        }

    }

    /////checks if the entered dates are in a valid format
    ////if not, it will be skipped over

    public boolean isThisDateValid(String dateToValidate, String dateFormat){

        if(dateToValidate == null){
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);

        try {

            //if not valid, it will throw ParseException
            Date date = sdf.parse(dateToValidate);
            System.out.println(date);

        } catch (ParseException e) {

            e.printStackTrace();
            return false;
        }

        return true;
    }


    //creates the notification on the user's device
    ///all methods have different "notifyId"s so that there are multiple notifications instead of having just one
    public void addNotification(int notifyId, String title, String begin, String body, Class className) {

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //String channelId = "Term_Manager_App_Channel";
            CharSequence channelName = "Term Manager Channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{0, 250, 250, 250});
            notificationManager.createNotificationChannel(notificationChannel);


        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_termanager) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(begin)// message for notification
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(begin + body))
                .setGroup(GROUP_KEY_DATE_NOTIFY)
                .setAutoCancel(true); // clear notification after click


        /////THIS IS TO CALL ACTIVITY ON NOTIFICATION CLICK
        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(this, className);

        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {

            stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(resultIntent);
            // Get the PendingIntent containing the entire back stack
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

        }else{

            PendingIntent pi = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pi);

        }

        mBuilder.setVibrate(new long[] { 1000, 1000});
        notificationManager.notify(notifyId, mBuilder.build());

    }

}
