//package com.schalert.nfccardread.auth
//
//import android.app.ProgressDialog
//import android.os.Bundle
//import android.text.TextUtils
//import android.view.View
//import android.widget.Button
//import android.widget.EditText
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.schalert.nfccardread.R
//
//class ForgotPassword : AppCompatActivity() {
//    private var edtEmail: EditText? = null
//    private var btnResetPassword: Button? = null
//    private var btnBack: Button? = null
//    private var mProgress: ProgressDialog? = null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_reset_password)
//        edtEmail = findViewById<View>(R.id.input_login_email) as EditText
//        btnResetPassword = findViewById<View>(R.id.btn_login) as Button
//        btnBack = findViewById<View>(R.id.btn_back) as Button
//        mProgress = ProgressDialog(this)
//        mProgress!!.setMessage("Sending Resuest...")
//        mProgress!!.setCancelable(true)
//        btnResetPassword!!.setOnClickListener(View.OnClickListener {
//            val email = edtEmail!!.text.toString().trim { it <= ' ' }
//            if (TextUtils.isEmpty(email)) {
//                Toast.makeText(applicationContext, "Enter your email!", Toast.LENGTH_SHORT).show()
//                return@OnClickListener
//            }
//            Toast.makeText(this@ForgotPassword, "do something", Toast.LENGTH_SHORT).show()
//        })
//        btnBack!!.setOnClickListener { finish() }
//    }
//
//
//}