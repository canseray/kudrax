package tr.limonist.kudra.app.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
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

import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;
import tr.limonist.classes.BranchesItem;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;

public class Branches extends Activity {

    ArrayList<BranchesItem> results;
    Activity m_activity;
    TransparentProgressDialog pd;
    RefreshLayout refreshLayout;
    String[] part1;
    JazzyListView list;
    lazy adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = Branches.this;
        APP.setWindowsProperties(m_activity, true);
        pd = new TransparentProgressDialog(m_activity, "", true);

        String title = getIntent().getStringExtra("title");

        setContentView(R.layout.z_layout_listview_with_white_top);

        ViewStub stub = (ViewStub) findViewById(R.id.lay_stub);
        stub.setLayoutResource(R.layout.b_top_img_txt_emp);
        stub.setBackgroundColor(getResources().getColor(R.color.a_white11));

        stub.inflate();

        MyTextView tv_baslik = (MyTextView) findViewById(R.id.tv_baslik);
        tv_baslik.setTextColor(getResources().getColor(R.color.a_brown11));
        tv_baslik.setText(title);

        ImageView img_left = (ImageView) findViewById(R.id.img_left);
        img_left.setImageResource(R.drawable.left_k);

        LinearLayout top_left = (LinearLayout) findViewById(R.id.top_left);
        top_left.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }

        });

        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new WaterDropHeader(this));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                new Connection().execute();
            }
        });

        list = (JazzyListView) findViewById(R.id.list);
        list.setTransitionEffect(JazzyHelper.TILT);
        list.setDivider(new ColorDrawable(getResources().getColor(R.color.a_black12)));
        list.setDividerHeight(0);

        results = new ArrayList<>();
        adapter = new lazy();
        list.setAdapter(adapter);

        pd.show();
        new Connection().execute();

    }

    class Connection extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            results = new ArrayList<>();

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/get_branches_data_list.php");

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
                            BranchesItem pi = new BranchesItem(
                                    temp.length > 0 ? temp[0] : "", temp.length > 1 ? temp[1] : "",
                                    temp.length > 2 ? temp[2] : "", temp.length > 3 ? temp[3] : "",
                                    temp.length > 4 ? temp[4] : "", temp.length > 5 ? temp[5] : "",
                                    temp.length > 6 ? temp[6] : "", temp.length > 7 ? temp[7] : ""
                            );
                            results.add(pi);
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
            if (refreshLayout != null)
                refreshLayout.finishRefresh();
            if (result.contentEquals("true")) {
                adapter.notifyDataSetChanged();
            } else {
                APP.show_status(m_activity, 1,getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }

    public class lazy extends BaseAdapter {
        LayoutInflater inflater = null;

        public lazy() {
            inflater = LayoutInflater.from(m_activity);
        }

        @Override
        public int getCount() {
            return results.size();
        }

        @Override
        public BranchesItem getItem(int position) {
            return results.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {

            MyTextView name,address;

        }

        public View getView(final int position, View view, ViewGroup parent) {
            final BranchesItem item = results.get(position);

            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.c_item_branches, null);

                holder.name = (MyTextView) view.findViewById(R.id.name);
                holder.address = (MyTextView) view.findViewById(R.id.address);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.name.setText(item.getName());
            holder.address.setText(item.getCity());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(m_activity, BranchesSingle.class).putExtra("branch_id", item.getId()));
                }
            });

            return view;
        }

    }

}