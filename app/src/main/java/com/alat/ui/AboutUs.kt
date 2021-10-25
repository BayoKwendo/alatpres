package com.alat.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alat.HomePage
import com.alat.R
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element
import java.util.*

class AboutUs : AppCompatActivity() {
    var version: String? = null
    var verCode = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aboutus)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        try {
            val pInfo =
                this.packageManager.getPackageInfo(packageName, 0)
            version = pInfo.versionName
            verCode = pInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        val aboutPage = AboutPage(this)
            .enableDarkMode(false)
            .setDescription("AlatPres")
            .setImage(R.mipmap.ic_launcher)
            .addItem(Element().setTitle("Version Name $version"))
//            .addItem(Element().setTitle("Version Code $verCode"))
            .addGroup("Connect with us")
            .addEmail("support@alatpres.com")
            .addWebsite("http://alatpres.com/")
            .addFacebook("Alatpres-103664461222667")
            .addTwitter("alatpres")
            .addPlayStore("com.alat")
            .addItem(copyRightsElement)
            .create()
        setContentView(aboutPage)
    }

    val copyRightsElement: Element
        get() {
            val copyRightsElement = Element()
            val copyrights = String.format(
                getString(R.string.copy_right),
                Calendar.getInstance()[Calendar.YEAR]
            )
            copyRightsElement.title = copyrights
            copyRightsElement.iconDrawable = R.drawable.copy
            copyRightsElement.autoApplyIconTint = true
            copyRightsElement.iconTint = mehdi.sakout.aboutpage.R.color.about_item_icon_color
            copyRightsElement.iconNightTint = android.R.color.white
            copyRightsElement.gravity = Gravity.CENTER
            copyRightsElement.onClickListener = View.OnClickListener {
                Toast.makeText(
                    this@AboutUs,
                    copyrights,
                    Toast.LENGTH_SHORT
                ).show()
            }
            return copyRightsElement
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}