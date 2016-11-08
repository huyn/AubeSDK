package com.aube.sdkdemo;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.aube.rvi.AubeAgent;
import com.aube.rvi.OnAubePreparedListener;
import com.aube.rvi.OnVideoPlayerListener;
import com.aube.sdkdemo.model.VideoItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Formatter;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by huyaonan on 16/10/25.
 */
public class VideoPlayActivity extends Activity {

    public static final String VIDEO_URI = "VIDEO_URI";
    public static final String VIDEO_ID = "VIDEO_ID";

    private VideoItem videoItem;

    private VideoView mVideoView;

    private MediaController mediaPlayerControl;

    private OnVideoPlayerListener mLisenter;

    private SeekBar mSeekBar;
    private TextView mEndTime, mCurrentTime;

    private AtomicBoolean mIsOnTouchMode = new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video);

        mVideoView = (VideoView) findViewById(R.id.video);

        String videoUrl = getIntent().getStringExtra(VIDEO_URI);
        videoItem = (VideoItem) getIntent().getSerializableExtra(VIDEO_ID);

        mediaPlayerControl = new MediaController(this);
        mVideoView.setMediaController(mediaPlayerControl);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mVideoView.start();

//                try {
//                    mLisenter = AubeAgent.load(VideoPlayActivity.this, (ViewGroup) findViewById(R.id.video_interaction), format(videoItem));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                startTimer();
            }
        });

        mVideoView.setVideoURI(Uri.parse(videoUrl));

        mEndTime = (TextView) findViewById(R.id.time);
        mCurrentTime = (TextView) findViewById(R.id.time_current);

        mSeekBar = (SeekBar) findViewById(R.id.mediacontroller_progress);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsOnTouchMode.set(true);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mIsOnTouchMode.set(false);
                long duration = mVideoView.getDuration();
                long newposition = (duration * seekBar.getProgress()) / 1000L;
                mLisenter.onSeekComplete(newposition);
                mVideoView.seekTo((int) newposition);
            }
        });


        AubeAgent.load(this, (ViewGroup) findViewById(R.id.video_interaction), videoItem.videoid, new OnAubePreparedListener() {
            @Override
            public void onPrepared(OnVideoPlayerListener onVideoPlayerListener) {
                mLisenter = onVideoPlayerListener;
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    StringBuilder mFormatBuilder = new StringBuilder();
    Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private String format(VideoItem item) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt("videoid", item.videoid);
            jsonObject.putOpt("showid", item.showid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String s = jsonObject.toString();
        return s;
    }

    @Override
    public void onBackPressed() {
        if(timer != null) {
            timer.cancel();
            timer = null;
        }

        if(mLisenter != null) {
            mLisenter.onDestroy();
        }
//        try {
//            String result = AubeDatabaseHelper.getInstance(this).export();
//            System.out.println("------------" + result);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        super.onBackPressed();
    }

    private Timer timer;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(mLisenter != null) {
//                System.out.println("-----------" + mVideoView.getCurrentPosition());
                long pos = mVideoView.getCurrentPosition();
                mLisenter.onProgressChanged(pos);

                updateProgress();

//                AubeDatabaseHelper.getInstance(VideoPlayActivity.this).report("play", "time:" + (pos/1000));
                AubeAgent.report(VideoPlayActivity.this, "play", "time:" + (pos/1000));
            }
        }
    };

    private void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0);
            }
        }, 100, 100);
    }

    private void updateProgress() {
        int position = mVideoView.getCurrentPosition();
        int duration = mVideoView.getDuration();
        if (duration > 0) {
            // use long to avoid overflow
            long pos = 1000L * position / duration;
            if(!mIsOnTouchMode.get())
                mSeekBar.setProgress( (int) pos);
        }
        int percent = mVideoView.getBufferPercentage();
        mSeekBar.setSecondaryProgress(percent * 10);

        if (mEndTime != null)
            mEndTime.setText(stringForTime(duration));
        if (mCurrentTime != null)
            mCurrentTime.setText(stringForTime(position));
    }

}
