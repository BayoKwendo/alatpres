package com.alat


import android.content.Context
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
import com.alat.helpers.Constants
import com.alat.helpers.PromptPopUpView
import com.alat.interfaces.FriendInivte
import com.alat.ui.activities.ResponseProviders
import com.alat.ui.activities.auth.LoginActivity
import com.alat.ui.activities.enterprise.AddClientActivitity
import com.alat.ui.fragments.*
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
import java.util.concurrent.TimeUnit


class HomePage : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener
{
    private var toolbar: androidx.appcompat.widget.Toolbar? = null
    private var promptPopUpView: PromptPopUpView? = null

    var navigationView: NavigationView? = null
    var linear2:LinearLayout ? = null

    var linear:LinearLayout ? = null

    private var backPressedTime: Long = 0
    var pref: SharedPreferences? = null
    private var userid: String? = null

    var fname: String? = null
    private var account: String? = null

    private var roleID: String? = null


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


//
//        if (roleID == "2") {
//            if (account == "0") {
//                startActivity(Intent(this, account_enterprise::class.java))
//                Toast.makeText(
//                    this,
//                    "Kindly subscribe first!",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
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
//        pref =
//            getSharedPreferences("MyPref", 0) // 0 - for private mode
//        fname = pref!!.getString("fname", null) + "\t" + pref!!.getString("lname", null)
//        val drawers =
//            findViewById<View>(R.id.tvDriverName) as TextView
//
//        drawers.setText(fname)
        navigationView!!.setNavigationItemSelectedListener(this)

        val alert = navigationView!!.getMenu().findItem(R.id.alert);
        val create_resp = navigationView!!.getMenu().findItem(R.id.create_resp);
        val join_resp = navigationView!!.getMenu().findItem(R.id.join_resp);
        val exit_resp = navigationView!!.getMenu().findItem(R.id.exit_resp);
        val generate_rep = navigationView!!.getMenu().findItem(R.id.generate_rep);
        val resp_team = navigationView!!.getMenu().findItem(R.id.resp_team);
        val manage_alert = navigationView!!.getMenu().findItem(R.id.manage_alert);
        val profile = navigationView!!.getMenu().findItem(R.id.profile);
        val SetupGr = navigationView!!.menu.findItem(R.id.SetupGr);

        val alert_entr = navigationView!!.getMenu().findItem(R.id.alert_entr);
        val create_station = navigationView!!.getMenu().findItem(R.id.create_station);
        val create_client = navigationView!!.getMenu().findItem(R.id.create_client);
        val local_grp = navigationView!!.getMenu().findItem(R.id.local_grp);
        val client_grp = navigationView!!.getMenu().findItem(R.id.client_grp);
        val resp_manage = navigationView!!.getMenu().findItem(R.id.resp_manage);

        val logout_alert = navigationView!!.getMenu().findItem(R.id.sign_out);

        navigationView!!.getMenu().setGroupVisible(R.id.SetupGroup, false);


        if (roleID == "1") {
            navigationView!!.menu.performIdentifierAction(R.id.alert, 0);

            navigationView!!.getMenu().setGroupVisible(R.id.SetupGroup, false);
            alert_entr.isVisible = false
            create_station.isVisible = false
            create_client.isVisible = false
            local_grp.isVisible = false
            client_grp.isVisible = false
            resp_manage.isVisible = false
            resp_team.isVisible = false
            alert.isVisible = true
            create_resp.isVisible = true
            join_resp.isVisible = true
            exit_resp.isVisible = true
            generate_rep.isVisible = true
            manage_alert.isVisible = true
            profile.isVisible = true
            SetupGr.isVisible = true


        } else if (roleID == "2") {
            navigationView!!.menu.performIdentifierAction(R.id.alert_entr, 0);
            navigationView!!.menu.getItem(0).isChecked = true;
            alert.isVisible = true
            create_resp.isVisible = false
            join_resp.isVisible = false
            exit_resp.isVisible = false
            generate_rep.isVisible = false
            manage_alert.isVisible = false
            SetupGr.isVisible = false
            profile.isVisible = true
            logout_alert.isVisible = true
            SetupGr.isVisible = true
            alert_entr.isVisible = true
            resp_team.isVisible = true
            create_station.isVisible = true
            create_client.isVisible = true
            local_grp.isVisible = true
            client_grp.isVisible = true
            resp_manage.isVisible = true
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
                toolbar!!.title = "ALERTS";
            }

//            R.id.alert -> {
//                fragment = alerts()
//                toolbar!!.title = "ALERTS";
//            }

