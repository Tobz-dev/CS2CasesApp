package com.example.elincasesapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
        EditText editTextParisChallengers = findViewById(R.id.editTextParisChallengers);
        EditText editTextParisLegends = findViewById(R.id.editTextParisLegends);
        EditText editTextParisContenders = findViewById(R.id.editTextParisContenders);

        Button buttonConfirm = findViewById(R.id.buttonConfirm);

        buttonConfirm.setOnClickListener(view -> {
            // Retrieve entered values
            String horizonCasesStr = editTextHorizonCases.getText().toString();
            String dangerZoneCasesStr = editTextDangerZoneCases.getText().toString();
            String prismaCasesStr = editTextPrismaCases.getText().toString();
            String prisma2CasesStr = editTextPrisma2Cases.getText().toString();
            String parisChallengersStr = editTextParisChallengers.getText().toString();
            String parisLegendsStr = editTextParisLegends.getText().toString();
            String parisContendersStr = editTextParisContenders.getText().toString();

            // Parse values to integers (set to 0 if empty)
            int horizonCases = TextUtils.isEmpty(horizonCasesStr) ? 0 : Integer.parseInt(horizonCasesStr);
            int dangerZoneCases = TextUtils.isEmpty(dangerZoneCasesStr) ? 0 : Integer.parseInt(dangerZoneCasesStr);
            int prismaCases = TextUtils.isEmpty(prismaCasesStr) ? 0 : Integer.parseInt(prismaCasesStr);
            int prisma2Cases = TextUtils.isEmpty(prisma2CasesStr) ? 0 : Integer.parseInt(prisma2CasesStr);
            int parisChallengers = TextUtils.isEmpty(parisChallengersStr) ? 0 : Integer.parseInt(parisChallengersStr);
            int parisLegends = TextUtils.isEmpty(parisLegendsStr) ? 0 : Integer.parseInt(parisLegendsStr);
            int parisContenders = TextUtils.isEmpty(parisContendersStr) ? 0 : Integer.parseInt(parisContendersStr);

            // Save the case amounts to SharedPreferences
            saveCasesToSharedPreferences(horizonCases, dangerZoneCases, prismaCases, prisma2Cases, parisChallengers, parisLegends, parisContenders);

            // Create an Intent to return the data to MainActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("horizonCases", horizonCases);
            resultIntent.putExtra("dangerZoneCases", dangerZoneCases);
            resultIntent.putExtra("prismaCases", prismaCases);
            resultIntent.putExtra("prisma2Cases", prisma2Cases);
            resultIntent.putExtra("parisChallengers", parisChallengers);
            resultIntent.putExtra("parisLegends", parisLegends);
            resultIntent.putExtra("parisContenders", parisContenders);

            // Set the result and finish the activity
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void saveCasesToSharedPreferences(int horizonCases, int dangerZoneCases, int prismaCases, int prisma2Cases, int parisChallengers, int parisLegends, int parisContenders) {
        // Save the case amounts to SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
        editor.putInt("horizonCases", horizonCases);
        editor.putInt("dangerZoneCases", dangerZoneCases);
        editor.putInt("prismaCases", prismaCases);
        editor.putInt("prisma2Cases", prisma2Cases);
        editor.putInt("parisChallengers", parisChallengers);
        editor.putInt("parisLegends", parisLegends);
        editor.putInt("parisContenders", parisContenders);
        editor.apply();
    }
}
