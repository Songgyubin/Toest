package hanium.toast.mytoast.uploading;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;

import butterknife.BindView;
import hanium.toast.mytoast.R;
import hanium.toast.mytoast.base.BaseActivity;
import hanium.toast.mytoast.player.PlayerActivity;

public class Uploading extends BaseActivity implements UploadingContract.View {
    private static final String TAG = "Uploading";

    // var
    private String music_id;
    private String music_name;
    private String music_duration;
    // component
    @BindView(R.id.imageview)
    ImageView imageView;

    // others
    private UploadingPresenter presenter = new UploadingPresenter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploading);

        presenter.setContext(this);
        presenter.setView(this);




        try {
            LottieAnimationView lottieAnimationView = findViewById(R.id.animation_view);
            lottieAnimationView.setRepeatCount(10);
            lottieAnimationView.playAnimation();
        }catch (Exception e){
            imageView.setVisibility(View.VISIBLE);
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // MyLCList.class -> Uploading.class
                Intent intent = getIntent();
                music_id = intent.getStringExtra("music_id");
                music_name = intent.getStringExtra("music_name");
                music_duration = intent.getStringExtra("music_duration");

                Intent intent1 = new Intent(getApplicationContext(), PlayerActivity.class);
                intent1.putExtra("music_id",music_id);
                intent1.putExtra("music_name",music_name);

                presenter.setDB(music_id,music_name, music_duration);
                presenter.getDB();
                startActivity(intent1);
                Log.e(TAG, "run: "+"플레이어 클래스로 이동" );
                finish();
                //서버에서 정보 받아 온 후  플레이어로
            }
        },3000);
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}

