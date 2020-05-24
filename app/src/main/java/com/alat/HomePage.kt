package com.alat


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
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
import androidx.fragment.app.FragmentManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.alat.helpers.PromptPopUpView
import com.alat.ui.activities.CreateAlert
import com.alat.ui.activities.auth.LoginActivity
import com.alat.ui.fragments.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView


class HomePage : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener
{
    private var toolbar: androidx.appcompat.widget.Toolbar? = null
    private var promptPopUpView: PromptPopUpView? = null
    var floatingActionButton: FloatingActionButton? = null
    val navigationView: NavigationView? = null
    var linear2:LinearLayout ? = null
    private var backPressedTime: Long = 0
    var pref: SharedPreferences? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer_home)

        toolbar = findViewById(R.id.toolbar)
        toolbar!!.title = "FRAFF"
        supportActionBar
        setSupportActionBar(toolbar)

        linear2 = findViewById(R.id.linear)
        floatingActionButton =
            findViewById<View>(R.id.floating_action_button) as FloatingActionButton
        floatingActionButton!!.setOnClickListener {
            startActivity(Intent(this, CreateAlert::class.java))
        }


        if (!isNetworkAvailable()) {
            internet()
            promptPopUpView?.changeStatus(1, "Connection Error\n\n Check your internet connectivity")

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
        val navigationView =
            findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)


        navigationView.menu.performIdentifierAction(R.id.alert, 0);
        navigationView.menu.getItem(0).isChecked = true;

        val navigationHeaderView = navigationView.getHeaderView(0)


        val tvName =
            navigationHeaderView.findViewById<View>(R.id.tvDriverName) as TextView
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.alert -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_fragment, alerts())
                    .commit()
                linear2!!.visibility = View.VISIBLE

                toolbar!!.title = "ALERTS";
            }
            R.id.join_resp -> {

                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_fragment, JoinRG())
                    .commit()

                linear2!!.visibility = View.GONE
                toolbar!!.title = "Join Response Group";
            }
            R.id.create_resp -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_fragment, CreateRG())
                    .commit()
                linear2!!.visibility = View.GONE

                toolbar!!.title = "Create Response Group";
            }
            R.id.generate_rep -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_fragment, GenerateReport())
                    .commit()
                linear2!!.visibility = View.GONE

                toolbar!!.title = "Generate Reports";
            }
            R.id.manage_alert -> {
//                supportFragmentManager
//                    .beginTransaction()
//                    .replace(R.id.main_fragment,)
//                    .commit()
                linear2!!.visibility = View.GONE

                toolbar!!.title = "Manage Alerts";

            }
            R.id.profile -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_fragment, Profile())
                    .commit()
                linear2!!.visibility = View.GONE

                toolbar!!.title = "Profile";

            }
            R.id.sign_out -> {
                logout()

            }
        }
        val drawer =
            findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    fun logout(){
        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Are you sure?")
            .setContentText("You will be required to login again to access ALATPRES!")
            .setConfirmText("Yes, sign me out!")
            .setConfirmClickListener {
                    sDialog -> sDialog.dismissWithAnimation()
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


    override fun onBackPressed() {
        val t = System.currentTimeMillis()

        val drawer =
            findViewById<View>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        }
        if (supportFragmentManager.backStackEntryCount >= 1) {
            navigationView!!.setCheckedItem(R.id.alert)
            navigationView.menu.performIdentifierAction(R.id.alert, 0)
        }
        else{
            if (supportFragmentManager.backStackEntryCount == 0) {
                if (t - backPressedTime > 3000) {    // 3 secs
                    backPressedTime = t
                    Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
                } else {
                    // clean up
                    super.onBackPressed()
                }
            } else {
                supportFragmentManager.popBackStack()
            }
        }
    }


//    override fun onBackPressed() {
//        val fragmentManager: android.app.FragmentManager? = fragmentManager
//        val backCount: Int = fragmentManager!!.backStackEntryCount
//        if (backCount > 1) {
//            super.onBackPressed()
//        } else {
//            finish()
//        }
//    }

}




