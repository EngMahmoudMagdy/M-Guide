package com.magdy.mguide.UI;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.magdy.mguide.BuildConfig;
import com.magdy.mguide.Data.Contract;
import com.magdy.mguide.Information;
import com.magdy.mguide.R;
import com.magdy.mguide.VideoAndReviewData;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import com.magdy.mguide.Data.Contract;

/**
 * A placeholder fragment containing a simple view.
 */

public class DetailActivityFragment extends Fragment {

    public VideoAndReviewData thisMovieData  = new VideoAndReviewData();
    public ArrayAdapter <String> mTrailerAdapter ;
    Context mContext ;
    ImageView img2 ;
    ImageView img ;
    public int id ;

    public DetailActivityFragment() {
    }

    CollapsingToolbarLayout collapsingToolbarLayout ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        String t1 = getArguments().getString(Contract.Movie.COLUMN_TITLE);

        collapsingToolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar) ;
        collapsingToolbarLayout.setTitle(t1);
        collapsingToolbarLayout.setContentDescription(t1);

        String t2 = getArguments().getString(Contract.Movie.COLUMN_DATE);
        ((TextView) rootView.findViewById(R.id.date))
                .setText(t2);
        String t3 = getArguments().getString(Contract.Movie.COLUMN_RATE);
        ((TextView) rootView.findViewById(R.id.rates))
                .setText(String.format(Locale.getDefault(),"%s / 10",t3));
        String t4 = getArguments().getString(Contract.Movie.COLUMN_OVERVIEW);
        TextView overview =  ((TextView) rootView.findViewById(R.id.overView));
        overview.setText(t4);
        overview.setContentDescription(t4);

        id = getArguments().getInt(Contract.Movie.COLUMN_MOVIE_ID,0);
        String pic = getArguments().getString(Contract.Movie.COLUMN_PIC_LINK);
        img = (ImageView) rootView.findViewById(R.id.imageView);
        img2 = (ImageView) rootView.findViewById(R.id.toolbar_photo);

        Picasso.with(getActivity()) //
                .load(pic) //
                .placeholder(R.drawable.placeholder) //
                .resize(138, 207)
                .into(img);

        Picasso.with(getActivity())
                .load(pic)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from){
                        img2.setImageBitmap(bitmap);
                    }
                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        img2.setImageResource(R.drawable.placeholder);
                    }
                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {}
                });


        final Information info = new Information();
        info.Title = t1 ;
        info.Date = t2 ;
        info.Vote= t3;
        info.OverView= t4;
        info.id= id;
        info.PIC= pic;

        mContext = getActivity();
        Button b = (Button) rootView.findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ContentValues quoteCV = new ContentValues();
                quoteCV.put(Contract.Movie.COLUMN_MOVIE_ID,info.id);
                quoteCV.put(Contract.Movie.COLUMN_TITLE,info.Title);
                quoteCV.put(Contract.Movie.COLUMN_DATE,info.Date);
                quoteCV.put(Contract.Movie.COLUMN_PIC_LINK,info.PIC);
                quoteCV.put(Contract.Movie.COLUMN_RATE,info.Vote);
                quoteCV.put(Contract.Movie.COLUMN_OVERVIEW,info.OverView);
                mContext.getContentResolver().insert(Contract.Movie.URI,quoteCV);

                Intent dataUpdatedIntent = new Intent(Contract.ACTION_DATA_UPDATED);
                mContext.sendBroadcast(dataUpdatedIntent);

            }
        });


        Log.w("Intent is here " , " "+ pic );
        return rootView;
    }

    public void updateDetails(int idHere) {


        FetchTrailerReview moviesTask = new FetchTrailerReview() ;

        moviesTask.execute(idHere) ;

    }
    @Override
    public void onStart()
    {
        super.onStart();
        updateDetails(id);
    }



    public class FetchTrailerReview extends AsyncTask< Integer ,Void ,VideoAndReviewData > {

        private final String LOG_TAG = FetchTrailerReview.class.getSimpleName() ;




        private VideoAndReviewData getMoviesDataFromJson(String MoviesJsonStrVideo , String MoviesJsonStrReview)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String VIDEO_RESULTS = "results";
            final String Video_key= "key";
            final String Video_name = "name";
            final String REVIEW_RESULTS = "results";
            final String Review_author = "author";
            final String Review_content = "content";
            final String YOUTUBE_LINK_BASE_URL = "https://www.youtube.com/watch?v=";

            JSONObject VideoJson = new JSONObject(MoviesJsonStrVideo);
            JSONObject ReviewJson = new JSONObject(MoviesJsonStrReview);
            JSONArray VideoArray = VideoJson.getJSONArray(VIDEO_RESULTS);
            JSONArray ReviewArray = ReviewJson.getJSONArray(REVIEW_RESULTS);
            VideoAndReviewData m = new VideoAndReviewData();
            for(int i = 0; i < VideoArray.length(); i++) {
                JSONObject Movie = VideoArray.getJSONObject(i);
                m.TrailerName.add(Movie.getString(Video_name) );
                m.TrailerLink.add( YOUTUBE_LINK_BASE_URL+Movie.getString(Video_key));
            }
            for(int i = 0; i < ReviewArray.length(); i++) {
                JSONObject Movie = ReviewArray.getJSONObject(i);
                m.ReviewAuthor.add( Movie.getString(Review_author) );
                m.ReviewContent.add( Movie.getString(Review_content) );

            }
            return m;
        }

        @Override

        protected VideoAndReviewData doInBackground(Integer ... params) {



            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnectionV = null;
            HttpURLConnection urlConnectionR = null;
            BufferedReader readerV = null;
            BufferedReader readerR = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStrVideo = null;
            String moviesJsonStrReview = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                //http://api.themoviedb.org/3/movie/popular?api_key=179a8cf9fc6fab0def62671610a2704b
                final String YOUTUBE_TRAILER_BASE_URL = "https://api.themoviedb.org/3/movie/"+params[0]+"/videos?";
                final String REVIEW_BASE_URL = "https://api.themoviedb.org/3/movie/"+ params[0]+"/reviews?";
                final String APPID_PARAM ="api_key";


                Uri builtUrivideos = Uri.parse(YOUTUBE_TRAILER_BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();

                Uri builtUrireview = Uri.parse(REVIEW_BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();

                URL urlvideos = new URL(builtUrivideos.toString());
                URL urlreview = new URL(builtUrireview.toString());

                Log.v(LOG_TAG, "Built URI videos " + builtUrivideos.toString());
                Log.v(LOG_TAG, "Built URI review " + builtUrireview.toString());
                // Create the request to OpenWeatherMap, and open the connection
                urlConnectionV = (HttpURLConnection) urlvideos.openConnection();
                urlConnectionR = (HttpURLConnection) urlreview.openConnection();
                urlConnectionV.setRequestMethod("GET");
                urlConnectionR.setRequestMethod("GET");
                urlConnectionV.connect();
                urlConnectionR.connect();

                // Read the input stream into a String
                InputStream inputStreamV = urlConnectionV.getInputStream();
                InputStream inputStreamR = urlConnectionR.getInputStream();
                StringBuffer bufferV = new StringBuffer();
                StringBuffer bufferR = new StringBuffer();
                if (inputStreamV == null) {
                    // Nothing to do.
                    return null;
                }
                if (inputStreamR == null) {
                    // Nothing to do.
                    return null;
                }
                readerV = new BufferedReader(new InputStreamReader(inputStreamV));
                readerR = new BufferedReader(new InputStreamReader(inputStreamR));

                String lineV;
                String lineR;
                while ((lineV = readerV.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    bufferV.append(lineV + "\n");
                }
                while ((lineR = readerR.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    bufferR.append(lineR + "\n");
                }

                if (bufferV.length() == 0  ) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                if (bufferR.length() == 0  ) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStrVideo = bufferV.toString();
                moviesJsonStrReview = bufferR.toString();
                Log.v(LOG_TAG, "Video JSON String " + moviesJsonStrVideo);
                Log.v(LOG_TAG, "Review JSON String " + moviesJsonStrReview);

            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnectionV != null) {
                    urlConnectionV.disconnect();
                }
                if (urlConnectionR != null) {
                    urlConnectionR.disconnect();
                }
                if (readerV != null) {
                    try {
                        readerV.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
                if (readerR != null) {
                    try {
                        readerR.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            try{
                // loadPicLinks(moviesJsonStr);
                return getMoviesDataFromJson(moviesJsonStrVideo , moviesJsonStrReview);


            }
            catch (JSONException e)
            {
                Log.e(LOG_TAG,e.getMessage(),e);
                e.printStackTrace();


            }


            return null;
        }
        @Override
        protected void onPostExecute(VideoAndReviewData result)
        {
            if(result != null)
            {
                thisMovieData.TrailerName.clear();
                thisMovieData.TrailerLink.clear();
                thisMovieData.ReviewAuthor.clear();
                thisMovieData.ReviewContent.clear();

                thisMovieData =  result ;

            }
            TextView TrailerTitle = (TextView) getActivity().findViewById(R.id.trailer_title);
            ListView trailerList = (ListView) getActivity().findViewById(R.id.listview_trailer);


            if(!thisMovieData.TrailerName.isEmpty() && !thisMovieData.TrailerLink.isEmpty()) {

                mTrailerAdapter = new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.trailer_item,
                        R.id.t,
                        thisMovieData.TrailerName

                );

                trailerList.setAdapter(mTrailerAdapter);

                TrailerTitle.setText("Trailer: ");
                TrailerTitle.setTextSize(20);

                int totalHeight = 0;
                for (int i = 0; i < mTrailerAdapter.getCount(); i++) {
                    View listItem = mTrailerAdapter.getView(i, null, trailerList);
                    listItem.measure(0, 0);
                    totalHeight += listItem.getMeasuredHeight();
                }
                ViewGroup.LayoutParams params = trailerList.getLayoutParams();
                params.height = totalHeight + (trailerList.getDividerHeight() * (mTrailerAdapter.getCount() - 1));
                trailerList.setLayoutParams(params);
                trailerList.requestLayout();

                trailerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(thisMovieData.TrailerLink.get(position)));
                        startActivity(intent);
                    }
                });
                mTrailerAdapter.notifyDataSetChanged();

            }

            TextView reviewTitle = (TextView) getActivity().findViewById(R.id.review_title);
            TextView reviewTextView = (TextView) getActivity().findViewById(R.id.textview_review);
            if (!thisMovieData.ReviewContent.isEmpty()) {

            reviewTitle.setText("Reviews: ");
            reviewTitle.setTextSize(20);
            String r1 ="" ;
            for (int i = 0 ; i < thisMovieData.ReviewContent.size();i++)
            {

                r1 =r1+"Review #"+(i+1)+" by "+thisMovieData.ReviewAuthor.get(i)+"\n"+"\n"+ thisMovieData.ReviewContent.get(i)+"\n" +"\n";
            }
            reviewTextView.setText(r1);
            reviewTextView.setTextSize(20);

            }


        }

    } ;


}
