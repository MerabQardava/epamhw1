package com.epam.hw.entity;



public class Trainer extends User{

    public Integer specializationId;
    public Integer userId;

    public Trainer(String firstName, String lastName, Integer specializationId, Integer userId) {
        super(firstName, lastName);
        this.specializationId = specializationId;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Trainer{" +
                "specializationId=" + specializationId +
                ", userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", isActive=" + isActive +
                '}';
    }

    public Integer getSpecializationId() {
        return specializationId;
    }

    public void setSpecializationId(Integer specializationId) {
        this.specializationId = specializationId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
