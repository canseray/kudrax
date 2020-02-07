package tr.limonist.kudra.app.cart;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import java.util.Set;

import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;
import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;

public class PaymentWebPage extends Activity {

	Intent returnIntent;
	private WebView webView;
	private TransparentProgressDialog pd = null;
	private LinearLayout top_left;
	private Activity m_activity;
	private String params;
	private MyTextView tv_baslik;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_activity = PaymentWebPage.this;
		pd = new TransparentProgressDialog(m_activity,"", true);

		params = getIntent().getExtras().getString("params");

		setContentView(R.layout.z_payment_contract);

		tv_baslik = (MyTextView) findViewById(R.id.tv_baslik);
		tv_baslik.setText(getString(R.string.s_payment));

		top_left = (LinearLayout) findViewById(R.id.top_left);
		top_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent returnIntent = new Intent();
				returnIntent.putExtra("result", getString(R.string.s_error_occured_when_pay));
				setResult(RESULT_CANCELED, returnIntent);
				finish();
			}
		});

		webView = (WebView) findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.setWebViewClient(new myWebClient());
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.setWebChromeClient(new WebChromeClient());
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		webView.getSettings().setDomStorageEnabled(true);

		webView.getSettings().setTextSize(WebSettings.TextSize.LARGEST);
		webView.getSettings().setBuiltInZoomControls(true);

		String url = APP.base64Decode(params);
		url = APP.base64Decode(url);
		url = APP.base64Decode(url);
		url = APP.base64Decode(url);
		url = APP.base64Decode(url);

		webView.loadData(url, "text/html", "UTF-8");

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (webView.canGoBack() == true) {
					webView.goBack();
				} else {
					Intent returnIntent = new Intent();
					returnIntent.putExtra("result", getString(R.string.s_error_occured_when_pay));
					setResult(RESULT_CANCELED, returnIntent);
					finish();
				}
				return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}

	public class myWebClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub

			super.onPageStarted(view, url, favicon);
			String[] page = url.split("/");
			if (page[page.length - 1].startsWith("payment_done.php")) {

				String prom_info ="";

				Uri url_new = Uri.parse(url);
				Set<String> paramNames = url_new.getQueryParameterNames();
				for (String key: paramNames) {

					if(key.contentEquals("prom"))
						prom_info = url_new.getQueryParameter(key);
				}

				Intent returnIntent = new Intent();
				setResult(RESULT_OK, returnIntent);
				returnIntent.putExtra("prom",APP.base64Decode(prom_info));
				finish();

			} else if (page[page.length - 1].startsWith("payment_error.php")) {
				Intent returnIntent = new Intent();
				setResult(RESULT_CANCELED, returnIntent);
				finish();
			} 

		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub

			view.loadUrl(url);
			return true;

		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);
			pd.dismiss();
		}

	}
}
