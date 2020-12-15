package com.rubenramos.final_wear_app;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        String lightAsJson = getIntent().getStringExtra("jsonLight");
        String pressureAsJson = getIntent().getStringExtra("jsonPressure");
        String humidityAsJson = getIntent().getStringExtra("jsonHumidity");

        Gson gson = new Gson();

        Type type = new TypeToken<List<Dummy>>() {
        }.getType();

        List<Dummy> lightValues = gson.fromJson(lightAsJson, type);
        List<Dummy> pressureValues = gson.fromJson(pressureAsJson, type);
        List<Dummy> humidityValues = gson.fromJson(humidityAsJson, type);

        List<BarEntry> barEntriesLight = new ArrayList<>();
        List<BarEntry> barEntriesPressure = new ArrayList<>();
        List<BarEntry> barEntriesHumidty = new ArrayList<>();

        for(int i = 0; i < lightValues.size();i++){
            barEntriesLight.add(new BarEntry(1,lightValues.get(i).value));
            barEntriesPressure.add(new BarEntry(2,pressureValues.get(i).value));
            barEntriesHumidty.add(new BarEntry(3,humidityValues.get(i).value));
        }


        BarChart   barChart = findViewById(R.id.barChart);

        BarDataSet barDataSetLight = new BarDataSet(barEntriesLight,"Light");
        BarDataSet barDataSetPressure = new BarDataSet(barEntriesPressure,"Pressure");
        BarDataSet barDataSetHumidity = new BarDataSet(barEntriesHumidty,"Humidity");



        barDataSetLight.setColors(Color.RED);
        barDataSetLight.setValueTextColor(Color.BLACK);
        barDataSetLight.setValueTextSize(16f);

        barDataSetPressure.setColors(Color.BLUE);
        barDataSetPressure.setValueTextColor(Color.BLACK);
        barDataSetPressure.setValueTextSize(16f);

        barDataSetHumidity.setColors(Color.GREEN);
        barDataSetHumidity.setValueTextColor(Color.BLACK);
        barDataSetHumidity.setValueTextSize(16f);



        BarData barData = new BarData(barDataSetLight, barDataSetHumidity, barDataSetPressure);
        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("Chart of values");
        barChart.animateY(2000);

    }
}