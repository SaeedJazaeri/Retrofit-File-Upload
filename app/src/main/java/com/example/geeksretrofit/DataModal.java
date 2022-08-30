package com.example.geeksretrofit;

import com.google.gson.annotations.SerializedName;

import java.io.File;

public class DataModal {

    // string variables for our name and job
    @SerializedName("description")
    private String description;
    @SerializedName("date_published")
    private String date_published;
    @SerializedName("file")
    private File file;

    public DataModal(String description, String date_published, File file) {
        this.description = description;
        this.date_published = date_published;
        this.file = file;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate_published() {
        return date_published;
    }

    public void setDate_published(String date_published) {
        this.date_published = date_published;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
