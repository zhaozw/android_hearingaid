package com.hari.hearingaid;

import android.content.Context;
import android.view.View;
import android.widget.Button;

public class PlayButton extends Button {
    boolean mStartPlaying = true;

    OnClickListener clicker = new OnClickListener() {
        public void onClick(View v) {
            MainActivity.onPlay(mStartPlaying);
            if (mStartPlaying) {
                setText("Stop playing");
            } else {
                setText("Start playing");
            }
            mStartPlaying = !mStartPlaying;
        }
    };

    public PlayButton(Context ctx) {
        super(ctx);
        setText("Start playing");
        setOnClickListener(clicker);
    }
}