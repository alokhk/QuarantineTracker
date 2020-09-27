package com.ak86.quarantinetracker;

import java.util.Date;

public class Person {

    private String name;
    private Date startDate, endDate, medicalDate;
    private boolean coronaPositive;

    public Person(){}
    public Person(String name, Date startDate){
        this.name = name;
        this.startDate = startDate;
    }
    public Person(String name, Date startDate, Date endDate, Date medicalDate){
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.medicalDate = medicalDate;

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getMedicalDate() {
        return medicalDate;
    }

    public void setMedicalDate(Date medicalDate) {
        this.medicalDate = medicalDate;
    }

    public boolean isCoronaPositive() {
        return coronaPositive;
    }

    public void setCoronaPositive(boolean coronaPositive) {
        this.coronaPositive = coronaPositive;
    }
}
