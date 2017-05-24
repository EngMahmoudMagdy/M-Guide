package com.magdy.mguide.Data;

import android.net.Uri;
import android.provider.BaseColumns;
import com.google.common.collect.ImmutableList;
/**
 * Created by engma on 5/23/2017.
 */

public class Contract {

    static final String AUTHORITY = "com.magdy.mguide";
    static final String PATH_MOVIE = "movie";
    public static final String ACTION_DATA_UPDATED = "com.magdy.mguide.ACTION_DATA_UPDATED";
    private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    private Contract() {
    }

    @SuppressWarnings("unused")
    public static final class Movie implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_MOVIE).build();
        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_PIC_LINK = "pic_link";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RATE = "rate";
        public static final String COLUMN_OVERVIEW = "over_view";
        public static final String COLUMN_DATE = "date";
        public static final int POSITION_ID = 0;
        public static final int POSITION_MOVIE_ID = 1;
        public static final int POSITION_PIC_LINK = 2;
        public static final int POSITION_TITLE = 3;
        public static final int POSITION_RATE = 4;
        public static final int POSITION_OVERVIEW = 5;
        public static final int POSITION_DATE = 6;
        public static final ImmutableList<String> MOVIE_COLUMNS = ImmutableList.of(
                _ID,
                COLUMN_MOVIE_ID,
                COLUMN_PIC_LINK,
                COLUMN_TITLE,
                COLUMN_RATE,
                COLUMN_OVERVIEW,
                COLUMN_DATE
        );

        public static Uri makeUriForMovie(String movieid) {
            return URI.buildUpon().appendPath(movieid).build();
        }

        static String getMovieFromUri(Uri queryUri) {
            return queryUri.getLastPathSegment();
        }


    }
}
