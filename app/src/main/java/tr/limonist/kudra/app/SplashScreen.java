package tr.limonist.kudra.app;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import tr.limonist.extras.TransparentProgressDialog;
import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;
import tr.limonist.views.MyAlertDialog;
import tr.limonist.views.MyInfoDialog;

public class SplashScreen extends AppCompatActivity {

    public TransparentProgressDialog pd;
    private Activity m_activity;
    boolean is_checked = false;
    boolean can_go = false;
    boolean have_message = false;
    private PackageInfo pInfo;
    String part1;
    private String part2;
    String call_type = "0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = SplashScreen.this;
        pd = new TransparentProgressDialog(m_activity, "", true);
        APP.setWindowsProperties(m_activity,true);
        call_type = "0";
        if (getIntent().hasExtra("call_type"))
            call_type = getIntent().getStringExtra("call_type");

        setContentView(R.layout.z_splash);

        pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        APP.version = pInfo.versionName;

        new Connection().execute("IMAGE_IDS");

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                check();
            }
        }, 1000);
    }

    public void check() {
        APP.device_id = APP.myPrefs.getString("regId", "");
        if(APP.device_id.contentEquals("")) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull final Task<InstanceIdResult> task) {
                    if (!task.isSuccessful()) {

                    } else {
                        APP.e.putString("regId", task.getResult().getToken());
                        APP.e.commit();
                    }

                }
            });
        }

        if (is_checked) {
            if (can_go) {
                startActivity(new Intent(m_activity, StartMain.class).putExtra("call_type",call_type));
                finish();
            } else {
                if(have_message)
                {
                    final String[] infos = part2.split("\\[-\\]");

                    final MyInfoDialog alert = new MyInfoDialog(m_activity, part2);

                    alert.setButtonClick(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            if (infos.length > 3) {
                                if (infos[3].contentEquals("TERMINATE")) {
                                    m_activity.finish();
                                } else if (infos[3].contentEquals("UPDATE")) {
                                    Uri uri = Uri.parse(
                                            infos.length > 2 ? infos[2] : ("market://details?id=" + APP.package_name));
                                    Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);

                                    try {
                                        startActivity(myAppLinkToMarket);
                                        m_activity.finish();

                                    } catch (ActivityNotFoundException e) {

                                        Toast.makeText(m_activity, "Google Play Uygulaması yüklü değil...",
                                                Toast.LENGTH_LONG).show();
                                    }
                                } else
                                    alert.dismiss();
                            }

                        }
                    });

                    alert.show();
                }
                else
                {
                    final MyAlertDialog alert = new MyAlertDialog(m_activity, R.drawable.alert_error,
                            getString(R.string.s_connection_error),
                            getString(R.string.s_unexpected_connection_error_has_occured), getString(R.string.s_refresh),
                            getString(R.string.s_cancel), false);

                    alert.setNegativeClicl(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            alert.dismiss();
                            finish();
                        }
                    });
                    alert.setPositiveClicl(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            alert.dismiss();

                            pd.show();
                            new Connection().execute("IMAGE_IDS");
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    check();
                                }
                            }, 2000);
                        }
                    });
                    alert.show();
                }
            }
        } else {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    check();
                }
            }, 2000);
        }
    }

    private class Connection extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.version)));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(APP.language_id)));

            String xml = APP.post1(nameValuePairs, APP.path + "/start.php");
            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {
                        part1 = APP.base64Decode(APP.getElement(parse, "part1"));
                        part2 = APP.base64Decode(APP.getElement(parse, "part2"));
                    }
                    if (part1.contentEquals("GO")) {
                        return "true";
                    } else if (part1.contentEquals("REPAIR") || part1.contentEquals("VERSION")) {
                        return "message";
                    } else {
                        return "hata";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return "false";
                }
            } else
                return "bos";
        }

        @Override
        protected void onPostExecute(String result) {
            if (pd != null)
                pd.dismiss();
            is_checked = true;
            if (result.contentEquals("true")) {
                can_go = true;
            } else if (result.contentEquals("message")) {
                can_go = false;
                have_message = true;
            } else {
                can_go = false;
                have_message = false;
            }

        }

    }
}

