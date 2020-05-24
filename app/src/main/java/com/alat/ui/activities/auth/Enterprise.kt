package com.alat.ui.activities.auth

import adil.dev.lib.materialnumberpicker.dialog.CountyDialog
import adil.dev.lib.materialnumberpicker.dialog.EmployDialogue
import adil.dev.lib.materialnumberpicker.dialog.OrgDialogue
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import com.alat.R
import com.alat.helpers.Utils
import com.alat.model.TextViewDatePicker
import com.google.android.material.textfield.TextInputLayout

class Enterprise : AppCompatActivity() {
    var btn_next: Button? = null
    var linearLayout: LinearLayoutCompat?=null
    var frameLayout: FrameLayout?=null
    private var textInputname: TextInputLayout? = null
    private var textInputothername: TextInputLayout? = null
    private var textInputtax: TextInputLayout? = null
    private var textInputdob: TextInputLayout? = null
    private var textInputcounties: TextInputLayout? = null
    private var mcounty: EditText? = null
    private var textInputnature: TextInputLayout? = null
    private var textInputselectednature: EditText? = null
    private var textInputnclient: TextInputLayout? = null
    private var textInputemply: TextInputLayout? = null
    private var textInputselectedemploy: EditText? = null
    private var textInputdescription: TextInputLayout? = null
    private var textInputDoB: EditText? = null

    private  var name: String? = null
    private var other_name: String? = null
    private var nature: String? = null
    private var tax: String? = null
    private  var client: String? = null
    private  var employee: String? = null
    private var description: String? = null
    private  var dob: String? = null
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


    private var physical: String? = null
    private var postal: String? = null
    private var email: String? = null
    private var code: String? = null
    private var mssidn: String? = null
    private var mssidn2: String? = null
    private var town: String? = null
    private var website: String? = null
    private var btn_submit: Button? = null


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
                frameLayout!!.visibility  = View.VISIBLE
             }
        }

        btn_submit = findViewById(R.id.submit)

        btn_submit!!.setOnClickListener {
            if (!checkContactError()) return@setOnClickListener
            else {
                Toast.makeText(this, "Submitted", Toast.LENGTH_SHORT).show()
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
        textInputnclient = findViewById(R.id.client)

        //contact
        textInputemail = findViewById(R.id.email)
        textInputmssidn = findViewById(R.id.mobile)
        textInputmssidn2 = findViewById(R.id.mobile2)
        textInputphysicaladdress = findViewById(R.id.physical_address)
        textInputpostal = findViewById(R.id.postal_address)
        textInputtown = findViewById(R.id.town)
        textInputwebsite = findViewById(R.id.website)
        textInputcode = findViewById(R.id.code)

        btn_next = findViewById(R.id.buttonNext)
        btn_next!!.setOnClickListener {
            if (!checkError()) return@setOnClickListener
            else {
                Toast.makeText(this, "WOW", Toast.LENGTH_SHORT).show()
            }
        }

        TextViewDatePicker(this, textInputDoB!!)

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

    }


    private fun registerVar() {
        name = textInputname!!.editText?.text.toString().trim { it <= ' ' }
        client = textInputnclient!!.editText?.text.toString().trim { it <= ' ' }
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


    }

    private fun showKeyBoard(){
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
        if (Utils.checkIfEmptyString(description)) {
            textInputdescription!!.error = "Description is mandatory"
            textInputdescription!!.requestFocus()
            showKeyBoard()
            return false
        } else textInputdescription!!.error = null

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

        return true
    }



//    override fun onBackPressed() {
//        linearLayout?.visibility = View.VISIBLE
//        frameLayout?.visibility  = View.INVISIBLE
//    }
}

