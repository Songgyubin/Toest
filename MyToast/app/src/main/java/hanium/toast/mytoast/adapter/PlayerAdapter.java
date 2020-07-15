package hanium.toast.mytoast.adapter;

import android.util.Log;
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
import hanium.toast.mytoast.data.PlayerData;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {
    private static final String TAG = "PlayerAdapter";

    private ArrayList<PlayerData> items = new ArrayList<>();
    private OnScriptClickListener listener;
    private PlayerViewHolder holder;

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_player, parent, false);

        return new PlayerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        this.holder = holder;
        PlayerData playerData = items.get(holder.getAdapterPosition());

        holder.script_tv.setText("You will hear a question or statement and three responses spoken in english.");

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(ArrayList<PlayerData> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setScriptClickListener(OnScriptClickListener listener) {
        this.listener = listener;
    }

    public interface OnScriptClickListener {
        void onScriptClick(PlayerData playerData);
    }

    public void ScriptMode(boolean on) {
        //Log.e(TAG, "ScriptMode: "+on );
        if (on == true) {
            holder.script_tv.setVisibility(View.VISIBLE);
        }else{
            holder.script_tv.setVisibility(View.GONE);
        }
    }

    class PlayerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.direction_iv)
        ImageView direction_iv;

        @BindView(R.id.direction_view2)
        View direction_view2;

        @BindView(R.id.script_tv)
        TextView script_tv;

        PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