            R.id.create_client -> {
                fragment = AddClientActivitity()
                toolbar!!.title = "ADD CLIENT";
            }

            R.id.join_resp -> {
                fragment = JoinRGs()
                toolbar!!.title = "Join Group";
            }
            R.id.create_resp -> {
                fragment = CreateRG()
                toolbar!!.title = "Create Response Group";
            }

            R.id.exit_resp -> {
                fragment = exitResponse()
                toolbar!!.title = "Exit Response Group";
            }
            R.id.generate_rep -> {

                fragment = GenerateReport()

                toolbar!!.title = "Generate Reports";
            }
            R.id.manage_alert -> {
                fragment = ManageAlert()
                toolbar!!.title = "Manage Alerts";

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
            R.id.support -> {
                fragment = Support()
                toolbar!!.title = "Support";
            }

            R.id.resp_db -> {
                fragment = ResponseProviders()

                toolbar!!.title = "Response Providers Database";
            }

            R.id.invite_friend-> {
                val sharingIntent =
                    Intent(Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                val shareBody =
                    """
                     ALATPRES APP
                     
                     Get AlatPres.
                     
                     Download the app via https://play.google.com/store/apps/details?id=com.alatpres
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

                    toolbar!!.title = "ALERTS";

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



//
//    private fun joinRequest() {
//
//
//        //mToolbar!!.title = response_group + "\tRG"
//
//        val interceptor = HttpLoggingInterceptor()
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
//        val client: OkHttpClient = OkHttpClient.Builder()
//            .addInterceptor(interceptor) //.addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
//            .connectTimeout(2, TimeUnit.MINUTES)
//            .writeTimeout(2, TimeUnit.MINUTES) // write timeout
//            .readTimeout(2, TimeUnit.MINUTES) // read timeout
//            .addNetworkInterceptor(object : Interceptor {
//                @Throws(IOException::class)
//                override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
//                    val request: Request =
//                        chain.request().newBuilder() // .addHeader(Constant.Header, authToken)
//                            .build()
//                    return chain.proceed(request)
//                }
//            }).build()
//        val retrofit: Retrofit = Retrofit.Builder()
//            .baseUrl(Constants.API_BASE_URL)
//            .client(client) // This line is important
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        val params: HashMap<String, String> = HashMap()
//        params["rg_id"] = response_group!!
//        //params["userid"] = response_group!!
//
//
//        val api: FriendReq = retrofit.create(FriendReq::class.java)
//        val call: Call<ResponseBody> = api.Requests(params)
//
//        call.enqueue(object : Callback<ResponseBody?> {
//            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
//                //Toast.makeText()
//
//                Log.d("Call request", call.request().toString());
//                Log.d("Response raw header", response.headers().toString());
//                Log.d("Response raw", response.toString());
//                Log.d("Response code", response.code().toString());
//
//
//                if (response.isSuccessful) {
//                    val remoteResponse = response.body()!!.string()
//                    Log.d("test", remoteResponse)
//
//                    if (response.code().toString() == "200") {
//                        errorNull!!.visibility = View.VISIBLE
//                        mProgressLayout!!.visibility = View.GONE
//                    }
//                    parseLoginData(remoteResponse)
//                } else {
//                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
//                    Log.d("BAYO", response.code().toString())
//                }
//            }
//
//            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
//                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
//                Log.i("onEmptyResponse", "" + t) //
//            }
//        })
//    }
//
//    private fun parseLoginData(remoteResponse: String) {
//        try {
//            val o = JSONObject(remoteResponse)
//            val array: JSONArray = o.getJSONArray("records")
//            val names = arrayOfNulls<String>(array.length())
//
//            val items: List<rgModel> =
//                Gson().fromJson<List<rgModel>>(
//                    array.toString(),
//                    object : TypeToken<List<rgModel?>?>() {}.type
//                )
//
//            Collections.reverse(items);
//
//            contactList!!.clear()
//
//            contactList!!.addAll(items)
//            mAdapter!!.notifyDataSetChanged()
//
//            mProgressLayout!!.visibility = View.GONE
//            errorNull!!.visibility = View.GONE
//
//
//            //    Log.d("onSuccess1", firstSport.toString())
//
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//    }


}




