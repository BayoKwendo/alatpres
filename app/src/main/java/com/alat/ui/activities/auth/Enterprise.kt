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
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.afollestad.materialdialogs.MaterialDialog
import com.alat.BasicUserActivity
import com.alat.R
import com.alat.adapters.CountriesArrayListAdapter
import com.alat.adapters.CountriesListAdapter
import com.alat.adapters.MultiSelectionSpinner
import com.alat.helpers.Constants
import com.alat.helpers.PromptPopUpView
import com.alat.helpers.Utils
import com.alat.interfaces.CreateEnterprise
import com.alat.interfaces.CreateProvider
import com.alat.model.TextViewDatePicke
import com.google.android.material.textfield.TextInputLayout
import fr.ganfra.materialspinner.MaterialSpinner
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
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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
    private var clients: String? = null
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
    private var termsMaterialDialog: MaterialDialog? = null

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

    private val ITEMS3= arrayOf("Monthly ksh. 300", "Quarterly Ksh. 3400", "Yearly Ksh. 44,100")
    private val ITEMS1 = arrayOf("YES", "NO")
    private val ITEMS2 = arrayOf("ddjdj", "fjfjf")
    var spinner: MaterialSpinner? = null
    var spinner_3: MaterialSpinner? = null
    var selectedItem: String? = null
    var selectedItem2: String? = null
    var selectedItem3: String? = null
    var account_status: String? = null
    private var backText: TextView? = null
    private var mSearchableSpinner1: SearchableSpinner? = null
    private var mSimpleListAdapter1: CountriesListAdapter? = null
    private var mSimpleArrayListAdapter1: CountriesArrayListAdapter? = null
    private var countries: String? = null
    private val mCountries: ArrayList<String> = ArrayList()
    private var pw: PopupWindow? = null
    private var expanded = false
    var mySpinner: MultiSelectionSpinner? = null
    var listString: String? = null
    var listString2: String? = null
    var mySpinner1: MultiSelectionSpinner? = null
    var myLinear: LinearLayout? = null
    var myLinear1: LinearLayout? = null
    var checkSelected: BooleanArray? = null
    var selectedItemss: ArrayList<String>? = null
    var selectedItemCountry: ArrayList<String>? = null
    var pref: SharedPreferences? = null

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

        mySpinner = findViewById(R.id.nature_response)
        mySpinner1 = findViewById(R.id.country_operation)
        myLinear1 = findViewById(R.id.country_operation1)
        myLinear = findViewById(R.id.nature_response1)

        init()
        linearLayout = findViewById(R.id.main_view)
        frameLayout = findViewById(R.id.next_view)
        mSimpleListAdapter1 = CountriesListAdapter(this, mCountries)

        mSimpleArrayListAdapter1 = CountriesArrayListAdapter(this, mCountries)

        mSearchableSpinner1 = findViewById<View>(R.id.SearchableSpinner1) as SearchableSpinner

        btn_next = findViewById(R.id.buttonNext)
        btn_next!!.setOnClickListener {
            if (!checkError()) return@setOnClickListener
            else {
                linearLayout!!.visibility = View.INVISIBLE
                frameLayout!!.visibility = View.VISIBLE
            }
        }


        val privacy_policy: TextView =
            findViewById(R.id.privacy_text)

        //        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(getPhoneInput(), InputMethodManager.SHOW_IMPLICIT);
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
                loadPolicy()
            }

            override fun updateDrawState(textPaint: TextPaint) {
                textPaint.color = textPaint.linkColor // you can use custom color
                textPaint.isUnderlineText = false // this remove the underline
            }
        }, spanText.length - policy.length, spanText.length, 0)

        privacy_policy.movementMethod = LinkMovementMethod.getInstance()
        privacy_policy.setText(spanText, TextView.BufferType.SPANNABLE)


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


                    return
                } else {
                    selectedItem = spinner!!.selectedItem.toString()

                    if (selectedItem == "YES") {
                        mySpinner!!.visibility = View.VISIBLE
                        mySpinner1!!.visibility = View.VISIBLE
                        myLinear!!.visibility = View.VISIBLE
                        myLinear1!!.visibility = View.VISIBLE

                    //    Toast.makeText(this@Enterprise, "Please" + , Toast.LENGTH_LONG).show();

                    } else {
                        mySpinner!!.visibility = View.GONE
                        mySpinner1!!.visibility = View.GONE
                        myLinear1!!.visibility = View.GONE
                        myLinear!!.visibility = View.GONE

                    }
                    // Toast.makeText(this@NFCWrite, tv, Toast.LENGTH_LONG).show();
                }
                // TODO Auto-generated method stub
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        }


        //Adapters
        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, ITEMS3)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_3 = findViewById<View>(R.id.package_sub) as MaterialSpinner
        spinner_3?.adapter = adapter2
        spinner_3!!.isSelected = false;  // otherwise listener will be called on initialization
        spinner_3!!.setSelection(0, true)
        spinner_3?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                arg0: AdapterView<*>?, arg1: View?,
                arg2: Int, arg3: Long
            ) {
                if (spinner_3!!.selectedItem == null) {
                   // Toast.makeText(this@Enterprise, "Please select an Alert Type", Toast.LENGTH_LONG).show();
                    return
                }else{
                    selectedItem3 = spinner_3!!.selectedItem.toString()

                   if(selectedItem3 == "ALATPRES BASIC"){
                       account_status = "0"
                   }else if(selectedItem3 == "ALATPRES ENTERPRISE"){
                       account_status = "1"
                   }
                }
             }
                // TODO Auto-generated method stub

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        }


        val adapter1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, ITEMS2)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        btn_submit = findViewById(R.id.submit)

        btn_submit!!.setOnClickListener {
            if (!checkContactError()) return@setOnClickListener
            else {
                createUser()
                mProgress!!.show()
            }
        }


        mSearchableSpinner1!!.setAdapter(mSimpleListAdapter1)
        mSearchableSpinner1!!.setOnItemSelectedListener(mOnItemSelectedListener1)
        mSearchableSpinner1!!.setStatusListener(object : IStatusListener {
            override fun spinnerIsOpening() {
            }

            override fun spinnerIsClosing() {}
        })


