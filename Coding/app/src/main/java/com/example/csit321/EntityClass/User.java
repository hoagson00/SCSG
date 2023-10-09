package com.example.csit321.EntityClass;

import java.util.List;

import androidx.room.Entity;

@Entity
public class User {

    private String userId;

    private String fullName;

    private String phoneNum;

    private String username;

    private String password;

    private List<String> likedPosts;

    private List<String> savedEvents;

    private int accountStatus; //1 is user, 2 is admin

    private String accountType; //0 is Care Taker, 1 is Cancer Survivor, 2 is Other

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(int accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public List<String> getLikedPosts() {
        return likedPosts;
    }

    public void setLikedPosts(List<String> likedPosts) {
        this.likedPosts = likedPosts;
    }

    public List<String> getSavedEvents() {
        return savedEvents;
    }

    public void setSavedEvents(List<String> savedEvents) {
        this.savedEvents = savedEvents;
    }

    public User() {
    }
}
