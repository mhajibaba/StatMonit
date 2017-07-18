package com.pec.mob.statmonit.object;

public class TransStat {
    private int cnt;
    private int avgtime;
    private String txntitle;

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public int getAvgtime() {
        return avgtime;
    }

    public void setAvgtime(int avgtime) {
        this.avgtime = avgtime;
    }

    public String getTxntitle() {
        return txntitle;
    }

    public void setTxntitle(String txntitle) {
        this.txntitle = txntitle;
    }

    @Override
    public String toString() {
        return txntitle;
    }
}
