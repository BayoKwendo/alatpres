package com.alat.ui.fragments

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.ShareActionProvider
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import com.alat.R
import com.alat.ui.AboutUs
import com.alat.ui.activities.CreateAlert
import com.alat.ui.activities.Invitations
import com.alat.ui.activities.Notification
import com.alat.ui.activities.account_enterprise
import com.google.android.material.floatingactionbutton.FloatingActionButton
import libs.mjn.prettydialog.PrettyDialog

class Alert_Enterpris : Fragment() {
    var floatingActionButton: FloatingActionButton? = null

    private var toolbar : Toolbar? = null
    private var ui_hot: TextView? = null
    private var hot_number = 0
    private var searchView: SearchView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_home_enterpris, container, false)
        setHasOptionsMenu(true)
        floatingActionButton =
            view.findViewById<View>(R.id.floating_action_button) as FloatingActionButton
         return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        floatingActionButton!!.setOnClickListener {
            alerttype()
        }
        //you can set the title for your toolbar here for different fragments different title
    }


    private fun alerttype() {

        val pDialog = PrettyDialog(activity)
        pDialog
            .setIconTint(R.color.colorPrimary)
            .setTitle("Create An Alert")
            .setTitleColor(R.color.pdlg_color_blue)
            .setMessage("Choose the type of alert you want to create")
            .setMessageColor(R.color.pdlg_color_gray)
            .addButton(
                "Station Alert",
                R.color.pdlg_color_white,
                R.color.colorAccent
            ) { pDialog.dismiss()
                 Toast.makeText(context,"Soon",Toast.LENGTH_LONG).show();
            }
            .addButton(
                "Client Alert",
                R.color.pdlg_color_white,
                R.color.colorAccent) {
                pDialog.dismiss()

                Toast.makeText(context,"Soon",Toast.LENGTH_LONG).show();

            }
            .show()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_items, menu)
        val item9 = menu.findItem(R.id.join)

        MenuItemCompat.setActionView(
            item9,
            R.layout.action_bar_notifitcation_icon
        )
        val menu_hotlist = MenuItemCompat.getActionView(item9)
        ui_hot = menu_hotlist.findViewById(R.id.hotlist_hot) as TextView
        updateHotCount(hot_number)
        object : alerts.MyMenuItemStuffListener(menu_hotlist, "Show hot message") {
            override fun onClick(v: View?) {
                //  activity.onHotlistSelected()
            }
        }
        val item = menu.findItem(R.id.action_share)
        // Fetch and store ShareActionProvider

        val item6 = menu.findItem(R.id.action_invite)
        item6.isVisible = false

        val mShareActionProvider: ShareActionProvider? =
            MenuItemCompat.getActionProvider(item) as ShareActionProvider?
        mShareActionProvider?.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME)

        mShareActionProvider?.setShareIntent(createShareIntent())
        val searchManager =
            activity!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu.findItem(R.id.action_search)
            .actionView as SearchView
        searchView!!.setSearchableInfo(
            searchManager
                .getSearchableInfo(activity!!.componentName)
        )
        searchView!!.maxWidth = Int.MAX_VALUE

        // listening to search query text change
        searchView!!.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // filter recycler view when query submitted
              //  mAdapter!!.filter.filter(query)

                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                // filter recycler view when text is changed
               // mAdapter!!.filter.filter(query)
                return false
            }
        })
    }

    // call the updating code on the main thread,
    // so we can call this asynchronously
    fun updateHotCount(new_hot_number: Int) {
        hot_number = new_hot_number
        if (ui_hot == null) return
        activity!!.runOnUiThread(Runnable {
            if (new_hot_number == 0) ui_hot!!.visibility = View.INVISIBLE else {
                ui_hot!!.visibility = View.VISIBLE
                ui_hot!!.text = Integer.toString(new_hot_number)
            }
        })
    }



    // Create and return the Share Intent
    private fun createShareIntent(): Intent? {
        val shareIntent =
            Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(
            Intent.EXTRA_TEXT, """
          ALATPRES
     Get AlatPres.
     https://play.google.com/store/apps/details?id=com.alatpres
     """.trimIndent()
        )
        val intent =
            Intent.createChooser(shareIntent, "Share Via")
        return shareIntent
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        when (id) {
            R.id.action_search -> {
                true
            }
            R.id.join -> {
                startActivity(Intent(activity, Notification::class.java))

            }


            R.id.invites -> {
                startActivity(Intent(activity, Invitations::class.java))
            }

            R.id.aboutus -> {
                startActivity(Intent(activity, AboutUs::class.java))
            }

            R.id.about -> {

                    startActivity(Intent(activity, account_enterprise::class.java))

            }
//            R.id.team-> {
//                startActivity(Intent(activity, ResponseProviders::class.java))
//            }

//            R.id.logout -> {
//                logout()
//            }

            else -> super.onOptionsItemSelected(item)
        }

        return true
    }





}