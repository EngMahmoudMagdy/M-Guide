package com.magdy.mguide;

import java.io.Serializable;


public class Information implements Serializable {

    public String PIC;
    public String OverView;
    public String Date;
    public String Title;
    public String Vote;
    public int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getOverView() {
        return OverView;
    }

    public void setOverView(String overView) {
        OverView = overView;
    }

    public String getPIC() {
        return PIC;
    }

    public void setPIC(String PIC) {
        this.PIC = PIC;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getVote() {
        return Vote;
    }

    public void setVote(String vote) {
        Vote = vote;
    }

}
