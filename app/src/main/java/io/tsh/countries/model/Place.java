package io.tsh.countries.model;

import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import io.tsh.countries.log.L;

/**
 * Created by klaud on 2015-12-23.
 */
public class Place implements Comparable<Place>{

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("picture_url")
    private String pictureUrl;

    @SerializedName("date")
    private Calendar date;

    @SerializedName("description")
    private String description;

    @SerializedName("see_more_url")
    private String seeMoreUrl;

    public Place() {
       // required by Gson
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSeeMoreUrl() {
        return seeMoreUrl;
    }

    public void setSeeMoreUrl(String seeMoreUrl) {
        this.seeMoreUrl = seeMoreUrl;
    }

    @Override
    public int compareTo(Place another) {
        return Comparators.DATE_ASC.compare(this, another);
    }



    public static class Comparators {

        public static Comparator<Place> NAME = new Comparator<Place>() {
            @Override
            public int compare(Place o1, Place o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
        public static Comparator<Place> DATE_ASC = new Comparator<Place>() {
            @Override
            public int compare(Place o1, Place o2) {
                return o1.getDate().getTime().compareTo( o2.getDate().getTime());
            }
        };
    }
}
