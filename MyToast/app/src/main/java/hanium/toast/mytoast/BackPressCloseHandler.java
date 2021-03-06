package hanium.toast.mytoast;

import android.app.Activity;
import android.widget.Toast;

public class BackPressCloseHandler {

    private long backKeyPressedTime = 0;
    private Toast toast;
    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();

            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.finish();

            // 첫번째 액티비티 아닐때 앱 종료
            /*activity.finishAffinity();
            System.runFinalization();
            System.exit(0);*/

            toast.cancel();
        }
    }

    public void showGuide() {
        toast = Toast.makeText(activity, "뒤로 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG);
        toast.show();
    }
}
