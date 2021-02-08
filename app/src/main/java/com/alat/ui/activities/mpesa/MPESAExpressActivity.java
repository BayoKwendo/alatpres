package com.alat.ui.activities.mpesa;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.alat.HomePage;
import com.alat.R;
import com.alat.helpers.Constants;
import com.alat.helpers.PromptPopUpView;
import com.alat.interfaces.UpdateSubscription;
import com.androidstudy.daraja.Daraja;
import com.androidstudy.daraja.DarajaListener;
import com.androidstudy.daraja.model.AccessToken;
import com.androidstudy.daraja.model.LNMExpress;
import com.androidstudy.daraja.model.LNMResult;
import com.androidstudy.daraja.util.TransactionType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import timber.log.Timber;


public class MPESAExpressActivity extends AppCompatActivity {

    @BindView(R.id.editTextPhoneNumber)
    EditText editTextPhoneNumber;
    @BindView(R.id.sendButton)
    Button sendButton;
    String p;
    AlertDialog dialog;
    Button send, btn_back, confirmPay;
    EditText phone, mpesa_code;
    ConstraintLayout constraintLayout, constraintLayout2;
    //ProgressDialog dialog;
    private PromptPopUpView promptPopUpView;
    private static final String OPTIONAL_ZERO = "(0";
    private static final String OPTIONAL_ZERO_REGEX = Pattern.quote(OPTIONAL_ZERO);
    private ProgressDialog mProgressDialog, mProgress;
    //Delare Daraja :: Global Variable
    Daraja daraja;
    String phoneNumber;
    String price;
    String time;
    Thread thread;
    String userid, firstname, email, sname, dob, role,gender, mssdn, idNo, county, clients, account_status, responseprovider;
    private volatile boolean running = true;

    SharedPreferences pref;

