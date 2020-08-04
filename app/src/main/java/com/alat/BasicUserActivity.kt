package com.alat

import adil.dev.lib.materialnumberpicker.dialog.CountyDialog
import adil.dev.lib.materialnumberpicker.dialog.GenderDialogue
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
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.afollestad.materialdialogs.MaterialDialog
import com.alat.adapters.CountriesArrayListAdapter
import com.alat.adapters.CountriesListAdapter
import com.alat.helpers.Constants
import com.alat.helpers.PromptPopUpView
import com.alat.helpers.Utils
import com.alat.interfaces.CreateUser
import com.alat.model.PreferenceModel
import com.alat.model.TextViewDatePicker
import com.alat.ui.activities.enterprise.AddClientActivitity
import com.alat.ui.activities.auth.LoginActivity
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class BasicUserActivity : AppCompatActivity() {

    private var textInputfirstName: TextInputLayout? = null
    private var textInputlastname: TextInputLayout? = null
    private var textInputgender: TextInputLayout? = null
    private var textInputdob: TextInputLayout? = null
    private var textInputcounties: TextInputLayout? = null
    private var textInputemail: TextInputLayout? = null
    private var textInputmssidn: TextInputLayout? = null
    private var textInputidno: TextInputLayout? = null
    private var textInputuserid: TextInputLayout? = null
    private var textInputconfirmuserid: TextInputLayout? = null
    private var textInputpassword: TextInputLayout? = null
    private var textInputconfirmpassword: TextInputLayout? = null
    private var textInputDoB: EditText? = null
    private var genderselect: EditText? = null
    private var mcounty: EditText? = null
    private var firstName: String? = null
    private var lastName: String? = null
    private var email: String? = null
    private var mssidn: String? = null
    private var id: String? = null
    private  var dob: String? = null
    private var gender: String? = null
    private var county: String? = null
    private var password: String? = null
    private var confirmpassword: String? = null
    private var user_id: String? = null
    private var confirm_user_id: String? = null
    private var promptPopUpView: PromptPopUpView? = null
    private var mProgress: ProgressDialog? = null
    var prefs: SharedPreferences? = null
    private var btn_register: Button? = null
    private var preferenceHelper: PreferenceModel? = null

    private var enterprs: TextView? = null
    private var termsMaterialDialog: MaterialDialog? = null

    private var mSearchableSpinner1: SearchableSpinner? = null
    private var mSimpleListAdapter1: CountriesListAdapter? = null
    private var mSimpleArrayListAdapter1: CountriesArrayListAdapter? = null

    private var countries: String? = null

    private val mCountries: ArrayList<String> = ArrayList()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)
        preferenceHelper = PreferenceModel(this)

        mSimpleListAdapter1 = CountriesListAdapter(this, mCountries)

        mSimpleArrayListAdapter1 = CountriesArrayListAdapter(this, mCountries)

        mSearchableSpinner1 = findViewById<View>(R.id.SearchableSpinner1) as SearchableSpinner
        val privacy_policy: TextView =
            findViewById(R.id.privacy_text)
        val terms = " Terms and Conditions "
        val policy = " Privacy Policy "
        val spanText = SpannableStringBuilder()
        spanText.append("By clicking SUBMIT, you agree to all our")
        spanText.append(terms)
        spanText.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                loadTerms()
            }
            override fun updateDrawState(textPaint: TextPaint) {
                textPaint.color = textPaint.linkColor // you can use custom color
                textPaint.isUnderlineText = false // this remove the underline
            }
        }, spanText.length - terms.length, spanText.length, 0)
        spanText.append("and that you have read & understood our")
        spanText.append(policy)
        spanText.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                loadTerms()
            }
            override fun updateDrawState(textPaint: TextPaint) {
                textPaint.color = textPaint.linkColor // you can use custom color
                textPaint.isUnderlineText = false // this remove the underline
            }
        }, spanText.length - policy.length, spanText.length, 0)
        privacy_policy.movementMethod = LinkMovementMethod.getInstance()
        privacy_policy.setText(spanText, TextView.BufferType.SPANNABLE)
        mProgress = ProgressDialog(this)
        mProgress!!.setMessage("Creating user...")
        mProgress!!.setCancelable(true)
        init()
        mSearchableSpinner1!!.setAdapter(mSimpleListAdapter1)
        mSearchableSpinner1!!.setOnItemSelectedListener(mOnItemSelectedListener1)
        mSearchableSpinner1!!.setStatusListener(object : IStatusListener {
            override fun spinnerIsOpening() {
            }
            override fun spinnerIsClosing() {}
        })
        parseEntityDatas()
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!mSearchableSpinner1!!.isInsideSearchEditText(event)) {
            mSearchableSpinner1!!.hideEdit()
        }
             return super.onTouchEvent(event)
    }

    private val mOnItemSelectedListener1: OnItemSelectedListener = object : OnItemSelectedListener {
        override fun onItemSelected(view: View?, position: Int, id: Long) {
            mSearchableSpinner1!!.visibility = View.VISIBLE
            if(position > 0){
                textInputcounties = findViewById(R.id.counties_layout)
                countries = mSimpleListAdapter1!!.getItem(position).toString()

                textInputcounties!!.isVisible = countries == "Kenya"
            } }
        override fun onNothingSelected() {
            Toast.makeText(this@BasicUserActivity, "Nothing Selected", Toast.LENGTH_SHORT).show() }
    }

    fun init() {

        textInputfirstName = findViewById(R.id.first_name)
        textInputlastname = findViewById<TextInputLayout>(R.id.last_name)
        textInputemail = findViewById(R.id.email)
        textInputidno = findViewById<TextInputLayout>(R.id.id)
        textInputgender = findViewById(R.id.genderlayout)
        textInputdob = findViewById(R.id.date_layout)
        enterprs = findViewById(R.id.enterprise)
        textInputuserid = findViewById(R.id.user_id)
        textInputconfirmuserid = findViewById(R.id.confirm_user_id)
        textInputpassword = findViewById(R.id.password)
        textInputconfirmpassword = findViewById(R.id.confirm_password)
        textInputcounties = findViewById<TextInputLayout>(R.id.counties_layout)
        genderselect = findViewById(R.id.gender)
        mcounty = findViewById(R.id.counties)
        textInputmssidn = findViewById(R.id.phone)
        textInputDoB = findViewById(R.id.date)
        btn_register = findViewById(R.id.buttonLogin)
        btn_register!!.setOnClickListener {
            if (!checkError()) return@setOnClickListener
            else {
               mProgress!!.show()
               createUser()
             }
        }
        enterprs!!.setOnClickListener {
            val intent = Intent(this@BasicUserActivity, AddClientActivitity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()  }

        TextViewDatePicker(this@BasicUserActivity, textInputDoB!!)
        genderselect?.setOnClickListener(View.OnClickListener { v: View? ->
            genderselect?.clearFocus()
            genderselect?.isFocusable = false
            genderselect?.isCursorVisible = false
            genderselect?.keyListener = null
            val dialog = GenderDialogue(this@BasicUserActivity)
            dialog.setOnSelectingGender { value -> genderselect?.setText(value) }
            dialog.show()
        })
        mcounty?.setOnClickListener(View.OnClickListener { v: View? ->
            mcounty?.clearFocus()
            mcounty?.isFocusable = false
            val dialog = CountyDialog(this@BasicUserActivity)
            dialog.setOnSelectingcounty { value -> mcounty?.setText(value) }
            dialog.show()
        })

    }

    private fun loadTerms() {


        val builder: MaterialDialog.Builder = MaterialDialog.Builder(this@BasicUserActivity)

            .customView(R.layout.dialog_webview, false)
            .cancelable(false)
            .positiveText(R.string.dismiss)
            .onPositive({ _, which -> termsMaterialDialog!!.dismiss() })
        termsMaterialDialog = builder.build()
        termsMaterialDialog!!.show()
        val webView: WebView =
            termsMaterialDialog!!.customView!!.findViewById(R.id.webview)
        try {

            // Load from changelog.html in the assets folder
            val json: String = resources.openRawResource(R.raw.terms).bufferedReader().use { it.readText() }

            Log.d("bayo", json)

             webView.loadUrl("file:///android_res/raw/terms.html")

        } catch (e: Throwable) {
            webView.loadData(
                "<h1>Unable to load</h1><p>" + e.localizedMessage + "</p>", "text/html",
                "UTF-8"
            )
        }
    }
    fun inputStreamToString(inputStream: InputStream): String? {
        return try {
            val bytes = ByteArray(inputStream.available())
            inputStream.read(bytes, 0, bytes.size)
            String(bytes)
        } catch (e: IOException) {
            null
        }
    }

    private fun parseEntityDatas() {

        try {
            val myJson =
                inputStreamToString(this.resources.openRawResource(R.raw.africancountries))

//            val br = BufferedReader(InputStreamReader(resources.openRawResource(R.raw.africancountries)))
//            var temp: String?
//            while (br.readLine().also { temp = it } != null) sb.append(temp)
            Log.i("jsonbayo", myJson )

            mCountries.clear()

            val jArray = JSONArray(myJson.toString())

            for (i in 0 until jArray.length()) {

               val json_obj = jArray.getJSONObject(i)

                mCountries.add(json_obj!!.getString("Country Name"))

            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun registerVar() {
        firstName = textInputfirstName!!.editText!!.text.toString().trim { it <= ' ' }
        lastName = textInputlastname?.editText?.text.toString().trim({ it <= ' ' })
        email = textInputemail!!.editText!!.text.toString().trim { it <= ' ' }
        mssidn = textInputmssidn?.editText?.text?.toString()?.trim({ it <= ' ' })
        id = textInputidno?.editText?.text?.toString()?.trim({ it <= ' ' })
        dob = textInputDoB!!.text.toString().trim { it <= ' ' }
        gender = genderselect!!.text.toString().trim { it <= ' ' }
        county = mcounty!!.text.toString().trim { it <= ' ' }
        password = textInputpassword?.editText?.text.toString().trim({ it <= ' ' })
        confirmpassword = textInputconfirmpassword?.editText?.text.toString().trim({ it <= ' ' })
        user_id = textInputuserid?.editText?.text.toString().trim({ it <= ' ' })
        confirm_user_id = textInputconfirmuserid?.editText?.text.toString().trim({ it <= ' ' })

        if(county == ""){
            county = "NONE"
        }

    }
  fun showKeyBoard(){
      val imm =
          getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
      imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }



    private fun checkError(): Boolean {

        registerVar()


        if (Utils.checkIfEmptyString(firstName)) {
            textInputfirstName!!.error = "FirstName is required"
            textInputfirstName!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputfirstName!!.error = null
        if (Utils.checkIfEmptyString(lastName)) {
            textInputlastname!!.error = "LastName is required"
            textInputlastname!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputlastname!!.error = null
        if (Utils.checkIfEmptyString(gender)) {
            Toast.makeText(this, "Gender is Mandatory", Toast.LENGTH_SHORT).show()
            textInputgender!!.error = "Gender is mandatory"
            textInputgender!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputgender!!.error = null
        if (Utils.checkIfEmptyString(dob)) {
            textInputdob!!.error = "Date of Birth is mandatory"
            textInputdob!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputdob!!.error = null
        if (Utils.checkIfEmptyString(email)) {
            textInputemail!!.error = "Email is mandatory"
            textInputemail!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputemail!!.error = null

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email!!).matches()) {
            textInputemail!!.error = "Enter a valid email!"
            textInputemail!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputemail!!.error = null
        if (Utils.checkIfEmptyString(mssidn)) {
            textInputmssidn!!.error = "Phone No. is mandatory"
            textInputmssidn!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputmssidn!!.error = null

        if (user_id!!.isEmpty()){
            textInputuserid!!.error = "Please set your user ID"
            textInputuserid!!.requestFocus()
            showKeyBoard()
            return false
        }else textInputuserid!!.error = null

        if (password!!.isEmpty()){
            textInputpassword!!.error = "Please set your password"
            textInputpassword!!.requestFocus()
            showKeyBoard()
            return false
        }else textInputpassword!!.error = null

        if (user_id!!.length < 4){
            Toast.makeText(this, "User ID must be at least four characters", Toast.LENGTH_SHORT).show()
            textInputuserid!!.error = "User ID must be at least four characters"
            textInputuserid!!.requestFocus()
            showKeyBoard()
            return false
        }else textInputuserid!!.error = null

        if (password!!.length < 6){
            Toast.makeText(this, "Password must be at least six characters", Toast.LENGTH_SHORT).show()
            textInputpassword!!.error = "User ID must be at least six characters"
            textInputpassword!!.requestFocus()
            showKeyBoard()
            return false
        }else textInputpassword!!.error = null

        if (user_id != confirm_user_id){
            Toast.makeText(this, "User ID do not match", Toast.LENGTH_SHORT).show()
            textInputconfirmuserid!!.error = "User ID do not match"
            textInputconfirmuserid!!.requestFocus()
            showKeyBoard()
            return false
        }else textInputconfirmuserid!!.error = null
        if (password != confirmpassword){
            Toast.makeText(this, "Password do not match", Toast.LENGTH_SHORT).show()
            textInputconfirmpassword!!.error = "Password do not match"
            textInputconfirmpassword!!.requestFocus()
            showKeyBoard()
            return false
        }else textInputconfirmpassword!!.error = null


        return true
    }

    private fun createUser() {

        registerVar()

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
        params["firstname"] = firstName!!
        params["lastname"] = lastName!!
        params["email"] = email!!
        params["gender"] = gender!!
        params["idNo"] = "0000"
        params["DoB"] = dob!!
        params["mssdn"] = mssidn!!
        params["country"] = countries!!
        params["county"] = county!!
        params["userid"] = user_id!!
        params["password"] = password!!

        val api: CreateUser = retrofit.create(CreateUser::class.java)
        val call: Call<ResponseBody> = api.UserCreate(params)

        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                //Toast.makeText()

                Log.d("Call request", call.request().toString());
//                Log.d("Response raw header", response.errorBody()!!.string());
                Log.d("Response raw", response.toString());
                Log.d("Response code", response.code().toString());


                if (response.isSuccessful) {
                    val remoteResponse = response.body()!!.string()
                    Log.d("test", remoteResponse)

                    if (response.code() == 201){
                        mProgress?.dismiss()

                        dialogue_error()
                        promptPopUpView?.changeStatus(1, "Email already taken!!")
                    }
                    if (response.code() == 202){
                        mProgress?.dismiss()

                        dialogue_error()
                        promptPopUpView?.changeStatus(1, "User ID already taken!!")
                    }
                    if (response.code() == 400){
                        mProgress?.dismiss()

                        dialogue_error()
                        promptPopUpView?.changeStatus(1, "Unable to create User! please try again")
                    }
                    else if(response.code() == 200){
                        mProgress?.dismiss()

                        Toast.makeText(this@BasicUserActivity, "Login to continue" , Toast.LENGTH_SHORT).show()

                        dialogue()
                        promptPopUpView?.changeStatus(2, "Registration was successful!")


                        Handler().postDelayed({
                            val intent = Intent(this@BasicUserActivity, LoginActivity::class.java)
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
            ) { dialog, _ -> dialog.dismiss()
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
                "Ok") { dialog, _ -> dialog.dismiss() }
           .setCancelable(false)
            .setView(promptPopUpView)
            .show() }

    override fun onBackPressed() {
        val intent = Intent(this@BasicUserActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
