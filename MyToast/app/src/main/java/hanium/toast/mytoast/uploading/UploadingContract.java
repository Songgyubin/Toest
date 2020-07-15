package hanium.toast.mytoast.uploading;

import android.content.Context;

import hanium.toast.mytoast.base.BaseContract;

public interface UploadingContract {
    interface View extends BaseContract.View {
        @Override
        void showToast(String message);

        //void setItems(ArrayList<PlayerData> items);



    }

    interface Presenter extends BaseContract.Presenter<UploadingContract.View> {

        @Override
        void setView(UploadingContract.View view);

        @Override
        void setContext(Context context);

        void setDB(String music_id, String music_name, String music_duration);

        String getDB();
    }
}
