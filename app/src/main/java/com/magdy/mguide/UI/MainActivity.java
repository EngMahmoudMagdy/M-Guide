package com.magdy.mguide.UI;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;


import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.magdy.mguide.Data.Contract;
import com.magdy.mguide.Information;
import com.magdy.mguide.ListInfoListener;
import com.magdy.mguide.R;


public class MainActivity extends AppCompatActivity implements ListInfoListener
{

    public boolean mTwoPane ;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private Menu menu ;
    TextView navName , navEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            Log.w("creating Main " , " here");
        FrameLayout flPanel2 = (FrameLayout) findViewById(R.id.fl_panel2);

        mTwoPane =(null != flPanel2);

        MainActivityFragment mainActivityFragment = new MainActivityFragment();
        mainActivityFragment.setListInfoListenter(this);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fl_panel1,mainActivityFragment)
                    .commit();
        }


        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);//layout itself
        nvDrawer = (NavigationView) findViewById(R.id.nvView); //navigation
        menu = nvDrawer.getMenu();
        View header = nvDrawer.inflateHeaderView(R.layout.nav_header);
        navEmail = (TextView) header.findViewById(R.id.emailtext);
        navName = (TextView) header.findViewById(R.id.uname);
        mDrawer.addDrawerListener(drawerToggle);


        setupDrawerContent(nvDrawer);
        drawerToggle = setupDrawerToggle();
        drawerToggle.setDrawerIndicatorEnabled(true);


    }


    public void setupDrawerContent(NavigationView upDrawerContent) {
        upDrawerContent.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    private void selectDrawerItem(MenuItem menuItem) {

        // Highlight the selected item has been done by NavigationView

        MainActivityFragment mainActivityFragment ;
        int type = 0 ;
        menuItem.setChecked(true);
        // Set action bar title
        // Close the navigation drawer
        mDrawer.closeDrawers();
        switch(menuItem.getItemId()) {
            case R.id.nav_most:
                type= 0;
                break;
            case R.id.nav_top:
                type= 1;
                break;
            case R.id.nav_fav:
                type = 2 ;
                break;
            case R.id.nav_log:
                /*if(mauth.getCurrentUser()!=null) {
                    mauth.signOut();
                    finish();
                }*/
                break;
            default:
                type = 0;
        }
        try {

            mainActivityFragment = new MainActivityFragment(type);
            mainActivityFragment.setListInfoListenter(this);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_panel1,mainActivityFragment)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar_home reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.drawer_open,
                R.string.drawer_close)
        {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public void setSelectedList(Information info) {
        if (mTwoPane)
        {
            DetailActivityFragment detailActivityFragment = new DetailActivityFragment();
            Bundle extra =  new Bundle() ;
            extra.putString(Contract.Movie.COLUMN_TITLE, info.Title);
            extra.putString(Contract.Movie.COLUMN_DATE, info.Date);
            extra.putString(Contract.Movie.COLUMN_RATE, info.Vote);

            extra.putString(Contract.Movie.COLUMN_OVERVIEW, info.OverView);

            extra.putString(Contract.Movie.COLUMN_PIC_LINK, info.PIC);
            extra.putInt(Contract.Movie.COLUMN_MOVIE_ID, info.id);
            detailActivityFragment.setArguments(extra);
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_panel2 ,detailActivityFragment).commit();

        }
        else {

            Intent intent = new Intent(this, DetailActivity.class);
            //intent.putExtra("t1", MovieData.get(position).Title);
            intent.putExtra(Contract.Movie.COLUMN_TITLE, info.Title);
            intent.putExtra(Contract.Movie.COLUMN_DATE, info.Date);
            intent.putExtra(Contract.Movie.COLUMN_RATE, info.Vote);

            intent.putExtra(Contract.Movie.COLUMN_OVERVIEW, info.OverView);

            intent.putExtra(Contract.Movie.COLUMN_PIC_LINK, info.PIC);
            intent.putExtra(Contract.Movie.COLUMN_MOVIE_ID, info.id);


            startActivity(intent);
        }

    }

}
