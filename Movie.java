package com.hadar.assignment3;

public class Movie {
    private String name;
    private int score;
    private String actors;
    private String imageURL;

    public Movie(String name, int score, String actors, String imageURL) {
        this.name = name;
        this.score = score;
        this.actors = actors;
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
