package hanium.toast.mytoast.mylclist;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import hanium.toast.mytoast.BackPressCloseHandler;
import hanium.toast.mytoast.R;
import hanium.toast.mytoast.adapter.MyLCListAdapter;
import hanium.toast.mytoast.base.BaseActivity;
import hanium.toast.mytoast.customdialog.CustomDialog;
import hanium.toast.mytoast.data.MyLCListData;
import hanium.toast.mytoast.main.MainActivity;
import hanium.toast.mytoast.uploading.Uploading;

import static hanium.toast.mytoast.permission.CheckPermission.MULTIPLE_PERMISSIONS;
import static hanium.toast.mytoast.permission.CheckPermission.activity;
import static hanium.toast.mytoast.permission.CheckPermission.checkPermissions;
import static hanium.toast.mytoast.permission.CheckPermission.context;
import static hanium.toast.mytoast.permission.CheckPermission.mpermissions;

public class MyLCList extends BaseActivity implements
        MyLCListContract.View,
        MyLCListAdapter.OnItemClickListener {
    private static final String TAG = "MyLCList";

    // component
    @BindView(R.id.mylclist_recylcerview)
    RecyclerView recyclerView;

    // actionBar
    private ImageButton closeBtn;
    private ImageButton backBtn;

    // var
    private File[] fileList;
    private HashMap<String, Integer> position = new HashMap<String, Integer>();
    private String newfilename;
    private String current_state = null;

    // others
    private MyLCListAdapter adapter = new MyLCListAdapter();
    private MyLCListPresenter presenter = new MyLCListPresenter();
    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mylclist);
        backPressCloseHandler = new BackPressCloseHandler(this);

        /**
         * 퍼미션 체크
         */
        activity = this;
        context = this;
        if (Build.VERSION.SDK_INT >= 23) { // 안드로이드 6.0 이상일 경우 퍼미션 체크
            checkPermissions(activity, context);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setClickListener(this);

        // presenter와 연결
        presenter.setView(this);
        presenter.setContext(this);

        // 데이터 로드
        presenter.loadData();

    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    @Override
    public void showToast(String message) {
        super.showToast(message);
    }

    @Override
    public void setItems(ArrayList<MyLCListData> myLCListData) {
        adapter.setItems(myLCListData);
    }

    /**
     * 음원 재생을 위한 ID값
     * HashMap<title,__id>
     *
     * @param hash
     */
    @Override
    public void setHash(HashMap<String, String> hash) {

    }


    /**
     * 퍼미션 리퀘스트
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(mpermissions[i])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showToast("권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.");
                            }
                        }
                    }
                } else {
                    showToast("권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.");
                }
                return;
            }
        }
    }

    /**
     * 액션바
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();

        // Custom Actionbar를 사용하기 위해 CustomEnabled을 true 시키고 필요 없는 것은 false 시킨다
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(true);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.
        actionBar.setElevation(0);

        //layout을 가지고 와서 actionbar에 포팅을 시킵니다.
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.custom_actionbar_mylclist, null);
        actionBar.setCustomView(actionbar);

        closeBtn = (ImageButton) actionbar.findViewById(R.id.action_bar_close);
//        closeBtn.setVisibility(View.GONE);
        backBtn = (ImageButton) actionbar.findViewById(R.id.action_bar_back);
        backBtn.setVisibility(View.GONE);

        //액션바 양쪽 공백 없애기
        Toolbar parent = (Toolbar) actionbar.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        return true;
    }

    /**
     * 액션바 온클릭
     *
     * @param v
     */
    public void actionbar_mylc_onClick(View v) {
        switch (v.getId()) {
            case R.id.action_bar_close:
                Intent intent = new Intent(MyLCList.this, MainActivity.class);
                startActivity(intent);
                break;

            case R.id.action_bar_back:
                presenter.clearData();
                presenter.loadData();
                closeBtn.setVisibility(View.VISIBLE);
                backBtn.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 파일리스트 온클릭
     *
     * @param myLCListData
     */
    @Override
    public void onClick(MyLCListData myLCListData) {
        ArrayList<MyLCListData> items = new ArrayList<>();
        ArrayList<String> abpath = new ArrayList<>();

        //            showToast(myLCListData.getPath());

        // 음원파일일때
        if (isMp3File(myLCListData)) {
            showToast("음원파일입니다");

            String filename = myLCListData.getPath().substring(0, myLCListData.getPath().lastIndexOf(".mp3"));

            //Log.e(TAG, "음원일때 : " + fileList[(int) position.get(filename)]);

            // 음원아이디 가져오기
            String music_id = presenter.getHashID(filename);

            // 음원 시간 가져오기
            String music_duration ;
            String totaltime = String.format("%02d%02d",
                    TimeUnit.MILLISECONDS.toMinutes(Integer.parseInt(presenter.getHashDuration(music_id))),
                    TimeUnit.MILLISECONDS.toSeconds(Integer.parseInt(presenter.getHashDuration(music_id)))
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Integer.parseInt(presenter.getHashDuration(music_id))))
            ); // -> ex) totaltime: 0527
            music_duration=totaltime.substring(0, 2) + ":" + totaltime.substring(2);

            // 다이얼로그 호출
            CustomDialog customDialog = new CustomDialog(MyLCList.this, filename);
            customDialog.setDialogListener(new CustomDialog.MyDialogListener() {
                @Override
                public void onOkCliked(String new_file_name) {
                    setFileName(new_file_name);
                    File f = fileList[(int) position.get(filename)];
                    File newf = new File(f.toString().substring(0, (f.toString().lastIndexOf("/") + 1)) + newfilename + ".mp3");

                    if (f.exists()) {
                        //Log.e(TAG, "onOkCliked: " + f.toString());
                        f.renameTo(newf);
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + newf.toString())));
                    } else {
                        //Log.e(TAG, "onOkCliked: " + "오류");
                    }
                    adapter.notifyDataSetChanged();
                    customDialog.dismiss();
                    Intent intent = new Intent(MyLCList.this, Uploading.class);
                    intent.putExtra("music_id", music_id);
                    intent.putExtra("music_name", new_file_name);
                    intent.putExtra("music_duration", music_duration);
                    Log.e(TAG, "onOkCliked: "+"Uploding클래스로 이동" );
                    startActivity(intent);
                    // 서버에 업로드
                }

                @Override
                public void onCancelClicked() {
                    Log.d(TAG, "onCancelClicked: ");
                    customDialog.dismiss();
                }
            });

            customDialog.show();
