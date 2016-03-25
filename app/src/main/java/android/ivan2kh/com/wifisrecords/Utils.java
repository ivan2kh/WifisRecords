package android.ivan2kh.com.wifisrecords;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ivan2kh on 3/22/2016.
 */
public class Utils {
    private static Map<Context, SharedPreferences> preferences = new HashMap<Context, SharedPreferences>();

    public static SharedPreferences getSharedPreferences(Context context) {
        SharedPreferences pref = preferences.get(context);
        if (pref == null) {
            pref = PreferenceManager.getDefaultSharedPreferences(context);
            preferences.put(context, pref);
        }

        return pref;
    }

    public static boolean getBooleanPreference(Context context, int resId, boolean defaultValue) {
        return getSharedPreferences(context).getBoolean(context.getString(resId), defaultValue);
    }

    public static String getStringPreference(Context context, int resId, String defaultValue) {
        return getSharedPreferences(context).getString(context.getString(resId), defaultValue);
    }

    public static int getIntPreference(Context context, int resId, int defaultValue) {
        return getSharedPreferences(context).getInt(context.getString(resId), defaultValue);
    }

    public static void savePreference(Context context, int resId, String newValue) {
        SharedPreferences mPreferences = Utils.getSharedPreferences(context);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(context.getString(resId), newValue);
        editor.commit();
    }

    public static void savePreference(Context context, int resId, int newValue) {
        SharedPreferences mPreferences = Utils.getSharedPreferences(context);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(context.getString(resId), newValue);
        editor.commit();
    }

    public static void savePreference(Context context, int resId, boolean newValue) {
        SharedPreferences mPreferences = Utils.getSharedPreferences(context);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(context.getString(resId), newValue);
        editor.commit();
    }

    public static void removePreference(Context context, int resId) {
        SharedPreferences mPreferences = Utils.getSharedPreferences(context);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(context.getString(resId));
        editor.commit();
    }

}
