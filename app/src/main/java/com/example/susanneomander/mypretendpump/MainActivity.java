package com.example.susanneomander.mypretendpump;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        float units;

        EditText eTBG = (EditText) findViewById(R.id.eTBG);
        EditText eTCarbs = (EditText) findViewById(R.id.eTCarbs);
        TextView eTUnits = (TextView) findViewById(R.id.eTUnitsInsulin);

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

        units = (bloodglucose-mTarget)/mSensitivity+carbohydrates/mRatio;
        eTUnits.setText(String.format("%.01f", units));

        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }


    }

}

