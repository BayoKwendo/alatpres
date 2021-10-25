package com.alat.model

class clientModel {

    var name: String? = null

    var id: Long? = null

    var isSelected: Boolean? = null

    var `object`: Any? = null

    @JvmName("getObject1")
    fun getObject(): Any? {
        return `object`
    }

    var phones: String? = null

}