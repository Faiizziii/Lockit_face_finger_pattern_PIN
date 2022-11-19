package com.suza.lockit.face.finger.pattern.pin.sharedpreferences

import android.content.SharedPreferences

object Shared {
    fun fingerprint(sharedPreferences: SharedPreferences?, z: Boolean) {
        val edit = sharedPreferences!!.edit()
        edit.putBoolean("fingerprint", z)
        edit.apply()
    }

    fun getfingerprint(sharedPreferences: SharedPreferences?): Boolean {
        return sharedPreferences!!.getBoolean("fingerprint", false)
    }

    fun locked(sharedPreferences: SharedPreferences?, z: Boolean) {
        val edit = sharedPreferences!!.edit()
        edit.putBoolean("locked", z)
        edit.apply()
    }

    fun getlocked(sharedPreferences: SharedPreferences?): Boolean {
        return sharedPreferences!!.getBoolean("locked", false)
    }

    fun pincode(sharedPreferences: SharedPreferences?, str: String?) {
        val edit = sharedPreferences!!.edit()
        edit.putString("pincode", str)
        edit.apply()
    }

    fun getpincode(sharedPreferences: SharedPreferences?): String? {
        return sharedPreferences?.getString("pincode", "null")
    }

    fun patterncode(sharedPreferences: SharedPreferences?, str: String?) {
        val edit = sharedPreferences!!.edit()
        edit.putString("pattern", str)
        edit.apply()
    }

    fun getpatterncode(sharedPreferences: SharedPreferences?): String? {
        return sharedPreferences!!.getString("pattern", "null")
    }

    fun securitytype(sharedPreferences: SharedPreferences?, str: String?) {
        val edit = sharedPreferences!!.edit()
        edit.putString("security", str)
        edit.apply()
    }

    fun getsecurity(sharedPreferences: SharedPreferences?): String? {
        return sharedPreferences!!.getString("security", "pin")
    }

    fun added(sharedPreferences: SharedPreferences?, str: String?) {
        val edit = sharedPreferences!!.edit()
        edit.putString("added", str)
        edit.apply()
    }

    fun getstatus(sharedPreferences: SharedPreferences?): String? {
        return sharedPreferences?.getString("packageName", "null ")
    }

    fun addPackageName(sharedPreferences: SharedPreferences?, str: String?) {
        val edit = sharedPreferences!!.edit()
        edit.putString("packageName", str)
        edit.apply()
    }

    fun getPackageName(sharedPreferences: SharedPreferences?): String? {
        return sharedPreferences?.getString("packageName", null)
    }
}