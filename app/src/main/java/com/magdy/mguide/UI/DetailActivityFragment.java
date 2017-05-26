package com.magdy.mguide.UI;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */

public class DetailActivityFragment extends Fragment {

    public static final String MOVIE_PIC_BASE_URL = "http://image.tmdb.org/t/p/w185//";
    public static final String TRAILER_PIC_BASE_URL = "http://i.ytimg.com/vi/";
    public static final String TRAILER_PIC_FOOTER_NAME = "/mqdefault.jpg";
    public VideoAndReviewData thisMovieData;
    public HorizontalRecyclerTrailerAdapter mTrailerAdapter;
    public RecyclerView mTrailerRecycler;
    public HorizontalReviewAdapter mReviewAdapter;
    public RecyclerView mReviewRecycler;
    public HorizontalReviewAdapter mFireReviewAdapter;
    public RecyclerView mFireReviewRecycler;
    public LinearLayout apiReviewSection;
    Context mContext;
    AppCompatActivity detailActivity;
    ImageView img2;
    ImageView img;
    boolean isFav = false;
    Information info;
    FloatingActionButton b;
    List<String> userReviews, userNames;
    CollapsingToolbarLayout collapsingToolbarLayout;
    boolean isTwoPane = false;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        if (savedInstanceState == null) {
            info = (Information) getArguments().getSerializable(Contract.Movie.TABLE_NAME);
            isTwoPane = getArguments().getBoolean("pane");
        } else {
            info = (Information) savedInstanceState.getSerializable(Contract.Movie.TABLE_NAME);
            isFav = savedInstanceState.getBoolean("fav");
            isTwoPane = savedInstanceState.getBoolean("pane");
        }

        mContext = getActivity();

        collapsingToolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(info.getTitle());
        collapsingToolbarLayout.setContentDescription(info.getTitle());
        TextView title = (TextView) rootView.findViewById(R.id.title);
        title.setText(info.getTitle());
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        if (isTwoPane) {
            detailActivity = (MainActivity) getActivity();
        } else {
            detailActivity = (DetailActivity) getActivity();
            detailActivity.setSupportActionBar(toolbar);
            if (detailActivity.getSupportActionBar() != null) {
                detailActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                detailActivity.getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
        }

        ((TextView) rootView.findViewById(R.id.date))
                .setText(info.getDate());
        ((TextView) rootView.findViewById(R.id.rates))
                .setText(String.format(Locale.getDefault(), "%s / 10", info.getVote()));
        TextView overview = ((TextView) rootView.findViewById(R.id.overView));
        overview.setText(info.getOverView());
        overview.setContentDescription(info.getOverView());


        thisMovieData = new VideoAndReviewData();
        userReviews = new ArrayList<>();
        userNames = new ArrayList<>();

        //For Trailers section with horizontal images of Trailers

        mTrailerAdapter =
                new HorizontalRecyclerTrailerAdapter(getContext(),
                        thisMovieData.TrailerImageLink,
                        thisMovieData.TrailerLink);
        mTrailerRecycler = (RecyclerView) rootView.findViewById(R.id.trailer_recycler);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mTrailerRecycler.setLayoutManager(horizontalLayoutManagaer);
        mTrailerRecycler.setAdapter(mTrailerAdapter);


        //For reveiw section with horizontal cardviews of reviews

        mReviewAdapter =
                new HorizontalReviewAdapter(getContext(),
                        thisMovieData.ReviewAuthor,
                        thisMovieData.ReviewContent);
        mReviewRecycler = (RecyclerView) rootView.findViewById(R.id.review_recycler);
        LinearLayoutManager horizontalLayoutManagaer2
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mReviewRecycler.setLayoutManager(horizontalLayoutManagaer2);
        mReviewRecycler.setAdapter(mReviewAdapter);

        mFireReviewAdapter =
                new HorizontalReviewAdapter(getContext(),
                        userNames,
                        userReviews);
        mFireReviewRecycler = (RecyclerView) rootView.findViewById(R.id.user_review_recycler);
        LinearLayoutManager horizontalLayoutManagaer3
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mFireReviewRecycler.setLayoutManager(horizontalLayoutManagaer3);
        mFireReviewRecycler.setAdapter(mFireReviewAdapter);

        //linear layout review section
        apiReviewSection = (LinearLayout) rootView.findViewById(R.id.api_review_section);

        // prepare images

        img = (ImageView) rootView.findViewById(R.id.imageView);
        img.setContentDescription(info.getTitle());
        img2 = (ImageView) rootView.findViewById(R.id.toolbar_photo);
        img2.setContentDescription(info.getTitle());

        // Load images in toolbar and image poster
        Picasso.with(getActivity()) //
                .load(MOVIE_PIC_BASE_URL + info.getPIC()) //
                .placeholder(R.drawable.placeholder) //
                .resize(138, 207)
                .into(img);

        Picasso.with(getActivity())
                .load(MOVIE_PIC_BASE_URL + info.getPIC())
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                        img2.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        img2.setImageResource(R.drawable.placeholder);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                    }
                });


        b = (FloatingActionButton) rootView.findViewById(R.id.button);
        Cursor c = mContext.getContentResolver().query(Contract.Movie.URI, null, Contract.Movie.COLUMN_MOVIE_ID + " = " + info.getId(), null, null);
        if (c != null) {
            if (c.getCount() == 0) {
                isFav = false;
                b.getDrawable().setColorFilter(ContextCompat.getColor(mContext, R.color.white), PorterDuff.Mode.SRC_IN);
            } else {
                isFav = true;
                b.getDrawable().setColorFilter(ContextCompat.getColor(mContext, R.color.material_red_700), PorterDuff.Mode.SRC_IN);
            }
            c.close();
        }

        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isFav) {
                    mContext.getContentResolver().delete(Contract.Movie.URI, Contract.Movie.COLUMN_MOVIE_ID + "=" + info.getId(), null);
                    b.getDrawable().setColorFilter(ContextCompat.getColor(mContext, R.color.white), PorterDuff.Mode.SRC_IN);
                } else {
                    ContentValues quoteCV = new ContentValues();
                    quoteCV.put(Contract.Movie.COLUMN_MOVIE_ID, info.id);
                    quoteCV.put(Contract.Movie.COLUMN_TITLE, info.Title);
                    quoteCV.put(Contract.Movie.COLUMN_DATE, info.Date);
                    quoteCV.put(Contract.Movie.COLUMN_PIC_LINK, info.PIC);
                    quoteCV.put(Contract.Movie.COLUMN_RATE, info.Vote);
                    quoteCV.put(Contract.Movie.COLUMN_OVERVIEW, info.OverView);
                    mContext.getContentResolver().insert(Contract.Movie.URI, quoteCV);
                    b.getDrawable().setColorFilter(ContextCompat.getColor(mContext, R.color.material_red_700), PorterDuff.Mode.SRC_IN);
                }
                isFav = !isFav;
                Intent dataUpdatedIntent = new Intent(Contract.ACTION_DATA_UPDATED);
                mContext.sendBroadcast(dataUpdatedIntent);
            }
        });


        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    userNames.clear();
                    userReviews.clear();
                    Iterable<DataSnapshot> ds = dataSnapshot.child("movies").child(String.valueOf(info.getId())).child("user_reviews").getChildren();
                    for (DataSnapshot das : ds) {
                        String name = dataSnapshot.child("users").child(das.getKey()).child("info").child("name").getValue(String.class);
                        String review = das.child("review").getValue(String.class);
                        userNames.add(name == null ? "" : name);
                        userReviews.add(review == null ? "" : review);
                    }
                    mFireReviewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Button reviewButton = (Button) rootView.findViewById(R.id.add_review);
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ReviewEntryActivity.class);
                intent.putExtra(Contract.Movie.COLUMN_MOVIE_ID, info.getId());
                getContext().startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Contract.Movie.TABLE_NAME, info);
        outState.putBoolean("pane", isTwoPane);
        outState.putBoolean("fav", isFav);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (item.getItemId() == android.R.id.home && !isTwoPane) {
            detailActivity.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateDetails(int idHere) {

        FetchTrailerReview moviesTask = new FetchTrailerReview();
        moviesTask.execute(idHere);

    }

    @Override
    public void onStart() {
        super.onStart();
        updateDetails(info.getId());
    }

    private class FetchTrailerReview extends AsyncTask<Integer, Void, VideoAndReviewData> {

        private final String LOG_TAG = FetchTrailerReview.class.getSimpleName();

        private VideoAndReviewData getMoviesDataFromJson(String MoviesJsonStrVideo, String MoviesJsonStrReview)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String VIDEO_RESULTS = "results";
            final String Video_key = "key";
            final String Video_name = "name";
            final String REVIEW_RESULTS = "results";
            final String Review_author = "author";
            final String Review_content = "content";
            final String YOUTUBE_LINK_BASE_URL = "https://www.youtube.com/watch?v=";

            JSONObject VideoJson = new JSONObject(MoviesJsonStrVideo);
            JSONObject ReviewJson = new JSONObject(MoviesJsonStrReview);
            JSONArray VideoArray = VideoJson.getJSONArray(VIDEO_RESULTS);
            JSONArray ReviewArray = ReviewJson.getJSONArray(REVIEW_RESULTS);
            VideoAndReviewData mData = new VideoAndReviewData();
            for (int i = 0; i < VideoArray.length(); i++) {
                JSONObject Movie = VideoArray.getJSONObject(i);
                mData.TrailerName.add(Movie.getString(Video_name));
                mData.TrailerLink.add(YOUTUBE_LINK_BASE_URL + Movie.getString(Video_key));
                mData.TrailerImageLink.add(TRAILER_PIC_BASE_URL + Movie.getString(Video_key) + TRAILER_PIC_FOOTER_NAME);
            }
            for (int i = 0; i < ReviewArray.length(); i++) {
                JSONObject Movie = ReviewArray.getJSONObject(i);
                mData.ReviewAuthor.add(Movie.getString(Review_author));
                mData.ReviewContent.add(Movie.getString(Review_content));

            }
            return mData;
        }

        @Override

        protected VideoAndReviewData doInBackground(Integer... params) {


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
                final String YOUTUBE_TRAILER_BASE_URL = "https://api.themoviedb.org/3/movie/" + params[0] + "/videos?";
                final String REVIEW_BASE_URL = "https://api.themoviedb.org/3/movie/" + params[0] + "/reviews?";
                final String APPID_PARAM = "api_key";


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
                StringBuilder bufferV = new StringBuilder();
                StringBuilder bufferR = new StringBuilder();
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
                    bufferV.append(lineV).append("\n");
                }
                while ((lineR = readerR.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    bufferR.append(lineR).append("\n");
                }

                if (bufferV.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                if (bufferR.length() == 0) {
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

            try {
                return getMoviesDataFromJson(moviesJsonStrVideo, moviesJsonStrReview);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(VideoAndReviewData result) {
            if (result != null) {
                thisMovieData.TrailerName.clear();
                thisMovieData.TrailerLink.clear();
                thisMovieData.TrailerImageLink.clear();
                thisMovieData.ReviewAuthor.clear();
                thisMovieData.ReviewContent.clear();
                thisMovieData = result;

            }

            if (!thisMovieData.TrailerName.isEmpty() && !thisMovieData.TrailerLink.isEmpty()) {
                mTrailerAdapter =
                        new HorizontalRecyclerTrailerAdapter(getContext(),
                                thisMovieData.TrailerImageLink,
                                thisMovieData.TrailerLink);
                mTrailerRecycler.setAdapter(mTrailerAdapter);
                mTrailerAdapter.notifyDataSetChanged();
            }


            if (thisMovieData.ReviewContent.isEmpty()) {
                apiReviewSection.setVisibility(View.GONE);
            } else {
                apiReviewSection.setVisibility(View.VISIBLE);
                mReviewAdapter = new HorizontalReviewAdapter(getContext(), thisMovieData.ReviewAuthor, thisMovieData.ReviewContent);
                mReviewRecycler.setAdapter(mReviewAdapter);
                mReviewAdapter.notifyDataSetChanged();
            }


        }

    }

    ;


}
