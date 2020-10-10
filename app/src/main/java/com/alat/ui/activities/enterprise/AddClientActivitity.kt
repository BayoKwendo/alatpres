package com.alat.ui.activities.enterprise

import adil.dev.lib.materialnumberpicker.dialog.CountyDialog
import adil.dev.lib.materialnumberpicker.dialog.EmployDialogue
import adil.dev.lib.materialnumberpicker.dialog.OrgDialogue
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.alat.BasicUserActivity
import com.alat.HomePage
import com.alat.R

import com.alat.helpers.Constants
import com.alat.helpers.PromptPopUpView
import com.alat.helpers.Utils
import com.alat.interfaces.AddClient

import com.google.android.material.textfield.TextInputLayout
import fr.ganfra.materialspinner.MaterialSpinner

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
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit

class AddClientActivitity : Fragment() {
    private var btn_submit: Button? = null

    private var textInputname: TextInputLayout? = null
    private var textInputlocation: TextInputLayout? = null
    private var textInputemail: TextInputLayout? = null
    private var textInputmssidn: TextInputLayout? = null
    private var textInputpostal: TextInputLayout? = null
    private var textInputwebsite: TextInputLayout? = null
    private var textInputnature: TextInputLayout? = null
    private var textInputselectednature: EditText? = null
    private var textInputdescription: TextInputLayout? = null
    private var textInputNameRG: TextInputLayout? = null
    private var natureRG: MaterialSpinner? = null

    private var promptPopUpView: PromptPopUpView? = null
    private var mProgress: ProgressDialog? = null

    private var name: String? = null
    private var location: String? = null
    private var postal: String? = null
    private var email: String? = null
    private var mssidn: String? = null
    private var website: String? = null
    private var nature: String? = null
    private var description: String? = null
    private var nameRG: String? = null
    private var nature_RG: String? = null
    private var mssidsn: String? = null

    var pref: SharedPreferences? = null

     private val ITEMS1 = arrayOf("Open", "Private")
    private var userid: String? = null

