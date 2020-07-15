package hanium.toast.mytoast;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder().build();

        // Realm에 셋팅한 정보 값 지정
        Realm.setDefaultConfiguration(configuration);

    }
}
