package com.livepost.javiergzzr.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Query;
import com.firebase.client.Transaction;
import com.livepost.javiergzzr.livepost.R;
import com.livepost.javiergzzr.objects.Messages;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.HashMap;
import java.util.Map;

public class ChatAdapter  {
    private static final String TAG = "ChatAdapter";
    public static final String MSG_KEY = "msg";
    private String mChatKey;
    private View.OnClickListener likeListener;
    private View.OnClickListener disLikeListener;
    private FragmentActivity mActivity;
    protected int mNumChildren;
    protected TextView mFollowersView;
    private LruCache<String, Bitmap> mMemoryCache;
    protected String mUsername;
    private DisplayImageOptions mOptions;
    private ImageLoader mImageLoader;


    public ChatAdapter(Query ref, FragmentActivity activity, int layout, String chatKey) {
        //super(ref, Messages.class, layout, activity);
        this.mActivity = activity;
        this.mChatKey = chatKey;
        this.mUsername =
                mActivity.getSharedPreferences(mActivity.getResources()
                        .getString(R.string.preference_file_key),Context.MODE_PRIVATE).getString("username", null);
        ImageLoaderConfiguration config  =  new ImageLoaderConfiguration.Builder(activity)

                .build();
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(config);

    }

    //@Override
    protected void populateView(View view, final Messages chat, String key) {
        TextView message = (TextView) view.findViewById(R.id.message);
        //CircleImageView img = (CircleImageView)view.findViewById(R.id.imgChat);
        ImageView img = (ImageView)view.findViewById(R.id.imgChat);
        Log.i(TAG, "" + chat.getSender());
        final ImageView imgLike = (ImageView)view.findViewById(R.id.imgLike);
        final TextView txtLikes = (TextView)view.findViewById(R.id.txtLike);
        final int likeCount = chat.getCountLikes();
        final Firebase upvotesRef = new Firebase(mActivity.getResources().getString(R.string.firebase_url)+"messages/" + mChatKey+ "/" + key );
        txtLikes.setText(likeCount + "");
        if(chat.getSender() != null){
            TextView authorText = (TextView) view.findViewById(R.id.author);
            authorText.setText(chat.getSender() + " :");
        }
        if(chat.getAvatar() != null){
            loadBitmap(chat.getAvatar(), (ImageView) view.findViewById(R.id.imgProfile));
        }
        if(chat.getMessage().contains(".png")){
            message.setVisibility(View.GONE);
            img.setVisibility(View.VISIBLE);
            loadBitmap(chat.getMessage(), (ImageView) view.findViewById(R.id.imgChat));
        }else{
            message.setVisibility(View.VISIBLE);
            img.setVisibility(View.GONE);
            message.setText(chat.getMessage());
        }
        if(true||mUsername!=null && mUsername == chat.getSender()){
            ImageView editView = (ImageView) view.findViewById(R.id.imgEdit);
            editView.setVisibility(View.VISIBLE);
            editView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG,"showDialog");
                    showDialog(chat);
                }
            });
        }
        String timeMsg;
        timeMsg = chat.getTimeMessage();
        if(timeMsg!=null && timeMsg!=""){
            ((TextView)view.findViewById(R.id.date)).setText(timeMsg);
        }

        likeListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeMessage(upvotesRef,likeCount);

            }
        };
        imgLike.setOnClickListener(likeListener);
    }
    public void showDialog(Messages m) {
        FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
        EditPostDialogFragment newFragment = EditPostDialogFragment.newInstance(m);
        //Bundle b = new Bundle();
        //b.putSerializable(MSG_KEY,m);
        //newFragment.setArguments(b);
        newFragment.show(fragmentManager, "dialog");
    }
    protected void likeMessage(final Firebase upvotesRef, final int likeCount){
        if(mUsername!=null && mUsername!="") {
            Map<String, Object> m = new HashMap<String, Object>();
            m.put(mUsername, true);
            upvotesRef.child("likes").updateChildren(m);
        }

        upvotesRef.child("countLikes").runTransaction(new Transaction.Handler(){
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                currentData.setValue(likeCount+1);
                return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
            }
            @Override
            public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot currentData) {
                if(firebaseError!=null)
                    Log.e(TAG, firebaseError.getMessage());
                //This method will be called once with the results of the transaction.
            }
        });
    }
    public void loadBitmap(String resUrl, ImageView imageView) {
        mImageLoader.displayImage(resUrl,imageView);
    }

}