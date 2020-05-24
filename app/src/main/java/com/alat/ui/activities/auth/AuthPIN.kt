//package com.schalert.nfccardread.auth
//
//import android.app.ProgressDialog
//import android.content.DialogInterface
//import android.content.Intent
//import android.os.Bundle
//import android.os.Handler
//import android.text.TextUtils
//import android.view.View
//import android.widget.Button
//import android.widget.EditText
//import android.widget.Toast
//import androidx.appcompat.app.AlertDialog
//import androidx.appcompat.app.AppCompatActivity
//import com.schalert.nfccardread.NFCWrite
//import com.schalert.nfccardread.R
//import com.schalert.nfccardread.models.PreferenceHelper
//import com.alat.helpers.PromptPopUpView
//
//class AuthPIN : AppCompatActivity() {
//    private var promptPopUpView: PromptPopUpView? = null
//    private var edtPIN: EditText? = null
//    private var btnConfirm: Button? = null
//    private var btnBack: Button? = null
//    private var preferenceHelper: PreferenceHelper? = null
//
//    private var mProgress: ProgressDialog? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.confirm_pin)
//        preferenceHelper = PreferenceHelper(this)
//
//        if (preferenceHelper!!.token == null){
//            startActivity(Intent(this, LoginActivity::class.java))
//        }
//        edtPIN = findViewById<View>(R.id.input_pin) as EditText
//        btnConfirm = findViewById<View>(R.id.btn_confirm) as Button
//        btnBack = findViewById<View>(R.id.btn_back) as Button
//        mProgress = ProgressDialog(this)
//        mProgress!!.setMessage("Sending Resuest...")
//        mProgress!!.setCancelable(true)
//
//      //  Toast.makeText(this@AuthPIN, "Nothing" + preferenceHelper!!.id_number, Toast.LENGTH_LONG).show()
//
//
//
//        btnConfirm!!.setOnClickListener(View.OnClickListener {
//            val pin = edtPIN!!.text.toString().trim { it <= ' ' }
//            when {
//                TextUtils.isEmpty(pin) -> {
//                    Toast.makeText(applicationContext, "Enter your PIN!", Toast.LENGTH_SHORT).show()
//                    return@OnClickListener
//                }
//                   pin == preferenceHelper!!.id_number -> {
//                    dialogue();
//                    promptPopUpView?.changeStatus(2, "SUCCESSFUL")
//                    Handler().postDelayed({
//                        startActivity(Intent(this@AuthPIN, NFCWrite::class.java))
//                    }, 2000)
//                }
//                else -> {
//
//                    dialogue_error()
//                    promptPopUpView?.changeStatus(1, "Oooops Error!!  Try Again")
//
//                }
//            }
//        })
//        btnBack!!.setOnClickListener { finish() }
//    }
//
//    private fun dialogue() {
//
//        promptPopUpView = PromptPopUpView(this)
//
//        AlertDialog.Builder(this)
//                .setPositiveButton("PROCESSING....") { _: DialogInterface?, _: Int ->
//                    finish()
//                }
//                .setCancelable(false)
//                .setView(promptPopUpView)
//                .show()
//    }
//
//    private fun dialogue_error() {
//        promptPopUpView = PromptPopUpView(this)
//        AlertDialog.Builder(this)
//                .setPositiveButton("Ok") { _: DialogInterface?, _: Int ->
//                 }
//                .setCancelable(true)
//                .setView(promptPopUpView)
//                .show()
//    }
//
//
//}