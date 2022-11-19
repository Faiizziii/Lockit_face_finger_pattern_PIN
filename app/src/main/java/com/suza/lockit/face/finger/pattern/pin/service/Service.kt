package com.suza.lockit.face.finger.pattern.pin.service

import android.accessibilityservice.AccessibilityService
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.core.app.NotificationCompat
import com.suza.lockit.face.finger.pattern.pin.activity.SetLock
import com.suza.lockit.face.finger.pattern.pin.database.ApplicationsList
import com.suza.lockit.face.finger.pattern.pin.sharedpreferences.Shared
import com.suza.lockit.face.finger.pattern.pin.utils.Utils
import io.realm.RealmResults


class Service : AccessibilityService() {
    var TAG = Service::class.java.simpleName
    var added: String? = null
    var appname: String? = null
    var apppackagename: String? = null
    var check = false
    var open = false

    //    private var realm: Realm? = null
    var spfadded: SharedPreferences? = null
    override fun onAccessibilityEvent(accessibilityEvent: AccessibilityEvent?) {
        var appsList: RealmResults<ApplicationsList>?
        val sharedPreferences = getSharedPreferences("packageName", MODE_PRIVATE)
        val realm = Utils.setDatabase(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_DISAPPEARED == accessibilityEvent?.eventType) {
                try {
                    apppackagename = accessibilityEvent.packageName.toString()
                    Log.e(TAG, "+++packagename: $apppackagename")
                } catch (e: Exception) {
                    Log.e(TAG, "++exception: $e")
                }
                realm.executeTransactionAsync {
                    val lockApps2 = it.where(ApplicationsList::class.java).equalTo("packageName", apppackagename).findFirst()
                    Log.e(TAG, "onAccessibilityEvent: ${lockApps2?.lock}")
                    Log.e(TAG, "onAccessibilityEvent: ${lockApps2?.packageName}")
                    Log.e(TAG, "onAccessibilityEvent: ${lockApps2?.name}")
                    if (apppackagename.equals(lockApps2?.packageName) && lockApps2?.lock!!) {
                        Shared.addPackageName(sharedPreferences, lockApps2.packageName)
                        Log.e(TAG, "::*_*::onAccessibilityEvent: ${Shared.getPackageName(sharedPreferences)}")
                        val intent = Intent(applicationContext, SetLock::class.java)
                        intent.putExtra("class", NotificationCompat.CATEGORY_SERVICE)
                        intent.putExtra("packagename", apppackagename)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        Log.e(TAG, "mai chal rha : hahahahahahahah")
                    }

                    appsList = Utils.getapps()

                    val getPackageName = Shared.getPackageName(sharedPreferences)
                    if (getPackageName != null) {
                        for (i in 0 until appsList?.size!!) {
                            if (apppackagename != appsList!![i]?.packageName) {
                                Log.e(TAG, "::*_*::onAccessibilityEvent1: $apppackagename")
                                Log.e(TAG, "::*_*::onAccessibilityEvent2: ${appsList!![i]?.packageName}")
                                Utils.updateapps(this, getApplicationName(getPackageName), getPackageName, true)
                            }
                        }
                    }
                }
            }

        } else {
            if (AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_DISAPPEARED == accessibilityEvent?.eventType) {
                try {
                    apppackagename = accessibilityEvent.packageName.toString()
                    Log.e(TAG, "+++packagename: $apppackagename")
                } catch (e: Exception) {
                    Log.e(TAG, "++exception: $e")
                }
                realm.executeTransactionAsync {
                    val lockApps2 = it.where(ApplicationsList::class.java).equalTo("packageName", apppackagename).findFirst()
                    Log.e(TAG, "onAccessibilityEvent: ${lockApps2?.lock}")
                    Log.e(TAG, "onAccessibilityEvent: ${lockApps2?.packageName}")
                    Log.e(TAG, "onAccessibilityEvent: ${lockApps2?.name}")
                    if (apppackagename.equals(lockApps2?.packageName) && lockApps2?.lock!!) {
                        Shared.addPackageName(sharedPreferences, lockApps2.packageName)
                        Log.e(TAG, "::*_*::onAccessibilityEvent: ${Shared.getPackageName(sharedPreferences)}")
                        val intent = Intent(applicationContext, SetLock::class.java)
                        intent.putExtra("class", NotificationCompat.CATEGORY_SERVICE)
                        intent.putExtra("packagename", apppackagename)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        Log.e(TAG, "mai chal rha : hahahahahahahah")
                    }

                    appsList = Utils.getapps()

                    val getPackageName = Shared.getPackageName(sharedPreferences)
                    if (getPackageName != null) {
                        for (i in 0 until appsList?.size!!) {
                            if (apppackagename != appsList!![i]?.packageName) {
                                Log.e(TAG, "::*_*::onAccessibilityEvent1: $apppackagename")
                                Log.e(TAG, "::*_*::onAccessibilityEvent2: ${appsList!![i]?.packageName}")
                                Utils.updateapps(this, getApplicationName(getPackageName), getPackageName, true)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onInterrupt() {}

    public override fun onServiceConnected() {
        super.onServiceConnected()
        Log.e(TAG, "onServiceConnected: ")
    }

/*    override fun onAccessibilityEvent(accessibilityEvent: AccessibilityEvent) {
        val sharedPreferences = getSharedPreferences("added", MODE_PRIVATE)
        spfadded = sharedPreferences
        added = getstatus(sharedPreferences)
        Log.e(TAG, "haram k pilly: " + added)
        if (AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_DISAPPEARED == accessibilityEvent.eventType) {
            try {
                apppackagename = accessibilityEvent.packageName.toString()
                Log.e(TAG, "+++packagename: $apppackagename")
            } catch (e: Exception) {
                Log.e(TAG, "++exception: $e")
            }
            val realm = Utils.setDatabase(this)
            realm.executeTransactionAsync {
                val lockApps2 = it.where(ApplicationsList::class.java).equalTo("packageName", apppackagename).findFirst()
                Log.e(TAG, "onAccessibilityEvent: ${lockApps2?.lock}")
                Log.e(TAG, "onAccessibilityEvent: ${lockApps2?.packageName}")
                Log.e(TAG, "onAccessibilityEvent: ${lockApps2?.name}")
                if (apppackagename.equals(lockApps2?.packageName)){
                    val intent = Intent(applicationContext, SetLock::class.java)
                    intent.putExtra("class", NotificationCompat.CATEGORY_SERVICE)
                    intent.putExtra("packagename", apppackagename)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    Log.e(TAG, "mai chal rha : hahahahahahahah")
                }

            }
            val realm = Utils.setDatabase(this)
             val lockApps2 = realm.where(LockApps::class.java).equalTo("packageName", apppackagename).findFirst()
             if (lockApps2 != null) {
                 Log.e(TAG, "NULL CHECKING: no")
                 val packageName = packageName
                 Log.e(TAG, "onAccessibilityEvent: $packageName")
                 if (lockApps2.packageName != null) {
                     appname = lockApps2.packageName
                 }
                 var isLock: Boolean = false
                 if (lockApps2.lock != null) {
                     isLock = lockApps2.lock!!
                 }
                 if (lockApps2.open != null) {
                     open = lockApps2.open!!
                 }
                 if (apppackagename == appname) {
                     if (appname == getPackageName() && !isLock && !open) {
                         Utils.setopen(this, getPackageName(), object : Utils.Callback {
                             override fun callback() {
                                 val intent = Intent(applicationContext, SetLock::class.java)
                                 intent.putExtra("class", NotificationCompat.CATEGORY_SERVICE)
                                 intent.putExtra("packagename", apppackagename)
                                 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                 startActivity(intent)
                                 Log.e(TAG, "mai chal rha : hahahahahahahah")
                             }
                         })

                     } else if (appname != apppackagename || !isLock) {
                         Log.e(TAG, "onAccessibilityEvent: ignore it baby")
                     } else if (appname != getPackageName()) {
                         Utils.setopen(this, getPackageName(), object : Utils.Callback {
                             override fun callback() {
                                 val intent2 = Intent(applicationContext, SetLock::class.java)
                                 intent2.putExtra("class", NotificationCompat.CATEGORY_SERVICE)
                                 intent2.putExtra("packagename", apppackagename)
                                 intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                 startActivity(intent2)
                             }
                         })
                     }
                     Log.e(TAG, "annideya: hello")
                 }
                 Log.e(TAG, "gandu dalli k bachy: ${apppackagename}${appname}")
             }
             if (added == "added") {
                 Utils.setclose(this, packageName)
                 Utils.lockcurrentapp(this, packageName)
                 for (i in 0 until Utils.lockapps.size) {
                     val lockedApps: String = Utils.lockapps[i]
                     if (lockedApps == packageName) {
                         Log.e(TAG, "kaminy: yes")
                     } else {
                         Utils.addlockedApps(this, lockedApps)
                         Log.e(TAG, "kaminy: no")
                     }
                 }
                 Log.e(TAG, "gandu: yes")
             } else {
                 Log.e(TAG, "gandu: no")
             }
             Log.e(TAG, "NULL CHECKING: yes")
        }
    }*/


    fun getApplicationName(packageName: String): String {
        val packageManager: PackageManager = this.packageManager
        var applicationInfo: ApplicationInfo? = null
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
        }
        return (if (applicationInfo != null) packageManager.getApplicationLabel(applicationInfo) else "???") as String
    }
}