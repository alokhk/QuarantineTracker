package com.ak86.quarantinetracker;

public class Block {

    private String blockName;
    private String blockDescr;
    private int blockCapacity;
    private String blockInCharge;

    public Block(){}

    public Block(String blockName, String blockDescr, int blockCapacity, String blockInCharge){
        this.blockName = blockName;
        this.blockDescr = blockDescr;
        this.blockCapacity = blockCapacity;
        this.blockInCharge = blockInCharge;
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

}
