package hanium.toast.mytoast.mylclist;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

import hanium.toast.mytoast.base.BaseContract;
import hanium.toast.mytoast.data.MyLCListData;

public interface MyLCListContract {

    interface View extends BaseContract.View {


        void showToast(String message);

        // 아이템을 어댑터에 연결해 줍니다.
        void setItems(ArrayList<MyLCListData> items);

        // 해쉬맵 연결
        void setHash(HashMap<String, String> hash);

        // 단일 아이템에 변경되었음을 알려줍니다.
//        void updateView(User user);

    }

    interface Presenter extends BaseContract.Presenter<MyLCListContract.View> {

        @Override
        void setView(MyLCListContract.View view);

        void setContext(Context context);
//        @Override
//        void releaseView();

        // API 통신을 통해 데이터를 받아옵니다.
        void loadData();


    }
}
