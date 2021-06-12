package com.example.mooyaho.data_class;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Success {
    @SerializedName("scheme")
    @Expose
    private String scheme;
    @SerializedName("link")
    @Expose
    private String link;

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
