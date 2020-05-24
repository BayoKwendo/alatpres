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

import java.util.Objects;

import adil.dev.lib.materialnumberpicker.R;
import adil.dev.lib.materialnumberpicker.adapter.EmployAdapter;


/**
 * Created by Brian Kwendo on 13/02/2020.
 */

public class EmployDialogue extends Dialog {
    static EmployDialogue instance = null;
    Context mContext;
    EmployDialogue.OnEmploySelectListener onSelectingEmploy;
    RecyclerView recyclerView;
    TextView okView, cancelView, selectedTextView;
    String selectedString = "";

    public EmployDialogue(Context context) {
        super(context);
        this.mContext = context;
    }

    public static EmployDialogue getInstance() {
        return instance;
    }

    public void setOnSelectingEmploy(EmployDialogue.OnEmploySelectListener onSelectingEmploy) {
        this.onSelectingEmploy = onSelectingEmploy;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.gender_picker_dialog);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
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
        selectedString = mContext.getString(R.string.employ);
    }

    private void initValuesInViews() {
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setItemViewCacheSize(2);
        selectedTextView.setText(selectedString);
        EmployAdapter adapter = new EmployAdapter(mContext );
        adapter.setOnItemClickCallBack(new EmployAdapter.ItemClickCallBack() {
            @Override
            public void onItemClicked(String employ) {
                selectedString = employ;
                selectedTextView.setText(selectedString);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void setOnClickListener() {
        cancelView.setOnClickListener(v -> dismiss());
        okView.setOnClickListener(v -> {
            onSelectingEmploy.onSelectingEmploy(selectedString);
            dismiss();
        });
    }

    public interface OnEmploySelectListener {
        void onSelectingEmploy(String value);
    }

}