//            //Log.e(TAG, "새파일이름: "+newFilename );

        } else if (myLCListData.getPath().equals("내장 메모리")) {
            Toast.makeText(this, "최상위 폴더입니다.", Toast.LENGTH_SHORT).show();

        } else if (myLCListData.getPath().contains("내장 메모리") && myLCListData.getPath().length() > 6) {
            presenter.clearData();
            presenter.loadData();
            closeBtn.setVisibility(View.VISIBLE);
            backBtn.setVisibility(View.GONE);
        }

        // 폴더일때
        else {
            closeBtn.setVisibility(View.GONE);
            backBtn.setVisibility(View.VISIBLE);

            File f = new File(Environment.getExternalStorageDirectory().toString() + "/" + myLCListData.getPath());

            //Log.e(TAG, "onClick: " + f.toString());
            // mp3파일만 리스트에 띄우기
            fileList = f.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith("mp3");
                }
            });

            // 현재 디렉토리 알림
            items.add(new MyLCListData("내장 메모리/" + myLCListData.getPath()));

            for (int i = 0; i < fileList.length; i++) {

                String tmp = String.valueOf(fileList[i]);
                abpath.add(fileList[i].getPath());
                String filename = tmp.substring((tmp.lastIndexOf("/") + 1));
                items.add(new MyLCListData(filename));
                //Log.e(TAG, "파일리스트: " + fileList[i]);
                position.put(filename.substring(0, filename.lastIndexOf(".mp3")), i);
                //Log.e(TAG, "파일명: " + filename.substring(0, filename.lastIndexOf(".mp3")));

            }
            presenter.refreshData(items);
            adapter.notifyDataSetChanged();
        }
    }

    // 음원파일인지 폴더인지 체크
    private boolean isMp3File(MyLCListData myLCListData) {
        if (myLCListData.getPath().contains(".mp3")) {
            return true;
        } else
            return false;
    }

    // 다이얼로그에서 데이터 가져오기
    private void setFileName(String filename) {
        newfilename = filename;
        Log.d(TAG, "setFileName: " + newfilename);
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    /**
     * 사용자가 권한 거부했을 시
     * 액티비티 생성될때마다 체크
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= 23) { // 안드로이드 6.0 이상일 경우 퍼미션 체크
            checkPermissions(activity, context);
        }
    }

}
