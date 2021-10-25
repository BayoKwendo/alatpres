package adil.dev.lib.materialnumberpicker.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import adil.dev.lib.materialnumberpicker.R;
import adil.dev.lib.materialnumberpicker.adapter.GenderAdapter;


/**
 * Created by Brian Kwendo on 13/02/2020.
 */

public class GenderDialogue extends Dialog {
    static GenderDialogue instance = null;
    Context mContext;
    GenderDialogue.OnGenderSelectListener onSelectingGender;
    RecyclerView recyclerView;
    TextView okView, cancelView, selectedTextView;
    String selectedString = "";

    public GenderDialogue(Context context) {
        super(context);
        this.mContext = context;
    }

    public static GenderDialogue getInstance() {
        return instance;
    }

    public void setOnSelectingGender(GenderDialogue.OnGenderSelectListener onSelectingGender) {
        this.onSelectingGender = onSelectingGender;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.gender_picker_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        instance = this;
        initViews();
        initValues();
        initValuesInViews();
        setOnClickListener();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        okView = findViewById(R.id.ok);
        cancelView = findViewById(R.id.cancel);
        selectedTextView = findViewById(R.id.dialog_selected_value);
    }

    private void initValues() {
        selectedString = mContext.getString(R.string.MPD_male);
    }

    private void initValuesInViews() {
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setItemViewCacheSize(2);
        selectedTextView.setText(selectedString);
        GenderAdapter adapter = new GenderAdapter(mContext );
        adapter.setOnItemClickCallBack(new GenderAdapter.ItemClickCallBack() {
            @Override
            public void onItemClicked(String gender) {
                selectedString = gender;
                selectedTextView.setText(selectedString);
                onSelectingGender.onSelectingGender(selectedString);
                dismiss();

            }
        });
        recyclerView.setAdapter(adapter);

    }

//    private void requestJsonObject(){
//        RequestQueue queue = Volley.newRequestQueue(   mContext);
//        String url ="http://toscanyacademy.com/blog/mp.php";
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
////                Log.d(TAG, "Response " + response);
//                GsonBuilder builder = new GsonBuilder();
//                Gson mGson = builder.create();
//                List<Gender> posts = new ArrayList<Gender>();
//                posts = Arrays.asList(mGson.fromJson(response, Gender[].class));
//
//
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d(TAG, "Error " + error.getMessage());
//            }
//        });
//        queue.add(stringRequest);
//    }

    private void setOnClickListener() {
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        okView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectingGender.onSelectingGender(selectedString);
                dismiss();
            }
        });
    }

    public interface OnGenderSelectListener {
        void onSelectingGender(String value);
    }

}
