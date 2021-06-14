package com.alat.ui.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import com.alat.HomePage
import com.alat.R
import com.alat.helpers.LoadingUtil.enableDisableView
import com.wang.avi.AVLoadingIndicatorView

class enterpriseFAQs : Fragment() {
    var fader: FrameLayout? = null
    var mainFrame: FrameLayout? = null
    var isPageError = false
    var linear: LinearLayout? = null
    var btn: Button? = null
    var txt: TextView? = null
    var avi: AVLoadingIndicatorView? = null
    private var mWebChromeClient: MyWebChromeClient? = null

    private var mCustomView: View? = null
//    val views: View? = null

    private var progressbar: ProgressBar? = null
    private var mContentView: RelativeLayout? = null
    private var mCustomViewContainer: FrameLayout? = null
    private var mCustomViewCallback: WebChromeClient.CustomViewCallback? = null
    private var myWebView: WebView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.activity_faq, container, false)

        linear = view.findViewById(R.id.linear)
        btn = view.findViewById(R.id.reload)
        txt = view.findViewById(R.id.errText)
        progressbar = view.findViewById(R.id.progress_bar)
        fader = view.findViewById(R.id.fader)
        mainFrame = view.findViewById(R.id.mainFrame)
        avi = view.findViewById(R.id.avi)
        setLoadingAnimation()
        isPageError = false
        progressbar!!.setVisibility(View.GONE)
        btn!!.setOnClickListener(View.OnClickListener { activity!!.recreate() })
        if (!isNetworkAvailable) {
            stopLoadingAnimation()
            // Create an Alert Dialog
            val builder = AlertDialog.Builder(activity)
            // Set the Alert Dialog Message
            builder.setMessage("Internet Connection Required")
            builder.setCancelable(false)
            builder.setNegativeButton(
                "Cancel"
            ) { dialog, id -> activity!!.finish() }
            builder.setPositiveButton(
                "Retry"
            ) { dialog, id -> // Restart the Activity
                activity!!.recreate()
            }
            val alert = builder.create()
            alert.show()
        }
        myWebView = view.findViewById(R.id.webview)
        mWebChromeClient = MyWebChromeClient()
        myWebView!!.setWebChromeClient(mWebChromeClient)
        myWebView!!.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                if (isPageError) {
                    myWebView!!.setVisibility(View.GONE)
                    linear!!.setVisibility(View.VISIBLE)
                }
                stopLoadingAnimation()
                progressbar!!.setVisibility(View.GONE)
            }

            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                isPageError = true
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                setLoadingAnimation()
                progressbar!!.setVisibility(View.VISIBLE)
                return true
            }
        })
        if (isNetworkAvailable) {
            val webSettings = myWebView!!.getSettings()
            webSettings.javaScriptEnabled = true
            webSettings.domStorageEnabled = true
            webSettings.useWideViewPort = true
            webSettings.loadsImagesAutomatically = true
            webSettings.loadWithOverviewMode = true
            webSettings.setSupportMultipleWindows(true)
            webSettings.setGeolocationEnabled(true)
            myWebView!!.loadUrl("http://167.172.17.121/faq/enterprise.html") //URL input
        } else {
            Toast.makeText(activity, "No internet connection", Toast.LENGTH_SHORT).show()
        }
        return view
    }// Using ConnectivityManager to check for Network Connection

    // Private class isNetworkAvailable
    private val isNetworkAvailable: Boolean
        private get() {
            // Using ConnectivityManager to check for Network Connection
            val connectivityManager = activity!!.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager
                .activeNetworkInfo
            return activeNetworkInfo != null
        }

    fun setLoadingAnimation() {
        enableDisableView(mainFrame!!, false)
        fader!!.visibility = View.VISIBLE
        avi!!.show()
    }

    fun stopLoadingAnimation() {
        enableDisableView(mainFrame!!, true)
        fader!!.visibility = View.GONE
        avi!!.hide()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        if (item.itemId == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(activity!!)
        }
        return super.onOptionsItemSelected(item)
    }

     fun onBackPressed() {
        startActivity(Intent(activity, HomePage::class.java))
        activity!!.finish()
    }

    inner class MyWebChromeClient : WebChromeClient() {
        var LayoutParameters = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT )

        override fun onShowCustomView(view: View, callback: CustomViewCallback) {
            // if a view already exists then immediately terminate the new one
            if (mCustomView != null) {
                callback.onCustomViewHidden()
                return
            }
            mContentView = view.findViewById(R.id.activity_faq)
            mContentView!!.setVisibility(View.GONE)
            mCustomViewContainer = FrameLayout(activity!!)
            mCustomViewContainer!!.layoutParams = LayoutParameters
            mCustomViewContainer!!.setBackgroundResource(android.R.color.black)
            view.layoutParams = LayoutParameters
            mCustomViewContainer!!.addView(view)
            mCustomView = view
            mCustomViewCallback = callback
            mCustomViewContainer!!.visibility = View.VISIBLE
            activity!!.setContentView(mCustomViewContainer)
        }

        override fun onHideCustomView() {
            if (mCustomView == null) {
                return
            } else {
                // Hide the custom view.
                mCustomView!!.visibility = View.GONE
                // Remove the custom view from its container.
                mCustomViewContainer!!.removeView(mCustomView)
                mCustomView = null
                mCustomViewContainer!!.visibility = View.GONE
                mCustomViewCallback!!.onCustomViewHidden()
                // Show the content view.
                mContentView!!.visibility = View.VISIBLE
                activity!!.setContentView(mContentView)
            }
        }
    }
}