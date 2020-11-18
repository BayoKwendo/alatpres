package com.alat.ui.braintree;

import android.os.Bundle;
import android.view.MenuItem;


import androidx.appcompat.app.AppCompatActivity;

import com.alat.ui.braintree.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }
}
