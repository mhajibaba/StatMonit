package com.pec.mob.statmonit.object;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class RiseFall implements Comparable<RiseFall>{

    @SerializedName("persiantitle") private String name;
    @SerializedName("terminalinumber") private int number;
    @SerializedName("nowcnt") private int nowCount;
    @SerializedName("onedaycnt") private int dayCount;
    @SerializedName("twodaycnt") private int twoDayCount;
    @SerializedName("threedaycnt") private int threeDayCount;
    @SerializedName("oneweekcnt") private int weekCount;
    @SerializedName("onemoncnt") private int monthCount;
    @SerializedName("avgcnt") private double average;
    @SerializedName("tolerance") private double tolerance;
    @SerializedName("advantage") private int advantage;

    public RiseFall(String name, int number, int nowCount, int dayCount, int twoDayCount, int threeDayCount, int weekCount, int monthCount, double average, double tolerance, int advantage) {
        this.name = name;
        this.number = number;
        this.nowCount = nowCount;
        this.dayCount = dayCount;
        this.twoDayCount = twoDayCount;
        this.threeDayCount = threeDayCount;
        this.weekCount = weekCount;
        this.monthCount = monthCount;
        this.average = average;
        this.tolerance = tolerance;
        this.advantage = advantage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNowCount() {
        return nowCount;
    }

    public void setNowCount(int nowCount) {
        this.nowCount = nowCount;
    }

    public int getDayCount() {
        return dayCount;
    }

    public void setDayCount(int dayCount) {
        this.dayCount = dayCount;
    }

    public int getTwoDayCount() {
        return twoDayCount;
    }

    public void setTwoDayCount(int twoDayCount) {
        this.twoDayCount = twoDayCount;
    }

    public int getThreeDayCount() {
        return threeDayCount;
    }

    public void setThreeDayCount(int threeDayCount) {
        this.threeDayCount = threeDayCount;
    }

    public int getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(int weekCount) {
        this.weekCount = weekCount;
    }

    public int getMonthCount() {
        return monthCount;
    }

    public void setMonthCount(int monthCount) {
        this.monthCount = monthCount;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public double getTolerance() {
        return tolerance;
    }

    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }

    public int getAdvantage() {
        return advantage;
    }

    public void setAdvantage(int advantage) {
        this.advantage = advantage;
    }

    @Override
    public int compareTo(@NonNull RiseFall o) {
        int compareQuantity = ((RiseFall) o).getAdvantage();
        return (compareQuantity - this.getAdvantage());

    }
}
