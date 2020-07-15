package hanium.toast.mytoast.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import butterknife.BindView;
import hanium.toast.mytoast.BackPressCloseHandler;
import hanium.toast.mytoast.R;
import hanium.toast.mytoast.ViewProducer;
import hanium.toast.mytoast.adapter.BaseExpandableRecyclerViewAdapter;
import hanium.toast.mytoast.adapter.MyLCCompleteAdapter;
import hanium.toast.mytoast.base.BaseActivity;
import hanium.toast.mytoast.data.MyLCCompleteChild;
import hanium.toast.mytoast.data.MyLCCompleteGroup;
import hanium.toast.mytoast.listeners.ExpandableRecyclerViewOnClickListener;
import hanium.toast.mytoast.mylclist.MyLCList;
import hanium.toast.mytoast.player.PlayerActivity;

import static hanium.toast.mytoast.permission.CheckPermission.MULTIPLE_PERMISSIONS;
import static hanium.toast.mytoast.permission.CheckPermission.activity;
import static hanium.toast.mytoast.permission.CheckPermission.checkPermissions;
import static hanium.toast.mytoast.permission.CheckPermission.context;
import static hanium.toast.mytoast.permission.CheckPermission.mpermissions;

public class MainActivity extends BaseActivity implements MainContract.View, ExpandableRecyclerViewOnClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private static final int FILE_SELECT_CODE = 0;
    static private String SHARE_NAME = "ISPUSH";
    static SharedPreferences sharedPreferences = null;
    static SharedPreferences.Editor editor = null;

    // component
    @BindView(R.id.main_recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.floating)
    FloatingActionButton floating;

    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    @Nullable
    @BindView(R.id.drawer)
    ViewGroup drawerView;

    @BindView(R.id.nav_push_switch)
    Switch nav_push_switch;

   /* @BindView(R.id.navigationView)
    NavigationView navigationView;
*/
    // var

    // others
    private MyLCCompleteAdapter adapter;
    private MainPresenter presenter = new MainPresenter();
    private BackPressCloseHandler backPressCloseHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(SHARE_NAME,MODE_PRIVATE);
        editor = sharedPreferences.edit();



        activity = this;
        context = this;
        backPressCloseHandler = new BackPressCloseHandler(this);
        if (Build.VERSION.SDK_INT >= 23) { // 안드로이드 6.0 이상일 경우 퍼미션 체크
            checkPermissions(activity, context);
        }


        // 프레젠터에서의 데이터를 뷰에 표현하기 위해
        // 프레젠터 적용
        presenter.setView(this);
        presenter.setContext(this);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyLCCompleteAdapter();
        adapter.setListener(this);
        presenter.loadData();
