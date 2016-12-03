package com.isee_flynn.foxmovie;

import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

/**
 * 创建人： flynn
 * 创建日期： 2016/12/3.
 */
public class MovieListAdapter extends BaseAdapter {

    private ArrayList<MovieEntity> mData;
    private MainActivity mContext;

    public MovieListAdapter(MainActivity context, ArrayList<MovieEntity> data) {
        mData = data;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.gridview_item, null);
            vh.imgIV = (ImageView) convertView.findViewById(R.id.movieList_iv);

//            DisplayMetrics metric = new DisplayMetrics();
//            mContext.getWindowManager().getDefaultDisplay().getMetrics(metric);
//            int targetWidth = metric.widthPixels / 2;
//            double aspectRatio = (double) 278 / (double) 185;
//            int targetHeight = (int) (targetWidth * aspectRatio);
//            vh.imgIV.setLayoutParams(new RelativeLayout.LayoutParams(targetWidth, targetHeight));
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        disableImage(vh, position);

        return convertView;
    }

    private void disableImage(final ViewHolder vh, int position) {
        vh.imgIV.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Picasso.with(mContext)
                .load(Consts.IMAGEURL + mData.get(position).getBackdrop_path())
                                .transform(transformation)
                .placeholder(R.mipmap.ic_launcher)
                .into(vh.imgIV, new Callback() {
                    @Override
                    public void onSuccess() {
                        //                        Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError() {
                        vh.imgIV.setImageResource(R.mipmap.ic_launcher);
                    }
                });
    }

    private class ViewHolder {
        ImageView imgIV;
    }

        Transformation transformation = new Transformation() {

            @Override
            public Bitmap transform(Bitmap source) {

                DisplayMetrics metric = new DisplayMetrics();
                mContext.getWindowManager().getDefaultDisplay().getMetrics(metric);
                //            int width = metric.widthPixels;     // 屏幕宽度（像素）
                //            int height = metric.heightPixels;   // 屏幕高度（像素）
                //            float density = metric.density;      // 屏幕密度（0.75 / 1.0 / 1.5）
                //            int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
                int targetWidth = metric.widthPixels / 2;
                //            LogCat.i("source.getHeight()="+source.getHeight()+",source.getWidth()="+source.getWidth()+",targetWidth="+targetWidth);
                if (source.getWidth() == 0) {
                    return source;
                }
                Log.i("===", "flynn ==  targetWidth " + targetWidth);
                //如果图片小于设置的宽度，则返回原图
                //            if (source.getWidth() < targetWidth) {
                //                return source;
                //            } else {
                //如果图片大小大于等于设置的宽度，则按照设置的宽度比例来缩放
                double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                int targetHeight = (int) (targetWidth * aspectRatio);
                if (targetHeight != 0 && targetWidth != 0) {
                    Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                    if (result != source) {
                        // Same bitmap is returned if sizes are the same
                        source.recycle();
                    }
                    return result;
                } else {
                    return source;
                }
            }

            @Override
            public String key() {
                return "transformation" + " desiredWidth";
            }
        };
}
