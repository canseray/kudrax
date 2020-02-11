package tr.limonist.kudra.app.profile;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akash.RevealSwitch;
import com.akash.revealswitch.OnToggleListener;
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

import tr.limonist.classes.NotificationsSettingsItem;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;
import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;

public class MyNotifications extends AppCompatActivity {
    Activity m_activity;
    TransparentProgressDialog pd;
    LinearLayout top_left;
    MyTextView tv_baslik;
    ImageView img_left;
    RefreshLayout refreshLayout;
    JazzyListView list;
    String title;
    int selected_toggle_pos;
    String selected_toggle_type, selection;
    ArrayList<NotificationsSettingsItem> results;
    String[] Part1;
    String sendPart1, sendPart2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = MyNotifications.this;
        APP.setWindowsProperties(m_activity, true);
        title = getIntent().getStringExtra("title");
        pd = new TransparentProgressDialog(m_activity, "", true);
        setContentView(R.layout.z_my_notifications);

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
               // new Connection().execute();
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

        pd.show();
        new Connection().execute();
    }

    private class Connection extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            results = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/notifications/get_notification_setting_options.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        Part1 = APP.base64Decode(APP.getElement(parse,"part1")).split("\\[##\\]");

                    }

                    if (!Part1[0].contentEquals("")) {


                        if (!Part1[0].contentEquals("")) {

                            for (int i = 0; i < Part1.length; i++) {
                                String[] temp = Part1[i].split("\\[#\\]");
                                NotificationsSettingsItem ai = new NotificationsSettingsItem(
                                        temp.length > 0 ? temp[0] : "",
                                        temp.length > 1 ? temp[1] : "",
                                        temp.length > 2 ? temp[2] : "");
                                results.add(ai);

                            }
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
                if(results.size()>0) {

                    list.setAdapter(null);
                    lazy adapter = new lazy();
                    list.setAdapter(adapter);
                }
                else {
                    //list.setVisibility(View.GONE);
                   // emptyView.setVisibility(View.VISIBLE);
                }
            } else {
                APP.show_status(m_activity, 1,
                        getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }

        }

    }

    public class lazy extends BaseAdapter {
        private LayoutInflater inflater = null;

        public lazy() {
            inflater = LayoutInflater.from(m_activity);
        }
        @Override
        public int getCount() {
            return results.size();
        }
        @Override
        public Object getItem(int position) {
            return results.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        public class ViewHolder {
            TextView title;
            RevealSwitch toggle;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {

            final NotificationsSettingsItem item = results.get(position);

            final ViewHolder holder;

            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.c_item_notifications_setting, null);
                holder.title = view.findViewById(R.id.notif_text);
                holder.toggle = view.findViewById(R.id.notif_switch);

                view.setTag(holder);

            } else {
                holder = (ViewHolder) view.getTag();

            }

            holder.title.setText(item.getTitle());

            holder.toggle.setToggleListener(new OnToggleListener() {
                @Override
                public void onToggle(boolean b) {

                }
            });

            if (item.getValue().contentEquals("1"))
                holder.toggle.setEnable(true);
            else
                holder.toggle.setEnable(false);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    holder.toggle.setToggleListener(new OnToggleListener() {
                        @Override
                        public void onToggle(boolean b) {

                            selected_toggle_pos = position;
                            selected_toggle_type = item.getId();
                            selection = b?"1":"0";

                            pd.show();
                            new Connection2().execute();
                        }
                    });
                }
            }, 500);

            return view;
        }
    }

    class Connection2 extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(selected_toggle_type)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode(selection)));
            nameValuePairs.add(new Pair<>("param4", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param5", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/notifications/send_notification_setting_option_request.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
                    List<String> dataList = new ArrayList<String>();

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        sendPart1 = APP.base64Decode(APP.getElement(parse,"part1"));
                        sendPart2 = APP.base64Decode(APP.getElement(parse,"part2"));

                    }

                    if (sendPart1.contentEquals("OK")) {
                        return "true";
                    } else if (sendPart1.contentEquals("FAIL")) {
                        return "error";
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

        protected void onPostExecute(String result) {
            if (pd != null)
                pd.dismiss();
            if (result.contentEquals("true")) {

                NotificationsSettingsItem item = results.get(selected_toggle_pos);
                item.setValue(selection);
                results.set(selected_toggle_pos, item);

            } else if (result.contentEquals("error")) {

                NotificationsSettingsItem item = results.get(selected_toggle_pos);
                item.setValue(selection.contentEquals("1") ? "0" : "1");
                results.set(selected_toggle_pos, item);

                APP.show_status(m_activity, 2, sendPart2);

            } else {

                NotificationsSettingsItem item = results.get(selected_toggle_pos);
                item.setValue(selection.contentEquals("1") ? "0" : "1");
                results.set(selected_toggle_pos, item);

                APP.show_status(m_activity, 1, getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
            list.setAdapter(null);
            lazy adapter = new lazy();
            list.setAdapter(adapter);
        }
    }


}
