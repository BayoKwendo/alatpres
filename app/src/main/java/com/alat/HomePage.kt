package com.alat


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
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
import com.alat.helpers.PromptPopUpView
import com.alat.ui.activities.auth.LoginActivity
import com.alat.ui.fragments.*
import com.google.android.material.navigation.NavigationView


class HomePage : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener
{
    private var toolbar: androidx.appcompat.widget.Toolbar? = null
    private var promptPopUpView: PromptPopUpView? = null

    val navigationView: NavigationView? = null
    var linear2:LinearLayout ? = null

    var linear:LinearLayout ? = null

    private var backPressedTime: Long = 0
    var pref: SharedPreferences? = null



    var fname: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer_home)

        toolbar = findViewById(R.id.toolbar)
        toolbar!!.title = "FRAFF"
        supportActionBar
        setSupportActionBar(toolbar)




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


//        pref =
//            getSharedPreferences("MyPref", 0) // 0 - for private mode
//        fname = pref!!.getString("fname", null) + "\t" + pref!!.getString("lname", null)
//        val drawers =
//            findViewById<View>(R.id.tvDriverName) as TextView
//
//        drawers.setText(fname)
        navigationView.setNavigationItemSelectedListener(this)


        navigationView.menu.performIdentifierAction(R.id.alert, 0);
        navigationView.menu.getItem(0).isChecked = true;

        val navigationHeaderView = navigationView.getHeaderView(0)


        val tvName =
            navigationHeaderView.findViewById<View>(R.id.tvDriverName) as TextView
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        var fragment: Fragment? = null
        when (item.itemId) {
            R.id.alert -> {
                fragment = alerts()

                toolbar!!.title = "ALERTS";
            }
            R.id.join_resp -> {
                fragment = GlobalRG()

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

            R.id.resp_team-> {
                fragment = ResponseTeam()

                toolbar!!.title = "Response Team Providers";
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

    private var exit = false
    override fun onBackPressed() {
        val drawer =
            findViewById<View>(R.id.drawer_layout) as DrawerLayout

        if (exit) {
            super.onBackPressed()
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
}




