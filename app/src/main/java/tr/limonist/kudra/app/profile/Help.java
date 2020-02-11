package tr.limonist.kudra.app.profile;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.scwang.smartrefresh.header.WaterDropHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import tr.limonist.classes.HelpChatItem;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;
import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;

public class Help extends AppCompatActivity {
    Activity m_activity;
    TransparentProgressDialog pd;
    LinearLayout top_left;
    String[] part1;
    String part2,part3;
    MyTextView tv_baslik;
    ImageView img_left;
    RefreshLayout refreshLayout;
    JazzyListView list;
    String title;
    ArrayList<HelpChatItem> results;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = Help.this;
        APP.setWindowsProperties(m_activity, true);
        title = getIntent().getStringExtra("title");
        pd = new TransparentProgressDialog(m_activity, "", true);
        setContentView(R.layout.z_help);

        ViewStub stub = (ViewStub) findViewById(R.id.lay_stub);
        stub.setLayoutResource(R.layout.b_top_img_txt_emp);
        stub.inflate();

        tv_baslik = (MyTextView) findViewById(R.id.tv_baslik);
        tv_baslik.setText(title);
        tv_baslik.setTextColor(getResources().getColor(R.color.a_brown11));

        img_left = (ImageView) findViewById(R.id.img_left);
        img_left.setImageResource(R.drawable.left_k);

        refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new WaterDropHeader(this));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
              //  new Connection().execute();
            }
        });

        top_left = (LinearLayout) findViewById(R.id.top_left);
        top_left.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }

        });

        list = (JazzyListView) findViewById(R.id.list);
        list.setTransitionEffect(JazzyHelper.TILT);
        list.setEmptyView(findViewById(android.R.id.empty));
        list.setDivider(new ColorDrawable(Color.BLACK));
        list.setDividerHeight(0);

        new Connection().execute();
    }


    private class Connection extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            results = new ArrayList<>();

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.device_id)));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(APP.device_id)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode(APP.main_user.id)));
            nameValuePairs.add(new Pair<>("param4", APP.base64Encode("A")));
            nameValuePairs.add(new Pair<>("param5", APP.base64Encode(APP.language_id)));

            String xml = APP.post1(nameValuePairs, APP.path + "/snyo_services/get_chat_messages.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {
                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {
                        part1 = APP.base64Decode(APP.getElement(parse, "part1")).split("\\[##\\]");
                        part2 = APP.base64Decode(APP.getElement(parse, "part2"));
                        part3 = APP.base64Decode(APP.getElement(parse, "part3"));

                    }

                    if (!part1[0].contentEquals("")) {

                        for (int i = 0; i < part1.length; i++) {
                            String[] temp = part1[i].split("\\[#\\]");
                            if (i == 0) {
                                HelpChatItem date = new HelpChatItem("0", "", temp.length > 1 ? temp[1] : "", "");
                                results.add(date);

                                HelpChatItem ai = new HelpChatItem(temp.length > 0 ? temp[0] : "",
                                        temp.length > 1 ? temp[1] : "",
                                        APP.base64Decode(APP.base64Decode(temp.length > 2 ? temp[2] : "")),
                                        temp.length > 3 ? temp[3] : "");
                                results.add(ai);
                            } else {
                                String[] check = part1[i - 1].split("\\[#\\]");
                                String[] prew_date = check[1].split(" ");
                                String[] new_date = temp[1].split(" ");
                                if (!prew_date[0].contentEquals(new_date[0])) {
                                    HelpChatItem date = new HelpChatItem("0", "", temp.length > 1 ? temp[1] : "", "");
                                    results.add(date);
                                }

                                HelpChatItem ai = new HelpChatItem(temp.length > 0 ? temp[0] : "",
                                        temp.length > 1 ? temp[1] : "",
                                        APP.base64Decode(APP.base64Decode(temp.length > 2 ? temp[2] : "")),
                                        temp.length > 3 ? temp[3] : "");
                                results.add(ai);
                            }

                        }
                    }

                    return "true";

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



            } else {
                APP.show_status(m_activity, 1, getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }

}
