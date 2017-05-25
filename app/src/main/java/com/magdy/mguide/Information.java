package com.magdy.mguide;

import java.io.Serializable;


public class Information implements Serializable {

    public String PIC ;
    public String OverView ;
    public String Date ;
    public String Title ;
    public String Vote ;
    public int id;

    public void setDate(String date) {
        Date = date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOverView(String overView) {
        OverView = overView;
    }

    public void setPIC(String PIC) {
        this.PIC = PIC;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setVote(String vote) {
        Vote = vote;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return Date;
    }

    public String getOverView() {
        return OverView;
    }

    public String getPIC() {
        return PIC;
    }

    public String getTitle() {
        return Title;
    }

    public String getVote() {
        return Vote;
    }

}
