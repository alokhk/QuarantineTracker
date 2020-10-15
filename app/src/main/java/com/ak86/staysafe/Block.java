package com.ak86.staysafe;

import java.util.Date;

public class Block {

    private String blockName;
    private String blockDescr;
    private int blockCapacity;
    private String blockInCharge;
    private int blockOccupied;

    public void setQuarantineStartDate(Date quarantineStartDate) {
        this.quarantineStartDate = quarantineStartDate;
    }

    public Date getQuarantineStartDate(){
        return this.quarantineStartDate;
    }

    private Date quarantineStartDate;
    private Date quarantineEndDate;
    private Date medicalDate;


    public Block(){}

    public Block(String blockName, String blockDescr, String blockInCharge, int blockCapacity, int blockOccupied, Date quarantineStartDate, Date quarantineEndDate, Date medicalDate){
        this.blockName = blockName;
        this.blockDescr = blockDescr;
        this.blockCapacity = blockCapacity;
        this.blockInCharge = blockInCharge;
        this.blockOccupied = blockOccupied;
        this.quarantineStartDate = quarantineStartDate;
        this.quarantineEndDate = quarantineEndDate;
        this.medicalDate = medicalDate;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public String getBlockDescr() {
        return blockDescr;
    }

    public void setBlockDescr(String blockDescr) {
        this.blockDescr = blockDescr;
    }

    public int getBlockCapacity() {
        return blockCapacity;
    }

    public void setBlockCapacity(int blockCapacity) {
        this.blockCapacity = blockCapacity;
    }

    public String getBlockInCharge() {
        return blockInCharge;
    }

    public void setBlockInCharge(String blockInCharge) {
        this.blockInCharge = blockInCharge;
    }


    public int getBlockOccupied() {
        return blockOccupied;
    }

    public void setBlockOccupied(int blockOccupied) {
        this.blockOccupied = blockOccupied;
    }

    public Date getQuarantineEndDate() {
        return quarantineEndDate;
    }

    public void setQuarantineEndDate(Date quarantineEndDate) {
        this.quarantineEndDate = quarantineEndDate;
    }

    public Date getMedicalDate() {
        return medicalDate;
    }

    public void setMedicalDate(Date medicalDate) {
        this.medicalDate = medicalDate;
    }


}
