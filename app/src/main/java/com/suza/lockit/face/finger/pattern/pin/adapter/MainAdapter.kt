package com.suza.lockit.face.finger.pattern.pin.adapter

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.suza.lockit.face.finger.pattern.pin.R
import com.suza.lockit.face.finger.pattern.pin.databinding.AppslistBinding
import com.suza.lockit.face.finger.pattern.pin.callBacks.LockClicked
import com.suza.lockit.face.finger.pattern.pin.database.ApplicationsList
import io.realm.RealmResults

class MainAdapter(
    private var context: Context? = null,
    private var model: RealmResults<ApplicationsList>? = null,
    private var lockclicked: LockClicked? = null
) : RecyclerView.Adapter<MainAdapter.MessagesAdapterViewHolder?>() {
    var TAG: String = MainAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesAdapterViewHolder {
        return MessagesAdapterViewHolder(AppslistBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MessagesAdapterViewHolder, position: Int) {
        holder.bind(model!![position], position)
    }

    inner class MessagesAdapterViewHolder(private var itemList: AppslistBinding) : RecyclerView.ViewHolder(itemList.root) {

        fun bind(applicationsList: ApplicationsList?, position: Int) {
            val drawable: Drawable? = try {
                context?.packageManager?.getApplicationIcon(applicationsList?.packageName!!)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                null
            }
            Log.e(TAG, "lockkk: ${applicationsList!!.lock}")
            if (applicationsList.lock) {
                itemList.lock.setImageResource(R.drawable.ic_lock)
            } else {
                itemList.lock.setImageResource(R.drawable.ic_unlock)
            }
            itemList.name.text = applicationsList.name
            itemList.icon.setImageDrawable(drawable)
            itemView.setOnClickListener {
                val adapterPosition = adapterPosition
                val name: String = applicationsList.name!!
                val packageName: String = applicationsList.packageName!!
                val isLock = applicationsList.lock
                if (!isLock) {
                    if (position % 2 == 0) {
                        lockclicked?.callback(name, packageName, true,true)
                    } else {
                        lockclicked?.callback(name, packageName, true,false)
                    }
                } else {
                    if (position % 2 == 0) {
                        lockclicked?.callback(name, packageName, false,true)
                    } else {
                        lockclicked?.callback(name, packageName, false,false)
                    }
                }
            }
        }
    }


    override fun getItemCount(): Int {
        val realmResults = model
        return if (realmResults == null || realmResults.size == 0) {
            0
        } else model!!.size
    }
}