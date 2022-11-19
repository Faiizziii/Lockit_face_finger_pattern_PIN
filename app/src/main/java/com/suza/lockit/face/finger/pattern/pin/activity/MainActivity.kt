package com.suza.lockit.face.finger.pattern.pin.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.suza.lockit.face.finger.pattern.pin.R
import com.suza.lockit.face.finger.pattern.pin.databinding.ActivityMainBinding
import com.suza.lockit.face.finger.pattern.pin.adapter.PagerAdapter
import com.suza.lockit.face.finger.pattern.pin.adsManager.InterstitialsAdClass
import com.suza.lockit.face.finger.pattern.pin.framgents.SystemApps
import com.suza.lockit.face.finger.pattern.pin.framgents.ThirdPartyApps
import com.suza.lockit.face.finger.pattern.pin.sharedpreferences.Shared.added
import com.suza.lockit.face.finger.pattern.pin.sharedpreferences.Shared.getpincode
import com.suza.lockit.face.finger.pattern.pin.sharedpreferences.Shared.getstatus
import com.suza.lockit.face.finger.pattern.pin.utils.Utils
import io.realm.Realm


class MainActivity : BaseActivity<ActivityMainBinding>() {
    private var adapter: PagerAdapter? = null
    var pin: String? = null
    var realm: Realm? = null
    var spfopen: SharedPreferences? = null
    var spfpin: SharedPreferences? = null
    public override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        Log.e(TAG, "onUserLeaveHintmainactivity: hello")
    }

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        bindViews(R.layout.activity_main)
        binding?.customLayoutBannerTop?.shimmerViewContainer?.startShimmer()
        showBannerAd(binding?.customLayoutBannerTop)
        spfpin = getSharedPreferences("pincode", MODE_PRIVATE)
        spfopen = getSharedPreferences("added", MODE_PRIVATE)
        added(spfopen, "added")
        Log.e(TAG, "haram k pilly Main activity: ${getstatus(spfopen)}")
        pin = getpincode(spfpin)
        Log.e(TAG, "onCreate: $pin")
        binding?.setting?.setOnClickListener {
            InterstitialsAdClass.showFacebookInterstitial(this@MainActivity, object : InterstitialsAdClass.AdDismiss {
                override fun dismissed() {
                    startActivity(Intent(this@MainActivity.applicationContext, Settings::class.java))
                }
            })
        }
        adapter = PagerAdapter(supportFragmentManager, this)
        adapter?.addFragment(SystemApps(), "System Apps")
        adapter?.addFragment(ThirdPartyApps(), "Third Party Apps")
        binding?.simpleViewPager?.adapter = adapter
        binding?.simpleTabLayout?.setupWithViewPager(binding?.simpleViewPager)
        highLightCurrentTab(0)
        binding?.simpleViewPager?.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrollStateChanged(i: Int) {}

            override fun onPageScrolled(i: Int, f: Float, i2: Int) {}

            override fun onPageSelected(i: Int) {
                highLightCurrentTab(i)
            }
        })
    }

    fun highLightCurrentTab(i: Int) {
        for (i2 in 0 until binding?.simpleTabLayout?.tabCount!!) {
            val tabAt = binding?.simpleTabLayout?.getTabAt(i2)
            tabAt!!.customView = null
            tabAt.customView = adapter!!.getTabView(i2)
        }
        val tabAt2 = binding?.simpleTabLayout?.getTabAt(i)
        tabAt2!!.customView = null
        tabAt2.customView = adapter!!.getSelectedTabView(i)
    }
}