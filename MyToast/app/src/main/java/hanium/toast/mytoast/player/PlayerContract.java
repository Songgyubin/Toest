package hanium.toast.mytoast.player;

import android.content.Context;

import java.util.ArrayList;

import hanium.toast.mytoast.base.BaseContract;
import hanium.toast.mytoast.data.PlayerData;

public interface PlayerContract {

    interface View extends BaseContract.View {
        @Override
        void showToast(String message);

        void setItems(ArrayList<PlayerData> items);

    }

    interface Presenter extends BaseContract.Presenter<View> {

        @Override
        void setView(View view);

        @Override
        void setContext(Context context);

        void loadData();

    }

}
