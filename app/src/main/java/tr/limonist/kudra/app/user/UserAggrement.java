package tr.limonist.kudra.app.user;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.github.lguipeng.library.animcheckbox.AnimCheckBox;
import com.github.mmin18.widget.RealtimeBlurView;
import com.google.gson.Gson;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import tr.limonist.classes.USER;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;
import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;
import tr.limonist.kudra.app.main.Main;
import tr.limonist.views.MyContractDialog;
import tr.limonist.views.MyImageDialog;

public class UserAggrement extends AppCompatActivity {
    Activity m_activity;
    private TransparentProgressDialog pd;
    AnimCheckBox acb;
    private boolean is_check;
    String part1;
    WebView webView;
    private String respPart1,respPart2,respPart3;
    String s_mail, s_pass, s_name, s_surname, s_phone, s_skin_type;
    MyTextView tv_done;
    RealtimeBlurView realtimeBlurView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = UserAggrement.this;
        APP.setWindowsProperties(m_activity,false);
        pd = new TransparentProgressDialog(m_activity, "", true);
        setContentView(R.layout.z_user_aggrement);

        webView = findViewById(R.id.webview);
        tv_done = findViewById(R.id.tv_done);
        realtimeBlurView = findViewById(R.id.login_blur);


        s_name = getIntent().getStringExtra("s_name");
        s_surname = getIntent().getStringExtra("s_surname");
        s_mail = getIntent().getStringExtra("s_mail");
        s_phone = getIntent().getStringExtra("s_phone");
        s_pass = getIntent().getStringExtra("s_pass");
        s_skin_type = "1";
        s_skin_type = getIntent().getStringExtra("s_skin_type");

        pd.show();
        new Connection2().execute();


        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_check) {
                    pd.show();
                    new Connection().execute("");
                } else {
                    APP.show_status(m_activity, 2,
                            getResources().getString(R.string.s_please_read_and_accept_user_aggrement));
                }
            }
        });

        is_check = false;
        acb = (AnimCheckBox) findViewById(R.id.acb);
        acb.setOnCheckedChangeListener(new AnimCheckBox.OnCheckedChangeListener() {
            @Override
            public void onChange(AnimCheckBox view, boolean checked) {
                is_check = checked;
            }
        });

    }

    private class Connection2 extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode("0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode("1")));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param4", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/get_contract_data.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        part1 = APP.base64Decode(APP.getElement(parse,"part1"));

                    }
                    if (!part1.contentEquals("")) {
                        return "true";
                    } else {
                        return "false";
                    }

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
                webView.loadDataWithBaseURL("", part1, "text/html", "UTF-8", "");

            } else {
                APP.show_status(m_activity, 1,getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }

    class Connection extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            String device_model = "";
            String os_version = "";
            try {
                device_model = Build.MANUFACTURER + " " + Build.MODEL;
                os_version = Build.VERSION.RELEASE;
            } catch (Exception e1) {
                e1.printStackTrace();

            }

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(s_name)));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(s_surname)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode(s_mail)));
            nameValuePairs.add(new Pair<>("param4", APP.base64Encode(s_phone)));
            nameValuePairs.add(new Pair<>("param5", APP.base64Encode(s_pass)));
            nameValuePairs.add(new Pair<>("param6", APP.base64Encode(APP.android_id)));
            nameValuePairs.add(new Pair<>("param7", APP.base64Encode(device_model)));
            nameValuePairs.add(new Pair<>("param9", APP.base64Encode(os_version)));
            nameValuePairs.add(new Pair<>("param10", APP.base64Encode(APP.android_id)));
            nameValuePairs.add(new Pair<>("param11", APP.base64Encode(APP.version != null ? APP.version : "")));
            nameValuePairs.add(new Pair<>("param17", APP.base64Encode("A")));
            nameValuePairs.add(new Pair<>("param18", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param19", APP.base64Encode(s_skin_type)));


            String xml = APP.post1(nameValuePairs, APP.path + "/account_panel/send_new_account_request.php");

            if (xml != null && !xml.contentEquals("fail")) {
                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        respPart1 = APP.base64Decode(APP.getElement(parse,"part1"));
                        respPart2 = APP.base64Decode(APP.getElement(parse,"part2"));
                        respPart3 = APP.base64Decode(APP.getElement(parse,"part3"));

                    }

                    if (respPart1.contentEquals("OK")) {

                        USER user = new USER(respPart3, s_name, s_surname, s_mail,"", s_phone,s_pass);
                        APP.main_user = user;
                        Gson gson = new Gson();
                        String json = gson.toJson(user);
                        APP.e.putString("USER", json);
                        APP.e.putString("welcome", "no");
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

                realtimeBlurView.setVisibility(View.VISIBLE);
                final MyImageDialog dia = new MyImageDialog(m_activity,"TEBRİKLER !",
                        "Üyelik işlemleriniz başarılı bir şekilde tamamlanmıştır.","Tamam",true);

                dia.setOkClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dia.dismiss();
                        startActivity(new Intent(m_activity, Main.class));
                        finish();
                    }
                });

                dia.show();

              /*  Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "OK");
                setResult(Activity.RESULT_OK, returnIntent);
                finish(); */

            } else if (result.contentEquals("error")) {
                APP.show_status(m_activity, 2, respPart2);
            } else {
                APP.show_status(m_activity, 1, getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }


}
