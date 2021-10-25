package com.alat.ui.activities.auth

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.alat.BasicUserActivity
import com.alat.Permission
import com.alat.R
import com.alat.helpers.Constants
import com.alat.helpers.PromptPopUpView
import com.alat.interfaces.LoginInterface
import com.alat.ui.activities.enterprise.AddClientActivitity
import com.squareup.picasso.Picasso
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
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class LoginActivity : AppCompatActivity() {

    var join: TextView? = null
    var normal_user: TextView? = null
    var enterprise_user: TextView? = null
    var signin: Button? = null
    var email_edittext: EditText? = null
    var pin_edittext: EditText? = null
    private var mProgress: ProgressDialog? = null
    var pref: SharedPreferences? = null
    private var promptPopUpView: PromptPopUpView? = null
    var email: String? = null
    var password: String? = null
    var forgot: TextView? = null
    var image: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        email_edittext = findViewById(R.id.input_login_email)
        pin_edittext = findViewById(R.id.input_login_pin)
        signin = findViewById(R.id.btn_login)
        join = findViewById(R.id.joinalat)
        image = findViewById(R.id.imageview)
        join?.setOnClickListener {
            startActivity(Intent(this, LauncherActivity::class.java))
        }
        mProgress = ProgressDialog(this);
        mProgress!!.setMessage("Authenticating...");
        mProgress!!.setCancelable(true);
        signin!!.setOnClickListener {
            validation()
        }
//        Toast.makeText(this, "",    Toast.LENGTH_LONG).show()

        // Picasso.with(this).load().fit().into(image);
        Picasso.get().load("http://167.172.17.121/api/logo.png").into(image)
        pref =
            this.getSharedPreferences("MyPref", 0) // 0 - for private mode

        if (pref!!.getBoolean("isLogin", false) === true){
            val intent = Intent(this@LoginActivity, Permission::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        forgot = findViewById(R.id.tv_forgot_password)
        forgot?.setOnClickListener {
            startActivity(Intent(this, ForgotPassword::class.java))
        }

    }


    private fun validation() {
        email = email_edittext?.text.toString().trim();
        password = pin_edittext?.text.toString().trim();


        if (TextUtils.isEmpty(email)) {

            email_edittext?.error = "User ID Required";
            email_edittext?.requestFocus()
            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
            return;
        }
        if (TextUtils.isEmpty(password)) {
            pin_edittext?.error = "Password Required";
            pin_edittext?.isFocusable = true
            pin_edittext?.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

            return;
        }
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            if (!isNetworkAvailable()) {
                internet()
                promptPopUpView?.changeStatus(1, "Connection Error\n\n Check your internet connectivity")

            } else {
                mProgress?.show()
                signin!!.text = "Loading..."
                loginUser(email!!, password!!);
            }
        }
//            intent = Intent(this, NfcHome::class.java)
//            this.startActivity(intent)
    }

    private fun loginUser(email: String, password: String) {
        //RequestBody body = RequestBody.Companion.create(json, JSON)\\\

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
        params["password"] = password

        val api: LoginInterface = retrofit.create(LoginInterface::class.java)
        val call: Call<ResponseBody> = api.getUserLogin(params)

        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                //Toast.makeText()
                //Toast.makeText()

                Log.d("Call request", call.request().toString());
                Log.d("Response raw header", response.headers().toString());
                Log.d("Response raw", response.toString());
                Log.d("Response code", response.code().toString());


                if (response.isSuccessful) {
                    val remoteResponse = response.body()!!.string()
                    Log.d("test", remoteResponse)
                    parseLoginData(remoteResponse)
                } else {
                    mProgress?.dismiss()
                    dialogue_error();
                    promptPopUpView?.changeStatus(1, "Wrong credentials. Try again")
                    Log.d("BAYO", response.code().toString())
                    signin!!.text = "Login"
                    mProgress?.dismiss()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                signin!!.text = "Proceed"

                dialogue_error()
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                Log.i("onEmptyResponse", "" + t) //
                mProgress?.dismiss()
            }
        })
    }
    private fun parseLoginData(jsonresponse: String) {
        try {
            val jsonObject = JSONObject(jsonresponse)
            if (jsonObject.getBoolean("login")) {
                saveInfo(jsonresponse)
                dialogue();
                promptPopUpView?.changeStatus(2, "SUCCESSFUL")
                mProgress?.dismiss()
                Handler().postDelayed({
                    val intent = Intent(this@LoginActivity, Permission::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }, 3000)
            }else{
                mProgress?.dismiss()
                dialogue_error();
                promptPopUpView?.changeStatus(1, "Wrong credentials. Try again")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun saveInfo(response: String) {
        try {

            val jsonObject = JSONObject(response)
            pref =
                applicationContext.getSharedPreferences("MyPref", 0) // 0 - for private mode
            val editor: SharedPreferences.Editor = pref!!.edit()
            editor.putBoolean("isLogin", true)
            editor.putString("fname", jsonObject.getString("firstname"))
            editor.putString("lname", jsonObject.getString("lastname"))
            editor.putString("email", jsonObject.getString("email"))
            editor.putString("dob", jsonObject.getString("DoB"))
            editor.putString("role", jsonObject.getString("role_id"))
            editor.putString("mssdn", jsonObject.getString("mssdn"))
            editor.putString("gender", jsonObject.getString("gender"))
            editor.putString("idNo", jsonObject.getString("idNo"))
            editor.putString("county", jsonObject.getString("county"))
            editor.putString("userid", jsonObject.getString("userid"))
            editor.putString("mstatus", jsonObject.getString("status"))
            editor.putString("account_status", jsonObject.getString("account_status"))
            editor.putString("clients", jsonObject.getString("clients"))
            editor.putString("response_provider", jsonObject.getString("response_provider"))


            pref =
                applicationContext.getSharedPreferences("FIRSTCHECK", 0) // 0 - for private mode
            val editor6: SharedPreferences.Editor = pref!!.edit()
            editor6.putString("first_check", jsonObject.getString("first_check"))
            editor6.clear()
            editor6.apply()

            pref =
                applicationContext.getSharedPreferences("ADS_BASIC", 0) // 0 - for private mode
            val editor4: SharedPreferences.Editor = pref!!.edit()
            editor4.putString("ads_basic", "0")
            editor4.clear()
            editor4.apply()

            pref =
                applicationContext.getSharedPreferences("ADS_ENTER", 0) // 0 - for private mode
            val editor5: SharedPreferences.Editor = pref!!.edit()
            editor5.putString("ads_enter", "0")
            editor5.clear()
            editor5.apply()



            editor.clear()
            editor.apply()




        }  catch (e: JSONException) {
            e.printStackTrace()

            Log.d("ERRRRRO", e.toString())


        }


    }



    private fun isNetworkAvailable(): Boolean {
        // Using ConnectivityManager to check for Network Connection
        val connectivityManager = (this
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
        val activeNetworkInfo = connectivityManager
            .activeNetworkInfo
        return activeNetworkInfo != null
    }


    private fun join() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialog.setIcon(R.drawable.logo)
        alertDialog.setTitle("Join AlatPres")
        val inflater = this.layoutInflater
        val layout_pwd: View = inflater.inflate(R.layout.layout_dialogu_alatpres, null)
        alertDialog.setView(layout_pwd)
        val alert = alertDialog.create()
        normal_user = layout_pwd.findViewById<TextView>(R.id.normal_user)
        enterprise_user = layout_pwd.findViewById<TextView>(R.id.enterprise_user)
        normal_user?.setOnClickListener(View.OnClickListener {
            alert.dismiss()
            val dialog =
                AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("You want to join ALATPRES as a normal user")
                    .setPositiveButton(
                        "YES") { _: DialogInterface, _: Int ->
                        startActivity(Intent(this, BasicUserActivity::class.java))
                    }
                    .setNegativeButton(
                        "No"
                    ) { dialog, _ -> dialog.dismiss() }
                    .setCancelable(true)
                    .show()
            val btnPositive =
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val btnNegative =
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            val layoutParams =
                btnPositive.layoutParams as LinearLayout.LayoutParams
            layoutParams.weight = 10f
            btnPositive.layoutParams = layoutParams
            btnNegative.layoutParams = layoutParams
            btnPositive.setTextColor(resources.getColor(R.color.success))
            btnNegative.setTextColor(resources.getColor(R.color.error))
        })

        enterprise_user?.setOnClickListener(View.OnClickListener {
            alert.dismiss()
            val dialog =
                AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("You want to join ALATPRES as an Enterprise")
                    .setPositiveButton(
                        "YES") { _: DialogInterface, _: Int ->
                        startActivity(Intent(this, AddClientActivitity::class.java))

                    }
                    .setNegativeButton(
                        "No"
                    ) { dialog, _ -> dialog.dismiss() }
                    .setCancelable(true)
                    .show()
            val btnPositive =
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val btnNegative =
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            val layoutParams =
                btnPositive.layoutParams as LinearLayout.LayoutParams
            layoutParams.weight = 10f
            btnPositive.layoutParams = layoutParams
            btnNegative.layoutParams = layoutParams
            btnPositive.setTextColor(resources.getColor(R.color.success))
            btnNegative.setTextColor(resources.getColor(R.color.error))
        })

        alertDialog.setView(layout_pwd)
        alert.show()
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
                "Ok"
            ) { dialog, _ -> dialog.dismiss()
            }


            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
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
}


