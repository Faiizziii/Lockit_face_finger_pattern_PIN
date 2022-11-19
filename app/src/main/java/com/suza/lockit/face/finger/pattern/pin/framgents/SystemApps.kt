package com.suza.lockit.face.finger.pattern.pin.framgents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.suza.lockit.face.finger.pattern.pin.R
import com.suza.lockit.face.finger.pattern.pin.databinding.FragmentSystemAppsBinding
import com.suza.lockit.face.finger.pattern.pin.adapter.MainAdapter
import com.suza.lockit.face.finger.pattern.pin.adsManager.InterstitialsAdClass
import com.suza.lockit.face.finger.pattern.pin.callBacks.LockClicked
import com.suza.lockit.face.finger.pattern.pin.database.ApplicationsList
import com.suza.lockit.face.finger.pattern.pin.utils.Utils
import io.realm.Realm
import io.realm.RealmResults


class SystemApps : Fragment() {
    private var mainAdapter: MainAdapter? = null
    var realm: Realm? = null
    private var systemapps: RealmResults<ApplicationsList>? = null
    private lateinit var binding: FragmentSystemAppsBinding


    override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup?, bundle: Bundle?): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_system_apps, viewGroup, false)
        realm = Utils.setDatabase(requireContext())
        realm?.addChangeListener { mainAdapter?.notifyDataSetChanged() }
        systemapps = Utils.getapps("System Apps")
        setRecycler()
        return binding.root
    }

    private fun setRecycler() {
        binding.recycler.setHasFixedSize(true)
        mainAdapter = MainAdapter(activity, systemapps, object : LockClicked {
            override fun callback(name: String?, packageName: String?, lock: Boolean, showInterstitialAd: Boolean) {
                if (showInterstitialAd){
                 InterstitialsAdClass.showFacebookInterstitial(requireActivity(),object :InterstitialsAdClass.AdDismiss{
                     override fun dismissed() {
                         if (lock) {
                             Utils.updateapps(requireContext(), name, packageName, true)
                         } else {
                             Utils.updateapps(requireContext(), name, packageName, false)
                         }
                     }
                 })
                }else{
                    if (lock) {
                        Utils.updateapps(requireContext(), name, packageName, true)
                    } else {
                        Utils.updateapps(requireContext(), name, packageName, false)
                    }
                }
            }
        })
        binding.recycler.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.recycler.adapter = mainAdapter
    }

}