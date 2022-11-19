package com.suza.lockit.face.finger.pattern.pin.activity

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.PatternLockView.Dot
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import com.andrognito.patternlockview.utils.ResourceUtils
import com.andrognito.pinlockview.IndicatorDots
import com.andrognito.pinlockview.PinLockListener
import com.suza.lockit.face.finger.pattern.pin.R
import com.suza.lockit.face.finger.pattern.pin.databinding.ActivitySetLockBinding
import com.suza.lockit.face.finger.pattern.pin.sharedpreferences.Shared.getfingerprint
import com.suza.lockit.face.finger.pattern.pin.sharedpreferences.Shared.getpatterncode
import com.suza.lockit.face.finger.pattern.pin.sharedpreferences.Shared.getpincode
import com.suza.lockit.face.finger.pattern.pin.sharedpreferences.Shared.getsecurity
import com.suza.lockit.face.finger.pattern.pin.sharedpreferences.Shared.locked
import com.suza.lockit.face.finger.pattern.pin.sharedpreferences.Shared.patterncode
import com.suza.lockit.face.finger.pattern.pin.sharedpreferences.Shared.pincode
import com.suza.lockit.face.finger.pattern.pin.sharedpreferences.Shared.securitytype
import com.suza.lockit.face.finger.pattern.pin.utils.Utils
import io.realm.Realm
import io.realm.internal.IOException
import java.security.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.security.cert.CertificateException


class SetLock : BaseActivity<ActivitySetLockBinding>() {
    var abc = false
    var apppackagename: String? = null
    var changepattern = "oldpattern"
    var changepin = "oldpin"
    private val changingpattern: PatternLockViewListener = object : PatternLockViewListener {
        override fun onStarted() {
            Log.e(TAG, "Pattern drawing started")
        }

        override fun onProgress(list: List<Dot>) {
            Log.e(TAG, "Pattern progress: " + PatternLockUtils.patternToString(binding?.patternLockView, list))
        }

        override fun onComplete(list: List<Dot>) {
            Log.e(TAG, "Pattern complete: " + PatternLockUtils.patternToString(binding?.patternLockView, list))
            if (!abc) {
                abc = true
                pattern = PatternLockUtils.patternToString(binding?.patternLockView, list)
                binding?.pinLockView?.resetPinLockView()
                binding?.indicatorDots?.removeAllViews()
                binding?.pinstatus?.text = "Draw Pattern Again"
            }
        }

        override fun onCleared() {
            Log.e(TAG, "Pattern has been cleared")
        }
    }
    private val changingpincode: PinLockListener = object : PinLockListener {
        override fun onComplete(str: String) {
            Log.e(TAG, "Pin complete: $str")
            val pin = getpincode(spfpin)
            if (changepin == "oldpin") {
                if (pin == str) {
                    changepin = "newpin"
                    binding?.pinLockView?.resetPinLockView()
                    binding?.indicatorDots?.removeAllViews()
                    binding?.pinstatus?.text = "Enter new Pin"
                } else {
                    Toast.makeText(this@SetLock, "Wrong Pin", Toast.LENGTH_SHORT).show()
                    binding?.pinLockView?.resetPinLockView()
                    binding?.indicatorDots?.removeAllViews()
                    binding?.pinstatus?.text = resources.getString(R.string.Enter_Pin_Again)
                }
            } else if (changepin == "newpin") {
                binding?.pinLockView?.resetPinLockView()
                binding?.indicatorDots?.removeAllViews()
                binding?.pinstatus?.text = resources.getString(R.string.Enter_Pin_Again)
                changepin = "cnfrmpin"
                cnfrmpin=str
            } else if (changepin == "cnfrmpin") {
                if (cnfrmpin == str) {
                    pincode(spfpin, "")
                    val str4 = getpincode(spfpin)
                    val str5 = TAG
                    Log.e(str5, "onComplete: $str4")
                    startActivity(Intent(applicationContext, Settings::class.java))
                    finish()
                } else {
                    Toast.makeText(this@SetLock, "Pin doesn't matches", Toast.LENGTH_SHORT).show()
                    binding?.pinLockView?.resetPinLockView()
                    binding?.indicatorDots?.removeAllViews()
                    binding?.pinstatus?.text = resources.getString(R.string.Enter_Pin_Again)
                }
            }
        }

        override fun onEmpty() {
            Log.e(TAG, "Pin empty")
        }

        // com.andrognito.pinlockview.PinLockListener
        override fun onPinChange(i: Int, str: String) {
            val str2 = TAG
            Log.e(str2, "Pin changed, new length $i with intermediate pin $str")
        }
    }
    private var cipher: Cipher? = null
    var cnfrmpattern: String? = null
    var cnfrmpin: String? = null
    private var cryptoObject: FingerprintManager.CryptoObject? = null
    var fingerprint = false
    private var fingerprintManager: FingerprintManager? = null
    var incomingintent: String? = null
    private var keyGenerator: KeyGenerator? = null
    private var keyStore: KeyStore? = null
    private var keyguardManager: KeyguardManager? = null
    var pattern: String? = null
    private val pattternunlock: PatternLockViewListener = object : PatternLockViewListener {
        override fun onStarted() {
            Log.e(TAG, "Pattern drawing started")
        }

        override fun onProgress(list: List<Dot>) {
            Log.e(TAG, "Pattern progress: " + PatternLockUtils.patternToString(binding?.patternLockView, list))
        }

        override fun onComplete(list: List<Dot>) {
            Log.e(TAG, "Pattern complete: " + PatternLockUtils.patternToString(binding?.patternLockView, list))
            if (PatternLockUtils.patternToString(binding?.patternLockView, list) != getpatterncode(spfpattern)) {
                Toast.makeText(this@SetLock, "Incorrect Pattern", Toast.LENGTH_SHORT).show()
                binding?.patternLockView?.clearPattern()
                binding?.patternLockView?.wrongStateColor = ResourceUtils.getColor(applicationContext, R.color.red)
            } else {
                Utils.updateapps(this@SetLock, getApplicationName(apppackagename!!), apppackagename, false)
                finish()
                finishAffinity()
                finishAndRemoveTask()
            }
        }

        override fun onCleared() {
            Log.e(TAG, "Pattern has been cleared")
        }
    }

