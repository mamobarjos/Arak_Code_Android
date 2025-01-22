package com.arakadds.arak.model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CreateNewAdDataHolder implements Parcelable {

    private ArrayList<Bitmap> bitmapArrayList;
    private List<Uri> uriList;
    private boolean isCompany;
    private String title;
    private String companyName;
    private String description;
    private String phoneNumber;
    private String location;
    private String lon;
    private String lat;
    private String websiteUrl;
    private String duration;
    private Uri videoThumbnail;

    public CreateNewAdDataHolder() {
    }


    protected CreateNewAdDataHolder(Parcel in) {
        bitmapArrayList = in.createTypedArrayList(Bitmap.CREATOR);
        uriList = in.createTypedArrayList(Uri.CREATOR);
        isCompany = in.readByte() != 0;
        title = in.readString();
        companyName = in.readString();
        description = in.readString();
        phoneNumber = in.readString();
        location = in.readString();
        lon = in.readString();
        lat = in.readString();
        websiteUrl = in.readString();
        duration = in.readString();
        videoThumbnail = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<CreateNewAdDataHolder> CREATOR = new Creator<CreateNewAdDataHolder>() {
        @Override
        public CreateNewAdDataHolder createFromParcel(Parcel in) {
            return new CreateNewAdDataHolder(in);
        }

        @Override
        public CreateNewAdDataHolder[] newArray(int size) {
            return new CreateNewAdDataHolder[size];
        }
    };

    public ArrayList<Bitmap> getBitmapArrayList() {
        return bitmapArrayList;
    }

    public void setBitmapArrayList(ArrayList<Bitmap> bitmapArrayList) {
        this.bitmapArrayList = bitmapArrayList;
    }

    public List<Uri> getUriList() {
        return uriList;
    }

    public void setUriList(List<Uri> uriList) {
        this.uriList = uriList;
    }

    public boolean isCompany() {
        return isCompany;
    }

    public void setCompany(boolean company) {
        isCompany = company;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeTypedList(bitmapArrayList);
        dest.writeTypedList(uriList);
        dest.writeByte((byte) (isCompany ? 1 : 0));
        dest.writeString(title);
        dest.writeString(companyName);
        dest.writeString(description);
        dest.writeString(phoneNumber);
        dest.writeString(location);
        dest.writeString(lon);
        dest.writeString(lat);
        dest.writeString(websiteUrl);
        dest.writeString(duration);
        dest.writeParcelable(videoThumbnail, flags);
    }

    public Uri getVideoThumbnail() {
        return videoThumbnail;
    }

    public void setVideoThumbnail(Uri videoThumbnail) {
        this.videoThumbnail = videoThumbnail;
    }
}
