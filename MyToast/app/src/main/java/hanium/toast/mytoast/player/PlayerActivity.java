package hanium.toast.mytoast.player;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import hanium.toast.mytoast.R;
import hanium.toast.mytoast.adapter.PlayerAdapter;
import hanium.toast.mytoast.base.BaseActivity;
import hanium.toast.mytoast.data.MyLCCompleteGroup;
import hanium.toast.mytoast.data.PlayerData;
import hanium.toast.mytoast.main.MainActivity;

public class PlayerActivity extends BaseActivity
        implements PlayerContract.View,
        PlayerAdapter.OnScriptClickListener,
        View.OnClickListener {
    private static final String TAG = "PlayerActivity";

    // component
    @BindView(R.id.play)
    ImageButton play;

    @BindView(R.id.pause)
    ImageButton pause;

    @BindView(R.id.pre_sentence)
    ImageButton pre_sentence;

    @BindView(R.id.next_sentence)
    ImageButton next_sentence;

    @BindView(R.id.repeat)
    ImageButton repeat;

    @BindView(R.id.repeat_one)
    ImageButton repeat_one;

    @BindView(R.id.setspeed)
    LinearLayout setspeed;

    @BindView(R.id.script_on_off)
    ImageButton script_on_off;

    @BindView(R.id.current_time)
    TextView current_time;

    @BindView(R.id.total_time)
    TextView total_time;

    @BindView(R.id.seekbar)
    SeekBar seekBar;

    @BindView(R.id.player_recyclerview)
    RecyclerView player_recyclerView;

    @BindView(R.id.speed_value)
    TextView speed_value;

    // actionbar
    private TextView action_bar_musicname;

    // var
    private boolean repeat_flag = false;
    private boolean isPlaying = true;
    private boolean mPlayOnAudioFocus;
    private boolean script_on=true;
    private String id;
    private String title;
    private String music_id;
    private String music_name;
    private int currentPosition = 0;
    private int position;
    private ArrayList<MyLCCompleteGroup> list;
    private HashMap<String, String> id_hashMap = new HashMap<>();
    private HashMap<String, String> duration_hashMap = new HashMap<>();
    int focusResult;
    private int speed_count=0;

    // others
    Handler handler;
    private MediaPlayer mediaPlayer;
    private PlayerThread playerThread;
    private PlayerAdapter adapter = new PlayerAdapter();
    private PlayerPresenter presenter = new PlayerPresenter();
    private AudioManager am;
    private AudioManager.OnAudioFocusChangeListener focusChangeListener;

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: "+"플레이어 액티비티 실행" );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // from MyLCList.onOkCliked
        Intent intent = getIntent();
        music_name = intent.getStringExtra("music_name");

        // recyclerview
        player_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        player_recyclerView.setAdapter(adapter);
        adapter.setScriptClickListener(this);

        // presenter와 연결
        presenter.setView(this);
        presenter.setContext(this);

        // 데이터 로드
        presenter.loadData();

        // 미디어플레이어
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        // 오디오 포커스

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        AudioAttributes mAudioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        focusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {

                    case (AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK):
                        mediaPlayer.pause();
                        break;

                    case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK):
                        // 재생중인 media mute 처리 또는 최소화
                        mediaPlayer.setVolume(0.2f, 0.2f);
                        //Log.e(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                        break;

                    case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT):

                        if(isPlaying) {
                            mPlayOnAudioFocus=true;
                            mediaPlayer.pause();
                        }
                        //Log.e(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                        break;
                    case (AudioManager.AUDIOFOCUS_LOSS):
                        // 다른 플레이어에서 미디어 실행 시
                        am.abandonAudioFocus(this);
                        mPlayOnAudioFocus=false;
                        mediaPlayer.pause();
                        pause.setVisibility(View.GONE);
                        play.setVisibility(View.VISIBLE);
                        //Log.e(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                        break;

                    case (AudioManager.AUDIOFOCUS_GAIN):
                        if (mPlayOnAudioFocus &&!isPlaying) {
                            mediaPlayer.start();
                        } else if (isPlaying) {
                            mediaPlayer.setVolume(0.2f, 0.2f);
                        }
                        mPlayOnAudioFocus = false;
                        //Log.e(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                        break;
                    case (AudioManager.AUDIOFOCUS_GAIN_TRANSIENT):

                        //Log.e(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                        break;

                    default:
                        break;
                }

            }
        };
        AudioFocusRequest mAudioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(mAudioAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener(focusChangeListener)
                .build();

        focusResult = am.requestAudioFocus(focusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        // 버튼 리스너 지정
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        repeat.setOnClickListener(this);
        repeat_one.setOnClickListener(this);
        pre_sentence.setOnClickListener(this);
        next_sentence.setOnClickListener(this);
        script_on_off.setOnClickListener(this);
        setspeed.setOnClickListener(this);


        // 재생시킬 미디어 ID 가져오기
        getMusicId();
        // 음원 재생
        playMusic(id_hashMap.get(music_name));
        String totaltime = String.format("%02d%02d",
                TimeUnit.MILLISECONDS.toMinutes(Integer.parseInt(duration_hashMap.get(music_name))),
                TimeUnit.MILLISECONDS.toSeconds(Integer.parseInt(duration_hashMap.get(music_name)))
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Integer.parseInt(duration_hashMap.get(music_name))))
        ); // -> ex) totaltime: 0527
        total_time.setText(totaltime.substring(0, 2) + ":" + totaltime.substring(2));


        /**
         * SeekBar 처리
         * 구간반복 핸들러
         */
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    currentPosition = msg.arg1;
                    seekBar.setProgress(currentPosition);
                    String currenttime = String.format("%02d%02d",
                            TimeUnit.MILLISECONDS.toMinutes(currentPosition),
                            TimeUnit.MILLISECONDS.toSeconds(currentPosition)
                                    - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentPosition))
                    );
                    current_time.setText(currenttime.substring(0, 2) + ":" + currenttime.substring(2));

