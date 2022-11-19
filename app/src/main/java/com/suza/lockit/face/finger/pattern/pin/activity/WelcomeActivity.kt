package com.suza.lockit.face.finger.pattern.pin.activity

import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.suza.lockit.face.finger.pattern.pin.R
import com.suza.lockit.face.finger.pattern.pin.adsManager.InterstitialsAdClass
import com.suza.lockit.face.finger.pattern.pin.databinding.ActivityWelcomeBinding
import com.suza.lockit.face.finger.pattern.pin.sharedpreferences.Shared.getlocked
import com.suza.lockit.face.finger.pattern.pin.utils.Utils


class WelcomeActivity : BaseActivity<ActivityWelcomeBinding>() {
    var enableNotificationListenerAlertDialog: AlertDialog? = null
    var getlock = false
    var progressDialog: ProgressDialog? = null
    var spflock: SharedPreferences? = null

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        bindViews(R.layout.activity_welcome)
        val sharedPreferences = getSharedPreferences("locked", MODE_PRIVATE)
        binding?.customLayoutBannerTop?.shimmerViewContainer?.startShimmer()
        showBannerAd(binding?.customLayoutBannerTop)
        spflock = sharedPreferences
        getlock = getlocked(sharedPreferences)
        progressDialog = ProgressDialog(this)
        initviews()
        if (!Utils.isAccessibilitySettingsOn(this)) {
            val buildNotificationServiceAlertDialog = buildNotificationServiceAlertDialog()
            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog
            buildNotificationServiceAlertDialog.show()
        }
        binding?.go?.visibility = View.VISIBLE
    }

    private fun initviews() {
        binding?.go?.setOnClickListener { Async().execute(*arrayOfNulls<Any>(0)) }
        binding?.privacypolicy?.setOnClickListener {
            try {
                val intent = Intent("android.intent.action.MAIN")
                intent.component = ComponentName.unflattenFromString("com.android.chrome/com.android.chrome.Main")
                intent.addCategory("android.intent.category.LAUNCHER")
                intent.data = Uri.parse("https://docs.google.com/document/d/1-leDq98dyAq-OGFTC8ryUnoktorin6GdoIfUWtas2ww/edit?usp=sharing")
                startActivity(intent)
            } catch (unused: ActivityNotFoundException) {
                startActivity(Intent("android.intent.action.VIEW", Uri.parse("https://docs.google.com/document/d/1-leDq98dyAq-OGFTC8ryUnoktorin6GdoIfUWtas2ww/edit?usp=sharing")))
            }
        }
    }

    internal inner class Async : AsyncTask<Any?, Any?, Any?>() {
        public override fun onPreExecute() {
            super.onPreExecute()
            progressDialog!!.setMessage("Loading...")
            progressDialog!!.show()
        }

        // android.os.AsyncTask
        public override fun doInBackground(objArr: Array<Any?>): Any? {
            Utils.getappslists(this@WelcomeActivity)
            return null
        }

        // android.os.AsyncTask
        public override fun onPostExecute(obj: Any?) {
            super.onPostExecute(obj)
            progressDialog!!.dismiss()
            if (!getlock) {
                val intent = Intent(this@WelcomeActivity, SetLock::class.java)
                intent.putExtra("class", "splash")
                startActivity(intent)
                finish()
            } else {
                startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
                finish()
            }
        }
    }

    public override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        Log.e(TAG, "onUserLeaveHintsplash: hello")
    }

    private fun buildNotificationServiceAlertDialog(): AlertDialog {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.notification_listener_service)
        builder.setMessage(R.string.notification_listener_service_explanation)
        builder.setCancelable(false)
        builder.setPositiveButton(R.string.yes) { _, _ ->
            val intents = Intent("android.settings.ACCESSIBILITY_SETTINGS")
            startActivityForResult(intents, 0)
        }
        builder.setNegativeButton(R.string.no) { _, _ ->
            enableNotificationListenerAlertDialog!!.dismiss()
            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog()
            enableNotificationListenerAlertDialog!!.show()
        }
        return builder.create()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e(TAG, "onActivityResult: $requestCode")
    }
}
