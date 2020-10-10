package com.alat

import android.Manifest
import android.Manifest.permission_group.STORAGE
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.alat.ui.activities.account_enterprise
import com.alat.ui.activities.auth.LoginActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class Permission : Activity() {

    var pref: SharedPreferences? = null

    private var account: String? = null

    private var userid: String? = null
    private var roleID: String? = null

    var context: Context = this@Permission
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permssion)

        pref =
            this.getSharedPreferences("MyPref", 0) // 0 - for private mode


        account = pref!!.getString("account_status", null)
        userid = pref!!.getString("userid", null)

        roleID = pref!!.getString("role", null)




        requestPermission()
    }


    private fun requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS,
                        Manifest.permission.RECORD_AUDIO



                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {

                         if (roleID == "1") {
                            startActivity(Intent(this@Permission, HomePage::class.java))
                         } else if (roleID == "2") {
//                             if (account == "0") {
////                                 startActivity(Intent(this@Permission, account_enterprise::class.java))
////                             }else if (account == "1") {
                                 startActivity(Intent(this@Permission, HomePage::class.java))
                             }
                         }
                        }
                        // check for permanent denial of any permission


                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?, token: PermissionToken?) {
                        TODO("Not yet implemented")
                    }

                })
                .onSameThread()
                .check()
    }


}
