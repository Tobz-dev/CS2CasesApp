package com.example.elincasesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
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
    double EURO_TO_SEK = 11.23;
    private TextView mResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCalculateButton = findViewById(R.id.calculateButton);
        mResultTextView = findViewById(R.id.print);

        mCalculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int horizonCaseCount = 40600;
                int dangerZoneCaseCount = 33985;
                int prismaCaseCount = 33600;
                int prisma2CaseCount = 22798;

                new FetchPricesTask().execute(horizonCaseCount, dangerZoneCaseCount, prismaCaseCount, prisma2CaseCount);
            }
        });


    }

    private class FetchPricesTask extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... caseCounts) {
            String result = "";
            double totalLowestValue = 0;
            double totalMedianValue = 0;
            String[] cases = {"Horizon Case", "Danger Zone Case", "Prisma Case", "Prisma 2 Case"};

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

                    result += caseName + ":\n";
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

