package com.magdy.mguide.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;


import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.magdy.mguide.BuildConfig;
import com.magdy.mguide.Data.Contract;
import com.magdy.mguide.Information;
import com.magdy.mguide.ListInfoListener;
import com.magdy.mguide.R;
import com.squareup.picasso.Picasso;

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

/**
 * A placeholder fragment containing a simple view.
 */

public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{


    private GridView grid ;
    private TextView errorText ;
    private ImageAdapter imgrid  ;
    public List<Information> MovieData2  = new ArrayList<Information>();
    public List<String> MovieTitle  = new ArrayList<String>();
    ListInfoListener fListener ;
    View rootView;
    Context mContext ;
    private int mType ;
    private final static String TYPE_KEY = "type";

    public MainActivityFragment()
    {

    }
    @SuppressLint("ValidFragment")
    public MainActivityFragment(int type)
    {
        mType = type ;
        //updatePage();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        rootView = inflater.inflate(R.layout.fragment_main, container, false);


        grid = (GridView) rootView.findViewById(R.id.grid1);
        errorText = (TextView)rootView.findViewById(R.id.no_internet);

        mContext = getActivity() ;
        imgrid = new ImageAdapter(getActivity(), MovieData2);
        grid.setAdapter(imgrid);


        if(savedInstanceState!=null) {
            mType = savedInstanceState.getInt(TYPE_KEY);
        }


        return rootView ;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TYPE_KEY,mType);
    }

    public void updatePage() {

        FetchMoviesTask moviesTask = new FetchMoviesTask();
        switch(mType)
        {
            case 0:
                moviesTask.execute(getString(R.string.pref_kind_most_popular));
                break;
            case 1:
                moviesTask.execute(getString(R.string.pref_kind_top_rated));
                break;
            case 2:
                getLoaderManager().initLoader(0, null, this);
                break;
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        updatePage();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                Contract.Movie.URI,
                Contract.Movie.MOVIE_COLUMNS.toArray(new String[]{}),
                null, null, Contract.Movie.COLUMN_DATE);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        MovieData2.clear();
        if(data==null)
        {
            errorText.setText(R.string.no_movies);
            errorText.setVisibility(View.VISIBLE);
        }
        else{
            for (int i = 0; i < data.getCount(); i++) {
                Information movie1 = new Information();
                data.moveToPosition(i);
                movie1.setPIC( data.getString(Contract.Movie.POSITION_PIC_LINK));
                movie1.setTitle(data.getString(Contract.Movie.POSITION_TITLE));
                movie1.setOverView(data.getString(Contract.Movie.POSITION_OVERVIEW));
                movie1.setVote(data.getString(Contract.Movie.POSITION_RATE));
                movie1.setDate(data.getString(Contract.Movie.POSITION_DATE));
                movie1.setId(data.getInt(Contract.Movie.POSITION_MOVIE_ID));
                MovieData2.add(movie1);
                errorText.setVisibility(View.GONE);
            }
            imgrid.notifyDataSetChanged();
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    private class FetchMoviesTask extends AsyncTask<String ,Void ,List<Information> > {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName() ;
        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private List <Information> getMoviesDataFromJson(String MoviesJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_RESULTS = "results";
            final String TMDB_PIC = "poster_path";
            final String TMDB_OverView = "overview";
            final String TMDB_Date = "release_date";
            final String TMDB_Title = "title";
            final String TMDB_Vote = "vote_average";
            final String TMDB_ID = "id";



            JSONObject MoviesJson = new JSONObject(MoviesJsonStr);
            JSONArray MoviesArray = MoviesJson.getJSONArray(TMDB_RESULTS);
            List<Information> MovieData  = new ArrayList<>();
            MovieData.clear() ;
            for(int i = 0; i < MoviesArray.length(); i++) {
                Information movie1 = new Information();
                JSONObject Movie = MoviesArray.getJSONObject(i);
                movie1.PIC = Movie.getString(TMDB_PIC);

                MovieTitle.add( Movie.getString(TMDB_Title));
                movie1.Title= Movie.getString(TMDB_Title);
                movie1.OverView= Movie.getString(TMDB_OverView);
                movie1.Vote= Movie.getString(TMDB_Vote);
                movie1.Date= Movie.getString(TMDB_Date);
                movie1.id= Movie.getInt(TMDB_ID);

                MovieData.add(movie1) ;
            }
            Log.v("data coming " , MovieTitle.get(0));


            return MovieData;
        }

        @Override

        protected List <Information> doInBackground(String... params) {



            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;
            try {

                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                //http://api.themoviedb.org/3/movie/popular?api_key=179a8cf9fc6fab0def62671610a2704b
                final String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/"+params[0];
                final String APPID_PARAM ="api_key";
                Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();


                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Movies JSON String " + moviesJsonStr);

            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            try{

                return getMoviesDataFromJson(moviesJsonStr);


            }
            catch (JSONException e)
            {
                Log.e(LOG_TAG,e.getMessage(),e);
                e.printStackTrace();


            }


            return null;
        }
        @Override
        protected void onPostExecute(List <Information> result)
        {
            MovieData2.clear();
            if(result == null)
            {
                errorText.setText(R.string.no_movies_inter);
                errorText.setVisibility(View.VISIBLE);
            }
            else
            {
                for(int i = 0 ; i < result.size() ; i++)
                {
                    MovieData2.add(result.get(i));
                }
                errorText.setVisibility(View.GONE);
            }
            // making the grid here :
            imgrid = new ImageAdapter(getActivity(), MovieData2);
            grid.setAdapter(imgrid);
            imgrid.notifyDataSetChanged();

        }

    }

    //setter for listener
    public void setListInfoListenter(ListInfoListener lsn)
    {
        fListener = lsn  ;
    }



    //the custom adapter

    private class ImageAdapter extends BaseAdapter
    {
         static final String MOVIE_PIC_BASE_URL = "http://image.tmdb.org/t/p/w185//";
        List <Information> list = new ArrayList<> ();
        private Context mContext ;
//        private Cursor cursor;
         ImageAdapter (Context c , List <Information >s)
        {
            mContext = c ;
            list =s ;

        }
        /*void setCursor(Cursor cursor) {
            this.cursor = cursor;
            notifyDataSetChanged();
        }*/
        @Override
        public int getCount() {


            return list.size();
            //return MovieData2.size();
        }

        @Override
        public Object getItem(int position) {


            return list.get(position);
           // return MovieData2.get(position).PIC;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v=inflater.inflate(R.layout.grid_item,parent,false);
            ImageView view = (ImageView) v.findViewById(R.id.movie_image) ;
            view.setContentDescription(list.get(position).Title);
            TextView textView = (TextView) v.findViewById(R.id.movie_title);
            textView.setText(list.get(position).Title);
            Picasso.with(mContext)
                    .load(MOVIE_PIC_BASE_URL+list.get(position).PIC)
                    .placeholder(R.drawable.placeholder)
                    .fit()
                    .into(view);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fListener.setSelectedList(MovieData2.get(position));
                }
            });
            return v;
        }
    }




}