//                    //Log.e(TAG, "currentPosition: " + currentPosition);
                    if (repeat_flag == true && mediaPlayer.getCurrentPosition() > 15000) {

                        mediaPlayer.pause();
                        mediaPlayer.seekTo(9999);
                        mediaPlayer.start();
                    }
                }
            }
        };

        //쓰레드 시작
        playerThread = new PlayerThread(handler);
        playerThread.setDaemon(true);
        playerThread.start();

        /**
         * Seekbar 리스너
         */
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                seekBar.setProgress(mediaPlayer.getCurrentPosition());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
                if (seekBar.getProgress() > 0 && play.getVisibility() == View.GONE) {
                    mediaPlayer.start();
                }
            }
        });

        /**
         * 다음곡 자동재생
         */
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                /*if(position+1<list.size()) {
                    position++;
                    playMusic(list.get(position));
                }*/
            }
        });
    }

    /**
     * 플레이어 각 버튼 온클릭
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                if(focusResult == AudioManager.AUDIOFOCUS_GAIN) {
                    pause.setVisibility(View.VISIBLE);
                    play.setVisibility(View.GONE);
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
                    mediaPlayer.start();
                }
                break;

            case R.id.pause:
                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
                mediaPlayer.pause();
                break;

            case R.id.repeat:
                mediaPlayer.seekTo(9999);
                repeat_flag = true;

                break;
            case R.id.setspeed:
                setSpeed();
                break;
            /*case R.id.next:
                if(position+1<list.size()){
                    position++;
                    playMusic(list.get(position));
                    seekBar.setProgress(0);
                }

                break;*/
            case R.id.script_on_off:

                if(script_on ==true) {
                    script_on = false;
                    adapter.ScriptMode(script_on);
                    script_on_off.setImageResource(R.drawable.scriptoff_36);
                    break;
                }
                else if(script_on == false)
            {
                script_on = true;
                adapter.ScriptMode(script_on);
                script_on_off.setImageResource(R.drawable.scripton_36);
                break;
            }


        }
    }

    @Override
    public void onScriptClick(PlayerData playerData) {

    }

    @Override
    public void setItems(ArrayList<PlayerData> items) {
        adapter.setItems(items);
    }
    /**
     * 재생 속도 조절
     */
    private void setSpeed(){

        if (speed_count == 0){
            mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(0.8f));
            speed_value.setText("0.8");
            speed_count++;
        }
        else if(speed_count == 1){
            mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(1.0f));
            speed_value.setText("1.0");
            speed_count++;
        }
        else if(speed_count == 2){
            mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(1.5f));
            speed_value.setText("1.5");
            speed_count++;
        }
        else if(speed_count == 3){
            mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(2.0f));
            speed_value.setText("2.0");
            speed_count=0;
        }




    }


    /**
     * 음원재생
     *
     * @param id    : 재생할 미디어 ID
     * @param title : 음원 타이틀
     */
    public void playMusic(String id) {

        try {
            seekBar.setProgress(0);

            /*Uri musicURI = Uri.withAppendedPath(
                    Uri.parse("content://com.android.providers.downloads.documents/document"), "" + id);*/
            Uri musicURI = Uri.withAppendedPath(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + id);

//            Log.d(TAG, "playMusic: " + MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + " " + id);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(this, musicURI);
            mediaPlayer.prepare();
            mediaPlayer.start();


            seekBar.setMax(mediaPlayer.getDuration());
            if (mediaPlayer.isPlaying()) {
                play.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
            } else {
                play.setVisibility(View.VISIBLE);
                pause.setVisibility(View.GONE);
            }
//            Bitmap bitmap = BitmapFactory.decodeFile(getCoverArtPath(Long.parseLong(musicDto.getAlbumId()),getApplication()));
//            album.setImageBitmap(bitmap);

        } catch (Exception e) {
//            //Log.e("SimplePlayer", e.getMessage());
        }
    }

    /**
     * 재생할 음원ID 가져오기
     */
    private void getMusicId() {
        String[] projection = {MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.IS_MUSIC,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DURATION
        };

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, MediaStore.Audio.Media.IS_MUSIC, null, null);

        while (cursor.moveToNext()) {

            String absolute_path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String path = absolute_path.substring(0, absolute_path.lastIndexOf('/'));
            String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

//            //Log.e(TAG, "getMusicId: " + title);
            id_hashMap.put(title, id);
            duration_hashMap.put(title, duration);
        }
        cursor.close();
    }

    /**
     * 액션바 커스텀
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
        View actionbar = inflater.inflate(R.layout.custom_actionbar_player, null);
        action_bar_musicname = (TextView) actionbar.findViewById(R.id.action_bar_musicname);
        action_bar_musicname.setText(music_name);
        actionBar.setCustomView(actionbar);

        //액션바 양쪽 공백 없애기
        Toolbar parent = (Toolbar) actionbar.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        return true;
    }


    /**
     * actionbar onClick
     * @param view
     */
    public void actionbar_player_onClick(View view){

        switch (view.getId()){
            case R.id.action_bar_back_player:
                Intent intent = new Intent(PlayerActivity.this, MainActivity.class);
                startActivity(intent);
                break;


        }
    }

    /**
     * SeekBar 처리
     * 구간반복 쓰레드
     */
    class PlayerThread extends Thread {

        Handler handler;

        PlayerThread(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            while (isPlaying) {
                try {

                    Thread.sleep(500);
                    if (mediaPlayer != null) {
                        Message msg = Message.obtain();
                        msg.what = 0;
                        msg.arg1 = mediaPlayer.getCurrentPosition();
                        handler.sendMessage(msg);

                    }
                } catch (Exception e) {
//                    //Log.e("ProgressUpdate", e.getMessage());
                }

            }
        }
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
    protected void onDestroy() {
        super.onDestroy();
        isPlaying = false;
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}

