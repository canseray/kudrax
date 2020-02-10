package tr.limonist.kudra.app.shop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.Pair;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.scwang.smartrefresh.header.WaterDropHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.twotoasters.jazzylistview.JazzyGridView;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import tr.limonist.kudra.APP;
import tr.limonist.kudra.MyVideoDialog;
import tr.limonist.kudra.R;
import tr.limonist.classes.ProductItem;
import tr.limonist.classes.MainItem;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;

public class Products extends FragmentActivity {

    ArrayList<MainItem> results_categories,results_categories_temp;
    ArrayList<ProductItem> results;
    RefreshLayout refreshLayout;
    JazzyGridView list;
    Activity m_activity;
    private TransparentProgressDialog pd;
    RecyclerView hl_main,hl_sub;
    private int selected_page = 0, selected_main_page = 0;
    private String[] part1,part2;
    private String selected_sub_category_id="0";
    private String selected_product_id="0";
    private String favPart1,favPart2;
    private lazy adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = Products.this;
        APP.setWindowsProperties(m_activity, true);
        selected_main_page = getIntent().getIntExtra("selected_main_page",0);

        pd = new TransparentProgressDialog(m_activity, "", true);
        setContentView(R.layout.z_layout_gridview_products);

        ViewStub stub = findViewById(R.id.lay_stub);
        stub.setLayoutResource(R.layout.b_top_img_txt_img);
        stub.inflate();

        MyTextView tv_baslik = (MyTextView) findViewById(R.id.tv_baslik);
        tv_baslik.setTextColor(getResources().getColor(R.color.a_brown11));
        tv_baslik.setText(getString(R.string.s_products));

        ImageView img_left = (ImageView) findViewById(R.id.img_left);
        img_left.setImageResource(R.drawable.left_k);

        ImageView img_right = findViewById(R.id.img_right);
        img_right.setImageResource(R.drawable.box_k);

