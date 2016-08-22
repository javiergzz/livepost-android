package com.livepost.javiergzzr.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.support.v7.widget.SearchView;
import android.widget.Button;

import android.widget.ListView;

import com.firebase.client.Firebase;

import com.livepost.javiergzzr.fragments.FragmentChatClass;
import com.livepost.javiergzzr.fragments.ListSessionsFragment;
import com.livepost.javiergzzr.interfaces.OnFragmentInteractionListener;

import com.livepost.javiergzzr.specialViews.NonSwipeableViewPager;
import com.livepost.javiergzzr.livepost.R;
import com.livepost.javiergzzr.objects.Search;


public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener{
    private Intent starterIntent;
    public static final String TAG = "MainActivity";
    public static final int HOME_IDX = 0;
    public static final int FAVORITES_IDX=1;
    public static final int NEW_SESSION_IDX=2;
    public static final int PROFILE_IDX = 3;
    public static final int CHAT_IDX = 4;
    public static final String REBOOT_REQ = "reboot_req";

    protected Firebase mFirebaseRef;
    protected Menu mMenu;
    private ListView mListView;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private NonSwipeableViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        starterIntent = getIntent();
        mFirebaseRef = new Firebase(getString(R.string.firebase_url));
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_36dp);
        setSupportActionBar(toolbar);
        //setupNavigation();
    }
    private void setupNavigation(){
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (NonSwipeableViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new NonSwipeableViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTitle(mSectionsPagerAdapter.getPageTitle(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setTitle(mSectionsPagerAdapter.getPageTitle(HOME_IDX));
        setupDrawers();
    }
    protected void launchLogin(){
        startActivityForResult(new Intent(this, LoginActivity.class), 1);
    }
    private void setupDrawers(){
        View navMenu = findViewById(R.id.nav_menu);
        View homeButton = navMenu.findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(HOME_IDX,true);
            }
        });
        View favoritesButton = navMenu.findViewById(R.id.favorites_button);
        favoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(FAVORITES_IDX, true);
            }
        });
        View newSessionButton = navMenu.findViewById(R.id.new_session_button);
        newSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(NEW_SESSION_IDX, true);
            }
        });
        View profileButton = navMenu.findViewById(R.id.profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(PROFILE_IDX, true);
            }
        });
        Button loginButton = (Button)navMenu.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchLogin();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        Firebase.setAndroidContext(this);
        setupNavigation();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;
        updateMenu();
        return super.onCreateOptionsMenu(menu);
    }
    void updateMenu(){
        MenuItem loginActionView = mMenu.findItem(R.id.action_login);
        MenuItem logoutActionView = mMenu.findItem(R.id.action_logout);
        if(mFirebaseRef.getAuth()==null){
            loginActionView.setVisible(true);
            logoutActionView.setVisible(false);
        }else{
            loginActionView.setVisible(false);
            logoutActionView.setVisible(true);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
                if(drawer!=null)
                    if(drawer.isDrawerOpen(GravityCompat.START))
                        drawer.closeDrawer(GravityCompat.START);
                    else
                        drawer.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                startActivityForResult(new Intent(this, SettingsActivity.class), 1);
                return true;
            case R.id.action_login:
                launchLogin();
                updateMenu();
                return true;
            case R.id.action_logout:
                mFirebaseRef.unauth();
                updateMenu();
                Log.i(TAG,"User Logged out.");
                return true;
        };
        return super.onOptionsItemSelected(item);
    }
    /*
    @Override
    public void OnSelectChat(String key, String author){
        if(Utilities.isNullOrEmpty(key))return;

        SearchView searchView =(SearchView) MenuItemCompat.getActionView(mMenu.findItem(R.id.search));
        searchView.setQuery("", false);
        searchView.clearFocus();
        mMenu.findItem(R.id.search).collapseActionView();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, FragmentChatClass.newInstance(key, author));
        transaction.addToBackStack(null);
        transaction.commit();
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateMenu();
        if(data!=null && data.getBooleanExtra(REBOOT_REQ,false)){
            finish();
            startActivity(starterIntent);
        }
    }

    @Override
    public void onFragmentInteraction(int id, Bundle args) {
        switch (id){
            case HOME_IDX:
                break;
            case CHAT_IDX:
                break;
            default:
                break;
        }
    }
    /*
    @Override
    public boolean onQueryTextSubmit(String query) {
        String searchQuery = query.trim();
        if(query==null ||query.isEmpty()|| query =="") {
            mSessionListAdapter = new SessionListAdapter(mFirebaseRef.child("sessions").limitToLast(50), this, R.layout.item_session,false);
            mListView.setAdapter(mSessionListAdapter);
            mSessionListAdapter.notifyDataSetChanged();
            return false;
        }
        Log.d(TAG, "Search Query:" + query);
        String[] catArray=getResources().getStringArray(R.array.categories);
        if(Arrays.asList(catArray).contains(query)){
            searchQuery = "category:"+searchQuery;
            Log.d(TAG,"category search:"+searchQuery);
        }
        //Elasticsearch query
        Firebase searchRef = new Firebase(getString(R.string.firebase_url)+"search");
        Search searchObj = new Search("firebase","session",searchQuery);
        Firebase newSearchRef = searchRef.child("request").push();
        newSearchRef.setValue(searchObj);
        searchRef.child("response/"+newSearchRef.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot d) {
                if (d.hasChildren() && d.child("total").getValue().toString() != "0") {
                    Log.d(TAG, d.toString());
                    setSessionListAdapter(d.child("/hits/").getRef(), R.layout.item_session, true);
                } else {
                    Toast.makeText(main, getResources().getString(R.string.no_results), Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        //Category filtering

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.trim();
        if(newText ==null || newText.isEmpty() || newText==""|| newText.length()<1) {
            mSessionListAdapter = new SessionListAdapter(mFirebaseRef.child("sessions").limitToLast(50), this, R.layout.item_session,false);
            mListView.setAdapter(mSessionListAdapter);
            mSessionListAdapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }*/
