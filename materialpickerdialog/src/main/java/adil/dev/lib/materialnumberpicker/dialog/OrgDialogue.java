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
import adil.dev.lib.materialnumberpicker.adapter.GenderAdapter;
import adil.dev.lib.materialnumberpicker.adapter.OrgAdapter;


/**
 * Created by Brian Kwendo on 13/02/2020.
 */

public class OrgDialogue extends Dialog {
    static OrgDialogue instance = null;
    Context mContext;
    OrgDialogue.OnOrgSelectListener onSelectingOrg;
    RecyclerView recyclerView;
    TextView okView, cancelView, selectedTextView;
    String selectedString = "";

    public OrgDialogue(Context context) {
        super(context);
        this.mContext = context;
    }

    public static OrgDialogue getInstance() {
        return instance;
    }

    public void setOnSelectingOrg(OrgDialogue.OnOrgSelectListener onSelectingOrg) {
        this.onSelectingOrg = onSelectingOrg;
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
        selectedString = mContext.getString(R.string.org);
    }

    private void initValuesInViews() {
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setItemViewCacheSize(2);
        selectedTextView.setText(selectedString);
        OrgAdapter adapter = new OrgAdapter(mContext );
        adapter.setOnItemClickCallBack(new OrgAdapter.ItemClickCallBack() {
            @Override
            public void onItemClicked(String org) {
                selectedString = org;
                selectedTextView.setText(selectedString);
                onSelectingOrg.onSelectingOrg(selectedString);
                dismiss();
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
                onSelectingOrg.onSelectingOrg(selectedString);
                dismiss();
            }
        });
    }

    public interface OnOrgSelectListener {
        void onSelectingOrg(String value);
    }

}
