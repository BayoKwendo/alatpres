package adil.dev.lib.materialnumberpicker.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import adil.dev.lib.materialnumberpicker.R;
import adil.dev.lib.materialnumberpicker.adapter.CountyAdapter;


/**
 * Created by Brian Kwendo on 13/02/2020.
 */
public class CountyDialog extends Dialog {
    static CountyDialog instance = null;
    Context mContext;
    CountyDialog.OncountySelectListener onSelectingcounty;
    RecyclerView recyclerView;
    TextView okView, cancelView, selectedTextView;
    String selectedString = "";
    CountyAdapter adapter;

    EditText etSearch;

    public CountyDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public static CountyDialog getInstance() {
        return instance;
    }

    public void setOnSelectingcounty(CountyDialog.OncountySelectListener onSelectingcounty) {
        this.onSelectingcounty = onSelectingcounty;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.county_picker_dialog);
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
        etSearch=(EditText)findViewById(R.id.etSearch);
        etSearch.setText("");
        selectedTextView = findViewById(R.id.dialog_selected_value);

    }

    private void initValues() {
        selectedString = mContext.getString(R.string.baringo);
    }

    private void initValuesInViews() {
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setItemViewCacheSize(2);
        selectedTextView.setText(selectedString);
        adapter = new CountyAdapter(mContext);
        adapter.setOnItemClickCallBack(new CountyAdapter.ItemClickCallBack() {
            @Override
            public void onItemClicked(String county) {
                selectedString = county;
                selectedTextView.setText(selectedString);
            }
        });
        recyclerView.setAdapter(adapter);
    }

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
                onSelectingcounty.onSelectingcounty(selectedString);
                dismiss();
            }
        });
    }

    public interface OncountySelectListener {
        void onSelectingcounty(String value);
    }

}
