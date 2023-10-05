package com.example.elincasesapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SetCasesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_cases);

        EditText editTextHorizonCases = findViewById(R.id.editTextHorizonCases);
        EditText editTextDangerZoneCases = findViewById(R.id.editTextDangerZoneCases);
        EditText editTextPrismaCases = findViewById(R.id.editTextPrismaCases);
        EditText editTextPrisma2Cases = findViewById(R.id.editTextPrisma2Cases);

        Button buttonConfirm = findViewById(R.id.buttonConfirm);

        buttonConfirm.setOnClickListener(view -> {
            // Retrieve entered values
            int horizonCases = parseEditTextValue(editTextHorizonCases.getText().toString());
            int dangerZoneCases = parseEditTextValue(editTextDangerZoneCases.getText().toString());
            int prismaCases = parseEditTextValue(editTextPrismaCases.getText().toString());
            int prisma2Cases = parseEditTextValue(editTextPrisma2Cases.getText().toString());

            // If a value is entered, update the corresponding case count in SharedPreferences
            if (horizonCases != -1) {
                saveCaseToSharedPreferences("horizonCases", horizonCases);
            }
            if (dangerZoneCases != -1) {
                saveCaseToSharedPreferences("dangerZoneCases", dangerZoneCases);
            }
            if (prismaCases != -1) {
                saveCaseToSharedPreferences("prismaCases", prismaCases);
            }
            if (prisma2Cases != -1) {
                saveCaseToSharedPreferences("prisma2Cases", prisma2Cases);
            }

            // Finish the activity
            finish();
        });
    }

    private void saveCaseToSharedPreferences(String key, int value) {
        // Save the case amount to SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private int parseEditTextValue(String value) {
        if (TextUtils.isEmpty(value.trim())) {
            return -1;  // Set to -1 if not entered
        }
        return Integer.parseInt(value);
    }
}
