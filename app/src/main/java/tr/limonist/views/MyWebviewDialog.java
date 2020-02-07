package tr.limonist.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewStub;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import tr.limonist.kudra.R;

public class MyWebviewDialog extends Dialog {

	TextView tv_title,tv_url;
	LinearLayout top_right;
	ImageView img_right;
	WebView mWebView;
	Context mContext;

	public MyWebviewDialog(Context context, String url) {
		super(context, android.R.style.Theme_Black_NoTitleBar);
		mContext=context;
		setContentView(R.layout.a_my_webview_dialog);

		ViewStub stub = (ViewStub) findViewById(R.id.lay_stub);
		stub.setLayoutResource(R.layout.b_top_title_desc_img);
		stub.inflate();

		tv_url = (TextView) findViewById(R.id.tv_url);
		tv_title = (TextView) findViewById(R.id.tv_title);

		img_right = (ImageView) findViewById(R.id.img_right);
		img_right.setImageResource(R.drawable.b_ic_close_blue);

		top_right = (LinearLayout) findViewById(R.id.top_right);
		top_right.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		mWebView = (WebView) findViewById(R.id.mWebView);

		mWebView.setWebViewClient(new WebViewClient(){

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon){
				tv_title.setText(mContext.getString(R.string.s_loading));
				tv_url.setText(view.getUrl());
			}
			@Override
			public void onPageFinished(WebView view, String url){

				tv_title.setText(view.getTitle());
				tv_url.setText(view.getUrl());

			}

		});

		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.loadUrl(url);

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
