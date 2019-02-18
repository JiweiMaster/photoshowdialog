package com.jiweimaster.photoshowdialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
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

    int currentShowPosition;

    public interface IconItemListener{
        void deleteImageView();
        void onDownloadItemClick(int position);
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
                currentShowPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //显示删除的按钮
        deleteImageViewLayout.setVisibility(View.INVISIBLE);
        //显示下载的按钮
//        downLoadImageViewLayout.setVisibility(View.INVISIBLE);

        deleteImageViewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iconItemListener.deleteImageView();
            }
        });
        downLoadImageViewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iconItemListener.onDownloadItemClick(currentShowPosition);
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

    //url转化为Bitmap
    public static Bitmap returnBitmap(String url){
        URL myFileUrl;
        Bitmap bitmap = null;
        try{
            myFileUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
        }catch(Exception e){
            Log.e("PhotoShowDialog","url turn bitmap exception=>"+e.toString());
        }
        return bitmap;
    }
    //bitmap保存到本地的相册中
    public static void saveBitmapToLocalThumb(Context context, Bitmap bitmap, Handler pictureHandler){
        File appDir = new File(context.getCacheDir(),"faq");
        if(!appDir.exists()){
            appDir.mkdir();
        }
        String fileName = "faq_save_picture.jpg";
        File bitmapFile = new File(appDir,fileName);
        try{
            FileOutputStream fos = new FileOutputStream(bitmapFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
            fos.flush();
            fos.close();
        }catch(Exception e){
            Log.e("PhotoShowDialog",""+e.toString());
        }
        //文件插入到图库
        try{
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    bitmapFile.getAbsolutePath(),fileName,null);
        }catch(Exception e){
            Log.e("PhotoShowDialog",e.toString());
            pictureHandler.obtainMessage(1).sendToTarget();//保存失败
        }
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(bitmapFile);
        intent.setData(uri);
        context.sendBroadcast(intent);
        pictureHandler.obtainMessage(0).sendToTarget();//保存失败
    }
}
