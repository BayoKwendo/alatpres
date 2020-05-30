package com.alat.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.alat.R
import com.alat.ui.activities.SplashActivity
import com.alat.ui.activities.auth.LoginActivity

class SplashActivity : AppCompatActivity(),
    Animation.AnimationListener {
    var animBounce: Animation? = null
    var btnStart: Button? = null
    var imgPoster: ImageView? = null
    override fun onAnimationRepeat(animation: Animation) {}
    override fun onAnimationStart(animation: Animation) {}
    @SuppressLint("WrongConstant")
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            this@SplashActivity.startActivity(
                Intent(
                    this@SplashActivity,
                    LoginActivity::class.java
                )
            )
            finish()
        }, 1500.toLong())
        Handler().postDelayed({
            imgPoster!!.visibility = 0
            imgPoster!!.startAnimation(animBounce)
        }, 0)
        splashLoaded = true
        imgPoster =
            findViewById<View>(R.id.imgLogo) as ImageView
        animBounce = AnimationUtils.loadAnimation(
            applicationContext,
            R.anim.bounce
        )
        animBounce!!.setAnimationListener(this)
    }

    override fun onAnimationEnd(animation: Animation) {
        val animation2 = animBounce
    }

    companion object {
        private var splashLoaded = false
    }
}