        LinearLayout top_left = (LinearLayout) findViewById(R.id.top_left);
        top_left.setOnClickListener(new OnClickListener() {

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
                new ConnectionSub().execute();
            }
        });

        list = (JazzyGridView) findViewById(R.id.list);
        list.setNumColumns(2);

        hl_main = (RecyclerView) findViewById(R.id.hl_main);
        hl_sub = (RecyclerView) findViewById(R.id.hl_sub);

        selected_page = 0;
        selected_sub_category_id = "0";

        results = new ArrayList<>();
        results_categories = new ArrayList<>();

        hl_main.addItemDecoration(new DividerItemDecoration(m_activity, LinearLayoutManager.HORIZONTAL) {
            @Override
            public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {

            }
        });
        adapter_main = new rvMainCategoryAdapter();
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(m_activity, LinearLayoutManager.HORIZONTAL, false);
        hl_main.setLayoutManager(horizontalLayoutManager);
        hl_main.setAdapter(adapter_main);

        hl_sub.addItemDecoration(new DividerItemDecoration(m_activity, LinearLayoutManager.HORIZONTAL) {
            @Override
            public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {

            }
        });
        adapter_sub = new rvSubCategoryAdapter();
        LinearLayoutManager horizontalLayoutManager2 = new LinearLayoutManager(m_activity, LinearLayoutManager.HORIZONTAL, false);
        hl_sub.setLayoutManager(horizontalLayoutManager2);
        hl_sub.setAdapter(adapter_sub);
        adapter_sub.notifyDataSetChanged();

        adapter_main.notifyDataSetChanged();
        hl_main.postDelayed(new Runnable() {
            @Override
            public void run() {
                hl_main.smoothScrollToPosition(selected_main_page);
            }
        }, 250);

        results = new ArrayList<>();
        adapter = new lazy();
        list.setAdapter(adapter);

        pd.show();
        new ConnectionSub().execute();

    }

    rvMainCategoryAdapter adapter_main;
    rvSubCategoryAdapter adapter_sub;

    class rvMainCategoryAdapter extends RecyclerView.Adapter<rvMainCategoryAdapter.GroceryViewHolder> {

        public rvMainCategoryAdapter() {
        }

        @Override
        public rvMainCategoryAdapter.GroceryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.c_item_hl_main, parent, false);
            rvMainCategoryAdapter.GroceryViewHolder gvh = new rvMainCategoryAdapter.GroceryViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public void onBindViewHolder(rvMainCategoryAdapter.GroceryViewHolder holder, final int position) {

            holder.ll_root.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    select_main_category(position);
                }
            });
            holder.title.setText(Categories.results.get(position).getTitle());
            if (selected_main_page == position) {
                holder.ll_root.setSelected(true);
                holder.title.setTypeface(null,Typeface.BOLD_ITALIC);
            } else {
                holder.ll_root.setSelected(false);
                holder.title.setTypeface(null,Typeface.NORMAL);
            }
        }

        @Override
        public int getItemCount() {
            return Categories.results.size();
        }

        public class GroceryViewHolder extends RecyclerView.ViewHolder {
            LinearLayout ll_root;
            TextView title;

            public GroceryViewHolder(View view) {
                super(view);
                ll_root = view.findViewById(R.id.ll_root);
                title = view.findViewById(R.id.title);
            }
        }
    }

    private void select_main_category(int position) {
        selected_main_page = position;
        selected_page = 0;
        selected_sub_category_id ="0";
        adapter_main.notifyDataSetChanged();
        adapter_sub.notifyDataSetChanged();
        pd.show();
        new ConnectionSub().execute();
    }

    private void select_sub_category(int position) {
        selected_page = position;
        selected_sub_category_id = results_categories.get(position).getId();
        adapter_sub.notifyDataSetChanged();
        pd.show();
        new ConnectionSub().execute();

    }

    class ConnectionSub extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            results = new ArrayList<>();
            results_categories_temp = new ArrayList<>();

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(Categories.results.get(selected_main_page).getId())));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode(selected_sub_category_id)));
            nameValuePairs.add(new Pair<>("param4", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param5", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/get_category_and_products_data_list.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        part1 = APP.base64Decode(APP.getElement(parse, "part1")).split("\\[##\\]");
                        part2 = APP.base64Decode(APP.getElement(parse, "part2")).split("\\[##\\]");
                    }

                    if (!part1[0].contentEquals("")) {

                        for (int i = 0; i < part1.length; i++) {
                            String[] temp = part1[i].split("\\[#\\]");
                            MainItem ai = new MainItem(temp.length > 0 ? temp[0] : "",
                                    temp.length > 1 ? temp[1] : "", temp.length > 2 ? temp[2] : "",
                                    temp.length > 3 ? temp[3] : "", temp.length > 4 ? temp[4] : "",
                                    temp.length > 5 ? temp[5] : "", temp.length > 6 ? temp[6] : "",
                                    temp.length > 7 ? temp[7] : "");
                            results_categories_temp.add(ai);
                        }

                    }

                    if (!part2[0].contentEquals("")) {

                        for (int i = 0; i < part2.length; i++) {
                            String[] temp = part2[i].split("\\[#\\]");
                            ProductItem ai = new ProductItem(temp.length > 0 ? temp[0] : "",
                                    temp.length > 1 ? temp[1] : "", temp.length > 2 ? temp[2] : "",
                                    temp.length > 3 ? temp[3] : "", temp.length > 4 ? temp[4] : "",
                                    temp.length > 5 ? temp[5] : "");
                            results.add(ai);
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

                fill_products();

            } else {
                APP.show_status(m_activity, 1, getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }

    private void fill_products() {
        adapter.notifyDataSetChanged();
        results_categories = results_categories_temp;
        adapter_sub.notifyDataSetChanged();
        hl_sub.postDelayed(new Runnable() {
            @Override
            public void run() {
                hl_sub.smoothScrollToPosition(selected_page);
            }
        }, 250);
    }

    class rvSubCategoryAdapter extends RecyclerView.Adapter<rvSubCategoryAdapter.GroceryViewHolder> {

        public rvSubCategoryAdapter() {
        }

        @Override
        public rvSubCategoryAdapter.GroceryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.c_item_hl_sub, parent, false);
            rvSubCategoryAdapter.GroceryViewHolder gvh = new rvSubCategoryAdapter.GroceryViewHolder(groceryProductView);
            return gvh;
        }

        @Override
        public void onBindViewHolder(rvSubCategoryAdapter.GroceryViewHolder holder, final int position) {
            holder.ll_root.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    select_sub_category(position);
                }
            });
            holder.title.setText(results_categories.get(position).getTitle());
            if (selected_page == position) {
                holder.ll_root.setSelected(true);
                holder.title.setTypeface(null,Typeface.BOLD_ITALIC);
            } else {
                holder.ll_root.setSelected(false);
                holder.title.setTypeface(null,Typeface.NORMAL);
            }
        }

        @Override
        public int getItemCount() {
            return results_categories.size();
        }

        public class GroceryViewHolder extends RecyclerView.ViewHolder {
            LinearLayout ll_root;
            TextView title;

            public GroceryViewHolder(View view) {
                super(view);
                ll_root = view.findViewById(R.id.ll_root);
                title = view.findViewById(R.id.title);
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
        public ProductItem getItem(int position) {
            return results.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {

            SimpleDraweeView img;
            ImageView img_play, img_favorite;
            MyTextView title, price;

        }

        public View getView(final int position, View view, ViewGroup parent) {
            final ProductItem item = results.get(position);

            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.c_item_products, null);

                holder.img_favorite = view.findViewById(R.id.img_favorite);
                holder.img = view.findViewById(R.id.img);
                holder.img_play = view.findViewById(R.id.img_play);
                holder.title = view.findViewById(R.id.title);
                holder.price = view.findViewById(R.id.price);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.img.setImageURI(item.getImage());
            holder.title.setText(item.getTitle());
            holder.price.setText(item.getPrice());

            if(!item.getVideo().contentEquals(""))
                holder.img_play.setImageResource(R.drawable.ic_video_on1);
            else
                holder.img_play.setImageResource(R.drawable.ic_video_off1);

            holder.img_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!item.getVideo().contentEquals(""))
                    {
                        startActivity(new Intent(m_activity, MyVideoDialog.class).putExtra("url",item.getVideo()));
                    }
                }
            });

            if(!item.getFavorite().contentEquals("0"))
                holder.img_favorite.setImageResource(R.drawable.b_ic_fav_on);
            else
                holder.img_favorite.setImageResource(R.drawable.b_ic_fav_off);

            holder.img_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected_product_id = item.getId();
                    pd.show();
                    new Connection2().execute();
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(m_activity,ProductDetail.class)
                            .putExtra("product_id",item.getId())
                            .putExtra("product_title",item.getTitle())
                    );
                }
            });

            return view;
        }
    }

    class Connection2 extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(selected_product_id)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param4", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/cart_controls/send_add_to_favorite_request.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {
                        favPart1 = APP.base64Decode(APP.getElement(parse, "part1"));
                        favPart2 = APP.base64Decode(APP.getElement(parse, "part2"));
                    }

                    if (favPart1.contentEquals("OK")) {
                        return "true";
                    } else if (favPart1.contentEquals("FAIL")) {
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

            if (result.contentEquals("true")) {
                new ConnectionSub().execute();
            } else if (result.contentEquals("error")) {
                if (pd != null)
                    pd.dismiss();
                APP.show_status(m_activity, 2, favPart2);
            } else {
                if (pd != null)
                    pd.dismiss();
                APP.show_status(m_activity, 1, getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }
}
