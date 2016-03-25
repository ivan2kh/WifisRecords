package android.ivan2kh.com.wifisrecords;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by ivan2kh on 3/22/2016.
 */
public class WifiScanReceiver extends BroadcastReceiver {
    private static String TAG = WifiScanReceiver.class.getSimpleName();

    public static final String SCAN_TICK_RESP = "android.ivan2kh.com.wifisrecords.SCAN_TICK_RESP";
    public static final String SCANS_TICK  = "android.net.wifi.SCANS_TICK";

    public static int scans_count = 0;
    private static WifiManager wifiManager = null;
    private static int scans_per_browadcast = 1;
    private static String fileName = "";
    private static int beep_id  = 0;

    public static void writeFile(String filename,
                           String line) {
        try {
            File file = new File(filename);
            FileOutputStream stream  = new FileOutputStream(file, true);
            try {
                stream.write(line.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
            fileName = Utils.getStringPreference(context, R.string.file_name, "");
            if(fileName.isEmpty())
                return;

            scans_per_browadcast = Utils.getIntPreference(context, R.string.scans_per_browadcast, 1);
            beep_id = Utils.getIntPreference(context, R.string.beep_id, 1);

            if(wifiManager == null)
                wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            scans_count = scans_count + 1;
            List<ScanResult> wifiScanList = wifiManager.getScanResults();

            boolean startScanRes = wifiManager.startScan();

            double timestame = System.currentTimeMillis() / 1000.0;

            String lines="";
            for (int i = 0; i < wifiScanList.size(); i++) {
                ScanResult res = wifiScanList.get(i);

                String line = String.format(Locale.US, "\"%s\", \"%s\", %d, %d, %.3f\n", res.SSID,
                        res.BSSID,
                        res.level,
                        res.frequency,
                        timestame);
                lines = lines + line;
            }

            writeFile(fileName, lines);

            Intent tickIntent = new Intent(SCAN_TICK_RESP);
            tickIntent.putExtra(SCANS_TICK, scans_count / scans_per_browadcast);
            context.sendBroadcast(tickIntent);

            if(beep_id == R.id.cbBeepOnTick) {
                Intent service = new Intent(context, BackgroundService.class);
                // Start the service, keeping the device awake while it is launching.
                context.startService(service);
            }
        }
    }
}
