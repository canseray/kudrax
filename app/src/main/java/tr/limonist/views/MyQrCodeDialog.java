package tr.limonist.views;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.util.Pair;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import at.grabner.circleprogress.CircleProgressView;
import tr.limonist.extras.MyTextView;
import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;
import tr.limonist.extras.TransparentProgressDialog;

public class MyQrCodeDialog extends Dialog {
	MyTextView tv_done, tv_baslik, tv_forget;
	private ImageView img_left;
	private final TransparentProgressDialog pd;
	private final ImageView img;
	private final CircleProgressView circle;
	private final TextView second,tv_exp;
	Activity m_activity;
	LinearLayout lay_dismiss;
	int REFRESH_TIME_IN_SECOND = 30;
	public String part1;
	public String part2;
	public String part3;
	public String[] part4;
	public String part5;

	public MyQrCodeDialog(Activity activity) {
		super(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		setCancelable(true);
		setCanceledOnTouchOutside(true);
		m_activity = activity;
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setContentView(R.layout.a_my_qr_code_dialog);

		pd = new TransparentProgressDialog(m_activity, "", true);

		LinearLayout viewstub_parent_ly = findViewById(R.id.viewstub_parent_ly);

		ViewStub stub = findViewById(R.id.lay_stub);
		stub.setLayoutResource(R.layout.b_top_img_txt_emp);
		stub.inflate();

		tv_baslik = (MyTextView) findViewById(R.id.tv_baslik);
		tv_baslik.setText(m_activity.getString(R.string.s_my_qr_code));
		tv_baslik.setTextColor(m_activity.getResources().getColor(R.color.a_brown11));

		img_left = (ImageView) findViewById(R.id.img_left);
		img_left.setImageResource(R.drawable.left_k);
		img_left.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				m_activity.finish();
			}

		});

		img = (ImageView) findViewById(R.id.img);
		circle = (CircleProgressView) findViewById(R.id.circle);
		second = (TextView) findViewById(R.id.second);

		tv_exp = (TextView) findViewById(R.id.tv_exp);

		lay_dismiss = (LinearLayout) findViewById(R.id.lay_dismiss);
		lay_dismiss.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				dismiss();

			}
		});

		cdt = new CountDownTimer(1000 * REFRESH_TIME_IN_SECOND, 1000) {

			public void onTick(long millisUntilFinished) {

				circle.setValue(circle.getCurrentValue() - 1);

				if(circle.getCurrentValue()<=0)
				{
					cdt.cancel();
					circle.setValue(0);
					new Connection().execute("");
				}
				else
					second.setText(String.valueOf((int) (circle.getCurrentValue() - 1)));

			}

			public void onFinish() {
				cdt.cancel();
				circle.setValue(0);
				new Connection().execute("");

			}

		};

		show();
		pd.show();
		new Connection().execute("");

	}

	@Override
	public void show() {
		super.show();
	}

	@Override
	public void dismiss() {
		cdt.cancel();
		super.dismiss();
	}

	class Connection extends AsyncTask<String, Void, String> {

		protected String doInBackground(String... args) {

			List<Pair<String, String>> nameValuePairs = new ArrayList<>();

			nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user.id)));
			nameValuePairs.add(new Pair<>("param2", APP.base64Encode(APP.android_id)));
			nameValuePairs.add(new Pair<>("param3", APP.base64Encode("A")));
			nameValuePairs.add(new Pair<>("param6", APP.base64Encode(APP.android_id)));
			nameValuePairs.add(new Pair<>("param7", APP.base64Encode(APP.android_id)));
			nameValuePairs.add(new Pair<>("param8", APP.base64Encode(APP.language_id)));

			String xml = APP.post1(nameValuePairs, APP.path + "/account_panel/set_user_qrcode.php");

			if (xml != null && !xml.contentEquals("fail")) {

				try {

					DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
					Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
					List<String> dataList = new ArrayList<String>();

					for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {
						part1 = APP.base64Decode(APP.getElement(parse,"part1"));
						part2 = APP.base64Decode(APP.getElement(parse,"part2"));
						part3 = APP.getElement(parse,"part3");
						part4 = APP.base64Decode(APP.getElement(parse,"part4")).split("\\[#\\]");
						part5 = APP.base64Decode(APP.getElement(parse,"part5"));
					}

					if (part1.contentEquals("OK"))
						return "true";
					else
						return "error";

				} catch (Exception e) {
					e.printStackTrace();
					return "false";
				}

			} else {
				return "false";
			}

		}

		protected void onPostExecute(String result) {
			if (pd != null)
				pd.dismiss();
			if (result.contentEquals("true")) {

				fillComponents();

			} else if (result.contentEquals("error")) {

				cdt.cancel();
				circle.setValue(0);
				second.setText("0");
				img.setImageBitmap(null);

				APP.show_status(m_activity, 1, part2);

			} else {

				cdt.cancel();
				circle.setValue(0);
				second.setText("0");
				img.setImageBitmap(null);

				APP.show_status(m_activity, 1,
						m_activity.getResources().getString(R.string.s_unexpected_connection_error_has_occured));
			}
		}
	}

	public void fillComponents() {

		if (!part5.contentEquals("")) {
			try {
				REFRESH_TIME_IN_SECOND = Integer.parseInt(part5);
			} catch (Exception e) {

			}
		}

		tv_exp.setText(part4.length > 0 ? part4[0] : m_activity.getString(R.string.s_my_qr_code_exp));

		second.setText(String.valueOf(REFRESH_TIME_IN_SECOND));

		circle.setMaxValue(REFRESH_TIME_IN_SECOND);
		circle.setValue(REFRESH_TIME_IN_SECOND);

		MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
		try {
			BitMatrix bitMatrix = multiFormatWriter.encode(part3, BarcodeFormat.QR_CODE,200,200);
			BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
			Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
			img.setImageBitmap(bitmap);

			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					cdt.start();
				}
			}, 1000);
		} catch (WriterException e) {
			e.printStackTrace();
		}

	}


	CountDownTimer cdt;

}
