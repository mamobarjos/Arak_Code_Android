package com.arakadds.arak.model;

public class ImageUploadModel {

    private String imageUrl;

    public ImageUploadModel() {
    }

    public ImageUploadModel(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
