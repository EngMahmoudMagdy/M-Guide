package com.magdy.mguide.widget;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.magdy.mguide.Data.Contract;
import com.magdy.mguide.Information;
import com.magdy.mguide.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.InputStream;

/**
 * Created by engma on 5/23/2017.
 */

public class MovieWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewFactory();
    }

    private class ListRemoteViewFactory implements RemoteViewsFactory {

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
            movie1.PIC = data.getString(Contract.Movie.POSITION_PIC_LINK);
            movie1.Title= data.getString(Contract.Movie.POSITION_TITLE);
            movie1.OverView= data.getString(Contract.Movie.POSITION_OVERVIEW);
            movie1.Vote= data.getString(Contract.Movie.POSITION_RATE);
            movie1.Date= data.getString(Contract.Movie.POSITION_DATE);
            movie1.id= data.getInt(Contract.Movie.POSITION_MOVIE_ID);
            remoteViews.setTextViewText(R.id.widget_image,movie1.Title);


            /*DownloadImageTask downloadImageTask = new DownloadImageTask(remoteViews);
            downloadImageTask.execute(movie1.PIC);*/

            /*Picasso.with(getBaseContext())
                    .load(movie1.PIC)
                    .placeholder(R.drawable.placeholder)
                    .fit()
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            remoteViews.setImageViewBitmap(R.id.widget_image,bitmap);
                        }
                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                            remoteViews.setImageViewResource(R.id.widget_image,R.drawable.placeholder);
                        }
                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });*/


            final Intent i = new Intent();
            i.putExtra(Contract.Movie.COLUMN_MOVIE_ID, movie1.id);
            i.putExtra(Contract.Movie.COLUMN_TITLE, movie1.Title);
            i.putExtra(Contract.Movie.COLUMN_OVERVIEW,movie1.OverView);
            i.putExtra(Contract.Movie.COLUMN_DATE,movie1.Date);
            i.putExtra(Contract.Movie.COLUMN_PIC_LINK,movie1.PIC);
            i.putExtra(Contract.Movie.COLUMN_RATE, movie1.Vote);
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
    private class DownloadImageTask extends AsyncTask<String,Void,Bitmap>
    {
        RemoteViews remoteViews ;
        protected DownloadImageTask(RemoteViews rviewa )
        {
            remoteViews = rviewa ;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                InputStream in = new java.net.URL(strings[0]).openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                Log.v("ImageDownload", "download succeeded");
                Log.v("ImageDownload", "Param 0 is: " + strings[0]);
                return bitmap;
                //NOTE:  it is not thread-safe to set the ImageView from inside this method.  It must be done in onPostExecute()
            } catch (Exception e) {
                Log.e("ImageDownload", "Download failed: " + e.getMessage());
            }
            return  null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap==null)
                remoteViews.setImageViewResource(R.id.widget_image,R.drawable.placeholder);
            else
            {
                remoteViews.setImageViewBitmap(R.id.widget_image,bitmap);
            }
        }
    }

}
