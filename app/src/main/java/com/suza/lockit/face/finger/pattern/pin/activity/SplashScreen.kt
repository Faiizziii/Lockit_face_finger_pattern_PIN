package com.suza.lockit.face.finger.pattern.pin.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import com.suza.lockit.face.finger.pattern.pin.R
import com.suza.lockit.face.finger.pattern.pin.databinding.ActivityWelcomeBinding

class SplashScreen : BaseActivity<ActivityWelcomeBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindViews(R.layout.activity_splash)
        timer.start()
    }


    private val timer = object : CountDownTimer(2000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
        }

        override fun onFinish() {
            gotoActivity(Intent(applicationContext, WelcomeActivity::class.java), true)
        }
    }

}