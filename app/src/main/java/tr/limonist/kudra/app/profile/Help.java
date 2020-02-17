package tr.limonist.kudra.app.profile;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.scwang.smartrefresh.header.WaterDropHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import tr.limonist.classes.HelpChatItem;
import tr.limonist.extras.GradientTextView;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;
import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;
import tr.limonist.views.MyCallDialog;
import tr.limonist.views.MyZoomImageDialog;

public class Help extends Activity {

    Activity m_activity;
    private ListView list;
    private TransparentProgressDialog pd;
    public lazy adapter;
    TextView tv_desc;
    public String[] part1;
    LinearLayout lay_compose;
    private EditText et_message;
    private ImageView img_send;
    String message, part2, part3;
    public String sendPart1;
    public String sendPart2;
    MyTextView tv_call_number;
    ArrayList<HelpChatItem> results;
    LinearLayout top_left;
    MyTextView tv_baslik;
    ImageView img_left;

    public static boolean is_visible=false;

    @Override
    protected void onPause() {
        is_visible=false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        is_visible=true;

        myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.zxing_beep);
                mp.start();
                new Connection4().execute("");
            }
        };

        pd.show();
        new Connection().execute("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = Help.this;
        pd = new TransparentProgressDialog(m_activity, "", true);
        String title = getIntent().getStringExtra("title");

        setContentView(R.layout.z_help);

        ViewStub stub = (ViewStub) findViewById(R.id.lay_stub);
        stub.setLayoutResource(R.layout.b_top_img_txt_emp);
        stub.inflate();

        tv_baslik = (MyTextView) findViewById(R.id.tv_baslik);
        tv_baslik.setText("snyo");
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

        lay_compose = findViewById(R.id.lay_compose);

        LinearLayout lay_call = findViewById(R.id.lay_call);
        lay_call.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new MyCallDialog(m_activity, part2);

            }
        });

        list = findViewById(R.id.list);

        tv_desc = findViewById(R.id.tv_desc);

        tv_call_number = findViewById(R.id.tv_call_number);

        img_send = findViewById(R.id.img_send);
        img_send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                message = et_message.getText().toString().trim();
                if (message.trim().length() > 0) {
                    lay_compose.setEnabled(false);
                    lay_compose.setAlpha(0.5f);
                    new Connection2().execute("");
                }

            }
        });

        et_message = findViewById(R.id.et_message);
        et_message.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().trim().length() == 0) {
                    img_send.setEnabled(false);
                    img_send.setAlpha(0.3f);

                } else {
                    img_send.setEnabled(true);
                    img_send.setAlpha(1.0f);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        img_send.setEnabled(false);
        img_send.setAlpha(0.3f);

        results = new ArrayList<>();
        adapter = new lazy();
        list.setAdapter(adapter);

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
        public HelpChatItem getItem(int position) {
            return results.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View view, ViewGroup parent) {

            final HelpChatItem m = results.get(position);

            LayoutInflater mInflater = (LayoutInflater) m_activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (m.getSendertype().contentEquals("1")) {
                view = mInflater.inflate(R.layout.c_item_help_chat_right, null);
            } else if (m.getSendertype().contentEquals("2")) {
                view = mInflater.inflate(R.layout.c_item_help_chat_left, null);
            } else {
                view = mInflater.inflate(R.layout.c_item_call_center_date, null);
            }

            TextView date = (TextView) view.findViewById(R.id.date);
            final TextView text = (TextView) view.findViewById(R.id.text);
            if (m.getSendertype().contentEquals("0")) {

                String[] mytime = m.getMessage().split(" ");
                String[] same = mytime[1].split(":");
                String time = same[0] + ":" + same[1];
                try {
                    if (formatToYesterdayOrToday(mytime[0]).contentEquals(getResources().getString(R.string.s_today))) {
                        text.setText(getResources().getString(R.string.s_today));
                    } else if (formatToYesterdayOrToday(mytime[0])
                            .contentEquals(getResources().getString(R.string.s_yesterday))) {
                        text.setText(getResources().getString(R.string.s_yesterday) );
                    } else {

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date myDate = null;
                        try {
                            myDate = dateFormat.parse(mytime[0]);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM yyyy");
                        String finalDate = timeFormat.format(myDate);
                        text.setText(finalDate);

                    }
                } catch (Resources.NotFoundException e1) {
                    e1.printStackTrace();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }

            } else {

                String[] mytime = m.getDate().split(" ");
                date.setText(mytime[1].substring(0,mytime[1].length() - 3));
                text.setText(m.getMessage());
            }

            return view;
        }
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

                fillCallCenterInfo();
                adapter.notifyDataSetChanged();

            } else {
                APP.show_status(m_activity, 1, getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }

    private void fillCallCenterInfo() {

        tv_desc.setText(part3);
        tv_call_number.setText(part2);

    }

    private class Connection2 extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.device_id)));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(APP.device_id)));
            //nameValuePairs.add(new Pair<>("param3", APP.base64Encode(APP.base64Encode(APP.base64Encode(message)))));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode(APP.base64Encode(message))));
            nameValuePairs.add(new Pair<>("param4", APP.base64Encode(APP.main_user.id)));
            nameValuePairs.add(new Pair<>("param5", APP.base64Encode("A")));
            nameValuePairs.add(new Pair<>("param6", APP.base64Encode(APP.language_id)));


            String xml = APP.post1(nameValuePairs, APP.path + "/snyo_services/send_chat_message.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {
                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {
                        sendPart1 = APP.base64Decode(APP.getElement(parse, "part1"));
                        sendPart2 = APP.base64Decode(APP.getElement(parse, "part2"));
                    }

                    if (sendPart1 != null && sendPart1.contentEquals("OK"))
                        return "true";
                    else if (sendPart1 != null && sendPart1.contentEquals("FAIL"))
                        return "error";
                    else
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
            lay_compose.setEnabled(true);
            lay_compose.setAlpha(1.0f);

            if (result.contentEquals("true")) {
                et_message.setText(Html.fromHtml(""));
                new Connection4().execute("");

            } else if (result.contentEquals("error")) {
                if (pd != null)
                    pd.dismiss();
                APP.show_status(m_activity,2, sendPart2);

            } else {
                if (pd != null)
                    pd.dismiss();
                APP.show_status(m_activity, 1, getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }

    private class Connection4 extends AsyncTask<String, Void, String> {

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
                adapter.notifyDataSetChanged();
            } else {
                APP.show_status(m_activity, 1, getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }

    public String formatToYesterdayOrToday(String date) throws ParseException {
        Date dateTime = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);

        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            return getResources().getString(R.string.s_today);
        } else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR)
                && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
            return getResources().getString(R.string.s_yesterday);
        } else {
            return date;
        }
    }

    public static void update(){
        myHandler.sendEmptyMessage(1);
    }

    static Handler myHandler;

}