    fun getApplicationName(packageName: String): String {
        val packageManager: PackageManager = this.packageManager
        var applicationInfo: ApplicationInfo? = null
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
        }
        return (if (applicationInfo != null) packageManager.getApplicationLabel(applicationInfo) else "???") as String
    }

    var pin: String? = null
    private val pinunlock: PinLockListener = object : PinLockListener {
        override fun onComplete(str: String) {
            Log.e(TAG, "Pin complete: $str")
            if (getpincode(spfpin) != str) {
                Toast.makeText(this@SetLock, "Incorrect Pin", Toast.LENGTH_SHORT).show()
                binding?.pinLockView?.resetPinLockView()
                binding?.indicatorDots?.removeAllViews()
            } else {
                Utils.updateapps(this@SetLock, getApplicationName(apppackagename!!), apppackagename, false, object : Utils.Callback {
                    override fun callback() {
                        finish()
                        finishAffinity()
//                        finishAndRemoveTask()
                    }
                })
            }
        }

        override fun onEmpty() {
            Log.e(TAG, "Pin empty")
        }

        override fun onPinChange(i: Int, str: String) {
            Log.e(TAG, "Pin changed, new length $i with intermediate pin $str")
        }
    }
    var realm: Realm? = null
    private val settingpattern: PatternLockViewListener = object : PatternLockViewListener {
        override fun onStarted() {
            Log.e(TAG, "Pattern drawing started")
        }

        override fun onProgress(list: List<Dot>) {
            Log.e(TAG, "Pattern progress: " + PatternLockUtils.patternToString(binding?.patternLockView, list))
        }

        override fun onComplete(list: List<Dot>) {
            Log.e(TAG, "Pattern complete: " + PatternLockUtils.patternToString(binding?.patternLockView, list))
            if (abc) {
                cnfrmpattern = PatternLockUtils.patternToString(binding?.patternLockView, list)
                if (cnfrmpattern == pattern) {
                    securitytype(spftype, "pattern")
                    patterncode(spfpattern, pattern)
                    binding?.patternLockView?.correctStateColor = ResourceUtils.getColor(applicationContext, R.color.green)
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@SetLock, "Pattern Doesn't Matches", Toast.LENGTH_SHORT).show()
                    binding?.patternLockView?.clearPattern()
                    binding?.patternLockView?.wrongStateColor = ResourceUtils.getColor(this@SetLock.applicationContext, R.color.red)
                }
            } else {
                abc = true
                pattern = PatternLockUtils.patternToString(binding?.patternLockView, list)
                binding?.patternLockView?.clearPattern()
                binding?.patternstatus?.text = "Draw Pattern Again"
            }
        }

        override fun onCleared() {
            Log.e(TAG, "Pattern has been cleared")
        }
    }
    private val settingpincode: PinLockListener = object : PinLockListener {
        override fun onComplete(str: String) {
            val str2 = TAG
            Log.e(str2, "Pin complete: $str")
            if (abc) {
                cnfrmpin = str
                if (cnfrmpin == pin) {
                    locked(spflock, true)
                    Log.e(TAG, "onComplete: $pin")
                    pincode(spfpin, pin)
                    Log.e(TAG, "onComplete: ${getpincode(spfpin)}")
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                } else {

                    Toast.makeText(this@SetLock, "Pin Does not match", Toast.LENGTH_SHORT).show()
                    binding?.pinLockView?.resetPinLockView()
                    binding?.indicatorDots?.removeAllViews()
                    Log.e(TAG, "onComplete: Wrong")
                }
            } else {
                abc = true
                pin = str
                binding?.pinLockView?.resetPinLockView()
                binding?.indicatorDots?.removeAllViews()
                binding?.pinstatus?.text = resources.getString(R.string.Enter_Pin_Again)
            }
        }

        override fun onEmpty() {
            Log.e(TAG, "Pin empty")
        }

        override fun onPinChange(i: Int, str: String) {
            Log.e(TAG, "Pin changed, new length $i with intermediate pin $str")
        }
    }
    var spffinger: SharedPreferences? = null
    var spflock: SharedPreferences? = null
    var spfpattern: SharedPreferences? = null
    var spfpin: SharedPreferences? = null
    var spftype: SharedPreferences? = null
    var type: String? = null


    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        bindViews(R.layout.activity_set_lock)
        incomingintent = intent.extras!!.getString("class")

        spflock = getSharedPreferences("locked", MODE_PRIVATE)
        spfpin = getSharedPreferences("pincode", MODE_PRIVATE)
        spftype = getSharedPreferences("security", MODE_PRIVATE)
        spfpattern = getSharedPreferences("pattern", MODE_PRIVATE)
        spffinger = getSharedPreferences("fingerprint", MODE_PRIVATE)
        type = getsecurity(spftype)
        val str = TAG
        Log.e(str, "security type: $type")
        binding?.patternLockView?.dotCount = 3
        binding?.patternLockView?.dotNormalSize = ResourceUtils.getDimensionInPx(this, com.andrognito.patternlockview.R.dimen.pattern_lock_dot_size).toInt()
        binding?.patternLockView?.dotSelectedSize = ResourceUtils.getDimensionInPx(this, com.andrognito.patternlockview.R.dimen.pattern_lock_dot_selected_size).toInt()
        binding?.patternLockView?.pathWidth = ResourceUtils.getDimensionInPx(this, com.andrognito.patternlockview.R.dimen.pattern_lock_path_width).toInt()
        binding?.patternLockView?.isAspectRatioEnabled = true
        binding?.patternLockView?.aspectRatio = PatternLockView.AspectRatio.ASPECT_RATIO_HEIGHT_BIAS
        binding?.patternLockView?.setViewMode(PatternLockView.PatternViewMode.CORRECT)
        binding?.patternLockView?.dotAnimationDuration = 150
        binding?.patternLockView?.pathEndAnimationDuration = 100
        binding?.patternLockView?.correctStateColor = ResourceUtils.getColor(this, R.color.white)
        binding?.patternLockView?.isInStealthMode = false
        binding?.patternLockView?.isTactileFeedbackEnabled = true
        binding?.patternLockView?.isInputEnabled = true
        binding?.pinLockView?.attachIndicatorDots(binding?.indicatorDots)
        binding?.pinLockView?.pinLength = 4
        binding?.pinLockView?.textColor = ContextCompat.getColor(this, R.color.black)
        binding?.indicatorDots?.indicatorType = IndicatorDots.IndicatorType.FILL_WITH_ANIMATION
