package com.alat.ui.activities

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alat.HomePage
import com.alat.R
import com.alat.adapters.ResponseServices
import com.alat.helpers.MyDividerItemDecoration
import com.alat.model.rgModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.io.InputStream
import java.util.*


@Suppress("UNREACHABLE_CODE")
class IntergrationMendu : Fragment(), ResponseServices.ContactsAdapterListener {

    var alert_id: String? = null

    //var response_group_name: String? = null
    private var recyclerView: RecyclerView? = null
    private var contactList: MutableList<rgModel>? = null
    private var mAdapter: ResponseServices? = null
    private var searchView: SearchView? = null
    private var mProgressLayout: LinearLayout? = null
    var errorNull: TextView? = null
    var addMessage: FloatingActionButton? = null
    var roleID: String? = null
    var pref: SharedPreferences? = null
    var mToolbar: Toolbar? = null
    var button2: FloatingActionButton? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_alert_menu, container, false)
        setHasOptionsMenu(true)
        button2 = view.findViewById(R.id.fab_add_topic)
        recyclerView = view.findViewById(R.id.recycler_view)
        errorNull = view.findViewById(R.id.texterror)
        contactList = ArrayList()
        mAdapter = ResponseServices(requireActivity(), contactList!!, this)
        mProgressLayout = view.findViewById(R.id.layout_discussions_progress);

        val mLayoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(activity)
        recyclerView!!.layoutManager = mLayoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.addItemDecoration(
            MyDividerItemDecoration(
                requireActivity(),
                DividerItemDecoration.VERTICAL,
                36
            )
        )
        recyclerView!!.adapter = mAdapter
        mProgressLayout!!.visibility = View.VISIBLE
        errorNull!!.visibility = View.GONE
        responsegroups()

        button2!!.setOnClickListener {
            val viewIntent = Intent(
                "android.intent.action.VIEW",
                Uri.parse("https://alatpres.com/partner-with-us/")
            )
            startActivity(viewIntent)
        }
        return view;
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

    private fun responsegroups() {
        try {
//            mProgressLayout!!.visibility = View.VISIBLE
//            errorNull!!.visibility = View.GONE

            val myJson =
                inputStreamToString(this.resources.openRawResource(R.raw.responseservices))
            val array: JSONArray = JSONArray(myJson)
//            Toast.makeText(
//                requireActivity(),
//                array.length(),
//                Toast.LENGTH_LONG
//            ).show()
            for (i in 0 until array.length()) {
                val items: List<rgModel> =
                    Gson().fromJson<List<rgModel>>(
                        array.toString(),
                        object : TypeToken<List<rgModel?>?>() {}.type
                    )
                contactList!!.clear()
                contactList!!.addAll(items)
                mAdapter!!.notifyDataSetChanged()
                mProgressLayout!!.visibility = View.GONE
                errorNull!!.visibility = View.GONE
            }
            //    Log.d("onSuccess1", firstSport.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        mToolbar!!.inflateMenu(R.menu.menu_items);
        inflater.inflate(R.menu.menu_items, menu)

        // Associate searchable configuration with the SearchView


        var item = menu.findItem(R.id.action_share)
        val item2 = menu.findItem(R.id.join)
        val item3 = menu.findItem(R.id.invites)
        val item4 = menu.findItem(R.id.about)
        val item5 = menu.findItem(R.id.logout)
        val item6 = menu.findItem(R.id.action_invite)

        item.isVisible = false
        item2.isVisible = false
        item3.isVisible = false
        item4.isVisible = false
        item5.isVisible = false
        item6.isVisible = false
        val searchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu!!.findItem(R.id.action_search)
            .actionView as SearchView
        searchView!!.setSearchableInfo(
            searchManager
                .getSearchableInfo(requireActivity().componentName)
        )
        searchView!!.maxWidth = Int.MAX_VALUE
        // listening to search query text change
        searchView!!.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // filter recycler view when query submitted
                mAdapter!!.filter.filter(query)

                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                // filter recycler view when text is changed
                mAdapter!!.filter.filter(query)
                return false
            }
        })
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_search -> {
                true
            }
            android.R.id.home -> {
                BackAlert()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }


    fun onBackPressed() {
        // close search view on back button pressed
        if (!searchView!!.isIconified) {
            searchView!!.isIconified = true
            return
        }
        startActivity(Intent(requireActivity(), HomePage::class.java))

        BackAlert()
    }


    fun BackAlert() {
        AlertDialog.Builder(requireActivity())
            .setMessage("Leaving page for now??")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, id ->
            }
            .setNegativeButton("No", null)
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

    //redirect to safety provider services
    override fun onContactSelected(contact: rgModel?) {
        val i =
            Intent(activity, Safety_Providers::class.java)
        i.putExtra("safety_provider", contact!!.name.toString())
        startActivity(i)
    }
}
