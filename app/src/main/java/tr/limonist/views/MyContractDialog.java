package tr.limonist.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import tr.limonist.kudra.R;
import tr.limonist.extras.MyTextView;

public class MyContractDialog extends Dialog {

	MyTextView tv_title;
	WebView webView1;
	Context m_activity;
	ImageView img_right;

	public MyContractDialog(Context context, String content, String title) {
		super(context, android.R.style.Theme_Black_NoTitleBar);
		m_activity=context;
		getWindow().setBackgroundDrawableResource(R.color.a_black12);
		setContentView(R.layout.a_my_contract_dialog);

		tv_title = (MyTextView) findViewById(R.id.tv_title);
		tv_title.setTextColor(m_activity.getResources().getColor(R.color.a_brown11));
		tv_title.setText(title);

		img_right = (ImageView) findViewById(R.id.img_right);
		img_right.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		webView1 = (WebView) findViewById(R.id.webView1);
		webView1.setBackgroundColor(Color.TRANSPARENT);
		webView1.loadDataWithBaseURL("", content, "text/html", "UTF-8", "");

		show();

	}

	@Override
	public void show() {
		super.show();
	}

	@Override
	public void dismiss() {
		super.dismiss();
	}

}
