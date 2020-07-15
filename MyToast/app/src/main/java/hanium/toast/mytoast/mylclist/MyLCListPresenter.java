package hanium.toast.mytoast.mylclist;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.HashMap;

import hanium.toast.mytoast.data.MyLCListData;

public class MyLCListPresenter implements MyLCListContract.Presenter {
    private static final String TAG = "MyLCListPresenter";

    private MyLCListContract.View view;
    private Context context;
    private ArrayList<MyLCListData> items = new ArrayList<>();
    private HashMap<String, String> hashMapId = new HashMap<String, String>();
    private HashMap<String, String> hashMapDuration = new HashMap<String, String>();

    @Override
    public void setView(MyLCListContract.View view) {
        this.view = view;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void loadData() {
        getDirectory();
        view.setItems(items);
    }

    public void clearData() {
        items.clear();
        view.setItems(items);
    }

    public void refreshData(ArrayList<MyLCListData> items) {
        view.setItems(items);
    }

    // 음원ID 가져오기
    public String getHashID(String key) {
        return hashMapId.get(key);
    }

    // 음원 시간 가져오기
    public String getHashDuration(String key) { return hashMapDuration.get(key); }

    /**
     * 음원파일이 들어있는 디렉토리 가져오기
     */
    private void getDirectory() {

        ArrayList<String> tmp = new ArrayList<>();
        ArrayList<String> tmp1 = new ArrayList<>();

        String[] projection = {MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.IS_MUSIC,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DURATION
        };

        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, MediaStore.Audio.Media.IS_MUSIC, null, null);

        while (cursor.moveToNext()) {

            String absolute_path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String path = absolute_path.substring(0, absolute_path.lastIndexOf('/'));
            String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            hashMapId.put(title, id);
            hashMapDuration.put(id,duration);
            tmp.add(path);
        }
        cursor.close();

        // 폴더명 중복제거
        for (int i = 0; i < tmp.size(); i++) {
            if (!tmp1.contains(tmp.get(i))) {
                tmp1.add(tmp.get(i));
            }
        }
        items.add(new MyLCListData("내장 메모리"));
        // 폴더명 삽입
        for (int i = 0; i < tmp1.size(); i++) {
            tmp1.set(i, tmp1.get(i).substring(Environment.getExternalStorageDirectory().toString().length() + 1));
            items.add(new MyLCListData(tmp1.get(i)));
        }

    }
}
