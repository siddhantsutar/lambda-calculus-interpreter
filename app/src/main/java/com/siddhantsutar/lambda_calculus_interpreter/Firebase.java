package com.siddhantsutar.lambda_calculus_interpreter;

import android.content.SharedPreferences;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;

public class Firebase {

    private String androidId;
    private DatabaseReference ref;
    private static Firebase instance = new Firebase();

    public static Firebase getInstance() {
        return instance;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public void setRef(DatabaseReference ref) {
        this.ref = ref;
    }

    public void pull(final String original) {
        SharedPreferencesHandler sharedPreferencesHandler = SharedPreferencesHandler.getInstance();
        SharedPreferences sharedPreferences = sharedPreferencesHandler.getSharedPreferences();
        final Lexer lexer = Lexer.getInstance();
        if (sharedPreferences.getBoolean("firebase", true)) {
            ref.child("evaluations").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String result = getData((Map<String, Object>) dataSnapshot.getValue(), original);
                    if (result == null) lexer.println("Short circuit evaluation unavailable for this expression!");
                    else lexer.println(Interpreter.outputFormat(result));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    // Checks if expression already exists in the database; if not, adds it. WARNING: does not work if database is initially empty
    public void push(final String original, final String result) {
        SharedPreferencesHandler sharedPreferencesHandler = SharedPreferencesHandler.getInstance();
        SharedPreferences sharedPreferences = sharedPreferencesHandler.getSharedPreferences();
        if (sharedPreferences.getBoolean("firebase", true)) {
            ref.child("evaluations").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String result = getData((Map<String, Object>) dataSnapshot.getValue(), original);
                    if (result == null) {
                        Map<String, String> values = new HashMap<String, String>();
                        values.put("original", original);
                        values.put("result", result);
                        ref.child("evaluations").push().setValue(values);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private String getData(Map<String, Object> evaluations, String original) {
        for (Map.Entry<String, Object> entry : evaluations.entrySet()) {
            Map singleEntry = (Map) entry.getValue();
            String buffer = (String) singleEntry.get("original");
            if (buffer.equals(original)) return ((String) singleEntry.get("result"));
        }
        return null;
    }

}