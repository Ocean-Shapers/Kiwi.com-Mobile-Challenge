package com.oceanshapers.kiwi.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.oceanshapers.kiwi.R
import com.oceanshapers.kiwi.fragment.OptionsFragment
import kotlinx.android.synthetic.main.fragment_options.*

class MainActivity : AppCompatActivity() {
    var background_music = MediaPlayer()
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        background_music = MediaPlayer.create(
            this,
            resources.getIdentifier("game_play", "raw", packageName)
        )
        background_music.start()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.activity_main, OptionsFragment())
        fragmentTransaction.commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        background_music.stop()
    }
}

