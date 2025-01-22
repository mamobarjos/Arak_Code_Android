package com.arakadds.arak.model.message;

public class User {

    private String email;
    private String fullname;
    private String Id;
    private String img_avatar;

    public User() {
    }

    public User(String email, String fullname, String Id, String img_avatar) {
        this.email = email;
        this.fullname = fullname;
        this.Id = Id;
        this.img_avatar = img_avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getImg_avatar() {
        return img_avatar;
    }

    public void setImg_avatar(String img_avatar) {
        this.img_avatar = img_avatar;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", fullname='" + fullname + '\'' +
                ", id='" + Id + '\'' +
                ", img_avatar='" + img_avatar + '\'' +
                '}';
    }
}
