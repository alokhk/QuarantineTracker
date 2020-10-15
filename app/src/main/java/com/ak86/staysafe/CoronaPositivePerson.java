package com.ak86.staysafe;

import java.util.Date;

public class CoronaPositivePerson {

    private String name;
    private Date dayOfResult = null;
    private Boolean isNowPositive = false;
    private Boolean isNowNegative = true;
    private String currentLocation = null;
    private Boolean isActive = false;
    private Date dateOfNextTest = null;
    private Date dateOfNegativeResult = null;

    public CoronaPositivePerson() {}

    public CoronaPositivePerson(String name){
        this.name = name;
        this.dayOfResult = new Date();
        this.dateOfNextTest = new Date();
        this.currentLocation = "Please Update";
        this.isActive = true;
        this.isNowPositive = true;
        this.isNowNegative = false;
        this.dateOfNextTest = new Date();
        this.dateOfNegativeResult = new Date();
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Date getDayOfResult() {
        return dayOfResult;
    }

    public void setDayOfResult(Date dayOfResult) {
        this.dayOfResult = dayOfResult;
    }

    public Boolean getNowPositive() {
        return isNowPositive;
    }

    public void setNowPositive(Boolean nowPositive) {
        isNowPositive = nowPositive;
    }

    public Boolean getNowNegative() {
        return isNowNegative;
    }

    public void setNowNegative(Boolean nowNegative) {
        isNowNegative = nowNegative;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Date getDateOfNextTest() {
        return dateOfNextTest;
    }

    public void setDateOfNextTest(Date dateOfNextTest) {
        this.dateOfNextTest = dateOfNextTest;
    }

    public Date getDateOfNegativeResult() {
        return dateOfNegativeResult;
    }

    public void setDateOfNegativeResult(Date dateOfNegativeResult){
        this.dateOfNegativeResult = dateOfNegativeResult;
    }
}
