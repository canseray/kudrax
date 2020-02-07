package tr.limonist.kudra.app.user;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
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

import tr.limonist.kudra.app.main.Main;
import tr.limonist.classes.USER;
import tr.limonist.classes.WelcomeItem;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;
import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;

public class LoginMain extends Activity {
    MyTextView tv_done, tv_baslik, tv_forget;
    EditText et_user, et_pass;
    LinearLayout top_left;
    boolean checkMail = false, checkValid = false, checkPass = false;
    String logEmail, LogPass;
    private String respPart1, respPart2;
    private TransparentProgressDialog pd;
    private Activity m_activity;
    private ImageView img_left;
    public String[] part1;
    ArrayList<WelcomeItem> results_welcome;

    @Override
    protected void onResume() {
        super.onResume();
        if (et_user != null && et_pass != null) {
            if (APP.main_user != null) {
                et_user.setText(APP.main_user.email);
                et_pass.setText(APP.main_user.pass);
            }
        }

    }


    //giriş yap ekranım layputu gırıs yap a cevır

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = LoginMain.this;
        APP.setWindowsProperties(m_activity,false);
        pd = new TransparentProgressDialog(m_activity, "", true);
        setContentView(R.layout.z_login_main);

        LinearLayout viewstub_parent_ly = findViewById(R.id.viewstub_parent_ly);
        viewstub_parent_ly.setBackgroundColor(getResources().getColor(R.color.a_brown11));

        ViewStub stub = (ViewStub) findViewById(R.id.lay_stub);
        stub.setLayoutResource(R.layout.b_top_img_txt_emp);
        stub.inflate();

        tv_baslik = (MyTextView) findViewById(R.id.tv_baslik);
        tv_baslik.setText(getString(R.string.s_log_in));
        tv_baslik.setTextColor(getResources().getColor(R.color.a_white11));

        img_left = (ImageView) findViewById(R.id.img_left);

        img_left.setImageResource(R.drawable.b_ic_close_white);

        top_left = (LinearLayout) findViewById(R.id.top_left);
        top_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }

        });

        tv_done = (MyTextView) findViewById(R.id.tv_done);
        tv_done.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                checkMail = false;
                checkPass = false;
                checkValid = false;

                hideKeyboard(et_user);
                logEmail = et_user.getText().toString();
                LogPass = et_pass.getText().toString();

                if (APP.isValidEmail(logEmail)) {
                    if (LogPass.length() > 0) {
                        pd.show();
                        new Connection().execute("");
                    } else {
                        APP.show_status(m_activity, 2, getResources().getString(R.string.s_please_enter_a_password));
                    }
                } else {
                    APP.show_status(m_activity, 2,
                            getResources().getString(R.string.s_please_enter_a_valid_email_address));
                }

            }
        });

        et_user = (EditText) findViewById(R.id.et_user);

        tv_forget = (MyTextView) findViewById(R.id.tv_forget);
        tv_forget.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(m_activity, PassRecovery.class));
            }

        });

    }

    private void hideKeyboard(EditText et) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    private class Connection extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            APP.device_id = APP.myPrefs.getString("regId", "");

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            String device_model = "";
            String os_version = "";
            try {
                device_model = Build.MANUFACTURER + " " + Build.MODEL;
                os_version = Build.VERSION.RELEASE;
            } catch (Exception e1) {
                e1.printStackTrace();

            }

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(logEmail)));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(LogPass)));
            nameValuePairs.add(new Pair<>("param7", APP.base64Encode(APP.device_id)));
            nameValuePairs.add(new Pair<>("param8", APP.base64Encode("A")));
            nameValuePairs.add(new Pair<>("param9", APP.base64Encode(APP.android_id)));
            nameValuePairs.add(new Pair<>("param10", APP.base64Encode(device_model)));
            nameValuePairs.add(new Pair<>("param12", APP.base64Encode(os_version)));
            nameValuePairs.add(new Pair<>("param13", APP.base64Encode(APP.android_id)));
            nameValuePairs.add(new Pair<>("param14", APP.base64Encode(APP.version != null ? APP.version : "")));
            nameValuePairs.add(new Pair<>("param15", APP.base64Encode(APP.language_id)));

            String xml = APP.post1(nameValuePairs, APP.path + "/account_panel/login_in.php");

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
                        String[] customerInfo = respPart2.split("\\[#\\]");

                        USER user = new USER(
                                (customerInfo.length > 0 ? customerInfo[0] : ""),
                                (customerInfo.length > 1 ? customerInfo[1] : ""),
                                (customerInfo.length > 2 ? customerInfo[2] : ""),
                                (customerInfo.length > 3 ? customerInfo[3] : ""),
                                (customerInfo.length > 4 ? customerInfo[4] : ""),
                                (customerInfo.length > 5 ? customerInfo[5] : ""),
                                LogPass);

                        APP.main_user = user;
                        Gson gson = new Gson();
                        String json = gson.toJson(user);
                        APP.e.putString("USER", json);
                        APP.e.commit();

                        return "true";
                    } else {
                        APP.main_user = null;
                        APP.e.putString("USER", null);
                        APP.e.commit();
                        return "error";
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
                startMainScreen();
            } else if (result.contentEquals("error")) {
                APP.show_status(m_activity, 2, respPart2);
            } else {
                APP.show_status(m_activity, 1, getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                if (result.contentEquals("OK")) {
                    startMainScreen();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }

    private void startMainScreen() {
        startActivity(new Intent(m_activity, Main.class));
        finish();
    }

    private class Connection3 extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            results_welcome = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.android_id)));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode("A")));
            String xml = APP.post1(nameValuePairs, APP.path + "/get_intro_screen_items.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        part1 = APP.base64Decode(APP.getElement(parse,"part1")).split("\\[##\\]");

                    }

                    if (!part1[0].contentEquals("")) {

                        for (int i = 0; i < part1.length; i++) {
                            String[] temp = part1[i].split("\\[#\\]");
                            WelcomeItem wi = new WelcomeItem("", "", temp.length > 0 ? temp[0] : "", "");
                            results_welcome.add(wi);
                        }

                        return "true";
                    } else
                        return "false";

                } catch (Exception e) {
                    e.printStackTrace();
                    return "false";
                }

            } else {
                return "false";
            }

        }

        @Override
        protected void onPostExecute(String result) {

            if (pd != null)
                pd.dismiss();

            if (result.contentEquals("true")) {
                startActivityForResult(new Intent(m_activity, WelcomeActivity.class).putExtra("results",results_welcome), 1);
            } else {
                APP.show_status(m_activity, 1,
                        getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }

        }

    }

}
