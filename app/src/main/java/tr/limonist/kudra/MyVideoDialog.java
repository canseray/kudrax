package tr.limonist.kudra;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

public class MyVideoDialog extends Activity {

	ImageView img_close;
	VideoView video;
	Activity m_activity;
	MediaController mc;
	String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_activity = MyVideoDialog.this;
		APP.setWindowsProperties(m_activity, false);
		url = getIntent().getStringExtra("url");
		setContentView(R.layout.a_my_video_dialog);

		img_close = (ImageView) findViewById(R.id.img_close);
		img_close.setImageResource(R.drawable.b_ic_close_white);
		img_close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		video = (VideoView) findViewById(R.id.video);
		/*RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(video.getLayoutParams());
		p.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		p.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		p.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		p.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		video.setLayoutParams(p);*/

		MediaController mediaController = new MediaController(this);
		mediaController.setAnchorView(video);
		video.setMediaController(mediaController);

		Uri uri=Uri.parse(url);
		video.setVideoPath(url);
		video.start();



		/*mc = new MediaController(m_activity);

		mc.setAnchorView(video);
		video.setVideoURI(uri);

		video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
                video.start();
			}
		});
        video.requestFocus();*/
	}

}
