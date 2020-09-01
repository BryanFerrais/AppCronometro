package com.example.appcronometro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Locale;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public  static Chronometer chrono;
    public static Sensor sAcelerometro;
    TextView txtLast;
    Button btnCmd,btnSaveTempos,btnSettings,btnCloseApp;
    LinearLayoutCompat layoutSTT;
    public static TextToSpeech tts;
    ListView lvTempos;

    public static byte sensorEnabled = 0;
    public static byte narrarEnabled = 0;
    public static byte stopAlarmEnabled = 0;
    public static byte chronoStart = 0;
    public static byte started = 0;

    public static SensorManager mSensores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        final ListClass L = new ListClass();
        final ArrayList<String> lista = new ArrayList<String>();

        mSensores = (SensorManager) getSystemService(SENSOR_SERVICE);

        sAcelerometro = mSensores.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        txtLast = findViewById(R.id.txtLast);
        chrono = findViewById(R.id.chrono);
        btnCmd = findViewById(R.id.btnCmd);
        lvTempos = findViewById(R.id.lvTempos);
        btnSaveTempos = findViewById(R.id.btnSaveTempos);
        btnSettings = findViewById(R.id.btnSettings);
        btnCloseApp = findViewById(R.id.btnCloseApp);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,lista);

        lvTempos.setAdapter(adapter);

        layoutSTT = findViewById(R.id.layoutSST);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR){
                    tts.setLanguage(Locale.getDefault());
                }
            }
        });

        mSensores.registerListener(
                new Acelerometro(),
                sAcelerometro,
                SensorManager.SENSOR_DELAY_GAME
        );

        btnSaveTempos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtC = chrono.getText().toString();
                txtLast.setText(txtC);
                L.addTempos(lista,adapter,txtC);
            }
        });

        btnCmd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capturar();
            }
        });

        lvTempos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                lista.remove(i);
                adapter.notifyDataSetChanged();
                return true;
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                janelaSettings();
            }
        });

        btnCloseApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });

    }//Oncreate

    class Acelerometro implements SensorEventListener{

        @Override
        public void onSensorChanged(SensorEvent event) {
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];


            if(sensorEnabled == 1) {
                if (x > 12) {
                    if (chronoStart == 0) {
                        chronoStart = 1;
                        chrono.setBase(SystemClock.elapsedRealtime());
                        chrono.start();
                    }
                } else if (x < -12) {
                    if (chronoStart == 1) {
                        chronoStart = 0;
                        String txtC = chrono.getText().toString();
                        chrono.stop();
                        if(narrarEnabled == 1) {
                            narrar("00:" + txtC);
                        }
                    }
                }//else if
            }//if
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }

    private void janelaSettings(){
        Intent janela = new Intent(this, SettingActivity.class);
        startActivity(janela);
    }

    private void capturar(){
        Intent iSTT = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        iSTT.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        iSTT.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        startActivityForResult(iSTT, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> resultado =
                        data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String textoReconhecido = resultado.get(0);

                if(textoReconhecido.toUpperCase().contains("FECHAR A APLICAÇÃO")
                        ||textoReconhecido.toUpperCase().contains("FECHAR APLICAÇÃO")){
                    finishAffinity();
                }
                if(textoReconhecido.toUpperCase().contains("ZERAR ÚLTIMO TEMPO SALVO")
                        ||textoReconhecido.toUpperCase().contains("ZERAR O ÚLTIMO TEMPO SALVO")||
                        textoReconhecido.toUpperCase().contains("ZERAR ÚLTIMO TEMPO")
                        ||textoReconhecido.toUpperCase().contains("ZERAR O ÚLTIMO TEMPO")
                        ){
                    txtLast.setText("00:00");
                }
                if(textoReconhecido.toUpperCase().contains("START")
                        ||textoReconhecido.toUpperCase().contains("COMEÇAR")){
                    if (chronoStart == 0) {
                        chronoStart = 1;
                        chrono.setBase(SystemClock.elapsedRealtime());
                        chrono.start();
                    }

                }
                else if(textoReconhecido.toUpperCase().contains("STOP")
                        ||textoReconhecido.toUpperCase().contains("PARAR")){
                    if (chronoStart == 1) {
                        chronoStart = 0;
                        String txtC = chrono.getText().toString();
                        chrono.stop();
                        if(narrarEnabled == 1) {
                            narrar("00:" + txtC);
                        }
                    }

                }
            }
        }
    }

    private void narrar(String texto){
        tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null);
    }

}