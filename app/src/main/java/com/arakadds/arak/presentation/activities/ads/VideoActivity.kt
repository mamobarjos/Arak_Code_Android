package com.arakadds.arak.presentation.activities.ads;

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.arakadds.arak.R


class VideoActivity : AppCompatActivity() {
    var path: String? = ""
    private var videoView:VideoView? = null
    private var progressBar:ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_video)
        videoView = findViewById<View>(R.id.VideoView) as VideoView
        progressBar = findViewById<View>(R.id.video_progressBar_id) as ProgressBar
        supportActionBar?.hide()

        val intent = intent
        if (intent != null) {
            path = intent.getStringExtra("VideoPath")
            Log.d(TAG, "the video path is: " + path)
            playVideo()
        }

    }

    private fun playVideo() {
        progressBar?.visibility = View.VISIBLE
        videoView?.setVideoURI(Uri.parse(path))
        videoView?.setOnPreparedListener{
            progressBar?.visibility = View.GONE
            videoView?.start()

        }


    }

    companion object {
        private const val TAG = "VideoActivity"
    }
}