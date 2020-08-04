package com.alat.ui.fragments

import android.app.ProgressDialog
import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.ShareActionProvider
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.alat.HomePage
import com.alat.R
import com.alat.adapters.RGAdapter
import com.alat.helpers.Constants
import com.alat.helpers.MyDividerItemDecoration
import com.alat.helpers.PromptPopUpView
import com.alat.interfaces.ViewGroups
import com.alat.model.PreferenceModel
import com.alat.model.rgModel
import com.alat.ui.AboutUs
import com.alat.ui.activities.*
import com.alat.ui.activities.auth.LoginActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
import java.util.*
import java.util.concurrent.TimeUnit

class alerts : Fragment(),
    RGAdapter.ContactsAdapterListener {

    private var toolbar: Toolbar? = null


    private val TAG = HomePage::class.java.simpleName
    private var recyclerView: RecyclerView? = null
    private var contactList: MutableList<rgModel>? = null
    private var mAdapter: RGAdapter? = null
    private var searchView: SearchView? = null
    private var edtEmail: EditText? = null
    private var mProgressLayout: LinearLayout? = null

    private var preferenceHelper: PreferenceModel? = null
    private var promptPopUpView: PromptPopUpView? = null


    var floatingActionButton: FloatingActionButton? = null

    var global: FloatingActionButton? = null
    private var hot_number = 0
    private var ui_hot: TextView? = null
    private var btnResetPassword: Button? = null
    private var btnBack: Button? = null
    var errorNull: TextView? = null
    private var mProgress: ProgressDialog? = null
    var pref: SharedPreferences? = null
    private var accounts: String? = null
    private var userid: String? = null

    private var roleID: String? = null
    var MYCODE = 1000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_home, container, false)
        setHasOptionsMenu(true)
        preferenceHelper = PreferenceModel(activity!!)
        recyclerView = view.findViewById(R.id.recycler_view)
        errorNull = view.findViewById(R.id.texterror)
        contactList = ArrayList()
        mAdapter = RGAdapter(activity!!, contactList!!, this)
        recyclerView!!.setNestedScrollingEnabled(false);
        mProgressLayout = view.findViewById(R.id.layout_discussions_progress);

        val mLayoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(activity)
        recyclerView!!.layoutManager = mLayoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.addItemDecoration(
            MyDividerItemDecoration(
                activity!!,
                DividerItemDecoration.VERTICAL,
                36
            )
        )
        recyclerView!!.adapter = mAdapter
        floatingActionButton =
            view.findViewById<View>(R.id.floating_action_button) as FloatingActionButton
        pref =
            context!!.getSharedPreferences("MyPref", 0)

        userid = pref!!.getString("userid", null)

        accounts = pref!!.getString("account_status", null)

        roleID = pref!!.getString("role", null)


        global =
            view.findViewById<View>(R.id.joinGlobal) as FloatingActionButton

        global!!.setOnClickListener {
            startActivity(Intent(activity!!, JoinGlobal::class.java))
        }

        floatingActionButton!!.setOnClickListener {
            startActivity(Intent(activity!!, CreateAlert::class.java))
        }

//      `  val runnable = Runnable { loadCustomAd() }
//        runnable.run()`

        view.context
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mProgressLayout!!.visibility = View.VISIBLE
        errorNull!!.visibility = View.GONE
        getStudent()
        //you can set the title for your toolbar here for different fragments different title
    }


