package com.suza.lockit.face.finger.pattern.pin.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.suza.lockit.face.finger.pattern.pin.R


class PagerAdapter(fragmentManager: FragmentManager?, private var context: Context) : FragmentStatePagerAdapter(fragmentManager!!) {
    private val mFragmentList: MutableList<Fragment> = ArrayList()
    private val mFragmentTitleList: MutableList<String> = ArrayList()
    override fun getPageTitle(i: Int): CharSequence? {
        return null
    }


    override fun getItem(i: Int): Fragment {
        return mFragmentList[i]
    }

    fun addFragment(fragment: Fragment, str: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(str)
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun getTabView(i: Int): View {
        val inflate: View = LayoutInflater.from(context).inflate(R.layout.custom_tab, null as ViewGroup?)
        (inflate.findViewById<View>(R.id.tabTextView) as TextView).text = mFragmentTitleList[i]
        val imageView = inflate.findViewById<View>(R.id.tabImageView) as ImageView
        Log.e("pagerAdapter", "getTabView: clicked")
        return inflate
    }

    fun getSelectedTabView(i: Int): View {
        val inflate: View = LayoutInflater.from(context).inflate(R.layout.custom_tab, null as ViewGroup?)
        (inflate.findViewById(R.id.tabTextView) as TextView).text = mFragmentTitleList[i]
        val imageView: ImageView = inflate.findViewById(R.id.tabImageView) as ImageView
        Log.e("pagerAdapter", "getSelectedTabView: clicked")
        return inflate
    }
}
