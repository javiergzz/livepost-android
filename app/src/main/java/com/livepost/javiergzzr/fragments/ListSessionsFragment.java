package com.livepost.javiergzzr.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.livepost.javiergzzr.adapters.PostsListAdapter;
import com.livepost.javiergzzr.adapters.ChatListOfflineAdapter;
import com.livepost.javiergzzr.livepost.R;
import com.livepost.javiergzzr.interfaces.OnFragmentInteractionListener;

public class ListSessionsFragment extends Fragment {
    private static final String TAG= "ListSessionsFragment";
    public static final String ARG_LIST_TYPE = "list-type";
    public static final int STAGGERED_TYPE = 0;
    public static final int LIST_TYPE = 1;
    public static final int COLS_LANDSCAPE = 4;
    public static final int COLS_PORTRAIT = 2;

    private int mPresentationType;
    private int mOrientation;
    private Firebase mFirebaseRef;
    private ValueEventListener mConnectedListener;
    private PostsListAdapter mPostsListAdapter;
    private ChatListOfflineAdapter mOffileSessionsAdapter;
    private OnFragmentInteractionListener mOnFragmentInteractionListener;

    public static ListSessionsFragment newInstance(int listType) {
        ListSessionsFragment fragment = new ListSessionsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LIST_TYPE, listType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseRef = new Firebase(getString(R.string.firebase_url)).child("posts");
        Log.e(TAG,mFirebaseRef.toString());
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String k= getString(R.string.pref_key_home_mode);
        mOrientation = getResources().getConfiguration().orientation;
        mPresentationType = Integer.parseInt(SP.getString(k,"0"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        Context context = view.getContext();
        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;

            if(mPresentationType==LIST_TYPE){
                LinearLayoutManager llm = new LinearLayoutManager(context);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(llm);
            }else {
                int ncolumns = mOrientation == Configuration.ORIENTATION_LANDSCAPE?COLS_LANDSCAPE:COLS_PORTRAIT;
                StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(ncolumns, StaggeredGridLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(sglm);
            }
            setupAdapter(recyclerView);
        }
        return view;
    }
    private void setupAdapter(final RecyclerView recyclerView){
        mPostsListAdapter = new PostsListAdapter(mFirebaseRef.limitToLast(50),(AppCompatActivity)getActivity(), R.layout.item_session,mPresentationType,false);
        recyclerView.setAdapter(mPostsListAdapter);
        mPostsListAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                //recyclerView.scrollToPosition(mPostsListAdapter.getItemCount() - 1);
                recyclerView.scrollToPosition(0);
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();


        mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    Log.i(TAG, "Connected to Firebase");
                    //saveSessions();
                } else {
                    Log.i(TAG, "Disconnected from Firebase");
                    //getSessions();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mPostsListAdapter.cleanup();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            //mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
