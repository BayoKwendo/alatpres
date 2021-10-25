package com.alat.ui.activities

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.alat.HomePage
import com.alat.R
import com.alat.helpers.Constants
import com.alat.interfaces.AlertCounts
import com.alat.interfaces.GetAlertPost
import com.itextpdf.text.BadElementException
import com.itextpdf.text.Document
import com.itextpdf.text.DocumentException
import com.itextpdf.text.Image
import com.itextpdf.text.pdf.PdfWriter
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
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.MalformedURLException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class AlertReport : AppCompatActivity() {


    var mid: String? = null
    var mToolbar: Toolbar? = null

    var generate: Button? = null
    var rl: String? = null

    var pref: SharedPreferences? = null


    var response_group: TextView? = null
    var alert_type: TextView? = null


    var fullnaem: TextView? = null
    var levelresp: TextView? = null

    var createdOn: TextView? = null


    var neutralized: TextView? = null


    var postedBy: TextView? = null



    var nutralize: TextView? = null
    var safe: TextView? = null
    var ignore: TextView? = null
    var shout: TextView? = null
    var elevates: TextView? = null
    var mshare: TextView? = null

    var imageView: ImageView? = null

    var url: String? = null


    var alert_name: String? = null
    var rls: String? = null
    var mssdn: String? = null
    var userid: String? = null


    var ignore_count: String? = null
    var shout_count: String? = null
    var elevate_count: String? = null
    var share_count: String? = null
    var neutral_count: String? = null
    var safe_count: String? = null
    private var IS_MANY_PDF_FILE = false

    /**
     * This is identify to number of pdf file. If pdf model list size > sector so we have create many file. After that we have merge all pdf file into one pdf file
     */
    private val SECTOR = 100 // Default value for one pdf file.


    private var mProgress: ProgressDialog? = null
    var rg: String? = null
    var view: View? = null

    var alertyp: String? = null
    var usr: String? = null
    var loc: String? = null
    var created: String? = null


    var modified: String? = null


    var posts: String? = null

    var scrollView: RelativeLayout? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary_report)


        val state = Environment.getExternalStorageState()

        if (!Environment.MEDIA_MOUNTED.equals(state)) { Toast.makeText(this@AlertReport, "Your internal Storage is not writable", Toast.LENGTH_LONG).show()
        }

        pref =
            this.getSharedPreferences("MyPref", 0) // 0 - for private mode

        usr = pref!!.getString("fname", null) + "\t" + pref!!.getString("lname", null)

        mid = intent.getStringExtra("alertSelect")
        rl = intent.getStringExtra("level")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        alert_type = findViewById(R.id.textView9)

        scrollView = findViewById(R.id.relation)

        counts()
        response_group = findViewById(R.id.textView7)
        fullnaem =findViewById(R.id.textView21)
        levelresp = findViewById(R.id.textView15)
        generate = findViewById(R.id.generate)
        createdOn = findViewById(R.id.textView11)
        nutralize = findViewById(R.id.neutralized)
        safe = findViewById(R.id.safe)
        ignore = findViewById(R.id.ignore)
        shout = findViewById(R.id.shouts)
        elevates = findViewById(R.id.elevate)
        mshare = findViewById(R.id.share)


        neutralized = findViewById(R.id.textView23)
        postedBy = findViewById(R.id.textView28)

        view = findViewById(R.id.view)

        mProgress = ProgressDialog(this)
            mProgress!!.setMessage("Saving...")
        mProgress!!.setCancelable(true)

        val pdfDir = File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS), "ALATPRES")

        if (!pdfDir.exists())
        { pdfDir.mkdir() }
        generate!!.isEnabled = false;
        generate!!.setOnClickListener {

                mProgress!!.show()
                val pdfFile = File(pdfDir, "aalertpresReport.pdf")

            scrollView!!.isDrawingCacheEnabled = true;
            val screen =
                getBitmapFromView(scrollView!!)



                try
                {
                    val document = Document()
                    PdfWriter.getInstance(document, FileOutputStream(pdfFile))
                    document.open()

                    Toast.makeText(this@AlertReport, "Great!! Your Summary Report is Created and Saved. Check your Documents folder", Toast.LENGTH_LONG).show()

                    mProgress!!.dismiss()

                    val stream = ByteArrayOutputStream()
                    screen!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val byteArray = stream.toByteArray()
                    addImage(document, byteArray)
                    document.close()
                }
                catch (e:Exception) {
                    e.printStackTrace()
                }


            //  convertCertViewToImage()
         }
        getStudent()

    }




    private fun addImage(
        document: Document,
        byteArray: ByteArray) {

        var image: Image? = null
        try {
            image = Image.getInstance(byteArray)
        } catch (e: BadElementException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: MalformedURLException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        // image.scaleAbsolute(150f, 150f);
        try {
            document.add(image)
        } catch (e: DocumentException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }

    fun getBitmapFromView(view: View): Bitmap? {
        //Define a bitmap with the same size as the view
        val returnedBitmap = Bitmap.createBitmap(
            view.width,
            view.height,
            Bitmap.Config.ARGB_8888
        )
        //Bind a canvas to it
        val canvas = Canvas(returnedBitmap)
        //Get the view's background
        val bgDrawable: Drawable? = view.background
        if (bgDrawable != null) //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas) else  //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE)
        // draw the view on the canvas
        view.draw(canvas)
        //return the bitmap
        return returnedBitmap
    }
//    private fun getBitmapFromView(v: RelativeLayout): Bitmap? {
//        v.layoutParams = RelativeLayout.LayoutParams(
//            RelativeLayout.LayoutParams.WRAP_CONTENT,
//            RelativeLayout.LayoutParams.WRAP_CONTENT
//        )
//        v.measure(
//            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
//        )
//        v.layout(0, 0, v.measuredWidth, v.measuredHeight)
//        val b = Bitmap.createBitmap(
//            v.measuredWidth,
//            v.measuredHeight,
//            Bitmap.Config.ARGB_8888
//        )
//        val c = Canvas(b)
//        v.draw(c)
//        return b
//    }


    private fun getStudent() {

        val alert_type: String? = null
        val rg: String? = null
        val name: String? = null
        val locations: String? = null
        val notes: String? = null


        val alert_name: String? = null
        val rl: String? = null
        val mssdn: String? = null
        val userid: String? = null

        val modified: String? = null
        val attachment: String? = null

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
        val api: GetAlertPost = retrofit.create(
            GetAlertPost::class.java)
        val call: Call<String>? = api.getAlert(mid,alert_name,alert_type,rl,mssdn,userid,name,rg,locations,attachment,notes,created, modified)
        call?.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                Log.d("Responsestring", response.toString())
                //Toast.makeText()
                if (response.isSuccessful) {
                    if (response.body() != null) {


                        Log.d("onSuccess", response.body().toString())
                        //Toast.makeText(this@LoginActivity, "Success"  + response.body(), Toast.LENGTH_LONG).show()
                        val jsonresponse = response.body().toString()
                        parseLoginData(jsonresponse)
                    } else {
                        Log.i(
                            "onEmptyResponse",
                            "Returned empty response"
                        ) //Toast.makeText(getContext(),"Nothing returned",Toast.LENGTH_LONG).show();
                    }
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Log.i("onEmptyResponse", "" + t) //
            }
        })
    }


    private fun parseLoginData(jsonresponse: String) {
        try {
            val jsonObject = JSONObject(jsonresponse)
            alertyp = jsonObject.getString("alert_type")
            alert_type!!.text = alertyp
            rg = jsonObject.getString("rg")
            response_group!!.text = rg
            generate!!.isEnabled = true

            fullnaem!!.text = usr


            levelresp!!.text = rl


            modified = jsonObject.getString("modified")

            neutralized!!.text = modified



            posts = jsonObject.getString("fullname")
            postedBy!!.text = posts


            var currentTime: String =
                SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            created = jsonObject.getString("created")
            createdOn!!.text = currentTime
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }




    private fun counts() {

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

        params["alert_id"] = mid!!


        val api: AlertCounts = retrofit.create(AlertCounts::class.java)
        val call: Call<ResponseBody> = api.Mark(params)

        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                //Toast.makeText()

                Log.d("Call request", call.request().toString());
                Log.d("Response raw header", response.headers().toString());
                Log.d("Response raw", response.toString());
                Log.d("Response code", response.code().toString());


                if (response.isSuccessful) {

                    if (response.body() != null) {
                        val remoteResponse = response.body()!!.string()

                        Log.d("test", remoteResponse)
                        parseDetails(remoteResponse)

                        //Toast.makeText(this@ActiveAlerts,"Nothing "  + remoteResponse,Toast.LENGTH_LONG).show();

                    } else {

                        Log.i(
                            "onEmptyResponse",
                            "Returned empty response"
                        )
                    }

                } else {

                    Log.d("BAYO", response.code().toString())

                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.i("onEmptyResponse", "" + t) //
            }
        })
    }

    private fun parseDetails(remoteResponse: String) {
        try {
            val o = JSONObject(remoteResponse)

            val array: JSONArray = o.getJSONArray("records")

            for (i in 0 until array.length()) {
                val jsonObject: JSONObject = array.getJSONObject(i)


//                Toast.makeText(this@AlertReport, array.getJSONObject(i) , Toast.LENGTH_LONG).show()

                ignore_count = jsonObject.getString("ignore_counts")
                if (jsonObject.getString("ignore_counts") > "0") {
                    ignore!!.setText("IGNORE (" + ignore_count + " )\n" + jsonObject.getString("fullname"))
                }

                shout_count = jsonObject.getString("shout_counts")
                if (jsonObject.getString("shout_counts") > "0") {
                    shout!!.setText("SHOUT (" + shout_count + " )\n" + jsonObject.getString("fullname"))
                }

                safe_count = jsonObject.getString("safe_counts")

                if (jsonObject.getString("safe_counts") > "0") {
                    safe!!.setText("SAFE (" + safe_count + ")\n" + jsonObject.getString("fullname"))
                }


                elevate_count = jsonObject.getString("elevate_counts")
                if (jsonObject.getString("elevate_counts") > "0") {
                    elevates!!.setText("ELEVATE (" + elevate_count + " )\n" + jsonObject.getString("fullname"))
                }

                share_count = jsonObject.getString("share_counts")
                if (jsonObject.getString("share_counts") > "0") {

                    mshare!!.setText("SHARE (" + share_count + " )\n" + jsonObject.getString("fullname"))
                }

                neutral_count = jsonObject.getString("neutralized_counts")
                if (jsonObject.getString("neutralized_counts") > "0") {
                    nutralize!!.setText(
                        "NEUTRALIZED (" + neutral_count + " )\n" + jsonObject.getString(
                            "fullname"
                        )
                    )
                }
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }


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
                finish()
//                startActivity(Intent(this@AlertReport, HomePage::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

        return true
    }



    override fun onBackPressed() {
        // close search view on back button pressed

       finish()
    }

}
