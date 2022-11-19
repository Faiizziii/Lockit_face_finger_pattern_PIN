package com.suza.lockit.face.finger.pattern.pin.callBacks

import com.google.android.gms.ads.admanager.AdManagerInterstitialAd

interface LockClicked {
    fun callback(name: String?, packageName: String?, lock: Boolean, showInterstitialAd: Boolean)
}