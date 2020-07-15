package hanium.toast.mytoast.main;


import android.content.Context;

import java.util.List;

import hanium.toast.mytoast.base.BaseContract;
import hanium.toast.mytoast.data.MyLCCompleteGroup;


public interface MainContract {

    interface View extends BaseContract.View {


        void showToast(String message);

        // 아이템을 어댑터에 연결해 줍니다.
        void setItems(List<MyLCCompleteGroup> items);

        // 단일 아이템에 변경되었음을 알려줍니다.
//        void updateView(User user);

    }

    interface Presenter extends BaseContract.Presenter<View> {

        @Override
        void setView(View view);

        void setContext(Context context);
//        @Override
//        void releaseView();

        // API 통신을 통해 데이터를 받아옵니다.
        void loadData();

        // RxEventBus 를 연결하여 Like 값을 동기화 해줍니다.
        void setRxEvent();
    }
}
