package hanium.toast.mytoast.uploading;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import hanium.toast.mytoast.data.LCDataBase;
import io.realm.Realm;
import io.realm.RealmResults;

public class UploadingPresenter implements UploadingContract.Presenter{
    private static final String TAG = "UploadingPresenter";

    private UploadingContract.View view;
    private Context context;
    private Realm realm = Realm.getDefaultInstance();
    @Override
    public void setView(UploadingContract.View view) {
        this.view = view;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void setDB(String music_id, String music_name,String music_duration) {


            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    LCDataBase lcDataBase = realm.createObject(LCDataBase.class,music_id);
                    lcDataBase.setMusic_name(music_name);
                    lcDataBase.setMusic_duration(music_duration);
                    //Log.e(TAG, "execute: " +music_id);

                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    //Log.e(TAG, "onSuccess: " );
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    //Log.e(TAG, "onError: " +error);
                    //Log.e(TAG, "에러: "+ error.getMessage() );
                    if (error.getMessage().contains("Primary key value already exists")){
                        Toast.makeText(context, "이미 목록에 추가된 음원입니다", Toast.LENGTH_SHORT).show();
                    }
                }
    });



    }

    @Override
    public String getDB() {
        RealmResults<LCDataBase> realmResults = realm.where(LCDataBase.class).findAll();
        for(int i=0;i<realmResults.size();i++){
            //Log.e(TAG, "getDB: "+realmResults.get(i).getMusic_id());
        }
        return realmResults.get(0).getMusic_id();
    }

}
