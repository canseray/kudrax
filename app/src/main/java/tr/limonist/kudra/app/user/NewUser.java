package tr.limonist.kudra.app.user;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.lguipeng.library.animcheckbox.AnimCheckBox;
import com.google.gson.Gson;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;
import tr.limonist.classes.USER;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;
import tr.limonist.views.MyContractDialog;

public class NewUser extends Activity {

    MyTextView tv_done;
    EditText et_mail, et_pass, et_name, et_surname, et_phone;
    TextView et_skin_type;
    String s_mail, s_pass, s_name, s_surname, s_phone, s_skin_type;
    private TransparentProgressDialog pd;
    private boolean is_check;
    private Activity m_activity;
    private String part1;
    AnimCheckBox acb;
    private String respPart1,respPart2,respPart3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = NewUser.this;
        APP.setWindowsProperties(m_activity,false);
        pd = new TransparentProgressDialog(m_activity, "", true);

        setContentView(R.layout.z_new_user);

        LinearLayout viewstub_parent_ly = findViewById(R.id.viewstub_parent_ly);
        viewstub_parent_ly.setBackgroundColor(getResources().getColor(R.color.a_brown11));


        ViewStub stub = (ViewStub) findViewById(R.id.lay_stub);
        stub.setLayoutResource(R.layout.b_top_img_txt_emp);
        stub.inflate();

        MyTextView tv_baslik = (MyTextView) findViewById(R.id.tv_baslik);
        tv_baslik.setText(getString(R.string.s_user_information));
        tv_baslik.setBackgroundColor(getResources().getColor(R.color.a_brown11));
        tv_baslik.setTextColor(getResources().getColor(R.color.a_white11));

        ImageView img_left = (ImageView) findViewById(R.id.img_left);
        img_left.setImageResource(R.drawable.b_ic_prew_white);

        LinearLayout top_left = (LinearLayout) findViewById(R.id.top_left);
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


                s_mail = et_mail.getText().toString();
                s_pass = et_pass.getText().toString();
                s_name = et_name.getText().toString();
                s_surname = et_surname.getText().toString();
                s_phone = et_phone.getText().toString();
               // s_skin_type = et_skin_type.getText().toString();

                if (s_name.length() > 0) {
                    if (s_surname.length() > 0) {
                        if (APP.isValidEmail(s_mail)) {
                            if (s_phone.length() > 0) {
                                if (s_pass.length() > 5 && s_pass.length() < 21) {
                                         pd.show();
                                       // new Connection().execute("");
                                        startActivity(new Intent(m_activity, UserAggrement.class).putExtra("contract",part1));

                                } else {
                                    APP.show_status(m_activity, 2,
                                            getResources().getString(R.string.s_please_enter_a_password));
                                }
                            } else {
                                APP.show_status(m_activity, 2,
                                        getResources().getString(R.string.s_please_enter_x, getString(R.string.s_phone_number)));
                            }
                        } else {
                            APP.show_status(m_activity, 2,
                                    getResources().getString(R.string.s_please_enter_a_valid_email_address));
                        }
                    } else {
                        APP.show_status(m_activity, 2,
                                getResources().getString(R.string.s_please_enter_x, getString(R.string.s_lastname)));
                    }
                } else {
                    APP.show_status(m_activity, 2,
                            getResources().getString(R.string.s_please_enter_x, getString(R.string.s_firstname)));
                }
            }
        });

        et_mail = (EditText) findViewById(R.id.et_mail);
        et_pass = (EditText) findViewById(R.id.et_pass);
        et_name = (EditText) findViewById(R.id.et_name);
        et_surname = (EditText) findViewById(R.id.et_surname);
        et_phone = (EditText) findViewById(R.id.et_phone);

      /*  is_check = false;
        acb = (AnimCheckBox) findViewById(R.id.acb);
        acb.setOnCheckedChangeListener(new AnimCheckBox.OnCheckedChangeListener() {
            @Override
            public void onChange(AnimCheckBox view, boolean checked) {
                is_check = checked;
            }
        });

        MyTextView tv_read = (MyTextView) findViewById(R.id.tv_read);
        tv_read.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                pd.show();
                new Connection2().execute("");
            }
        }); */

      new Connection2().execute();

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
                new MyContractDialog(m_activity, part1,getString(R.string.s_contract));
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
            nameValuePairs.add(new Pair<>("param19", APP.base64Encode("skin type")));


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

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "OK");
                setResult(Activity.RESULT_OK, returnIntent);
                finish();

            } else if (result.contentEquals("error")) {
                APP.show_status(m_activity, 2, respPart2);
            } else {
                APP.show_status(m_activity, 1, getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }

}
