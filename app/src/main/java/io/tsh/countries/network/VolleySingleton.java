package io.tsh.countries.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;

import io.tsh.countries.log.L;

/**
 * Created by klaud on 2015-12-23.
 */
public class VolleySingleton {
    private static VolleySingleton sInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private Context mContext;

    private VolleySingleton(Context context){
        mContext = context;
        // Instantiate the cache
        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);


        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {

            private LruCache<String, Bitmap> cache = new LruCache<>((int)((Runtime.getRuntime().maxMemory()/1024)/8));

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });

        mRequestQueue.start();
    }

    public static synchronized  VolleySingleton getInstance(Context context){
        if(sInstance == null){
            sInstance = new VolleySingleton(context);
        }
        return sInstance;
    }

    public RequestQueue getRequestQueue(){
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
