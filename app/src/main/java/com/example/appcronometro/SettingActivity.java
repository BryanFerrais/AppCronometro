package com.example.appcronometro;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;


public class SettingActivity extends AppCompatActivity {

    public static Button btnAlarmOn;
    Switch switchSensor,switchNarrar,switchAlarmStopChrono;
    EditText edtAlarme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().hide();

        switchSensor = findViewById(R.id.switchSensor);
        switchNarrar = findViewById(R.id.switchNarrar);
        btnAlarmOn = findViewById(R.id.btnAlarmOn);
        edtAlarme = findViewById(R.id.edtAlarme);
        switchAlarmStopChrono = findViewById(R.id.switchAlarmStopChrono);

        Intent i = new Intent(SettingActivity.this, AlertReceiver.class);
        final PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);
        final AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

        if(MainActivity.sensorEnabled == 1){
            switchSensor.setChecked(true);
        }
        if(MainActivity.narrarEnabled == 1){
            switchNarrar.setChecked(true);
        }
        if(MainActivity.stopAlarmEnabled == 1){
            switchAlarmStopChrono.setChecked(true);
        }
        if(MainActivity.started == 1){
            btnAlarmOn.setText("Cancelar");
        }

        switchSensor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    MainActivity.sensorEnabled = 1;
                }
                else{
                    MainActivity.sensorEnabled = 0;
                }
            }
        });

        switchNarrar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    MainActivity.narrarEnabled = 1;
                }
                else{
                    MainActivity.narrarEnabled = 0;
                }
            }
        });

        switchAlarmStopChrono.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    MainActivity.stopAlarmEnabled = 1;
                }
                else{
                    MainActivity.stopAlarmEnabled = 0;
                }
            }
        });

        btnAlarmOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(MainActivity.started == 0) {
                   if (edtAlarme != null || !edtAlarme.getText().equals("")) {
                       int time = Integer.parseInt(edtAlarme.getText().toString());
                       am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time * 1000, pi);
                       MainActivity.started = 1;
                       btnAlarmOn.setText("Cancelar");
                   } else {
                       Toast.makeText(SettingActivity.this, "Coloque um numero primeiro.", Toast.LENGTH_SHORT).show();
                   }
               }
               else if(MainActivity.started == 1){
                   am.cancel(pi);
                   Toast.makeText(SettingActivity.this, "Alarme cancelado.", Toast.LENGTH_SHORT).show();
                   MainActivity.started = 0;
                   btnAlarmOn.setText("Iniciar");
               }
            }
        });


    }
    @Override
    public void onBackPressed(){
        finish();
    }
}