package hanium.toast.mytoast.player;

import android.content.Context;

import java.util.ArrayList;

import hanium.toast.mytoast.data.PlayerData;

public class PlayerPresenter implements PlayerContract.Presenter {
    private static final String TAG = "PlayerPresenter";

    private PlayerContract.View view;
    private Context context;
    private ArrayList<PlayerData> scripts = new ArrayList<>();


    @Override
    public void setView(PlayerContract.View view) {
        this.view = view;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void loadData() {
        scripts.add(new PlayerData("what are you doing?"));
        view.setItems(scripts);
    }
}
