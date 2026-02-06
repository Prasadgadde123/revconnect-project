package com.revconnect.model;

public class Profile {

    private int profileId;
    private int userId;
    private String name;
    private String bio;

    // No-arg constructor
    public Profile() {
    }

    // Parameterized constructor
    public Profile(int profileId, int userId, String name, String bio) {
        this.profileId = profileId;
        this.userId = userId;
        this.name = name;
        this.bio = bio;
    }

    // Getters and Setters
    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}