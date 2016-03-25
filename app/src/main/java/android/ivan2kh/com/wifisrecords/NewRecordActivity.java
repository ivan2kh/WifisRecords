package android.ivan2kh.com.wifisrecords;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewRecordActivity extends Activity {
    public static final String FILE_NAME = "android.ivan2kh.com.wifisrecords.FILE_NAME";

    public enum State {
        EDITING, RECORDING
    }

    private final ResponseReceiver receiver = new ResponseReceiver();
    private TextView counterView;
    private EditText commentEdit;
    private EditText measuresEdit;
    private CheckBox cbBeepOnTick;
    private RadioButton cbBeep05;
    private RadioButton cbBeep06;
    private RadioButton cbBeep10;
    private RadioGroup radioBeep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_record);
        counterView = (TextView)findViewById(R.id.textViewCounter);
        commentEdit = (EditText)findViewById(R.id.editRecordComment);
        measuresEdit = (EditText)findViewById(R.id.editRecordDelay);

//        cbBeepOnTick = (CheckBox)findViewById(R.id.cbBeepOnTick);
//        cbBeep05 = (RadioButton)findViewById(R.id.cbBeep05);
//        cbBeep06 = (RadioButton)findViewById(R.id.cbBeep06);
//        cbBeep10 = (RadioButton)findViewById(R.id.cbBeep10);
        radioBeep = (RadioGroup)findViewById(R.id.radioBeep);
    }

    protected State getState() {
        String fileName = Utils.getStringPreference(this, R.string.file_name, "");
        return fileName.isEmpty() ? State.EDITING : State.RECORDING;
    }

    public void onStartButton(View view) {
        if(getState() == State.EDITING) {
            int scans_per_browadcast = Integer.parseInt(measuresEdit.getText().toString());
            String file_name = commentEdit.getText().toString();
            if(file_name.isEmpty())
                file_name = "dummy";

            startWifiReceiver(file_name, scans_per_browadcast);

            counterView.setText(Integer.toString(0));
        } else {
            stop();
        }
        updateView();
    }

    private String getBeepType() {
        switch (radioBeep.getCheckedRadioButtonId()) {
            case R.id.cbBeep05: return "beep every 0.5s";
            case R.id.cbBeep06: return "beep every 0.6s";
            case R.id.cbBeep10: return "beep every 1.0s";
            case R.id.cbBeepOnTick: return "beep on tick";
        }
        return "no beep";
    }

    private void startWifiReceiver(String comment, int scans_per_browadcast) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm");
        String date = format.format(new Date());

        String file_name = String.format(Locale.US, "%s %d %s.csv",
                date,
                scans_per_browadcast,
                comment.replaceAll("\\W+", " "));

        WifiScanReceiver.scans_count = 0;

        File extDir = Environment.getExternalStoragePublicDirectory("WifiRecords");;
        File file = new File(extDir, file_name);
        WifiScanReceiver.writeFile(file.getAbsolutePath(),
                String.format(Locale.US,
                        "#%s\n#scans_per_browadcast %s\n#%s\n%s\n",
                        comment,
                        scans_per_browadcast,
                        getBeepType(),
                        "SSID,BSSID,level,frequency,timestamp"));

        Utils.savePreference(this, R.string.scans_per_browadcast, scans_per_browadcast);
        Utils.savePreference(this, R.string.file_name, file.getAbsolutePath());
        Utils.savePreference(this, R.string.beep_id, radioBeep.getCheckedRadioButtonId());
        enableWifiReceiver(true);

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
    }

    private void setupBeepReceiver(boolean enabled) {
        //Kill old intent
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarm = new Intent(this, BeepReceiver.class);
        PendingIntent oldIntent = PendingIntent.getBroadcast(this, 0, alarm, PendingIntent.FLAG_NO_CREATE);
        if(oldIntent != null) {
            alarmManager.cancel(oldIntent);
        }
        if(enabled == false) return; //nothing to do

        //Setup new intent
        int timeout = 1000;
        switch (radioBeep.getCheckedRadioButtonId()) {
            case R.id.cbBeep05: timeout = 500; break;
            case R.id.cbBeep06: timeout = 600; break;
            case R.id.cbBeep10: timeout = 1000; break;
            case R.id.cbBeepOnTick:
            default: return;
        }
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarm, 0);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), timeout, pendingIntent);
    }

    private void enableWifiReceiver(boolean enabled) {
        setupBeepReceiver(enabled);

        int flag=(enabled ?
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
        ComponentName component=new ComponentName(this, WifiScanReceiver.class);

        getPackageManager()
                .setComponentEnabledSetting(component, flag,
                        PackageManager.DONT_KILL_APP);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(WifiScanReceiver.SCAN_TICK_RESP));
        updateView();
//        registerReceiver(receiver, new IntentFilter(BackgroundService.SCAN_FINISHED_RESP));
    }

    private void updateView() {
        int commonVisibility;
        TextView startBtn = (TextView) findViewById(R.id.buttonStartRecord);
        if(getState() == State.EDITING) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            commonVisibility = View.VISIBLE;
            counterView.setVisibility(View.INVISIBLE);
            startBtn.setText("START");
            startBtn.setBackgroundResource(R.drawable.buttonshape);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            commonVisibility = View.INVISIBLE;
            counterView.setVisibility(View.VISIBLE);
            startBtn.setText("STOP");
            startBtn.setBackgroundResource(R.drawable.buttonshapered);
        }
        findViewById(R.id.textView).setVisibility(commonVisibility);
        findViewById(R.id.textView2).setVisibility(commonVisibility);
        findViewById(R.id.editRecordComment).setVisibility(commonVisibility);
        findViewById(R.id.editRecordDelay).setVisibility(commonVisibility);

        radioBeep.setVisibility(commonVisibility);
    }

    private void stop() {
        String fileName = Utils.getStringPreference(this, R.string.file_name, "");
        Utils.removePreference(this, R.string.scans_per_browadcast);
        Utils.removePreference(this, R.string.file_name);
        enableWifiReceiver(false);

        MediaScannerConnection.scanFile(this, new String[]{fileName}, null, null);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Intent intent = new Intent();
        intent.putExtra(FILE_NAME, fileName);
        setResult(RESULT_OK, intent);
        finish();
    }

    public class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiScanReceiver.SCAN_TICK_RESP)) {
                int tick = intent.getIntExtra(WifiScanReceiver.SCANS_TICK, 0);
                counterView.setText(Integer.toString(tick));
            } else if (intent.getAction().equals(BackgroundService.SCAN_FINISHED_RESP)) {
                stop();
            }
        }
    }
}
