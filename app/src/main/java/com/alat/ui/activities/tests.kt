package com.alat.ui.activities

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.alat.R
import fr.ganfra.materialspinner.MaterialSpinner


class tests : AppCompatActivity() {


    private val ITEMS1 = arrayOf("YES", "NO")
    private val ITEMS2 = arrayOf("security", "medical", "fire", "car", "towing")
    var spinner: MaterialSpinner? = null
    var spinner_2: MaterialSpinner? = null
    var selectedItem: String? = null
    var selectedItem2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sample)

        //Adapters
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ITEMS1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner = findViewById<View>(R.id.rsp) as MaterialSpinner
        spinner?.adapter = adapter
        spinner!!.isSelected = false;  // otherwise listener will be called on initialization
        spinner!!.setSelection(0, true)
        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                arg0: AdapterView<*>?, arg1: View?,
                arg2: Int, arg3: Long
            ) {
                if (spinner!!.selectedItem == null) {
                    spinner_2!!.visibility = View.GONE
                    // Toast.makeText(this@CreateAlert, "Please select an Alert Type", Toast.LENGTH_LONG).show();
                    return
                } else {
                    selectedItem = spinner!!.selectedItem.toString()

                    if (selectedItem == "YES"){
                        spinner_2!!.visibility = View.VISIBLE
                    }else{
                        spinner_2!!.visibility = View.GONE
                    }
                    // Toast.makeText(this@NFCWrite, tv, Toast.LENGTH_LONG).show();
                }
                // TODO Auto-generated method stub
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        }

        val adapter1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, ITEMS2)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_2 = findViewById<View>(R.id.nature_response) as MaterialSpinner
        spinner_2?.adapter = adapter1
        spinner_2!!.isSelected = false;  // otherwise listener will be called on initialization
        spinner_2!!.setSelection(0, true)
        spinner_2?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                arg0: AdapterView<*>?, arg1: View?,
                arg2: Int, arg3: Long
            ) {
                if (spinner_2!!.selectedItem == null) {
                    // Toast.makeText(this@CreateAlert, "Please select an Alert Type", Toast.LENGTH_LONG).show();
                    return
                } else {
                    selectedItem2 = spinner_2!!.selectedItem.toString()

                    // Toast.makeText(this@NFCWrite, tv, Toast.LENGTH_LONG).show();
                }
                // TODO Auto-generated method stub
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        }




    }


}