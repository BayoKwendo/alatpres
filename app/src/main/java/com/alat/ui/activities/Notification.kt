package com.alat.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
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

class Notification : AppCompatActivity() {
    val dataModels = ArrayList<Any?>()

     var card: CardView? = null
    var card2: CardView? = null
    var card3: CardView? = null
    var card4: CardView? = null


      override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notify)
          supportActionBar!!.setDisplayHomeAsUpEnabled(true)
          card = findViewById(R.id.cardactive)

          card3 = findViewById(R.id.version)
          card3!!.setOnClickListener {

        Toast.makeText(
            this,
            "No updates for now",
            Toast.LENGTH_LONG
        ).show()

          }


          card!!.setOnClickListener {
              startActivity(Intent(this, GroupsRequests::class.java))
          }

          card2 = findViewById(R.id.neutralized)

          card2!!.setOnClickListener {
              startActivity(Intent(this, Invitations::class.java))
          }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {

            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

        return true
    }

}