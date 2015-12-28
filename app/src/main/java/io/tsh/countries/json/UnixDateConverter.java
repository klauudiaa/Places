package io.tsh.countries.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.TimeZone;

import io.tsh.countries.log.L;

/**
 * Created by klaud on 2015-12-23.
 * Serializes Date Type -> UnixDateType and deserializes UnixDateType -> Date Type.
 * Has to be registered with GsonBuilder.
 */
public class UnixDateConverter implements JsonSerializer<Calendar>, JsonDeserializer<Calendar> {


    @Override
    public JsonElement serialize(Calendar src, Type typeOfSrc, JsonSerializationContext context) {

        int unixDate = toUnixDate(src);

        return new JsonPrimitive(unixDate);
    }

    @Override
    public Calendar deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        int unixDate = 0;
        Calendar date = null;

        try {
            unixDate = json.getAsInt();
            date = toDate(unixDate);
        } catch (IllegalArgumentException e){
            L.e(e.getMessage());
        }

        return date;
    }

    /**
     * Converts seconds into Date.
     * @param unixDate time in seconds
     * @return date
     */
    public static Calendar toDate(int unixDate){
        Calendar instance = Calendar.getInstance(TimeZone.getDefault());
        instance.setTimeInMillis((long)unixDate*1000);

        return instance;
    }

    /**
     * Converts date into seconds.
     * @param calendar
     * @return seconds since Jan 1, 1970
     */
    public static int toUnixDate(Calendar calendar){
        int unixDate = -1;
        try{
            unixDate = (int)(calendar.getTimeInMillis()/1000);
        }catch (NullPointerException e) {
            L.e(e.getMessage());
        }
        return unixDate;
    }


}
