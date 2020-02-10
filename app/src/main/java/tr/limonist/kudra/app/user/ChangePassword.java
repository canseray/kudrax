package tr.limonist.kudra.app.user;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;

public class ChangePassword extends Activity {
    MyTextView tv_done, tv_baslik;
    EditText et_old, et_new, et_new_again;
    LinearLayout top_left;
    String s_old, s_new, s_new_again;
    private String respPart1, respPart2;
    private TransparentProgressDialog pd;
    private Activity m_activity;
    private ImageView img_left;
    private ImageView img_show_old, img_show_new, img_show_new_again;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = ChangePassword.this;
        APP.setWindowsProperties(m_activity, false);
        pd = new TransparentProgressDialog(m_activity, "", true);
        setContentView(R.layout.z_change_password);

        ViewStub stub = (ViewStub) findViewById(R.id.lay_stub);
        stub.setLayoutResource(R.layout.b_top_img_txt_emp);
        stub.inflate();

        tv_baslik = (MyTextView) findViewById(R.id.tv_baslik);
        tv_baslik.setText(getString(R.string.s_change_password));
        tv_baslik.setTextColor(getResources().getColor(R.color.a_brown11));

        img_left = (ImageView) findViewById(R.id.img_left);
        img_left.setImageResource(R.drawable.left_k);

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

                hideKeyboard(et_old);

                s_old = et_old.getText().toString();
                s_new = et_new.getText().toString();
                s_new_again = et_new_again.getText().toString();

                if (s_old.length() > 0) {
                    if (s_new.length() > 0) {
                        if (s_new_again.length() > 0) {
                            if (s_new.contentEquals(s_new_again)) {
                                if (s_old.contentEquals(APP.main_user.pass)) {
                                    pd.show();
                                    new Connection().execute("");
                                } else
                                    APP.show_status(m_activity, 2, getString(R.string.s_old_password_wrong));
                            } else
                                APP.show_status(m_activity, 2, getString(R.string.s_new_paswords_not_match));
                        } else
                            APP.show_status(m_activity, 2, getString(R.string.s_please_enter_x, getString(R.string.s_new_pass_again)));
                    } else
                        APP.show_status(m_activity, 2, getString(R.string.s_please_enter_x, getString(R.string.s_new_pass)));
                } else
                    APP.show_status(m_activity, 2, getString(R.string.s_please_enter_x, getString(R.string.s_old_pass)));

            }
        });

        et_old = (EditText) findViewById(R.id.et_old);
        et_new = (EditText) findViewById(R.id.et_new);
        et_new_again = (EditText) findViewById(R.id.et_new_again);

        img_show_old = (ImageView) findViewById(R.id.img_show_old);
        img_show_old.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePass(0);
            }
        });
        img_show_new = (ImageView) findViewById(R.id.img_show_new);
        img_show_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePass(1);
            }
        });
        img_show_new_again = (ImageView) findViewById(R.id.img_show_new_again);
        img_show_new_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePass(2);
            }
        });
    }

    boolean pass_old = false, pass_new = false, pass_new_again = false;

    public void changePass(int type) {

        if (type == 0) {
            if (pass_old) {
                et_old.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                et_old.setTransformationMethod(PasswordTransformationMethod.getInstance());
                et_old.setSelection(et_old.getText().length());
                img_show_old.setImageResource(R.drawable.ic_eye_open);
                pass_old = false;
            } else {
                et_old.setInputType(InputType.TYPE_CLASS_TEXT);
                et_old.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                et_old.setSelection(et_old.getText().length());
                img_show_old.setImageResource(R.drawable.ic_eye_close);
                pass_old = true;
            }
        } else if (type == 1) {
            if (pass_new) {
                et_new.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                et_new.setTransformationMethod(PasswordTransformationMethod.getInstance());
                et_new.setSelection(et_new.getText().length());
                img_show_new.setImageResource(R.drawable.ic_eye_open);
                pass_new = false;
            } else {
                et_new.setInputType(InputType.TYPE_CLASS_TEXT);
                et_new.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                et_new.setSelection(et_new.getText().length());
                img_show_new.setImageResource(R.drawable.ic_eye_close);
                pass_new = true;
            }
        } else if (type == 2) {
            if (pass_new_again) {
                et_new_again.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                et_new_again.setTransformationMethod(PasswordTransformationMethod.getInstance());
                et_new_again.setSelection(et_new.getText().length());
                img_show_new_again.setImageResource(R.drawable.ic_eye_open);
                pass_new_again = false;
            } else {
                et_new_again.setInputType(InputType.TYPE_CLASS_TEXT);
                et_new_again.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                et_new_again.setSelection(et_new.getText().length());
                img_show_new_again.setImageResource(R.drawable.ic_eye_close);
                pass_new_again = true;
            }
        }

    }

    private void hideKeyboard(EditText et) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    private class Connection extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user.id)));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(s_old)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode(s_new)));
            nameValuePairs.add(new Pair<>("param4", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param5", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/account_panel/set_password.php");

            if (xml != null && !xml.contentEquals("fail")) {
                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        respPart1 = APP.base64Decode(APP.getElement(parse, "part1"));
                        respPart2 = APP.base64Decode(APP.getElement(parse, "part2"));

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

                APP.main_user.setPass(s_new);
                Gson gson = new Gson();
                String json = gson.toJson(APP.main_user);
                APP.e.putString("USER", json);
                APP.e.commit();

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
