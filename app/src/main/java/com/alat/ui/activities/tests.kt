package com.alat.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import com.alat.R
import com.alat.adapters.CustomAdapter
import com.alat.adapters.RGAdapter
import com.alat.helpers.Constants
import com.alat.interfaces.GetRGs
import com.alat.model.DataModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class tests : AppCompatActivity() {
    val dataModels = ArrayList<Any?>()

    var listView: ListView? = null
    private var adapter: CustomAdapter? = null

    private var contactList: MutableList<DataModel>? = null
    private var mAdapter: RGAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        listView = findViewById<View>(R.id.listView) as ListView

        getStudent()

    }



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
            .addConverterFactory(ScalarsConverterFactory.create())

            .build()
        val api: GetRGs = retrofit.create(GetRGs::class.java)
        val call: Call<String>? = api.getRG(group_name, alerts)

        call?.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                Log.d("Responsestring", response.body().toString())
                //Toast.makeText()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        Log.d("onSuccess", response.body().toString())
                        val jsonresponse = response.body().toString()


//                        if (response.code().toString() == "200") {
//                            errorNull!!.visibility = View.VISIBLE
//                            mProgressLayout!!.visibility = View.GONE
//                        }

                        parseLoginData(jsonresponse)
                    } else {
//                        errorNull!!.visibility = View.VISIBLE
//                        mProgressLayout!!.visibility = View.GONE

                        Log.i("onEmptyResponse", "Returned empty response")
                        //   Toast.makeText(context,"Nothing returned",Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d("bayo", response.errorBody()!!.string())
//                    errorNull!!.visibility = View.VISIBLE
//                    mProgressLayout!!.visibility = View.GONE
//
//                    // Toast.makeText(context,"Nothing" +  response.errorBody()!!.string(),Toast.LENGTH_LONG).show();


//                    internet()
//                    promptPopUpView?.changeStatus(1, "Something went wrong. Try again")


                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                //   btn.text = "Proceed"
                Log.i("onEmptyResponse", "" + t) //
                // Toast.makeText(context,"Nothing ",Toast.LENGTH_LONG).show();

//                internet()
//                promptPopUpView?.changeStatus(1, "Something went wrong. Try again")
//                //mProgress?.dismiss()
            }
        })
    }

    private fun parseLoginData(jsonresponse: String) {
        try {
            val o = JSONObject(jsonresponse)
            val array: JSONArray = o.getJSONArray("records")
            val names = arrayOfNulls<String>(array.length())

            val items: List<DataModel> =
                Gson().fromJson<List<DataModel>>(
                    array.toString(),
                    object : TypeToken<List<DataModel?>?>() {}.type
                )
            Collections.reverse(items);
            dataModels.clear()
            dataModels.addAll(items)

            adapter = CustomAdapter(dataModels, applicationContext)
            listView!!.adapter = adapter
            listView!!.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    val dataModel: DataModel = dataModels.get(position) as DataModel
                    dataModel.checked = !dataModel.checked
                    adapter!!.notifyDataSetChanged()
                }

//            mProgressLayout!!.visibility = View.GONE
//            errorNull!!.visibility = View.GONE


            //    Log.d("onSuccess1", firstSport.toString())

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_items, menu)
        val mSearch = menu.findItem(R.id.action_search)
        val mSearchView =
            mSearch.actionView as SearchView
        mSearchView.queryHint = "Search"
        mSearchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter!!.filter.filter(newText)
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

}