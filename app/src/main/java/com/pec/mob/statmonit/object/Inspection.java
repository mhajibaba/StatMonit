package com.pec.mob.statmonit.object;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Inspection implements Comparable<Inspection>{

    private int Agency_Code;
    private String Title;
    @SerializedName("anjam nashode") private int nodone;
    @SerializedName("anjam shode") private int done;
    @SerializedName("kol") private int total;

    public int getAgency_Code() {
        return Agency_Code;
    }

    public void setAgency_Code(int agency_Code) {
        Agency_Code = agency_Code;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public int getNodone() {
        return nodone;
    }

    public void setNodone(int nodone) {
        this.nodone = nodone;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int compareTo(@NonNull Inspection compareInspection) {

        int compareQuantity = compareInspection.getTotal();
        //ascending order
        return this.getTotal() - compareQuantity;
        //descending order
        //return compareQuantity - this.getTotcnt();
    }
}
