package com.alat.ui.activities

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.alat.HomePage
import com.alat.R
import com.alat.helpers.PromptPopUpView


class JoinGlobal : AppCompatActivity() {
    private var promptPopUpView: PromptPopUpView? = null
    private var edtPIN: EditText? = null
    private var btnConfirm: Button? = null
    private var btnBack: Button? = null


    var const: ConstraintLayout? = null
    var const2: ConstraintLayout? = null
    private var mProgressLayout: LinearLayout? = null

    var user: String? = null
    var pref: SharedPreferences? = null

    private var mProgress: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_id)
        edtPIN = findViewById<View>(R.id.input_pin) as EditText
        btnConfirm = findViewById<View>(R.id.btn_confirm) as Button
        btnBack = findViewById<View>(R.id.btn_back) as Button
        mProgress = ProgressDialog(this)
        mProgress!!.setMessage("Joining...")
        mProgress!!.setCancelable(true)


        pref =
            this.getSharedPreferences("MyPref", 0) // 0 - for private mode
        user = pref!!.getString("userid", null)

        btnBack!!.setOnClickListener {

                finish()
        }

        dialogue();
        promptPopUpView?.changeStatus(2, "Welcome to \n\n Global Response Group")
        Handler().postDelayed({
            //  Toast.makeText(this@JoinGlobal, "YEEES", Toast.LENGTH_LONG).show()
            val i =
                Intent(this@JoinGlobal, AlertGlobal::class.java)
            i.putExtra("alatpres_id", user)

            startActivity(i)

        }, 3000)

       // edtPIN!!.setText(response_id)
        btnConfirm!!.setOnClickListener(View.OnClickListener {
            val pin = edtPIN!!.text.toString().trim { it <= ' ' }
            when {
                TextUtils.isEmpty(pin) -> {
                    Toast.makeText(applicationContext, "Enter your PIN!", Toast.LENGTH_SHORT).show()
                    return@OnClickListener
                }
                    pin == user -> {
//                    dialogue();
//                    promptPopUpView?.changeStatus(2, "Welcome to \n\n Global Response Group")
//                    Handler().postDelayed({
//                       //  Toast.makeText(this@JoinGlobal, "YEEES", Toast.LENGTH_LONG).show()
//                        val i =
//                            Intent(this@JoinGlobal, AlertGlobal::class.java)
//                             i.putExtra("alatpres_id", user)
//
//                        startActivity(i)
//
//                    }, 2000)
                }
                else -> {

                    dialogue_error()
                    promptPopUpView?.changeStatus(1, "Oooops Wrong ID!!  Try Again")

                }
            }
        })
        btnBack!!.setOnClickListener { finish() }
    }

    override fun onBackPressed() {
        super.onBackPressed()
         finish()
    }

    fun BackAlert() {
        AlertDialog.Builder(this)
            .setMessage("Are you sure want to go back?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, id ->
                startActivity(Intent(this@JoinGlobal, HomePage::class.java))
            }
            .setNegativeButton("No", null)
            .show().withCenteredButtons()
    }

    private fun AlertDialog.withCenteredButtons() {
        val positive = getButton(AlertDialog.BUTTON_POSITIVE)
        val negative = getButton(AlertDialog.BUTTON_NEGATIVE)

        //Disable the material spacer view in case there is one
        val parent = positive.parent as? LinearLayout
        parent?.gravity = Gravity.CENTER_HORIZONTAL
        val leftSpacer = parent?.getChildAt(1)
        leftSpacer?.visibility = View.GONE

        //Force the default buttons to center
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        layoutParams.weight = 1f
        layoutParams.gravity = Gravity.CENTER

        positive.layoutParams = layoutParams
        negative.layoutParams = layoutParams
    }


    private fun dialogue() {

        promptPopUpView = PromptPopUpView(this)

        AlertDialog.Builder(this)
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


}