    var selectedItem: String? = null
    private var fullname: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.activity_add_client, container, false)
        pref =
            activity!!.getSharedPreferences("MyPref", 0) // 0 - for private mode
        userid = pref!!.getString("userid", null)
        fullname = pref!!.getString("fname", null) + "\t" + pref!!.getString("lname", null)

        mssidsn = pref!!.getString("mssdn", null)
        textInputname = view.findViewById(R.id.name)
        textInputlocation = view.findViewById(R.id.mlocation)
        textInputpostal = view.findViewById(R.id.postal_address)
        textInputmssidn = view.findViewById(R.id.mobile)
        textInputemail = view.findViewById(R.id.email)
        textInputwebsite = view.findViewById(R.id.website)
        textInputnature = view.findViewById(R.id.nature)
        textInputselectednature = view.findViewById(R.id.nature_org)
        textInputNameRG = view.findViewById(R.id.rg_name)
        textInputdescription = view.findViewById(R.id.description)
        textInputselectednature?.setOnClickListener(View.OnClickListener { v: View? ->
            textInputselectednature?.clearFocus()
            textInputselectednature?.isFocusable = false
            val dialog = OrgDialogue(activity)
            dialog.setOnSelectingOrg { value -> textInputselectednature!!.setText(value) }
            dialog.show()
        })

        mProgress = ProgressDialog(activity)
        mProgress!!.setMessage("Creating client....")
        mProgress!!.setCancelable(true)

        //Adapters
        val adapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, ITEMS1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        natureRG = view.findViewById<View>(R.id.nature_rg) as MaterialSpinner
        natureRG?.adapter = adapter
        natureRG!!.isSelected = false;  // otherwise listener will be called on initialization
        natureRG!!.setSelection(0, true)
        natureRG?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                arg0: AdapterView<*>?, arg1: View?,
                arg2: Int, arg3: Long
            ) {
                if (natureRG!!.selectedItem == null) {

                    // Toast.makeText(this@CreateAlert, "Please select an Alert Type", Toast.LENGTH_LONG).show();
                    return
                } else {
                    selectedItem = natureRG!!.selectedItem.toString()
                    // Toast.makeText(this@NFCWrite, tv, Toast.LENGTH_LONG).show();
                }
                // TODO Auto-generated method stub
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }


        }
        btn_submit = view.findViewById(R.id.msubmit)

        btn_submit!!.setOnClickListener {
            if (!checkContactError()) return@setOnClickListener
            else {
                createUser()
                btn_submit!!.setText("Submitting...")
                mProgress!!.show()
            }
        }

        return view


    }


    private fun registerVar() {
        name = textInputname!!.editText?.text.toString().trim { it <= ' ' }
        location = textInputlocation!!.editText?.text.toString()?.trim { it <= ' ' }
        postal = textInputpostal!!.editText?.text.toString().trim { it <= ' ' }
        mssidn = textInputmssidn!!.editText!!.text.toString().trim { it <= ' ' }
        email = textInputemail!!.editText!!.text.toString().trim { it <= ' ' }
        website = textInputwebsite!!.editText!!.text.toString().trim { it <= ' ' }
        description = textInputdescription!!.editText!!.text.toString().trim { it <= ' ' }
        nature = textInputselectednature?.text?.toString()?.trim({ it <= ' ' })
        nameRG = textInputNameRG!!.editText?.text.toString()?.trim({ it <= ' ' })

    }


    private fun showKeyBoard() {
        val imm =
            activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    private fun checkContactError(): Boolean {

        registerVar()

        if (Utils.checkIfEmptyString(postal)) {
            textInputpostal!!.error = "Postal address is mandatory"
            textInputpostal!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputpostal!!.error = null
        if (Utils.checkIfEmptyString(mssidn)) {
            textInputmssidn!!.error = "phone number is mandatory"
            textInputmssidn!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputmssidn!!.error = null
        if (Utils.checkIfEmptyString(email)) {
            textInputemail!!.error = "email is mandatory"
            textInputemail!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputemail!!.error = null

        if (selectedItem == null) {
            Toast.makeText(
                activity,
                "Please  nature of RG",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
        if (Utils.checkIfEmptyString(name)) {
            textInputname!!.error = "Name is required"
            textInputname!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputname!!.error = null

        if (Utils.checkIfEmptyString(location)) {
            textInputlocation!!.error = "Location is mandatory"
            textInputlocation!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputlocation!!.error = null
        if (Utils.checkIfEmptyString(nameRG)) {
            textInputNameRG!!.error = "Name of the RG is mandatory"
            textInputNameRG!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputNameRG!!.error = null

        if (Utils.checkIfEmptyString(nature)) {
            textInputnature!!.error = "Nature of your org is mandatory"
            textInputnature!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputnature!!.error = null


        return true
    }


    private fun createUser() {
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

        params["name"] = name!!
        params["location"] = location!!
        params["email"] = email!!
        params["nature_client"] = nature!!
        params["description"] = description!!
        params["postal_address"] = postal!!
        params["website"] = website!!
        params["mssdn"] = mssidn!!
        params["userid"] = userid!!
        params["name_rg"] = nameRG!!
        params["nature_rg"] = selectedItem!!
        params["fullname"] = fullname!!
        params["mssidn"] = mssidsn!!

        val api: AddClient = retrofit.create(AddClient::class.java)
        val call: Call<ResponseBody> = api.addClient(params)

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
                            btn_submit!!.setText("Submit")

                            promptPopUpView?.changeStatus(2, "Client has been added successfully")
                        } else if (jsonObject.getString("status") == "false"){
                            dialogue_error();
                            btn_submit!!.setText("Submit")

                            promptPopUpView?.changeStatus(1, "Unable to create the Client. Please Try Again")
                        }

                        else if (jsonObject.getString("status") == "normal"){
                            dialogue_error();
                            btn_submit!!.setText("Submit")

                            promptPopUpView?.changeStatus(1, "Client name is already taken. Please use a different name for the client.")
                        }
                        else{
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

    private fun dialogue() {

        promptPopUpView = PromptPopUpView(activity)

        AlertDialog.Builder(activity!!)
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

     fun onBackPressed() {
//        val intent = Intent(this@AddClientActivitity, BasicUserActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//        startActivity(intent)
//        finish()
    }
}

