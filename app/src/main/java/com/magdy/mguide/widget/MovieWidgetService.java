package com.magdy.mguide.widget;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;

import android.os.Binder;

import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.magdy.mguide.Data.Contract;
import com.magdy.mguide.Information;
import com.magdy.mguide.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;



/**
 * Created by engma on 5/23/2017.
 */

public class MovieWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewFactory();
    }

    private class ListRemoteViewFactory implements RemoteViewsFactory {

         static final String MOVIE_PIC_BASE_URL = "http://image.tmdb.org/t/p/w185//";
        private Cursor data = null;
        @Override
        public void onCreate() {

        }
        @Override
        public void onDestroy() {
            if (data != null) {
                data.close();
                data = null;
            }

        }

        @Override
        public void onDataSetChanged() {

            if (data != null) data.close();

            final long identityToken = Binder.clearCallingIdentity();

            String[] s=  Contract.Movie.MOVIE_COLUMNS.toArray(new String[]{});
            data = getContentResolver().query(Contract.Movie.URI,
                    s,
                    null,
                    null,
                    Contract.Movie.COLUMN_DATE);
            Binder.restoreCallingIdentity(identityToken);
        }

        @Override
        public int getCount() {
            return  data == null ? 0 : data.getCount();
        }

        @SuppressLint("PrivateResource")
        @Override
        public RemoteViews getViewAt(int position) {
            if (position == AdapterView.INVALID_POSITION || data == null
                    || !data.moveToPosition(position)) {
                return null;
            }

            final RemoteViews remoteViews = new RemoteViews(getBaseContext().getPackageName(), R.layout.widget_grid_item);

            Information movie1 = new Information();
            movie1.setPIC( data.getString(Contract.Movie.POSITION_PIC_LINK));
            movie1.setTitle(data.getString(Contract.Movie.POSITION_TITLE));
            movie1.setOverView(data.getString(Contract.Movie.POSITION_OVERVIEW));
            movie1.setVote(data.getString(Contract.Movie.POSITION_RATE));
            movie1.setDate(data.getString(Contract.Movie.POSITION_DATE));
            movie1.setId(data.getInt(Contract.Movie.POSITION_MOVIE_ID));

            Uri imageUri = Uri.parse(MOVIE_PIC_BASE_URL+movie1.getPIC());
            Bitmap bitmap;
            try {
                bitmap =  Picasso.with(getApplicationContext())
                        .load(imageUri)
                        .get();
                remoteViews.setImageViewBitmap(R.id.widget_image,bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                remoteViews.setImageViewResource(R.id.widget_image,R.drawable.placeholder);
            }

            final Intent i = new Intent();
            i.putExtra(Contract.Movie.TABLE_NAME,movie1);
            remoteViews.setOnClickFillInIntent(R.id.grid_item, i);
            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return new RemoteViews(getPackageName(), R.layout.widget_grid_item);
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return data.moveToPosition(i) ? data.getLong(Contract.Movie.POSITION_ID) : i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
