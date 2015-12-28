package io.tsh.countries.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.TimeZone;

import io.tsh.countries.model.Place;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by klaud on 2015-12-23.
 */
public class UnixDateConverterTest {

    Gson gson;
    String jsonString;

    @Before
    public void setUpGson(){
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Calendar.class, new UnixDateConverter());
        gson = gsonBuilder.create();
    }

    @After
    public void afterTests(){
        TimeZone.setDefault(null);
    }

    @Test
    public void jsonToDate(){
        jsonString ="1443183071";//Fri, 25 Sep 2015 12:11:11 GMT
        Calendar date = gson.fromJson(jsonString, Calendar.class);
        assertTrue(date.get(Calendar.DAY_OF_MONTH)==   25);
        assertTrue(date.get(Calendar.MONTH)       ==    8);
        assertTrue(date.get(Calendar.YEAR)        == 2015);
        assertTrue(date.get(Calendar.HOUR_OF_DAY) ==   12);
        assertTrue(date.get(Calendar.MINUTE)      ==   11);
        assertTrue(date.get(Calendar.SECOND)      ==   11);
    }

    @Test
    public void jsonToDate2(){
        jsonString ="1450869236";//(GMT): Wed, 23 Dec 2015 11:13:56 GMT
        Calendar date = gson.fromJson(jsonString, Calendar.class);
        assertTrue(date.get(Calendar.DAY_OF_MONTH)==   23);
        assertTrue(date.get(Calendar.MONTH)       ==   11);
        assertTrue(date.get(Calendar.YEAR)        == 2015);
        assertTrue(date.get(Calendar.HOUR_OF_DAY) ==   11);
        assertTrue(date.get(Calendar.MINUTE)      ==   13);
        assertTrue(date.get(Calendar.SECOND)      ==   56);
    }

    @Test
    public void dateToJson(){
        String result = null;
        jsonString ="1443183071";//Fri, 25 Sep 2015 12:11:11 GMT
        Calendar date = gson.fromJson(jsonString, Calendar.class);
        date.set(Calendar.DAY_OF_MONTH,   25);
        date.set(Calendar.MONTH       ,    8);
        date.set(Calendar.YEAR        , 2015);
        date.set(Calendar.HOUR_OF_DAY ,   12);
        date.set(Calendar.MINUTE      ,   11);
        date.set(Calendar.SECOND      ,   11);

        result = gson.toJson(date, Calendar.class);
        assertEquals(jsonString, result);
    }

    @Test
    public void dateToJson2(){
        jsonString ="1450869236";//(GMT): Wed, 23 Dec 2015 11:13:56 GMT
        String result = null;
        Calendar date = Calendar.getInstance();
        date.set(Calendar.DAY_OF_MONTH,   23);
        date.set(Calendar.MONTH       ,   11);
        date.set(Calendar.YEAR        , 2015);
        date.set(Calendar.HOUR_OF_DAY ,   11);
        date.set(Calendar.MINUTE      ,   13);
        date.set(Calendar.SECOND      ,   56);

        result = gson.toJson(date, Calendar.class);
        assertEquals(jsonString, result);
    }


    @Test
    public void fromJsonTest(){
        jsonString = "{\"id\":18," +
        "\"name\":\"Australia\""   +
        ",\"picture_url\":\"http://serene-mountain-2455.herokuapp.com/images/australia.jpg\" "+
        ",\"date\":1443183071}"; //Fri, 25 Sep 2015 12:11:11 GMT

        Place p = gson.fromJson(jsonString, Place.class);
        assertTrue( p.getId() == 18  );
        assertTrue( p.getName().equals("Australia"));
        assertTrue( p.getPictureUrl().equals("http://serene-mountain-2455.herokuapp.com/images/australia.jpg") );

        assertNotNull( p.getDate() );
        assertNull   ( p.getDescription() );
        assertNull   ( p.getSeeMoreUrl()  );

    }
}
