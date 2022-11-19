package com.suza.lockit.face.finger.pattern.pin.model

data class AddAppModel (
    var lock: Boolean = false,
    var name: String? = null,
    var packageName: String? = null,
    var type: String? = null
)