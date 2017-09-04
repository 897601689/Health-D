package com.health_d.bean;

/**
 * Created by Admin on 2016/8/15.
 */
public class WBCTest {
    private String IDCard;
    private String time;
    private String value;
    private String doctor;

    public WBCTest(String IDCard, String time, String value, String doctor) {
        this.IDCard = IDCard;
        this.time = time;
        this.value = value;
        this.doctor = doctor;
    }

    public String getIDCard() {
        return IDCard;
    }

    public void setIDCard(String IDCard) {
        this.IDCard = IDCard;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    @Override
    public String toString() {
        return "WBCTest{" +
                "IDCard='" + IDCard + '\'' +
                ", time='" + time + '\'' +
                ", value='" + value + '\'' +
                ", doctor='" + doctor + '\'' +
                '}';
    }
}
