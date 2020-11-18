package com.alat.ui.fragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.alat.HomePage
import com.alat.R
import com.alat.helpers.Constants
import com.alat.helpers.PromptPopUpView
import com.alat.helpers.Utils
import com.alat.interfaces.AddResponse
import com.alat.interfaces.UpdateUserRecord
import com.alat.ui.activities.auth.LoginActivity
import com.rengwuxian.materialedittext.MaterialEditText
import dmax.dialog.SpotsDialog
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


class Profile : Fragment() {

    private var toolbar: Toolbar? = null
    var fullname: TextView? = null
    var userid: TextView? = null
    var email: TextView? = null
    var county: TextView? = null
    var dob: TextView? = null
    var phone: TextView? = null
    var idNo: TextView? = null




    var clients: TextView? = null
    var mclients: TextView? = null



    var mdob: TextView? = null

    var midNo: TextView? = null

    var pref: SharedPreferences? = null


    var fname: String? = null
    var user: String? = null
    var emailuser: String? = null
    var county_name: String? = null
    var dob_user: String? = null
    var mssidn: String? = null
    var id: String? = null

    var updateFName: String? = null

    var updateLName: String? = null

    var updatePhone: String? = null

    var mclientss: String? = null

    var updateAlatpressID: String? = null

    var updateEmail: String? = null

    var alert: AlertDialog? = null

    var updateFNamee: MaterialEditText? = null

    var updateLNamee: MaterialEditText? = null

    var updatePhonee: MaterialEditText? = null

    var updateIDD: MaterialEditText? = null

    var updateAlatpressIDD: MaterialEditText? = null
    private var account: String? = null

    var updateEmaill: MaterialEditText? = null
    private var mProgress: ProgressDialog? = null
    private var promptPopUpView: PromptPopUpView? = null

    private var roleID: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.activity_profile, container, false)

        fullname = view.findViewById(R.id.textView)
        userid = view.findViewById(R.id.textView2)
        idNo = view.findViewById(R.id.textView21)
        phone = view.findViewById(R.id.textView7)
        email = view.findViewById(R.id.textView9)
        dob = view.findViewById(R.id.textView15)
        county = view.findViewById(R.id.textView11)


        clients = view.findViewById(R.id.textView27)
        mclients = view.findViewById(R.id.textView24)

        mdob = view.findViewById(R.id.textView13)
        midNo = view.findViewById(R.id.textView20)

        pref =
            activity!!.getSharedPreferences("MyPref", 0) // 0 - for private mode
        fname = pref!!.getString("fname", null) + "\t" + pref!!.getString("lname", null)
        mssidn = pref!!.getString("mssdn", null)
        user = pref!!.getString("userid", null)
        id = pref!!.getString("idNo", null)

        emailuser = pref!!.getString("email", null)
        county_name = pref!!.getString("county", null)
        dob_user = pref!!.getString("dob", null)
        roleID = pref!!.getString("role", null)
        mclientss = pref!!.getString("clients", null)

        //Toast.makeText(activity, "g" + roleID, Toast.LENGTH_LONG).show() ;

        if(roleID == "2"){
            dob!!.visibility = View.GONE
            idNo!!.visibility = View.GONE
            mdob!!.visibility = View.GONE
            midNo!!.visibility = View.GONE
        }


        if(roleID == "1"){
            clients!!.visibility = View.GONE
            mclients!!.visibility = View.GONE
        }


        setHasOptionsMenu(true);


        fullname!!.text = fname

        idNo!!.text = id

        phone!!.text = mssidn

        email!!.text = emailuser

        dob!!.text = dob_user
        clients!!.text =mclientss
        county!!.text = county_name

        userid!!.text = "AlatPres ID\t$user"

        mProgress = ProgressDialog(activity)

        mProgress!!.setMessage("Updating...");
        mProgress!!.setCancelable(true);

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        //you can set the title for your toolbar here for different fragments different title
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile, menu)

