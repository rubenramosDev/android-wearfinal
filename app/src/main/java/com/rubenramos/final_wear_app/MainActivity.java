package com.rubenramos.final_wear_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private Sensor pressureSensor;
    private Sensor humiditySensor;
    private Boolean flag;
    private Button buttonFlag, buttonData,buttonCalculer;

    private TextView
            txtRPressure, txtRLight, txtRHumidity,
            txtLightHigher, txtLightMin, txtLightAvg,
            txtHiger, txtLower, txtAvg, txtPressureHigher,
            txtPressureAvg, txtPressureMin;

    private final String LIGHT_CONST = "LIGHT";
    private final String HUMIDITY_CONST = "HUMIDITY";
    private final String PRESSURE_CONST = "PRESSURE";


    private ArrayList<Dummy> lightValues    = new ArrayList<>();
    private ArrayList<Dummy> pressureValues = new ArrayList<>();
    private ArrayList<Dummy> humidityValues = new ArrayList<>();


    private DatabaseReference firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initElements();
        initSensors();
        listener();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        readValues();

    }

    private void listener() {
        buttonFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag) {
                    flag = false;
                    buttonFlag.setText("ON");
                } else {
                    flag = true;
                    buttonFlag.setText("OFF");
                }
            }
        });
        buttonData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                String jsonLight     = gson.toJson(lightValues);
                String jsonPressure  = gson.toJson(pressureValues);
                String jsonHumidity  = gson.toJson(humidityValues);

                Intent intent = new Intent(view.getContext(), DataActivity.class);

                intent.putExtra("jsonLight", jsonLight);
                intent.putExtra("jsonPressure", jsonPressure);
                intent.putExtra("jsonHumidity", jsonHumidity);

                startActivity(intent);
            }
        });
        buttonCalculer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calcul();
            }
        });
    }

    private void initElements() {
        flag = false;
        txtRPressure = findViewById(R.id.txtRPressure);
        txtRLight = findViewById(R.id.txtRLight);
        txtRHumidity = findViewById(R.id.txtRHumidity);
        buttonFlag = findViewById(R.id.btnFlag);
        buttonData = findViewById(R.id.btnData);
        buttonCalculer = findViewById(R.id.btnCalculer);
        txtAvg = findViewById(R.id.txtAvg);
        txtHiger = findViewById(R.id.txtHigher);
        txtLower = findViewById(R.id.txtLower);
        txtLightHigher = findViewById(R.id.txtLightHigher);
        txtLightMin = findViewById(R.id.txtLightMin);
        txtLightAvg = findViewById(R.id.txtLightAvg);
        txtPressureHigher = findViewById(R.id.txtPressureHigher);
        txtPressureAvg = findViewById(R.id.txtPressureAvg);
        txtPressureMin = findViewById(R.id.txtPressureMin);

    }

    private void initSensors() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
    }

    private void readValues() {
        FirebaseDatabase messagesHumidity   = FirebaseDatabase.getInstance();
        DatabaseReference humidityReference = messagesHumidity.getReference(HUMIDITY_CONST);
        DatabaseReference lightReference    = messagesHumidity.getReference(LIGHT_CONST);
        DatabaseReference pressureReference = messagesHumidity.getReference(PRESSURE_CONST);


        humidityReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                showData(snapshot, HUMIDITY_CONST);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        lightReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                showData(snapshot, LIGHT_CONST);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        pressureReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                showData(snapshot, PRESSURE_CONST);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void showData(DataSnapshot snapshot, String value) {
        switch (value) {
            case LIGHT_CONST:
                for (DataSnapshot data : snapshot.getChildren()) {
                    Dummy dummy = data.getValue(Dummy.class);
                    System.out.println("Light"+dummy.getValue());
                    lightValues.add(dummy);
                }
                break;
            case PRESSURE_CONST:
                for (DataSnapshot data : snapshot.getChildren()) {
                    Dummy dummy = data.getValue(Dummy.class);
                    System.out.println("Pressure"+dummy.getValue());
                    pressureValues.add(dummy);
                }
                break;
            case HUMIDITY_CONST:
                for (DataSnapshot data : snapshot.getChildren()) {
                    Dummy dummy = data.getValue(Dummy.class);
                    System.out.println("Humidity"+dummy.getValue());
                    humidityValues.add(dummy);
                }
                break;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (humiditySensor != null) {
            sensorManager.registerListener(this, humiditySensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (pressureSensor != null) {
            sensorManager.registerListener(this, pressureSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Integer sensorType = sensorEvent.sensor.getType();
        Float currentValue = sensorEvent.values[0];

        DatabaseReference messagesRef = firebaseDatabase.getRoot();


        switch (sensorType) {
            case Sensor.TYPE_LIGHT:
                if (flag) {
                    txtRLight.setText(currentValue.toString());
                    messagesRef.child(LIGHT_CONST).push().setValue(new Dummy(currentValue));
                }
                break;

            case Sensor.TYPE_RELATIVE_HUMIDITY:
                if (flag) {
                    txtRHumidity.setText(currentValue.toString());
                    messagesRef.child(HUMIDITY_CONST).push().setValue(new Dummy(currentValue));
                }
                break;

            case Sensor.TYPE_PRESSURE:
                if (flag) {
                    txtRPressure.setText(currentValue.toString());
                    messagesRef.child(PRESSURE_CONST).push().setValue(new Dummy(currentValue));
                }
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void calcul() {
        Double humidityValue = 0.0;
        Double lightValue = 0.0;
        Double pressureValue = 0.0;
        List<Float> humidityCleanValues = new ArrayList<>();
        List<Float> lightCleanValues    = new ArrayList<>();
        List<Float> pressureCleanValues = new ArrayList<>();

        for (Dummy item : lightValues) {
            lightValue += item.getValue();
            lightCleanValues.add(item.getValue());
        }
        for (Dummy item : humidityValues) {
            humidityValue += item.getValue();
            humidityCleanValues.add(item.getValue());
        }
        for (Dummy item : pressureValues) {
            pressureValue += item.getValue();
            pressureCleanValues.add(item.getValue());
        }


        txtHiger.setText(String.valueOf(Collections.max(humidityCleanValues)));
        txtLower.setText(String.valueOf(Collections.min(humidityCleanValues)));
        txtAvg.setText(String.valueOf(humidityValue / humidityValues.size()));

        txtLightHigher.setText(String.valueOf(Collections.max(lightCleanValues)));
        txtLightMin.setText(String.valueOf(Collections.min(lightCleanValues)));
        txtLightAvg.setText(String.valueOf(lightValue / lightValues.size()));

        txtPressureHigher.setText(String.valueOf(Collections.max(pressureCleanValues)));
        txtPressureMin.setText(String.valueOf(Collections.min(pressureCleanValues)));
        txtPressureAvg.setText(String.valueOf(pressureValue / pressureCleanValues.size()));

    }

}