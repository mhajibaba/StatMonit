package com.pec.mob.statmonit.object;

import com.pec.mob.statmonit.util.JalaliCalendar;

public class Notification {
    private String ID;
    private String Status;
    private String Type;
    private String Severity;
    private String Title;
    private String Text;
    private String UTCCreatedOn;

    //@SerializedName("ID")
    public String getID() {
        return ID;
    }

    //@SerializedName("ID")
    public void setID(String ID) {
        this.ID = ID;
    }

    //@SerializedName("Status")
    public String getStatus() {
        return Status;
    }

    //@SerializedName("Status")
    public void setStatus(String Status) {
        this.Status = Status;
    }

    //@SerializedName("Type")
    public String getType() {
        return Type;
    }

    //@SerializedName("Type")
    public void setType(String Type) {
        this.Type = Type;
    }

    //@SerializedName("Severity")
    public String getSeverity() {
        return Severity;
    }

    //@SerializedName("Severity")
    public void setSeverity(String Severity) {
        this.Severity = Severity;
    }

    //@SerializedName("Title")
    public String getTitle() {
        return Title;
    }

    //@SerializedName("Title")
    public void setTitle(String Title) {
        this.Title = Title;
    }

    //@SerializedName("Text")
    public String getText() {
        return Text;
    }

    //@SerializedName("Text")
    public void setText(String Text) {
        this.Text = Text;
    }

    //@SerializedName("UTCCreatedOn")
    public String getUTCCreatedOn() {
        return UTCCreatedOn;
    }

    //@SerializedName("UTCCreatedOn")
    public void setUTCCreatedOn(String UTCCreatedOn) {
        this.UTCCreatedOn = UTCCreatedOn;
    }

    public String getDate() {
        try {
            int year = Integer.parseInt(UTCCreatedOn.substring(0, 4));
            int month = Integer.parseInt(UTCCreatedOn.substring(5, 7));
            int day = Integer.parseInt(UTCCreatedOn.substring(8, 10));
            return JalaliCalendar.gregorianToJalali(new JalaliCalendar.YearMonthDate(year, month, day)).toString();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return UTCCreatedOn;
        }
    }
}
