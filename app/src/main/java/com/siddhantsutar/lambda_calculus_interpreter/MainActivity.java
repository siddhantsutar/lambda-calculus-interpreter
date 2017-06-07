package com.siddhantsutar.lambda_calculus_interpreter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.firebase.database.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private SharedPreferencesHandler sharedPreferencesHandler;
    private SharedPreferences sharedPreferences;
    private Firebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Lambda Calculus Interpreter");
        sharedPreferencesHandler = sharedPreferencesHandler.getInstance();
        firebase = Firebase.getInstance();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPreferencesHandler.setSharedPreferences(PreferenceManager.getDefaultSharedPreferences(getBaseContext()));
        firebase.setAndroidId(Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));
        firebase.setRef(FirebaseDatabase.getInstance().getReference());
        sharedPreferences = sharedPreferencesHandler.getSharedPreferences();
        Button button = (Button) findViewById(R.id.button_evaluate);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = (EditText) findViewById(R.id.edit_input);
                TextView output = (TextView) findViewById(R.id.text_output);
                output.setMovementMethod(new ScrollingMovementMethod());
                InputStream is = new ByteArrayInputStream((input.getText().toString() + '\0').getBytes());
                SetDictionary variableSettings = new SetDictionary();
                loadVariable(variableSettings, "maxEvalSteps", 10000);
                loadVariable(variableSettings, "preOrderEvaluate", 1);
                loadVariable(variableSettings, "printLevel", true);
                Parser parser = new Parser(variableSettings, sharedPreferences.getBoolean("verbose", false));
                try {
                    parser.run(is, output);
                } catch (Exception e) {
                    Log.e(e.getClass().getName(), "Exception", e);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadVariable(SetDictionary setDictionary, String key, Object defValue) {
        if (defValue instanceof Boolean) {
            setDictionary.put(key, (sharedPreferences.getBoolean(key, (boolean) defValue)) ? 1 : 0);
        } else {
            setDictionary.put(key, Integer.valueOf(sharedPreferences.getString(key, String.valueOf(defValue))));
        }
    }

}