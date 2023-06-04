package com.example.myapplication;

public class FanItem {
    private String fanName;
    private Integer fanHeat;
    private Integer fanFirst;

    public FanItem(String fanName, Integer fanHeat, Integer fanFirst) {
        this.fanName = fanName;
        this.fanHeat = fanHeat;
        this.fanFirst = fanFirst;
    }


    public String getFanName() {
        return fanName;
    }

    public Integer getFanHeat() {
        return fanHeat;
    }

    public Integer getFanFirst() {return fanFirst;}

    public void setFanName(String fanName) {
        this.fanName = fanName;
    }

    public void setFanHeat(Integer fanHeat) {
        this.fanHeat = fanHeat;
    }

    public void setFanFirst(Integer fanFirst) {this.fanFirst = fanFirst;}
}