    private boolean Execute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpesa);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        pref =
                getSharedPreferences("MyPref", 0) ;// 0 - for private mode
        userid = pref.getString("userid", null);
        firstname = pref.getString("fname", null);
        sname = pref.getString("lname", null);
        email = pref.getString("email", null);
        dob = pref.getString("dob", null);
        role = pref.getString("role", null);
        gender = pref.getString("gender", null);
        mssdn = pref.getString("mssdn", null);
        idNo = pref.getString("idNo", null);
        county = pref.getString("county", null);
        clients = pref.getString("clients", null);
        account_status = pref.getString("account_status", null);
        responseprovider = pref.getString("response_provider", null);
        price = getIntent().getStringExtra("price");
        time = getIntent().getStringExtra("time");


       //Toast.makeText(this,  time, Toast.LENGTH_SHORT).show();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        Execute = false;

        // price = preferenceHelper.getPrice();
        //docid = preferenceHelper.getPay();

        send = findViewById(R.id.send);
        mpesa_code = findViewById(R.id.mpesa_code);
        btn_back = findViewById(R.id.btn_back);
        confirmPay = findViewById(R.id.btn_confirm);

        constraintLayout = findViewById(R.id.view1);

        constraintLayout2 = findViewById(R.id.view2);
        mProgress = new ProgressDialog(MPESAExpressActivity.this);
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(true);

        mProgressDialog = new ProgressDialog(MPESAExpressActivity.this);
        mProgressDialog.setMessage("Checking...please wait");
        mProgressDialog.setCancelable(false);
        btn_back.setOnClickListener(view -> {
            constraintLayout.setVisibility(View.VISIBLE);
            constraintLayout2.setVisibility(View.GONE);

        });


        confirmPay.setOnClickListener(view -> {
            if (!isNetworkAvailable()) {
                network();
            }
            phoneNumber = editTextPhoneNumber.getText().toString().trim();
            if (TextUtils.isEmpty(phoneNumber)) {
                editTextPhoneNumber.requestFocus();
                editTextPhoneNumber.setError("Re-Enter Phone No. to confirm");
                return;
            }
            mProgress.show();
            if (!isNetworkAvailable()) {
                network();
            }

        });

        if (!isNetworkAvailable()) {
            network();
        }

        send.setOnClickListener(view -> {
            String transaction = mpesa_code.getText().toString();
            int length = mpesa_code.getText().length();
            if (transaction.isEmpty()) {
                mpesa_code.setError("Transaction code Require");
                mProgress.dismiss();

            } else if (length < 10 || length > 10) {
                mpesa_code.setError("Code not Valid");
                mProgress.dismiss();

            } else {
                enterTransaction();
            }
        });

        //Init Daraja
        //TODO :: REPLACE WITH YOUR OWN CREDENTIALS  :: THIS IS SANDBOX DEMO
        daraja = Daraja.with("ATdLu5SiNUEpBTq2MTBCQOJHB9mblqnE", "49WWldD96Qu44MPb", new DarajaListener<AccessToken>() {
            @Override
            public void onResult(@NonNull AccessToken accessToken) {
                Log.i(MPESAExpressActivity.this.getClass().getSimpleName(), accessToken.getAccess_token());
              //  Toast.makeText(MPESAExpressActivity.this, "TOKEN : " + accessToken.getAccess_token(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Log.e(MPESAExpressActivity.this.getClass().getSimpleName(), error);
            }
        });

        //TODO :: THIS IS A SIMPLE WAY TO DO ALL THINGS AT ONCE!!! DON'T DO THIS :)
        sendButton.setOnClickListener(v -> {
            //Get Phone Number from User Input
            phoneNumber = editTextPhoneNumber.getText().toString().trim();
            if (TextUtils.isEmpty(phoneNumber)) {
                editTextPhoneNumber.requestFocus();
                editTextPhoneNumber.setError("Please Provide a Your Number");
                return;
            }

            p = phoneNumber.replaceFirst("^0+(?!$)", "");
            getJSON("http://167.172.17.121/api/transactions/read.php?phone=" + 254 + p);

            mProgress.show();
            if (!isNetworkAvailable()) {
                network();
            }

            //Toast.makeText(this,  254+p, Toast.LENGTH_SHORT).show();
        });
    }

    void enterTransaction() {
        mProgress.show();
        String p = mpesa_code.getText().toString().trim();
        getJSO("http://167.172.17.121/api/transactions/update.php?phone=" + p+"&userid="+ userid);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getJSO(final String urlWebService) {

        class GetJSON extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    loadIntoListVie(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    private void getpayments(final String urlWebService) {

        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    checkpayment(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }


    private void checkpayment(String json) throws JSONException {
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.getString("status").equals("true")) {
//                mProgressDialog.show();
                if (!mProgressDialog.isShowing()) {
                    mProgressDialog.show();
                }
                    JSONArray dataArray = jsonObject.getJSONArray("postData");
                    for (int i = 0; i < dataArray.length(); i++) {
                        if (dataArray.length() == 1) {
                            editTextPhoneNumber.setEnabled(false);
                            JSONObject dataobj = dataArray.getJSONObject(i);
                            constraintLayout.setVisibility(View.GONE);
                            constraintLayout2.setVisibility(View.VISIBLE);
                            mpesa_code.setText(dataobj.getString("TransID"));
//                        Toast.makeText(this, "Confirm your transaction", Toast.LENGTH_SHORT).show();
                            mProgress.dismiss();
                            String p = dataobj.getString("TransID");
                            getJSO("http://167.172.17.121/api/transactions/update.php?phone=" + p+"&userid="+ userid);
                            constraintLayout.setVisibility(View.VISIBLE);
                            constraintLayout2.setVisibility(View.GONE);
                            phoneNumber = editTextPhoneNumber.getText().toString().trim();
                            editTextPhoneNumber.setEnabled(true);
                        }
                    }
                } else {
                mProgressDialog.dismiss();

//                if(mProgressDialog != null) {
//                    if(mProgressDialog.isShowing()) { //check if dialog is showing.
//
//                        //get the Context object that was used to great the dialog
//                        Context context = ((ContextWrapper)mProgressDialog.getContext()).getBaseContext();
//
//                        //if the Context used here was an activity AND it hasn't been finished or destroyed
//                        //then dismiss it
//                        if(context instanceof Activity) {
//                            if(!((Activity)context).isFinishing() && !((Activity)context).isDestroyed())
//                                mProgressDialog.dismiss();
//                        } else //if the Context used wasnt an Activity, then dismiss it too
//                    }
//                    mProgressDialog = null;
//                }
//
//                }
            }
        } catch (JSONException e) {


            e.printStackTrace();
        }
    }

    private void getJSON(final String urlWebService) {

        @SuppressLint("StaticFieldLeak")
        class GetJSON extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    loadIntoListView(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }


    private void loadIntoListView(String json) throws JSONException {

        try {
            running = false;
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.getString("status").equals("true")) {
                JSONArray dataArray = jsonObject.getJSONArray("postData");
                for (int i = 0; i < dataArray.length(); i++) {

                    if (dataArray.length() == 1) {
                        mProgressDialog.show();
                        editTextPhoneNumber.setEnabled(false);
                        JSONObject dataobj = dataArray.getJSONObject(i);
                        constraintLayout.setVisibility(View.GONE);
                        constraintLayout2.setVisibility(View.VISIBLE);
                        mpesa_code.setText(dataobj.getString("TransID"));
//                        Toast.makeText(this, "Confirm your transaction", Toast.LENGTH_SHORT).show();
                        mProgress.dismiss();
                        String p = dataobj.getString("TransID");
                        getJSO("http://167.172.17.121/api/transactions/update.php?phone=" + p+"&userid="+ userid);
                        constraintLayout.setVisibility(View.VISIBLE);
                        constraintLayout2.setVisibility(View.GONE);
                        phoneNumber = editTextPhoneNumber.getText().toString().trim();
                        editTextPhoneNumber.setEnabled(true);

                    }

                }
            }  else {
                // Toast.makeText(this, "" + price, Toast.LENGTH_SHORT).show();

                //TODO :: REPLACE WITH YOUR OWN CREDENTIALS  :: THIS IS SANDBOX DEMO
                LNMExpress lnmExpress = new LNMExpress (
                        "4036601",
                        "c3b0702afc4d3133916e1ff36821b9b407a8f4da06e30afbdcd6907d940ef033",  //https://developer.safaricom.co.ke/test_credentials
                        TransactionType.CustomerPayBillOnline,
                         "1",
                        "254717629732",
                        "4036601",
                         phoneNumber,
                        "http://167.172.17.121/api/transactions/alatpres.php",
                        "alatpres",
                        "Pay No"
                );

                //This is the
                daraja.requestMPESAExpress(lnmExpress,
                        new DarajaListener<LNMResult>() {
                            @Override
                            public void onResult(@NonNull LNMResult lnmResult) {
                                Timber.i(lnmResult.ResponseDescription);
                                mProgress.dismiss();
                                Toast.makeText(MPESAExpressActivity.this, lnmResult.ResponseDescription, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(String error) {
                                Timber.i(error);
                                mProgress.dismiss();
                                Toast.makeText(MPESAExpressActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void loadIntoListVie(String json) throws JSONException {

        try {
            JSONObject jsonObject = new JSONObject(json);

            if (jsonObject.getString("status").equals("true")) {

                JSONArray dataArray = jsonObject.getJSONArray("postData");

                for (int i = 0; i < dataArray.length(); i++) {

                    JSONObject dataobj = dataArray.getJSONObject(i);

                    //Toast.makeText(this, "" + dataobj.getString("TransID"), Toast.LENGTH_SHORT).show();

                    String p = mpesa_code.getText().toString().trim();

                    if (p.equals(dataobj.getString("TransID"))) {
                        loginUser();

                    } else {

                        promptPopUpView = new PromptPopUpView(this);
                        promptPopUpView.changeStatus(1, "WRONG CODE");


                        AlertDialog dialog = new AlertDialog.Builder(Objects.requireNonNull(this))
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setCancelable(true)
                                .setView(promptPopUpView)
                                .show();


                        Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        // button.setEnabled(false);
                        button.setTextColor(getResources().getColor(R.color.colorBlack));

                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void network() {
        promptPopUpView = new PromptPopUpView(this);

        promptPopUpView.changeStatus(1, "Network Error");


        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(Objects.requireNonNull(this))
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        recreate();
                    }
                })
                .setNegativeButton("Exit", (dialog12, which) -> finish())
                .setCancelable(false)
                .setView(promptPopUpView)
                .show();

        Button btnPositive = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE);
        Button btnNegative = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
        layoutParams.weight = 10;

        btnPositive.setLayoutParams(layoutParams);
        btnNegative.setLayoutParams(layoutParams);
        btnPositive.setTextColor(getResources().getColor(R.color.success));
        btnNegative.setTextColor(getResources().getColor(R.color.error));
    }


    private boolean isNetworkAvailable() {
        // Using ConnectivityManager to check for Network Connection
        // Using ConnectivityManager to check for Network Connection
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


    @Override
    protected void onResume() {
        super.onResume();
        mProgressDialog.show();
        phoneNumber = editTextPhoneNumber.getText().toString().trim();
        p = phoneNumber.replaceFirst("^0+(?!$)", "");
        if (p != null) {
//            mProgressDialog.dismiss();
            thread = new Thread() {
                @Override
                public void run() {
                    try {
                        while (running) {
                            synchronized (this) {
                                wait(3000);
                                MPESAExpressActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        getpayments("http://167.172.17.121/api/transactions/read.php?phone=" + 254 + p);

                                    }
                                });
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();

        } else {
            mProgressDialog.dismiss();
        }

        if (Execute) {
            recreate();
        } else {
            Execute = true;
        }

        ;
        // recreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        running = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if ((mProgressDialog != null) && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @Override

    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    private void loginUser() {

        OkHttpClient httpClient = new OkHttpClient();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .client(httpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())

                .build();

        UpdateSubscription api = retrofit.create(UpdateSubscription.class);

        HashMap<String, String> meMap= new HashMap<>();
        meMap.put("subscription_date",time);
        meMap.put("userid",userid);

        Call<String> call = api.AddM(meMap);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i("Responsestring", response.body().toString());
                //Toast.makeText()
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.i("onSuccess", response.body().toString());

                        String jsonresponse = response.body().toString();
                        parseLoginData(jsonresponse);

                    } else {
                        Log.i("onEmptyResponse", "Returned empty response");//Toast.makeText(getContext(),"Nothing returned",Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    private void parseLoginData(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("true")) {
                mProgressDialog.dismiss();

                PromptPopUpView promptPopUpView = new PromptPopUpView(MPESAExpressActivity.this);
                promptPopUpView.changeStatus(2, "Account Upgraded successfully");
                androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(MPESAExpressActivity.this)

                        .setCancelable(false)
                        .setView(promptPopUpView)
                        .show();
                new Handler().postDelayed(new Runnable() {
                    public void run() {
//                        Toast.makeText(MPESAExpressActivity.this,  "You have to Login again", Toast.LENGTH_SHORT).show();
                        SharedPreferences preferences =getSharedPreferences("MyPref",0);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("isLogin", true);
                        editor.putString("fname", firstname);
                        editor.putString("lname", sname);
                        editor.putString("email", email);
                        editor.putString("dob", dob);
                        editor.putString("role", role);
                        editor.putString("mssdn",mssdn );
                        editor.putString("gender", gender);
                        editor.putString("idNo", idNo);
                        editor.putString("county", county);
                        editor.putString("userid", userid);
                        editor.putString("mstatus", "1");
                        editor.putString("account_status", "1");
                        editor.putString("clients", clients);
                        editor.putString("response_provider", responseprovider);
                        editor.clear();
                        editor.apply();
                        finish();
                        Intent intent = new Intent(MPESAExpressActivity.this, HomePage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                }, 3000);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }










}



