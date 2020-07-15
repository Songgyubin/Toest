package hanium.toast.mytoast.main;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import hanium.toast.mytoast.data.LCDataBase;
import hanium.toast.mytoast.data.MyLCCompleteChild;
import hanium.toast.mytoast.data.MyLCCompleteGroup;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainPresenter implements MainContract.Presenter {
    private static final String TAG = "MainPresenter";

    private MainContract.View view;
    private Context context;
    private ArrayList<String> id = new ArrayList<>();
    private ArrayList<String> title = new ArrayList<>();
    private ArrayList<String> duration = new ArrayList<>();
    private List<MyLCCompleteGroup> list;
    private Realm realm = Realm.getDefaultInstance();
    /**
     * 해당 뷰 정보 세팅
     *
     * @param view
     */
    @Override
    public void setView(MainContract.View view) {
        this.view = view;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }


//    @Override
//    public void releaseView() {
//
//    }


    /**
     * 데이터 로드
     */
    @Override
    public void loadData() {
      getMusicList();
        list = new ArrayList<>();
      for (int i = 0; i < id.size(); i++) {
            final List<MyLCCompleteChild> childList = new ArrayList<>(i);
            for (int j = 0; j < i; j++) {
                childList.add(new MyLCCompleteChild(title.get(i),id.get(i)));
            }
            list.add(new MyLCCompleteGroup(childList, title.get(i),id.get(i),duration.get(i)));
        }
        view.setItems(list);
    }

    @Override
    public void setRxEvent() {

    }

    /**
     * 음원 정보 가져오기
     */
    public void getMusicList() {

        RealmResults<LCDataBase> realmResults = realm.where(LCDataBase.class).findAll();

        for (int i = 0; i < realmResults.size(); i++) {

            id.add(realmResults.get(i).getMusic_id());
            title.add(realmResults.get(i).getMusic_name());
            duration.add(realmResults.get(i).getMusic_duration());

        }
    }
}
