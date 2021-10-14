package com.alat.ui.activities.auth

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.alat.HomePage
import com.alat.R
import com.alat.helpers.Constants
import com.alat.helpers.PromptPopUpView
import com.alat.interfaces.GetNeutralizeAlerts
import com.alat.interfaces.RestPassword
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

class ForgotPassword : AppCompatActivity() {
    private var edtEmail: EditText? = null
    private var btnResetPassword: Button? = null
    private var btnBack: Button? = null
    private var promptPopUpView: PromptPopUpView? = null
    private var mProgress: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        edtEmail = findViewById<View>(R.id.input_login_email) as EditText
        btnResetPassword = findViewById<View>(R.id.btn_login) as Button
        btnBack = findViewById<View>(R.id.btn_back) as Button
        mProgress = ProgressDialog(this)
        mProgress!!.setMessage("Sending Resuest...")
        mProgress!!.setCancelable(true)
        btnResetPassword!!.setOnClickListener(View.OnClickListener {
            val email = edtEmail!!.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(applicationContext, "Enter your alatpres id!", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            } else {
                if (!isNetworkAvailable()) {
                    internet()
                    promptPopUpView?.changeStatus(1, "Connection Error\n\n Check your internet connectivity")
                }
                else {
                mProgress!!.show()
                    submti()
                }
            }
            //  Toast.makeText(this@ForgotPassword, "do something", Toast.LENGTH_SHORT).show()
        })
        btnBack!!.setOnClickListener { finish() }
    }


    private fun submti() {
        val email = edtEmail!!.text.toString().trim { it <= ' ' }
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor) //.addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
            .connectTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES) // write timeout
            .readTimeout(2, TimeUnit.MINUTES) // read timeout
            .addNetworkInterceptor(object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                    val request: Request =
                        chain.request().newBuilder() // .addHeader(Constant.Header, authToken)
                            .build()
                    return chain.proceed(request)
                }
            }).build()
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Constants.API_BASE_URL)
            .client(client) // This line is important
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val params: HashMap<String, String> = HashMap()
        params["userid"] = email

        val api: RestPassword = retrofit.create(RestPassword::class.java)
        val call: Call<ResponseBody> = api.Update(params)

        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                //Toast.makeText()

                Log.d("Call request", call.request().toString());
                Log.d("Response raw header", response.headers().toString());
                Log.d("Response raw", response.toString());
                Log.d("Response code", response.code().toString());


                if (response.isSuccessful) {
                    val remoteResponse = response.body()!!.string()
                    Log.d("test", remoteResponse)

                    if (response.code() == 200) {
                        mProgress?.dismiss()
                        dialogue()
                        promptPopUpView?.changeStatus(2, "Success!! \n\nCheck your phone")
                    }else{

                        mProgress?.dismiss()
                        dialogue_error()
                        promptPopUpView?.changeStatus(1, "alatpres id not found")
                    }

                } else {
                    mProgress?.dismiss()
                    dialogue_error()
                    promptPopUpView?.changeStatus(1, "alatpres id not found")
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                mProgress?.dismiss()
                dialogue_error()
                promptPopUpView?.changeStatus(1, "alatpres id not found")
            }
        })
    }

    private fun dialogue() {

        promptPopUpView = PromptPopUpView(this)

        AlertDialog.Builder(this)
            .setPositiveButton("Exit") { _: DialogInterface?, _: Int ->
                //     finish()

                startActivity(Intent(this@ForgotPassword, LoginActivity::class.java))


            }

            .setCancelable(false)
            .setView(promptPopUpView)
            .show().withCenteredButtons()
    }
    private fun isNetworkAvailable(): Boolean {
        // Using ConnectivityManager to check for Network Connection
        val connectivityManager = (this
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
        val activeNetworkInfo = connectivityManager
            .activeNetworkInfo
        return activeNetworkInfo != null
    }
    private fun internet() {
        promptPopUpView = PromptPopUpView(this)

        AlertDialog.Builder(this)

            .setPositiveButton(
                "Retry"
            ) { dialog, _ -> dialog.dismiss()
                recreate()
            }

            .setNegativeButton(
                "Cancel"
            ) { dialog, _ ->
                dialog.dismiss()
            }

            .setCancelable(false)
            .setView(promptPopUpView)
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

    private fun dialogue_error() {

        promptPopUpView = PromptPopUpView(this)

        AlertDialog.Builder(this)
        AlertDialog.Builder(this)

            .setPositiveButton(
                "Ok"
            ) { dialog, _ ->
                dialog.dismiss()
            }


            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }


}