package com.alat.ui.activities.enterprise

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.alat.HomePage
import com.alat.R
import com.alat.adapters.SimpleArrayListAdapter
import com.alat.adapters.SimpleListAdapter
import com.alat.helpers.Constants
import com.alat.helpers.PromptPopUpView
import com.alat.helpers.Utils
import com.alat.interfaces.*
import com.alat.interfaces.AddStation
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import gr.escsoft.michaelprimez.searchablespinner.SearchableSpinner
import gr.escsoft.michaelprimez.searchablespinner.interfaces.IStatusListener
import gr.escsoft.michaelprimez.searchablespinner.interfaces.OnItemSelectedListener
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AddStation : Fragment() {
    private var btn_submit: Button? = null
    private var mSearchableSpinner1: SearchableSpinner? = null
    private var mSimpleListAdapter: SimpleListAdapter? = null

    private var textInputname: TextInputLayout? = null
    private var textInputlocation: TextInputLayout? = null
    private var textInputemail: TextInputLayout? = null
    private var textInputmssidn: TextInputLayout? = null
    private var mSimpleArrayListAdapter: SimpleArrayListAdapter? = null
    private val mStrings: ArrayList<String> = ArrayList()

    private var mProgress: ProgressDialog? = null

    private var mProgressfetch: ProgressDialog? = null

    private var name: String? = null
    private var location: String? = null

    private var userid: String? = null
    var btn_back: android.widget.Button? = null
    var confirmPay: android.widget.Button? = null

    private var postal: String? = null
    private var email: String? = null
    private var mssidn: String? = null
    private var website: String? = null
    private var nature: String? = null
    private var description: String? = null
    private var nameRG: String? = null
    var phone: EditText? = null
    var mpesa_code: android.widget.EditText? = null

    var mtxtView: TextView? = null

    var payment_procedure: android.widget.TextView? = null

    private var nature_RG: String? = null

    var AUTOCOMPLETE_REQUEST_CODE = 1

    private var destinationAddress: String? = null

    private val ITEMS1 = arrayOf("Open", "Private")
    var selectedItem: String? = null
    private var promptPopUpView: PromptPopUpView? = null
    val catList: ArrayList<String> = ArrayList()
    private var selecteditem3: String? = null
    var pref: SharedPreferences? = null
    private var fullname: String? = null
    private var edit: TextInputEditText? = null

    var p: String?= null

    var send: Button? = null

    var constraintLayout: ConstraintLayout? = null
    var constraintLayout2: androidx.constraintlayout.widget.ConstraintLayout? = null

    private var mProgressDialog: ProgressDialog? = null

    var main_linear: LinearLayoutCompat? = null
    var main_relative: RelativeLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.activity_add_station, container, false)
        if (!Places.isInitialized()) {
            Places.initialize(requireActivity(), getString(R.string.google_maps_key))
        }
        pref =
            requireActivity().getSharedPreferences("MyPref", 0) // 0 - for private mode
        userid = pref!!.getString("userid", null)

        fullname = pref!!.getString("fname", null) + "\t" + pref!!.getString("lname", null)

        mssidn = pref!!.getString("mssdn", null)

        main_linear = view.findViewById<LinearLayoutCompat>(R.id.main_view)
        main_relative = view.findViewById<RelativeLayout>(R.id.main_relative)

        mtxtView = view.findViewById<TextView>(R.id.textView6)
        payment_procedure = view.findViewById<TextView>(R.id.payment_procedure)

        mProgressDialog = ProgressDialog(requireActivity())
        mProgressDialog!!.setMessage("Checking...please wait")
        mProgressDialog!!.setCancelable(false)
        mpesa_code = view.findViewById<EditText>(R.id.mpesa_code)
        btn_back = view.findViewById<Button>(R.id.btn_back)
        confirmPay = view.findViewById<Button>(R.id.btn_confirm)
        constraintLayout = view.findViewById<ConstraintLayout>(R.id.view1)
        constraintLayout2 = view.findViewById<ConstraintLayout>(R.id.view2)

