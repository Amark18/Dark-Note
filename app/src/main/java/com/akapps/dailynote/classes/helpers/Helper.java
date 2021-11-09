package com.akapps.dailynote.classes.helpers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import com.akapps.dailynote.R;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import www.sanju.motiontoast.MotionToast;
import static android.content.Context.MODE_PRIVATE;

public class Helper {

    public static boolean isTablet(Context context){
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    // returns true if device is in landscape
    public static boolean isLandscape(Context context){
        int orientation = context.getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    // returns true if device is in portrait
    public static boolean isPortrait(Context context){
        int orientation = context.getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    // locks orientation
    public static void setOrientation(Activity activity, Context context){
        if(isPortrait(context))
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    public static String getCurrentDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy\nhh:mm:ss a");
        return sdf.format(c.getTime());
    }

    public static void unSetOrientation(Activity activity, Context context){
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    public static String twentyFourToTwelve(String dateString){
        String date = dateString.split(" ")[0];
        dateString = dateString.split(" ")[1];
        String[] time = dateString.split(":");
        int hour = Integer.parseInt(time[0]);
        String end = "";
        if(hour>=12) {
            end = "PM";
            hour %=12;
        }
        else
            end = "AM";

        if(hour == 0)
            hour = 12;

        return date + " " + hour + ":" + time[1] + ":" + time[2] + " " + end;
    }

    public static String getOccurrenceInString(int occurrence){
        if(occurrence == 1)
            return "Daily";
        else if(occurrence == 7)
            return "Weekly";
        else{
            return "Every " + occurrence + " days";
        }
    }

    public static Calendar dateToCalender(String dateString){
        Calendar calendar = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("E, MMM dd, yyyy hh:mm:ss aa");
            String dateInString = dateString;
            Date date = sdf.parse(dateInString);
            calendar = Calendar.getInstance();
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    // pin is based on the current year and the hour
    public static String createBackupFileName(){
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int minute = calendar.get(Calendar.MINUTE);
        String amOrPm = calendar.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";
        return "Backup_" + month + "_" + calendar.get(Calendar.DAY_OF_MONTH)
                + "_" + calendar.get(Calendar.YEAR)
                + "_at_" + calendar.get(Calendar.HOUR) + "_" +amOrPm + ".realm";
    }

    public static String getTimeDifference(Calendar calendar, boolean before){
        try {
            String timeDifference = "";
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime dateSelected = LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault());
            long secs, mins, hours, days, months, years;
            if (before) {
                years = ChronoUnit.YEARS.between(now, dateSelected);
                months = ChronoUnit.MONTHS.between(now, dateSelected);
                secs = ChronoUnit.SECONDS.between(now, dateSelected);
                mins = ChronoUnit.MINUTES.between(now, dateSelected);
                hours = ChronoUnit.HOURS.between(now, dateSelected);
                days = ChronoUnit.DAYS.between(now, dateSelected);
            } else {
                years = ChronoUnit.YEARS.between(dateSelected, now);
                months = ChronoUnit.MONTHS.between(dateSelected, now);
                secs = ChronoUnit.SECONDS.between(dateSelected, now);
                mins = ChronoUnit.MINUTES.between(dateSelected, now);
                hours = ChronoUnit.HOURS.between(dateSelected, now);
                days = ChronoUnit.DAYS.between(dateSelected, now);
            }

            if (years > 0) {
                timeDifference += years + " year";
                if (years > 1)
                    timeDifference += "s ";
                else
                    timeDifference += " ";
            }
            if (months % 12 > 0) {
                timeDifference += months % 12 + " month";
                if (months % 12 > 1)
                    timeDifference += "s ";
                else
                    timeDifference += " ";
            }
            if (days % 30 > 0) {
                timeDifference += days % 30 + " day";
                if (days % 30 > 1)
                    timeDifference += "s ";
                else
                    timeDifference += " ";
            }
            if (hours % 24 > 0) {
                timeDifference += hours % 24 + " hour";
                if ((hours % 24) > 1)
                    timeDifference += "s ";
                else
                    timeDifference += " ";
            }
            if (mins % 60 > 0) {
                timeDifference += mins % 60 + " min";
                if ((mins % 60) > 1)
                    timeDifference += "s ";
                else
                    timeDifference += " ";
            }
            if (secs % 60 > 0) {
                timeDifference += secs % 60 + " sec";
                if ((secs % 60) > 1)
                    timeDifference += "s";
            }
            return timeDifference;
        }
        catch (Exception e){
            return "";
        }
    }

    /**
     * Determines if recyclerview is empty and displays empty view
     * @param size   The size of recyclerview
     * @param empty_Layout   This contains an image
     */
    public static void isListEmpty(Context context, int size, ScrollView empty_Layout, TextView title,
                                   TextView subTitle, TextView subSubTitle,
                                   boolean isResults, boolean isChecklist, boolean isChecklistAdded){
        if(isResults){
            title.setText("No Results");
            title.setTextSize(30);
            subTitle.setText("");
            subSubTitle.setText("");
        }
        else if(isChecklist) {
            title.setText("");
            subTitle.setText("soooo empty in this checklist");
            subSubTitle.setText("Tap the bottom right button to create an item");
        }
        else if(!isResults){
            title.setTextSize(20);
            title.setText("Don't get lost in the universe trying to remember.");
            subTitle.setText("Let me do it for you");
            subSubTitle.setText("Tap the bottom right button to create an item");
        }

        if(isChecklistAdded)
            subSubTitle.setText("");

        if (size == 0)
            empty_Layout.setVisibility(View.VISIBLE);
        else
            empty_Layout.setVisibility(View.GONE);
    }

    public static ArrayList<String> getCurrentList(Context context){
        String stringHistory = PreferenceManager.getDefaultSharedPreferences(context).getString("categories", null);
        if(stringHistory!=null)
            return  new ArrayList<>(Arrays.asList(stringHistory.split("~")));
        else
            return new ArrayList<>();
    }

    public static ArrayList<String> getAutoCompleteList(Context context){
        String stringHistory = PreferenceManager.getDefaultSharedPreferences(context).getString("autocomplete", null);
        if(stringHistory!=null)
            return  new ArrayList<>(Arrays.asList(stringHistory.split("~~")));
        else
            return new ArrayList<>();
    }

    // saves a small piece of data
    public static void savePreference(Context context, String data, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("app", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, data);
        editor.apply();
    }

    // retrieved data saved
    public static String getPreference(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("app", MODE_PRIVATE);
        String data = sharedPreferences.getString(key, null);
        return data;
    }

    // retrieved data saved
    public static String getKey(Context context) {
        if(null == getPreference(context, "key_encryption")){
            byte[] key = new byte[32];
            new SecureRandom().nextBytes(key);
            savePreference(context, new String(key), "key_encryption");
        }
        return getPreference(context, "key_encryption");
    }

    // saves a small piece of data
    public static void saveBooleanPreference(Context context, boolean data, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("app", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, data);
        editor.apply();
    }

    // retrieved data saved
    public static boolean getBooleanPreference(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("app", MODE_PRIVATE);
        boolean data = sharedPreferences.getBoolean(key, false);
        return data;
    }

    // shows the user a message
    public static void showMessage(Activity activity, String title, String message, String typeOfMessage){
        try {
            MotionToast.Companion.darkColorToast(activity,
                    title,
                    message,
                    typeOfMessage,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(activity, R.font.overpass));
        }
        catch (Exception e){
            Toast.makeText(activity.getBaseContext(), title + "\n" + message, Toast.LENGTH_LONG).show();
        }
    }

    public static Dialog showLoading(String loadingText, Dialog progressDialog, Context context, boolean show){
        try {
            if (show) {
                loadingText += "\nDo not close app";
                if(loadingText.toLowerCase().contains("sync"))
                    loadingText += "\nData might be Lost\nThis should take not take more than a minute" +
                            " depending on size of backup";
                progressDialog = new Dialog(context);
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressDialog.setContentView(R.layout.custom_dialog_progress);

                TextView progressTv = progressDialog.findViewById(R.id.progress_tv);
                progressTv.setText(loadingText);
                progressTv.setGravity(Gravity.CENTER);
                progressTv.setTextColor(ContextCompat.getColor(context, R.color.golden_rod));
                progressTv.setTextSize(19F);
                if (progressDialog.getWindow() != null)
                    progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                progressDialog.setCancelable(false);
                progressDialog.show();
            } else
                progressDialog.cancel();
        }catch (Exception e){ }
        return progressDialog;
    }

    // deletes cache directory to ensure app size does not get too big
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}
