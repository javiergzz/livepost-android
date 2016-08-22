package com.livepost.javiergzzr.util;

        import android.graphics.Bitmap;

        import com.livepost.javiergzzr.livepost.R;
        import com.nostra13.universalimageloader.core.DisplayImageOptions;
        import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class Consts {
    // Universal Image Loader
    public static final DisplayImageOptions UIL_DEFAULT_DISPLAY_OPTIONS = new DisplayImageOptions.Builder()
            .cacheInMemory(true).imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).cacheOnDisc(true).
                    considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();

/*
    public static final DisplayImageOptions UIL_USER_AVATAR_DISPLAY_OPTIONS = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.placeholder_user).showImageForEmptyUri(R.drawable.placeholder_user)
            .showImageOnFail(R.drawable.placeholder_user).cacheOnDisc(true).cacheInMemory(true).considerExifParams(true).build();

    public static final DisplayImageOptions UIL_GROUP_AVATAR_DISPLAY_OPTIONS = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.placeholder_group).showImageForEmptyUri(
                    R.drawable.placeholder_group).showImageOnFail(R.drawable.placeholder_group).cacheOnDisc(
                    true).cacheInMemory(true).considerExifParams(true).build();*/
}