//       Toast.makeText(activity, "WOW"  +   userid, Toast.LENGTH_SHORT).show()

        textInputlocation = view.findViewById(R.id.mlocation)

        textInputname = view.findViewById(R.id.rg_name)

        edit = view.findViewById((R.id.loc2))

        mProgress = ProgressDialog(activity)
        mProgress!!.setMessage("Creating Department....")
        mProgress!!.setCancelable(true)

        mProgressfetch = ProgressDialog(activity)
        mProgressfetch!!.setMessage("Fetching Clients....")
        mProgressfetch!!.setCancelable(true)

        mSimpleListAdapter = SimpleListAdapter(activity, mStrings)
        mSimpleArrayListAdapter = SimpleArrayListAdapter(activity, mStrings)

        mSearchableSpinner1 = view.findViewById<View>(R.id.SearchableSpinner1) as SearchableSpinner

        mSearchableSpinner1!!.setAdapter(mSimpleListAdapter)
        mSearchableSpinner1!!.setOnItemSelectedListener(mOnItemSelectedListener1)
        mSearchableSpinner1!!.setStatusListener(object : IStatusListener {
            override fun spinnerIsOpening() {

            }

            override fun spinnerIsClosing() {}
        })
        //Adapters
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ITEMS1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        btn_submit = view.findViewById(R.id.msubmit)

        btn_submit!!.setOnClickListener {

            if (!checkContactError()) return@setOnClickListener
            else {
                createStation()
                mProgress!!.show()
                btn_submit!!.setText("Submitting....")
            }
        }

        textInputlocation!!.setOnClickListener {
            searchPlace()
        }
        textInputlocation!!.requestFocus()
        edit!!.setOnClickListener {
            searchPlace()
        }
        payment_procedure!!.text = Html.fromHtml("1. Go to M-Pesa menu<br/>2. Click on Lipa na M-Pesa<br/>3. Click on Paybill Option<br/>4. Enter Businness No. <b>4036601</b><br/>5. Enter Account Name <b>$userid</b> <br/><b>N/B: </b> " +
                "Account Name is your Alatpres ID <br/>6. Enter amount <b>1200</b> KES<br/>7. Wait for the M-Pesa message<br/>8. Confirm your payment <br/>(Click the button below)")
        mtxtView!!.setText(Html.fromHtml("A Fee of Ksh. <b>12,000 is required to proceed on creating a station/department</b>"))
        confirmPay!!.setOnClickListener(View.OnClickListener { view: View? ->
            mProgressDialog!!.show()
            constraintLayout!!.visibility = View.GONE
            constraintLayout2!!.visibility = View.VISIBLE
            getPayments()
        })

        send = view.findViewById<Button>(R.id.send)

        send!!.setOnClickListener {
            val transaction: String = mpesa_code!!.getText().toString()
            val length: Int = mpesa_code!!.getText().length
            if (transaction.isEmpty()) {
                mpesa_code!!.error = "Transaction code Require"
                mProgress!!.dismiss()
            } else if (length < 10 || length > 10) {
                mpesa_code!!.error = "Code not Valid"
                mProgress!!.dismiss()
            } else {
                enterTransaction()
            }
        }

        return view
    }


    fun enterTransaction() {
        mProgress!!.show()
        p = mpesa_code!!.text.toString().trim { it <= ' ' }
        mProgressDialog!!.show();
        ConfirmPayment(p)
    }

    private fun getPayments() {
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
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()


        val api: CheckPayments = retrofit.create(
            CheckPayments::class.java
        )
        val call: Call<String>? = api.checkPay(userid)
        call?.enqueue(object : Callback<String?> {
            @SuppressLint("LogNotTimber")
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                Log.d("Responsestring", response.toString())
                //Toast.makeText()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        Log.d("onSuccess", response.body().toString())
                        //Toast.makeText(this@LoginActivity, "Success"  + response.body(), Toast.LENGTH_LONG).show()
                        val jsonresponse = response.body().toString()
                        checkpayment(jsonresponse)
                    } else {
                        mProgress!!.dismiss()
                        Log.i(
                            "onEmptyResponse",
                            "Returned empty response"
                        ) //Toast.makeText(getContext(),"Nothing returned",Toast.LENGTH_LONG).show();
                    }
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Log.i("onEmptyResponse", "" + t) //
                mProgress!!.dismiss()
            }
        })
    }
    @Throws(JSONException::class)
    private fun checkpayment(json: String) {
        try {
            val jsonObject = JSONObject(json)
            if (jsonObject.getString("status") == "true") {
                Toast.makeText(requireActivity(), "DONE", Toast.LENGTH_SHORT).show()
                val dataArray = jsonObject.getJSONArray("postData")
                for (i in 0 until dataArray.length()) {
                    if (dataArray.length() == 1) {
                        val dataobj = dataArray.getJSONObject(i)
                        mpesa_code!!.setText(dataobj.getString("TransID"))
                        mProgressDialog!!.dismiss()
                        p = dataobj.getString("TransID")
//                        Check
//                        getJSO("http://167.172.17.121/api/transactions/update.php?phone=$p&userid=$userid&amount=1200")
                    }
                }
            } else {
//                Toast.makeText(requireActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                mProgressDialog!!.dismiss()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

     fun ConfirmPayment(transaction: String?) {
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
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()


        val api: ConfirmPayments = retrofit.create(
            ConfirmPayments::class.java
        )
        val call: Call<String>? = api.confirmPay(transaction,"1200",userid)
        call?.enqueue(object : Callback<String?> {
            @SuppressLint("LogNotTimber")
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                Log.d("Responsestring", response.toString())
                //Toast.makeText()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        Log.d("onSuccess", response.body().toString())
                        //Toast.makeText(this@LoginActivity, "Success"  + response.body(), Toast.LENGTH_LONG).show()
                        val jsonresponse = response.body().toString()
                        loadIntoListVie(jsonresponse)
                    } else {
                        mProgress!!.dismiss()
                        Log.i(
                            "onEmptyResponse",
                            "Returned empty response"
                        ) //Toast.makeText(getContext(),"Nothing returned",Toast.LENGTH_LONG).show();
                    }
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Log.i("onEmptyResponse", "" + t) //
                mProgress!!.dismiss()
            }
        })
    }


    @Throws(JSONException::class)
    private fun loadIntoListVie(json: String) {
        try {
            val jsonObject = JSONObject(json)
            if (jsonObject.getString("status") == "true") {
                val dataArray = jsonObject.getJSONArray("postData")
                for (i in 0 until dataArray.length()) {
                    val dataobj = dataArray.getJSONObject(i)
                    Toast.makeText(
                        requireActivity(),
                        "" + dataobj.getString("TransID"),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    val p = mpesa_code!!.text.toString().trim { it <= ' ' }
                    if (p == dataobj.getString("TransID")) {
//                        loginUser()

                          ShowStationDiaslogue()
                        mProgressDialog!!.dismiss()
                        promptPopUpView?.changeStatus(2, "Payment confirmed successfully")


                    } else {
                        promptPopUpView = PromptPopUpView(requireActivity())
                        promptPopUpView!!.changeStatus(1, "WRONG CODE")
                        mProgress!!.dismiss()
                        val dialog = AlertDialog.Builder(Objects.requireNonNull(requireActivity()))
                            .setPositiveButton(
                                "Ok"
                            ) { dialog, which -> dialog.dismiss() }
                            .setCancelable(true)
                            .setView(promptPopUpView)
                            .show()
                        val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        // button.setEnabled(false);
                        button.setTextColor(resources.getColor(R.color.colorBlack))
                    }
                }
            } else {
                promptPopUpView = PromptPopUpView(requireActivity())
                promptPopUpView!!.changeStatus(1, jsonObject.getString("message"))
                mProgress!!.dismiss()
                mProgressDialog!!.dismiss()
                val dialog = AlertDialog.Builder(Objects.requireNonNull(requireActivity()))
                    .setPositiveButton(
                        "Ok"
                    ) { dialog, which -> dialog.dismiss() }
                    .setCancelable(true)
                    .setView(promptPopUpView)
                    .show()
                val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                // button.setEnabled(false);
                button.setTextColor(resources.getColor(R.color.colorBlack))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    private fun searchPlace() {

        // Set the fields to specify which types of place data to return.
        val fields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )
        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY, fields
        ).setCountry("KE") //KENYA
            .build(activity!!)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    private val mOnItemSelectedListener1: OnItemSelectedListener = object : OnItemSelectedListener {
        override fun onItemSelected(view: View?, position: Int, id: Long) {
            if (position > 0) {
                selecteditem3 = mSimpleListAdapter!!.getItem(position).toString()
            }

            // Toast.makeText(activity, "VALUE" + selecteditem3, Toast.LENGTH_LONG).show();


        }

        override fun onNothingSelected() {
            Toast.makeText(activity, "Nothing Selected", Toast.LENGTH_SHORT).show()
        }
    }


    private fun registerVar() {
        name = textInputname!!.editText?.text.toString().trim { it <= ' ' }
        location = textInputlocation!!.editText?.text.toString()?.trim { it <= ' ' }

    }


    private fun showKeyBoard() {
        val imm =
            activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    private fun checkContactError(): Boolean {

        registerVar()
//       if (selecteditem3 == null) {
//            Toast.makeText(
//                activity,
//                "Please  name of the client",
//                Toast.LENGTH_LONG
//            ).show()
//            return false
//        }
        if (Utils.checkIfEmptyString(name)) {
            textInputname!!.error = "Station Name is required"
            textInputname!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputname!!.error = null

        if (Utils.checkIfEmptyString(location)) {
            textInputlocation!!.error = " Station Location is mandatory"
            textInputlocation!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputlocation!!.error = null

        return true
    }


    private fun getStudent() {
        mProgressfetch!!.show()


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
        val api: ViewGPsEnteClient = retrofit.create(ViewGPsEnteClient::class.java)
        val call: Call<ResponseBody>? = api.viewRG(params)

        call?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                //Toast.makeText()
                if (response.isSuccessful) {
                    val remoteResponse = response.body()!!.string()
                    parseLogiData(remoteResponse)

                } else {
                    Log.d("bayo", response.errorBody()!!.string())

                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                //   btn.text = "Proceed"
                Log.i("onEmptyResponse", "" + t) //

                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                //mProgress?.dismiss()
            }
        })
    }

    private fun parseLogiData(jsonresponse: String) {
        try {
            val o = JSONObject(jsonresponse)
            val array: JSONArray = o.getJSONArray("records")
            //  val array: JSONArray = JSONArray(jsonresponse)
            mProgressfetch!!.dismiss()
            val jsonarray = JSONArray(array.toString())
            for (i in 0 until jsonarray.length()) {
                val jsonobject: JSONObject = jsonarray.getJSONObject(i)
                mStrings.add(jsonobject.getString("name"))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }


    }


    private fun createStation() {
        registerVar()

        // Toast.makeText(this@Enterprise, "VALUE" + selectedItem2, Toast.LENGTH_LONG).show();

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
            .addConverterFactory(ScalarsConverterFactory.create())

            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val params: HashMap<String, String> = HashMap()

//        params["client_name"] = selecteditem3!!
        params["location"] = location!!
        params["userid"] = userid!!
        params["station_name"] = name!!
        params["fullname"] = fullname!!
        params["mssidn"] = mssidn!!


        val api: AddStation = retrofit.create(AddStation::class.java)
        val call: Call<ResponseBody> = api.createStation(params)

        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                //Toast.makeText()

                Log.d("Call request", call.request().toString());
                Log.d("Response raw header", response.headers().toString());
                Log.d("Response raw", response.toString());


                if (response.isSuccessful) {
                    var remoteResponse = response.body()!!.string()
                    Log.d("Responsess", remoteResponse);

                    try {
                        var jsonObject = JSONObject(remoteResponse)
                        if (jsonObject.getString("status") == "true") {
                            dialogue()
                            mProgress?.dismiss()
                            btn_submit!!.setText("Submit")
                            promptPopUpView?.changeStatus(2, "Station has been added successfully")
                        } else if (jsonObject.getString("status") == "false") {
                            dialogue_error();
                            mProgress?.dismiss()
                            btn_submit!!.setText("Submit")
                            promptPopUpView?.changeStatus(1, "Unsuccessfully")
                        } else if (jsonObject.getString("status") == "normal") {
                            dialogue_error();
                            mProgress?.dismiss()
                            btn_submit!!.setText("Submit")
                            promptPopUpView?.changeStatus(
                                1,
                                "Station name is already taken. Please use a different name for the client."
                            )
                        } else {
                            dialogue_error();
                            btn_submit!!.setText("Submit")
                            promptPopUpView?.changeStatus(1, "Something went wrong")
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                } else {
                    mProgress?.dismiss()
                    dialogue_error()
                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                    Log.d("BAYO", response.code().toString())
                    mProgress?.dismiss()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                Log.i("onEmptyResponse", "" + t) //
                mProgress?.dismiss()
            }
        })
    }


    private fun ShowStationDiaslogue() {

        promptPopUpView = PromptPopUpView(activity)

        AlertDialog.Builder(requireActivity())
            .setPositiveButton(
                "ok"
            ) { dialog, _ ->
                dialog.dismiss()

                mProgressDialog!!.dismiss()
                mProgress!!.dismiss()
                main_relative!!.visibility = View.GONE
                main_linear!!.visibility = View.VISIBLE
//
//                val i = Intent(activity, HomePage::class.java)
//                startActivity(i)
            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }

    private fun dialogue() {

        promptPopUpView = PromptPopUpView(activity)

        AlertDialog.Builder(requireActivity())
            .setPositiveButton(
                "ok"
            ) { dialog, _ ->
                dialog.dismiss()

                val i = Intent(activity, HomePage::class.java)
                startActivity(i)
            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }

    private fun dialogue_error() {

        promptPopUpView = PromptPopUpView(activity)

        AlertDialog.Builder(activity!!)
            .setPositiveButton(
                "Ok"
            ) { dialog, _ ->
                dialog.dismiss()
            }


            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                destinationAddress = place.name

                textInputlocation!!.editText?.setText(destinationAddress.toString())

                // Toast.makeText(this@CreateAlert, "" + destinationAddress.toString() , Toast.LENGTH_LONG).show();

                // lblAddress
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status = Autocomplete.getStatusFromIntent(data!!)
//                Toast.makeText(this@CreateRG, "Some went wrong. Search again", Toast.LENGTH_SHORT)
//                    .show()
                // Log.i(TAG, status.getStatusMessage())
            }
        }
    }

    fun onBackPressed() {
//        val intent = Intent(this@AddClientActivitity, BasicUserActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//        startActivity(intent)
//        finish()
    }
}

