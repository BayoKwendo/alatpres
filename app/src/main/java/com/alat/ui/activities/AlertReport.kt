package com.alat.ui.activities

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.alat.HomePage
import com.alat.R
import com.alat.helpers.Constants
import com.alat.interfaces.GetAlertPost
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
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
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class AlertReport : AppCompatActivity() {

    var mid: String? = null
    var mToolbar: Toolbar? = null

    var generate: Button? = null
    var rl: String? = null



    var response_group: TextView? = null
    var alert_type: TextView? = null


    var fullnaem: TextView? = null
    var levelresp: TextView? = null

    var createdOn: TextView? = null


    private var IS_MANY_PDF_FILE = false

    /**
     * This is identify to number of pdf file. If pdf model list size > sector so we have create many file. After that we have merge all pdf file into one pdf file
     */
    private val SECTOR = 100 // Default value for one pdf file.

    private var START = 0
    private var END = SECTOR
    private var NO_OF_PDF_FILE = 1
    private var NO_OF_FILE = 0
    private var LIST_SIZE = 0
    private var progressDialog: ProgressDialog? = null
    var rg: String? = null
    var view: View? = null

    var alertyp: String? = null
    var usr: String? = null
    var loc: String? = null
    var created: String? = null
   var scrollView: ScrollView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary_report)
        mid = intent.getStringExtra("alertSelect")
        rl = intent.getStringExtra("level")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        alert_type = findViewById(R.id.textView9)

        scrollView = findViewById(R.id.scrollView)

        response_group = findViewById(R.id.textView7)
        fullnaem =findViewById(R.id.textView21)
        levelresp = findViewById(R.id.textView15)
        generate = findViewById(R.id.generate)
        createdOn = findViewById(R.id.textView11)
        view = findViewById(R.id.view)

         generate!!.setOnClickListener {

           //  convertCertViewToImage()
         }
        getStudent()

    }

    fun convertCertViewToImage() {
        scrollView!!.setDrawingCacheEnabled(true)
        scrollView!!.measure(
            View.MeasureSpec.makeMeasureSpec(
                0,
                View.MeasureSpec.UNSPECIFIED
            ),
            View.MeasureSpec.makeMeasureSpec(
                0,
                View.MeasureSpec.UNSPECIFIED
            )
        )
        scrollView!!.layout(0, 0, scrollView!!.getMeasuredWidth(), scrollView!!.getMeasuredHeight())
        scrollView!!.buildDrawingCache()
        val bm: Bitmap = Bitmap.createBitmap(scrollView!!.getDrawingCache())
        scrollView!!.setDrawingCacheEnabled(false) // clear drawing cache
        val share =
            Intent(Intent.ACTION_SEND)
        share.type = "image/jpg"
        val bytes = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val f =
            File(getExternalFilesDir(null)!!.absolutePath + File.separator + "Certificate" + File.separator + "myCertificate.jpg")
        f.createNewFile()
        val fo = FileOutputStream(f)
        fo.write(bytes.toByteArray())
    }

    private fun getStudent() {

        val alert_type: String? = null
        val rg: String? = null
        val name: String? = null
        val locations: String? = null
        val notes: String? = null

        val created: String? = null
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
        val call: Call<String>? = api.getAlert(mid,alert_type,name,rg,locations,notes,created)
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
            usr = jsonObject.getString("fullname")
            fullnaem!!.text = usr

            levelresp!!.text = rl
            var currentTime: String =
                SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            created = jsonObject.getString("created")

            createdOn!!.text = currentTime




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
                startActivity(Intent(this@AlertReport, HomePage::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

        return true
    }



    //    private fun generatePdfReport() {
//
//
//        // NO_OF_FILE : This is identify to one file or many file have to created
//        LIST_SIZE = pdfModels.size()
//        NO_OF_FILE = LIST_SIZE / SECTOR
//        if (LIST_SIZE % SECTOR !== 0) {
//            NO_OF_FILE++
//        }
//        if (LIST_SIZE > SECTOR) {
//            IS_MANY_PDF_FILE = true
//        } else {
//            END = LIST_SIZE
//        }
//        createPDFFile()
//    }
//
//    private fun createProgressBarForPDFCreation(maxProgress: Int) {
//        progressDialog = ProgressDialog(this)
//        progressDialog.setMessage(
//            String.format(
//                getString(R.string.msg_progress_pdf),
//                maxProgress.toString()
//            )
//        )
//        progressDialog.setCancelable(false)
//        progressDialog.setIndeterminate(false)
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
//        progressDialog.setMax(maxProgress)
//        progressDialog.show()
//    }
//
//    private fun createProgressBarForMergePDF() {
//        progressDialog = ProgressDialog(this)
//        progressDialog.setMessage(getString(R.string.msg_progress_merger_pdf))
//        progressDialog.setCancelable(false)
//        progressDialog.show()
//    }
//
//    /**
//     * This function call with recursion
//     * This recursion depend on number of file (NO_OF_PDF_FILE)
//     */
//    private fun createPDFFile() {
//
//        // Find sub list for per pdf file data
//        val pdfDataList: List<PDFModel> = pdfModels.subList(START, END)
//        PdfBitmapCache.clearMemory()
//        PdfBitmapCache.initBitmapCache(applicationContext)
//        val pdfCreationUtils =
//            PDFCreationUtils(this@PdfCreationActivity, pdfDataList, LIST_SIZE, NO_OF_PDF_FILE)
//        if (NO_OF_PDF_FILE === 1) {
//            createProgressBarForPDFCreation(PDFCreationUtils.TOTAL_PROGRESS_BAR)
//        }
//        pdfCreationUtils.createPDF(object : PDFCallback() {
//            fun onProgress(i: Int) {
//                progressDialog.setProgress(i)
//            }
//
//            fun onCreateEveryPdfFile() {
//                // Execute may pdf files and this is depend on NO_OF_FILE
//                if (IS_MANY_PDF_FILE) {
//                    NO_OF_PDF_FILE++
//                    if (NO_OF_FILE === NO_OF_PDF_FILE - 1) {
//                        progressDialog.dismiss()
//                        createProgressBarForMergePDF()
//                        pdfCreationUtils.downloadAndCombinePDFs()
//                    } else {
//
//                        // This is identify to manage sub list of current pdf model list data with START and END
//                        START = END
//                        if (LIST_SIZE % SECTOR !== 0) {
//                            if (NO_OF_FILE === NO_OF_PDF_FILE) {
//                                END = START - SECTOR + LIST_SIZE % SECTOR
//                            }
//                        }
//                        END = SECTOR + END
//                        createPDFFile()
//                    }
//                } else {
//                    // Merge one pdf file when all file is downloaded
//                    progressDialog.dismiss()
//                    createProgressBarForMergePDF()
//                    pdfCreationUtils.downloadAndCombinePDFs()
//                }
//            }
//
//            fun onComplete(filePath: String?) {
//                progressDialog.dismiss()
//                if (filePath != null) {
//                    btnPdfPath.setVisibility(View.VISIBLE)
//                    btnPdfPath.setText("PDF path : $filePath")
//                    Toast.makeText(
//                        this@PdfCreationActivity,
//                        "pdf file $filePath",
//                        Toast.LENGTH_LONG
//                    ).show()
//                    btnSharePdfFile.setVisibility(View.VISIBLE)
//                    btnSharePdfFile.setOnClickListener(View.OnClickListener { sharePdf(filePath) })
//                }
//            }
//
//            fun onError(e: Exception) {
//                Toast.makeText(this@PdfCreationActivity, "Error  " + e.message, Toast.LENGTH_LONG)
//                    .show()
//            }
//        })
//    }
//
//
//    private fun sharePdf(fileName: String) {
//        val emailIntent =
//            Intent(Intent.ACTION_SEND_MULTIPLE)
//        emailIntent.type = "text/plain"
//        emailIntent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
//        val uris: ArrayList<Uri> = ArrayList<Uri>()
//        val fileIn = File(fileName)
//        val u: Uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, fileIn)
//        uris.add(u)
//        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
//        try {
//            startActivity(
//                Intent.createChooser(
//                    emailIntent,
//                    getString(R.string.send_to)
//                )
//            )
//        } catch (e: ActivityNotFoundException) {
//            Toast.makeText(this, getString(R.string.error_file), Toast.LENGTH_SHORT).show()
//        }
//    }
    override fun onBackPressed() {
        // close search view on back button pressed

        startActivity(Intent(this@AlertReport, HomePage::class.java))

    }

}
