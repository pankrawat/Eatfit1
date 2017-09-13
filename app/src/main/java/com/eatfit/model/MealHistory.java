package com.eatfit.model;

import java.io.Serializable;

/**
 * Created by appsquadz on 5/9/17.
 */
public class MealHistory implements Serializable {
    private String mealTime;

    private String carbohydrate;

    private String mealImage;

    private String fat;

    private String modified;

    private String id;

    private String minCal;

    private String category;

    private String created;

    private String description;

    private String protein;

    private String name;

    private String minQuantity;

    public String getMealTime() {
        return mealTime;
    }

    public void setMealTime(String mealTime) {
        this.mealTime = mealTime;
    }

    public String getCarbohydrate() {
        return carbohydrate;
    }

    public void setCarbohydrate(String carbohydrate) {
        this.carbohydrate = carbohydrate;
    }

    public String getMealImage() {
        return mealImage;
    }

    public void setMealImage(String mealImage) {
        this.mealImage = mealImage;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMinCal() {
        return minCal;
    }

    public void setMinCal(String minCal) {
        this.minCal = minCal;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(String minQuantity) {
        this.minQuantity = minQuantity;
    }

    @Override
    public String toString() {
        return "ClassPojo [mealTime = " + mealTime + ", carbohydrate = " + carbohydrate + ", mealImage = " + mealImage + ", fat = " + fat + ", modified = " + modified + ", id = " + id + ", minCal = " + minCal + ", category = " + category + ", created = " + created + ", description = " + description + ", protein = " + protein + ", name = " + name + ", minQuantity = " + minQuantity + "]";
    }
}
