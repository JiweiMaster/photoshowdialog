package com.jiweimaster.photoshowdialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
/**
 * Created by 18099 on 2018/11/1.
 */

public class PhotoShowDialog {
    Dialog dialog;
    Context context;
    IconItemListener iconItemListener;
    LinearLayout deleteImageViewLayout;
    LinearLayout downLoadImageViewLayout;
    LinearLayout closeImageViewLayout;

    ViewPager photoViewPager;
    PhotoViewPagerAdapter photoViewPagerAdapter;

    TextView indictorTextView;

    ArrayList<String> imageUrlStr = new ArrayList<>();
    public interface IconItemListener{
        void deleteImageView();
        void onDownloadItemClick();
    }

    public PhotoShowDialog(final Context context,
                           final ArrayList<String> imageUrlStr,
                           String firstUrlStr){
        this.context = context;
        this.imageUrlStr = imageUrlStr;
        int firstPosition = 0;
        for(String urlstr: imageUrlStr){
            if(urlstr.equals(firstUrlStr)){
                break;
            }
            firstPosition++;
        }

        View view = LayoutInflater.from(context).inflate(R.layout.layout_photo_view,null);
        deleteImageViewLayout = view.findViewById(R.id.pictureDeleteLayout);
        downLoadImageViewLayout = view.findViewById(R.id.pictureDownloadLayout);
        closeImageViewLayout = view.findViewById(R.id.dialog_closeLayout);
        photoViewPager = view.findViewById(R.id.photoviewpager);
        indictorTextView = view.findViewById(R.id.indictorTextView);
        photoViewPagerAdapter = new PhotoViewPagerAdapter(context,imageUrlStr);
        photoViewPager.setAdapter(photoViewPagerAdapter);

        //只保存左右两个界面
        photoViewPager.setOffscreenPageLimit(0);
//        通过反射去修改保存的界面值
//        try {
//            Field field = photoViewPager.getClass().getDeclaredField("mOffscreenPageLimit");
//            field.setAccessible(true);
//            field.setInt(photoViewPager,0);
//            Log.e("JiweiTag",field.getInt(photoViewPager)+"");
//
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }

        photoViewPager.setCurrentItem(firstPosition);

        indictorTextView.setText(firstPosition+1+"/"+imageUrlStr.size());

        photoViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                indictorTextView.setText(position+1+"/"+imageUrlStr.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        deleteImageViewLayout.setVisibility(View.INVISIBLE);
        downLoadImageViewLayout.setVisibility(View.INVISIBLE);

        deleteImageViewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iconItemListener.deleteImageView();
            }
        });
        downLoadImageViewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iconItemListener.onDownloadItemClick();
            }
        });
        closeImageViewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog = new Dialog(context,R.style.photoShow);
        dialog.setContentView(view);
    }



    public void setOnIconItemClick(IconItemListener iconItemListener){
        this.iconItemListener = iconItemListener;
    }


    public void setIconDeleteVisibility(boolean isShow){
        if(isShow == true){
            deleteImageViewLayout.setVisibility(View.VISIBLE);
        }else{
            deleteImageViewLayout.setVisibility(View.GONE);
        }
    }

    public void setIconDownLoadVisibility(boolean isShow){
        if(isShow == true){
            downLoadImageViewLayout.setVisibility(View.VISIBLE);
        }else{
            downLoadImageViewLayout.setVisibility(View.GONE);
        }
    }

    public void show(){
        if(imageUrlStr.size() == 0){
            return;
        }
        dialog.show();
    }

    public void dismiss(){
        if(imageUrlStr.size() == 0){
            return;
        }
        dialog.dismiss();
    }
}
