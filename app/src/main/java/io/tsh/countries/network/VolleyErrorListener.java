package io.tsh.countries.network;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import io.tsh.countries.R;
import io.tsh.countries.log.L;

/**
 * Created by klaud on 2015-12-25.
 */
public class VolleyErrorListener implements Response.ErrorListener {
    private Context mContext;

    public VolleyErrorListener(Context context) {
        this.mContext = context;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        try {
            L.t(mContext, mContext.getString(R.string.error_while_loading) + error.getMessage() +
                    "\n" + mContext.getString(R.string.error_code) + error.networkResponse.statusCode);
        }catch(NullPointerException exc){
            L.e( error.getMessage());
        }
    }
}
