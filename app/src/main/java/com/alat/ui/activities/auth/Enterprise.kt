package com.alat.ui.activities.auth

import adil.dev.lib.materialnumberpicker.dialog.CountyDialog
import adil.dev.lib.materialnumberpicker.dialog.EmployDialogue
import adil.dev.lib.materialnumberpicker.dialog.OrgDialogue
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import com.alat.HomePage
import com.alat.MainActivity
import com.alat.R
import com.alat.helpers.Constants
import com.alat.helpers.PromptPopUpView
import com.alat.helpers.Utils
import com.alat.interfaces.CreateEnterprise
import com.alat.interfaces.CreateProvider
import com.alat.interfaces.CreateUser
import com.alat.model.TextViewDatePicke
import com.alat.model.TextViewDatePicker
import com.google.android.material.textfield.TextInputLayout
import fr.ganfra.materialspinner.MaterialSpinner
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
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

class Enterprise : AppCompatActivity() {
    var btn_next: Button? = null
    var linearLayout: LinearLayoutCompat? = null
    var frameLayout: FrameLayout? = null
    private var textInputname: TextInputLayout? = null
    private var textInputothername: TextInputLayout? = null
    private var textInputtax: TextInputLayout? = null
    private var textInputdob: TextInputLayout? = null
    private var textInputcounties: TextInputLayout? = null
    private var mcounty: EditText? = null
    private var textInputnature: TextInputLayout? = null
    private var textInputselectednature: EditText? = null
    private var textInputnclient: TextInputLayout? = null

    private var promptPopUpView: PromptPopUpView? = null
    private var mProgress: ProgressDialog? = null

    private var textInputnclientEdit: EditText? = null

    private var textInputemply: TextInputLayout? = null
    private var textInputselectedemploy: EditText? = null
    private var textInputdescription: TextInputLayout? = null
    private var textInputDoB: EditText? = null

    private var name: String? = null
    private var other_name: String? = null
    private var nature: String? = null
    private var tax: String? = null
    private var client: String? = null
    private var employee: String? = null
    private var description: String? = null
    private var dob: String? = null
    private var county: String? = null


    //Contact
    private var textInputemail: TextInputLayout? = null
    private var textInputmssidn: TextInputLayout? = null
    private var textInputmssidn2: TextInputLayout? = null
    private var textInputtown: TextInputLayout? = null
    private var textInputcode: TextInputLayout? = null
    private var textInputphysicaladdress: TextInputLayout? = null
    private var textInputpostal: TextInputLayout? = null
    private var textInputwebsite: TextInputLayout? = null
    private var textInputClient: TextInputLayout? = null

    private var textInputuserid: TextInputLayout? = null
    private var textInputconfirmuserid: TextInputLayout? = null

    private var textInputpassword: TextInputLayout? = null
    private var textInputconfirmpassword: TextInputLayout? = null

    private var physical: String? = null
    private var postal: String? = null
    private var email: String? = null
    private var code: String? = null
    private var mssidn: String? = null
    private var mssidn2: String? = null
    private var town: String? = null
    private var website: String? = null
    private var btn_submit: Button? = null
    private var password: String? = null
    private var confirmpassword: String? = null
    private var user_id: String? = null
    private var confirm_user_id: String? = null

    private val ITEMS1 = arrayOf("YES", "NO")
    private val ITEMS2 = arrayOf("security", "medical", "fire", "car", "towing")
    var spinner: MaterialSpinner? = null
    var spinner_2: MaterialSpinner? = null
    var selectedItem: String? = null
    var selectedItem2: String? = null


    private var backText: TextView? = null


