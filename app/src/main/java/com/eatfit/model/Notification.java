package com.eatfit.model;

/**
 * Created by appsquadz on 6/9/17.
 */
public class Notification {
    private String messageCode;

    private String message;

    private String id;

    private String time;

    private String to_id;

    private String from_id;

    private String created_at;

    public String getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTo_id() {
        return to_id;
    }

    public void setTo_id(String to_id) {
        this.to_id = to_id;
    }

    public String getFrom_id() {
        return from_id;
    }

    public void setFrom_id(String from_id) {
        this.from_id = from_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "ClassPojo [messageCode = " + messageCode + ", message = " + message + ", id = " + id + ", time = " + time + ", to_id = " + to_id + ", from_id = " + from_id + ", created_at = " + created_at + "]";
    }
}
