package com.example.elincasesapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class MainActivity extends AppCompatActivity {

    private Button mCalculateButton;
    private Button mSetCashButton; // new button for setting cash
    private Button mSetCasesButton;
    private static final int REQUEST_CODE_SET_CASES = 1;

    double EURO_TO_SEK = 11.59;
    int CASH;
    private TextView mResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCalculateButton = findViewById(R.id.calculateButton);
        mResultTextView = findViewById(R.id.print);
        mSetCashButton = findViewById(R.id.cash);
        mSetCasesButton = findViewById((R.id.setCasesButton));
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        CASH = prefs.getInt("cash", 0);
        mSetCashButton.setText("Set Cash (" + CASH + ")");

        mCalculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);

                int horizonCaseCount = prefs.getInt("horizonCases", 0);
                int dangerZoneCaseCount = prefs.getInt("dangerZoneCases", 0);
                int prismaCaseCount = prefs.getInt("prismaCases", 0);
                int prisma2CaseCount = prefs.getInt("prisma2Cases", 0);
                int paris2023ChallengersStickerCapsuleCount = prefs.getInt("parisChallengers", 0);
                int paris2023LegendsStickerCapsuleCount = prefs.getInt("parisLegends", 0);
                int paris2023ContendersStickerCapsuleCount = prefs.getInt("parisContenders", 0);


                new FetchPricesTask().execute(horizonCaseCount, dangerZoneCaseCount, prismaCaseCount, prisma2CaseCount,
                        paris2023ChallengersStickerCapsuleCount, paris2023LegendsStickerCapsuleCount, paris2023ContendersStickerCapsuleCount);

            }
        });

        mCalculateButton.performClick();

        mSetCasesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Code to launch SetCasesActivity goes here
                Intent intent = new Intent(MainActivity.this, SetCasesActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SET_CASES);
            }
        });

        mSetCashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editText = new EditText(MainActivity.this);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setText(String.valueOf(CASH));
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Set Cash")
                        .setMessage("Enter the amount of cash you have:")
                        .setView(editText)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String input = editText.getText().toString();
                                if (!TextUtils.isEmpty(input)) {
                                    // Save the cash value to SharedPreferences
                                    SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
                                    CASH = Integer.parseInt(input);
                                    editor.putInt("cash", CASH);
                                    editor.apply();
                                    // Update the text of the button to reflect the new value
                                    mSetCashButton.setText("Set Cash (" + CASH + ")");
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }

    public class FetchPricesTask extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... caseCounts) {
            String result = "";
            double totalLowestValue = 0;
            double totalMedianValue = 0;
            String[] cases = {"Horizon Case", "Danger Zone Case", "Prisma Case", "Prisma 2 Case", "Paris 2023 Challengers Sticker Capsule", "Paris 2023 Legends Sticker Capsule", "Paris 2023 Contenders Sticker Capsule" };

            for (int i = 0; i < cases.length; i++) {
                String caseName = cases[i];
                int caseCount = caseCounts[i];

                try {
                    URL url = new URL("https://steamcommunity.com/market/priceoverview/?appid=730&currency=3&market_hash_name=" + caseName.replaceAll(" ", "%20"));
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuilder builder = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    reader.close();
                    JSONObject json = new JSONObject(builder.toString());
                    String lowestPrice = json.getString("lowest_price").replaceAll("[^\\d.,]+", "");
                    String medianPrice = json.getString("median_price").replaceAll("[^\\d.,]+", "");

                    double lowestValue = caseCount * Double.parseDouble(lowestPrice.replaceAll(",", "."));
                    totalLowestValue += lowestValue;

                    double medianValue = caseCount * Double.parseDouble(medianPrice.replaceAll(",", "."));
                    totalMedianValue += medianValue;

                    result += caseName + " Amount: " + caseCount + "\n";
                    result += "Lowest Price: " + lowestPrice + "\n";
                    result += "Value (lowest price): " + String.format("%.0f", lowestValue) + "€\n";
                    result += "Median Price: " + medianPrice + "\n";
                    result += "Value (median price): " + String.format("%.0f", medianValue) + "€\n\n";
                } catch (Exception e) {
                    return "Failed to fetch " + caseName + " value";
                }
            }
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator(' ');
            DecimalFormat df = new DecimalFormat("#,##0", symbols);

            //add cash
            totalLowestValue += CASH;
            totalMedianValue += CASH;

            String formattedTotalValue = df.format(totalLowestValue);
            String formattedTotalMedianValue = df.format(totalMedianValue);

            result += "Total Value in EURO (lowest price): " + formattedTotalValue + " €\n";
            result += "Total Value in SEK (lowest price): " + df.format(totalLowestValue * EURO_TO_SEK) + " SEK\n\n";
            result += "Total Value in EURO (median price): " + formattedTotalMedianValue + " €\n";
            result += "Total Value in SEK (median price): " + df.format(totalMedianValue * EURO_TO_SEK) + " SEK\n";
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            mResultTextView.setText(result);
        }
    }
}
