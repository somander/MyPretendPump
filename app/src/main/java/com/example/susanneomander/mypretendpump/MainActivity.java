package com.example.susanneomander.mypretendpump;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.prefs.Preferences;

public class MainActivity extends AppCompatActivity {
    float units;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayLog();
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        Intent i = new Intent(this, MyPreferencesActivity.class);
        startActivity(i);
        return true;
    }

    public void calculateInsulin(View view) {

        EditText eTBG = (EditText) findViewById(R.id.eTBG);
        EditText eTCarbs = (EditText) findViewById(R.id.eTCarbs);
        TextView eTUnits = (TextView) findViewById(R.id.eTUnitsInsulin);
        TextView tVBGUnits = (TextView) findViewById(R.id.tVBGUnits);
        TextView tVCarbUnits = (TextView) findViewById(R.id.tVCarbUnits);

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        float mTarget = Float.parseFloat(SP.getString("bg_preference", "120.0"));

        SharedPreferences SPr = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        float mRatio = Float.parseFloat(SPr.getString("ratio_preference", "20.0"));

        SharedPreferences SPs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        float mSensitivity = Float.parseFloat(SPs.getString("sensitivity_preference", "80.0"));

        Editable bg = eTBG.getText();
        Editable carbs = eTCarbs.getText();

        float bloodglucose;
        float carbohydrates;

        if(bg.toString().isEmpty()) {
            bloodglucose = mTarget;
        }
        else {
            bloodglucose = Float.parseFloat(bg.toString());
        }
        if(carbs.toString().isEmpty()) {
            carbohydrates = 0.0f;
        }
        else {
            carbohydrates = Float.parseFloat(carbs.toString());
        }

        //Toast toast = Toast.makeText(getApplicationContext(), "bg=" + bg + " carbs=" + carbs, Toast.LENGTH_LONG);
        //toast.show();

        float bgcorrection = (bloodglucose-mTarget)/mSensitivity;
        tVBGUnits.setText(String.format("   %.01f", bgcorrection));
        float carbcorrection = carbohydrates / mRatio;
        tVCarbUnits.setText(String.format("   %.01f", carbcorrection));

        units = bgcorrection + carbcorrection;
        eTUnits.setText(String.format("  %.01f", units));

        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }


    }

    public void displayLog() {
        TextView tVLog = (TextView) findViewById(R.id.tVLog);
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        float dose = sharedPref.getFloat("dose", 0);
        long doseTime = sharedPref.getLong("doseTime", 0);
        long timeSinceDose = System.currentTimeMillis() - doseTime;
        long fullDay = 24 * 60 * 60 * 1000;
        if (timeSinceDose > fullDay) {
            tVLog.setText("  Last bolus was more than 24 hours ago");
        }
        if (timeSinceDose < fullDay) {
            tVLog.setText(String.format("  Last bolus was %.01f units %s", dose, DateUtils.getRelativeTimeSpanString(doseTime)));
        }
        if (doseTime == 0) {
            tVLog.setText("  No bolus log recorded");
        }
    }

    public void logDose(View view) {

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat("dose", units);
        editor.putLong("doseTime", System.currentTimeMillis());
        editor.commit();
        displayLog();
    }

}

