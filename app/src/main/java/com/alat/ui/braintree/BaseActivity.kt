package com.alat.ui.braintree

import android.Manifest
import android.R
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.alat.BuildConfig
import com.alat.helpers.Constants
import com.alat.interfaces.tokenBraintree
import com.alat.ui.braintree.models.ClientToken
import com.braintreepayments.api.BraintreeFragment
import com.braintreepayments.api.interfaces.BraintreeCancelListener
import com.braintreepayments.api.interfaces.BraintreeErrorListener
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener
import com.braintreepayments.api.internal.SignatureVerificationOverrides
import com.braintreepayments.api.models.PaymentMethodNonce
import com.paypal.android.sdk.onetouch.core.PayPalOneTouchCore
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

abstract class BaseActivity : AppCompatActivity(),
    ActivityCompat.OnRequestPermissionsResultCallback,
    PaymentMethodNonceCreatedListener, BraintreeCancelListener, BraintreeErrorListener,
    ActionBar.OnNavigationListener {
    @JvmField
    protected var mAuthorization: String? = null
    protected var mCustomerId: String? = null

    @JvmField
    protected var mBraintreeFragment: BraintreeFragment? = null
    private var mActionBarSetup = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        val api: tokenBraintree = retrofit.create(tokenBraintree::class.java)
        val call: Call<ResponseBody>? = api.viewRG(params)

        call?.enqueue(object : retrofit2.Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: retrofit2.Response<ResponseBody?>
            ) {

                val remoteResponse = response.body()!!.string()


                try {
                    val o = JSONObject(remoteResponse)
                    mAuthorization = o.getString("token")

                    if (savedInstanceState != null && savedInstanceState.containsKey(KEY_AUTHORIZATION)
                    ) {
                        mAuthorization =
                            savedInstanceState.getString(o.getString("token"))
                    }


                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                //   btn.text = "Proceed"
                Log.i("onEmptyResponse", "" + t) //
                // Toast.makeText(context,"Nothing ",Toast.LENGTH_LONG).show();

                //mProgress?.dismiss()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (!mActionBarSetup) {
            setupActionBar()
            mActionBarSetup = true
        }
        SignatureVerificationOverrides.disableAppSwitchSignatureVerification(
            Settings.isPayPalSignatureVerificationDisabled(this)
        )
        PayPalOneTouchCore.useHardcodedConfig(
            this,
            Settings.useHardcodedPayPalConfiguration(this)
        )
        if (BuildConfig.DEBUG && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        } else {
            handleAuthorizationState()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        handleAuthorizationState()
    }

    private fun handleAuthorizationState() {
        if (mAuthorization == null ||
            Settings.useTokenizationKey(this) && mAuthorization != Settings.getEnvironmentTokenizationKey(
                this
            ) ||
            !TextUtils.equals(
                mCustomerId,
                Settings.getCustomerId(this)
            )
        ) {
            performReset()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (mAuthorization != null) {
            outState.putString(
                KEY_AUTHORIZATION,
                mAuthorization
            )
        }
    }

    override fun onPaymentMethodNonceCreated(paymentMethodNonce: PaymentMethodNonce) {
        Log.d(
            javaClass.simpleName,
            "Payment Method Nonce received: " + paymentMethodNonce.typeLabel
        )
    }

    override fun onCancel(requestCode: Int) {
        Log.d(javaClass.simpleName, "Cancel received: $requestCode")
    }

    override fun onError(error: Exception) {
        Log.d(
            javaClass.simpleName,
            "Error received (" + error.javaClass + "): " + error.message
        )
        Log.d(javaClass.simpleName, error.toString())
        showDialog("An error occurred (" + error.javaClass + "): " + error.message)
    }

    private fun performReset() {
        mAuthorization = null
        mCustomerId = Settings.getCustomerId(this)
        if (mBraintreeFragment == null) {
            mBraintreeFragment = supportFragmentManager
                .findFragmentByTag(BraintreeFragment.TAG) as BraintreeFragment?
        }
        if (mBraintreeFragment != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                supportFragmentManager.beginTransaction().remove(mBraintreeFragment!!)
                    .commitNow()
            } else {
                supportFragmentManager.beginTransaction().remove(mBraintreeFragment!!).commit()
                supportFragmentManager.executePendingTransactions()
            }
            mBraintreeFragment = null
        }
        reset()
        fetchAuthorization()
    }

    protected abstract fun reset()
    protected abstract fun onAuthorizationFetched()
    protected fun fetchAuthorization() {
        if (mAuthorization != null) {
            onAuthorizationFetched()
        } else {
            DemoApplication.getApiClient(this)
                .getClientToken(
                    Settings.getCustomerId(this),
                    Settings.getMerchantAccountId(this),
                    object : Callback<ClientToken> {
                        override fun success(
                            clientToken: ClientToken,
                            response: Response
                        ) {
                            if (TextUtils.isEmpty(clientToken.clientToken)) {
                                showDialog("Client token was empty")
                            } else {
                                mAuthorization = clientToken.clientToken
                                onAuthorizationFetched()
                            }
                        }

                        override fun failure(error: RetrofitError) {
                            showDialog(
                                "Unable to get a client token. Response Code: " +
                                        error.response.status + " Response body: " +
                                        error.response.body
                            )
                        }
                    })
        }
    }

    protected fun showDialog(message: String?) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton(
                R.string.ok
            ) { dialog, which -> dialog.dismiss() }
            .show()
    }

    protected fun setUpAsBack() {
        if (actionBar != null) {
            actionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupActionBar() {
        val actionBar = supportActionBar
        actionBar!!.setDisplayShowTitleEnabled(false)
        actionBar.navigationMode = ActionBar.NAVIGATION_MODE_LIST
        val adapter =
            ArrayAdapter.createFromResource(
                this,
                com.alat.R.array.environments, R.layout.simple_spinner_dropdown_item
            )
        actionBar.setListNavigationCallbacks(adapter, this)
        actionBar.setSelectedNavigationItem(Settings.getEnvironment(this))
    }

    override fun onNavigationItemSelected(
        itemPosition: Int,
        itemId: Long
    ): Boolean {
        if (Settings.getEnvironment(this) != itemPosition) {
            Settings.setEnvironment(this, itemPosition)
            performReset()
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(com.alat.R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home -> {
                finish()
                true
            }
            com.alat.R.id.reset -> {
                performReset()
                true
            }
            com.alat.R.id.settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> false
        }
    }

    companion object {
        private const val KEY_AUTHORIZATION =
            "com.braintreepayments.demo.KEY_AUTHORIZATION"
    }
}