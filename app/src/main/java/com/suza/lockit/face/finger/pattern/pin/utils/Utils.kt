package com.suza.lockit.face.finger.pattern.pin.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import com.suza.lockit.face.finger.pattern.pin.BuildConfig
import com.suza.lockit.face.finger.pattern.pin.database.ApplicationsList
import com.suza.lockit.face.finger.pattern.pin.model.AddAppModel
import com.suza.lockit.face.finger.pattern.pin.service.Service
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults


object Utils {
    var TAG: String = Utils::class.java.simpleName
    var addApps: RealmResults<ApplicationsList>? = null
    var locked = false
    var retname: String? = null
    var retpackname: String? = null
    var rettype: String? = null


    var liveinterstetial = "ca-app-pub-3940256099942544/1033173712"
    var debuginterstetial = "ca-app-pub-3940256099942544/1033173712"

    var livebanner = "ca-app-pub-3940256099942544/6300978111"
    var debugbanner = "ca-app-pub-3940256099942544/6300978111"


    fun getIntersteitalId(): String {
        return if (BuildConfig.DEBUG) {
            debuginterstetial
        } else {
            liveinterstetial
        }
    }

    fun getBannerId(): String {
        return if (BuildConfig.DEBUG) {
            debugbanner
        } else {
            livebanner
        }
    }

    fun setDatabase(context: Context): Realm {
        Realm.init(context)
        val config = RealmConfiguration.Builder()
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .build()
        val realm = Realm.getInstance(config)
        return realm!!
    }

    fun getapps(type: String? = null): RealmResults<ApplicationsList> {
        val realm2 = Realm.getDefaultInstance()
        addApps = if (type != null)
            realm2!!.where(ApplicationsList::class.java).equalTo("type", type).findAll()
        else
            realm2!!.where(ApplicationsList::class.java).findAll()
        return addApps!!
    }

    fun updateapps(context: Context, name: String?, packageName: String?, lock: Boolean, callback: Callback? = null) {
        val addAppModel = AddAppModel(name = name, packageName = packageName, lock = lock)
        val realm2 = setDatabase(context)
        realm2.executeTransactionAsync {
            val addApps2 = it.where(ApplicationsList::class.java).equalTo("name", addAppModel.name).equalTo("packageName", addAppModel.packageName).findFirst()
            if (addApps2 != null) {
                addApps2.name = addAppModel.name
                addApps2.packageName = addAppModel.packageName
                addApps2.lock = addAppModel.lock
            } else {
                val addApps3 = it.createObject(ApplicationsList::class.java)
                addApps3.name = addAppModel.name
                addApps3.packageName = addAppModel.packageName
                addApps3.lock = addAppModel.lock
            }
        }
        callback?.callback()
    }


    fun getappslists(context: Context) {
        val packageName = context.packageName
        val packageManager = context.packageManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val mainIntent = Intent(Intent.ACTION_MAIN, null)
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            for (list in packageManager.queryIntentActivities(mainIntent, 0)) {
                Log.e(TAG, "getappslists: ${list.activityInfo.applicationInfo.packageName}")
                if (list.activityInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0 || list.activityInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP != 0) {
                    addapps(context, list.loadLabel(packageManager).toString(), list.activityInfo.applicationInfo.packageName, "System Apps")
                } else {
                    if (list.activityInfo.applicationInfo.packageName != context.packageName) {
                        addapps(context, list.loadLabel(packageManager).toString(), list.activityInfo.applicationInfo.packageName, "Third Party Apps")
                    }
                }
            }
        } else {
            for (next in packageManager.getInstalledApplications(PackageManager.GET_META_DATA)) {
                val charSequence = next.loadLabel(packageManager).toString()
                if (next.flags and 1 == 1) {
                    if (packageManager.getLaunchIntentForPackage(next.packageName) != null) {
                        addapps(context, charSequence, next.packageName, "System Apps")
                    }
                } else if (next.packageName != packageName) {
                    if (next.packageName != context.packageName) {
                        addapps(context, charSequence, next.packageName, "Third Party Apps")
                    }
                }
            }
        }
    }

    fun addapps(context: Context, name: String, packageName: String, type: String) {
        val addAppModel = AddAppModel(name = name, packageName = packageName, type = type)
        val realm2 = setDatabase(context)
        realm2.executeTransactionAsync {
            val addApps2 = it.where(ApplicationsList::class.java).equalTo("name", addAppModel.name).equalTo("packageName", addAppModel.packageName).findFirst()
            if (addApps2 != null) {
                addApps2.name = addAppModel.name
                addApps2.packageName = addAppModel.packageName
                addApps2.type = addAppModel.type
            } else {
                val addApps3 = it.createObject(ApplicationsList::class.java)
                addApps3.name = addAppModel.name
                addApps3.packageName = addAppModel.packageName
                addApps3.type = addAppModel.type
            }
        }
    }


    fun isAccessibilitySettingsOn(context: Context): Boolean {
        var accessibilityEnabled = 0
        val service = context.packageName + "/" + Service::class.java.canonicalName
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED)
            Log.v(TAG, "accessibilityEnabled = $accessibilityEnabled")
        } catch (e: Settings.SettingNotFoundException) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.message)
        }
        val mStringColonSplitter = TextUtils.SimpleStringSplitter(':')

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------")
            val settingValue: String = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityService = mStringColonSplitter.next()
                    Log.v(TAG, "-------------- > accessibilityService :: $accessibilityService $service")
                    if (accessibilityService.equals(service, ignoreCase = true)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!")
                        return true
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***")
        }

        return false
    }


    fun lockcurrentapp(context: Context, packageName: String?) {
        /*  val addAppModel = LockAppsModel(packageName = packageName, lock = false)
          val realm2 = setDatabase(context)
          realm2.executeTransactionAsync {
              val lockApps = it.where(ApplicationsList::class.java).equalTo("packageName", addAppModel.packageName).findFirst()
              if (lockApps != null) {
                  lockApps.packageName = addAppModel.packageName
                  lockApps.lock = addAppModel.lock
              } else {
                  val lockApps2 = it.createObject(ApplicationsList::class.java)
                  lockApps2.packageName = addAppModel.packageName
                  lockApps2.lock = addAppModel.lock
              }
          }*/
    }


    interface Callback {
        fun callback()
    }
}