package io.tsh.countries.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Calendar;

import io.tsh.countries.json.UnixDateConverter;

/**
 * Created by klaud on 2015-12-25.
 */
public class PlaceGson {
    public static Gson getInstance(){
        return new GsonBuilder()
                .registerTypeAdapter(Calendar.class, new UnixDateConverter())
                .create();
    }
}
