package com.pec.mob.statmonit.object;

public class BankInfo implements Comparable<BankInfo> {
    private String iin;
    private String PersianTitle;
    private int totcnt;
    private int sccnt;
    private double scperc;
    private int rejcnt;
    private double rejperc;
    private int USrejcnt;
    private int issrejcnt;
    private double issStableperc;
    private int avgtxntime;
    private int mintxntime;
    private int maxtxntime;

    public String getIin() {
        return iin;
    }

    public void setIin(String iin) {
        this.iin = iin;
    }

    public String getPersianTitle() {
        return PersianTitle.trim();
    }

    public void setPersianTitle(String persianTitle) {
        PersianTitle = persianTitle;
    }

    public int getTotcnt() {
        return totcnt;
    }

    public void setTotcnt(int totcnt) {
        this.totcnt = totcnt;
    }

    public int getSccnt() {
        return sccnt;
    }

    public void setSccnt(int sccnt) {
        this.sccnt = sccnt;
    }

    public double getScperc() {
        return scperc;
    }

    public void setScperc(double scperc) {
        this.scperc = scperc;
    }

    public int getRejcnt() {
        return rejcnt;
    }

    public void setRejcnt(int rejcnt) {
        this.rejcnt = rejcnt;
    }

    public double getRejperc() {
        return rejperc;
    }

    public void setRejperc(double rejperc) {
        this.rejperc = rejperc;
    }

    public int getUSrejcnt() {
        return USrejcnt;
    }

    public void setUSrejcnt(int USrejcnt) {
        this.USrejcnt = USrejcnt;
    }

    public int getIssrejcnt() {
        return issrejcnt;
    }

    public void setIssrejcnt(int issrejcnt) {
        this.issrejcnt = issrejcnt;
    }

    public double getIssStableperc() {
        return issStableperc;
    }

    public void setIssStableperc(double issStableperc) {
        this.issStableperc = issStableperc;
    }

    public int getAvgtxntime() {
        return avgtxntime;
    }

    public void setAvgtxntime(int avgtxntime) {
        this.avgtxntime = avgtxntime;
    }

    public int getMintxntime() {
        return mintxntime;
    }

    public void setMintxntime(int mintxntime) {
        this.mintxntime = mintxntime;
    }

    public int getMaxtxntime() {
        return maxtxntime;
    }

    public void setMaxtxntime(int maxtxntime) {
        this.maxtxntime = maxtxntime;
    }

    @Override
    public String toString() {
        return PersianTitle;
    }

    public int compareTo(BankInfo compareBank) {

        int compareQuantity = ((BankInfo) compareBank).getTotcnt();

        //ascending order
        return this.getTotcnt() - compareQuantity;

        //descending order
        //return compareQuantity - this.getTotcnt();
    }
}
