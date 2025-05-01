package com.nhom13.phonemart.dto;

import java.io.Serializable;
import java.util.Set;

public class BranchDto implements Serializable {
    private Long id;
    private String name;
    private String phoneNumber;
    private String email;
    private String openingTime;
    private String introduce;
    private boolean status;
    private double latitude;
    private double longitude;
    private Set<ImageDto> images;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Set<ImageDto> getImages() {
        return images;
    }

    public void setImages(Set<ImageDto> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "BranchDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", openingTime='" + openingTime + '\'' +
                ", introduce='" + introduce + '\'' +
                ", status=" + status +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", images=" + images +
                '}';
    }
}
