package com.alat


import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import cn.pedant.SweetAlert.SweetAlertDialog
import com.alat.helpers.Admob
import com.alat.helpers.Constants
import com.alat.helpers.PromptPopUpView
import com.alat.interfaces.FindTime
import com.alat.interfaces.FriendInivte
import com.alat.ui.activities.ResponseProviders
import com.alat.ui.activities.auth.LoginActivity
import com.alat.ui.activities.enterprise.AddClientActivitity
import com.alat.ui.activities.enterprise.AddStation
import com.alat.ui.fragments.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.navigation.NavigationView
import io.karn.notify.Notify
import libs.mjn.prettydialog.PrettyDialog
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
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit


class HomePage : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener
{
    private var toolbar: androidx.appcompat.widget.Toolbar? = null
    private var promptPopUpView: PromptPopUpView? = null

    var navigationView: NavigationView? = null
    var linear2:LinearLayout ? = null
    var date2: String? = null

    var linear:LinearLayout ? = null
    var firstname:kotlin.String? = null
    var email:kotlin.String? = null
    var sname:kotlin.String? = null
    var dob:kotlin.String? = null
    var gender:kotlin.String? = null
    var mssdn:kotlin.String? = null
    var idNo:kotlin.String? = null
    var county:kotlin.String? = null
    var clients:kotlin.String? = null
    var responseprovider:kotlin.String? = null

    private var backPressedTime: Long = 0
    var pref: SharedPreferences? = null
    var pref2: SharedPreferences? = null

    private var userid: String? = null
    var check_first: String? = null

    var fname: String? = null
    private var account: String? = null
    var view: View? = null
    private var mAdView: AdView? = null
    private var roleID: String? = null
    private var ADS: String? = null
    var mstatus: String? = null
    var adsstatus: String? = null
    var adsstatus2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer_home)

        toolbar = findViewById(R.id.toolbar)
        toolbar!!.title = "FRAFF"
        supportActionBar
        setSupportActionBar(toolbar)
        pref =
            this.getSharedPreferences("MyPref", 0) // 0 - for private mode
        userid = pref!!.getString("userid", null)
        account = pref!!.getString("account_status", null)
        roleID = pref!!.getString("role", null)
        responseprovider = pref!!.getString("response_provider", null)
        firstname = pref!!.getString("fname", null)
        sname = pref!!.getString("lname", null)
        email = pref!!.getString("email", null)
        dob = pref!!.getString("dob", null)
        gender = pref!!.getString("gender", null)
        mssdn = pref!!.getString("mssdn", null)
        idNo = pref!!.getString("idNo", null)
        county = pref!!.getString("county", null)
        clients = pref!!.getString("clients", null)
        mstatus = pref!!.getString("mstatus", null)

        pref =
            this.getSharedPreferences("FIRSTCHECK", 0) // 0 - for private mode

        check_first = pref!!.getString("first_check", null)




//        Toast.makeText(this@HomePage, pref!!.getString("first_check", null), Toast.LENGTH_LONG).show();

//        if (roleID == "2") {
        if (account == "1" || mstatus == "0") {
            getExpiry()
        }

        pref =
            this.getSharedPreferences("ADS_ENTER", 0) // 0 - for private mode
        adsstatus = pref!!.getString("ads_enter", null)

        pref =
            this.getSharedPreferences("ADS_BASIC", 0) // 0 - for private mode
        adsstatus2 = pref!!.getString("ads_basic", null)

        if (adsstatus == "0" || adsstatus2 == "0") {

            pref2 =
                this.getSharedPreferences("ADS", 0) // 0 - for private mode
            ADS = pref2!!.getString("ads", null)

            if (ADS == "0") {
                MobileAds.initialize(this, "ca-app-pub-1439472385814796/1524804272"); //TEST KEY
                view = window.decorView.rootView;

                Admob.createLoadBanner(applicationContext, view);
                Admob.createLoadInterstitial(applicationContext, null);

                mAdView = findViewById<View>(R.id.adView) as AdView?
                val adRequest: AdRequest = AdRequest.Builder().build()
                mAdView!!.loadAd(adRequest)

            }
        }


        if (account == "0") {
            setSupportActionBar(toolbar)


//
        }
