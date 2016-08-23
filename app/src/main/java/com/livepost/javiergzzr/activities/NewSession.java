package com.livepost.javiergzzr.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.firebase.client.Firebase;
import com.livepost.javiergzzr.adapters.ExpandableListAdapter;
import com.livepost.javiergzzr.asynctask.S3PutObjectTask;
import com.livepost.javiergzzr.interfaces.OnPutImageListener;
import com.livepost.javiergzzr.livepost.R;
import com.livepost.javiergzzr.objects.Post;
import com.livepost.javiergzzr.util.GV;
import com.livepost.javiergzzr.util.Utilities;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class NewSession extends ActionBarActivity implements OnPutImageListener{
    private static final String TAG_CLASS = "NewSession";
    private static final int TAKE_PICTURE = 0;
    private static final int PHOTO_SELECTED = 1;
    public static final String CHAT_PREFS_KEY = "ChatPrefs";
    public static final String USERNAME_KEY = "username";
    private ImageView mImageView;
    private TextView txtCategory;
    private EditText txtDescription;
    private ExpandableListView list;
    private Uri mIimageUri;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private ExpandableListAdapter listAdapter;
    private OnPutImageListener putImageListener;
    private AmazonS3Client s3Client = new AmazonS3Client(new BasicAWSCredentials(GV.ACCESS_KEY_ID, GV.SECRET_KEY));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_session);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ImageView imgSelectPhoto = (ImageView) findViewById(R.id.imgSelectPhoto);
        mImageView = (ImageView) findViewById(R.id.imgSelectPhoto);
        txtCategory = (TextView) findViewById(R.id.txtCategory);
        txtDescription = (EditText) findViewById(R.id.txtTitle);

        imgSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PHOTO_SELECTED);
            }
        });
        list = (ExpandableListView)findViewById(R.id.expandableListView);
        prepareListData();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        // setting list adapter
        list.setAdapter(listAdapter);

        list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                txtCategory.setText(listDataChild.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition));
                return false;
            }
        });
    }


    private void prepareListData() {
        listDataChild = new HashMap<String, List<String>>();
        listDataHeader = Arrays.asList(getResources().getStringArray(R.array.array_categories));

        List<String> news = Arrays.asList(getResources().getStringArray(R.array.array_news));
        List<String> entertainment = Arrays.asList(getResources().getStringArray(R.array.array_entertainments));
        List<String> sports = Arrays.asList(getResources().getStringArray(R.array.array_sports));
        List<String> technology = Arrays.asList(getResources().getStringArray(R.array.array_technology));
        List<String> business = Arrays.asList(getResources().getStringArray(R.array.array_business));
        List<String> events = Arrays.asList(getResources().getStringArray(R.array.array_events));
        List<String> lifestyle = Arrays.asList(getResources().getStringArray(R.array.array_lifestyle));
        List<String> interviews = Arrays.asList(getResources().getStringArray(R.array.array_interviews));

        listDataChild.put(listDataHeader.get(0), news); // Header, Child data
        listDataChild.put(listDataHeader.get(1), entertainment);
        listDataChild.put(listDataHeader.get(2), sports);
        listDataChild.put(listDataHeader.get(3), technology);
        listDataChild.put(listDataHeader.get(4), business);
        listDataChild.put(listDataHeader.get(5), events);
        listDataChild.put(listDataHeader.get(6), lifestyle);
        listDataChild.put(listDataHeader.get(7), interviews);
    }

    private void showDialogText(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NewSession.this);
        Resources r = getResources();
        builder.setTitle(r.getString(R.string.lbl_title));
        builder.setMessage(msg)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create();
        builder.show();
    }

    private void addSession(){
        Resources r = getResources();
        if (Utilities.isNullOrEmpty(txtCategory.getText().toString()) || Utilities.isNullOrEmpty(txtDescription.getText().toString())) {
            showDialogText(r.getString(R.string.lbl_warn));
        } else {
            String key = txtDescription.getText().toString().toLowerCase().trim();
            String pictureName = "profile" + key;
            putImageListener = new OnPutImageListener() {
                @Override
                public void onSuccess(String url) {
                    saveSession(url);
                }
            };
            new S3PutObjectTask(NewSession.this, s3Client, putImageListener, pictureName, true).execute(mIimageUri);

        }
    }

    private void saveSession(String url){
        SharedPreferences prefs = getSharedPreferences(CHAT_PREFS_KEY, 0);
        String mUsername = prefs.getString(USERNAME_KEY, null);
        Firebase ref = new Firebase(getString(R.string.firebase_url)).child("posts");
        //Calendar c = Calendar.getInstance();
        //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lastTime = System.currentTimeMillis()/ 1000L;
        Post post = new Post(mUsername, mUsername,txtCategory.getText().toString(),true,null,lastTime, getString(R.string.amazon_image_path) + url,
                lastTime,txtDescription.getText().toString());
        ref.push().setValue(post);
        NewSession.this.finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE:
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    mImageView.setImageBitmap(imageBitmap);
                    break;
                case PHOTO_SELECTED:
                    try {
                        mIimageUri = data.getData();
                        Bitmap selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mIimageUri);
                        mImageView.setImageBitmap(null);
                        mImageView.setMaxWidth(150);
                        mImageView.setMaxHeight(150);
                        mImageView.setImageBitmap(selectedImage);
                    } catch (IOException e) {
                        Log.e(TAG_CLASS, "Error: " + e);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_session, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_session) {
            addSession();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSuccess(String url) {
        if(url==null || url == "")
            showDialogText(getResources().getString(R.string.lbl_err));
    }
}
