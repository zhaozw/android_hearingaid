package com.hari.hearingaid;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import ca.uol.aig.fftpack.RealDoubleFFT;

public class Classify extends Activity implements OnClickListener {

	int frequency = 16000;
	@SuppressWarnings("deprecation")
	int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	private RealDoubleFFT transformer;
	int blockSize = 512;
	public AudioRecord audioRecord;
	Button startStopButton;
	boolean started = false;
	RecordAudio recordTask;
	ImageView imageView;
	Bitmap bitmap;
	Canvas canvas;
	Paint paint;

	// AudioRecord audioRecord;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_classify);

		startStopButton = (Button) this.findViewById(R.id.StartStopButton);
		startStopButton.setOnClickListener(this);

		transformer = new RealDoubleFFT(blockSize);

		imageView = (ImageView) this.findViewById(R.id.ImageView01);
		bitmap = Bitmap.createBitmap((int) 512, (int) 200,
				Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bitmap);
		paint = new Paint();
		paint.setColor(Color.GREEN);
		imageView.setImageBitmap(bitmap);

	}

	public void onPause() {
		super.onPause();
		audioRecord.release();
	}

	public class RecordAudio extends AsyncTask<Void, double[], Void> {

		@Override
		protected Void doInBackground(Void... arg0) {

			try {
				// int bufferSize = AudioRecord.getMinBufferSize(frequency,
				// AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
				int bufferSize = AudioRecord.getMinBufferSize(frequency,
						channelConfiguration, audioEncoding);

				audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
						frequency, channelConfiguration, audioEncoding,
						bufferSize);

				short[] buffer = new short[blockSize];
				double[] toTransform = new double[blockSize];

				audioRecord.startRecording();

				// started = true; hopes this should true before calling
				// following while loop

				while (started) {
					int bufferReadResult = audioRecord.read(buffer, 0,
							blockSize);

					for (int i = 0; i < blockSize && i < bufferReadResult; i++) {
						toTransform[i] = (double) buffer[i] / 32768.0;

					}
					transformer.ft(toTransform);
					publishProgress(toTransform);

				}

				audioRecord.stop();

			} catch (Throwable t) {
				t.printStackTrace();
				Log.e("AudioRecord", "Recording Failed");
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(double[]... toTransform) {

			canvas.drawColor(Color.BLACK);

			for (int i = 0; i < toTransform[0].length; i++) {
				int x = i;
				int downy = (int) (200 - (toTransform[0][i] * 10));
				int upy = 200;

				canvas.drawLine(x, downy, x, upy, paint);
			}

			imageView.invalidate();

			// TODO Auto-generated method stub
			// super.onProgressUpdate(values);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if (started) {
			started = false;
			startStopButton.setText("Start");
			recordTask.cancel(true);
		} else {
			started = true;
			startStopButton.setText("Stop");
			recordTask = new RecordAudio();
			recordTask.execute();
		}
	}
}