package com.suza.lockit.face.finger.pattern.pin.database

import io.realm.RealmObject


open class ApplicationsList(
    var lock: Boolean = false,
    var name: String? = null,
    var packageName: String? = null,
    var type: String? = null
) : RealmObject()