//        btn_next?.findViewById<Button>(R.id.buttonNext)?.setOnClickListener {
//          //  btn_next.toggleVisibility()
//
//        }
        parseEntityDatas()



        country()
        initialize()


    }

    private fun loadPolicy() {
        val builder: MaterialDialog.Builder = MaterialDialog.Builder(this@Enterprise)
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

            webView.loadUrl("file:///android_res/raw/policy.html")

        } catch (e: Throwable) {
            webView.loadData(
                "<h1>Unable to load</h1><p>" + e.localizedMessage + "</p>", "text/html",
                "UTF-8"
            )
        }
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

//                textInputcounties!!.isVisible = countries == "Kenya"
            } }
        override fun onNothingSelected() {
            Toast.makeText(this@Enterprise, "Nothing Selected", Toast.LENGTH_SHORT).show() }
    }


    private fun loadTerms() {


        val builder: MaterialDialog.Builder = MaterialDialog.Builder(this)

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


    private fun initialize() {
        //data source for drop-down list
        val items = java.util.ArrayList<String>()
        items.add("General Response Service")
        items.add("Medical")
        items.add("Law  Enforcement  &Security")
        items.add("Ambulance and Other  Transport")
        items.add("Search,Rescue and Evacuation")
        items.add("Telehealth")
        items.add("Fire")
        items.add("Disease Control&Prevention")
        items.add("Food and Water Safety")
        items.add("Highway Emergencies")
        items.add("Professional Volunteering")
        items.add("Cyber Crime")
        items.add("Unmaned Aerial Vehicle  Services")
        items.add("Power and Electrical Emergency")
        items.add("Communication Network Backup")
        items.add("Emergency Veterinary Services")
        items.add("Others")

            mySpinner!!.setItems(items)
            //data source for drop-down list
            selectedItemss = mySpinner!!.selectedItems

            listString =   TextUtils.join(", ", mySpinner!!.selectedItems)


            mySpinner!!.setSelection(selectedItemss);

    }

    private fun country() {
        try {
            val items = java.util.ArrayList<String>()
            val myJson =  inputStreamToString(this.resources.openRawResource(R.raw.counties))
            Log.i("jsonbayo", myJson)
            val jArray = JSONArray(myJson.toString())
            for (i in 0 until jArray.length()) {
                val json_obj = jArray.getJSONObject(i)
                items.add(json_obj!!.getString("county"))
                mySpinner1!!.setItems(items)
                //data source for drop-down list
                selectedItemCountry = mySpinner1!!.selectedItems
                listString2 =   TextUtils.join(", ", mySpinner1!!.selectedItems)



                mySpinner1!!.setSelection(selectedItemCountry);
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

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
            val intent = Intent(this@Enterprise, BasicUserActivity::class.java)
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
        clients = textInputnclientEdit!!.text.toString().trim { it <= ' ' }
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
        if(county == ""){
            county = "NONE"
        }
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

//        if (Utils.checkIfEmptyString(tax)) {
//            textInputtax!!.error = "Tax ID is mandatory"
//            textInputtax!!.requestFocus()
//            showKeyBoard()
//            return false
//        } else textInputtax!!.error = null
//        if (Utils.checkIfEmptyString(dob)) {
//            textInputdob!!.error = "Date of Incorporation is mandatory"
//            textInputdob!!.requestFocus()
//            showKeyBoard()
//            return false
//        } else textInputdob!!.error = null

//        if (Utils.checkIfEmptyString(nature)) {
//            textInputnature!!.error = "Nature of your org is mandatory"
//            textInputnature!!.requestFocus()
//            showKeyBoard()
//            return false
//        } else textInputnature!!.error = null

//        if (Utils.checkIfEmptyString(clients)) {
//            textInputnclient!!.error = "Client level is mandatory"
//            textInputnclient!!.requestFocus()
//            showKeyBoard()
//            return false
//        } else textInputnclient!!.error = null
//        if (Utils.checkIfEmptyString(employee)) {
//            Toast.makeText(this, "Employee number is Mandatory", Toast.LENGTH_SHORT).show()
//            textInputemply!!.error = "Gender is mandatory"
//            textInputemply!!.requestFocus()
//            showKeyBoard()
//            return false
//        } else textInputemply!!.error = null


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

//        if (Utils.checkIfEmptyString(postal)) {
//            textInputpostal!!.error = "Postal address is mandatory"
//            textInputpostal!!.requestFocus()
//            showKeyBoard()
//            return false
//        } else textInputpostal!!.error = null
//        if (Utils.checkIfEmptyString(town)) {
//            textInputtown!!.error = "Town is mandatory"
//            textInputtown!!.requestFocus()
//            showKeyBoard()
//            return false
//        } else textInputtown!!.error = null
//        if (Utils.checkIfEmptyString(code)) {
//            textInputcode!!.error = "Date of Incorporation is mandatory"
//            textInputcode!!.requestFocus()
//            showKeyBoard()
//            return false
//        } else textInputcode!!.error = null
        if (Utils.checkIfEmptyString(mssidn)) {
            textInputmssidn!!.error = "phone number is mandatory"
            textInputmssidn!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputmssidn!!.error = null

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

        if (password!!.length < 4) {
            Toast.makeText(this, "Password must be at least four characters", Toast.LENGTH_SHORT)
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
            Log.i("jsonbayo", myJson)

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


    private fun createUser() {
        registerVar()
        var date: String? = null
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val c = Calendar.getInstance()
        c.add(Calendar.DATE, 30)
        date = dateFormat.format(c.time)
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
        params["taxid"] = "333"
        params["nature_org"] = "nature"
        params["description"] = "desc"
        params["physical_address"] = physical!!
        params["postal_address"] = postal!!
        params["town"] = "town"
        params["clients"] = "3"
        params["code"] = "233"
        params["website"] = "web"
        params["country"] = countries!!
        params["date_of_incooperation"] = "Date"
        params["mssdn2"] = date!!
        params["mssdn"] = mssidn!!
        params["account_status"] = "1"
        params["response_provider"] = selectedItem!!
        params["nature_response"] = mySpinner!!.buildSelectedItemString()!!
        params["county"] = mySpinner1!!.buildSelectedItemString()!!
        params["userid"] = user_id!!
        params["date_now"] = date
        params["password"] = password!!


        val api: CreateEnterprise = retrofit.create(CreateEnterprise::class.java)
        val call: Call<ResponseBody> = api.EnterCreate(params)

        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                //Toast.makeText()

                Log.d("Call request", call.request().toString());
                Log.d("Response raw header", response.headers().toString());
                Log.d("Response raw", response.toString());
                Log.d("Response code", response.code().toString());


                if (response.isSuccessful) {
//                    val remoteResponse = response.body()!!.string()
//                    Log.d("test", remoteResponse)

                    if (response.code() == 201) {
                        mProgress?.dismiss()

                        dialogue_error()
                        promptPopUpView?.changeStatus(1, "Email already taken!!")
                    }
                    if (response.code() == 204){
                        mProgress?.dismiss()

                        dialogue_error()
                        promptPopUpView?.changeStatus(1, "Phone already taken!!")
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


                        if (selectedItem == "YES") {
                            createProvider()
                        } else {
                            pref =
                                applicationContext.getSharedPreferences("ADS", 0) // 0 - for private mode

                            val editor2: SharedPreferences.Editor = pref!!.edit()
                            editor2.putString("ads", "0")
                            editor2.clear()
                            editor2.apply()

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
        params["nature_response"] =mySpinner!!.buildSelectedItemString()!!
        params["userid"] = user_id!!
        params["town"] = "None"
        params["county_operation"] = mySpinner1!!.buildSelectedItemString()!!
        params["code"] = "000"
        params["physical_address"] = physical!!
        params["postal_address"] = "NONE"
        params["county"] = "NONE"




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
                        promptPopUpView?.changeStatus(
                            1,
                            "Unable to create Provider! please try again"
                        )
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
        val intent = Intent(this@Enterprise, BasicUserActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}

