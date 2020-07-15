package hanium.toast.mytoast.base;

import android.content.Context;

public class BaseContract {

    public interface View {
        void showToast(String message);
    }

    public interface Presenter<T> {

        void setView(T view);

        void setContext(Context context);

        //void releaseView();
    }

}
