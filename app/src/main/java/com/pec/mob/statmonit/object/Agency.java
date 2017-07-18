package com.pec.mob.statmonit.object;

import android.support.annotation.NonNull;

public class Agency implements Comparable<Agency>{

    private int TXNcnt;
    private int identifier;
    private String title;

    public int getTXNcnt() {
        return TXNcnt;
    }

    public void setTXNcnt(int TXNcnt) {
        this.TXNcnt = TXNcnt;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    @Override
    public int compareTo(@NonNull Agency compareAgency) {

        int compareQuantity = ((Agency) compareAgency).getTXNcnt();
        //ascending order
        return this.getTXNcnt() - compareQuantity;
        //descending order
        //return compareQuantity - this.getTotcnt();
    }
}
