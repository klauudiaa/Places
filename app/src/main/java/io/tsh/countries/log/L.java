package io.tsh.countries.log;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by klaud on 2015-12-23.
 */
public class L {
    private static final String TAG = "Countries";
    public static void m(String message) {
        Log.d(TAG, "" + message);
    }

    public static void t(Context context, String message) {
        Toast.makeText(context, message + "", Toast.LENGTH_SHORT).show();
    }

    public static void e(String message) {
        Log.e(TAG, "" + message);
    }

}
