package io.tsh.countries.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by klaud on 2015-12-28.
 */
public class PlaceDetail extends Place {
    @SerializedName("description")
    private String description;

    @SerializedName("see_more_url")
    private String seeMoreUrl;

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
}