//            2
        if (incomingintent == "splash") {
            binding?.topBanner?.visibility = View.VISIBLE
            binding?.customLayoutBannerTop?.shimmerViewContainer?.startShimmer()
            showBannerAd(binding?.customLayoutBannerTop)
            binding?.pinfinger?.visibility = View.GONE
            binding?.patternfinger?.visibility = View.GONE
            if (type == "pin") {
                binding?.pinstatus?.text = getString(R.string.enter_pin)
                binding?.pinlayout?.visibility = View.VISIBLE
                binding?.patternlayout?.visibility = View.INVISIBLE
                binding?.pinLockView?.setPinLockListener(settingpincode)
                return
            }
            binding?.patternstatus?.text = "Draw Pattern"
            binding?.pinlayout?.visibility = View.INVISIBLE
            binding?.patternlayout?.visibility = View.VISIBLE
            binding?.patternLockView?.addPatternLockListener(settingpattern)
        } else if (incomingintent == "change") {
            binding?.topBanner?.visibility = View.VISIBLE
            binding?.customLayoutBannerTop?.shimmerViewContainer?.startShimmer()
            showBannerAd(binding?.customLayoutBannerTop)
            binding?.pinfinger?.visibility = View.GONE
            binding?.patternfinger?.visibility = View.GONE
            if (type == "pin") {
                binding?.pinstatus?.text = getString(R.string.enter_Old_Pin)
                binding?.pinlayout?.visibility = View.VISIBLE
                binding?.patternlayout?.visibility = View.INVISIBLE
                binding?.pinLockView?.setPinLockListener(changingpincode)
                return
            }
            binding?.patternstatus?.text = "Draw Old Pattern"
            binding?.pinlayout?.visibility = View.INVISIBLE
            binding?.patternlayout?.visibility = View.VISIBLE
            binding?.patternLockView?.addPatternLockListener(changingpattern)
        } else if (incomingintent == NotificationCompat.CATEGORY_SERVICE) {
            apppackagename = intent.extras!!.getString("packagename")
            val str2 = TAG
            Log.e(str2, "faizanmujahid: " + apppackagename)
            fingerprint = getfingerprint(spffinger)
            val str3 = TAG
            Log.e(str3, "onCreate: " + fingerprint)
            if (type == "pin") {
                if (fingerprint) {
                    binding?.pinfinger?.visibility = View.VISIBLE
                } else {
                    binding?.pinfinger?.visibility = View.INVISIBLE
                }
                binding?.pinlayout?.visibility = View.VISIBLE
                binding?.patternlayout?.visibility = View.INVISIBLE
                binding?.pinLockView?.setPinLockListener(pinunlock)
            } else {
                if (fingerprint) {
                    binding?.patternfinger?.visibility = View.VISIBLE
                } else {
                    binding?.patternfinger?.visibility = View.INVISIBLE
                }
                binding?.pinlayout?.visibility = View.INVISIBLE
                binding?.patternlayout?.visibility = View.VISIBLE
                binding?.patternLockView?.addPatternLockListener(pattternunlock)
            }
            if (Build.VERSION.SDK_INT >= 23) {
                keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                val fingerprintManager2 = getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
                fingerprintManager = fingerprintManager2
                if (!fingerprintManager2.isHardwareDetected) {
                    binding?.pinfinger?.visibility = View.GONE
                    binding?.patternfinger?.visibility = View.GONE
                }
                ActivityCompat.checkSelfPermission(this, "android.permission.USE_FINGERPRINT")
                fingerprintManager!!.hasEnrolledFingerprints()
                if (keyguardManager!!.isKeyguardSecure) {
                    try {
                        generateKey()
                    } catch (e: FingerprintException) {
                        e.printStackTrace()
                    }
                    if (initCipher()) {
                        cryptoObject = FingerprintManager.CryptoObject(cipher!!)
                        FingerprintHandler(this).startAuth(fingerprintManager!!, cryptoObject)
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Throws(FingerprintException::class)
    private fun generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyGenerator = KeyGenerator.getInstance("AES", "AndroidKeyStore")
            keyStore!!.load(null)
            keyGenerator!!.init(KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT).setBlockModes(KeyProperties.BLOCK_MODE_CBC).setUserAuthenticationRequired(true).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7).build())
            keyGenerator!!.generateKey()
        } catch (e: IOException) {
            e.printStackTrace()
            throw FingerprintException(e)
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
            throw FingerprintException(e)
        } catch (e: KeyStoreException) {
            e.printStackTrace()
            throw FingerprintException(e)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            throw FingerprintException(e)
        } catch (e: NoSuchProviderException) {
            e.printStackTrace()
            throw FingerprintException(e)
        } catch (e: CertificateException) {
            e.printStackTrace()
            throw FingerprintException(e)
        }
    }

    inner class FingerprintException(exc: Exception?) : Exception(exc)

    private fun initCipher(): Boolean {
        return try {
            cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            try {
                keyStore!!.load(null as KeyStore.LoadStoreParameter?)
                cipher?.init(Cipher.ENCRYPT_MODE, keyStore!!.getKey(KEY_NAME, null as CharArray?) as SecretKey)
                true
            } catch (e: IOException) {
                throw RuntimeException("Failed to init Cipher", e)
            } catch (e: InvalidKeyException) {
                throw RuntimeException("Failed to init Cipher", e)
            } catch (e: KeyStoreException) {
                throw RuntimeException("Failed to init Cipher", e)
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException("Failed to init Cipher", e)
            } catch (e: UnrecoverableKeyException) {
                throw RuntimeException("Failed to init Cipher", e)
            } catch (e: CertificateException) {
                throw RuntimeException("Failed to init Cipher", e)
            }
        } catch (e2: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to get Cipher", e2)
        } catch (e2: NoSuchPaddingException) {
            throw RuntimeException("Failed to get Cipher", e2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    inner class FingerprintHandler(private val context: Context) : FingerprintManager.AuthenticationCallback() {
        private var cancellationSignal: CancellationSignal? = null
        override fun onAuthenticationError(i: Int, charSequence: CharSequence) {}
        override fun onAuthenticationHelp(i: Int, charSequence: CharSequence) {}
        fun startAuth(fingerprintManager: FingerprintManager, cryptoObject: FingerprintManager.CryptoObject?) {
            cancellationSignal = CancellationSignal()
            if (ActivityCompat.checkSelfPermission(context, "android.permission.USE_FINGERPRINT") == 0) {
                fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null)
            }
        }

        override fun onAuthenticationFailed() {
            Toast.makeText(this@SetLock.applicationContext, "Fingerprint doesnot matches", Toast.LENGTH_SHORT).show()
        }

        override fun onAuthenticationSucceeded(authenticationResult: FingerprintManager.AuthenticationResult) {
            binding?.pinfinger?.setImageResource(R.drawable.fingerr)
            binding?.patternfinger?.setImageResource(R.drawable.fingerr)
            if (fingerprint) {
                Utils.updateapps(this@SetLock, getApplicationName(apppackagename!!), apppackagename, false, object : Utils.Callback {
                    override fun callback() {
                        finish()
                        finishAffinity()
//                        finishAndRemoveTask()
                    }
                })
            }
        }
    }

    companion object {
        private const val KEY_NAME = "yourKey"
    }
}
