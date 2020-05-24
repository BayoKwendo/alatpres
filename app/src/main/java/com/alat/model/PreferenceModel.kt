package com.alat.model

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences


/**
 *
 */
class PreferenceModel(context: Context) {
    private var INTRO = "intro"
    private var TOKEN = "token"
    private var ID = "idNo"

    private var NAME = "firstname"

    private var NAME2 = "lastname"
    private var EMAIL = "email"

    private var DOB = "DoB"

    private var PHONE = "mssdn"

    private var COUNTY = "county"

    private var USERID = "userid"


    private var STATUS: String? = "status"

    private val app_prefs: SharedPreferences
    private val context: Context

    fun putIsLogin(loginorout: Boolean) {
        val edit = app_prefs.edit()
        edit.putBoolean(INTRO, loginorout)
        edit.apply()
    }

    val isLogin: Boolean
        get() = app_prefs.getBoolean(INTRO, false)

    fun putFName(loginorout: String?) {
        var edit = app_prefs.edit()
        edit.putString(NAME, loginorout)
        edit.apply()
    }

    val firstname: String?
        get() = app_prefs.getString(NAME, "")

    fun put2Name(loginorout: String) {
        var edit = app_prefs.edit()
        edit.putString(NAME2, loginorout)
        edit.apply()
    }
    val lastname: String?
        get() = app_prefs.getString(NAME2, "")


    fun putEmail(loginorout: String) {
        val edit = app_prefs.edit()
        edit.putString(EMAIL, loginorout)
        edit.apply()
    }
    val email: String?
        get() = app_prefs.getString(EMAIL, "")



    fun putDoB(loginorout: String) {
        val edit = app_prefs.edit()
        edit.putString(DOB, loginorout)
        edit.apply()
    }
    val DoB: String?
        get() = app_prefs.getString(DOB, "")


    fun putPhone(loginorout: String) {
        val edit = app_prefs.edit()
        edit.putString(PHONE, loginorout)
        edit.apply()
    }
    val mssdn: String?
        get() = app_prefs.getString(PHONE, "")

    fun putCounty(loginorout: String) {
        val edit = app_prefs.edit()
        edit.putString(COUNTY, loginorout)
        edit.apply()
    }
    val county: String?
        get() = app_prefs.getString(COUNTY, "")


    fun putUserid(loginorout: String) {
        val edit = app_prefs.edit()
        edit.putString(USERID, loginorout)
        edit.apply()
    }
    val userid: String?
        get() = app_prefs.getString(USERID, "")



    fun putToken(loginorout: String?) {
        val edit = app_prefs.edit()
         edit.remove(TOKEN)
        edit.putString(TOKEN, loginorout)
        edit.apply()
    }
    val token: String?
        get() = app_prefs.getString(TOKEN, "")


    fun putStatus(loginorout: String?) {
        var edit = app_prefs.edit()
        edit.putString(STATUS, loginorout)
        edit.apply()
    }


    val status: String?
        get() = app_prefs.getString(STATUS, "")

    fun putId(loginorout: String) {
        val edit = app_prefs.edit()
        edit.putString(ID, loginorout)
        edit.apply()
    }
    val idNo: String?
        get() = app_prefs.getString(ID, "")

    init {
        app_prefs = context.getSharedPreferences("shared",
            Context.MODE_PRIVATE)
        this.context = context
    }
}