    var prefs: SharedPreferences? = null
    private var btn_register: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_enterprise)
        val toolbar =
            findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.elevation = 0f
        }
        init()
        linearLayout = findViewById(R.id.main_view)
        frameLayout = findViewById(R.id.next_view)


        btn_next = findViewById(R.id.buttonNext)
        btn_next!!.setOnClickListener {
            if (!checkError()) return@setOnClickListener
            else {
                linearLayout!!.visibility = View.INVISIBLE
                frameLayout!!.visibility = View.VISIBLE
            }
        }

        mProgress = ProgressDialog(this)
        mProgress!!.setMessage("Creating enterprise....")
        mProgress!!.setCancelable(true)

        //Adapters
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ITEMS1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner = findViewById<View>(R.id.rsp) as MaterialSpinner
        spinner?.adapter = adapter
        spinner!!.isSelected = false;  // otherwise listener will be called on initialization
        spinner!!.setSelection(0, true)
        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                arg0: AdapterView<*>?, arg1: View?,
                arg2: Int, arg3: Long
            ) {
                if (spinner!!.selectedItem == null) {
                    spinner_2!!.visibility = View.GONE
                    // Toast.makeText(this@CreateAlert, "Please select an Alert Type", Toast.LENGTH_LONG).show();
                    return
                } else {
                    selectedItem = spinner!!.selectedItem.toString()

                    if (selectedItem == "YES") {
                        spinner_2!!.visibility = View.VISIBLE
                    } else {
                        spinner_2!!.visibility = View.GONE
                    }
                    // Toast.makeText(this@NFCWrite, tv, Toast.LENGTH_LONG).show();
                }
                // TODO Auto-generated method stub
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        }

        val adapter1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, ITEMS2)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_2 = findViewById<View>(R.id.nature_response) as MaterialSpinner
        spinner_2?.adapter = adapter1
        spinner_2!!.isSelected = false;  // otherwise listener will be called on initialization
        spinner_2!!.setSelection(0, true)
        spinner_2?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                arg0: AdapterView<*>?, arg1: View?,
                arg2: Int, arg3: Long
            ) {
                selectedItem2 = when (spinner_2!!.selectedItem) {
                    null -> {
                        // Toast.makeText(this@CreateAlert, "Please select an Alert Type", Toast.LENGTH_LONG).show();
                        "NULL"

                    }
                    else -> {
                        spinner_2!!.selectedItem.toString()

                    }
                }
                // TODO Auto-generated method stub
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        }

        btn_submit = findViewById(R.id.submit)

        btn_submit!!.setOnClickListener {
            if (!checkContactError()) return@setOnClickListener
            else {
                createUser()
                mProgress!!.show()
            }
        }


