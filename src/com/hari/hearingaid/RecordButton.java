package com.hari.hearingaid;

import android.content.Context;
import android.view.View;
import android.widget.Button;

class RecordButton extends Button {
    boolean mStartRecording = true;

    OnClickListener clicker = new OnClickListener() {
        public void onClick(View v) {
            MainActivity.onRecord(mStartRecording);
            if (mStartRecording) {
                setText("Stop recording");
            } else {
                setText("Start recording");
            }
            mStartRecording = !mStartRecording;
        }
    };

    public RecordButton(Context ctx) {
        super(ctx);
        setText("Start recording");
        setOnClickListener(clicker);
    }
}