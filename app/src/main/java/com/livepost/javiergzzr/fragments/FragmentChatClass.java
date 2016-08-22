package com.livepost.javiergzzr.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.livepost.javiergzzr.adapters.ChatAdapter;
import com.livepost.javiergzzr.asynctask.S3PutObjectTask;
import com.livepost.javiergzzr.interfaces.OnFragmentInteractionListener;
import com.livepost.javiergzzr.interfaces.OnPutImageListener;
import com.livepost.javiergzzr.livepost.R;
import com.livepost.javiergzzr.objects.Chat;
import com.livepost.javiergzzr.objects.Messages;
import com.livepost.javiergzzr.util.GV;

import java.util.Date;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Random;

public class FragmentChatClass extends Fragment implements AbsListView.OnItemClickListener {

    private static final String TAG_CLASS = "FragmentChatClass";
    private static final String TAG_ID = "id";
    private static final String TAG_AUTHOR = "author";
    private static final int TAKE_PICTURE = 0;
    private static final int PHOTO_SELECTED = 1;
    private Firebase mFirebaseRef;
    private String mId, mUsername, mImgProfile, mName, mAuthor;
    private OnFragmentInteractionListener mListener;
    private ValueEventListener mConnectedListener;
    private ChatAdapter mChatListAdapter;
    private  Uri mIimageUri;
    private  AmazonS3Client s3Client = new AmazonS3Client(new BasicAWSCredentials(GV.ACCESS_KEY_ID, GV.SECRET_KEY));
    private  OnPutImageListener putImageListener;

    public static FragmentChatClass newInstance(String id, String author) {
        FragmentChatClass fragment = new FragmentChatClass();
        Bundle args = new Bundle();
        args.putString(TAG_ID, id);
        args.putString(TAG_AUTHOR, author);
        fragment.setArguments(args);
        return fragment;
    }
    public FragmentChatClass() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mId = getArguments().getString(TAG_ID);
            mAuthor = getArguments().getString(TAG_AUTHOR);
        }
        mFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url)).child("messages").child(mId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);

        // Setup our input methods. Enter key on the keyboard or pushing the send button
        EditText inputText = (EditText) view.findViewById(R.id.messageInput);
        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });

        view.findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        view.findViewById(R.id.btnAddPicture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                getActivity().startActivityForResult(intent,PHOTO_SELECTED);
            }
        });

        putImageListener = new OnPutImageListener() {
            @Override
            public void onSuccess(String url) {
                Chat chat = new Chat(getResources().getString(R.string.amazon_image_path) + url, mUsername, mName, mImgProfile);
                mFirebaseRef.push().setValue(chat);
            }
        };
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        SharedPreferences prefs = getActivity().getSharedPreferences("ChatPrefs", 0);
        mUsername = prefs.getString("username", null);
        //mUsername = mUsername.replaceAll("[\u0000]", "");
        mImgProfile = prefs.getString("imageProfile", null);
        mName = prefs.getString("twNameUser", null);
        final ListView listView = (ListView)getView().findViewById(android.R.id.list);
        listView.setDivider(null);
        //if(mAuthor.equals(mUsername)){
            LinearLayout messageBar = (LinearLayout)getView().findViewById(R.id.listFooter);
            messageBar.setVisibility(View.VISIBLE);
        //}else{
          //  LinearLayout messageBar = (LinearLayout)getView().findViewById(R.id.listFooter);
            //messageBar.setVisibility(View.GONE);
        //}
        // Tell our list adapter that we only want 50 messages at a time
        try {
            /*mChatListAdapter = new ChatAdapter(mFirebaseRef.limitToLast(50), getActivity(), R.layout.chat_message_one, mId);
            listView.setAdapter(mChatListAdapter);
            mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    listView.setSelection(mChatListAdapter.getCount() - 1);
                }
            });*/
        }catch (Exception e){
            Log.e(TAG_CLASS,"Bad adapter");
        }
        // Finally, a little indication of connection status
        mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    Log.i(TAG_CLASS,"Connected to Firebase");
                } else {
                    Log.i(TAG_CLASS,"Disconnected from Firebase");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        //mChatListAdapter.cleanup();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void sendMessage() {
        final EditText inputText = (EditText) getView().findViewById(R.id.messageInput);
        String input = inputText.getText().toString();
        if (!input.equals("")) {
            // Create our 'model', a Chat object
            Chat chat = new Chat(input, mUsername, mName, mImgProfile);
            //(String avatar, int countLikes, Map<String,Boolean> likes, String message, String sender, String timeMessage, long timestamp)
            final long unixTime = System.currentTimeMillis() / 1000L;
            final String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            //String userAvatar = SharedPreferences.
            Messages m = new Messages("def",0,null,input,mUsername,currentDateTimeString,unixTime);
            // Create a new, auto-generated child of that chat location, and save our chat data there
            mFirebaseRef.push().setValue(chat);
            inputText.setText("");
        }
    }

    public  void loadImage(Context ctx, Intent data){
        if(data != null){
            //try {
            Random r = new Random();
            mIimageUri = data.getData();
            //TODO .- imagen selectedImage ?
            //Bitmap selectedImage = MediaStore.Images.Media.getBitmap(ctx.getContentResolver(), mIimageUri);
            new S3PutObjectTask(ctx, s3Client, putImageListener, mId + mUsername + r.nextInt(100000), false).execute(mIimageUri);
            //new S3PutObjectTask(NewSession.this, s3Client, putImageListener, pictureName, true).execute(mIimageUri);
            //} catch (IOException e) {
            // Log.e(TAG_CLASS, "Error: " + e);
            //}
        }
    }

}