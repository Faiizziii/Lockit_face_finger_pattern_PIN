package com.suza.lockit.face.finger.pattern.pin

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.suza.lockit.face.finger.pattern.pin.adsManager.InterstitialsAdClass
import com.suza.lockit.face.finger.pattern.pin.utils.Utils

class AppController : Application() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) {}
        InterstitialsAdClass.loadFacebookInterstitial(this)
    }
}