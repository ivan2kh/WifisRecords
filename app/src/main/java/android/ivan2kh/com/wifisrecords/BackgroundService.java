package android.ivan2kh.com.wifisrecords;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by john on 3/19/2016.
 */
public class BackgroundService extends IntentService{
    public static final String SCAN_FINISHED_RESP = "android.ivan2kh.com.wifisrecords.SCAN_FINISHED_RESP";
    public static final String SCAN_RESULT = "android.ivan2kh.com.wifisrecords.SCAN_RESULT";
    public static final String SCANS_COUNT = "android.net.wifi.SCANS_COUNT";
    public static final String SCANS_SCANS_PER_BROADCAST = "android.net.wifi.SCANS_SCANS_PER_BROADCAST";
    public static final String FILE_NAME = "android.net.wifi.FILE_NAME";
    public static final String START_SCAN_ACTION = "android.ivan2kh.com.wifisrecords.START_SCAN";
    public static final String STOP_SCAN_ACTION = "android.ivan2kh.com.wifisrecords.STOP_SCAN";

    private static WifiScanReceiver wifiReciever = null;
    private static WifiManager wifiManager = null;
    private static WifiManager.WifiLock wifiLock = null;

    private static String file_name="";
    private static int scans_count=0;
    private static int scans_per_browadcast=1;
    private static File file;

    public BackgroundService() {
        super(BackgroundService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_VOICE_CALL, 100);
        toneG.startTone(ToneGenerator.TONE_DTMF_0);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        toneG.stopTone();
        toneG.release();
    }
}