//
        getStudent()
        if (!isNetworkAvailable()) {
            internet()
            promptPopUpView?.changeStatus(
                1,
                "Connection Error\n\n Check your internet connectivity"
            )

        }
        initDrawer()

//        Toast.makeText(
//            this,
//            roleID ,
//            Toast.LENGTH_LONG
//        ).show()

    }


    private fun initDrawer() {
        val drawer =
            findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val toggle =
            ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
         navigationView =
             findViewById<View>(R.id.nav_view) as NavigationView
        navigationView!!.setNavigationItemSelectedListener(this)
        val alert = navigationView!!.getMenu().findItem(R.id.alert);
        val create_resp = navigationView!!.getMenu().findItem(R.id.create_resp);
        val create_respRG = navigationView!!.getMenu().findItem(R.id.create_respRG);
        val join_resp = navigationView!!.getMenu().findItem(R.id.join_resp);
        val exit_resp = navigationView!!.getMenu().findItem(R.id.exit_resp);
        val generate_rep = navigationView!!.getMenu().findItem(R.id.generate_rep);
        val manage_alert = navigationView!!.getMenu().findItem(R.id.manage_alert);
        val profile = navigationView!!.getMenu().findItem(R.id.profile);
        val SetupGr = navigationView!!.menu.findItem(R.id.SetupGr);
        val station_rg = navigationView!!.getMenu().findItem(R.id.station_rg);
        val alert_entr = navigationView!!.getMenu().findItem(R.id.alert_entr);
        val create_station = navigationView!!.getMenu().findItem(R.id.create_station);
        val create_client = navigationView!!.getMenu().findItem(R.id.create_client);
        val client_grp = navigationView!!.getMenu().findItem(R.id.client_grp);
        val resp_manage = navigationView!!.getMenu().findItem(R.id.resp_manage);
        val logout_alert = navigationView!!.getMenu().findItem(R.id.sign_out);
        val join_resp_ent = navigationView!!.getMenu().findItem(R.id.join_resp_ent);
        val random = navigationView!!.getMenu().findItem(R.id.random_client);

        navigationView!!.getMenu().setGroupVisible(R.id.SetupGroup, false);

        if (roleID == "1") {

            navigationView!!.menu.performIdentifierAction(R.id.alert, 0);
            navigationView!!.getMenu().setGroupVisible(R.id.SetupGroup, false);
            alert_entr.isVisible = false
            create_station.isVisible = false
            create_client.isVisible = false
            client_grp.isVisible = false
            resp_manage.isVisible = false
            join_resp_ent.isVisible = false
            alert.isVisible = true
            station_rg.isVisible = false
            create_resp.isVisible = true
            create_respRG.isVisible = false
            join_resp.isVisible = true
            random.isVisible = false
            exit_resp.isVisible = true
            generate_rep.isVisible = true
            manage_alert.isVisible = true
            profile.isVisible = true
            SetupGr.isVisible = true

        }
        else if (roleID == "2") {
            if (account == "0") {
                navigationView!!.menu.performIdentifierAction(R.id.alert_entr, 0);

            }else if (account == "1" || mstatus == "0") {
                navigationView!!.menu.performIdentifierAction(R.id.alert_entr, 0);
            }

            navigationView!!.menu.getItem(0).isChecked = true;
            alert.isVisible = false
            create_resp.isVisible = false
            create_respRG.isVisible = true

            join_resp.isVisible = false
            station_rg.isVisible = true
            exit_resp.isVisible = false
            generate_rep.isVisible = true
            manage_alert.isVisible = false
            join_resp_ent.isVisible = true
            SetupGr.isVisible =  false
            profile.isVisible = true
            random.isVisible = true

            logout_alert.isVisible = false
            SetupGr.isVisible = true
            alert_entr.isVisible = true
            exit_resp.isVisible = true
            create_station.isVisible = true
            create_client.isVisible = true
            client_grp.isVisible = true
            resp_manage.isVisible = true
        }


        if (responseprovider == "NO") {
            create_client.isVisible = false
            client_grp.isVisible = false
            random.isVisible = false
        }

        val navigationHeaderView = navigationView!!.getHeaderView(0)
        val tvName =
            navigationHeaderView.findViewById<View>(R.id.tvDriverName) as TextView
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        val bayo = navigationView!!.getMenu().findItem(R.id.invite_friend);
        val faq = navigationView!!.getMenu().findItem(R.id.faqs);
        val join = navigationView!!.getMenu().findItem(R.id.join);
        val resp_db = navigationView!!.getMenu().findItem(R.id.resp_db);
        val support = navigationView!!.getMenu().findItem(R.id.support);
        val feedback = navigationView!!.getMenu().findItem(R.id.feedback);
        navigationView!!.getMenu().setGroupVisible(R.id.SetupGroup, false);

        bayo.setVisible(false)
        faq.isVisible = false
        join.setVisible(false)
        resp_db.isVisible = false
        support.isVisible = false
        feedback.isVisible = false

        var fragment: Fragment? = null

        when (item.itemId) {
            R.id.alert -> {
                fragment = alerts()
                toolbar!!.title = "Alats";
            }
            R.id.alert_entr -> {
                    fragment = Alert_Enterpris()
                    toolbar!!.title = "Local Rgs Alats";

            }

            R.id.intergration -> {
                subscribe()
                promptPopUpView?.changeStatus(
                    3,
                    "NO DATA"
                )
            }

            R.id.join_resp_ent -> {
                if (account == "0") {
                    if (roleID == "1") {
                        subscribe()
                        promptPopUpView?.changeStatus(
                            1,
                            "Kindly upgrade to a PRO to enjoy this feature. Thank you"
                        )
                    }else{
                        subscribe()
                        promptPopUpView?.changeStatus(
                            1,
                            "Kindly subscribe to a plan to enjoy this feature. Thank you"
                        )
                    }
                } else {
                    fragment = JoinRGEnter()
                    toolbar!!.title = "Join Response Group";
                }
            }

            R.id.station_rg -> {
                if (account == "0") {
                    subscribe()
                    promptPopUpView?.changeStatus(1, "Kindly subscribe to a plan to enjoy this feature. Thank you")
                } else {
                    fragment = Station_Response()
                    toolbar!!.title = "Department RGs Alats";
                }

            }

            R.id.client_grp -> {
                if (account == "0") {
                    subscribe()
                    promptPopUpView?.changeStatus(1, "Kindly subscribe to a plan to enjoy this feature. Thank you")
                } else {
                    fragment = Client_Response()
                    toolbar!!.title = "Client RGs Alats";
                }
            }

            R.id.create_client -> {
                if (account == "0") {
                    subscribe()
                    promptPopUpView?.changeStatus(1, "Kindly subscribe to a plan to enjoy this feature. Thank you")
                } else {
                    fragment = AddClientActivitity()
                    toolbar!!.title = "ADD CLIENT"
                }
            }

            R.id.create_station -> {
                if (account == "0") {
                    subscribe()
                    promptPopUpView?.changeStatus(1, "Kindly subscribe to a plan to enjoy this feature. Thank you")
                } else {
                    fragment = AddStation()
                    toolbar!!.title = "ADD DEPARTMENT"
                }
            }
            R.id.random_client -> {
                if (account == "0") {
                    subscribe()
                    promptPopUpView?.changeStatus(1, "Kindly subscribe to a plan to enjoy this feature. Thank you")
                } else {
                    fragment = randomClients()
                    toolbar!!.title = "Random Clients"
                }
            }

            R.id.join_resp -> {

                fragment = JoinRGs()
                toolbar!!.title = "Join Group";
            }
            R.id.create_resp -> {
                fragment = CreateRG()
                toolbar!!.title = "Create Response Group";
            }
            R.id.create_respRG -> {
                if (account == "0") {
                    subscribe()
                    promptPopUpView?.changeStatus(1, "Kindly subscribe to a plan to enjoy this feature. Thank you")
                } else {
                    fragment = CreateRG()
                    toolbar!!.title = "Create Local RG";
                }
            }
            R.id.exit_resp -> {
                if (account == "0") {
                    if (roleID == "1") {
                        subscribe()
                        promptPopUpView?.changeStatus(
                            1,
                            "Kindly upgrade to a PRO to enjoy this feature. Thank you"
                        )
                    }else{
                        subscribe()
                        promptPopUpView?.changeStatus(
                            1,
                            "Kindly subscribe to a plan to enjoy this feature. Thank you"
                        )

                    }
                } else {
                    fragment = exitResponse()
                    toolbar!!.title = "Exit Response Group";
                }
            }

            R.id.generate_rep -> {
                fragment = GenerateReport()
                toolbar!!.title = "Generate Reports";
            }
            R.id.resp_manage -> {
                if (account == "0") {
                    if (roleID == "1") {
                        subscribe()
                        promptPopUpView?.changeStatus(
                            1,
                            "Kindly upgrade to a PRO to enjoy this feature. Thank you"
                        )
                    }else{
                        subscribe()
                        promptPopUpView?.changeStatus(
                            1,
                            "Kindly subscribe to a plan to enjoy this feature. Thank you"
                        )

                    }
                } else {
                    fragment = ManageAlert()
                    toolbar!!.title = "Manage Alats";
                }
            }


            R.id.manage_alert -> {
                if (account == "0") {
                    if (roleID == "1") {
                        subscribe()
                        promptPopUpView?.changeStatus(
                            1,
                            "Kindly upgrade to a PRO to enjoy this feature. Thank you"
                        )
                    }else{
                        subscribe()
                        promptPopUpView?.changeStatus(
                            1,
                            "Kindly subscribe to a plan to enjoy this feature. Thank you"
                        )

                    }
                } else {
                    fragment = ManageAlert()
                    toolbar!!.title = "Manage Alats";
                }
            }

            R.id.SetupGr -> {
                bayo.setVisible(true)
                faq.setVisible(true)
                join.setVisible(true)
                resp_db.isVisible = true
                support.isVisible = true
                feedback.isVisible = true
                navigationView!!.getMenu().setGroupVisible(R.id.SetupGroup, true);
                return true;
            }

            R.id.feedback -> {
                fragment = Feedback()
                toolbar!!.title = "Feedback";
            }

            R.id.join -> {
                socialMedia()
            }

            R.id.faqs -> {
                if (roleID == "1") {
                    fragment = basicFAQ()
                    toolbar!!.title = "FAQs";
                } else {
                    fragment = enterpriseFAQs()
                    toolbar!!.title = "FAQs";
                }
            }

            R.id.support -> {
                fragment = Support()
                toolbar!!.title = "Support";
            }

            R.id.resp_db -> {
                fragment = ResponseProviders()
                toolbar!!.title = "Response Providers Database";
            }

            R.id.invite_friend -> {
                val sharingIntent =
                    Intent(Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                val shareBody =
                    """
                    Hi Friend,                     
                    
                    I just discovered a Reliable and Amazing way to share Emergency Alerts, Access Emergency services, 
                    receive safety updates, manage my business and personal safety as well as that of my loved ones and thought of sharing with you.
 
                    Click https://play.google.com/store/apps/details?id=com.alatpres to download Alatpres App and enjoy managing your safety.
                    """.trimIndent()
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
                startActivity(Intent.createChooser(sharingIntent, "Share Via"))
            }
            R.id.profile -> {
                fragment = Profile()
                toolbar!!.title = "Profile";
            }


            R.id.sign_out -> {
                logout()
            }

        }

        //replacing the fragment
        //replacing the fragment
        if (fragment != null) {
            val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
            ft.replace(R.id.main_fragment, fragment)
            ft.commit()
        }
        val drawer =
            findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun subscribe() {

        promptPopUpView = PromptPopUpView(this)

        AlertDialog.Builder(this)
            .setPositiveButton("Ok") { _: DialogInterface?, _: Int ->

            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }
    private fun socialMedia() {

        val pDialog = PrettyDialog(this)
        pDialog
            .setIconTint(R.color.colorPrimary)
            .setTitle("Join our community")
            .setTitleColor(R.color.pdlg_color_blue)
            .setMessage("You can Join our community\n Via")
            .setMessageColor(R.color.pdlg_color_gray)
            .addButton(
                "Facebook",
                R.color.pdlg_color_white,
                R.color.fb
            ) {
                pDialog.dismiss()
                val intent =
                    Intent(Intent.ACTION_VIEW)
                intent.data =
                    Uri.parse("https://web.facebook.com/Alatpres-103664461222667")
                this@HomePage.startActivity(intent)
            }
            .addButton(
                "Twitter",
                R.color.pdlg_color_white,
                R.color.twit
            ) {
                pDialog.dismiss()
                val intent =
                    Intent(Intent.ACTION_VIEW)
                intent.data =
                    Uri.parse("https://twitter.com/alatpres")
                this@HomePage.startActivity(intent)

            }
            .addButton(
                "LinkedIn",
                R.color.pdlg_color_white,
                R.color.link
            ) {
                pDialog.dismiss()
                val intent =
                    Intent(Intent.ACTION_VIEW)
                intent.data =
                    Uri.parse("https://www.linkedin.com/company/alatpres")
                this@HomePage.startActivity(intent)
            }

            .show()

    }

    fun logout() {
        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Are you sure?")
            .setContentText("You will be required to login again to access ALATPRES!")
            .setConfirmText("Yes, sign me out!")
            .setConfirmClickListener { sDialog ->
                sDialog.dismissWithAnimation()
                pref =
                    applicationContext.getSharedPreferences("MyPref", 0) // 0 - for private mode
                val editor: SharedPreferences.Editor = pref!!.edit()
                editor.putBoolean("isLogin", false)
                editor.clear()
                editor.apply(); // commit changes

                startActivity(Intent(this, LoginActivity::class.java))
            }
            .show()
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

        AlertDialog.Builder( this)

            .setPositiveButton(
                "Retry"
            ) { dialog, _ -> dialog.dismiss()
                recreate()
            }
            .setNegativeButton(
                "Cancel"
            ) { dialog, _ -> dialog.dismiss() }

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

    private var exit = false
    override fun onBackPressed() {
        val drawer =
            findViewById<View>(R.id.drawer_layout) as DrawerLayout

        if (exit) {
            super.onBackPressed()
            finishAffinity()
            return
        }
        try {
            val fragmentManager: FragmentManager = supportFragmentManager
            var fragment: Fragment? =
                fragmentManager.findFragmentByTag("HOME")

            if (fragment != null) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START)
                } else {
                    if (fragment.isVisible) {
                        exit = true
                        Toast.makeText(
                            this,
                            "Press Back again to Exit",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START)
                } else {

                    toolbar!!.title = "Alats";

                    fragment = alerts::class.java.newInstance()
                    getFragmentManager().popBackStack()

                    fragmentManager.beginTransaction().replace(R.id.main_fragment, fragment, "HOME")
                        .commit()

                }
            }
        } catch (e: Exception) {
        }
        Handler().postDelayed(Runnable { exit = false }, 3000)
    }

















    private fun getStudent() {


        //mToolbar!!.title = response_group + "\tRG"

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
        // params["rg_id"] = response_group!!

        params["userid"] = userid!!

//        Toast.makeText(
//            this,
//            "Selected: " +  userid,
//            Toast.LENGTH_LONG
//        ).show()


        val api: FriendInivte = retrofit.create(FriendInivte::class.java)
        val call: Call<ResponseBody> = api.invites(params)

        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                //Toast.makeText()

                Log.d("Call request", call.request().toString());
                Log.d("Response raw header", response.headers().toString());
                Log.d("Response raw", response.toString());
                Log.d("Response code", response.code().toString());


                if (response.isSuccessful) {
                    val remoteResponse = response.body()!!.string()
                       try {
                            val o = JSONObject(remoteResponse)

                            val array: JSONArray = o.getJSONArray("records")

                            for (i in 0 until array.length()) {
                                val dataobj: JSONObject = array.getJSONObject(i)
                                Log.d("DATAFULL", remoteResponse);

                                Log.d("STATUSRECORD", dataobj.getString("status"));

                                if (dataobj.getString("status") == "true") {
                                    Notify
                                        .with(this@HomePage)
                                        .asBigText {
                                            title = "Group Invitation Request!!"
                                            text = "You have a group invitation request to review "
                                            expandedText = ""
                                            bigText =
                                                "You have been invited to join response group/s.\n" + "\n" +
                                                        "Go to menu, notifications then invitation, for you to accept or reject your invitation/s"
                                        }
                                        .show()
                                }
                            }

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                } else {
                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                    Log.d("BAYO", response.code().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                Log.i("onEmptyResponse", "" + t) //
            }
        })
    }


       private fun getExpiry() {
        // Toast.makeText(this@GroupsRequests, userid  , Toast.LENGTH_LONG).show()
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


        val api: FindTime = retrofit.create(FindTime::class.java)
        val call: Call<ResponseBody> = api.fRGs(params)

        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                Log.d("Responsestring", response.body().toString())
                //Toast.makeText()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        // val jsonresponse = response.body().toString()
                        // Log.d("onSuccessS", response.errorBody()!!.toString())
                        try {

                            Log.d("SUCCESS", response.body().toString())
                            val o = JSONObject(response.body()!!.string())
                            val array: JSONArray = o.getJSONArray("records")

                            for (i in 0 until array.length()) {
                                val dataobj: JSONObject = array.getJSONObject(i)
                                date2 = dataobj.getString("subscription_date")

                                val sdf =
                                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                val strDate = sdf.parse(dataobj.getString("subscription_date"))
                                if (System.currentTimeMillis() > strDate.time) {
                                    pref =
                                        applicationContext.getSharedPreferences("ADS", 0) // 0 - for private mode

                                    val editor2: SharedPreferences.Editor = pref!!.edit()
                                    editor2.putString("ads", "0")
                                    editor2.clear()
                                    editor2.apply()

                                    val preferences =
                                        getSharedPreferences("MyPref", 0)
                                    val editor =
                                        preferences.edit()
                                    editor.putBoolean("isLogin", true)
                                    editor.putString("fname", firstname)
                                    editor.putString("lname", sname)
                                    editor.putString("email", email)
                                    editor.putString("dob", dob)
                                    editor.putString("role", roleID)
                                    editor.putString("mssdn", mssdn)
                                    editor.putString("gender", gender)
                                    editor.putString("mstatus", "1")
                                    editor.putString("idNo", idNo)
                                    editor.putString("county", county)
                                    editor.putString("userid", userid)
                                    editor.putString("account_status", "0")
                                    editor.putString("clients", clients)

                                    editor.putString("response_provider", responseprovider)
                                    editor.clear()
                                    editor.apply()



                                    pref =
                                        applicationContext.getSharedPreferences("ADS_ENTER", 0) // 0 - for private mode

                                    val editor4: SharedPreferences.Editor = pref!!.edit()
                                    editor4.putString("ads_enter", "0")
                                    editor4.clear()
                                    editor4.apply()


                                    pref =
                                        applicationContext.getSharedPreferences("FIRSTCHECK", 0) // 0 - for private mode
                                    val editor9: SharedPreferences.Editor = pref!!.edit()
                                    editor9.putString("first_check", check_first)
                                    editor9.clear()
                                    editor9.apply()

                                    pref =
                                        applicationContext.getSharedPreferences("ADS_BASIC", 0) // 0 - for private mode
                                    val editor6: SharedPreferences.Editor = pref!!.edit()
                                    editor6.putString("ads_basic", "0")
                                    editor6.clear()
                                    editor6.apply()


                                    recreate()

                                }
                            }
                            //Log.d("onSuccess1", firstSport.toString())
                           } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {


                        Log.i("onEmptyResponse", "Returned empty response") //Toast.makeText(getContext(),"Nothing returned",Toast.LENGTH_LONG).show();
                    }
                }else{
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




}




