package com.example.facebooklocation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initView()

        Handler().postDelayed({

            startActivity(Intent(this,MainActivity::class.java))
            finish()
        },3000)
    }

    private fun initView() {



        var animation= AnimationUtils.loadAnimation(this,R.anim.zoom_in_out)
        animation.setInterpolator ( LinearInterpolator() )
        iv_icons.startAnimation(animation)
    }
}