package com.health_d.bean;

/**
 * Created by Admin on 2016/8/15.
 */
public class HGBTest {
    private String IDCard;
    private String time;
    public HGBTest(String IDCard, String time, String value, String doctor) {
        this.IDCard = IDCard;
        this.time = time;
        this.value = value;
        this.doctor = doctor;
    }

    private String value;

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
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

    private String doctor;



    @Override
    public String toString() {
        return "HGBTest{" +
                "IDCard='" + IDCard + '\'' +
                ", time='" + time + '\'' +
                ", value='" + value + '\'' +
                ", doctor='" + doctor + '\'' +
                '}';
    }

}
