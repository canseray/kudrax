package tr.limonist.kudra.app.user;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;
import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;

public class PassRecovery extends Activity {
	MyTextView tv_done, tv_baslik;

	EditText et_user;
	LinearLayout top_left;
	boolean checkMail = false, checkValid = false;
	String logEmail;
	private String respPart1, respPart2;
	private TransparentProgressDialog pd;
	private Activity m_activity;
	private ImageView img_left;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_activity = PassRecovery.this;
		pd = new TransparentProgressDialog(m_activity, "", true);
		APP.setWindowsProperties(m_activity,false);
		setContentView(R.layout.z_pass_recovery);

		LinearLayout viewstub_parent_ly = findViewById(R.id.viewstub_parent_ly);
		viewstub_parent_ly.setBackgroundColor(getResources().getColor(R.color.a_brown11));

		ViewStub stub = (ViewStub) findViewById(R.id.lay_stub);
		stub.setLayoutResource(R.layout.b_top_img_txt_emp);
		stub.inflate();

		tv_baslik = (MyTextView) findViewById(R.id.tv_baslik);
		tv_baslik.setText(getString(R.string.s_forgot_password));
		tv_baslik.setTextColor(getResources().getColor(R.color.a_white11));

		img_left = (ImageView) findViewById(R.id.img_left);
		img_left.setImageResource(R.drawable.b_ic_prew_white);

		top_left = (LinearLayout) findViewById(R.id.top_left);
		top_left.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}

		});

		tv_done = (MyTextView) findViewById(R.id.tv_done);
		tv_done.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				checkMail = false;
				checkValid = false;

				hideKeyboard(et_user);
				logEmail = et_user.getText().toString();

				if (APP.isValidEmail(logEmail)) {
					pd.show();
					new Connection().execute("");

				} else {
					APP.show_status(m_activity, 2,
							getResources().getString(R.string.s_please_enter_a_valid_email_address));
				}

			}
		});

		et_user = (EditText) findViewById(R.id.et_user);
		et_user.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if (keyCode == KeyEvent.KEYCODE_ENTER) {
						checkMail = false;
						checkValid = false;

						logEmail = et_user.getText().toString();

						if (APP.isValidEmail(logEmail)) {
							pd.show();
							new Connection().execute("");

						} else {
							APP.show_status(m_activity, 2,
									getResources().getString(R.string.s_please_enter_a_valid_email_address));
						}

						return true;
					}
				}
				return false;
			}
		});

	}

	private void hideKeyboard(EditText et) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
	}

    private class Connection extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(logEmail)));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/account_panel/send_password_recovery_request.php");

            if (xml != null && !xml.contentEquals("fail")) {
                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
                    List<String> dataList = new ArrayList<String>();

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        respPart1 = APP.base64Decode(APP.getElement(parse,"part1"));
                        respPart2 = APP.base64Decode(APP.getElement(parse,"part2"));

                    }

                    if (respPart1.contentEquals("OK")) {
                        return "true";
                    } else if (respPart1.contentEquals("FAIL")) {
                        return "error";
                    } else {
                        return "hata";
                    }

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
                APP.show_status(m_activity, 0, respPart2);
            } else if (result.contentEquals("error")) {
                APP.show_status(m_activity, 2, respPart2);
            } else {
                APP.show_status(m_activity, 1,
                        getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }
}
