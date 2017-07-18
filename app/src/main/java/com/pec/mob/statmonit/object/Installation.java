package com.pec.mob.statmonit.object;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Installation implements Comparable<Installation>{

    @SerializedName("Agency_Code") private int id;
    @SerializedName("Agency_Name") private String agencyName;
    @SerializedName("inscnt") private int installCount;
    @SerializedName("uninscnt") private int uninstallCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public int getInstallCount() {
        return installCount;
    }

    public void setInstallCount(int installCount) {
        this.installCount = installCount;
    }

    public int getUninstallCount() {
        return uninstallCount;
    }

    public void setUninstallCount(int uninstallCount) {
        this.uninstallCount = uninstallCount;
    }

    public int compareTo(@NonNull Installation compare) {

        int compareQuantity = compare.getInstallCount();
        //ascending order
        return this.getInstallCount() - compareQuantity;
        //descending order
        //return compareQuantity - this.getTotcnt();
    }
}
