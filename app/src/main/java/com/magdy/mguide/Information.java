package com.magdy.mguide;

import android.os.Parcel;
import android.os.Parcelable;


public class Information implements Parcelable {




    public String PIC ; //="http://i.imgur.com/Z3QjilA.jpg" ;
    public String OverView ;
    public String Date ;
    public String Title ;
    public String Vote ;
    public int id;

    protected Information(Parcel in) {
        PIC = in.readString();
        OverView = in.readString();
        Date = in.readString();
        Title = in.readString();
        Vote = in.readString();
        id = in.readInt();
    }

    public static final Creator<Information> CREATOR = new Creator<Information>() {
        @Override
        public Information createFromParcel(Parcel in) {
            return new Information(in);
        }

        @Override
        public Information[] newArray(int size) {
            return new Information[size];
        }
    };

    public Information() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(PIC);
        dest.writeString(OverView);
        dest.writeString(Date);
        dest.writeString(Title);
        dest.writeString(Vote);
        dest.writeInt(id);
    }
}