//        btn_next?.findViewById<Button>(R.id.buttonNext)?.setOnClickListener {
//          //  btn_next.toggleVisibility()
//
//        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        if (item.itemId == android.R.id.home) {
            if (frameLayout!!.visibility == View.VISIBLE) {
                frameLayout!!.visibility = View.GONE
                linearLayout!!.visibility = View.VISIBLE
                // Its visible
            } else {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun init() {
        textInputname = findViewById(R.id.name)
        textInputothername = findViewById<TextInputLayout>(R.id.other_name)
        textInputtax = findViewById(R.id.tax)
        textInputcounties = findViewById(R.id.counties_layout)
        mcounty = findViewById(R.id.counties)
        textInputdob = findViewById(R.id.date_layout)
        textInputDoB = findViewById(R.id.date)
        textInputdescription = findViewById(R.id.description)
        textInputnature = findViewById(R.id.name)
        textInputselectednature = findViewById(R.id.nature_org)
        textInputemply = findViewById(R.id.employee)
        textInputselectedemploy = findViewById(R.id.number_employ)
        textInputnclientEdit = findViewById(R.id.client_level)
        textInputnclient = findViewById(R.id.client)

        textInputuserid = findViewById(R.id.user_id)
        textInputconfirmuserid = findViewById(R.id.confirm_user_id)
        textInputpassword = findViewById(R.id.password)
        textInputconfirmpassword = findViewById(R.id.confirm_password)

        //contact
        textInputemail = findViewById(R.id.email)
        textInputmssidn = findViewById(R.id.mobile)
        textInputmssidn2 = findViewById(R.id.mobile2)
        textInputphysicaladdress = findViewById(R.id.physical_address)
        textInputpostal = findViewById(R.id.postal_address)
        textInputtown = findViewById(R.id.town)
        textInputwebsite = findViewById(R.id.website)
        textInputcode = findViewById(R.id.code)

        backText = findViewById(R.id.back)


        backText!!.setOnClickListener {
            val intent = Intent(this@Enterprise, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        btn_next = findViewById(R.id.buttonNext)
        btn_next!!.setOnClickListener {
            if (!checkError()) return@setOnClickListener
            else {
                Toast.makeText(this, "WOW", Toast.LENGTH_SHORT).show()
            }
        }

        TextViewDatePicke(this, textInputDoB!!)

        textInputselectednature?.setOnClickListener(View.OnClickListener { v: View? ->
            textInputselectednature?.clearFocus()
            textInputselectednature?.isFocusable = false
            val dialog = OrgDialogue(this)
            dialog.setOnSelectingOrg { value -> textInputselectednature!!.setText(value) }
            dialog.show()
        })

        mcounty?.setOnClickListener(View.OnClickListener { v: View? ->
            mcounty?.clearFocus()
            mcounty?.isFocusable = false
            val dialog = CountyDialog(this)
            dialog.setOnSelectingcounty { value -> mcounty?.setText(value) }
            dialog.show()
        })

        textInputselectedemploy?.setOnClickListener(View.OnClickListener { v: View? ->
            textInputselectedemploy?.clearFocus()
            textInputselectedemploy?.isFocusable = false
            val dialog = EmployDialogue(this)
            dialog.setOnSelectingEmploy { value -> textInputselectedemploy?.setText(value) }
            dialog.show()
        })

        textInputnclientEdit?.setOnClickListener(View.OnClickListener { v: View? ->
            textInputnclientEdit?.clearFocus()
            textInputnclientEdit?.isFocusable = false
            val dialog = EmployDialogue(this)
            dialog.setOnSelectingEmploy { value -> textInputnclientEdit?.setText(value) }
            dialog.show()
        })



    }


    private fun registerVar() {
        name = textInputname!!.editText?.text.toString().trim { it <= ' ' }
        client = textInputnclientEdit!!.text.toString().trim { it <= ' ' }
        other_name = textInputothername?.editText?.text.toString().trim({ it <= ' ' })
        tax = textInputtax!!.editText!!.text.toString().trim { it <= ' ' }
        description = textInputdescription!!.editText!!.text.toString().trim { it <= ' ' }
        nature = textInputselectednature?.text?.toString()?.trim({ it <= ' ' })
        employee = textInputselectedemploy?.text?.toString()?.trim({ it <= ' ' })
        dob = textInputDoB!!.text.toString().trim { it <= ' ' }
        county = mcounty!!.text.toString().trim { it <= ' ' }

        //contact
        physical = textInputphysicaladdress!!.editText?.text.toString().trim { it <= ' ' }
        postal = textInputpostal!!.editText?.text.toString().trim { it <= ' ' }
        town = textInputtown!!.editText!!.text.toString().trim { it <= ' ' }
        code = textInputcode!!.editText!!.text.toString().trim { it <= ' ' }
        mssidn = textInputmssidn!!.editText!!.text.toString().trim { it <= ' ' }
        mssidn2 = textInputmssidn2!!.editText!!.text.toString().trim { it <= ' ' }
        email = textInputemail!!.editText!!.text.toString().trim { it <= ' ' }
        website = textInputwebsite!!.editText!!.text.toString().trim { it <= ' ' }

        password = textInputpassword?.editText?.text.toString().trim({ it <= ' ' })
        confirmpassword = textInputconfirmpassword?.editText?.text.toString().trim({ it <= ' ' })
        user_id = textInputuserid?.editText?.text.toString().trim({ it <= ' ' })
        confirm_user_id = textInputconfirmuserid?.editText?.text.toString().trim({ it <= ' ' })

    }


    private fun showKeyBoard() {
        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    private fun checkError(): Boolean {

        registerVar()
        if (Utils.checkIfEmptyString(name)) {
            textInputname!!.error = "Name is required"
            textInputname!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputname!!.error = null

        if (Utils.checkIfEmptyString(tax)) {
            textInputtax!!.error = "Tax ID is mandatory"
            textInputtax!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputtax!!.error = null
        if (Utils.checkIfEmptyString(county)) {
            Toast.makeText(this, "County of operation is Mandatory", Toast.LENGTH_SHORT).show()
            textInputcounties!!.error = "County of operation is mandatory"
            textInputcounties!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputcounties!!.error = null

        if (Utils.checkIfEmptyString(dob)) {
            textInputdob!!.error = "Date of Incorporation is mandatory"
            textInputdob!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputdob!!.error = null

        if (Utils.checkIfEmptyString(nature)) {
            textInputnature!!.error = "Nature of your org is mandatory"
            textInputnature!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputnature!!.error = null

        if (Utils.checkIfEmptyString(client)) {
            textInputnclient!!.error = "Client level is mandatory"
            textInputnclient!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputnclient!!.error = null
        if (Utils.checkIfEmptyString(employee)) {
            Toast.makeText(this, "Employee number is Mandatory", Toast.LENGTH_SHORT).show()
            textInputemply!!.error = "Gender is mandatory"
            textInputemply!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputemply!!.error = null


        return true
    }


    private fun checkContactError(): Boolean {

        registerVar()
        if (Utils.checkIfEmptyString(physical)) {
            textInputphysicaladdress!!.error = "Physical address is required"
            textInputphysicaladdress!!.requestFocus()
            showKeyBoard()


            return false
        } else textInputphysicaladdress!!.error = null

        if (Utils.checkIfEmptyString(postal)) {
            textInputpostal!!.error = "Postal address is mandatory"
            textInputpostal!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputpostal!!.error = null
        if (Utils.checkIfEmptyString(town)) {
            textInputtown!!.error = "Town is mandatory"
            textInputtown!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputtown!!.error = null
        if (Utils.checkIfEmptyString(code)) {
            textInputcode!!.error = "Date of Incorporation is mandatory"
            textInputcode!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputcode!!.error = null
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
                this@Enterprise,
                "Please select if you are a response provider alone or not",
                Toast.LENGTH_LONG
            ).show()
            return false
        }

        if (user_id!!.isEmpty()) {
            textInputuserid!!.error = "Please set your user ID"
            textInputuserid!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputuserid!!.error = null

        if (password!!.isEmpty()) {
            textInputpassword!!.error = "Please set your password"
            textInputpassword!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputpassword!!.error = null

        if (user_id!!.length < 4) {
            Toast.makeText(this, "User ID must be at least four characters", Toast.LENGTH_SHORT)
                .show()
            textInputuserid!!.error = "User ID must be at least four characters"
            textInputuserid!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputuserid!!.error = null

        if (password!!.length < 6) {
            Toast.makeText(this, "Password must be at least six characters", Toast.LENGTH_SHORT)
                .show()
            textInputpassword!!.error = "User ID must be at least six characters"
            textInputpassword!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputpassword!!.error = null

        if (user_id != confirm_user_id) {
            Toast.makeText(this, "User ID do not match", Toast.LENGTH_SHORT).show()
            textInputconfirmuserid!!.error = "User ID do not match"
            textInputconfirmuserid!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputconfirmuserid!!.error = null
        if (password != confirmpassword) {
            Toast.makeText(this, "Password do not match", Toast.LENGTH_SHORT).show()
            textInputconfirmpassword!!.error = "Password do not match"
            textInputconfirmpassword!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputconfirmpassword!!.error = null

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
        params["firstname"] = name!!
        params["lastname"] = other_name!!
        params["email"] = email!!
        params["taxid"] = tax!!
        params["nature_org"] = nature!!
        params["description"] = description!!
        params["physical_address"] = physical!!
        params["postal_address"] = postal!!
        params["town"] = town!!
        params["code"] = code!!
        params["website"] = website!!
        params["date_of_incooperation"] = dob!!
        params["mssdn2"] = mssidn2!!
        params["mssdn"] = mssidn!!
        params["response_provider"] = selectedItem!!
        params["nature_response"] = selectedItem2!!
        params["county"] = county!!
        params["userid"] = user_id!!
        params["password"] = password!!


        val api: CreateEnterprise = retrofit.create(CreateEnterprise::class.java)
        val call: Call<ResponseBody> = api.EnterCreate(params)

        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                //Toast.makeText()

                Log.d("Call request", call.request().toString());
                Log.d("Call request header", call.request().headers.toString());
                Log.d("Response raw header", response.headers().toString());
                Log.d("Response raw", response.toString());
                Log.d("Response code", response.code().toString());


                if (response.isSuccessful) {
                    val remoteResponse = response.body()!!.string()
                    Log.d("test", remoteResponse)

                    if (response.code() == 201) {
                        mProgress?.dismiss()

                        dialogue_error()
                        promptPopUpView?.changeStatus(1, "Email already taken!!")
                    }
                    if (response.code() == 202) {
                        mProgress?.dismiss()

                        dialogue_error()
                        promptPopUpView?.changeStatus(1, "User ID already taken!!")
                    }
                    if (response.code() == 400) {
                        mProgress?.dismiss()

                        dialogue_error()
                        promptPopUpView?.changeStatus(1, "Unable to create User! please try again")
                    } else if (response.code() == 200) {


                        if(selectedItem == "YES"){
                            createProvider()
                        } else {

                            mProgress?.dismiss()

                            Toast.makeText(this@Enterprise, "Login to continue", Toast.LENGTH_SHORT)
                                .show()

                            dialogue()
                            promptPopUpView?.changeStatus(2, "Registration was successful!")


                            Handler().postDelayed({
                                val intent = Intent(this@Enterprise, LoginActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()
                            }, 3000)

                        }




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


    private fun createProvider() {
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
        params["firstname"] = name!!
        params["lastname"] = other_name!!
        params["email"] = email!!
        params["mssdn"] = mssidn!!
        params["nature_response"] = selectedItem2!!
        params["userid"] = user_id!!
        params["town"] = town!!
        params["code"] = code!!
        params["physical_address"] = physical!!
        params["postal_address"] = postal!!
        params["county"] = county!!




        val api: CreateProvider = retrofit.create(CreateProvider::class.java)
        val call: Call<ResponseBody> = api.respon(params)

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

                    if (response.code() == 400) {
                        mProgress?.dismiss()

                        dialogue_error()
                        promptPopUpView?.changeStatus(1, "Unable to create Provider! please try again")
                    } else if (response.code() == 200) {

                            mProgress?.dismiss()

                            Toast.makeText(this@Enterprise, "Login to continue", Toast.LENGTH_SHORT)
                                .show()

                            dialogue()
                            promptPopUpView?.changeStatus(2, "Registration was successful!")


                            Handler().postDelayed({
                                val intent = Intent(this@Enterprise, LoginActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()
                            }, 3000)

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

        promptPopUpView = PromptPopUpView(this)

        AlertDialog.Builder(this)


            .setPositiveButton(
                "PROCESSING....."
            ) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
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

    override fun onBackPressed() {
        val intent = Intent(this@Enterprise, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}

