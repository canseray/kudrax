package tr.limonist.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.webkit.WebView;

import tr.limonist.extras.MyTextView;
import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;
import tr.limonist.kudra.app.user.LoginMain;

public class MyInfoDialog extends Dialog {

	MyTextView button;
	WebView webView1;
	Activity m_activity;

	public MyInfoDialog(Activity context, String content) {
		super(context, android.R.style.Theme_Black_NoTitleBar);
		m_activity=context;
		getWindow().setBackgroundDrawableResource(R.color.a_black12);
		setContentView(R.layout.a_my_info_dialog);

		final String[] infos = content.split("\\[-\\]");
		if (infos.length > 4) {
			if (infos[4].contentEquals("NOCLS")) {
				this.setCancelable(false);
				this.setCanceledOnTouchOutside(false);
			} else {
				this.setCancelable(true);
				this.setCanceledOnTouchOutside(true);
			}
		} else {
			this.setCancelable(true);
			this.setCanceledOnTouchOutside(false);
		}

		this.button = (MyTextView) findViewById(R.id.button);
		this.button.setText(infos.length > 1 ? infos[1] : (context.getString(R.string.s_close)));
		this.button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(infos.length>1)
				{
					if(infos.length>2)
					{
						if (infos[2].contentEquals("CLS")) {
							dismiss();
						} else if (infos[2].contentEquals("PRESENTFORQRCODE")) {
							if (APP.main_user != null) {
								new MyQrCodeDialog(m_activity);
								dismiss();
							} else
								m_activity.startActivity(new Intent(m_activity, LoginMain.class));

						} else if (infos[2].contentEquals("SEGUEFORCEPTENALVIEW")) {
							if (APP.main_user == null)
								m_activity.startActivity(new Intent(m_activity, LoginMain.class));
							else
							{
								dismiss();
							}

						}
					}
					else
					{
						dismiss();
					}
				}
				else
				{
					dismiss();
				}
			}
		});

		this.webView1 = (WebView) findViewById(R.id.webView1);
		this.webView1.setBackgroundColor(Color.TRANSPARENT);
		this.webView1.loadDataWithBaseURL("", infos[0], "text/html", "UTF-8", "");

	}

	public void setButtonClick(View.OnClickListener clickListener) {
		this.button.setOnClickListener(clickListener);
	}

	@Override
	public void show() {
		super.show();
	}

	@Override
	public void dismiss() {
		super.dismiss();
	}

	public void setCustomDismiss(OnDismissListener dismiss) {
		this.setOnDismissListener(dismiss);
	}

}