//    fun loadCustomAd() {
//        val fragmentManager: FragmentManager? = fragmentManager
//        val ad = CustomAd()
//        ad.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog)
//        ad.show(fragmentManager!!, "Input Dialog")
//        if (ad.dialog != null) {
//            ad.dialog!!.setCanceledOnTouchOutside(true)
//        }
//    }


    private fun getStudent() {

        val group_name: String? = null
        val alerts = 0

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

        params["userid"] = userid!!
        val api: ViewGroups = retrofit.create(ViewGroups::class.java)
        val call: Call<ResponseBody>? = api.viewRG(params)

        call?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                //Toast.makeText()
                if (response.isSuccessful) {
                    val remoteResponse = response.body()!!.string()
                    try {
                        val o = JSONObject(remoteResponse)

                        if (o.getString("status") == "true") {

                            val array: JSONArray = o.getJSONArray("records")

                            for (i in 0 until array.length()) {
                                val dataobj: JSONObject = array.getJSONObject(i)

                                val items: List<rgModel> =
                                    Gson().fromJson<List<rgModel>>(
                                        array.toString(),
                                        object : TypeToken<List<rgModel?>?>() {}.type
                                    )
                                Collections.reverse(items);
                                contactList!!.clear()
                                contactList!!.addAll(items)
                                mAdapter!!.notifyDataSetChanged()
                                mProgressLayout!!.visibility = View.GONE
                                errorNull!!.visibility = View.GONE

                            }
                        } else {
                            errorNull!!.visibility = View.VISIBLE
                            mProgressLayout!!.visibility = View.GONE
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                } else {
                    Log.d("bayo", response.errorBody()!!.string())
                    errorNull!!.visibility = View.VISIBLE
                    mProgressLayout!!.visibility = View.GONE

                    // Toast.makeText(context,"Nothing" +  response.errorBody()!!.string(),Toast.LENGTH_LONG).show();


                    internet()
                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")


                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                //   btn.text = "Proceed"
                Log.i("onEmptyResponse", "" + t) //
                // Toast.makeText(context,"Nothing ",Toast.LENGTH_LONG).show();

                internet()
                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
                //mProgress?.dismiss()
            }
        })
    }


    private fun dialogue() {

        promptPopUpView = PromptPopUpView(activity)

        AlertDialog.Builder(activity!!)
            .setPositiveButton("PROCESSING....") { _: DialogInterface?, _: Int ->
                //      finish()

            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }

    private fun dialogue_error() {

        promptPopUpView = PromptPopUpView(activity)

        AlertDialog.Builder(activity!!)
            .setPositiveButton("Ok") { _: DialogInterface?, _: Int ->

            }
            .setCancelable(false)
            .setView(promptPopUpView)
            .show()
    }

    private fun internet() {
//        promptPopUpView = PromptPopUpView(activity)
//
//        AlertDialog.Builder( activity!!)
//
//            .setPositiveButton(
//                "Retry"
//            ) { dialog, _ -> dialog.dismiss()
//                activity!!.recreate()
//            }
//         .setNegativeButton(
//            "Cancel"
//         ) { dialog, _ -> dialog.dismiss() }
//
//            .setCancelable(false)
//            .setView(promptPopUpView)
//            .show().withCenteredButtons()
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
        object : MyMenuItemStuffListener(menu_hotlist, "Show hot message") {
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

    internal abstract class MyMenuItemStuffListener(
        private val view: View,
        private val hint: String
    ) :
        View.OnClickListener, View.OnLongClickListener {
        abstract override fun onClick(v: View?)
        override fun onLongClick(v: View?): Boolean {
            val screenPos = IntArray(2)
            val displayFrame = Rect()
            view.getLocationOnScreen(screenPos)
            view.getWindowVisibleDisplayFrame(displayFrame)
            val context = view.context
            val width = view.width
            val height = view.height
            val midy = screenPos[1] + height / 2
            val screenWidth = context.resources.displayMetrics.widthPixels
            val cheatSheet: Toast = Toast.makeText(context, hint, Toast.LENGTH_SHORT)
            if (midy < displayFrame.height()) {
                cheatSheet.setGravity(
                    Gravity.TOP or Gravity.RIGHT,
                    screenWidth - screenPos[0] - width / 2, height
                )
            } else {
                cheatSheet.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, height)
            }
            cheatSheet.show()
            return true
        }

        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }
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
                if (roleID == "1") {
                    startActivity(Intent(activity, com.alat.ui.activities.account::class.java))
                } else if (roleID == "2") {
                    startActivity(Intent(activity, account_enterprise::class.java))
                }
            }
//            R.id.team-> {
//                startActivity(Intent(activity, ResponseProviders::class.java))
//            }

            R.id.logout -> {
                logout()
            }

            else -> super.onOptionsItemSelected(item)
        }

        return true
    }


    private fun logout() {
        SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Are you sure?")
            .setContentText("You will be required to login again to access ALATPRES!")
            .setConfirmText("Yes, sign me out!")
            .setConfirmClickListener {

                    sDialog ->
                sDialog.dismissWithAnimation()

                pref =
                    context!!.getSharedPreferences("MyPref", 0) // 0 - for private mode

                val editor: SharedPreferences.Editor = pref!!.edit()
                editor.putBoolean("isLogin", false)
                editor.clear()
                editor.apply(); // commit changes


                startActivity(Intent(activity, LoginActivity::class.java))


            }
            .show()
    }

    fun onBackPressed() {
        // close search view on back button pressed
        if (!searchView!!.isIconified()) {
            searchView!!.isIconified = true
            return
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        // Result OK.d.
        if (requestCode == MYCODE) {
            if (!searchView!!.isIconified()) {
                searchView!!.isIconified = true
                return
            }
            // do something good
        }
    }


    override fun onContactSelected(contact: rgModel?) {


        val i =
            Intent(activity, AlertsPerResponse::class.java)
        i.putExtra("groupSelect", contact!!.group_name.toString())
        i.putExtra("groupID", contact!!.id.toString())

        startActivity(i)


    }
}
