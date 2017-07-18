package com.pec.mob.statmonit.object;

import com.pec.mob.statmonit.util.JalaliCalendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Item {

    private String CaptureUtcDate;
    private long bintvalue;
    private String nvrvalue;
    private String valtype;
    private int hasError;
    private String ErrorDesc;

    public Item(String captureUtcDate, long bintvalue, String nvrvalue, String valtype, int hasError, String errorDesc) {
        CaptureUtcDate = captureUtcDate;
        this.bintvalue = bintvalue;
        this.nvrvalue = nvrvalue;
        this.valtype = valtype;
        this.hasError = hasError;
        ErrorDesc = errorDesc;
    }

    public String getCaptureUtcDate() {
        return CaptureUtcDate;
    }

    public void setCaptureUtcDate(String captureUtcDate) {
        CaptureUtcDate = captureUtcDate;
    }

    public long getBintvalue() {
        return bintvalue;
    }

    public void setBintvalue(long bintvalue) {
        this.bintvalue = bintvalue;
    }

    public String getNvrvalue() {
        return nvrvalue;
    }

    public void setNvrvalue(String nvrvalue) {
        this.nvrvalue = nvrvalue;
    }

    public String getValtype() {
        return valtype;
    }

    public void setValtype(String valtype) {
        this.valtype = valtype;
    }

    public int getHasError() {
        return hasError;
    }

    public void setHasError(int hasError) {
        this.hasError = hasError;
    }

    public String getErrorDesc() {
        return ErrorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        ErrorDesc = errorDesc;
    }

    public String getTimeString() {
        try {
            DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat printFormat = new SimpleDateFormat("HH:mm");
            //printFormat.setTimeZone(TimeZone.getTimeZone("GMT+4:30"));
            Date date = utcFormat.parse(CaptureUtcDate);
            return printFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getDateString() {
        try {
            int year = Integer.parseInt(CaptureUtcDate.substring(0, 4));
            int month = Integer.parseInt(CaptureUtcDate.substring(5, 7));
            int day = Integer.parseInt(CaptureUtcDate.substring(8, 10));
            return JalaliCalendar.gregorianToJalali(new JalaliCalendar.YearMonthDate(year, month, day)).toString();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return CaptureUtcDate;
        }
    }
}
