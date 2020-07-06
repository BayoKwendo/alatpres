package com.alat.ui.activities

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.alat.R
import com.alat.helpers.PromptPopUpView
import com.alat.ui.activities.mpesa.MPESAExpressActivity
import java.util.*

class account : AppCompatActivity() {
    private var promptPopUpView: PromptPopUpView? = null
    private var edtPIN: TextView? = null
    private var btnConfirm: Button? = null
    private var btnBack: Button? = null

    private var mProgress: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic)
        Objects.requireNonNull(supportActionBar)!!.setDisplayShowHomeEnabled(true)


        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        edtPIN = findViewById<View>(R.id.account) as TextView
        btnConfirm = findViewById<View>(R.id.btn_upgrade) as Button


        btnConfirm!!.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@account, MPESAExpressActivity::class.java))
        })
    }

    private fun dialogue() {

        promptPopUpView = PromptPopUpView(this)

        AlertDialog.Builder(this)
                .setPositiveButton("PROCESSING....") { _: DialogInterface?, _: Int ->
                    finish()
                }
                .setCancelable(false)
                .setView(promptPopUpView)
                .show()
    }

    private fun dialogue_error() {
        promptPopUpView = PromptPopUpView(this)
        AlertDialog.Builder(this)
                .setPositiveButton("Ok") { _: DialogInterface?, _: Int ->
                 }
                .setCancelable(true)
                .setView(promptPopUpView)
                .show()
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

        return true
    }


}