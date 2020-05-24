package adil.dev.lib.materialnumberpicker.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import adil.dev.lib.materialnumberpicker.R;
import adil.dev.lib.materialnumberpicker.adapter.LevelAdapter;


/**
 * Created by Brian Kwendo on 13/02/2020.
 */
public class LevelDialog extends Dialog {
    static LevelDialog instance = null;
    Context mContext;
    LevelDialog.OnLevelSelectListener onSelectingLevel;
    RecyclerView recyclerView;
    TextView okView, cancelView, selectedTextView;
    String selectedString = "";
    LevelAdapter adapter;

    EditText etSearch;

    public LevelDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public static LevelDialog getInstance() {
        return instance;
    }

    public void setOnSelectingLevel(LevelDialog.OnLevelSelectListener onSelectingLevel) {
        this.onSelectingLevel = onSelectingLevel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.level_picker_dialog);
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
        selectedString = mContext.getString(R.string.level);
    }

    private void initValuesInViews() {
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setItemViewCacheSize(2);
        selectedTextView.setText(selectedString);
        adapter = new LevelAdapter(mContext);
        adapter.setOnItemClickCallBack(new LevelAdapter.ItemClickCallBack() {
            @Override
            public void onItemClicked(String Level) {
                selectedString = Level;
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
                onSelectingLevel.onSelectingLevel(selectedString);
                dismiss();
            }
        });
    }

    public interface OnLevelSelectListener {
        void onSelectingLevel(String value);
    }

}