//        setCustomActionbar();

        /**
         * 플로팅 버튼 리스너
         * if adapter.getGroupConunt == 0
         * empty layout
         * else
         * MainActivity
         */
        floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.getGroupCount() == 0) {
                    Log.d(TAG, "onClick: 빈화면 ");
                    Intent intent = new Intent(MainActivity.this, MyLCList.class);
                    startActivity(intent);
                } else {
                    Log.d(TAG, "onClick: 리스트화면 ");
                    Intent intent = new Intent(MainActivity.this, MyLCList.class);
                    startActivity(intent);
                }
            }
        });

        /**
         * 푸쉬 알림 설정
         */
        nav_push_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    nav_push_switch.getTrackDrawable().setColorFilter(Color.argb(255,0,0,255), PorterDuff.Mode.MULTIPLY);
                    editor.putBoolean("PushOnOff",true);
                    editor.commit();
                    Log.e(TAG, "onCheckedChanged: "+sharedPreferences.getBoolean("PushOnOff",false) );

                }else {
                    nav_push_switch.getTrackDrawable().setColorFilter(Color.argb(255,0,0,0), PorterDuff.Mode.MULTIPLY);
                    editor.putBoolean("PushOnOff",false);
                    editor.commit();
                    Log.e(TAG, "onCheckedChanged: "+sharedPreferences.getBoolean("PushOnOff",false) );
                }
            }
        });


        /**
         * 데이터가 비어있을때의 화면
         */
        adapter.setEmptyViewProducer(new ViewProducer() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
                return new DefaultEmptyViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.empty, parent, false)
                );
            }

            /**
             * Empty Activity BindViewHolder
             * & Empty Activity Component Listener
             * @param holder
             */
            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder) {
//                Button btn = (Button) holder.itemView.findViewById(R.id.logobtn);
//                btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Log.d(TAG, "onClick: 로고고ㅗ");
//                    }
//                });
            }
        });

        /**
         * RecyclerView 애니메이션
         */
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof DefaultItemAnimator) {
            ((DefaultItemAnimator) animator).setSupportsChangeAnimations(false);

        }

        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onGroupLongClicked(BaseExpandableRecyclerViewAdapter.BaseGroupBean groupItem) {
        return false;
    }

    @Override
    public boolean onInterceptGroupExpandEvent(BaseExpandableRecyclerViewAdapter.BaseGroupBean groupItem, boolean isExpand) {
        return false;
    }

    @Override
    public void onGroupClicked(BaseExpandableRecyclerViewAdapter.BaseGroupBean groupItem) {
        Toast.makeText(this, "그룹", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChildClicked(BaseExpandableRecyclerViewAdapter.BaseGroupBean groupItem, MyLCCompleteChild childItem) {
        Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
        intent.putExtra("id", childItem.getMid());
        intent.putExtra("title", childItem.getName());
        startActivity(intent);
        Log.d(TAG, "onChildClicked: " + adapter.getListGroupBean());
    }

    /**
     * Presenter에서 받은 데이터셋을
     * adapter에 전달
     *
     * @param items
     */
    @Override
    public void setItems(List<MyLCCompleteGroup> items) {
        adapter.setItems(items);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            if(drawerLayout.isDrawerOpen(drawerView)){

                drawerLayout.closeDrawers();}
            else {
                drawerLayout.openDrawer(drawerView);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 드로어 레이아웃 온클릭
     * @param view
     */
    public void nav_onClick(View view){
        switch (view.getId()){
            case R.id.nav_push_on_off:
                Toast.makeText(this, "푸쉬 알림", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_information_use:
                Toast.makeText(this, "이용 안내", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_my_info:
                Toast.makeText(this, "제작자 정보/고객센터", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_review:
                Toast.makeText(this, "리뷰 남기러 가기", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_app_infomaiton:
                Toast.makeText(this, "앱 정보", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    /**
     * 액션바 커스텀
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();

        // Custom Actionbar를 사용하기 위해 CustomEnabled을 true 시키고 필요 없는 것은 false 시킨다
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayHomeAsUpEnabled(true);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 보임처리합니다.
        actionBar.setHomeAsUpIndicator(R.drawable.menu_24);
        actionBar.setHomeButtonEnabled(true);

        actionBar.setElevation(0);

        //layout을 가지고 와서 actionbar에 포팅을 시킵니다.
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.custom_actionbar_main, null);
        actionBar.setCustomView(actionbar);

        //액션바 양쪽 공백 없애기
        Toolbar parent = (Toolbar) actionbar.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        return true;
    }

    /**
     * 네비게이션 바 아이템 온클릭
     * @param menuItem
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
//            case R.id.navPushOnOff:
//                Toast.makeText(this, "음원 분석 알림", Toast.LENGTH_SHORT).show();
//                break;

        }

        return false;
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
        if(sharedPreferences!=null&&sharedPreferences.getBoolean("PushOnOff",false)==true){
            Log.e(TAG, "sharedPreferences.getBoolean: "+"체크 트루" );
            nav_push_switch.setChecked(true);
        }else if(sharedPreferences!=null&&sharedPreferences.getBoolean("PushOnOff",false)==false){
            Log.e(TAG, "sharedPreferences.getBoolean: "+"체크 폴스" );
            nav_push_switch.setChecked(false);
        }
        Log.e(TAG, "onStart: " );
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
     * actionbar onClick
     *
     * @param view
     */
    public void action_bar_onClick(View view) {

        switch (view.getId()) {
            /*case R.id.action_bar_nav:

                break;
*/

        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(drawerView)){
            drawerLayout.closeDrawers();}
        else {
            backPressCloseHandler.onBackPressed();
        }
    }


}