/*
    @Override
    public void onSupportActionModeFinished(ActionMode mode) {
        super.onSupportActionModeFinished(mode);
        Log.e("Main","Action Finished");
    }*/

    /*
    @Override
    public boolean onClose() {
        mSessionListAdapter = new SessionListAdapter(mFirebaseRef.child("sessions").limitToLast(50), this, R.layout.item_session);
        mListView.setAdapter(mSessionListAdapter);
        //mSessionListAdapter.notifyDataSetChanged();
        return true;
    */

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);
            switch (position) {
                case HOME_IDX:
                    return ListSessionsFragment.newInstance(ListSessionsFragment.STAGGERED_TYPE);
                case CHAT_IDX:
//                    return FragmentChatClass.newInstance();
                case FAVORITES_IDX:
                    return ListSessionsFragment.newInstance(ListSessionsFragment.STAGGERED_TYPE);
                case NEW_SESSION_IDX:
                    return ListSessionsFragment.newInstance(ListSessionsFragment.STAGGERED_TYPE);
                case PROFILE_IDX:
                    return ListSessionsFragment.newInstance(ListSessionsFragment.STAGGERED_TYPE);
                default: return ListSessionsFragment.newInstance(ListSessionsFragment.LIST_TYPE);
            }
        }

        @Override
        public int getCount() {
            // Show 5 total pages.
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case HOME_IDX:
                    return getString(R.string.home_str);
                case FAVORITES_IDX:
                    return getString(R.string.favorites_str);
                case NEW_SESSION_IDX:
                    return getString(R.string.new_session_str);
                case PROFILE_IDX:
                    return getString(R.string.profile_str);
                default: return getString(R.string.home_str);
            }
        }
    }



}
