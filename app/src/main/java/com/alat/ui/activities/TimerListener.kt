package com.alat.ui.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import javax.inject.Inject


class TimerListener : BroadcastReceiver() {

    @Inject



    override fun onReceive(context: Context?, intent: Intent) {

        }
    }



/*
    private fun save() {
        try {
            val file = File(Environment.getExternalStorageDirectory().toString() + File.separator + "Pictures", "okoafloat.json")
            val dataToSend = JSONObject()
            val arr = JSONArray()
            if (!file.exists()) {
                val jsonObject = JSONObject()
                jsonObject.put("Message", body)
                jsonObject.put("From", mobile)
                arr.put(jsonObject)
                dataToSend.put("MPESA", arr)
                val userString = dataToSend.toString()
                val fileWriter = FileWriter(file)
                val bufferedWriter = BufferedWriter(fileWriter)
                bufferedWriter.write(userString)
                bufferedWriter.close()
            } else {
                val fileReader = FileReader(file)
                val bufferedReader = BufferedReader(fileReader)
                val stringBuilder = StringBuilder()
                var line = bufferedReader.readLine()
                while (line != null) {
                    stringBuilder.append(line).append("\n")
                    line = bufferedReader.readLine()
                }
                bufferedReader.close()
                val response = stringBuilder.toString()
                val gson = Gson()
                val inputObj = gson.fromJson(response, JsonObject::class.java)
                val newObject = JsonObject()
                newObject.addProperty("Message", body)
                newObject.addProperty("From", mobile)
                inputObj["MPESA"].asJsonArray.add(newObject)
                val fileWriter = FileWriter(file)
                val bufferedWriter = BufferedWriter(fileWriter)
                bufferedWriter.write(inputObj.toString())
                bufferedWriter.close()
                Log.i("ARRAY", inputObj.toString())
            }
        } catch (ex: JSONException) {
            ex.printStackTrace()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }
*/
