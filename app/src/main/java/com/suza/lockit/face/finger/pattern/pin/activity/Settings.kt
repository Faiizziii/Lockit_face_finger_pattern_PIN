package com.suza.lockit.face.finger.pattern.pin.activity

import android.app.Dialog
import android.app.KeyguardManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.hardware.fingerprint.FingerprintManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import androidx.core.app.ActivityCompat
import com.suza.lockit.face.finger.pattern.pin.BuildConfig
import com.suza.lockit.face.finger.pattern.pin.R
import com.suza.lockit.face.finger.pattern.pin.databinding.ActivitySettingsBinding
import com.suza.lockit.face.finger.pattern.pin.databinding.SlctlockBinding
import com.suza.lockit.face.finger.pattern.pin.sharedpreferences.Shared.fingerprint
import com.suza.lockit.face.finger.pattern.pin.sharedpreferences.Shared.getfingerprint
import com.suza.lockit.face.finger.pattern.pin.sharedpreferences.Shared.getpatterncode
import com.suza.lockit.face.finger.pattern.pin.sharedpreferences.Shared.getsecurity
import com.suza.lockit.face.finger.pattern.pin.sharedpreferences.Shared.securitytype


class Settings : BaseActivity<ActivitySettingsBinding>() {
    var dialog: Dialog? = null
    private var fingerprintManager: FingerprintManager? = null
    var fingerstate = false
    private var keyguardManager: KeyguardManager? = null
    var pattern: String? = null
    var spf: SharedPreferences? = null
    var spfpattern: SharedPreferences? = null
    var spftype: SharedPreferences? = null
    var type: String? = null

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        bindViews(R.layout.activity_settings)
        binding?.customLayoutBannerTop?.shimmerViewContainer?.startShimmer()
        showBannerAd(binding?.customLayoutBannerTop)
        spf = getSharedPreferences("fingerprint", MODE_PRIVATE)
        spftype = getSharedPreferences("security", MODE_PRIVATE)
        spfpattern = getSharedPreferences("pattern", MODE_PRIVATE)
        type = getsecurity(spftype)
        pattern = getpatterncode(spfpattern)
        fingerstate = getfingerprint(spf)
        val str = TAG
        Log.e(str, "onCheckedChanged: $fingerstate")
        initviews()
        binding?.switchfinger?.isChecked = fingerstate
        if (Build.VERSION.SDK_INT >= 23) {
            keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            val fingerprintManager2 = getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
            fingerprintManager = fingerprintManager2
            if (!fingerprintManager2.isHardwareDetected) {
                binding?.fingerprintlayout?.visibility = View.GONE
            }
            ActivityCompat.checkSelfPermission(this, "android.permission.USE_FINGERPRINT")
            fingerprintManager!!.hasEnrolledFingerprints()
            if (keyguardManager!!.isKeyguardSecure) {
                binding?.fingerprintlayout?.visibility = View.VISIBLE
            }
        }
    }

    private fun initviews() {
        if (type == "pin") {
            binding?.changetxt?.text = "Change Pin"
            binding?.sectype?.text = "Pin"
        } else {
            binding?.changetxt?.text = "Change Pattern"
            binding?.sectype?.text = "Pattern"
        }
        onclick()
    }

    private fun onclick() {
        binding?.change?.setOnClickListener {
            val intent = Intent(applicationContext, SetLock::class.java)
            intent.putExtra("class", "change")
            startActivity(intent)
            finish()
        }
        binding?.back?.setOnClickListener {
            onBackPressed()
        }
        binding?.switchfinger?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, z ->

            if (!z) {
                Log.e(TAG, "onCheckedChanged: nhi hai")
                fingerprint(spf, false)
                val settings = this@Settings
                settings.fingerstate = getfingerprint(settings.spf)
                val str = TAG
                Log.e(str, "onCheckedChanged: " + fingerstate)
                return@OnCheckedChangeListener
            }
            fingerprint(spf, true)
            Log.e(TAG, "onCheckedChanged: hai")
            val settings2 = this@Settings
            settings2.fingerstate = getfingerprint(settings2.spf)
            val str2 = TAG
            Log.e(str2, "onCheckedChanged: " + fingerstate)
        })
        binding?.linear1?.setOnClickListener {
            SelectLock(this)
        }
        binding?.share?.setOnClickListener {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))
                var shareMessage = "\nLet me recommend you this application\n\n"
                shareMessage = """
                    ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                    
                    
                    """.trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: Exception) {
                //e.toString();
            }
        }
        binding?.moreapps?.setOnClickListener {
            val str = "https://play.google.com/store/apps/developer?id=Niklos+Studio+Apps"
            packageName
            try {
                gotoActivity(Intent("android.intent.action.VIEW", Uri.parse(str)))
            } catch (unused2: ActivityNotFoundException) {
                gotoActivity(Intent("android.intent.action.VIEW", Uri.parse(str)))
            }
        }
    }

    fun SelectLock(context: Context?) {
        val lockBinding = SlctlockBinding.inflate(layoutInflater)
        dialog = Dialog(context!!)
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(0))
        dialog?.setContentView(lockBinding.root)
        dialog!!.setCancelable(true)
        dialog!!.show()
        type = getsecurity(spftype)
        if (type == "pin") {
            lockBinding.pinradio.isChecked = true
            securitytype(spftype, "pin")
        } else {
            securitytype(spftype, "pattern")
            lockBinding.patternradio.isChecked = true
        }
        lockBinding.radiogroup.setOnCheckedChangeListener { radioGroup, i ->

            val str = TAG
            Log.e(str, "+++: $i")
            if (i == R.id.patternradio) {
                Log.e(TAG, "onCheckedChanged: pattern")
                if (pattern == "null") {
                    securitytype(spftype, "pattern")
                    val intent = Intent(this@Settings.applicationContext, SetLock::class.java)
                    intent.putExtra("class", "splash")
                    startActivity(intent)
                } else {
                    securitytype(spftype, "pattern")
                }
                binding?.changetxt?.text = "Change Pattern"
                binding?.sectype?.text = "Pattern"
                dialog!!.dismiss()
            } else if (i == R.id.pinradio) {
                securitytype(spftype, "pin")
                Log.e(TAG, "onCheckedChanged: pin")
                binding?.changetxt?.text = "Change Pin"
                binding?.sectype?.text = "Pin"
                dialog!!.dismiss()
            }
        }
    }
}