//        val item = menu.findItem(R.id.action_share)
//        val item2 = menu.findItem(R.id.join)
//        val item3 = menu.findItem(R.id.invites)
//        val item4 = menu.findItem(R.id.about)
//        val item5 = menu.findItem(R.id.logout)
//        val item6 = menu.findItem(R.id.action_invite)
//        val item7 = menu.findItem(R.id.action_search)
//
//
//        item.isVisible = false
//        item2.isVisible = false
//        item3.isVisible = false
//        item4.isVisible = false
//        item5.isVisible = false
//        item6.isVisible = false
//        item7.isVisible = false
//        // Associate searchable configuration with the SearchView
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        val id = item.itemId
        when (id) {
            R.id.edit -> {
                if (!isNetworkAvailable()) {
                    internet()
                    promptPopUpView?.changeStatus(
                        1,
                        "Connection Error\n\n Check your internet connectivity"
                    )
                } else {

                    pref =
                        activity!!.getSharedPreferences("MyPref", 0) // 0 - for private mode


                    account = pref!!.getString("account_status", null)


                        update()

                   // mProgress?.show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }


    fun update() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(activity)
        alertDialog.setTitle("Update")

        val inflater: LayoutInflater =
            context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val layout_pwd: View =
            inflater.inflate(R.layout.layout_update_information, null)
        alertDialog.setView(layout_pwd)
        alert = alertDialog.create()
        updateFNamee = layout_pwd.findViewById<View>(R.id.etName) as MaterialEditText
        updateLNamee = layout_pwd.findViewById<View>(R.id.last) as MaterialEditText
        updateIDD = layout_pwd.findViewById<View>(R.id.id_no) as MaterialEditText
        updateAlatpressIDD = layout_pwd.findViewById<View>(R.id.idalat) as MaterialEditText
        updateEmaill = layout_pwd.findViewById<View>(R.id.email) as MaterialEditText
        updatePhonee = layout_pwd.findViewById<View>(R.id.etPhone) as MaterialEditText

        updateIDD!!.visibility = View.GONE
        val updateButton: Button =
            layout_pwd.findViewById<View>(R.id.update) as Button
        updateButton.setOnClickListener(View.OnClickListener {

            val waitingDialog: AlertDialog = SpotsDialog.Builder().setContext(context).build()

            updateFName = updateFNamee!!.getText().toString()
            updateLName = updateLNamee!!.getText().toString()
//            updateID = updateIDD!!.getText()!!.toString()
            // updateAlatpressID = updateAlatpressIDD!!.getText().toString()
            updateEmail = updateEmaill!!.getText().toString()
            updatePhone = updatePhonee!!.getText().toString()



            if (Utils.checkIfEmptyString(updateFName)) {
                updateFNamee!!.error = "Firstname Is Mandatory"
                updateFNamee!!.requestFocus()
            }
            if (Utils.checkIfEmptyString(updateLName)) {
                updateLNamee!!.error = "Lastname Is Mandatory"
                updateLNamee!!.requestFocus()
            }
            if (Utils.checkIfEmptyString(updatePhone)) {
                updatePhonee!!.error = "Phone Is Mandatory"
                updatePhonee!!.requestFocus()
            }
            if (Utils.checkIfEmptyString(updateEmail)) {
                updateEmaill!!.error = "Phone Is Mandatory"
                updateEmaill!!.requestFocus()
            }else {
                waitingDialog.show()

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
                params["firstname"] = updateFName!!
                params["lastname"] = updateLName!!
                params["email"] = updateEmail!!
                params["mssdn"] = updatePhone!!
                params["userid"] = user!!

                val api: UpdateUserRecord = retrofit.create(UpdateUserRecord::class.java)
                val call: Call<ResponseBody> = api.updateUser(params)

                call.enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(
                        call: Call<ResponseBody?>,
                        response: Response<ResponseBody?>
                    ) {
                        //Toast.makeText()

                        Log.d("Call request", call.request().toString());
                        Log.d("Response raw header", response.headers().toString());
                        Log.d("Response raw", response.toString());
                        Log.d("Response code", response.code().toString());


                        if (response.isSuccessful) {
                            val remoteResponse = response.body()!!.string()
                            Log.d("test", remoteResponse)
                            parseLoginData(remoteResponse)
                            waitingDialog.dismiss()
                        } else {
                            mProgress?.dismiss()
                            dialogue_error();
                            promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                            Log.d("BAYO", response.code().toString())
                            //btnLogin!!.text = "Submit"
                            mProgress?.dismiss()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        //btnLogin!!.text = "Submit"

                        dialogue_error()
                        promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                        Log.i("onEmptyResponse", "" + t) //
                        mProgress?.dismiss()
                    }
                })
            }

            //RequestBody body = RequestBody.Companion.create(json, JSON)\\\


        })
        val dismissButton: Button =
            layout_pwd.findViewById<View>(R.id.cancel) as Button
        dismissButton.setOnClickListener(View.OnClickListener { alert!!.dismiss() })
        alertDialog.setView(layout_pwd)
        alert!!.show()

    }

    private fun parseLoginData(jsonresponse: String) {
        try {
            val jsonObject = JSONObject(jsonresponse)
            if (jsonObject.getString("status") == "true") {
                dialogue();
                promptPopUpView?.changeStatus(2, "UPDATED SUCCESSFUL")
                mProgress?.dismiss()

            }else if (jsonObject.getString("status") == "trues") {
                mProgress?.dismiss()
                dialogue_error();

                promptPopUpView?.changeStatus(1, "Email already taken")

            }else{
                mProgress?.dismiss()
                dialogue_error();

                promptPopUpView?.changeStatus(1, "Unable to add")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
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

        androidx.appcompat.app.AlertDialog.Builder(activity!!)

            .setPositiveButton(
                "Retry"
            ) { dialog, _ ->
                dialog.dismiss()
                activity!!.recreate()
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

    private fun dialogue() {

        promptPopUpView = PromptPopUpView(activity)

        androidx.appcompat.app.AlertDialog.Builder(activity!!)
            .setPositiveButton("Exit.") { _: DialogInterface?, _: Int ->
                //      finish()



                pref =
                    context!!.getSharedPreferences("MyPref", 0) // 0 - for private mode

                val editor: SharedPreferences.Editor = pref!!.edit()
                editor.putBoolean("isLogin", false)
                editor.clear()
                editor.apply(); // commit changes


                startActivity(Intent(activity, LoginActivity::class.java))
                alert!!.dismiss()
                 Toast.makeText(activity, "Please login again", Toast.LENGTH_LONG).show() ;

//                Toast.makeText(this@Profile, "Login to continue", Toast.LENGTH_SHORT)
//                    .show()

            }
            //      finish()
            //      startActivity(Intent(activity, HomePage::class.java))

            .setCancelable(false)
            .setView(promptPopUpView)
            .show().withCenteredButtons()
    }

    private fun androidx.appcompat.app.AlertDialog.withCenteredButtons() {
        val positive = getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
        val negative = getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)

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

        promptPopUpView = PromptPopUpView(activity!!)

        androidx.appcompat.app.AlertDialog.Builder(activity!!)
            .setPositiveButton("Ok") { _: DialogInterface?, _: Int ->
                alert!!.dismiss()
            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }

    private fun dialogue_errors() {

        promptPopUpView = PromptPopUpView(activity!!)

        androidx.appcompat.app.AlertDialog.Builder(activity!!)
            .setPositiveButton("Ok") { _: DialogInterface?, _: Int ->

            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }

}