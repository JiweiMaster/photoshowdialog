package com.jiweimaster.photoshowdialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by 18099 on 2019/1/17.
 */

public class PhotoViewPagerAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<String> imageUrlStrs;
    public PhotoViewPagerAdapter(Context context, ArrayList<String> imageUrlStrs ){
        this.context = context;
        this.imageUrlStrs = imageUrlStrs;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_photo_viewpager,
                null,false);
        final SubsamplingScaleImageView ScaleImageView = view.findViewById(R.id.scaleImageView);
        final ImageView gifImageView = view.findViewById(R.id.gifImageView);
        if(imageUrlStrs.get(position).contains(".gif")){
            ScaleImageView.setVisibility(View.GONE);
            gifImageView.setVisibility(View.VISIBLE);
            Glide.with(context).load(imageUrlStrs.get(position)).into(gifImageView);
        }else{
            gifImageView.setVisibility(View.GONE);
            ScaleImageView.setVisibility(View.VISIBLE);
            Observable.create(new ObservableOnSubscribe<File>() {
                @Override
                public void subscribe(ObservableEmitter<File> emitter) throws Exception {
                    RequestOptions options = new RequestOptions();
                    File pictureFile = Glide.with(context).load(imageUrlStrs.get(position)).apply(options)
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                    emitter.onNext(pictureFile);
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<File>() {
                        @Override
                        public void accept(File file) throws Exception {
                            ScaleImageView.setImage(ImageSource.uri(file.getAbsolutePath()));
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.e("PhotoViewPagerAdapter",""+throwable.getMessage());
                        }
                    });
        }

        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return imageUrlStrs.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }


    /**
     * 确定当前view的key
     * @param view
     * @param object
     * @return
     */
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    /**
     *界面滑动的时候，移除消失的view
     * @param object
     * @return
     */
    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
