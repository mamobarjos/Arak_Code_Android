package com.arakadds.arak.model.message;


import java.util.Date;

public class Message implements Comparable<Message> {
    String Id;
    String sender_Id;
    String imageUrl;
    String to_Id;
    String text;
    boolean seen;
    long timestamp;
    String productId;

    User user;

    public String chatPartnerId(String currentId) {

        if (currentId != null) { //userId
            if (sender_Id.equalsIgnoreCase(currentId)) {
                return to_Id;
            }
            return sender_Id;
        }
        return "";
    }

    public Message() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Message(String id, String sender_Id, String imageUrl, String to_Id, boolean seen, String text, long timestamp, String productId) {
        Id = id;
        this.sender_Id = sender_Id;
        this.imageUrl = imageUrl;
        this.to_Id = to_Id;
        this.seen = seen;
        this.text = text;
        this.timestamp = timestamp;
        this.productId = productId;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getSender_Id() {
        return sender_Id;
    }

    public void setSender_Id(String sender_Id) {
        this.sender_Id = sender_Id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTo_Id() {
        return to_Id;
    }

    public void setTo_Id(String to_Id) {
        this.to_Id = to_Id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    @Override
    public int compareTo(Message o) {
        Date date1 = new Date(getTimestamp() * 100);
        Date date2 = new Date(o.getTimestamp() * 100);
        if (date1 == null || date2 == null)
            return 1;
        return date2.compareTo(date1);
    }
}
