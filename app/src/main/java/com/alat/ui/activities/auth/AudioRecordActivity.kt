package com.alat.ui.activities.auth

import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.alat.R
import java.io.IOException
import java.util.*


class AudioRecordActivity : AppCompatActivity() {
    var buttonStart: Button? = null
    var buttonStop: Button? = null
    var AudioSavePathInDevice: String? = null
    var mediaRecorder: MediaRecorder? = null
    var random: Random? = null
    var RandomAudioFileName = "ABCDEFGHIJKLMNOP"
    var mediaPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)
        buttonStart = findViewById<View>(R.id.button) as Button
        buttonStop = findViewById<View>(R.id.button2) as Button
        buttonStop!!.isEnabled = false
        random = Random()
        buttonStart!!.setOnClickListener {
            if (checkPermission()) {
                AudioSavePathInDevice =
                    Environment.getExternalStorageDirectory().getAbsolutePath()
                        .toString() + "/" +
                            CreateRandomAudioFileName(5) + "AudioRecording.3gp"
                MediaRecorderReady()
                try {
                    mediaRecorder!!.prepare()
                    mediaRecorder!!.start()
                } catch (e: IllegalStateException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                } catch (e: IOException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }
                buttonStart!!.isEnabled = false
                buttonStop!!.isEnabled = true
                Toast.makeText(
                    this@AudioRecordActivity, "Recording started",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                requestPermission()
            }
        }
        buttonStop!!.setOnClickListener {
            mediaRecorder!!.stop()
            buttonStop!!.isEnabled = false
            buttonStart!!.isEnabled = true
            Toast.makeText(
                this@AudioRecordActivity, "Recording Completed",
                Toast.LENGTH_LONG
            ).show()
            val returnIntent = Intent()
            returnIntent.putExtra("result", AudioSavePathInDevice)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }
    }

    fun MediaRecorderReady() {
        mediaRecorder = MediaRecorder()
        mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder!!.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
        mediaRecorder!!.setOutputFile(AudioSavePathInDevice)
    }

    fun CreateRandomAudioFileName(string: Int): String {
        val stringBuilder = StringBuilder(string)
        var i = 0
        while (i < string) {
            stringBuilder.append(RandomAudioFileName[random!!.nextInt(RandomAudioFileName.length)])
            i++
        }
        return stringBuilder.toString()
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this@AudioRecordActivity,
            arrayOf<String>(WRITE_EXTERNAL_STORAGE, RECORD_AUDIO),
            RequestPermissionCode
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            RequestPermissionCode -> if (grantResults.size > 0) {
                val StoragePermission = grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED
                val RecordPermission = grantResults[1] ==
                        PackageManager.PERMISSION_GRANTED
                if (StoragePermission && RecordPermission) {
                    Toast.makeText(
                        this@AudioRecordActivity, "Permission Granted",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(this@AudioRecordActivity, "Permission Denied", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    fun checkPermission(): Boolean {
        val result: Int = ContextCompat.checkSelfPermission(
            applicationContext,
            WRITE_EXTERNAL_STORAGE
        )
        val result1: Int = ContextCompat.checkSelfPermission(
            applicationContext,
            RECORD_AUDIO
        )
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val RequestPermissionCode = 1
    }
}


