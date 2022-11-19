package com.suza.lockit.face.finger.pattern.pin.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewbinding.ViewBinding
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.suza.lockit.face.finger.pattern.pin.databinding.CustomBannerBinding

abstract class BaseActivity<Binding : ViewBinding> : AppCompatActivity() {
    var binding: Binding? = null
    var TAG: String? = null
    private var getActivity: Activity? = null

    fun bindViews(layoutID: Int) {
        binding = DataBindingUtil.setContentView(this, layoutID)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivity = this
        TAG = getActivity!!::class.java.simpleName
    }


    fun gotoActivity(intent: Intent, finish: Boolean = false) {
        Log.e(TAG, "gotoActiivyt: $finish")
        startActivity(intent)
        if (finish) {
            finish()
        }
    }


    fun showBannerAd(
        bannerLayout: CustomBannerBinding?
    ) {
        if (isNetworkAvailable(this)) {
            val adRequest = AdRequest.Builder().build()
            bannerLayout?.bannerTop?.loadAd(adRequest)

            bannerLayout?.bannerTop?.adListener = object : AdListener() {
                override fun onAdClicked() {
                    // Code to be executed when the user clicks on an ad.
                }

                override fun onAdClosed() {
                    // Code to be executed when the user is about to return
                    // to the app after tapping on an ad.
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    bannerLayout?.shimmerViewContainer?.stopShimmer()
                    bannerLayout?.bannerTop?.visibility = View.GONE
                    // Code to be executed when an ad request fails.
                }

                override fun onAdImpression() {
                    // Code to be executed when an impression is recorded
                    // for an ad.
                }

                override fun onAdLoaded() {
                    bannerLayout?.shimmerViewContainer?.stopShimmer()
                    bannerLayout?.bannerTop?.visibility = View.VISIBLE
                    // Code to be executed when an ad finishes loading.
                }

                override fun onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.
                }
            }
        }
    }



    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        val connectivityManager =
            context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        // if no network is availablgoButton_ide networkInfo will be null, otherwise check if we are connected
        try {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        } catch (e: Exception) {
            Log.e("UtilsClass", "isNetworkAvailable()::::" + e.message)
        }
        return false
    }

}