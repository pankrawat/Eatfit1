package com.eatfit.model;

import java.io.Serializable;

/**
 * Created by appsquadz on 13/6/17.
 */

public class VideosModel implements Serializable {

    private String id;
    private String name;
    private String category;
    private String type;
    private String description;
    private String link;
    private String created;
    private String modified;
    private Integer likeVideo;
    private Integer IsAlreadyAddedInWorkout;

    public Integer getIsAlreadyAddedInWorkout() {
        return IsAlreadyAddedInWorkout;
    }

    public void setIsAlreadyAddedInWorkout(Integer isAlreadyAddedInWorkout) {
        IsAlreadyAddedInWorkout = isAlreadyAddedInWorkout;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public Integer getLikeVideo() {
        return likeVideo;
    }

    public void setLikeVideo(Integer likeVideo) {
        this.likeVideo = likeVideo;
    }
}
