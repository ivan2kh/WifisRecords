package android.ivan2kh.com.wifisrecords;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by ivan2kh on 3/23/2016.
 */
public class BeepReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent service = new Intent(context, BackgroundService.class);
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, service);
    }
}
