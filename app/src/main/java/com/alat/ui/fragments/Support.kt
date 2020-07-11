package com.alat.ui.fragments

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.alat.HomePage
import com.alat.R
import com.alat.helpers.Constants
import com.alat.helpers.PromptPopUpView
import com.alat.interfaces.AddSupportFeed
import com.alat.interfaces.AlertShare
import com.alat.ui.activities.level_1
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

class Support : Fragment() {

    private var toolbar : Toolbar? = null

    var emailuser: String? = null

    private var fullname: String? = null
    private var mssidn: String? = null
    private var userid: String? = null

    private var mfeedback: EditText? = null
    private var feedback: String? = null

    private var mbutton: Button? = null

    var pref: SharedPreferences? = null
    private var mProgressS: ProgressDialog? = null

    private var promptPopUpView: PromptPopUpView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.activity_support, container, false)

        mfeedback = view.findViewById<View>(R.id.notes) as EditText

        mbutton = view.findViewById<View>(R.id.buttonLogin) as Button



        pref =
            activity!!.getSharedPreferences("MyPref", 0) // 0 - for private mode

        fullname = pref!!.getString("fname", null) + "\t" + pref!!.getString("lname", null)

        mssidn = pref!!.getString("mssdn", null)

        userid = pref!!.getString("userid", null)
        emailuser = pref!!.getString("email", null)

        mProgressS = ProgressDialog(context!!)
        mProgressS!!.setMessage("Submitting....");
        mProgressS!!.setCancelable(false)
        mbutton!!.setOnClickListener {
            if (!isNetworkAvailable()) {
                internet()
                promptPopUpView?.changeStatus(1, "Connection Error\n\n Check your internet connectivity")
            }else {
                feedback = mfeedback!!.text.toString()

                if(feedback == null){
                    Toast.makeText(activity,"Support field can not be null", Toast.LENGTH_LONG).show();
                }else {
                    mProgressS!!.show()
                    submit()
                }}
        }
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //you can set the title for your toolbar here for different fragments different title
    }

    private fun isNetworkAvailable(): Boolean {
        // Using ConnectivityManager to check for Network Connection
        val connectivityManager = (
                activity!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
        val activeNetworkInfo = connectivityManager
            .activeNetworkInfo
        return activeNetworkInfo != null
    }


    private fun internet() {
        promptPopUpView = PromptPopUpView(activity)

        AlertDialog.Builder(activity!!)

            .setPositiveButton(
                "Retry"
            ) { dialog, _ -> dialog.dismiss()
                dialog.dismiss()

            }

            .setNegativeButton(
                "Cancel"
            ) { dialog, _ ->
                dialog.dismiss()
            }

            .setCancelable(false)
            .setView(promptPopUpView)
            .show().withCenteredButton()
    }

    private fun submit() {
        feedback = mfeedback!!.text.toString()

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

        val params: java.util.HashMap<String, String> = java.util.HashMap()

        params["userid"] = userid!!
        params["fullname"] = fullname!!
        params["mssdn"] = mssidn!!
        params["email"] = emailuser!!
        params["message"] = feedback!!
        params["type"] = "support"

        val api: AddSupportFeed = retrofit.create(AddSupportFeed::class.java)
        val call: Call<ResponseBody> = api.accept(params)

        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                //Toast.makeText()

                Log.d("Call request", call.request().toString());
                Log.d("Response raw header", response.headers().toString());
                Log.d("Response raw", response.toString());
                Log.d("Response code", response.code().toString());


                if (response.isSuccessful) {

                    if (response.body() != null) {
                        val remoteResponse = response.body()!!.string()

                        Log.d("test", remoteResponse)
                        parseLoginDataasassss(remoteResponse)

                        //Toast.makeText(this@ActiveAlerts,"Nothing "  + remoteResponse,Toast.LENGTH_LONG).show();

                    } else {

                        Log.i(
                            "onEmptyResponse",
                            "Returned empty response"
                        )
                    }

                } else {

                    dialogue_error();
                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                    Log.d("BAYO", response.code().toString())
                    mProgressS!!.dismiss()

                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                Log.i("onEmptyResponse", "" + t) //
            }
        })
    }

    private fun parseLoginDataasassss(remoteResponse: String) {
        try {


            val jsonObject = JSONObject(remoteResponse)
            // val jsonObject = JSONObject(jsonresponse)


            if (jsonObject.getString("status") == "true") {
                dialogue();
                promptPopUpView?.changeStatus(
                    2,
                    "Your issue is well received!!\n\n we will get back to you within 24hrs"
                )

                mProgressS!!.dismiss()

            } else {
                dialogue_error();
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                mProgressS!!.dismiss()


            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    private fun dialogue() {

        promptPopUpView = PromptPopUpView(activity)

        AlertDialog.Builder(activity!!)
            .setPositiveButton("Exit") { _: DialogInterface?, _: Int ->
                startActivity(Intent(activity, HomePage::class.java))
            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show().withCenteredButton()
    }


    private fun dialogue_error() {

        promptPopUpView = PromptPopUpView(activity!!)

        AlertDialog.Builder(activity!!)
            .setPositiveButton("Try Again") { _: DialogInterface?, _: Int ->

            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }


    private fun AlertDialog.withCenteredButton() {
        val positive = getButton(AlertDialog.BUTTON_POSITIVE)
        val negative = getButton(AlertDialog.BUTTON_NEGATIVE)

        negative.setTextColor(ContextCompat.getColor(context, R.color.error))


        positive.setBackgroundColor(ContextCompat.getColor(context, R.color.success))

        positive.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))


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




}