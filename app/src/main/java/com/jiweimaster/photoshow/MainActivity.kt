package com.jiweimaster.photoshow

import android.graphics.Bitmap
import android.graphics.Picture
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.FragmentPagerAdapter
import android.telecom.Call
import android.util.Log
import android.widget.Switch
import android.widget.Toast
import com.jiweimaster.photoshowdialog.PhotoShowDialog
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val images = ArrayList<String>()
        images.add("http://106.14.14.212/media/QAImg/1615/8C2D3ED9-7911-4087-8322-7D1A6A1FB4AB-13016-0000102F889C154D_file.jpg.jpg")
        images.add("http://106.14.14.212/media/QAImg/825/1527670188090_B5ubaDL.png")
        images.add("http://106.14.14.212/media/QAImg/825/1527670188090_kwGsPS7.png")
        images.add("http://106.14.14.212/media/QAImg/825/1527670188090_0Hwwoyk.png")
        images.add("http://106.14.14.212/media/QAImg/825/1527670188090_B5ubaDL.png")
        images.add("http://106.14.14.212/media/QAImg/406/image001.gif")
        val dialog = PhotoShowDialog(this,images,
                "http://106.14.14.212/media/QAImg/825/1527670188090_B5ubaDL.png")
        dialog.show()
        dialog.setOnIconItemClick(object: PhotoShowDialog.IconItemListener {
            override fun deleteImageView() {
            }

            override fun onDownloadItemClick(position: Int) {
                Observable.create(object: ObservableOnSubscribe<Bitmap>{
                    override fun subscribe(emitter: ObservableEmitter<Bitmap>) {
                        Log.e(TAG,"当前显示的图片的地址=>"+images[position])
                        val saveBitmap = PhotoShowDialog.returnBitmap(images[position])
                        emitter.onNext(saveBitmap);

                    }
                }).subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : Consumer<Bitmap>{
                            override fun accept(t: Bitmap?) {
                                if(t == null){
                                    Log.e(TAG,"图片为空")
                                }
                                PhotoShowDialog.saveBitmapToLocalThumb(this@MainActivity,t,
                                        object : Handler(){
                                            override fun handleMessage(msg: Message?) {
                                                super.handleMessage(msg)
                                                when(msg?.what){
                                                    0 -> Log.e(TAG,"保存成功")
                                                    1 -> Log.e(TAG,"保存失败")
                                                }
                                            }
                                        })
                            }
                        })

            }

        } )
    }



}
