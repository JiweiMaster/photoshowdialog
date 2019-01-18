package com.jiweimaster.photoshow

import android.graphics.Picture
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.jiweimaster.photoshowdialog.PhotoShowDialog
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val images = ArrayList<String>()
        images.add("http://106.14.14.212/media/QAImg/825/1527670188090.png")
        images.add("http://106.14.14.212/media/QAImg/825/1527670188090_B5ubaDL.png")
        images.add("http://106.14.14.212/media/QAImg/825/1527670188090_kwGsPS7.png")
        images.add("http://106.14.14.212/media/QAImg/825/1527670188090_0Hwwoyk.png")
        images.add("http://106.14.14.212/media/QAImg/825/1527670188090_B5ubaDL.png")
        images.add("http://106.14.14.212/media/QAImg/406/image001.gif")
        val dialog = PhotoShowDialog(this,images,
                "http://106.14.14.212/media/QAImg/825/1527670188090_B5ubaDL.png")
        dialog.show()


    }
}
