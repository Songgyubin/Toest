package hanium.toast.mytoast.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import hanium.toast.mytoast.R;
import hanium.toast.mytoast.data.MyLCListData;

public class MyLCListAdapter extends RecyclerView.Adapter<MyLCListAdapter.MyLCListViewHolder> {
    private static final String TAG = "MyLCListAdapter";

    private ArrayList<MyLCListData> items = new ArrayList<>();
    private OnItemClickListener listener;
    private MyLCListViewHolder holder;

    @NonNull
    @Override
    public MyLCListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_mylc, parent, false);

        return new MyLCListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyLCListViewHolder holder, int position) {
        this.holder = holder;
        MyLCListData myLCListData = items.get(holder.getAdapterPosition());

        holder.mylclist_title.setText(myLCListData.getPath());
        holder.itemView.setOnClickListener(view -> listener.onClick(myLCListData));
        if (myLCListData.getPath().contains(".mp3")) {
            holder.mylclist_icon.setImageResource(R.drawable.mp3_40);
        } else if (position == 0) {
            holder.mylclist_icon.setImageResource(R.drawable.folderopen_40);
        } else {
            holder.mylclist_icon.setImageResource(R.drawable.folder_40);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

/*
    public int getPosition(MyLCListData myLCListData) {
        for(int i = 0; i < items.size(); i++) {
            if(items.get(i)!=null) {
                return i;
            }
        }
        return RecyclerView.NO_POSITION;
    }
*/

    public void setItems(ArrayList<MyLCListData> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onClick(MyLCListData myLCListData);
    }

    class MyLCListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.mylclist_icon)
        ImageView mylclist_icon;

        @BindView(R.id.mylclist_title)
        TextView mylclist_title;

        public MyLCListViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

