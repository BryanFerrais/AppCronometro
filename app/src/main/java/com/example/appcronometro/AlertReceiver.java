package com.example.appcronometro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context,"ALARME",Toast.LENGTH_LONG).show();
        Vibrator v = (Vibrator)context.getSystemService(context.VIBRATOR_SERVICE);
        v.vibrate(3000);
        MainActivity.started = 0;
        SettingActivity.btnAlarmOn.setText("Iniciar");
        if(MainActivity.stopAlarmEnabled == 1){
            if (MainActivity.chronoStart == 1) {
                MainActivity.chronoStart = 0;
                MainActivity.chrono.stop();
                if(MainActivity.narrarEnabled == 1) {
                    String txtC = MainActivity.chrono.getText().toString();
                    narrar("00:" + txtC);
                }
            }
        }
    }
    private void narrar(String texto){
        MainActivity.tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null);
    }
}
