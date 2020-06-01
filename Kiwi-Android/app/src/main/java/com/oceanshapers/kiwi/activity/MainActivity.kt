package com.oceanshapers.kiwi.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.oceanshapers.kiwi.R
import com.oceanshapers.kiwi.fragment.OptionsFragment

class MainActivity : AppCompatActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
       fragmentTransaction.replace(R.id.activity_main,OptionsFragment())
        fragmentTransaction.commit()

    }
}

