package adil.dev.lib.materialnumberpicker.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import adil.dev.lib.materialnumberpicker.R;
import adil.dev.lib.materialnumberpicker.model.employModel;
import okio.BufferedSource;
import okio.Okio;


public class EmployAdapter extends RecyclerView.Adapter<EmployAdapter.DialogViewHolder> {

    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<employModel> dataList = new ArrayList<>();
    private ItemClickCallBack itemClickCallBack;
    private int focusedItem = 0;

    public EmployAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        initList();
    }

    public void setOnItemClickCallBack(ItemClickCallBack onItemClickCallBack) {
        this.itemClickCallBack = onItemClickCallBack;
    }

    @Override
    public DialogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DialogViewHolder(inflater.inflate(R.layout.picker_item, parent, false));
    }

    @Override
    public void onBindViewHolder(DialogViewHolder holder, int position) {
        holder.number.setText(dataList.get(position).getEmploy());
        if (dataList.get(position).isHasFocus()) {
            holder.number.setBackgroundResource(R.color.colorPrimary);
            holder.number.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
        } else {
            holder.number.setBackgroundResource(R.drawable.ic_round_shape_unselected);
            holder.number.setTextColor(ContextCompat.getColor(mContext, R.color.MPD_pickerItemTextColorUnSelected));
        }
    }

    private void initList() {

/*
        dataList.add(new Gender(mContext.getString(R.string.MPD_male), true));
        dataList.add(new Gender(mContext.getString(R.string.MPD_female), false));
        dataList.add(new Gender(mContext.getString(R.string.MPD_others), false));
*/

       fetchCollectibles();

    }

    private void fetchCollectibles() {

        InputStream is = mContext.getResources().openRawResource(R.raw.employ);
        BufferedSource okioBufferSrce = Okio.buffer(Okio.source(is));
        try {

            Type listType = new TypeToken<List<employModel>>() {
            }.getType();

            List<employModel> list = new Gson().fromJson(okioBufferSrce.readUtf8(), listType);

            for (int i = 0; i < list.size(); i++) {

                dataList.add(new employModel(list.get(i).getEmploy(), list.get(i).isHasFocus()));

              }

        } catch (IOException e) {
            Log.e("ProjectRepo", "IOException occurs", e);
        }

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public interface ItemClickCallBack {
        void onItemClicked(String Employ);
    }

    class DialogViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView number;
        FrameLayout itemParent;


        public DialogViewHolder(View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.text_number);
            itemParent = itemView.findViewById(R.id.item_parent);
            itemParent.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.item_parent) {
                if (focusedItem <= -1) {
                    focusedItem = getLayoutPosition();
                    dataList.get(getLayoutPosition()).setHasFocus(true);
                    notifyItemChanged(getLayoutPosition());
                } else {
                    dataList.get(focusedItem).setHasFocus(false);
                    notifyItemChanged(focusedItem);
                    focusedItem = getLayoutPosition();
                    dataList.get(getLayoutPosition()).setHasFocus(true);
                    notifyItemChanged(getLayoutPosition());
                }
                itemClickCallBack.onItemClicked(dataList.get(getLayoutPosition()).getEmploy());
            }
        }
    }

}