package hanium.toast.mytoast.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class CheckPermission extends AppCompatActivity {

    /**
     * manifests 권한 싱크
     */
    public static String[] mpermissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };

    public static Context context;
    public static Activity activity;

    // 퍼미션 리퀘스트 코드
    public static final int MULTIPLE_PERMISSIONS = 101;

    /**
     * 퍼미션 체크
     *
     * @param activity
     * @param context
     * @return
     */
    public static boolean checkPermissions(Activity activity, Context context) {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : mpermissions) {
            result = ContextCompat.checkSelfPermission(context, pm);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(activity, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


}
