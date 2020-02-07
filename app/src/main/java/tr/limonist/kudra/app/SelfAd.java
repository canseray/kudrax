package tr.limonist.kudra.app;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;

public class SelfAd extends Activity {

	public String[] onemliGun;

	@Override
	public void onBackPressed() {
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_self_ad);
		Intent intent = getIntent();
		onemliGun = intent.getExtras().get("onemli").toString().split("\\[#\\]");

		final LinearLayout layLink = (LinearLayout) findViewById(R.id.layLink);
		final SimpleDraweeView img_banner = (SimpleDraweeView) findViewById(R.id.img_banner);
		img_banner.setImageURI(onemliGun[1]);

		TextView tvDetay = (TextView) findViewById(R.id.tvDetay);
		tvDetay.setText(onemliGun[5]);

		if (onemliGun[0].contentEquals("3")) {
			link = onemliGun[4];
			linkType = 3;
		} else if (onemliGun[0].contentEquals("2")) {
			link = onemliGun[2];
			linkType = 2;
		} else {
			layLink.setVisibility(View.GONE);
		}

		final ImageView btnClose = (ImageView) findViewById(R.id.btnClose);
		btnClose.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent returnIntent = new Intent();
				setResult(Activity.RESULT_OK, returnIntent);
				finish();

			}

		});

		layLink.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (linkType == 2) {
					Uri uri = Uri.parse(link);
					Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);

					try {
						startActivity(myAppLinkToMarket);
						finish();

					} catch (ActivityNotFoundException e) {

						Toast.makeText(SelfAd.this, getString(R.string.s_play_store_must_be_insatalled), Toast.LENGTH_LONG)
								.show();
					}
				} else if (linkType == 3) {
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
					startActivity(browserIntent);
					finish();
				} else
					;
			}

		});
		APP.e.putString("self_ad_date", onemliGun[7]);
		APP.e.putString("self_ad_type", onemliGun[0]);
		APP.e.commit();

		int time = Integer.parseInt(onemliGun[8]) * 1000;

		Handler handler3 = new Handler();
		handler3.postDelayed(new Runnable() {
			public void run() {
				finish();

			}
		}, time);
	}

	String link;
	int linkType = 0;

}
